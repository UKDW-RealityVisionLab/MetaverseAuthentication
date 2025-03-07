package org.ukdw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.ukdw.entity.*;
import org.ukdw.repository.*;
import org.ukdw.services.implementation.ClassroomServiceImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class PopulateDatabase implements CommandLineRunner {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ClassroomServiceImpl classroomServiceImpl;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Insert group data first
        if (groupRepository.count() == 0) {
            createInitialGroups();
            System.out.println("Default groups inserted successfully.");
        } else {
            System.out.println("Groups already exist, skipping insert.");
        }

        // Insert user data with associated group entities
        if (userAccountRepository.count() == 0) {
            createInitialUsers();
            System.out.println("Default users inserted successfully.");
        } else {
            System.out.println("Users already exist, skipping insert.");
        }

        if (resourceRepository.count() == 0){
            createInitialResources();
            System.out.println("Default resources inserted successfully.");
        } else {
            System.out.println("Resources already exist, skipping insert.");
        }

        // Insert group data first
        if (studentRepository.count() == 0) {
            createInitialStudents();
            System.out.println("Default students inserted successfully.");
        } else {
            System.out.println("students already exist, skipping insert.");
        }

        // Insert user data with associated group entities
        if (teacherRepository.count() == 0) {
            createInitialTeachers();
            System.out.println("Default teachers inserted successfully.");
        } else {
            System.out.println("Users teachers exist, skipping insert.");
        }

        if (classroomRepository.count() == 0) {
            ClassroomEntity mathClassroom = new ClassroomEntity();
            mathClassroom.setName("Math Classroom");
            mathClassroom.setDescription("Kelas Univ Indonesia, Membahas tentang blablabla");
            mathClassroom.setSemester("Genap");
            mathClassroom.setTahunAjaran("2024/2025");
            mathClassroom.setTeacherIds(Set.of(3L));  // Example teacher IDs
            mathClassroom.setStudentIds(Set.of(1L));  // Example student IDs
            ClassroomEntity savedClassroom = classroomServiceImpl.createClassroom(mathClassroom);

            Instant currentTime = Instant.now();

            AttendanceEntity mathAttendance = new AttendanceEntity();
            mathAttendance.setClassroom(savedClassroom);
            mathAttendance.setOpenTime(currentTime.minus(4, ChronoUnit.HOURS));
            mathAttendance.setCloseTime(currentTime.plus(4, ChronoUnit.HOURS));

            // Example of students' attendance times
            Set<AttendanceRecord> records = new HashSet<>();
//            AttendanceRecord record1 = new AttendanceRecord(1L, currentTime.minus(30, ChronoUnit.MINUTES));
//            record1.setAttendance(mathAttendance);  // Set the reference to the parent AttendanceEntity

//            AttendanceRecord record2 = new AttendanceRecord(11L, currentTime.minus(20, ChronoUnit.MINUTES));
//            record2.setAttendance(mathAttendance);  // Set the reference to the parent AttendanceEntity

//            records.add(record1);
//            records.add(record2);

            // Set the records to the attendance entity
            mathAttendance.setRecords(records);

            // Save attendance record
            attendanceRepository.save(mathAttendance);

            System.out.println("Database has been populated with initial data.");

        } else {
            System.out.println("Classroom already exist, skipping insert.");
        }
    }

    private void createInitialGroups() {
        // Create and save GROUP records
        GroupEntity studentGroup = new GroupEntity();
        studentGroup.setGroupname("STUDENT");
        studentGroup.setPermission(1L);
        groupRepository.save(studentGroup);

        GroupEntity teacherGroup = new GroupEntity();
        teacherGroup.setGroupname("TEACHER");
        teacherGroup.setPermission(3L);
        groupRepository.save(teacherGroup);

        GroupEntity adminGroup = new GroupEntity();
        adminGroup.setGroupname("ADMIN");
//        adminGroup.setPermission(511L);
        adminGroup.setPermission(65535L);
        groupRepository.save(adminGroup);
    }

    private void createInitialUsers() {
        // Retrieve groups
        Optional<GroupEntity> studentGroupOpt = groupRepository.findByGroupname("STUDENT");
        Optional<GroupEntity> teacherGroupOpt = groupRepository.findByGroupname("TEACHER");
        Optional<GroupEntity> adminGroupOpt = groupRepository.findByGroupname("ADMIN");

        // Sample user data
        UserAccountEntity newUser2 = new UserAccountEntity(
                "admin@example.com",
                "admin",
                passwordEncoder.encode("password"),
                "REG124",
                "admin"
        );
        adminGroupOpt.ifPresent(groupEntity -> newUser2.setGroups(Set.of(groupEntity)));

        // Sample user data
        UserAccountEntity newUser1 = new UserAccountEntity(
                "student@example.com",
                "student",
                passwordEncoder.encode("password"),
                "REG128",
                "student"
        );
        studentGroupOpt.ifPresent(groupEntity -> newUser1.setGroups(Set.of(groupEntity)));

        UserAccountEntity newUser3 = new UserAccountEntity(
                "teacher@example.com",
                "teacher",
                passwordEncoder.encode("password"),
                "REG125",
                "teacher"
        );
        teacherGroupOpt.ifPresent(groupEntity -> newUser3.setGroups(Set.of(groupEntity)));

        // Save all users to the database in one save
        userAccountRepository.saveAll(List.of(newUser1, newUser2, newUser3));
    }

    private void createInitialResources(){
        ResourceEntity enterMathClass = new ResourceEntity();
        enterMathClass.setResourceName("ENTER_MATH_CLASSROOM");
        enterMathClass.setResourceBitmask(1L);
        resourceRepository.save(enterMathClass);

        ResourceEntity teachMathClass = new ResourceEntity();
        teachMathClass.setResourceName("TEACHING_MATH_CLASSROOM");
        teachMathClass.setResourceBitmask(2L);
        resourceRepository.save(teachMathClass);

        ResourceEntity administerMathClass = new ResourceEntity();
        administerMathClass.setResourceName("ADMINISTER_MATH_CLASSROOM");
        administerMathClass.setResourceBitmask(4L);
        resourceRepository.save(administerMathClass);

        ResourceEntity enterBioClass = new ResourceEntity();
        enterBioClass.setResourceName("ENTER_BIOLOGY_CLASSROOM");
        enterBioClass.setResourceBitmask(8L);
        resourceRepository.save(enterBioClass);

        ResourceEntity teachBioClass = new ResourceEntity();
        teachBioClass.setResourceName("TEACHING_BIOLOGY_CLASSROOM");
        teachBioClass.setResourceBitmask(16L);
        resourceRepository.save(teachBioClass);

        ResourceEntity administerBioClass = new ResourceEntity();
        administerBioClass.setResourceName("ADMINISTER_BIOLOGY_CLASSROOM");
        administerBioClass.setResourceBitmask(32L);
        resourceRepository.save(administerBioClass);
    }


    private void createInitialStudents() {
        // Sample user data
        StudentEntity student1 = new StudentEntity(
                1,
                "Student 1",
                "UKDW",
                "712000001",
                "081255555555",
                "Jl.UKDW",
                "Yogya",
                "Jawa Tengah",
                "Indonesia",
                "71501",
                "MALE"
        );

        studentRepository.saveAll(List.of(student1));
    }

    private void createInitialTeachers(){
        // Sample user data
        TeacherEntity teacher1 = new TeacherEntity(
                3,
                "Teacher 1",
                "UKDW",
                "712000001",
                "081255555555",
                "Jl.UKDW",
                "Yogya",
                "Jawa Tengah",
                "Indonesia",
                "71501",
                "MALE",
                "www.google-scholar.com"
        );

        teacherRepository.saveAll(List.of(teacher1));

    }
}