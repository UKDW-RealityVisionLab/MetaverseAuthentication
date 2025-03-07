package org.ukdw.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRemoveClassroomStudentRequest {
    @NotNull(message = "teacherId cannot be null")
    private Long studentId;
}
