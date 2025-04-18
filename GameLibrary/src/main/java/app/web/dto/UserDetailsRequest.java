package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsRequest {
    @NotNull
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters!")
    private String username;

    @NotNull
    @Email
    private String email;

    @URL
    private String img_url;

    private BigDecimal balance;
}
