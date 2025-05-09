package com.finbox.subscrititionservice.models.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Builder
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(name = "enabled", nullable = false)
    private Boolean isEnabled;

}
