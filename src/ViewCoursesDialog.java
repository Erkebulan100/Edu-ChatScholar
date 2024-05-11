import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class ViewCoursesDialog extends JDialog {
    private JTable coursesTable;

    public ViewCoursesDialog(Frame owner, String studentName) {
        super(owner, "Currently Enrolled Courses", true);
        setSize(Math.max(800, Toolkit.getDefaultToolkit().getScreenSize().width * 3 / 4),
                Math.max(600, Toolkit.getDefaultToolkit().getScreenSize().height * 3 / 4));
        setLayout(new BorderLayout());
        setLocationRelativeTo(owner);
        setModal(true);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columnNames = {"Course Name", "Credits"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames);
        coursesTable = new JTable(model);
        coursesTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        add(scrollPane, BorderLayout.CENTER);

        populateCourses(studentName);
    }

    private void populateCourses(String studentName) {
        DefaultTableModel model = (DefaultTableModel) coursesTable.getModel();
        model.setRowCount(0);
        Map<String, String> coursesForStudent = LessonManagement.getCoursesForStudent(studentName);
        coursesForStudent.forEach((course, credit) -> model.addRow(new Object[]{course, credit}));
    }
}
