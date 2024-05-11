import java.util.ArrayList;
import java.util.List;

public class AuthenticationSystem {
    private List<User> users;

    public AuthenticationSystem() {
        this.users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
