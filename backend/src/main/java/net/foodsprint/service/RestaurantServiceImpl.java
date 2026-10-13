package net.foodsprint.service;

import lombok.RequiredArgsConstructor;
import net.foodsprint.Exceptions.custom_exceptions.RestaurantNotFoundException;
import net.foodsprint.web.dto.request.CreateRestaurantRequest;
import net.foodsprint.domain.Address;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.repository.AddressRepository;
import net.foodsprint.repository.RestaurantRepository;
import net.foodsprint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final AddressRepository addressRepository;
    
    private final UserRepository userRepository;

    @Override
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {

        Address address = addressRepository.save(req.getAddress());
        Restaurant restaurant = new Restaurant();
        restaurant.setAddress(address);
        restaurant.setContactInformation(req.getContactInformation());
        restaurant.setCuisineType(req.getCuisineType());
        restaurant.setDescription(req.getDescription());
        restaurant.setImages(req.getImages());
        restaurant.setName(req.getName());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setOwner(user);

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant getRestaurantByUserId(Long userId) {
        return restaurantRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RestaurantNotFoundException(userId));
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) {
        Restaurant savedRestaurant = findRestaurantById(restaurantId);

        if (updatedRestaurant.getCuisineType() != null) {
            savedRestaurant.setCuisineType(updatedRestaurant.getCuisineType());
        }
        if (updatedRestaurant.getDescription() != null) {
            savedRestaurant.setDescription(updatedRestaurant.getDescription());
        }
        if (updatedRestaurant.getName() != null) {
            savedRestaurant.setName(updatedRestaurant.getName());
        }


        return restaurantRepository.save(savedRestaurant);
    }

    @Override
    public Restaurant updateRestaurantStatus(Long id) {
        Restaurant restaurant = findRestaurantById(id);
        restaurant.setOpen(!restaurant.isOpen());
        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);

        restaurantRepository.delete(restaurant);
    }


    @Override
    public Restaurant findRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }


}
