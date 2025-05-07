package setting;

import db.DatabaseConnection;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class Setting {
    private static JFrame settingsPage;
    private static JFrame termsPage;
    private static JFrame aboutPage;
    private static JFrame ProfilePage;

    public static void main(String[] args) {
        showSettingsPage();
    }

    public static void showSettingsPage() {
        settingsPage = new JFrame("Career Lead - Settings");
        settingsPage.setSize(400, 700);
        settingsPage.setLocationRelativeTo(null);
        settingsPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        settingsPage.setLayout(null);
        settingsPage.getContentPane().setBackground(new Color(235, 237, 248));

        // Header with title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139,158,207));
        headerPanel.setBounds(0, 0, 400, 60);
        settingsPage.add(headerPanel);

        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Main buttons
        JButton profileButton = new JButton("My Profile");
        profileButton.setBounds(100, 150, 200, 40);
        settingsPage.add(profileButton);

        JButton privacyButton = new JButton("Privacy Policy");
        privacyButton.setBounds(100, 220, 200, 40);
        settingsPage.add(privacyButton);

        JButton aboutButton = new JButton("About");
        aboutButton.setBounds(100, 290, 200, 40);
        settingsPage.add(aboutButton);

        // Back Button
        JButton backButton = new JButton("Home");
        backButton.setBounds(100, 460, 200, 30);
        settingsPage.add(backButton);
        JPanel headerP = new JPanel();
        headerP.setBackground(new Color(139,158,207));
        headerP.setBounds(0, 630, 400, 60);
        settingsPage.add(headerP);

        // Action Listeners for navigation
        privacyButton.addActionListener(e -> {
            settingsPage.setVisible(false);
            showTermsPolicyPage();
        });
        aboutButton.addActionListener(e -> {
            settingsPage.setVisible(false);
            showAboutPage();
        });
        profileButton.addActionListener(e -> {
            settingsPage.setVisible(false);
            showProfilePage();
        });
        backButton.addActionListener(e -> {
            settingsPage.dispose();
        });

        settingsPage.setVisible(true);
    }

    private static void showTermsPolicyPage() {
        termsPage = new JFrame("Career Lead - Terms & Policy");
        termsPage.setSize(400, 700);
        termsPage.setLocationRelativeTo(null);
        termsPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        termsPage.setLayout(null);
        termsPage.getContentPane().setBackground(new Color(235, 237, 248));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139,158,207));
        headerPanel.setBounds(0, 0, 400, 60);
        termsPage.add(headerPanel);

        JLabel titleLabel = new JLabel("Terms & Policy", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Privacy Policy Section
        JLabel privacyLabel = new JLabel("Privacy Policy");
        privacyLabel.setBounds(50, 100, 300, 20);
        privacyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        termsPage.add(privacyLabel);

        JTextArea privacyText = new JTextArea("Your privacy is of utmost importance to us. This policy explains how we collect, use, and protect your personal information when using the College and Career Guidance System (CCGS).\n" + //
                "\n" + //
                "1. Information We Collect\n" + //
                "Personal Data: We collect details such as your name, contact information, educational history, skills, interests, and career aspirations to provide tailored recommendations.\n" + //
                "Academic Records: Information about your academic performance is collected to enhance our course and college recommendations.\n" + //
                "Cookies and Usage Data: Cookies are used to improve user experience, while usage data helps us enhance system functionality.\n" + //
                "2. Purpose of Collection\n" + //
                "Personalized Recommendations: We use your data to provide personalized course, college, and career recommendations based on your academic background and interests.\n" + //
                "User Experience Improvement: Collected data helps us improve and personalize the user experience within the platform.\n" + //
                "3. Data Security\n" + //
                "Your information is protected using industry-standard encryption and security protocols. Access to data is restricted to authorized personnel only.\n" + //
                "\n" + //
                "4. Data Sharing\n" + //
                "Your data will not be shared with third parties unless required by law or essential for service delivery.\n" + //
                "\n" + //
                "5. User Rights\n" + //
                "You have the right to:\n" + //
                "\n" + //
                "Modify or Delete Your Profile Information: You can modify or delete your profile information at any time.\n" + //
                "Opt Out of Notifications: You may opt out of receiving system notifications as per your preference.\n" + //
                "");
        privacyText.setLineWrap(true);
        privacyText.setWrapStyleWord(true);
        privacyText.setEditable(false);

        JScrollPane privacyScrollPane = new JScrollPane(privacyText);
        privacyScrollPane.setBounds(50, 130, 300, 100);
        termsPage.add(privacyScrollPane);

        // Terms & Conditions Section
        JLabel termsLabel = new JLabel("Terms & Conditions");
        termsLabel.setBounds(50, 250, 300, 20);
        termsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        termsPage.add(termsLabel);

        JTextArea termsText = new JTextArea("Terms and Conditions for College and Career Guidance System (CCGS)\n" + //
                "\n" + //
                "These terms govern your use of the College and Career Guidance System (CCGS). By accessing the platform, you agree to the following terms:\n" + //
                "\n" + //
                "1. User Responsibilities\n" + //
                "Accurate Information: You must provide accurate academic records and other personal information for effective recommendations.\n" + //
                "Account Security: Users are responsible for maintaining the confidentiality of their login details.\n" + //
                "2. Platform Use\n" + //
                "Educational and Career Recommendations: The platform provides personalized educational and career recommendations based on user inputs such as interests, academic history, and career goals.\n" + //
                "Informational Purpose: Recommendations are informational and do not guarantee admission or job placement.\n" + //
                "3. Account Termination\n" + //
                "CCGS reserves the right to terminate accounts that violate these terms or provide false information.\n" + //
                "\n" + //
                "4. Limitation of Liability\n" + //
                "CCGS is not responsible for decisions made based on its recommendations or any loss resulting from its use.\n" + //
                "\n" + //
                "5. Updates to Terms\n" + //
                "These terms may be updated, and continued use of the platform constitutes acceptance of the updated terms.\n" + //
                "\n" + //
                "");
        termsText.setLineWrap(true);
        termsText.setWrapStyleWord(true);
        termsText.setEditable(false);

        JScrollPane termsScrollPane = new JScrollPane(termsText);
        termsScrollPane.setBounds(50, 280, 300, 100);
        termsPage.add(termsScrollPane);

        // Back button
        JButton backButton = new JButton("Back to Settings");
        backButton.setBounds(100, 480, 200, 30);
        termsPage.add(backButton);

        JPanel headerP = new JPanel();
        headerP.setBackground(new Color(139,158,207));
        headerP.setBounds(0, 630, 400, 60);
        termsPage.add(headerP);

        backButton.addActionListener(e -> {
            termsPage.setVisible(false);
            showSettingsPage();
        });

        termsPage.setVisible(true);
    }

    private static void showAboutPage() {
        aboutPage = new JFrame("Career Lead - About");
        aboutPage.setSize(400, 700);
        aboutPage.setLocationRelativeTo(null);
        aboutPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        aboutPage.setLayout(null);
        aboutPage.getContentPane().setBackground(new Color(235, 237, 248));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139,158,207));
        headerPanel.setBounds(0, 0, 400, 60);
        aboutPage.add(headerPanel);

        JLabel titleLabel = new JLabel("About Us", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // About App Section
        JLabel aboutLabel = new JLabel("About the App");
        aboutLabel.setBounds(50, 100, 300, 20);
        aboutLabel.setFont(new Font("Arial", Font.BOLD, 16));
        aboutPage.add(aboutLabel);

        JTextArea aboutText = new JTextArea("Career Lead is a career recommendation app designed to guide users towards their dream careers by providing personalized recommendations based on their skills and interests.\n1. Creating Your Profile\n" + //
                "Profile Setup: Go to the Profile section and fill in your personal information, including academic background and career aspirations.\n" + //
                "2. Interest Finder\n" + //
                "Find Your Interests: Unsure about your interests? Use the Interest Finder to complete a questionnaire that analyzes your preferences and suggests potential fields of interest.\n" + //
                "3. Career Predictor\n" + //
                "Explore Careers: Based on your interests, the Career Predictor will suggest suitable career paths. You can explore each recommendation for further information on required skills and development courses.\n" + //
                "4. Course Finder\n" + //
                "Personalized Course Suggestions: Access personalized academic course suggestions in the Course Finder section. These are aligned with your strengths and interests to support your career ambitions.\n" + //
                "5. College Finder\n" + //
                "Discover Colleges: The College Finder helps you discover colleges that offer programs matching your preferences and goals.\n" + //
                "6. Contact Support\n" + //
                "Get Help: If you need further assistance, reach out to us at [support email] or call [support number].\n" + //
                "");
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setEditable(false);

        JScrollPane aboutScrollPane = new JScrollPane(aboutText);
        aboutScrollPane.setBounds(50, 130, 300, 200);
        aboutPage.add(aboutScrollPane);

        // Back button
        JButton backButton = new JButton("Back to Settings");
        backButton.setBounds(100, 480, 200, 30);
        aboutPage.add(backButton);

        JPanel headerP = new JPanel();
        headerP.setBackground(new Color(139,158,207));
        headerP.setBounds(0, 630, 400, 60);
        aboutPage.add(headerP);

        backButton.addActionListener(e -> {
            aboutPage.setVisible(false);
            showSettingsPage();
        });

        aboutPage.setVisible(true);
    }
    public static String currentUsername;

public static void showProfilePage() {
    if (currentUsername == null) {
        JOptionPane.showMessageDialog(null, "No user is currently logged in.");
        return;
    }

    // Fetch once to build the display
    UserProfile profile = fetchProfile(currentUsername);
    if (profile == null) return; // error already shown

    JFrame frame = new JFrame("User Profile");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(400, 700);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().setBackground(Color.WHITE);
    frame.setLayout(new BorderLayout(0, 10));

    // --- Top Panel ---
    JPanel topPanel = new JPanel();
    topPanel.setOpaque(false);
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    topPanel.add(Box.createVerticalStrut(15));

    // CORRECT way to load image from settings package
    ImageIcon icon = new ImageIcon(Setting.class.getResource("downloadpro.png"));  // No leading slash!
    JLabel picLabel = new JLabel(icon);
    picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    topPanel.add(picLabel);

    JLabel title = new JLabel("MY PROFILE");
    title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
    topPanel.add(title);
    frame.add(topPanel, BorderLayout.NORTH);

    // --- Center (details) ---
    JPanel detailsPanel = buildDetailsPanel(profile);
    frame.add(detailsPanel, BorderLayout.CENTER);

    // --- Bottom (buttons) ---
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    buttonsPanel.setOpaque(false);

    JButton editBtn = new JButton("Edit");
    editBtn.setPreferredSize(new Dimension(100, 32));
    // No more capturing local vars—just pass the frame
    editBtn.addActionListener(e -> openEditDialog(frame));
    buttonsPanel.add(editBtn);

    JButton deleteBtn = new JButton("Delete Account");
    deleteBtn.setPreferredSize(new Dimension(140, 32));
    deleteBtn.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to DELETE your account? This action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            deleteAccountAndExit();
        }
    });
    buttonsPanel.add(deleteBtn);

    frame.add(buttonsPanel, BorderLayout.SOUTH);
    frame.setVisible(true);
}

// Helper to fetch profile into a simple POJO
private static UserProfile fetchProfile(String username) {
    String q = "SELECT fullname, gender, email, education, phone FROM userdata WHERE username = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(q)) {
        ps.setString(1, username);
        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "No details found for user: " + username);
                return null;
            }
            return new UserProfile(
                rs.getString("fullname"),
                rs.getString("gender"),
                rs.getString("email"),
                rs.getString("education"),
                rs.getString("phone")
            );
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error retrieving profile details:\n" + ex.getMessage());
        return null;
    }
}

// Build the boxed details panel
private static JPanel buildDetailsPanel(UserProfile p) {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(new Color(240, 248, 255));
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(173, 216, 230), 2, true),
        BorderFactory.createEmptyBorder(20, 30, 20, 30)
    ));

    String[] labels = {"Full Name:", "Gender:", "Email:", "Education:", "Phone:"};
    String[] values = {p.fullname, p.gender, p.email, p.education, p.phone};

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.anchor = GridBagConstraints.WEST;

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0; gbc.gridy = i;
        JLabel lbl = new JLabel(labels[i]);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel val = new JLabel(values[i]);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setOpaque(true);
        val.setBackground(Color.WHITE);
        val.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        val.setPreferredSize(new Dimension(200, 24));
        panel.add(val, gbc);
    }
    return panel;
}

// Edit dialog now re-fetches the latest values
private static void openEditDialog(JFrame parent) {
    UserProfile p = fetchProfile(currentUsername);
    if (p == null) return;

    JDialog dialog = new JDialog(parent, "Edit Profile", true);
    dialog.setSize(400, 400);
    dialog.setLocationRelativeTo(parent);
    dialog.setLayout(new BorderLayout(0, 10));
    dialog.getContentPane().setBackground(Color.WHITE);

    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.anchor = GridBagConstraints.WEST;

    String[] labels = {"Full Name", "Gender", "Email", "Education", "Phone"};
    JTextField[] fields = new JTextField[] {
        new JTextField(p.fullname, 20),
        new JTextField(p.gender, 20),
        new JTextField(p.email, 20),
        new JTextField(p.education, 20),
        new JTextField(p.phone, 20)
    };

    for (int i = 0; i < labels.length; i++) {
        gbc.gridx = 0; gbc.gridy = i;
        form.add(new JLabel(labels[i] + ":"), gbc);
        gbc.gridx = 1;
        form.add(fields[i], gbc);
    }
    dialog.add(form, BorderLayout.CENTER);

    JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btns.setOpaque(false);
    JButton save = new JButton("Save");
    save.addActionListener(ev -> {
        String upd = "UPDATE userdata SET fullname=?, gender=?, email=?, education=?, phone=? WHERE username=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(upd)) {
            for (int i = 0; i < fields.length; i++) {
                ps.setString(i + 1, fields[i].getText().trim());
            }
            ps.setString(6, currentUsername);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully.");
            dialog.dispose();
            parent.dispose();
            showProfilePage();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error updating profile:\n" + ex.getMessage());
        }
    });
    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(ev -> dialog.dispose());
    btns.add(cancel);
    btns.add(save);
    dialog.add(btns, BorderLayout.SOUTH);

    dialog.setVisible(true);
}

// Delete and exit remains unchanged
private static void deleteAccountAndExit() {
    String del = "DELETE FROM userdata WHERE username = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(del)) {
        ps.setString(1, currentUsername);
        ps.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error deleting account:\n" + ex.getMessage());
        return;
    }
    JOptionPane.showMessageDialog(null, "Your account has been deleted. The application will now exit.");
    System.exit(0);
}

// Simple holder for profile data
private static class UserProfile {
    String fullname, gender, email, education, phone;
    UserProfile(String f, String g, String e, String ed, String p) {
        this.fullname = f;
        this.gender   = g;
        this.email    = e;
        this.education= ed;
        this.phone    = p;
    }
}


    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    private static JLabel createValue(String text) {
        JLabel value = new JLabel(text);
        value.setFont(new Font("Arial", Font.BOLD, 16));
        return value;
    }

}
