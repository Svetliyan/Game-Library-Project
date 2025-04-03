package app.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {
    @Size(min = 3, max = 40, message = "Must be between 3 and 40 symbols!")
    @NotNull
    private String name;
}
