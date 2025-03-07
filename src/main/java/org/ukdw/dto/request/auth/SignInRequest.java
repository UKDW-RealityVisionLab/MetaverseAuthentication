package org.ukdw.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: SRM-BE
 * Package: com.srmbe.model.request.auth
 * <p>
 * Creator: dendy
 * Date: 7/11/2020
 * Time: 12:55 PM
 * <p>
 * Description : AuthRequest
 */
@Setter
@Getter
@AllArgsConstructor
public class SignInRequest {
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}

