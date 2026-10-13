package net.foodsprint.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodsprint.config.JwtProvider;
import net.foodsprint.Exceptions.custom_exceptions.EmailAlreadyExistsException;
import net.foodsprint.domain.Cart;
import net.foodsprint.domain.USER_ROLE;
import net.foodsprint.domain.User;
import net.foodsprint.repository.CartRepository;
import net.foodsprint.repository.UserRepository;
import net.foodsprint.service.CustomerUserDetailsService;
import net.foodsprint.web.dto.request.AuthRequest;
import net.foodsprint.web.dto.request.LoginRequest;
import net.foodsprint.web.dto.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final CustomerUserDetailsService customerUserDetailsService;

    private final CartRepository cartRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody AuthRequest authRequest)  {

       if (userRepository.existsByEmail(authRequest.getEmail())) {
           throw new EmailAlreadyExistsException(authRequest.getEmail());
       }

        User createdUser = new User();
        createdUser.setEmail(authRequest.getEmail());
        createdUser.setFullName(authRequest.getFullName());
        createdUser.setRole(authRequest.getRole());
        createdUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        User savedUser = userRepository.save(createdUser);

        Cart cart = new Cart();
        cart.setCustomer(savedUser);
        cartRepository.save(cart);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Επιτυχία Εγγραφης");
        authResponse.setRole(savedUser.getRole());

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody LoginRequest request) {
       String username = request.getEmail();
       String password = request.getPassword();

       Authentication authentication = authenticate(username, password);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Επιτυχία εισόδου");
        authResponse.setRole(USER_ROLE.valueOf(role));

        return new ResponseEntity<>(authResponse, HttpStatus.OK);


    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Λάθος email ή κωδικός");
        }

        return new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
    }

}
