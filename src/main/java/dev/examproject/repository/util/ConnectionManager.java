package dev.examproject.repository.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class ConnectionManager {

    private static final TurboLogger log = new TurboLogger(ConnectionManager.class);

    private static String URL;
    private static String UID;
    private static String PWD;
    private static Connection conn;


    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        URL = url;
    }

    @Value("${spring.datasource.username}")
    public void setUid(String uid) {
        UID = uid;
    }

    @Value("${spring.datasource.password}")
    public void setPwd(String pwd) {
        PWD = pwd;
    }

    private ConnectionManager() {}

    public static Connection getConnection() {
        if (conn != null) return conn;

        try {
            conn = DriverManager.getConnection(URL, UID, PWD);
        } catch (SQLException e) {
            log.error("Failed to connect to the database: " + e.getMessage());
        }
        return conn;
    }
}
