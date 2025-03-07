package org.ukdw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {

    @NotBlank(message = "Resource name is required")
    private String resourceName;

    @NotNull(message = "Resource bit shift 2^(x) is required")
    @PositiveOrZero(message = "Permission must not be negative")
    private Long resourceShift;
}
