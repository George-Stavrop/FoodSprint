package net.foodsprint.unit;

import net.foodsprint.Exceptions.custom_exceptions.RestaurantNotFoundException;
import net.foodsprint.domain.Address;
import net.foodsprint.domain.ContactInformation;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.repository.AddressRepository;
import net.foodsprint.repository.RestaurantRepository;
import net.foodsprint.service.RestaurantServiceImpl;
import net.foodsprint.web.dto.request.CreateRestaurantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant restaurant;
    private User owner;
    private CreateRestaurantRequest request;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("test@test.com");

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Burgers");
        restaurant.setDescription("Test description");
        restaurant.setOpen(true);
        restaurant.setOwner(owner);

        request = new CreateRestaurantRequest();
        request.setName("Test Restaurant");
        request.setDescription("Test description");
        request.setCuisineType("Burgers");
        request.setAddress(new Address());
        request.setContactInformation(new ContactInformation());
        request.setImages(List.of("image1.png"));
    }

    // createRestaurant

    @Test
    void givenValidRequest_whenCreateRestaurant_thenRestaurantIsSaved() {
        given(addressRepository.save(any())).willReturn(new Address());
        given(restaurantRepository.save(any())).willReturn(restaurant);

        Restaurant result = restaurantService.createRestaurant(request, owner);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Restaurant");
        then(restaurantRepository).should().save(any());
    }

    // findRestaurantById

    @Test
    void givenValidId_whenFindRestaurantById_thenReturnRestaurant() {
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.findRestaurantById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void givenInvalidId_whenFindRestaurantById_thenThrowException() {
        given(restaurantRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.findRestaurantById(99L))
                .isInstanceOf(RestaurantNotFoundException.class);
    }

    // getRestaurantByUserId

    @Test
    void givenValidUserId_whenGetRestaurantByUserId_thenReturnRestaurant() {
        given(restaurantRepository.findByOwnerId(1L)).willReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getRestaurantByUserId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getOwner().getId()).isEqualTo(1L);
    }

    @Test
    void givenInvalidUserId_whenGetRestaurantByUserId_thenThrowException() {
        given(restaurantRepository.findByOwnerId(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getRestaurantByUserId(99L))
                .isInstanceOf(RestaurantNotFoundException.class);
    }

    // getAllRestaurants

    @Test
    void whenGetAllRestaurants_thenReturnList() {
        given(restaurantRepository.findAll()).willReturn(List.of(restaurant));

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    // updateRestaurant

    @Test
    void givenValidRequest_whenUpdateRestaurant_thenRestaurantIsUpdated() {
        request.setName("Updated Name");
        request.setCuisineType("Pizza");

        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(restaurantRepository.save(any())).willReturn(restaurant);

        Restaurant result = restaurantService.updateRestaurant(1L, request);

        assertThat(result).isNotNull();
        then(restaurantRepository).should().save(any());
    }

    @Test
    void givenInvalidId_whenUpdateRestaurant_thenThrowException() {
        given(restaurantRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.updateRestaurant(99L, request))
                .isInstanceOf(RestaurantNotFoundException.class);

        then(restaurantRepository).should(never()).save(any());
    }

    // updateRestaurantStatus

    @Test
    void givenOpenRestaurant_whenUpdateStatus_thenRestaurantIsClosed() {
        restaurant.setOpen(true);
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(restaurantRepository.save(any())).willReturn(restaurant);

        restaurantService.updateRestaurantStatus(1L);

        assertThat(restaurant.isOpen()).isFalse();
    }

    @Test
    void givenClosedRestaurant_whenUpdateStatus_thenRestaurantIsOpen() {
        restaurant.setOpen(false);
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(restaurantRepository.save(any())).willReturn(restaurant);

        restaurantService.updateRestaurantStatus(1L);

        assertThat(restaurant.isOpen()).isTrue();
    }

    // deleteRestaurant

    @Test
    void givenValidId_whenDeleteRestaurant_thenRestaurantIsDeleted() {
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        restaurantService.deleteRestaurant(1L);

        then(restaurantRepository).should().delete(restaurant);
    }

    @Test
    void givenInvalidId_whenDeleteRestaurant_thenThrowException() {
        given(restaurantRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.deleteRestaurant(99L))
                .isInstanceOf(RestaurantNotFoundException.class);

        then(restaurantRepository).should(never()).delete(any());
    }
}