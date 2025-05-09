package com.finbox.subscrititionservice.models.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "subscription_category_features")
@Getter
public class SubscriptionCategoryFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "subscription_category_id", nullable = false)
    private Long subscriptionCategoryId;
    @Column(name = "feature_id", nullable = false)
    private Long featureId;
    private Boolean isEnabled;

}
