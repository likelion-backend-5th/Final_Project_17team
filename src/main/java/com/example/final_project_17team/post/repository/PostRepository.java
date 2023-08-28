package com.example.final_project_17team.post.repository;

import com.example.final_project_17team.myrestaurant.entity.MyRestaurant;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndUserId(Long PostId, Long userId);
}