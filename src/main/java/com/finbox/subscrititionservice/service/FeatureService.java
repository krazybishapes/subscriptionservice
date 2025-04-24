package com.finbox.subscrititionservice.service;




import com.finbox.subscrititionservice.exception.SubscriptionServiceException;
import com.finbox.subscrititionservice.models.entities.Feature;
import com.finbox.subscrititionservice.models.request.FeatureRequest;

import java.util.List;

public interface FeatureService {
    Feature createFeature(FeatureRequest request);
    boolean isFeatureEnabled(String featureCode);
    Feature toggleFeature(String code, boolean flag);

    List<Feature> getAllFeatureFlag() throws SubscriptionServiceException;
}
