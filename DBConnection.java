import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/shopease?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "Asher@123#"; // put your password if any

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected successfully!");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
