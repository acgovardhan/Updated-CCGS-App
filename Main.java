import interest.InterestFinderPage;
import college.CollegeFinder;
import career.CareerRecommendationApp;
import course.CoursePredictorApp;
import setting.Setting;
import db.DatabaseConnection;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static boolean signUp(String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Passwords do not match.");
            return false;
        }
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement("SELECT username FROM userdata WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Username already exists.");
                return false;
            }
    
            // Insert new user with default values
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO userdata (username, password, fullname, education, gender, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, username); // fullname default
            insertStmt.setString(4, "");       // education
            insertStmt.setString(5, "");       // gender left empty
            insertStmt.setNull(6, java.sql.Types.VARCHAR); // email
            insertStmt.setNull(7, java.sql.Types.VARCHAR); // phone
    
            System.out.println("Executing INSERT for signup...");
            insertStmt.executeUpdate();
            System.out.println("Sign-up INSERT executed successfully.");
            return true;
    
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during sign-up: " + ex.getMessage());
            return false;
        }
    }
    

    // Login checks the provided credentials against the database
    public static boolean login(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM userdata WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

class OnboardingPage extends JFrame {
    private static final int TIMEOUT = 5000;
    public OnboardingPage() {
        setTitle("Career Lead - Onboarding");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        getContentPane().setBackground(new Color(235, 237, 248));

        JLabel title = new JLabel("CAREER LEAD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(50, 40, 300, 40);
        add(title);

        JLabel subtitle = new JLabel("Explore your dream Career!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setBounds(50, 90, 300, 30);
        add(subtitle);

        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(JLabel.CENTER);

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/career.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(300, 125, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(scaledImage));
        add(logo, BorderLayout.CENTER);

        JLabel imagePlaceholder = new JLabel(new ImageIcon(getClass().getResource("/test.png")));
        imagePlaceholder.setBounds(0, 150, 400, 300);
        add(imagePlaceholder);

        setVisible(true);

        JButton loginButton = new JButton("Get Started");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setBackground(new Color(47, 71, 140));
        loginButton.setBounds(120, 450, 150, 40);
        loginButton.setForeground(Color.WHITE);
        add(loginButton);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 158, 207));
        headerPanel.setBounds(0, 0, 400, 40);
        add(headerPanel);

        JPanel headerP = new JPanel();
        headerP.setBackground(new Color(139, 158, 207));
        headerP.setBounds(0, 630, 400, 60);
        add(headerP);

        loginButton.setEnabled(false);

        // Run internet check in background
        new Thread(() -> {
            boolean hasInternet = isInternetAvailable();
            SwingUtilities.invokeLater(() -> {
                if (!hasInternet) {
                    JOptionPane.showMessageDialog(this,
                            "No internet connection. Please check your network.",
                            "Network Unavailable", JOptionPane.WARNING_MESSAGE);
                } else {
                    loginButton.setEnabled(true); // enable login when internet is back
                }
            });
        }).start();

        loginButton.addActionListener(e -> {
            new LoginSignupGUI().setVisible(true);
        });

        setVisible(true);
    }
    public static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.connect();
            return connection.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }
    
    
}

// Class for collecting additional user details after sign-up using MySQL
class DetailsWindow extends JFrame {
    private JTextField nameField;
    private JTextField educationField;
    private JRadioButton maleButton;
    private JRadioButton femaleButton;
    private JTextField emailField;
    private JTextField phoneField;

    public DetailsWindow(String username) {
        setTitle("Let Us Know About You");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 700);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headingLabel = new JLabel("Let Us Know A Bit About You...");
        headingLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        headingLabel.setForeground(new Color(0, 51, 153));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(headingLabel, gbc);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JLabel educationLabel = new JLabel("Education:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(educationLabel, gbc);

        educationField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(educationField, gbc);

        JLabel genderLabel = new JLabel("Gender:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(genderLabel, gbc);

        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        gbc.gridx = 1;
        panel.add(maleButton, gbc);
        gbc.gridx = 2;
        panel.add(femaleButton, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(emailLabel, gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        JLabel phoneLabel = new JLabel("Phone:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(phoneLabel, gbc);

        phoneField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        JButton submitButton = new JButton("Submit");
        styleButton(submitButton);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        add(panel);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDetails(username);
            }
        });
    }

    //Save the entered details into the userdata table
    private void saveDetails(String username) {
        String name = nameField.getText();
        String education = educationField.getText();
        String gender = new String();
        if(maleButton.isSelected())
        {
            gender="Male";
        }
        else if(femaleButton.isSelected())
        {
            gender = "Female";
        }
        else{
            gender =null;
        }

        String email = emailField.getText();
        String phone = phoneField.getText();
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE userdata SET fullname = ?, education = ?, gender = ?, email = ?, phone = ? WHERE username = ?"
            );
            updateStmt.setString(1, name);
            updateStmt.setString(2, education);
            updateStmt.setString(3, gender);
            updateStmt.setString(4, email);
            updateStmt.setString(5, phone);
            updateStmt.setString(6, username);
            
            //Attempt to update the record
            updateStmt.executeUpdate();
            
            //If update is successful, inform the user and move to the HomePage
            JOptionPane.showMessageDialog(this, "Details saved successfully!");
            new HomePage(username);
            dispose();
            
        } catch (SQLException ex) {
            // Log the error for debugging
            ex.printStackTrace();
            
            // Show an error message to the user.
            // The window remains open so the user can correct the input.
            JOptionPane.showMessageDialog(this, 
                "Error!! (Email or Phonenumber)" + ex.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 51, 153));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
}

// Main application GUI class for login and signup functionality using MySQL
class LoginSignupGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginSignupGUI() {
        setTitle("Career Lead - Onboarding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/newlogo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(400, 125, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
        add(logoLabel, BorderLayout.NORTH);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridy = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridy = 3;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        gbc.gridy = 4;
        panel.add(loginButton, gbc);

        JButton signUpButton = new JButton("Sign Up");
        styleButton(signUpButton);
        gbc.gridy = 5;
        panel.add(signUpButton, gbc);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (User.login(username, password)) {
                    Setting.currentUsername = username;
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    new HomePage(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = JOptionPane.showInputDialog("Confirm Password:");
                if (User.signUp(username, password, confirmPassword)) {
                    Setting.currentUsername = username;
                    JOptionPane.showMessageDialog(null, "Sign-up successful!");
                    new DetailsWindow(username).setVisible(true);
                }
            }
        });
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 51, 153));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
}

// HomePage with navigation buttons
class HomePage extends JFrame {
    public HomePage(String username) {
        setTitle("Career Lead - Home");
        setSize(400, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Logout with confirmation
        JButton logoutButton = new JButton("⏻");
        logoutButton.setPreferredSize(new Dimension(50, 40));
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                // if you have a reference to the login frame, call loginFrame.setVisible(true);
            }
        });
        topPanel.add(logoutButton, BorderLayout.WEST);

        // Greeting
        JLabel greetingLabel = new JLabel("Hey there, " + username, JLabel.CENTER);
        greetingLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        greetingLabel.setForeground(new Color(0, 51, 153));
        topPanel.add(greetingLabel, BorderLayout.CENTER);

        // Settings button
        JButton settingsButton = new JButton("☰");
        settingsButton.setPreferredSize(new Dimension(50, 40));
        settingsButton.setFocusPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorderPainted(false);
        settingsButton.addActionListener(e -> settingsPage());
        topPanel.add(settingsButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Main buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton interestFinderButton = createStyledButton("Go to Interest Finder");
        JButton careerButton       = createStyledButton("Get Career Advice");
        JButton courseButton1      = createStyledButton("Find My Course");
        JButton courseButton2      = createStyledButton("Search Colleges");

        buttonPanel.add(interestFinderButton);
        buttonPanel.add(careerButton);
        buttonPanel.add(courseButton1);
        buttonPanel.add(courseButton2);

        add(buttonPanel, BorderLayout.CENTER);

        interestFinderButton.addActionListener(e -> openInterestFinder());
        careerButton      .addActionListener(e -> Careerrec());
        courseButton1     .addActionListener(e -> Courserrec());
        courseButton2     .addActionListener(e -> openCollegeFinder());

        setVisible(true);
    }

    // Smaller, rounded main buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBackground(new Color(0, 51, 153));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(6, 12, 6, 12));
        return button;
    }

    private void openInterestFinder() {
        new InterestFinderPage().showAgeSelectionPage();
    }
    private void settingsPage() {
        Setting.showSettingsPage();
    }
    private void Careerrec(){
        new CareerRecommendationApp();
    }
    private void openCollegeFinder() {
        CollegeFinder.showCollege();
    }
    private void Courserrec(){
        new CoursePredictorApp();
    }
}


// Main class to launch the application
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.invokeLater(OnboardingPage::new);
        });
    }
}
