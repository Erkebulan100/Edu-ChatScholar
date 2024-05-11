public class Instructor extends User {
    public Instructor(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public String getRole() {
        return "Instructor";
    }
}
