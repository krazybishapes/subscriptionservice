package com.finbox.subscrititionservice.repositories;

import com.finbox.subscrititionservice.models.entities.SubscriptionCategoryFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionCategoryFeatureRepository extends JpaRepository<Long, SubscriptionCategoryFeatures> {

    Optional<List<SubscriptionCategoryFeatures>> findBySubscriptionCategoryId(Long subscriptionCategoryId);
}
