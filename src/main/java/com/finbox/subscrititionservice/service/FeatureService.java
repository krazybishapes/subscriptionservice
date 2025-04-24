package com.finbox.subscriptionsvc.service;


import com.finbox.subscriptionsvc.model.entity.Feature;
import com.finbox.subscriptionsvc.model.request.FeatureRequest;
import com.finbox.subscriptionsvc.model.response.CommonResponse;

import java.util.List;

public interface FeatureService {
    Feature createFeature(FeatureRequest request);
    boolean isFeatureEnabled(String featureCode);
    Feature toggleFeature(String code, boolean flag);

    List<Feature> getAllFeatureFlag();
}
