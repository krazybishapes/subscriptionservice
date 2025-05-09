package com.finbox.subscrititionservice.service;


import com.finbox.subscrititionservice.exception.ResourceNotFoundException;
import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.models.request.ClientSubscriptionRequest;
import com.finbox.subscrititionservice.models.response.ClientFeatures;
import com.finbox.subscrititionservice.models.response.ClientSubscriptionResponse;
import org.springframework.stereotype.Service;

@Service
public interface ClientService {
    ClientResponse createClient(ClientRequest clientRequest) throws SubscriptionServiceException;
    String toggleFeatureForClient(String clientId, Long featureId, boolean enable) throws SubscriptionServiceException;
    ClientFeatures getAllEnabledFeatures(String clientId) throws ResourceNotFoundException;
    boolean getFeatureFlagStatus(String clientId, Long feaureId) throws ResourceNotFoundException;

    ClientSubscriptionResponse createSubscription(ClientSubscriptionRequest clientSubscriptionRequest) throws SubscriptionServiceException;
}

