package com.example.final_project_17team.review.repository;

import com.example.final_project_17team.review.entity.Review;
import com.example.final_project_17team.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByRestaurantIdAndIdAndUser(Long restaurantId, Long reviewId, User user);
    Page<Review> findAllByRestaurantId(Long restaurantId, Pageable pageable);
    List<Review> findByUser(User user);
    void deleteAllByUser(User user);
    Optional<Review> findByIdAndRestaurantId(Long id, Long restaurantId);
}
