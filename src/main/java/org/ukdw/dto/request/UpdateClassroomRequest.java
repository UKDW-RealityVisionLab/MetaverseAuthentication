package org.ukdw.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateClassroomRequest {
    String name;
    String description;
    String tahunAjaran;
    String semester;
    Set<Long> teacherIds;
    Set<Long> studentIds;
}
