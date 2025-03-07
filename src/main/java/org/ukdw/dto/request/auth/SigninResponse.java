package org.ukdw.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninResponse {
    private String accessToken;
    private String refreshToken;
}
