package org.ukdw.dto.request.auth;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;


/**

 * Creator: haniif
 * Date: 10/26/2024
 * Time: 8:05 PM
 *
 * Description : Add / remove permission from group
 */
@Setter
@Getter
public class GroupPermissionRequest {
    @NotNull(message = "group id is required")
    private Long groupId;

    @NotNull(message = "permission is required")
    @PositiveOrZero(message = "Permission must not be negative")
    private Long permission;
}

