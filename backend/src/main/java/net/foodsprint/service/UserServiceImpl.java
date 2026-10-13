package net.foodsprint.service;

import lombok.RequiredArgsConstructor;
import net.foodsprint.config.JwtProvider;
import net.foodsprint.domain.User;
import net.foodsprint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final  UserRepository userRepository;

    private final JwtProvider jwtProvider;

    @Override
    public User findUserByJwtToken(String jwt) {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->new UsernameNotFoundException("Ο χρήστης δεν βρέθηκε"));
    }
}
