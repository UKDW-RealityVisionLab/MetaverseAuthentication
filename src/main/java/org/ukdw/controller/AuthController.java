package org.ukdw.controller;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.ukdw.dto.request.auth.*;
import org.ukdw.dto.response.AppsCheckPermissionResponse;
import org.ukdw.dto.response.RefreshAccessTokenResponse;
import org.ukdw.dto.response.ResponseWrapper;
import org.ukdw.entity.StudentEntity;
import org.ukdw.entity.TeacherEntity;
import org.ukdw.entity.UserAccountEntity;
import org.ukdw.exception.InvalidTokenException;
import org.ukdw.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


/**
 * Creator: dendy
 * Date: 7/11/2020
 * Time: 12:52 PM
 * Description : auth controller
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
//@Api(tags = AUTH_SERVICE_TAG)
public class AuthController {

    private final AuthService authService;

    @ResponseBody
//    @ApiOperation(value = "Signin")
    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signin(
//            @ApiParam(name = "SignInRequest", value = "serverAuthCode=server authcode dari google, " +
//                    "clientType=mobile_app atau web_app", required = true)
            @Valid @RequestBody SignInRequest request) {
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), "Signin success", authService.signIn(request.getEmail(), request.getPassword()));
        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        UserAccountEntity userEntity = authService.signUp(request);
        ResponseWrapper<UserAccountEntity> response = new ResponseWrapper<>(HttpStatus.OK.value(), "Signup success", userEntity);
        return ResponseEntity.ok(response);
    }

    @ResponseBody
//    @ApiOperation(value = "check permssion to access specific feature of client side.")
    @PostMapping(value = "/apps-check-permission", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> appsCheckPermission(@Valid @RequestBody AppsCheckPermissionRequest request) throws InvalidTokenException {
        ResponseWrapper<AppsCheckPermissionResponse> response = new ResponseWrapper<>(HttpStatus.OK.value(),
                AppsCheckPermissionResponse.builder()
                        .status(authService.canAccessFeature(request.getFeatureCode()))
                        .build());
        return ResponseEntity.ok(response);
    }


    // Endpoint untuk check permission akses endpoint API sesuai group yang telah di assign ke user
    @ResponseBody
    @PostMapping(value = "/check-permission")
    public ResponseEntity<?> checkPermission(@Valid @RequestBody CheckPermissionRequest checkPermissionRequest, HttpServletRequest request) {
        var response = AppsCheckPermissionResponse.builder()
                .status(authService.canAccessFeature(checkPermissionRequest.getRoles(), checkPermissionRequest.getPermissions(), request))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(HttpStatus.BAD_REQUEST.value(), "Invalid token"));
        }

        String token = authHeader.substring(7);
        try {
            var verifyTokenDto = authService.isTokenValidAndNotExpired(token);
            return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK.value(), "Token is valid", verifyTokenDto));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ResponseWrapper<>(e.getStatusCode().value(), e.getReason(), null));
        }
    }

    @ResponseBody
    @PostMapping(value = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        try {
            String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
            ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK.value(), newAccessToken);
            return ResponseEntity.ok(response);
        } catch (ParseException | JwtException e) {
            ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
