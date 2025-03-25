package app.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CreateGiftCardRequest {

    @NotNull
    private String name;

    @NotNull
    private BigDecimal value;
}
