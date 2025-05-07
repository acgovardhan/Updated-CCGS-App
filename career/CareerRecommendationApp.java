package career;

import db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Desktop;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.List;

public class CareerRecommendationApp {

    public JFrame frame;
    public JFrame resultFrame;
    public JPanel interestPanel;
    public JButton predictButton;

    // Mapping loaded from DB: Interest -> (Career -> Point)
    public Map<String, Map<String, Integer>> interestToCareerPointsMap;

    public CareerRecommendationApp() {
        // Load mapping from the DB
        interestToCareerPointsMap = loadInterestCareerMapping();

        // Set up main frame
        frame = new JFrame("Interest-Based Career Predictor");
        frame.setSize(400, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(178, 215, 226));

        JLabel title = new JLabel("Select Your Interests", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        interestPanel = new JPanel();
        interestPanel.setLayout(new GridLayout(interestToCareerPointsMap.size(), 1));
        interestPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        List<JCheckBox> interestCheckBoxes = new ArrayList<>();

        // Create checkboxes for each interest from DB
        for (String interest : interestToCareerPointsMap.keySet()) {
            JCheckBox checkBox = new JCheckBox(interest);
            checkBox.setBackground(Color.WHITE);
            interestCheckBoxes.add(checkBox);
            interestPanel.add(checkBox);
        }
        frame.add(interestPanel, BorderLayout.CENTER);

        predictButton = new JButton("Predict Careers");
        predictButton.setFont(new Font("Arial", Font.PLAIN, 16));
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

    // Loads the interest->career mapping from interest_career_mapping table.
    private Map<String, Map<String, Integer>> loadInterestCareerMapping() {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        String query = "SELECT Interests, Career, Point FROM interest_career_mapping";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String interest = rs.getString("Interests");
                String career = rs.getString("Career");
                int point = rs.getInt("Point");
                map.computeIfAbsent(interest, k -> new HashMap<>()).put(career, point);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void showResultPage(List<String> selectedInterests) {
        resultFrame = new JFrame("Career Recommendations");
        resultFrame.setSize(400, 700);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.setLayout(new BorderLayout());
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.getContentPane().setBackground(Color.WHITE);

        JLabel resultLabel = new JLabel("Recommended Careers", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultFrame.add(resultLabel, BorderLayout.NORTH);

        JPanel careersPanel = new JPanel();
        careersPanel.setLayout(new BoxLayout(careersPanel, BoxLayout.Y_AXIS));
        careersPanel.setBackground(Color.WHITE);
        careersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (selectedInterests.isEmpty()) {
            JLabel noSelectionLabel = new JLabel("Please select at least one interest!", SwingConstants.CENTER);
            noSelectionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            careersPanel.add(noSelectionLabel);
        } else {
            Map<String, Integer> careerPointsMap = new HashMap<>();
            // Sum points for each career across all selected interests
            for (String interest : selectedInterests) {
                Map<String, Integer> careers = interestToCareerPointsMap.get(interest);
                if (careers != null) {
                    for (Map.Entry<String, Integer> entry : careers.entrySet()) {
                        String career = entry.getKey();
                        int points = entry.getValue();
                        careerPointsMap.put(career, careerPointsMap.getOrDefault(career, 0) + points);
                    }
                }
            }

            careerPointsMap = sortByValueDescending(careerPointsMap);

            for (Map.Entry<String, Integer> entry : careerPointsMap.entrySet()) {
                String career = entry.getKey();
                int points = entry.getValue();
                JPanel careerPanel = new JPanel();
                careerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                careerPanel.setBackground(new Color(230, 240, 255));
                careerPanel.setMaximumSize(new Dimension(350, 80));
                careerPanel.setLayout(new BorderLayout());

                JLabel careerLabel = new JLabel(career + " (" + points + " points)");
                careerLabel.setFont(new Font("Arial", Font.BOLD, 14));
                careerPanel.add(careerLabel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

                // "Show Description" will retrieve info from career_desc table.
                JButton skillsButton = new JButton("Show Description");
                skillsButton.addActionListener(e -> showSkillsPage(career));

                // "Show Courses" retrieves courses from career_course.
                JButton coursesButton = new JButton("Show Courses");
                coursesButton.addActionListener(e -> showCoursesPage(career));

                // "More Info" gets a course link from course_link table.
                JButton linkButton = new JButton("More Info");
                linkButton.addActionListener(e -> openWebPage(career));

                buttonPanel.add(skillsButton);
                buttonPanel.add(coursesButton);
                buttonPanel.add(linkButton);
                careerPanel.add(buttonPanel, BorderLayout.SOUTH);
                careersPanel.add(careerPanel);
                careersPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(careersPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultFrame.add(scrollPane, BorderLayout.CENTER);
        resultFrame.setVisible(true);
    }

    public Map<String, Integer> sortByValueDescending(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    // Shows the career description from the career_desc table
    public void showSkillsPage(String career) {
        String description = "";
        String query = "SELECT description FROM career_desc WHERE career = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, career);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                description = rs.getString("description");
            } else {
                description = "No description available for " + career;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            description = "Error retrieving description for " + career;
        }

        JFrame skillsFrame = new JFrame("Description for " + career);
        skillsFrame.setSize(400, 700);
        skillsFrame.setLocationRelativeTo(null);
        skillsFrame.setLayout(new BorderLayout());
        skillsFrame.getContentPane().setBackground(Color.WHITE);

        JLabel skillsLabel = new JLabel("Description", SwingConstants.CENTER);
        skillsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        skillsFrame.add(skillsLabel, BorderLayout.NORTH);

        JTextArea skillsArea = new JTextArea();
        skillsArea.setEditable(false);
        skillsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        skillsArea.setWrapStyleWord(true);
        skillsArea.setLineWrap(true);
        skillsArea.setText(description);

        JScrollPane scrollPane = new JScrollPane(skillsArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        skillsFrame.add(scrollPane, BorderLayout.CENTER);

        skillsFrame.setVisible(true);
    }

    // Shows courses for the given career from career_course table.
    public void showCoursesPage(String career) {
        List<String> courses = new ArrayList<>();
        String query = "SELECT course FROM career_course WHERE career = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, career);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courses.add(rs.getString("course"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JFrame coursesFrame = new JFrame("Courses for " + career);
        coursesFrame.setSize(400, 700);
        coursesFrame.setLocationRelativeTo(null);
        coursesFrame.setLayout(new BorderLayout());
        coursesFrame.getContentPane().setBackground(new Color(255, 255, 255));

        JLabel coursesLabel = new JLabel("Recommended Courses", SwingConstants.CENTER);
        coursesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        coursesFrame.add(coursesLabel, BorderLayout.NORTH);

        JTextArea coursesArea = new JTextArea();
        coursesArea.setEditable(false);
        coursesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        coursesArea.setWrapStyleWord(true);
        coursesArea.setLineWrap(true);

        StringBuilder coursesText = new StringBuilder();
        if (courses.isEmpty()) {
            coursesText.append("No courses data available for ").append(career);
        } else {
            for (String course : courses) {
                coursesText.append("- ").append(course).append("\n");
            }
        }
        coursesArea.setText(coursesText.toString());

        JScrollPane scrollPane = new JScrollPane(coursesArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        coursesFrame.add(scrollPane, BorderLayout.CENTER);

        coursesFrame.setVisible(true);
    }

    // Opens a web page by retrieving a course's link.
    public void openWebPage(String career) {
        // Get one course for the career from career_course table
        String course = null;
        String query = "SELECT course FROM career_course WHERE career = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, career);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                course = rs.getString("course");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (course != null) {
            // Now get the link from course_link table for this course
            String url = null;
            query = "SELECT Link FROM course_link WHERE Course = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, course);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    url = rs.getString("Link");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (url != null) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No link available for " + career, "Link Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No course found for " + career, "Course Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CareerRecommendationApp::new);
    }
}
