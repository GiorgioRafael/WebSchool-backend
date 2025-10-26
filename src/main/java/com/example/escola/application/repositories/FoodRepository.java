package com.example.escola.application.repositories;

import com.example.escola.domain.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

}
