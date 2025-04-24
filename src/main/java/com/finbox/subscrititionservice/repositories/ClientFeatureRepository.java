package com.finbox.subscrititionservice.repositories;



import com.finbox.subscrititionservice.models.entities.ClientFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientFeatureRepository extends JpaRepository<ClientFeature, Long> {

    ClientFeature findByClientIdAndFeatureId(String clientId, Long featureId);
    List<ClientFeature> findByClientId(String clientId);
}

