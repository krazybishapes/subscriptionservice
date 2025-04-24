package com.finbox.subscrititionservice.models.request;



import com.finbox.subscrititionservice.models.entities.Feature;
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

