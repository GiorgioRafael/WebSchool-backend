package com.example.escola.domain.entities;
import com.example.escola.infrastructure.web.dto.food.FoodRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.*;

@Table(name= "foods") //foods
@Entity(name ="foods") //foods
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Data
public class Food {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //IDENTITY é em ordem crescente
    private Long id;
    private String title;
    private String image;
    private Integer price;


    public Food(FoodRequestDTO data){
        this.image = data.image();
        this.price = data.price();
        this.title = data.title();
    }
}
