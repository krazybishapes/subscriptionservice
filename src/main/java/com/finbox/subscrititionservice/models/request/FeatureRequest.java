package com.finbox.subscriptionsvc.model.request;


import com.finbox.subscriptionsvc.model.entity.Feature;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FeatureRequest {
    private String name;
    private String description;
    private String code;
    private Boolean isActive;
    private Long parentFeatureId;

    public Feature createFeature() {
        return Feature.builder()
                .name(this.name)
                .code(this.code)
                .description(this.description)
                .isEnabled(Boolean.TRUE)
                .parentFeatureId(this.parentFeatureId)
                .build();
    }
}

