package com.grallien.stickynoteboard;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator class  for managing {@link Note} components and UI {@link NoteController}s.
 * <p>This class bridges model ({@link NoteDAO}) and view ({@link NoteController}) layers, maintaining
 * a list of active note controllers to allow for easy handling and skin updates.</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class NoteManager {
    private static final Logger logger = LoggerFactory.getLogger(NoteManager.class);
    private static final List<NoteController> noteControllers = new ArrayList<>();
    private static TrayController trayController;

    /**
     * Injects the {@link TrayController} to render notes.
     *
     * @param controller The main tray controller instance.
     */
    public static void setTrayController(TrayController controller) {
        trayController = controller;
    }

    /**
     * Method to create and display {@link Note} UI elements.
     * <p>This method loads the FXML, links the controller, initializes skins,
     * sets coordinates, and enables drag-and-drop functionality for a {@link Note} UI object.</p>
     *
     * @param note The {@link Note} data object to render.
     */
    public static void spawnNoteUI(Note note) {
        if (trayController == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(StickyNoteApp.class.getResource("note.fxml"));
            StackPane noteNode = loader.load();
            NoteController controller = loader.getController();

            controller.setNote(note);
            controller.applySkin(SettingsController.activeSkin);

            noteNode.setTranslateX(note.getXPosition());
            noteNode.setTranslateY(note.getYPosition());
            trayController.makeDraggable(noteNode, controller);

            trayController.getCanvasArea().getChildren().add(noteNode);

        } catch (IOException e) {
            logger.error("Failed to spawn Note UI.", e);
        }
    }

    /**
     * Pulls all notes from the {@link NoteDAO} and triggers the creation of Note UI elements.
     */
    public static void loadAllNotes() {
        List<Note> allNotes = NoteDAO.getAllNotes();
        for (Note note : allNotes) {
            spawnNoteUI(note);
        }
    }

    /**
     * Registers a {@link NoteController} to the manager's registry list.
     * <p>Call during the initialization of a note UI to enable
     * features like skin updates.</p>
     *
     * @param controller The controller to track.
     */
    public static void registerController(NoteController controller) {
        noteControllers.add(controller);
    }

    /**
     * Removes a {@link NoteController} from the manager's registry list.
     * @param controller The controller to stop tracking.
     */
    public static void unregisterController(NoteController controller) {
        noteControllers.remove(controller);
    }

    /**
     * Iterates through all stored {@link NoteController} instances and
     * applies a new skin.
     * @param skinName The identifier for the theme to apply.
     */
    public static void updateAllSkins(String skinName) {
        for (NoteController controller : noteControllers) {
            controller.applySkin(skinName);
        }
    }

    /**
     * Saves specific note's state (position, content, size) to the database.
     * @param note The {@link Note} data object to persist.
     */
    public static void saveNoteState(Note note) {
        NoteDAO.updateNote(note);
    }

    /**
     * Creates a new note in the Model.
     * @param title Note {@code String} title
     * @param content Note {@code String} content
     */
    public static void createNewNote(String title, String content) {
        Note newNote = new Note(title, content);
        NoteDAO.insertNote(newNote);
    }

    /**
     * Destroys a note.
     * <p>Executes in three phases:
     * <ol>
     *     <li>Data removal via {@link NoteDAO}.</li>
     *     <li>UI component removal from the {@code canvasArea}.</li>
     *     <li>Controller deregistration to prevent memory leaks.</li>
     * </ol></p>
     * @param controller The {@link NoteController} associated with the note to delete.
     */
    public static void deleteNote(NoteController controller) {
        NoteDAO.deleteNote(controller.getNote());
        if (controller.getRootPane().getParent() != null) {
            ((javafx.scene.layout.Pane) controller.getRootPane().getParent())
                    .getChildren().remove(controller.getRootPane());
        }
        unregisterController(controller);
    }
}