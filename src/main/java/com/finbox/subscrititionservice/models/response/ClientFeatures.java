package com.finbox.subscrititionservice.models.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientFeatures {

    private String clientId;
    private List<FeatureResponse> features;
}
