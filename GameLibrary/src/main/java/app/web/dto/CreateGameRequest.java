package app.web.dto;

import app.category.model.Category;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameRequest {
    @NotNull
    @Size(min = 5, max = 40, message = "Title length must be between 5 and 40 characters!")
    private String title;

    @NotNull
    @Size(min = 50, message = "Description must be between 50 and 255 characters!")
    private String description;

    @NotNull(message = "Cannot be null")
    private int storage;

    @NotNull(message = "Cannot be null")
    private BigDecimal price;

    @NotNull(message = "Cannot be null")
    @URL
    private String coverImage_url;

    @NotNull(message = "Cannot be null")
    @URL
    private String mainImg_url;

    @NotNull(message = "Cannot be null")
    @URL
    private String firstImage_url;

    @NotNull(message = "Cannot be null")
    @URL
    private String secondImage_url;

    @NotNull(message = "Cannot be null")
    @URL
    private String thirdImage_url;

    @NotNull(message = "Cannot be null")
    @URL
    private String fourthImage_url;

    private Integer category_id;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @NotNull
    private List<Category> categories = new ArrayList<Category>();

    private boolean isVisible;

    public CreateGameRequest(String newTitle, String newDescription, int i, BigDecimal bigDecimal, String image, String image1, String image2, String image3, String image4, String image5) {
    }
}
