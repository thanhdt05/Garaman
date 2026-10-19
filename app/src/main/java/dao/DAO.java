package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class DAO {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfiguration();
    }

    private static synchronized void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream is = DAO.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // Log or fallback to default
        }

        // Ưu tiên biến môi trường, sau đó đến file db.properties, cuối cùng là giá trị mặc định
        String envUrl = System.getenv("GARAMAN_DB_URL");
        String envUser = System.getenv("GARAMAN_DB_USER");
        String envPass = System.getenv("GARAMAN_DB_PASSWORD");

        dbUrl = envUrl != null ? envUrl : props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/garaman4?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8&allowPublicKeyRetrieval=true");
        dbUser = envUser != null ? envUser : props.getProperty("db.user", "root");
        dbPassword = envPass != null ? envPass : props.getProperty("db.password", "root");

        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy MySQL JDBC Driver: " + DRIVER_CLASS, e);
        }
    }

    /**
     * Mở kết nối mới đến CSDL. Không lưu giữ static connection để đảm bảo an toàn đa luồng.
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Escape các ký tự đặc biệt trong câu truy vấn LIKE (%, _, \)
     * và trả về mẫu bao quanh bởi %...% cho tìm kiếm không phân biệt.
     */
    protected String taoMauTimKiem(String tuKhoa) {
        if (tuKhoa == null) {
            return "%";
        }
        String trimmed = tuKhoa.trim();
        if (trimmed.isEmpty()) {
            return "%";
        }
        String escaped = trimmed.replace("\\", "\\\\")
                                .replace("%", "\\%")
                                .replace("_", "\\_");
        return "%" + escaped + "%";
    }
}
