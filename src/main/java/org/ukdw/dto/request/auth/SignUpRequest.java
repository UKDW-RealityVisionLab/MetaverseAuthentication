package org.ukdw.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@(staff|ti)\\.ukdw\\.ac\\.id$",
            message = "Invalid email format. Make sure it's staff / ti email account"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 40, message = "Password must be between 5 and 40 characters")
    private String password;

    @NotBlank(message = "Username is required")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9._]{3,38}[A-Za-z0-9]$",
            message = "Username must be 5-40 characters long, start with a letter, and only contain letters, numbers, underscores, or dots. It cannot start or end with an underscore or dot."
    )
    private String username;

    private String regNumber;

    @NotBlank(message = "scope is required (student, teacher)")
    @Pattern(
            regexp = "^(student|teacher)$",
            message = "Scope must be either 'student' or 'teacher'."
    )
    private String scope;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String nim; // untuk mahasiswa
    private String nid; // untuk dosen
    private String address;
    private String city;
    private String region;
    private String country;
    private String zipCode;
    private String gender;
    private String urlGoogleScholar;
}
