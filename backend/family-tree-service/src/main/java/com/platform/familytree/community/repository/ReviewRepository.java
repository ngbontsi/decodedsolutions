package com.platform.familytree.community.repository;

import com.platform.familytree.community.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByClaimIdOrderByReviewedAtDesc(UUID claimId);
}
