package app.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CreateGiftCardRequest {

    @NotNull
    @Size(min = 3, max = 30, message = "Name length must be between 3 and 30 characters!")
    private String name;

    @NotNull
    @Min(value = 5, message = "GiftCard value must be at least 5lv.")
    private BigDecimal value;
}
