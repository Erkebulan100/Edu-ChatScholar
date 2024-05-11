import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseSelectionDialog extends JDialog {
    private JTable coursesTable;
    private JButton saveButton;
    private boolean isDelete;
    private String studentName;  // Added as a class field

    public CourseSelectionDialog(Frame owner, String studentName, boolean isDelete) {
        super(owner, isDelete ? "View and Delete Courses" : "Select Courses", true);
        this.studentName = studentName;
        this.isDelete = isDelete;
        initializeDialog();
        setupCoursesTable();
        addButtons();
        setupResizeBehavior();
    }

    private void initializeDialog() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setSize(Math.max(800, screenSize.width * 3 / 4), Math.max(600, screenSize.height * 3 / 4));
        setLayout(new BorderLayout());
        setLocationRelativeTo(getOwner());
        setModal(true);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupCoursesTable() {
        String[] columnNames = {"Select", "Course Name", "Credits"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }
        };

        coursesTable = new JTable(model);
        coursesTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        add(scrollPane, BorderLayout.CENTER);

        populateCourses();  // Refresh and populate courses each time the dialog is opened
    }

    private void populateCourses() {
        DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
        model.setRowCount(0); // Clear existing rows

        if (!isDelete) {
            // New set of Computer Science courses for selection
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
            availableCourses.forEach((course, credit) -> {
                model.addRow(new Object[]{false, course, credit}); // Always false to ensure no pre-ticked boxes
            });
        } else {
            // For deletion, show currently enrolled courses from these specific CS courses
            Map<String, String> coursesForStudent = LessonManagement.getCoursesForStudent(studentName);
            coursesForStudent.forEach((course, credit) -> {
                model.addRow(new Object[]{false, course, credit});
            });
        }
    }


    private void addButtons() {
        saveButton = new JButton(isDelete ? "Remove Selected Courses" : "Add Selected Courses");
        saveButton.addActionListener(e -> {
            if (isDelete) {
                removeSelectedCourses();
            } else {
                addSelectedCourses();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSelectedCourses() {
        DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected) {
                String courseName = (String) model.getValueAt(i, 1);
                String credits = (String) model.getValueAt(i, 2);
                LessonManagement.addCourseForStudent(studentName, courseName, credits);
            }
        }
        dispose();  // Close the dialog after adding selected courses
    }

    private void removeSelectedCourses() {
        DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
        List<String> coursesToRemove = new ArrayList<>();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected) {
                String courseName = (String) model.getValueAt(i, 1);
                coursesToRemove.add(courseName);
                model.removeRow(i);  // Remove the row from the model
            }
        }
        if (!coursesToRemove.isEmpty()) {
            LessonManagement.removeCoursesForStudent(studentName, coursesToRemove);  // Persistently remove courses
        }
        showMessageDialog("Selected courses have been removed.");
    }

    private void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Course Update Status", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupResizeBehavior() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Font font = new Font("Arial", Font.PLAIN, Math.max(16, getWidth() / 50));
                saveButton.setFont(font);
                coursesTable.setFont(font);
                coursesTable.setRowHeight(Math.max(20, getHeight() / 30));
            }
        });
    }
}
