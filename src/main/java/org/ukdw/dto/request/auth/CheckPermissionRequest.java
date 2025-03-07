package org.ukdw.dto.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CheckPermissionRequest {
    @NotNull(message = "permission is required")
    private Long[] permissions;

    @NotNull(message = "role is required")
    private String[] roles;
}
