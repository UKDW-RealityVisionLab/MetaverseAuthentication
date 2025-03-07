package org.ukdw.dto.group;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    @NotBlank(message = "Groupname is required")
    private String groupname;

    private Optional<Long> permission = Optional.empty();
}
