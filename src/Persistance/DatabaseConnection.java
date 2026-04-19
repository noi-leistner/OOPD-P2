package Persistance;

import Business.Entities.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws Exception {
        ConfigDAO configDAO = new ConfigDAO();
        Config config = configDAO.getConfig();

        String url = "jdbc:mysql://" + config.getDb_host() + ":"
                + config.getDb_port() + "/"
                + config.getDb_name();

        return DriverManager.getConnection(
                url,
                config.getDb_username(),
                config.getDb_password()
        );
    }
}