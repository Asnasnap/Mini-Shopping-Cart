import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class CheckoutPage extends JFrame implements ActionListener {
    
    HashMap<String, Integer> cart;
    private JButton btnBuy, btnLogout;
    private ShoppingPage shoppingPage;

    public CheckoutPage(HashMap<String, Integer> cart, ShoppingPage shoppingPage) {
        this.cart = cart;
        this.shoppingPage = shoppingPage;

        setTitle("ShopEase - Checkout");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int grandTotal = 0;
        for (String item : cart.keySet()) {
            int qty = cart.get(item);
            int price = getPrice(item);
            int subtotal = price * qty;
            grandTotal += subtotal;

            JLabel lbl = new JLabel(item + " × " + qty + " = ₹" + subtotal);
            itemsPanel.add(lbl);
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnBuy = new JButton("Buy");
        btnLogout = new JButton("Logout");

        btnBuy.addActionListener(this);
        btnLogout.addActionListener(this);

        bottom.add(new JLabel("Grand Total: ₹" + grandTotal));
        bottom.add(btnBuy);
        bottom.add(btnLogout);

        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private int getPrice(String item) {
        try (java.sql.Connection conn = DBConnection.getConnection()) {
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT price FROM items WHERE name=?");
            ps.setString(1, item);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("price");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

   
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBuy) {
            JOptionPane.showMessageDialog(this, "Thank you for your purchase!");
            cart.clear();
            dispose();
            shoppingPage.setVisible(true);
        } else if (e.getSource() == btnLogout) {
            dispose();
            new LoginSignupPage();
        }
    }
}
