package com.finbox.subscrititionservice.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscription_categories")
@Getter
@Setter
@Builder
public class SubscriptionCategory {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Boolean isEnabled;

    public boolean isEnabled() {
        return isEnabled != null && isEnabled;
    }

    // Getters and Setters
}
