package course;

import db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.List;

class RoundedButton extends JButton {
    int radius;

    public RoundedButton(String label, int radius) {
        super(label);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 40);
    }
}

public class CoursePredictorApp {

    private JFrame frame;
    private JFrame resultFrame;
    private JPanel interestPanel;
    private RoundedButton predictButton;

    // Maps loaded from the database
    private Map<String, Map<String, Integer>> interestToCoursePointsMap;
    private Map<String, String> courseToLinkMap;

    public CoursePredictorApp() {
        // Load mappings from the DB
        interestToCoursePointsMap = loadInterestCourseMappingFromDB();
        courseToLinkMap = loadCourseLinksFromDB();

        // Set up main frame
        frame = new JFrame("Interest-Based Course Predictor");
        frame.setSize(400, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(139, 158, 207));

        JLabel title = new JLabel("Select Your Interests", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(title, BorderLayout.NORTH);

        // Panel for interest checkboxes
        interestPanel = new JPanel();
        interestPanel.setLayout(new GridLayout(interestToCoursePointsMap.size(), 1, 5, 5));
        interestPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        interestPanel.setBackground(new Color(228, 240, 255));
        List<JCheckBox> interestCheckBoxes = new ArrayList<>();

        // Create a checkbox for each interest from the DB mapping
        for (String interest : interestToCoursePointsMap.keySet()) {
            JCheckBox checkBox = new JCheckBox(interest);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 14));
            interestCheckBoxes.add(checkBox);
            interestPanel.add(checkBox);
        }
        frame.add(interestPanel, BorderLayout.CENTER);

        // Predict button
        predictButton = new RoundedButton("Predict Courses", 30);
        predictButton.setFont(new Font("Arial", Font.BOLD, 16));
        predictButton.setBackground(new Color(139, 158, 207));
        predictButton.setFocusPainted(false);
        predictButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedInterests = new ArrayList<>();
                for (JCheckBox checkBox : interestCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedInterests.add(checkBox.getText());
                    }
                }
                showResultPage(selectedInterests);
            }
        });
        frame.add(predictButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // Load interest-course mapping from the interest_course_mapping table
    private Map<String, Map<String, Integer>> loadInterestCourseMappingFromDB() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        String query = "SELECT Interests, Course, Point FROM interest_course_mapping";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String interest = rs.getString("Interests");
                String course = rs.getString("Course");
                int point = rs.getInt("Point");
                map.computeIfAbsent(interest, k -> new HashMap<>()).put(course, point);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    // Load course links from the course_link table
    private Map<String, String> loadCourseLinksFromDB() {
        Map<String, String> map = new HashMap<>();
        String query = "SELECT Course, Link FROM course_link";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String course = rs.getString("Course");
                String link = rs.getString("Link");
                map.put(course, link);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    // Display the recommended courses in a new JFrame
    private void showResultPage(List<String> selectedInterests) {
        resultFrame = new JFrame("Course Recommendations");
        resultFrame.setSize(400, 700);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.setLayout(new BorderLayout());
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel resultLabel = new JLabel("Recommended Courses", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultFrame.add(resultLabel, BorderLayout.NORTH);

        JPanel headerP = new JPanel();
        headerP.setBackground(new Color(139, 158, 207));
        headerP.setBounds(0, 610, 390, 80);
        RoundedButton backButton = new RoundedButton("Go Back", 40);
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setBackground(Color.decode("#e9f4f5"));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createLineBorder(Color.decode("#1e90ff"), 1));
        headerP.add(backButton);
        resultFrame.add(headerP);
        backButton.addActionListener(e -> {
            frame.setVisible(true);
            resultFrame.dispose();
        });

        JPanel coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(Color.decode("#e9f4f5"));

        // Calculate course scores based on selected interests
        Map<String, Integer> courseScores = new HashMap<>();
        for (String interest : selectedInterests) {
            Map<String, Integer> courses = interestToCoursePointsMap.get(interest);
            if (courses != null) {
                for (Map.Entry<String, Integer> entry : courses.entrySet()) {
                    String course = entry.getKey();
                    int points = entry.getValue();
                    courseScores.put(course, courseScores.getOrDefault(course, 0) + points);
                }
            }
        }

        List<Map.Entry<String, Integer>> sortedCourses = new ArrayList<>(courseScores.entrySet());
        sortedCourses.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        if (selectedInterests.isEmpty()) {
            JLabel noSelectionLabel = new JLabel("Please select at least one interest!", SwingConstants.CENTER);
            noSelectionLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
            noSelectionLabel.setForeground(Color.RED);
            coursesPanel.add(Box.createVerticalStrut(200));
            coursesPanel.add(noSelectionLabel);
        } else {
            for (Map.Entry<String, Integer> entry : sortedCourses) {
                String course = entry.getKey();
                int points = entry.getValue();
                JPanel coursePanel = new JPanel();
                coursePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                coursePanel.setBackground(new Color(230, 240, 255));
                coursePanel.setMaximumSize(new Dimension(350, 40));
                coursePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel courseLabel = new JLabel("<html><a href=''>" + course + " -> (" + points + "/10)</a></html>");
                courseLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                courseLabel.setForeground(Color.BLACK);
                courseLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                String courseLink = courseToLinkMap.get(course);
                if (courseLink != null) {
                    courseLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                // Trim the link to remove unwanted spaces
                                Desktop.getDesktop().browse(new URI(courseLink.trim()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }

                coursePanel.add(courseLabel);
                coursesPanel.add(Box.createVerticalStrut(10));
                coursesPanel.add(coursePanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultFrame.add(scrollPane, BorderLayout.CENTER);

        resultFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CoursePredictorApp::new);
    }
}
