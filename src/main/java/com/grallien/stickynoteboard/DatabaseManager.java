package com.grallien.stickynoteboard;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the SQLite database and connection.
 * <p>This class handles database connectivity. It establishes JDBC connections and ensures the required database
 * schema exists before the application attempts to perform CRUD operations.</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class DatabaseManager{
    /** The JDBC connection URL for the local SQLite database file. */
    private static final String FILE_URL = "jdbc:sqlite:sticky_notes.sqlite";
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    /**
     * Obtains a connection to the SQLite database using the FILE_URL class variable.
     *
     * @return a connection to the database
     * @throws SQLException Fails to connect
     */
    public static Connection connect() throws SQLException{
        return DriverManager.getConnection(FILE_URL);
    }

    /**
     * Starts the database connection by pushing a statement to create the notes table on the linked SQLite database.
     */
    public static void startDatabase(){
        String notesTable = "CREATE TABLE IF NOT EXISTS notes (" +
                "note_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "content TEXT, " +
                "x_position REAL DEFAULT 100.0, " +
                "y_position REAL DEFAULT 100.0, " +
                "width REAL DEFAULT 200.0, " +
                "height REAL DEFAULT 150.0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "is_pinned INTEGER DEFAULT 0" +
                ");";

        try (Connection conn = connect();
             Statement query = conn.createStatement()) {
                query.execute(notesTable);
                logger.info("Database successfully initialized at URL: {}", FILE_URL);
        } catch (SQLException e) {
            logger.error("Failed to initialize database", e);
        }
    }
}
