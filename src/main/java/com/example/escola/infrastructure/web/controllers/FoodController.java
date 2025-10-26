//PAPEL do foodcontroller: Controlar o crud (create, read, update, delete) da tabela food
package com.example.escola.infrastructure.web.controllers;

import com.example.escola.domain.entities.Food;
import com.example.escola.application.repositories.FoodRepository;
import com.example.escola.infrastructure.web.dto.food.FoodRequestDTO;
import com.example.escola.infrastructure.web.dto.food.FoodResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController //anotacao para mapear que é um controller. precisa settar o endpoit
@RequestMapping("foods") //mapeando o endpoint e o determinando como "foods"

public class FoodController {

    @Autowired
    private FoodRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders="*")
    @PostMapping
    public void saveFood(@RequestBody FoodRequestDTO data) {
        Food foodData = new Food(data);
        repository.save(foodData);
        return;
    }

    @GetMapping //quando baterem no endpoint food, vai utilizar o metodo abaixo \/
    //public List<Food> getAll() {
    public List<FoodResponseDTO> getAll() {

        List<FoodResponseDTO> foodList = repository.findAll().stream()
                .map(FoodResponseDTO::new)
                .collect(Collectors.toList());
        return foodList; //nao é uma boa prática, deve-se criar DTO (Data transfer object)
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public void deleteFood(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public void updateFood(@PathVariable Long id, @RequestBody FoodRequestDTO data){
        Food foodData = repository.getReferenceById(id);
        foodData.setTitle(data.title());
        foodData.setImage(data.image());
        foodData.setPrice(data.price());
        repository.save(foodData);
    }

}

