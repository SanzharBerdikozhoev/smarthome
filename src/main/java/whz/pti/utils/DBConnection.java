package whz.pti.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
    private static DBConnection instance;
    private final HikariDataSource dataSource;

    private String server;
    private String port;
    private String database;
    private String username;
    private String password;
    private boolean encrypt;
    private boolean trustServerCertificate;

    private DBConnection() {
        loadProperties();
        this.dataSource = createDataSource();
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
        this.password = "Admin1234";
        this.encrypt = false;
        this.trustServerCertificate = true;
    }

    private HikariDataSource createDataSource() {
        String connectionString = String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=%b;trustServerCertificate=%b",
                server, port, database, encrypt, trustServerCertificate
        );

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionString);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        logger.info("Initializing connection pool: " + connectionString);
        return new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Connection pool closed.");
        }
    }

    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    public String getServer() { return server; }
    public String getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
}