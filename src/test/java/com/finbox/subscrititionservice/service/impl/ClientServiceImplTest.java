package com.finbox.subscrititionservice.service.impl;


import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.models.response.CommonResponse;
import com.finbox.subscrititionservice.repositories.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Description;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;


class ClientServiceImplTest {

    private final ClientRepository clientRepository = mock(ClientRepository.class);
    private final ClientServiceImpl clientService = new ClientServiceImpl(clientRepository);

    @Test
    @DisplayName("Test case for creating a client with null request")
    void testCreateClient_NullRequest() throws SubscriptionServiceException {
        // Act & Assert
        SubscriptionServiceException ex =
                assertThrows(SubscriptionServiceException.class,
                        () -> clientService.createClient(null),
                        "Expected createClient(null) to throw");

        // Optional: verify details on the exception
        assertEquals("Request may not be null", ex.getMessage(),"Client request cannot be null");

    }

    @Test
    @DisplayName("Test case for creating a client successfully")
    void testCreateClient_Success() throws SubscriptionServiceException {
        // Arrange
        ClientRequest clientRequest = mock(ClientRequest.class); // Mock the ClientRequest object

        String clientId = UUID.randomUUID().toString();
        Client client = Client
                .builder()
                .clientId(clientId)
                .email("test@gmail.com")
                .name("Test Client")
                .phone("1234567890")
                .build();

        // Stub the methods on the mocked ClientRequest
        when(clientRequest.getClientId()).thenReturn(clientId);
        when(clientRequest.getName()).thenReturn("Test Client");
        when(clientRequest.getEmail()).thenReturn("test@gmail.com");
        when(clientRequest.getPhone()).thenReturn("1234567890");
        when(clientRequest.toClient()).thenReturn(client);

        // Mock the repository save method
        when(clientRepository.save(client)).thenReturn(client);

        // Act
        ClientResponse response = clientService.createClient(clientRequest);

        // Assert
        assertNotNull("Response should not be null", response);
        assertEquals("Client ID should not be null", clientId, response.getClientId());
        assertEquals("Client name should match", "Test Client", response.getName());
        assertEquals("Client email should match", "test@gmail.com", response.getEmail());
        assertEquals("Client phone should match", "1234567890", response.getPhone());
    }


    @Test
    @DisplayName("Test case for duplicate email id failure")
    void testCreateClient_failure() {
        // Arrange
        ClientRequest clientRequest = mock(ClientRequest.class); // Mock the ClientRequest object

        String clientId = UUID.randomUUID().toString();
        String duplicateEmail = "duplicate@gmail.com";

        Client existingClient = Client
                .builder()
                .clientId(clientId)
                .email(duplicateEmail)
                .name("Existing Client")
                .phone("1234567890")
                .build();

        Client newClient = Client
                .builder()
                .clientId(UUID.randomUUID().toString())
                .email(duplicateEmail)
                .name("New Client")
                .phone("0987654321")
                .build();

        // Stub the methods on the mocked ClientRequest
        when(clientRequest.getClientId()).thenReturn(newClient.getClientId());
        when(clientRequest.getName()).thenReturn(newClient.getName());
        when(clientRequest.getEmail()).thenReturn(duplicateEmail);
        when(clientRequest.getPhone()).thenReturn(newClient.getPhone());
        when(clientRequest.toClient()).thenReturn(newClient);

        // Mock the repository behavior
        when(clientRepository.save(newClient)).thenThrow(new RuntimeException("Duplicate email ID"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> clientService.createClient(clientRequest));
        assertEquals("Duplicate email ID", exception.getMessage(),"Duplicate email ID");
    }

    @Test
    @DisplayName("createClient should throw when repository returns null")
    void testCreateClient_NullClientFromRepository() {

        // Arrange
        ClientRequest clientRequest = mock(ClientRequest.class);

        String clientId = UUID.randomUUID().toString();
        when(clientRequest.getClientId()).thenReturn(clientId);
        when(clientRequest.getName()).thenReturn("Test Client");
        when(clientRequest.getEmail()).thenReturn("test@gmail.com");
        when(clientRequest.getPhone()).thenReturn("1234567890");
        when(clientRequest.toClient()).thenReturn(new Client());

        // repository.save(..) returns null  ➜ should trigger error handling
        when(clientRepository.save(any(Client.class))).thenReturn(null);

        // Act & Assert
        SubscriptionServiceException ex = assertThrows(
                SubscriptionServiceException.class,
                () -> clientService.createClient(clientRequest),
                "Expected createClient() to throw when repo.save() returns null");

        // Optional: verify diagnostic message
        assertTrue(ex.getMessage().contains("Failed to create client"),
                "Exception message should indicate why persisting failed");

        // Optional: verify interactions
        verify(clientRepository).save(any(Client.class));
        verifyNoMoreInteractions(clientRepository);
    }

}
