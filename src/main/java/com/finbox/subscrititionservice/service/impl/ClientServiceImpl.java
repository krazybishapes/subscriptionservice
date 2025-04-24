package com.finbox.subscrititionservice.service.impl;

import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.repositories.ClientRepository;
import com.finbox.subscrititionservice.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;

public class ClientServiceImpl implements ClientService {
    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponse createClient(ClientRequest clientRequest) {
        if (clientRequest == null) {
            log.warn("Client request is null");
            return ClientResponse.builder().build();
        }

        log.info("Creating new client: {}", clientRequest.getEmail());
        String clientId = UUID.randomUUID().toString();
        clientRequest.setClientId(clientId);
        Client client = clientRepository.save(clientRequest.toClient());
        return ClientResponse
                .builder()
                .clientId(client.getClientId())
                .email(client.getEmail())
                .name(client.getName())
                .phone(client.getPhone())
                .build();
    }
}
