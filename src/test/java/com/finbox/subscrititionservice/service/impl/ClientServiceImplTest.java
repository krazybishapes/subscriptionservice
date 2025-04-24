package com.finbox.subscrititionservice.service.impl;


import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.models.response.CommonResponse;
import com.finbox.subscrititionservice.repositories.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;


class ClientServiceImplTest {

    private final ClientRepository clientRepository = mock(ClientRepository.class);
    private final ClientServiceImpl clientService = new ClientServiceImpl(clientRepository);

    @Test
    @Description("Test case for creating a client with null request")
    void testCreateClient_NullRequest() {
        // Act
        ClientResponse response = clientService.createClient(null);

        // Assert
        assertNotNull("Response should not be null", response);
        assertEquals("Client ID should not be null", null, response.getClientId());

    }

    @Test
    @Description("Test case for creating a client successfully")
    void testCreateClient_Success() {
        // Arrange
        ClientRequest clientRequest = ClientRequest
                .builder()
                .name("Test Client")
                .clientId(UUID.randomUUID().toString())
                .email("test@gmail.com")
                .phone("1234567890")
                .build();

        // Act
        ClientResponse response = clientService.createClient(clientRequest);

        // Assert
        assertNotNull("Response should not be null", response);
        assertEquals("Client ID should not be null", clientRequest.getClientId(), response.getClientId());
        assertEquals("Client name should match", clientRequest.getName(), response.getName());
        assertEquals("Client email should match", clientRequest.getEmail(), response.getEmail());
    }

    //add test case for failure
    @Test
    @Description("Test case for duplicate email id failure")
    void testCreateClient_failure(){



    }
}
