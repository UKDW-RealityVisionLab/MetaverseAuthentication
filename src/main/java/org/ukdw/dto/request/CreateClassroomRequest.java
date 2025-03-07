package org.ukdw.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateClassroomRequest{
    String name;
    String description;
    String tahunAjaran;
    String semester;
}
