import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class InstructorWindow extends JFrame {
    private User currentUser;
    private JComboBox<String> studentSelector;
    private JTable coursesTable, availableCoursesTable;
    private JScrollPane scrollPane, availableScrollPane;
    private JButton addCourseButton, removeCourseButton, refreshButton, assignGradesButton, saveGradesButton;
    private JPanel topPanel, buttonPanel;
    private JLabel selectStudentLabel, enrolledCoursesLabel, availableCoursesLabel;

    public InstructorWindow(User user) {
        this.currentUser = user;
        setTitle("Instructor Dashboard - " + currentUser.getName());
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
        addListeners();
        setVisible(true);
    }

    private void initComponents() {
        topPanel = new JPanel();
        studentSelector = new JComboBox<>();
        populateStudents();
        selectStudentLabel = new JLabel("Select Student:");
        topPanel.add(selectStudentLabel);
        topPanel.add(studentSelector);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Select", "Course Name", "Credits", "Grades", "Grade Letter"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 3;
            }
        };
        coursesTable = new JTable(model);
        coursesTable.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(coursesTable);
        enrolledCoursesLabel = new JLabel("Currently Enrolled In");
        enrolledCoursesLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(enrolledCoursesLabel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        DefaultTableModel availableCoursesModel = new DefaultTableModel(null, new String[]{"Select", "Course Name", "Credits"}) {
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        availableCoursesTable = new JTable(availableCoursesModel);
        availableCoursesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); availableCoursesTable.setRowSelectionAllowed(true);
        availableCoursesTable.setFillsViewportHeight(true);
        availableScrollPane = new JScrollPane(availableCoursesTable);
        availableCoursesLabel = new JLabel("Courses to Select");
        availableCoursesLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(availableCoursesLabel, BorderLayout.NORTH);
        rightPanel.add(availableScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        addCourseButton = new JButton("Add Selected Courses");
        removeCourseButton = new JButton("Remove Selected Courses");
        refreshButton = new JButton("Refresh List");
        assignGradesButton = new JButton("Assign Grades");
        saveGradesButton = new JButton("Save Grades");
        buttonPanel.add(addCourseButton);
        buttonPanel.add(removeCourseButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(assignGradesButton);
        buttonPanel.add(saveGradesButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        studentSelector.addActionListener(e -> updateCourseView());
        addCourseButton.addActionListener(this::addSelectedCourses);
        removeCourseButton.addActionListener(this::removeSelectedCourses);
        refreshButton.addActionListener(e -> {
            updateCourseView();
            updateAvailableCourses();
        });
        assignGradesButton.addActionListener(e -> assignGrades());
        saveGradesButton.addActionListener(e -> GradeBook.saveGrades());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustFontSizes();
            }
        });
    }

    private void adjustFontSizes() {
        float size = Math.min(16, getHeight() / 30);
        Font font = new Font("Arial", Font.PLAIN, (int) size);
        selectStudentLabel.setFont(font);
        studentSelector.setFont(font);
        coursesTable.setFont(new Font("Arial", Font.BOLD, (int) size));
        availableCoursesTable.setFont(new Font("Arial", Font.BOLD, (int) size));
        addCourseButton.setFont(font);
        removeCourseButton.setFont(font);
        refreshButton.setFont(font);
        assignGradesButton.setFont(font);
        saveGradesButton.setFont(font);
        enrolledCoursesLabel.setFont(font);
        availableCoursesLabel.setFont(font);
    }

    private void populateStudents() {
        String path = "C:\\Users\\erkeb\\IdeaProjects\\EduChat Scholar\\src\\students.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                studentSelector.addItem(line.split(",")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load student data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCourseView() {
        if (studentSelector.getSelectedItem() != null) {
            String selectedStudent = studentSelector.getSelectedItem().toString();
            Map<String, String> courses = LessonManagement.getCoursesForStudent(selectedStudent);
            DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
            model.setRowCount(0);
            courses.forEach((courseName, credits) -> {
                String[] gradesInfo = GradeBook.getGradesForStudent(selectedStudent).getOrDefault(courseName, new String[]{"", ""});
                model.addRow(new Object[]{false, courseName, credits, gradesInfo[0], gradesInfo[1]});
            });
        }
    }

    private void assignGrades() {
        DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String gradeStr = (String) model.getValueAt(i, 3);
            String gradeLetter = "";

            if (!gradeStr.isEmpty()) {
                try {
                    int gradeValue = Integer.parseInt(gradeStr);
                    gradeLetter = getGradeLetter(gradeValue);
                } catch (NumberFormatException ex) {
                    gradeLetter = "";  // Keep grade letter empty if parsing fails
                }
            }

            model.setValueAt(gradeLetter, i, 4);
            String courseName = (String) model.getValueAt(i, 1);
            String studentName = studentSelector.getSelectedItem().toString();

            if (!gradeLetter.isEmpty()) {
                GradeBook.setGradeForStudent(studentName, courseName, gradeStr, gradeLetter);
            } else {
                GradeBook.removeGradeForStudent(studentName, courseName); // Call the new method to remove the grade
            }
        }
    }




    private String getGradeLetter(int grade) {
        if (grade >= 90) return "AA";
        if (grade >= 85) return "BA";
        if (grade >= 80) return "BB";
        if (grade >= 75) return "CB";
        if (grade >= 70) return "CC";
        if (grade >= 65) return "DC";
        if (grade >= 60) return "DD";
        if (grade >= 50) return "FD";
        return "FF";
    }

    private void addSelectedCourses(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) availableCoursesTable.getModel();
        int[] selectedRows = availableCoursesTable.getSelectedRows();
        String studentName = studentSelector.getSelectedItem().toString();
        List<String> addedCourses = new ArrayList<>();

        for (int rowIndex : selectedRows) {
            String courseName = (String) model.getValueAt(rowIndex, 1);
            String credits = (String) model.getValueAt(rowIndex, 2);
            if (LessonManagement.addCourseForStudent(studentName, courseName, credits)) {
                addedCourses.add(courseName);
            }
        }

        if (!addedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Added courses: " + String.join(", ", addedCourses), "Courses Added", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No courses were added.", "No Changes Made", JOptionPane.WARNING_MESSAGE);
        }

        updateCourseView();  // Ensure this method refreshes coursesTable to show current enrollments
        updateAvailableCourses();  // Ensure this method refreshes availableCoursesTable
    }



    private void removeSelectedCourses(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
        int[] selectedRows = coursesTable.getSelectedRows();
        List<String> coursesToRemove = new ArrayList<>();  // Ensure this list is defined inside the method

        for (int i = selectedRows.length - 1; i >= 0; i--) {
            String courseName = (String) model.getValueAt(selectedRows[i], 1);
            coursesToRemove.add(courseName);  // Correctly adding course name to the list
            model.removeRow(selectedRows[i]);  // Remove the row from the model
        }

        if (!coursesToRemove.isEmpty()) {
            LessonManagement.removeCoursesForStudent(studentSelector.getSelectedItem().toString(), coursesToRemove);
            JOptionPane.showMessageDialog(this, "Removed courses: " + String.join(", ", coursesToRemove), "Courses Removed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No courses were removed.", "No Changes Made", JOptionPane.WARNING_MESSAGE);
        }

        updateCourseView();  // Refresh the course view to show current state after removal
        updateAvailableCourses();  // Optionally refresh the list of available courses
    }



    private void updateAvailableCourses() {
        // Ensures the list of available courses is always consistent
        DefaultTableModel model = (DefaultTableModel) availableCoursesTable.getModel();
        model.setRowCount(0); // Clears the table
        Map<String, String> availableCourses = Map.of(
                "Algorithms and Data Structures", "4",
                "Operating Systems", "3",
                "Networks", "3",
                "Database Systems", "3",
                "Software Engineering", "3",
                "Artificial Intelligence", "3",
                "Machine Learning", "3",
                "Computer Graphics", "3",
                "Human-Computer Interaction", "3"
        );
        availableCourses.forEach((course, credits) -> {
            model.addRow(new Object[]{false, course, credits});
        });
    }

}
