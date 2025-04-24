package com.finbox.subscrititionservice.service.impl;

import com.finbox.subscrititionservice.exception.ResourceNotFoundException;
import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.repositories.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock  ClientRepository clientRepository;
    @InjectMocks ClientServiceImpl service;

    /* ---------- helpers ---------- */

    private static ClientRequest request(String name,
                                         String email,
                                         String phone) {
        ClientRequest req = ClientRequest.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .build();
        return req;
    }

    private static Client entityFrom(ClientRequest req) {
        Client c = new Client();
        c.setClientId(req.getClientId());
        c.setName(req.getName());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        return c;
    }

    /* ---------- tests ---------- */

    @Nested class CreateClient {

        @Test @DisplayName("1. happy path – returns populated response")
        void createsClientSuccessfully() throws Exception {
            // Arrange
            ClientRequest req = request("Alice", "a@b.com", "123");
            // repository should return a persisted copy (same fields)
            when(clientRepository.save(any(Client.class)))
                    .thenAnswer(inv -> {
                        Client toSave = inv.getArgument(0);
                        return toSave;  // mimic “saved” entity
                    });

            // Act
            ClientResponse resp = service.createClient(req);

            // Assert
            assertNotNull(resp.getClientId(), "ID must be set");
            assertEquals("Alice", resp.getName());
            assertEquals("a@b.com", resp.getEmail());
            assertEquals("123", resp.getPhone());
            // verify repo call once with converted entity
            verify(clientRepository).save(any(Client.class));
        }

        @Test @DisplayName("2. null request → BAD_REQUEST SubscriptionServiceException")
        void nullRequestThrows() {
            SubscriptionServiceException ex = assertThrows(
                    SubscriptionServiceException.class,
                    () -> service.createClient(null));
            assertEquals(400, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("cannot be null"));
            verifyNoInteractions(clientRepository);
        }


    }

    // ---------- 1. toggleFeatureForClient ----------
    @Nested class ToggleFeature {

        @Test @DisplayName("1.1 enable parent cascades to children")
        void enableParentCascades() {
            String cid = "C1";
            long pid = 10L;
            Client parentClient = client(cid);
            Feature parent = feature(pid, null, false);
            Feature childA = feature(11L, pid, false);
            Feature childB = feature(12L, pid, true);

            when(clientRepository.findByClientId(cid))
                    .thenReturn(Optional.of(parentClient));
            when(featureRepository.findById(pid))
                    .thenReturn(Optional.of(parent));
            when(featureRepository.findByParentFeatureId(pid))
                    .thenReturn(Optional.of(List.of(childA, childB)));
            when(clientFeatureRepository.save(any(ClientFeature.class)))
                    .thenAnswer(i -> i.getArgument(0));

            String result = service.toggleFeatureForClient(cid, pid, true);
            assertEquals("Feature toggled successfully for client.", result);

            // three saves: childA childB parentMapping
            ArgumentCaptor<ClientFeature> cap = ArgumentCaptor.forClass(ClientFeature.class);
            verify(clientFeatureRepository, times(3)).save(cap.capture());
            cap.getAllValues().forEach(cf -> assertTrue(cf.isEnabled()));
        }

        @Test @DisplayName("1.4 cannot enable child when parent disabled")
        void cannotEnableChildWhenParentOff() {
            String cid = "C2";
            long childId = 21L;
            long parentId = 20L;
            Client c = client(cid);
            Feature child = feature(childId, parentId, false);
            Feature parent = feature(parentId, null, false); // disabled
            ClientFeature parentCf = cf(cid, parentId, false);

            when(clientRepository.findByClientId(cid)).thenReturn(Optional.of(c));
            when(featureRepository.findById(childId)).thenReturn(Optional.of(child));
            when(clientFeatureRepository.findByClientIdAndFeatureId(cid, parentId))
                    .thenReturn(parentCf);

            String msg = service.toggleFeatureForClient(cid, childId, true);
            assertTrue(msg.startsWith("Cannot enable child"));
            verify(clientFeatureRepository, never()).save(any());
        }

        @Test @DisplayName("1.5 client not found -> ResourceNotFoundException")
        void clientNotFound() {
            when(clientRepository.findByClientId("X")).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> service.toggleFeatureForClient("X", 1L, true));
        }

        @Test @DisplayName("1.6 feature not found -> ResourceNotFoundException")
        void featureNotFound() {
            when(clientRepository.findByClientId("C3")).thenReturn(Optional.of(client("C3")));
            when(featureRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> service.toggleFeatureForClient("C3", 99L, false));
        }
    }

    // ---------- 2. getAllEnabledFeatures ----------
    @Nested class GetAllEnabled {

        @Test @DisplayName("2.1 returns only enabled")
        void returnsEnabledList() {
            String cid = "C4";
            Client c = client(cid);
            ClientFeature cf1 = cf(cid, 100L, true);
            ClientFeature cf2 = cf(cid, 101L, false);
            Feature f1 = feature(100L, null, true);
            Feature f2 = feature(101L, null, false);

            when(clientRepository.findByClientId(cid))
                    .thenReturn(Optional.of(c));
            when(clientFeatureRepository.findByClientId(cid))
                    .thenReturn(List.of(cf1, cf2));
            when(featureRepository.findAllById(List.of(100L, 101L)))
                    .thenReturn(List.of(f1, f2));

            ClientFeatures resp = service.getAllEnabledFeatures(cid);
            assertEquals(cid, resp.getClientId());
            assertEquals(1, resp.getFeatures().size());
            assertEquals(f1.getId(), resp.getFeatures().getFirst().getId());
        }

        @Test @DisplayName("2.2 client missing -> exception")
        void clientMissing() {
            when(clientRepository.findByClientId("NONE"))
                    .thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> service.getAllEnabledFeatures("NONE"));
        }

        @Test @DisplayName("2.3 no enabled features -> exception")
        void noEnabledFeatures() {
            String cid = "C5";
            Client c = client(cid);
            ClientFeature cf = cf(cid, 200L, true);
            Feature disabled = feature(200L, null, false);

            when(clientRepository.findByClientId(cid)).thenReturn(Optional.of(c));
            when(clientFeatureRepository.findByClientId(cid)).thenReturn(List.of(cf));
            when(featureRepository.findAllById(List.of(200L))).thenReturn(List.of(disabled));

            assertThrows(ResourceNotFoundException.class,
                    () -> service.getAllEnabledFeatures(cid));
        }
    }

    // ---------- 3. getFeatureFlagStatus ----------
    @Nested class FlagStatus {

        @Test @DisplayName("3.1 returns true when enabled")
        void enabled() {
            when(clientFeatureRepository.findByClientIdAndFeatureId("C6", 1L))
                    .thenReturn(cf("C6", 1L, true));
            assertTrue(service.getFeatureFlagStatus("C6", 1L));
        }

        @Test @DisplayName("3.2 returns false when disabled")
        void disabled() {
            when(clientFeatureRepository.findByClientIdAndFeatureId("C7", 2L))
                    .thenReturn(cf("C7", 2L, false));
            assertFalse(service.getFeatureFlagStatus("C7", 2L));
        }

        @Test @DisplayName("3.3 mapping missing -> exception")
        void mappingMissing() {
            when(clientFeatureRepository.findByClientIdAndFeatureId("C8", 3L))
                    .thenReturn(null);
            assertThrows(ResourceNotFoundException.class,
                    () -> service.getFeatureFlagStatus("C8", 3L));
        }
    }
}
