import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GradesAssignmentDialog extends JDialog {
    private JTextArea gradesArea;
    private JButton saveGradesButton;

    public GradesAssignmentDialog(Frame owner, String studentName) {
        super(owner, "Assign Grades", true);
        setSize(300, 200);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gradesArea = new JTextArea(5, 20);
        gradesArea.setBorder(BorderFactory.createTitledBorder("Grades (One per line)"));
        add(new JScrollPane(gradesArea), gbc);

        saveGradesButton = new JButton("Save Grades");
        saveGradesButton.addActionListener(e -> {
            String[] grades = gradesArea.getText().split("\\n");
            for (String grade : grades) {
                String[] parts = grade.split(":");
                if (parts.length == 3) {
                    GradeBook.setGradeForStudent(studentName, parts[0], parts[1], parts[2]);
                }
            }
            JOptionPane.showMessageDialog(this, "Grades saved for " + studentName);
            dispose();
        });
        add(saveGradesButton, gbc);
    }
}
