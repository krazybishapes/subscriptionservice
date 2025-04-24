package com.finbox.subscrititionservice.models.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "features")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Feature {

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

    private Long parentFeatureId;


    public boolean isEnabled() {
        return isEnabled != null && isEnabled;
    }
}

