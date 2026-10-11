import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class ShoppingPage extends JFrame implements ActionListener {

    
    /**
	 * 
	 */
	
	private JPanel itemsPanel;
    private JButton btnViewCart, btnLogout, btnCheckout;
    HashMap<String, Integer> cart = new HashMap<>();

    public ShoppingPage() {
        setTitle("ShopEase - Shopping");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(220, 240, 255));
        setLayout(new BorderLayout(10, 10));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topPanel.setBackground(new Color(180, 210, 250));

        btnViewCart = new JButton("View Cart");
        btnCheckout = new JButton("Checkout");
        btnLogout = new JButton("Logout");

        btnViewCart.addActionListener(this);
        btnCheckout.addActionListener(this);
        btnLogout.addActionListener(this);

        topPanel.add(btnViewCart);
        topPanel.add(btnCheckout);
        topPanel.add(btnLogout);
        add(topPanel, BorderLayout.NORTH);

        // Items panel
        itemsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        itemsPanel.setBackground(new Color(220, 240, 255));

        // Fetch items from database
        try (java.sql.Connection conn = DBConnection.getConnection()) {
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM items");
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String itemName = rs.getString("name");
                int price = rs.getInt("price");
                addItemCard(itemName, price);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addItemCard(String name, int price) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(140, 140));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setMaximumSize(new Dimension(120, 25));

        JLabel lblPrice = new JLabel("₹" + price, SwingConstants.CENTER);
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrice.setMaximumSize(new Dimension(120, 20));

        JButton btnAdd = new JButton("Add to Cart");
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.setMaximumSize(new Dimension(120, 25));

        btnAdd.addActionListener(e -> {
            String[] quantities = {"1", "2", "3", "4", "5"};
            JComboBox<String> combo = new JComboBox<>(quantities);

            int result = JOptionPane.showConfirmDialog(
                    ShoppingPage.this,
                    combo,
                    "Select Quantity for " + name,
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                int qty = Integer.parseInt((String) combo.getSelectedItem());
                cart.put(name, cart.getOrDefault(name, 0) + qty);
                JOptionPane.showMessageDialog(
                        ShoppingPage.this,
                        qty + " × " + name + " added to cart!"
                );
            }
        });

        card.add(Box.createVerticalStrut(10));
        card.add(lblName);
        card.add(Box.createVerticalStrut(20));
        card.add(lblPrice);
        card.add(Box.createVerticalStrut(5));
        card.add(btnAdd);
        card.add(Box.createVerticalStrut(10));

        itemsPanel.add(card);
    }

    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnViewCart) {
            showCartDialog();
        } else if (e.getSource() == btnCheckout) {
            setVisible(false);
            new CheckoutPage(cart, this);
        } else if (e.getSource() == btnLogout) {
            dispose();
            new LoginSignupPage();
        }
    }

    private void showCartDialog() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        JDialog cartDialog = new JDialog(this, "Your Cart", true);
        cartDialog.setSize(400, 300);
        cartDialog.setLocationRelativeTo(this);
        cartDialog.setLayout(new BorderLayout());

        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String item : cart.keySet()) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

            JLabel lblItem = new JLabel(item + " × " + cart.get(item));
            lblItem.setFont(new Font("Arial", Font.PLAIN, 14));

            JButton btnRemove = new JButton("Remove");
            btnRemove.setFont(new Font("Arial", Font.PLAIN, 12));
            btnRemove.setPreferredSize(new Dimension(80, 25));
            btnRemove.setMaximumSize(new Dimension(80, 25));
            btnRemove.setMinimumSize(new Dimension(80, 25));
            btnRemove.addActionListener(ev -> {
                cart.remove(item);
                cartDialog.dispose();
                showCartDialog();
            });

            row.add(lblItem);
            row.add(Box.createHorizontalGlue());
            row.add(btnRemove);

            cartPanel.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(cartPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cartDialog.add(scrollPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(ev -> cartDialog.dispose());
        JPanel bottom = new JPanel();
        bottom.add(btnClose);
        cartDialog.add(bottom, BorderLayout.SOUTH);

        cartDialog.setVisible(true);
    }
}
