package interest;

import db.DatabaseConnection;  // Ensure DatabaseConnection is in package "db"
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class InterestFinderPage {
    public static JFrame frame;
    public static int currentQuestionIndex = 0;
    public static String selectedAgeGroup;
    // This list will hold the questions loaded from the DB for the chosen age group
    public static List<InterestQuestion> currentQuestions = new ArrayList<>();
    // Map to store category scores dynamically
    public static Map<String, Integer> categoryScores = new HashMap<>();

    // Inner class to hold question details from the database
    public static class InterestQuestion {
        public int sl;
        public String question;
        public String category;
        public int agegroup;

        public InterestQuestion(int sl, String question, String category, int agegroup) {
            this.sl = sl;
            this.question = question;
            this.category = category;
            this.agegroup = agegroup;
        }
    }

    public static void main(String[] args) {
        showAgeSelectionPage();
    }

    public static void showAgeSelectionPage() {
        frame = new JFrame("Interest Finder");
        frame.setSize(400, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel titleLabel = new JLabel("Interest Finder", SwingConstants.CENTER);
        titleLabel.setBounds(100, 50, 200, 30);
        frame.add(titleLabel);

        // Updated action listeners based on your DB agegroup values:
        JButton btnBelow10 = new JButton("Students below 10");
        btnBelow10.setBounds(100, 130, 200, 40);
        btnBelow10.addActionListener(e -> {
            selectedAgeGroup = "Below 10";
            currentQuestionIndex = 0; // Reset the question index
            // Load questions where agegroup = 10
            currentQuestions = loadQuestionsFromDB(10);
            categoryScores.clear();
            showQuestionPage();
        });
        frame.add(btnBelow10);

        JButton btn10to15 = new JButton("Students between 10 and 15");
        btn10to15.setBounds(100, 200, 200, 40);
        btn10to15.addActionListener(e -> {
            selectedAgeGroup = "Between 10 and 15";
            currentQuestionIndex = 0; // Reset the question index
            // Load questions where agegroup = 15
            currentQuestions = loadQuestionsFromDB(15);
            categoryScores.clear();
            showQuestionPage();
        });
        frame.add(btn10to15);

        JButton btnAbove15 = new JButton("Students above 15");
        btnAbove15.setBounds(100, 270, 200, 40);
        btnAbove15.addActionListener(e -> {
            selectedAgeGroup = "Above 15";
            currentQuestionIndex = 0; // Reset the question index
            // Load questions where agegroup = 20
            currentQuestions = loadQuestionsFromDB(20);
            categoryScores.clear();
            showQuestionPage();
        });
        frame.add(btnAbove15);

        frame.setVisible(true);
    }

    // Loads questions from the DB for the given age group value
    public static List<InterestQuestion> loadQuestionsFromDB(int ageGroup) {
        List<InterestQuestion> questions = new ArrayList<>();
        String query = "SELECT sl, question, category, agegroup FROM interestquestions WHERE agegroup = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, ageGroup);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int sl = rs.getInt("sl");
                String q = rs.getString("question");
                String cat = rs.getString("category");
                int ag = rs.getInt("agegroup");
                questions.add(new InterestQuestion(sl, q, cat, ag));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading questions: " + ex.getMessage());
        }
        return questions;
    }

    // Displays the current question and answer buttons
    public static void showQuestionPage() {
        frame.getContentPane().removeAll();
        frame.repaint();

        if (currentQuestions.isEmpty()) {
            JLabel noQuestionsLabel = new JLabel("No questions available for this age group.", SwingConstants.CENTER);
            noQuestionsLabel.setBounds(50, 100, 300, 30);
            frame.add(noQuestionsLabel);
            frame.setVisible(true);
            return;
        }

        InterestQuestion currentQuestion = currentQuestions.get(currentQuestionIndex);

        JLabel questionLabel = new JLabel(currentQuestion.question);
        questionLabel.setBounds(60, 80, 300, 30);
        frame.add(questionLabel);

        String[] answers = {"Strongly Like", "Like", "Neutral", "Dislike"};
        int yOffset = 150;
        for (String answer : answers) {
            JButton answerButton = new JButton(answer);
            answerButton.setBounds(50, yOffset, 300, 30);
            frame.add(answerButton);

            answerButton.addActionListener(e -> {
                addScore(currentQuestion.category, answer);
                if (currentQuestionIndex < currentQuestions.size() - 1) {
                    currentQuestionIndex++;
                    showQuestionPage();
                } else {
                    showResultsPage();
                }
            });
            yOffset += 50;
        }

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setBounds(120, yOffset + 150, 150, 30);
        backButton.addActionListener(e -> showAgeSelectionPage());
        frame.add(backButton);

        frame.setVisible(true);
    }

    // Adds score based on the answer given to a question's category
    public static void addScore(String category, String answer) {
        int points = switch (answer) {
            case "Strongly Like" -> 3;
            case "Like" -> 2;
            case "Neutral" -> 1;
            default -> 0;
        };
        categoryScores.put(category, categoryScores.getOrDefault(category, 0) + points);
    }

    // Shows the results page with top interests based on category scores
    public static void showResultsPage() {
        frame.getContentPane().removeAll();
        frame.repaint();

        JLabel resultLabel = new JLabel("Your Top Interests");
        resultLabel.setBounds(150, 50, 200, 30);
        frame.add(resultLabel);

        List<String> topInterests = getTopInterests();
        int yOffset = 100;
        for (String interest : topInterests) {
            JButton interestButton = new JButton(interest);
            interestButton.setBounds(50, yOffset, 300, 30);
            interestButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, "Interest: " + interest);
            });
            frame.add(interestButton);
            yOffset += 45;
        }

        JLabel thankYouLabel = new JLabel("Thank you for using Interest Finder!");
        thankYouLabel.setBounds(50, yOffset + 20, 300, 40);
        frame.add(thankYouLabel);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setBounds(120, yOffset + 180, 150, 30);
        backButton.addActionListener(e -> showAgeSelectionPage());
        frame.add(backButton);

        frame.setVisible(true);
    }

    // Returns the top 5 categories based on scores
    public static List<String> getTopInterests() {
        return categoryScores.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }
}
