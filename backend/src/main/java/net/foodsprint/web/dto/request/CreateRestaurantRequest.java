package net.foodsprint.web.dto.request;

import net.foodsprint.domain.Address;
import net.foodsprint.domain.ContactInformation;
import lombok.Data;


import java.util.List;

@Data
public class CreateRestaurantRequest {

    private Long id;
    private String name;
    private String description;
    private String cuisineType;
    private Address address;
    private ContactInformation contactInformation;
    private String openingHours;
    private List<String> images;

}
