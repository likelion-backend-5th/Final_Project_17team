package com.example.final_project_17team.controller;

import com.example.final_project_17team.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@AllArgsConstructor
public class RestaurantController {
    private final RestaurantService service;

    @GetMapping("/search")
    @ResponseBody
    public String search(
            @RequestParam("target") String target
    ) throws IOException {
        return service.searchRestaurant(target);
    }
}