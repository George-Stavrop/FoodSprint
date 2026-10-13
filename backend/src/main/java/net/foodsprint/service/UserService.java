package net.foodsprint.service;

import net.foodsprint.domain.User;

public interface UserService {

     User findUserByJwtToken(String jwt);

     User findUserByEmail(String email);
}
