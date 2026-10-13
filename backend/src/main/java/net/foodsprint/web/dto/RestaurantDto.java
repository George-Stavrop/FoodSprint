package net.foodsprint.web.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.List;

@Data
@Embeddable
public class RestaurantDto {




    @Column(length = 1000)
    private List<String> images;

    private String title;

    private String description;
    private Long id;





}
