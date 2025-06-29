package com.finbox.subscrititionservice.service.impl;


import com.finbox.subscrititionservice.exception.InvalidRequestException;
import com.finbox.subscrititionservice.exception.ResourceNotFoundException;
import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Feature;
import com.finbox.subscrititionservice.models.request.FeatureRequest;
import com.finbox.subscrititionservice.repositories.FeatureRepository;
import com.finbox.subscrititionservice.service.FeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FeatureServiceImpl implements FeatureService {


    private final FeatureRepository featureRepository;

    private static final Logger log = LoggerFactory.getLogger(FeatureServiceImpl.class);

    public FeatureServiceImpl(final FeatureRepository featureRepository){
        this.featureRepository = featureRepository;
    }

    @Override
    public Feature createFeature(FeatureRequest request) throws SubscriptionServiceException {
        log.info("Creating feature with request: {}", request);
        if (request.getCode() == null) {
            throw new IllegalArgumentException("Feature code must not be null");
        }

        if (featureRepository.findByCode(request.getCode()).isPresent()) {
            throw new InvalidRequestException("Feature with code " + request.getCode() + " already exists");
        }


        if (request.getParentFeatureId() != null && featureRepository.findById(request.getParentFeatureId()).isEmpty()) {
            throw new SubscriptionServiceException("Parent feature not found");
        }
        Feature feature = featureRepository.save(request.createFeature());
        log.info("Feature created: {}", feature);
        return feature;
    }

    @Override
    public boolean isFeatureEnabled(String featureCode) throws SubscriptionServiceException {
        Optional<Feature> feature = featureRepository.findByCode(featureCode);
        if(!feature.isPresent()){
            throw new ResourceNotFoundException("Feature not found");
        }
        return feature.get()
                .getIsEnabled() != null && feature.get().getIsEnabled();


    }

    @Override
    @Transactional
    public Feature toggleFeature(String code, boolean flag) throws SubscriptionServiceException {
        Feature feature = featureRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found"));

        /**
         * Toggle feature for all dependent child is toggling for a parent feature
         * */
        if (feature.getParentFeatureId() == null) {
            //find all the features with feature id as parent
            Optional<List<Feature>> childFeatures = featureRepository.findByParentFeatureId(feature.getId());
            childFeatures.ifPresent(features -> features.forEach(childFeature -> {
                childFeature.setIsEnabled(flag);
                featureRepository.save(childFeature);
            }));
        }

        /**
         * In case of child feature, toggle the feature on only if parent is on
         * */
        if(feature.getParentFeatureId() != null){
            Optional<Feature> parentFeature = featureRepository.findById(feature.getParentFeatureId());

            if(parentFeature.isPresent()){
                Feature parent = parentFeature.get();
                if(!parent.getIsEnabled()){
                    throw new SubscriptionServiceException("Parent feature is not enabled");
                }
            }else{
                throw new SubscriptionServiceException("Parent feature not found");
            }


        }

        feature.setIsEnabled(flag);
        return featureRepository.save(feature);
    }

    @Override
    public List<Feature> getAllFeatureFlag() throws SubscriptionServiceException {

        List<Feature> features = featureRepository.findAll();
        if (features.isEmpty()) {
            throw new SubscriptionServiceException("No features found", HttpStatus.NO_CONTENT.value());
        }
        return features.stream()
                .filter(Feature::getIsEnabled)
                .toList();
    }


}
