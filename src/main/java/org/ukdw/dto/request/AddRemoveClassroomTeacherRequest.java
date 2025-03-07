package org.ukdw.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRemoveClassroomTeacherRequest {
    @NotNull(message = "teacherId cannot be null")
    private Long teacherId;
}
