package net.foodsprint.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Email
    private String email;

    @Size(min = 4, message = "Ο κωδικός πρέπει να έχει τουλάχιστον 4 χαρακτήρες")
    private String password;
}
