import java.util.ArrayList;
import java.util.List;

public class Course {
    private String code;
    private String name;
    private Instructor instructor;
    private List<Student> enrolledStudents = new ArrayList<>();

    public Course(String code, String name, Instructor instructor) {
        this.code = code;
        this.name = name;
        this.instructor = instructor;
    }

    public void enrollStudent(Student student) {
        enrolledStudents.add(student);
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public Instructor getInstructor() { return instructor; }
    public List<Student> getEnrolledStudents() { return enrolledStudents; }
}
