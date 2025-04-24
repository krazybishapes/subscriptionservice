package com.finbox.subscriptionsvc.repository;


import com.finbox.subscriptionsvc.model.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    Optional<Feature> findByCode(String code);

    Optional<List<Feature>> findByParentFeatureId(Long parentFeatureId);
}

