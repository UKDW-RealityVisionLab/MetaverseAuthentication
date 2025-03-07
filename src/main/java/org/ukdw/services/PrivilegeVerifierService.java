package org.ukdw.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivilegeVerifierService {

    private final AuthService authService;

    public boolean hasPrivilege(String roles, Long... permissions) {
        try {
            log.info("Checking permission for roles: {} and permissions: {}", roles, permissions);
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String[] rolesArray = roles.split(",");
            return authService.canAccessFeature(rolesArray, permissions, request);
        } catch (Exception e) {
            log.error("Error while checking permission", e);
            return false;
        }
    }
}
