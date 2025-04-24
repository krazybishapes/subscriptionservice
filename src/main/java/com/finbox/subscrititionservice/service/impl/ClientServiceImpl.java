package com.finbox.subscrititionservice.service.impl;

import com.finbox.subscrititionservice.exception.ResourceNotFoundException;
import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Client;
import com.finbox.subscrititionservice.models.entities.ClientFeature;
import com.finbox.subscrititionservice.models.entities.Feature;
import com.finbox.subscrititionservice.models.request.ClientRequest;
import com.finbox.subscrititionservice.models.request.ClientResponse;
import com.finbox.subscrititionservice.models.response.ClientFeatures;
import com.finbox.subscrititionservice.models.response.FeatureResponse;
import com.finbox.subscrititionservice.repositories.ClientFeatureRepository;
import com.finbox.subscrititionservice.repositories.ClientRepository;
import com.finbox.subscrititionservice.repositories.FeatureRepository;
import com.finbox.subscrititionservice.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final FeatureRepository featureRepository;
    private final ClientFeatureRepository clientFeatureRepository;

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    public ClientServiceImpl(ClientRepository clientRepository, FeatureRepository featureRepository, ClientFeatureRepository clientFeatureRepository) {
        this.clientRepository = clientRepository;
        this.featureRepository = featureRepository;
        this.clientFeatureRepository = clientFeatureRepository;
    }

    @Override
    public ClientResponse createClient(ClientRequest clientRequest) throws SubscriptionServiceException {
        if (clientRequest == null) {
            log.warn("Client request is null");
            throw new SubscriptionServiceException(
                    "Client request cannot be null",
                    HttpStatus.BAD_REQUEST.value()
            );
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

    @Override
    @Transactional
    public String toggleFeatureForClient(String clientId, Long featureId, boolean enable) {
        log.info("Toggling feature {} for client {} - enable: {}", featureId, clientId, enable);

        /**
         * Ideally we should have a feature code to identify the feature
         *         and calling service layer instead of calling repository directly
         */

        //STEP1: Validate if client is valid
        clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        //STEP2: Validate if feature is valid
        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + featureId));

        //STEP 3: Check parent rule
        if (enable && feature.getParentFeatureId() != null) {
            ClientFeature clientFeature = clientFeatureRepository
                    .findByClientIdAndFeatureId(clientId, feature.getParentFeatureId());

            if (clientFeature == null || !clientFeature.isEnabled()) {
                return "Cannot enable child feature when parent is disabled";
            }
        }

        //STEP 4: check child rule, if meet enable/disable for all child features
        if (feature.getParentFeatureId() == null) {
            Optional<List<Feature>> childFeaturesOptional = featureRepository
                    .findByParentFeatureId(featureId);

            if (childFeaturesOptional.isPresent()) {
                List<Feature> childFeatures = childFeaturesOptional.get();
                for (Feature childFeature : childFeatures) {
                    ClientFeature childClientFeature = clientFeatureRepository
                            .findByClientIdAndFeatureId(clientId, childFeature.getId());

                    if (childClientFeature == null) {
                        childClientFeature = ClientFeature
                                .builder()
                                .clientId(clientId)
                                .featureId(childFeature.getId())
                                .enabled(enable)
                                .build();
                    } else {
                        childClientFeature.setEnabled(enable);
                    }

                    clientFeatureRepository.save(childClientFeature);
                }
            }


        }

        //STEP 5: Update or create Client Feature mapping
        ClientFeature clientFeature = clientFeatureRepository
                .findByClientIdAndFeatureId(clientId, featureId);

        if (clientFeature == null) {
            clientFeature = ClientFeature
                    .builder()
                    .clientId(clientId)
                    .featureId(featureId)
                    .enabled(enable)
                    .build();


            log.info("Creating new feature toggle record");
        } else {
            clientFeature.setEnabled(enable);
            log.info("Updating existing feature toggle record");
        }

        clientFeatureRepository.save(clientFeature);

        return "Feature toggled successfully for client.";
    }

    @Override
    public ClientFeatures getAllEnabledFeatures(String clientId) {
        log.info("Fetching all enabled features for client {}", clientId);
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        List<ClientFeature> clientFeatures = clientFeatureRepository
                .findByClientId(clientId);
        List<Feature> features = featureRepository.findAllById(clientFeatures.stream()
                .map(ClientFeature::getFeatureId)
                .toList());
        List<Feature> enabledFeatures = features.stream()
                .filter(Feature::isEnabled)
                .toList();
        if (enabledFeatures.isEmpty()) {
            throw new ResourceNotFoundException("No enabled features found for client with id: " + clientId);
        }
        log.info("Enabled features for client {}: {}", clientId, enabledFeatures);
        // Convert to FeatureResponse
        List<FeatureResponse> featureResponses = enabledFeatures.stream()
                .map(feature -> FeatureResponse.builder()
                        .code(feature.getCode())
                        .name(feature.getName())
                        .description(feature.getDescription())
                        .build())
                .toList();
        return ClientFeatures.builder()
                .clientId(client.getClientId())
                .features(featureResponses)
                .build();
    }

    @Override
    public boolean getFeatureFlagStatus(String clientId, Long featureId) {
        log.info("Fetching feature flag status for client {} and feature {}", clientId, featureId);
        ClientFeature clientFeature = clientFeatureRepository
                .findByClientIdAndFeatureId(clientId, featureId);
        if (clientFeature == null) {
            throw new ResourceNotFoundException("No feature flag found for client with id: " + clientId + " and feature id: " + featureId);
        }
        return clientFeature.isEnabled();

    }
}
