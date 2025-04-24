package com.finbox.subscrititionservice.service.impl;

import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.repositories.ClientRepository;
import com.finbox.subscrititionservice.service.ClientService;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponse createClient(ClientRequest clientRequest) {
        return null;
    }
}
