package com.example.final_project_17team.restaurant.repository;

import com.example.final_project_17team.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByNameAndAddress(String name, String address);
    Page<Restaurant> findAllByCategoryList_Name(String categoryName, Pageable pageable);

    Optional<Restaurant> findById(Long id);
}
