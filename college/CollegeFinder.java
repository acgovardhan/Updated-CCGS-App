package college;

import db.DatabaseConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.sql.*;
import javax.swing.*;

public class CollegeFinder {

    public static JFrame collegePage;

    public static void main(String[] args) {
        // Directly show the college finder page; data will be loaded from DB.
        showCollege();
    }

    public static void showCollege() {
        collegePage = new JFrame("College Finder");
        collegePage.setSize(475, 700);
        collegePage.setLocationRelativeTo(null);
        collegePage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        collegePage.setLayout(null);

        JLabel label = new JLabel("Select a Course:");
        label.setBounds(50, 20, 150, 30);
        collegePage.add(label);

        // Predefined courses for the combo box (adjust as needed)
        String[] courses = {
                "Business Management", "Medicine", "Law", "Psychology",
                "Architecture", "Data Science", "Journalism and Mass Communication",
                "Environmental Science", "International Relations"
        };
        JComboBox<String> courseComboBox = new JComboBox<>(courses);
        courseComboBox.setBounds(180, 20, 160, 30);
        collegePage.add(courseComboBox);

        JButton findButton = new JButton("Find Colleges");
        findButton.setBounds(120, 70, 150, 30);
        collegePage.add(findButton);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setBounds(50, 120, 380, 500);
        collegePage.add(scrollPane);

        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCourse = (String) courseComboBox.getSelectedItem();
                resultPanel.removeAll();

                // Query the database for colleges offering the selected course.
                String query = "SELECT cd.collegeKey, cd.website " +
               "FROM collegedata cd " +
               "JOIN college_course cc ON cd.collegeKey = cc.collegeKey " +
               "WHERE cc.courseName = ?";

                try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, selectedCourse);
                    ResultSet rs = ps.executeQuery();
                    boolean found = false;
                    resultPanel.removeAll(); // clear previous results if needed

                    while (rs.next()) {
                        String collegeKey = rs.getString("collegeKey");
                        String website = rs.getString("website");
                        JPanel collegePanel = createCollegePanel(collegeKey, website);
                        resultPanel.add(collegePanel);
                        resultPanel.add(Box.createVerticalStrut(10));
                        found = true;
                    }

                    if (!found) {
                        resultPanel.add(new JLabel("No colleges found for the selected course."));
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(collegePage, "Error retrieving college data: " + ex.getMessage());
                }

                resultPanel.revalidate();
                resultPanel.repaint();

            }
        });

        collegePage.setVisible(true);
    }

    public static JPanel createCollegePanel(String collegeName, String website) {
        JPanel collegePanel = new JPanel();
        collegePanel.setLayout(new BorderLayout());
        collegePanel.setBackground(new Color(204, 204, 255)); // Periwinkle background
        collegePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        collegePanel.setPreferredSize(new Dimension(360, 50));

        JLabel collegeLabel = new JLabel(collegeName);
        collegeLabel.setForeground(Color.BLACK);
        collegeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        collegeLabel.setOpaque(true);
        collegeLabel.setBackground(new Color(204, 204, 255));

        // When the label is clicked, open the college's website.
        collegeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openCollegeWebsite(website, collegeName);
            }
        });

        collegePanel.add(collegeLabel, BorderLayout.CENTER);
        return collegePanel;
    }

    public static void openCollegeWebsite(String website, String collegeName) {
        try {
            if (website != null && !website.isEmpty()) {
                website = website.trim(); // Remove any extra spaces/newlines
                System.out.println("Attempting to open URL: " + website);
                Desktop.getDesktop().browse(new URI(website));
            } else {
                JOptionPane.showMessageDialog(collegePage, "No website available for " + collegeName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
