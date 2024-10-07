package org.ukdw.dto.request.auth;

import lombok.Data;

/**
 * Project: SRM-BE
 * Package: com.srmbe.dto.request.user
 * <p>
 * Creator: dendy
 * Date: 7/11/2020
 * Time: 12:52 PM
 * <p>
 * Description : Profile Request
 */

@Data
public class RefreshAccessTokenRequest {
    private String refreshToken;
}