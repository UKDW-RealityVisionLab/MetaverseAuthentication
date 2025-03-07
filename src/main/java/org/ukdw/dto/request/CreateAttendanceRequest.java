package org.ukdw.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttendanceRequest {
    Long classroomId;
    String openTime;
    String closeTime;
}
