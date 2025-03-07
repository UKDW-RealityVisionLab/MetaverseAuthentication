package org.ukdw.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyTokenDto {
    private String role;
    private int permission;
    private int id;
}
