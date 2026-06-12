package whz.pti.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;


public class DBConnection {
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    private static DBConnection instance;
    private Connection connection;

    // Config parameters
    private String server;
    private String port;
    private String database;
    private String username;
    private String password;
    private boolean encrypt;
    private boolean trustServerCertificate;

    private DBConnection() {
        loadProperties();
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.warning("config.properties file not found. Using default values.");
                setDefaultProperties();
            } else {
                properties.load(input);
                this.server = properties.getProperty("db.server", "localhost");
                this.port = properties.getProperty("db.port", "1433");
                this.database = properties.getProperty("db.database", "SmarthomeDB");
                this.username = properties.getProperty("db.username", "sa");
                this.password = properties.getProperty("db.password", "Admin1234");
                this.encrypt = Boolean.parseBoolean(properties.getProperty("db.encrypt", "false"));
                this.trustServerCertificate = Boolean.parseBoolean(properties.getProperty("db.trustServerCertificate", "true"));
            }
        } catch (IOException e) {
            logger.warning("Error loading config.properties: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        this.server = "localhost";
        this.port = "1433";
        this.database = "SmarthomeDB";
        this.username = "sa";
        this.password = "Admin123@";
        this.encrypt = false;
        this.trustServerCertificate = true;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        return connection;
    }

    private Connection createConnection() throws SQLException {
        String connectionString = String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=%b;trustServerCertificate=%b",
                server, port, database, encrypt, trustServerCertificate
        );

        logger.info("Connecting to database: " + connectionString);
        return DriverManager.getConnection(connectionString, username, password);
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed.");
            } catch (SQLException e) {
                logger.warning("Error closing database connection: " + e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getServer() {
        return server;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }
}
