import java.io.*;
import java.util.*;

public class GradeBook {
    private static final String GRADES_FILE = "grades.txt";
    private static Map<String, Map<String, String[]>> grades = new HashMap<>();

    static {
        System.out.println("Loading grades...");
        loadGrades();  // Load grades when the class is initialized
    }

    public static Map<String, String[]> getGradesForStudent(String studentName) {
        // Fetch the grades for a specific student from the grades map
        Map<String, String[]> studentGrades = grades.get(studentName);
        if (studentGrades == null) {
            System.out.println("No grades found for " + studentName);
            return new HashMap<>();  // Return an empty map if there are no grades
        }
        return new HashMap<>(studentGrades);  // Return a copy of the grades map
    }

    public static void setGradeForStudent(String studentName, String courseName, String numericGrade, String letterGrade) {
        String[] gradeDetails = new String[]{numericGrade, letterGrade};
        grades.computeIfAbsent(studentName, k -> new HashMap<>()).put(courseName, gradeDetails);
        saveGrades();
    }

    public static void removeGradeForStudent(String studentName, String courseName) {
        Map<String, String[]> studentGrades = grades.get(studentName);
        if (studentGrades != null) {
            studentGrades.remove(courseName);
            if (studentGrades.isEmpty()) {
                grades.remove(studentName); // Optionally remove the student entry if no more courses are registered
            }
            saveGrades();  // Save changes after removal
        }
    }
    public static void saveGrades() {
        // Save the grades to a file
        System.out.println("Saving grades...");
        try (PrintWriter out = new PrintWriter(new FileWriter(GRADES_FILE, false))) {
            for (Map.Entry<String, Map<String, String[]>> entry : grades.entrySet()) {
                String student = entry.getKey();
                Map<String, String[]> courses = entry.getValue();
                for (Map.Entry<String, String[]> courseEntry : courses.entrySet()) {
                    out.printf("%s,%s,%s,%s%n", student, courseEntry.getKey(), courseEntry.getValue()[0], courseEntry.getValue()[1]);
                }
            }
            System.out.println("Grades saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save grades.");
        }
    }

    private static void loadGrades() {
        // Load grades from a file
        System.out.println("Loading grades from file...");
        File file = new File(GRADES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String studentName = parts[0];
                        String courseName = parts[1];
                        String numericGrade = parts[2];
                        String letterGrade = parts[3];
                        setGradeForStudent(studentName, courseName, numericGrade, letterGrade);
                    }
                }
                System.out.println("Grades loaded successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load grades.");
            }
        } else {
            System.out.println("No grades file found, starting fresh.");
        }
    }
}
