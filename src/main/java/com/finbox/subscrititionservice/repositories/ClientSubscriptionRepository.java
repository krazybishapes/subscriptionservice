package com.finbox.subscrititionservice.repositories;

import com.finbox.subscrititionservice.models.entities.ClientSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientSubscriptionRepository extends JpaRepository<ClientSubscription, Long> {
    Optional<ClientSubscription> findByClientIdAndSubscriptionId(Long clientId, Long subscriptionId);
}
