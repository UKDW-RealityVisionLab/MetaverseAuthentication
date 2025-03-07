package org.ukdw.dto.classroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomPublicDTO {
    private Long id;
    private String name;
    private String description;
    private String tahunAjaran;
    private String semester;
}
