public class Student extends User {
    public Student(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public String getRole() {
        return "Student";
    }
}
