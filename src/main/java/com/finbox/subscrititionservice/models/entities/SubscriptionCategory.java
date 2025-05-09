package com.finbox.subscrititionservice.models.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscription_categories")
@Getter
@Setter
@Builder
public class SubscriptionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long code;
    private String name;
    private String description;
    private Boolean isEnabled;


    public boolean isEnabled() {
        return isEnabled != null && isEnabled;
    }

}
