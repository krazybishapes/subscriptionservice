package com.finbox.subscrititionservice.repositories;



import com.finbox.subscrititionservice.models.entities.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    Optional<Feature> findByCode(String code);

    Optional<List<Feature>> findByParentFeatureId(Long parentFeatureId);
}

