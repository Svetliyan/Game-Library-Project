package app.web.dto;

import app.category.model.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameRequest {
    @NotNull
    @Size(min = 5, max = 40, message = "Title length must be between 5 and 40 characters!")
    private String title;

    @NotNull
    @Size(min = 50, max = 20000, message = "Description must be over 50 characters!")
    private String description;

    @NotNull
    private int storage;

    @NotNull
    private BigDecimal price;

    @NotNull
    @URL
    private String img_url;

    private Integer category_id;

    @NotNull
    private List<Category> categories = new ArrayList<Category>();

    private boolean isVisible;
}
