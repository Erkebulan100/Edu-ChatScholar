import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Map;

public class GradesWindow extends JFrame {
    private JTable gradesTable;

    public GradesWindow(String studentName) {
        setTitle("Your Grades");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initializeUI(studentName);
        setResizable(true);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Add a component listener to dynamically adjust font sizes and row height
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentFonts();
            }
        });
    }

    private void initializeUI(String studentName) {
        String[] columnNames = {"Course Name", "Credits", "Grade", "Grade Letter"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make table cells non-editable
            }
        };
        gradesTable = new JTable(model);
        gradesTable.setFillsViewportHeight(true);

        List<String[]> coursesWithCredits = LessonManagement.getCoursesWithCreditsForStudent(studentName);
        Map<String, String[]> gradesForStudent = GradeBook.getGradesForStudent(studentName);

        for (String[] courseWithCredit : coursesWithCredits) {
            String courseName = courseWithCredit[0];
            String credits = courseWithCredit[1];
            String[] gradeDetails = gradesForStudent.getOrDefault(courseName, new String[]{"", ""});
            model.addRow(new Object[]{courseName, credits, gradeDetails[0], gradeDetails[1]});
        }

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void adjustComponentFonts() {
        Font tableFont = new Font("Arial", Font.PLAIN, Math.max(16, getWidth() / 50));
        Font headerFont = new Font("Arial", Font.BOLD, Math.max(16, getWidth() / 50));
        gradesTable.setFont(tableFont);
        gradesTable.getTableHeader().setFont(headerFont);
        gradesTable.setRowHeight(Math.max(20, getHeight() / 30));
    }

    public void display() {
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
