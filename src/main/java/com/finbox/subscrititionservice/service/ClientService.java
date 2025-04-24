package com.finbox.subscrititionservice.service;


import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import org.springframework.stereotype.Service;

@Service
public interface ClientService {
    ClientResponse createClient(ClientRequest clientRequest) throws SubscriptionServiceException;
}

