import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginSignupPage extends JFrame implements ActionListener {

    
    /**
	 * 
	 */
	
	private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnSignup;
    private JLabel lblTitle, lblUsername, lblPassword;

    // Individual button horizontal shifts
    private int btnLoginXShift = -40;   // positive moves right, negative moves left
    private int btnSignupXShift = 30;   // positive moves right, negative moves left

    public LoginSignupPage() {
        setTitle("ShopEase - Login / Signup");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Load background image
        ImageIcon bgIcon = new ImageIcon("background.jpg"); // adjust path
        Image bgImage = bgIcon.getImage();

        // Create background panel
        JPanel backgroundPanel = new JPanel() {
            
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Title
        lblTitle = new JLabel("Welcome to ShopEase", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        backgroundPanel.add(lblTitle);

        // Username label and text field
        lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.WHITE);
        backgroundPanel.add(lblUsername);

        txtUsername = new JTextField();
        backgroundPanel.add(txtUsername);

        // Password label and text field
        lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE);
        backgroundPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        backgroundPanel.add(txtPassword);

        // Login and Signup buttons
        btnLogin = new JButton("Login");
        btnLogin.setFocusable(false);
        btnLogin.addActionListener(this);
        backgroundPanel.add(btnLogin);

        btnSignup = new JButton("Sign Up");
        btnSignup.setFocusable(false);
        btnSignup.addActionListener(this);
        backgroundPanel.add(btnSignup);

        // Dynamically center all components
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                int componentWidth = 250;
                int labelWidth = 80;
                int fieldWidth = 180;
                int buttonWidth = 90;
                int buttonGap = 10;
                int verticalGap = 20;

                int totalHeight = 30 + verticalGap // title
                        + 25 + verticalGap // username
                        + 25 + verticalGap // password
                        + 30; // buttons

                int startY = (h - totalHeight) / 2;

                lblTitle.setBounds((w - componentWidth) / 2, startY, componentWidth, 30);

                int yUsername = startY + 30 + verticalGap;
                int startX = (w - (labelWidth + fieldWidth)) / 2;
                lblUsername.setBounds(startX, yUsername, labelWidth, 25);
                txtUsername.setBounds(startX + labelWidth, yUsername, fieldWidth, 25);

                int yPassword = yUsername + 25 + verticalGap;
                lblPassword.setBounds(startX, yPassword, labelWidth, 25);
                txtPassword.setBounds(startX + labelWidth, yPassword, fieldWidth, 25);

                int yButtons = yPassword + 25 + verticalGap;
                int totalButtonWidth = buttonWidth * 2 + buttonGap;
                int centerX = (w - totalButtonWidth) / 2;

                btnLogin.setBounds(centerX + btnLoginXShift, yButtons, buttonWidth, 30);
                btnSignup.setBounds(centerX + buttonWidth + buttonGap + btnSignupXShift, yButtons, buttonWidth, 30);
            }
        });

        setVisible(true);
        revalidate();
        repaint();
    }

    
    public void actionPerformed(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (e.getSource() == btnSignup) {
                // Check if username already exists
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=?");
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                            "This username already exists! Please choose another.",
                            "Signup Failed",
                            JOptionPane.WARNING_MESSAGE);
                    txtUsername.requestFocus();
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Create account for user: " + username + "?",
                        "Confirm Signup",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO users(username, password) VALUES(?, ?)"
                    );
                    insert.setString(1, username);
                    insert.setString(2, password);
                    insert.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                    txtUsername.setText("");
                    txtPassword.setText("");
                }

            } else if (e.getSource() == btnLogin) {
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=?"
                );
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Logged in successfully!");
                    dispose();
                    new ShoppingPage();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "<html><span style='color:red;'>Invalid username or password.</span></html>",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                    txtPassword.setText("");
                    txtUsername.requestFocus();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginSignupPage::new);
    }
}
