import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AuthenticationSystemLoader {

    private AuthenticationSystem authSystem;

    public AuthenticationSystemLoader(AuthenticationSystem authSystem) {
        this.authSystem = authSystem;
    }

    public void loadUsers(String filePath, String userType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    String email = parts[1].trim();
                    String password = parts[2].trim();
                    if ("student".equals(userType)) {
                        authSystem.addUser(new Student(name, email, password));
                    } else if ("instructor".equals(userType)) {
                        authSystem.addUser(new Instructor(name, email, password));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load users from " + filePath);
        }
    }
}
