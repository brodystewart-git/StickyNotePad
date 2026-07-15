package com.grallien.stickynoteboard;

import java.time.LocalDateTime;

/**
 * Object class for the note entity.
 * <p>This class holds all attributes of a note,
 * including its content, layout dimensions, screen position, and lifecycle
 * timestamps. It serves as the data object between the
 * UI and the persistence layer ({@link NoteDAO}).</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class Note {
    private int id;
    private String title;
    private String content;
    private final LocalDateTime created;
    private LocalDateTime updated;
    private boolean isPinned;
    private double xPosition;
    private double yPosition;
    private double width;
    private double height;

    /**
     * Constructs a new, simple note instance.
     * <p>Initializes the note with default dimensions (250x300) and position
     * (100, 100). Sets the {@code created} and {@code updated} timestamps
     * to the current system time.</p>
     *
     * @param title   The initial title of the note.
     * @param content The initial body text of the note.
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
        this.isPinned = false;
        this.xPosition = 100.0;
        this.yPosition = 100.0;
        this.width = 250.0;
        this.height = 300.0;
    }

    /**
     * Constructor for creating a Note object of a pre-existing note out of the database.
     * @param noteId The {@code int} ID of the note
     * @param title Title {@link String} of the note
     * @param content Content {@link String} of the note
     * @param createdAt The date in {@link LocalDateTime} that the note was created
     * @param updatedAt The date in {@link LocalDateTime} that the note was last updated
     * @param xPosition A {@code double} stating the x position of the note
     * @param yPosition A {@code double} stating the y position of the note
     * @param width A {@code double} stating the width of the note
     * @param height A {@code double} stating the height of the note
     */
    public Note(int noteId, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                boolean isPinned, double xPosition, double yPosition, double width, double height) {
        this.id = noteId;
        this.title = title;
        this.content = content;
        this.created = createdAt;
        this.updated = updatedAt;
        this.isPinned = isPinned;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
    }


    /**
     * Overrides toString method in order to make it easier to utilize its data.
     * @return A {@link String} of the note's data.
     */
    @Override
    public String toString() {
        return "Note{id=" + id + ", title='" + title + "', pos=(" + xPosition + "," + yPosition + ")}";
    }

    /**
     * Gets the note's x position
     * @return The {@code double} x position value.
     */
    public double getXPosition() { return xPosition; }

    /**
     * Sets the x position
     * @param xPosition The {@code double} x position value.
     */
    public void setXPosition(double xPosition) { this.xPosition = xPosition; }

    /**
     * Gets the note's y position
     * @return The {@code double} y position value.
     */
    public double getYPosition() { return yPosition; }

    /**
     * Sets the y position
     * @param yPosition The {@code double} y position value.
     */
    public void setYPosition(double yPosition) { this.yPosition = yPosition; }

    /**
     * Gets the note's width
     * @return The {@code double} width value.
     */
    public double getWidth() { return width; }

    /**
     * Sets the note's width
     * @param width The {@code double} width value.
     */
    public void setWidth(double width) { this.width = width; }

    /**
     * Gets the note's height
     * @return The {@code double} height value.
     */
    public double getHeight() { return height; }

    /**
     * Sets the note's height
     * @param height The {@code double} height value.
     */
    public void setHeight(double height) { this.height = height; }

    /**
     * Gets the note's ID.
     * @return The {@code int} id of the note.
     */
    public int getId(){return id;}

    /**
     * Sets the note's ID.
     * @param id The updated {@code int} id of the note.
     */
    public void setId(int id){this.id = id;}

    /**
     * Gets the note's title.
     * @return The {@link String} title of the note.
     */
    public String getTitle(){ return title;}

    /**
     * Sets the note's title.
     * @param title The updated {@link String} title of the note.
     */
    public void setTitle(String title){this.title = title;}

    /**
     * Gets the note's content text.
     * @return The {@link String} content of the note.
     */
    public String getContent(){return content;}

    /**
     * Sets the note's content text.
     * @param content The {@link String} content of the note.
     */
    public void setContent(String content) {
        this.content = content;
        this.updated = LocalDateTime.now();
    }

    /**
     * Gets the date the note was created.
     * @return the {@link LocalDateTime} of the note's creation.
     */
    public LocalDateTime getCreatedAt(){return created;}

    /**
     * Gets the date the note was last updated.
     * @return the {@link LocalDateTime} of the note's last update.
     */
    public LocalDateTime getUpdatedAt(){return updated;}

    /**
     * Sets the last updated date for the note
     * @param updatedAt The {@link LocalDateTime} of the last time the note was updated.
     */
    public void setUpdatedAt(LocalDateTime updatedAt){this.updated = updatedAt;}

    /**
     * Returns if the note was pinned or not.
     * @return {@code true} if the boolean was pinned, otherwise {@code false}
     */
    public boolean isPinned(){return isPinned;}

    /**
     * Sets the note to its new pinned state.
     * @param pinned {@code true} if the note was pinned, otherwise {@code false}
     */
    public void setPinned(boolean pinned){isPinned = pinned;}
}
