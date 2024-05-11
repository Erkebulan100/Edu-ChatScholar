import java.io.*;
import java.util.*;

public class LessonManagement {
    private static final Map<String, Map<String, String>> studentCourses = new HashMap<>();
    private static final String COURSES_FILE = "courses.txt";

    static {
        loadCourses();  // Load course data from file on startup
    }

    public static void setCoursesForStudent(String studentName, Map<String, String> courses) {
        studentCourses.put(studentName, new HashMap<>(courses));
        saveCourses();
    }

    public static Map<String, String> getCoursesForStudent(String studentName) {
        return studentCourses.getOrDefault(studentName, new HashMap<>());
    }

    public static void addCoursesForStudent(String studentName, List<String> courses) {
        Map<String, String> existingCourses = studentCourses.computeIfAbsent(studentName, k -> new HashMap<>());
        courses.forEach(course -> existingCourses.put(course, "3"));  // Assume 3 credits if not specified
        saveCourses();
    }

    public static void removeCoursesForStudent(String studentName, List<String> courses) {
        Map<String, String> existingCourses = studentCourses.get(studentName);
        if (existingCourses != null) {
            courses.forEach(existingCourses::remove);
            saveCourses();
        }
    }

    public static boolean addCourseForStudent(String studentName, String course, String credits) {
        Map<String, String> courses = studentCourses.computeIfAbsent(studentName, k -> new HashMap<>());
        if (!courses.containsKey(course)) {
            courses.put(course, credits);
            saveCourses();
            return true;  // Indicates the course was successfully added
        }
        return false;  // Indicates the course was not added because it already exists
    }

    public static void removeCourseForStudent(String studentName, String course) {
        Map<String, String> courses = studentCourses.get(studentName);
        if (courses != null && courses.remove(course) != null) {
            saveCourses();
            // Reflect these changes back to any listening views
            notifyViews();
        }
    }

    public static List<String[]> getCoursesWithCreditsForStudent(String studentName) {
        List<String[]> coursesWithCredits = new ArrayList<>();
        Map<String, String> courses = getCoursesForStudent(studentName);
        for (Map.Entry<String, String> entry : courses.entrySet()) {
            coursesWithCredits.add(new String[]{entry.getKey(), entry.getValue()});
        }
        return coursesWithCredits;
    }

    private static void saveCourses() {
        try (PrintWriter out = new PrintWriter(new FileWriter(COURSES_FILE))) {
            for (Map.Entry<String, Map<String, String>> entry : studentCourses.entrySet()) {
                out.print(entry.getKey() + ":");
                Map<String, String> courses = entry.getValue();
                courses.forEach((course, credit) -> out.print(course + "," + credit + ";"));
                out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadCourses() {
        File file = new File(COURSES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String name = parts[0];
                        Map<String, String> courses = new HashMap<>();
                        for (String courseCredit : parts[1].split(";")) {
                            String[] courseCreditParts = courseCredit.split(",");
                            if (courseCreditParts.length == 2) {
                                courses.put(courseCreditParts[0], courseCreditParts[1]);
                            }
                        }
                        studentCourses.put(name, courses);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No courses file found, starting fresh.");
        }
    }

    private static void notifyViews() {
        // This method would typically notify any observers that the course list has changed.
        // This could be implemented via an observer pattern or a similar mechanism to update GUI components.
    }
}
