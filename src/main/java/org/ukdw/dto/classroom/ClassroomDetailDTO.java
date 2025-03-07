package org.ukdw.dto.classroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDetailDTO {
    private Long id;
    private String name;
    private String description;
    private String tahunAjaran;
    private String semester;
    private Set<Long> teacherIds;
    private Set<Long> studentIds;
}
