package app.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotNull(message = "Oops... You forgot to name your new category!")
    @Size(min = 3, max = 40, message = "Name length must be between 5 and 40 characters!")
    private String name;
}
