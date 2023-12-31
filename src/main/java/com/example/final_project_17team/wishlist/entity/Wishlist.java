package com.example.final_project_17team.wishlist.entity;

import com.example.final_project_17team.global.entity.Base;
import com.example.final_project_17team.restaurant.entity.Restaurant;
import com.example.final_project_17team.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "wishlist")
public class Wishlist extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(nullable = false)
//    private boolean visited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Builder
    public Wishlist(User user, Restaurant restaurant) {
        this.user = user;
        this.restaurant = restaurant;
    }
}
