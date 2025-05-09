package com.finbox.subscrititionservice.models.entities;

import jakarta.persistence.*;
import lombok.Builder;

import java.util.Date;

@Entity
@Table(name = "client_subscriptions")
@Builder
public class ClientSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    @Column(name = "subscription_category_id", nullable = false)
    private Long subscriptionCategoryId;
    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    private Boolean isEnabled;
}
