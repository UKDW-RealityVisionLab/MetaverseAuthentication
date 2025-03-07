package org.ukdw.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;
import org.ukdw.filter.TokenAuthenticationFilter;
import org.ukdw.services.AuthService;
import org.ukdw.services.UserAccountService;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * <p>
 * Creator: dendy
 * Date: 6/30/2020
 * Time: 7:50 AM
 * <p>
 * Description : Web Security Configuration
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserAccountService userAccountService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    @Qualifier("delegateAuthenticationEntryPoint")
    private final AuthenticationEntryPoint authEntryPoint;

    private static final String[] ENDPOINT_WHITELIST = {
            "/auth/signin",
            "/auth/signup/**",
            "/auth/verify",
            "/auth/refresh-token",
            "/auth/apps-check-permission",
            "/auth/check-permission",
            "/h2-console/**"
    };

    //https://medium.com/@truongbui95/jwt-authentication-and-authorization-with-spring-boot-3-and-spring-security-6-2f90f9337421
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // prohibit in prod
                //If we use Spring Security in our project, we must take an extra step to make sure it plays well with CORS.
                // That’s because CORS needs to be processed first. Otherwise, Spring Security will reject the request before
                // it reaches Spring MVC.
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(ENDPOINT_WHITELIST)
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .httpBasic(basic -> basic.authenticationEntryPoint(authEntryPoint))
                .exceptionHandling(Customizer.withDefaults())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userAccountService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //    https://www.baeldung.com/spring-cors
    /*We can configure CORS to override the default Spring Security CORS handling.
    For that, we need to add a CorsConfigurationSource bean that takes care of the CORS configuration
    using a CorsConfiguration instance. The http.cors() method uses CorsFilter if a corsFilter
    bean is added, else it uses CorsConfigurationSource. If neither is configured,
    then it uses the Spring MVC pattern inspector handler.*/
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
