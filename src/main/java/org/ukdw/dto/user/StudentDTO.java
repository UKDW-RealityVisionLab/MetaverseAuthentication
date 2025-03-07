package org.ukdw.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String firstName;
    private String lastName;
    private String nim;
    private String phoneNumber;
    private String address;
    private String city;
    private String region;
    private String country;
    private String zipCode;
    private String gender;
}
