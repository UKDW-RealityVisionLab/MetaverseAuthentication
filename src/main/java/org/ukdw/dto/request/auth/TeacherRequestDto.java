package org.ukdw.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherRequestDto {

    private long userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String nid;
    private String address;
    private String city;
    private String region;
    private String country;
    private String zipCode;
    private String gender;
    private String googleScholar;

}
