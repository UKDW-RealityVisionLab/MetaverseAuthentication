package org.ukdw.util;


import org.ukdw.dto.classroom.ClassroomDetailDTO;
import org.ukdw.dto.classroom.ClassroomPublicDTO;
import org.ukdw.entity.ClassroomEntity;

public class ClassroomMapper {

    public static ClassroomPublicDTO toPublicDTO(ClassroomEntity entity) {
        return new ClassroomPublicDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTahunAjaran(),
                entity.getSemester()
        );
    }

    public static ClassroomDetailDTO toDetailDTO(ClassroomEntity entity) {
        return new ClassroomDetailDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTahunAjaran(),
                entity.getSemester(),
                entity.getTeacherIds(),
                entity.getStudentIds()
        );
    }
}
