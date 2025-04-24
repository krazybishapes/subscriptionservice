package com.finbox.subscrititionservice.service.impl;

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
}
