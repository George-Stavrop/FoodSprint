package net.foodsprint.bootstrap;

import lombok.RequiredArgsConstructor;
import net.foodsprint.domain.*;
import net.foodsprint.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final IngredientsItemRepository ingredientsItemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String BASE_URL = "/images/";

    @Override
    public void run(String... args) {
        if (restaurantRepository.count() > 0) return;

        createLaFamiglia();
        createMyBurger();
        createSmokers();
    }

    // LA FAMIGLIA

    private void createLaFamiglia() {
        User owner = createOwner("LaFamiglia", "lafamiglia@gmail.com", "lafamiglia");

        Restaurant restaurant = new Restaurant();
        restaurant.setName("La Famiglia");
        restaurant.setDescription("Κρέπες, burger και club sandwich που αξίζει να δοκιμάσεις!");
        restaurant.setCuisineType("Burger, Σάντουιτς, Club sandwich");
        restaurant.setOpeningHours("Δε-Κυ: 10:00-23:00");
        restaurant.setOpen(true);
        restaurant.setOwner(owner);
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setImages(List.of(BASE_URL + "lafamiglia.png", BASE_URL + "lafamigliaLogo.png"));

        ContactInformation contact = new ContactInformation();
        contact.setEmail("lafamiglia@gmail.com");
        contact.setMobile("2410-123456");
        contact.setTwitter("@lafamiglia");
        contact.setInstagram("@lafamiglia");
        restaurant.setContactInformation(contact);

        restaurant = restaurantRepository.save(restaurant);

        // Ingredient categories
        IngredientCategory ingredientCategory = saveIngredientCategory("Υλικά", restaurant);

        // Ingredients
        IngredientsItem philadelphia = saveIngredient("Philadelphia", ingredientCategory, restaurant);
        IngredientsItem bacon        = saveIngredient("Bacon", ingredientCategory, restaurant);
        IngredientsItem marouli      = saveIngredient("Μαρούλι", ingredientCategory, restaurant);
        IngredientsItem ntomata      = saveIngredient("Ντομάτα", ingredientCategory, restaurant);
        IngredientsItem cheddar      = saveIngredient("Cheddar", ingredientCategory, restaurant);
        IngredientsItem manouri      = saveIngredient("Μανούρι", ingredientCategory, restaurant);

        // Food categories
        Category clubSandwiches = saveCategory("Club Sandwiches", restaurant);
        Category burgers        = saveCategory("Burgers", restaurant);

        // Foods
        saveFood("Club sandwich Phila & Bacon",
                "Τραγανό ψωμί, bacon, μαρούλι, ντομάτα & Philadelphia. Με πατάτες.",
                7L, BASE_URL + "lafamigliaClub2.png", clubSandwiches, restaurant,
                List.of(philadelphia, bacon, marouli, ntomata));

        saveFood("Club sandwich Cheddar",
                "Τραγανό ψωμί, cheddar, μαρούλι και ντομάτα. Με πατάτες.",
                7L, BASE_URL + "lafamigliaCheddar.png", clubSandwiches, restaurant,
                List.of(cheddar, marouli, ntomata));

        saveFood("Club sandwich Μανούρι",
                "Τραγανό ψωμί, μανούρι, ντομάτα. Με πατάτες.",
                7L, BASE_URL + "lafamigliaManouri.png", clubSandwiches, restaurant,
                List.of(manouri, ntomata));

        saveFood("Burger Gianiotiko",
                "Burger με τοπικές γεύσεις Ιωαννίνων.",
                8L, BASE_URL + "lafamigliaGianiotiko.png", burgers, restaurant,
                List.of(cheddar, marouli, ntomata));
    }

    // ─── MY BURGER ─────────────────────────────────────────────

    private void createMyBurger() {
        User owner = createOwner("MyBurger", "myburger@gmail.com", "myburger");

        Restaurant restaurant = new Restaurant();
        restaurant.setName("My Burger");
        restaurant.setDescription("Χειροποίητα burgers και club sandwiches για λάτρεις του καλού φαγητού.");
        restaurant.setCuisineType("Burgers, Club Sandwich, Σαλάτες");
        restaurant.setOpeningHours("Δε-Κυ: 11:00-23:00");
        restaurant.setOpen(true);
        restaurant.setOwner(owner);
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setImages(List.of(BASE_URL + "myBurger.png", BASE_URL + "myBurgerLogo.png"));

        ContactInformation contact = new ContactInformation();
        contact.setEmail("myburger@gmail.com");
        contact.setMobile("2410-123456");
        contact.setTwitter("@myburger");
        contact.setInstagram("@myburger");
        restaurant.setContactInformation(contact);

        restaurant = restaurantRepository.save(restaurant);

        IngredientCategory ingredientCategory = saveIngredientCategory("Υλικά", restaurant);

        IngredientsItem mosxari   = saveIngredient("Μοσχάρι", ingredientCategory, restaurant);
        IngredientsItem cheddarMb = saveIngredient("Cheddar", ingredientCategory, restaurant);
        IngredientsItem marouli   = saveIngredient("Μαρούλι", ingredientCategory, restaurant);
        IngredientsItem ntomata   = saveIngredient("Ντομάτα", ingredientCategory, restaurant);
        IngredientsItem bbqSauce  = saveIngredient("BBQ Sauce", ingredientCategory, restaurant);
        IngredientsItem ciabata   = saveIngredient("Ciabata", ingredientCategory, restaurant);

        Category burgersCat = saveCategory("Burgers", restaurant);
        Category salatesCat = saveCategory("Σαλάτες", restaurant);

        saveFood("Dream Burger",
                "Χειροποίητο burger μοσχαριού με cheddar, μαρούλι και BBQ sauce.",
                9L, BASE_URL + "myBurgerDream.png", burgersCat, restaurant,
                List.of(mosxari, cheddarMb, marouli, bbqSauce));

        saveFood("Hangover Burger",
                "Ιδανικό για μετά. Διπλό μοσχάρι, διπλό cheddar.",
                11L, BASE_URL + "myBurgerHangover.png", burgersCat, restaurant,
                List.of(mosxari, cheddarMb, ntomata));

        saveFood("Italian Burger",
                "Burger με Italian εμπνεύσεις σε ciabata ψωμί.",
                10L, BASE_URL + "myBurgerItalian.png", burgersCat, restaurant,
                List.of(mosxari, ciabata, marouli, ntomata));

        saveFood("Club Sandwich Ciabata",
                "Club sandwich σε ciabata με φρέσκα υλικά.",
                8L, BASE_URL + "myBurgerCiabata.png", burgersCat, restaurant,
                List.of(ciabata, marouli, ntomata, cheddarMb));

        saveFood("Σαλάτα My Burger",
                "Φρέσκια σαλάτα με λαχανικά εποχής.",
                6L, BASE_URL + "myBurgerSalad1.png", salatesCat, restaurant,
                List.of(marouli, ntomata));
    }

    // ─── SMOKERS ───────────────────────────────────────────────

    private void createSmokers() {
        User owner = createOwner("Smokers", "smokers@gmail.com", "smokers");

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Smokers");
        restaurant.setDescription("Γύρος, σουβλάκι και πεϊνιρλί στη καλύτερή τους εκδοχή.");
        restaurant.setCuisineType("Σουβλάκι, Γύρος, Πεϊνιρλί");
        restaurant.setOpeningHours("Δε-Κυ: 10:00-02:00");
        restaurant.setOpen(true);
        restaurant.setOwner(owner);
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setImages(List.of(BASE_URL + "smokers.png", BASE_URL + "smokersLogo.png"));

        ContactInformation contact = new ContactInformation();
        contact.setEmail("smokers@gmail.com");
        contact.setMobile("2410-123456");
        contact.setTwitter("@smokers");
        contact.setInstagram("@smokers");

        restaurant = restaurantRepository.save(restaurant);

        IngredientCategory meat  = saveIngredientCategory("Κρέας", restaurant);
        IngredientCategory sauce = saveIngredientCategory("Σάλτσες", restaurant);

        IngredientsItem xoirino  = saveIngredient("Χοιρινό", meat, restaurant);
        IngredientsItem giros    = saveIngredient("Γύρος", meat, restaurant);
        IngredientsItem tzatziki = saveIngredient("Τζατζίκι", sauce, restaurant);
        IngredientsItem ouggareza    = saveIngredient("Ουγγαρέζα", sauce, restaurant);
        IngredientsItem feta     = saveIngredient("Φέτα", meat, restaurant);

        Category souvlakia  = saveCategory("Σουβλάκια & Γύρος", restaurant);
        Category peinirli   = saveCategory("Πεϊνιρλί", restaurant);

        saveFood("Smokers Deluxe",
                "Το signature μας. Γύρος χοιρινός με τζατζίκι και πατάτες.",
                4L, BASE_URL + "SmokersDeluxe.png", souvlakia, restaurant,
                List.of(xoirino, giros, tzatziki));

        saveFood("Γύρος Γιαννιώτικος",
                "Γύρος με φέτα και πιπεριές Φλωρίνης.",
                4L, BASE_URL + "SmokersGianniotiki.png", souvlakia, restaurant,
                List.of(giros, feta, tzatziki));

        saveFood("Smokers Γύρος",
                "Κλασικός γύρος σε πίτα με τζατζίκι.",
                3L, BASE_URL + "SmokersGiros.png", souvlakia, restaurant,
                List.of(giros, tzatziki));

        saveFood("Πεϊνιρλί Classic",
                "Πεϊνιρλί με ouggareza και αυγό.",
                5L, BASE_URL + "SmokersLacta.png", peinirli, restaurant,
                List.of(ouggareza));

        saveFood("Πεϊνιρλί Φέτα",
                "Πεϊνιρλί με φέτα και ντομάτα.",
                5L, BASE_URL + "SmokersPeinirli.png", peinirli, restaurant,
                List.of(feta));
    }

    // ─── HELPERS ───────────────────────────────────────────────

    private User createOwner(String fullname, String email, String password) {
        User user = new User();
        user.setFullName(fullname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(USER_ROLE.ROLE_RESTAURANT_OWNER);
        return userRepository.save(user);
    }

    private IngredientCategory saveIngredientCategory(String name, Restaurant restaurant) {
        IngredientCategory ingredientCategory = new IngredientCategory();
        ingredientCategory.setName(name);
        ingredientCategory.setRestaurant(restaurant);
        return ingredientCategoryRepository.save(ingredientCategory);
    }

    private IngredientsItem saveIngredient(String name, IngredientCategory ingredientCategory, Restaurant restaurant) {
        IngredientsItem ingredientsItem = new IngredientsItem();
        ingredientsItem.setName(name);
        ingredientsItem.setCategory(ingredientCategory);
        ingredientsItem.setRestaurant(restaurant);
        ingredientsItem.setInStock(true);
        return ingredientsItemRepository.save(ingredientsItem);
    }

    private Category saveCategory(String name, Restaurant restaurant) {
        Category category = new Category();
        category.setName(name);
        category.setRestaurant(restaurant);
        return categoryRepository.save(category);
    }

    private void saveFood(String name, String desc, Long price,
                          String imageUrl, Category category, Restaurant restaurant,
                          List<IngredientsItem> ingredients) {
        Food f = new Food();
        f.setName(name);
        f.setDescription(desc);
        f.setPrice(price);
        f.setImages(List.of(imageUrl));
        f.setFoodCategory(category);
        f.setRestaurant(restaurant);
        f.setIngredients(ingredients);
        f.setAvailable(true);
        f.setCreationDate(LocalDateTime.now());
        foodRepository.save(f);
    }
}
