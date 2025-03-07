package org.ukdw.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentEntity {

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String nim;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String address;

    private String city;

    private String region;

    private String country;

    private String zipCode;

    private String gender;

}
