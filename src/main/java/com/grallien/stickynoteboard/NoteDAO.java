package com.grallien.stickynoteboard;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) for {@link Note} objects.
 * <p>This class handles all SQL operations required to interact store and access notes,
 * bridging the Java and Database.</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class NoteDAO {
    private static final Logger logger = LoggerFactory.getLogger(NoteDAO.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Takes a new note and inserts it into the database.
     * <p>Upon successful insertion, retrieves the auto-generated
     * Note's ID from the database and updates the {@link Note}
     * with it.</p>
     *
     * @param note The {@link Note} object to insert.
     */
    public static void insertNote(Note note){
        String query = "INSERT INTO notes (title, content, x_position, y_position, width, height, created_at, " +
                "updated_at, is_pinned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmnt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            stmnt.setString(1, note.getTitle());
            stmnt.setString(2, note.getContent());
            stmnt.setDouble(3, note.getXPosition());
            stmnt.setDouble(4, note.getYPosition());
            stmnt.setDouble(5, note.getWidth());
            stmnt.setDouble(6, note.getHeight());
            stmnt.setString(7, note.getCreatedAt().format(DATE_FORMATTER));
            stmnt.setString(8, note.getUpdatedAt().format(DATE_FORMATTER));
            stmnt.setInt(9, note.isPinned() ? 1 : 0);
            stmnt.executeUpdate();

            try (ResultSet genKeys = stmnt.getGeneratedKeys()) {
                if (genKeys.next()) {
                    note.setId(genKeys.getInt(1));
                    logger.info("Saved new note, Assigned ID: {}", note.getId());
                }
            }
        }catch (SQLException e){
            logger.error("Failed to save note to database", e);
        }
    }

    /**
     * Updates an existing {@link Note} object in the database.
     * <p>This method uses the ID retrieved from the {@link Note} object
     * to update it in the database. If the ID isn't found, a warning is logged.</p>
     *
     * @param note The {@link Note} object containing updated information.
     */
    public static void updateNote(Note note){
        String query = "UPDATE notes SET title = ?, content = ?, x_position = ?, y_position = ?, width = ?, height = ?, " +
                "updated_at = ?, is_pinned = ? WHERE note_id = ?;";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmnt = conn.prepareStatement(query)){
            stmnt.setString(1, note.getTitle());
            stmnt.setString(2, note.getContent());
            stmnt.setDouble(3, note.getXPosition());
            stmnt.setDouble(4, note.getYPosition());
            stmnt.setDouble(5, note.getWidth());
            stmnt.setDouble(6, note.getHeight());
            stmnt.setString(7, note.getUpdatedAt().format(DATE_FORMATTER));
            stmnt.setInt(8, note.isPinned() ? 1 : 0);
            stmnt.setInt(9, note.getId());
            int changed = stmnt.executeUpdate();

            if(changed > 0){
                logger.info("Note update saved, Note ID: {}", note.getId());
            }else{
                logger.warn("Attempted to update a note that does not exist in the database, Note ID: {}", note.getId());
            }
        }catch (SQLException e){
            logger.error("Failed to update note in database", e);
        }
    }

    /**
     * Deletes a note from the database using its ID.
     *
     * @param note The {@link Note} object to be deleted.
     */
    public static void deleteNote(Note note){
        String query = "DELETE FROM notes WHERE note_id = ?;";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmnt = conn.prepareStatement(query)){
            stmnt.setInt(1, note.getId());
            int deleted = stmnt.executeUpdate();
            if (deleted > 0) {
                logger.info("Successfully deleted note, Note ID: {}", note.getId());
            } else {
                logger.warn("Attempted to delete note that doesn't exist in the database, Note ID: {}", note.getId());
            }
        } catch (SQLException e) {
            logger.error("Failed to delete note from database", e);
        }
    }

    /**
     * Retrieves all notes from the database.
     * <p>Notes are retrieved in the order of their {@code is_pinned} status,
     * followed by the most recently {@code updated_at} date.</p>
     *
     * @return A {@code List} of hydrated {@code Note} objects.
     */
    public static List<Note> getAllNotes(){
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes ORDER BY is_pinned DESC, updated_at DESC;";

        try (Connection conn = DatabaseManager.connect();
             Statement stmnt = conn.createStatement();
             ResultSet rs = stmnt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("note_id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                double x = rs.getDouble("x_position");
                double y = rs.getDouble("y_position");
                double w = rs.getDouble("width");
                double h = rs.getDouble("height");
                LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"), DATE_FORMATTER);
                LocalDateTime updatedAt = LocalDateTime.parse(rs.getString("updated_at"), DATE_FORMATTER);
                boolean isPinned = rs.getInt("is_pinned") == 1;
                Note note = new Note(id, title, content, createdAt, updatedAt, isPinned, x, y, w, h);
                notes.add(note);
            }
            logger.info("Successfully retrieved {} notes.", notes.size());
        } catch (SQLException e) {
            logger.error("Failed to fetch notes from database", e);
        }
        return notes;
    }
}
