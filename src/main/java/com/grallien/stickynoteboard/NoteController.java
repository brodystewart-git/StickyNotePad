package com.grallien.stickynoteboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * This class is the controller responsible for managing the UI interactions of a note.
 * <p>This class binds a {@link Note} model to the FXML layout. It handles:
 * <ul>
 *     <li>Real-time input validation and character counting.</li>
 *     <li>Persistence triggers whenever note data or position changes.</li>
 *     <li>UI event handling (forwarding events from children to the root pane).</li>
 *     <li>Skin application and dynamic styling.</li>
 * </ul>
 * </p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class NoteController {
    private Note note;

    @FXML private StackPane rootPane;
    @FXML private ImageView skinImageView;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private Label charCountLabel;
    @FXML private ImageView pinIcon;
    @FXML private ImageView closeIcon;

    /** Vertical offset used to reserve space for the "drag bar" area. */
    public static final double DRAG_BAR_HEIGHT = 10.0;
    /** Fixed width for all note instances. */
    public static final double NOTE_WIDTH = 350.0;
    /** Fixed height for all note instances. */
    public static final double NOTE_HEIGHT = 400.0 + DRAG_BAR_HEIGHT;
    /** Constraint for the content text area to prevent database overflow/UI clipping. */
    public static final int MAX_TEXT_CHARACTERS = 250;

    /**
     * Initializes the controller, sets layout constraints, and registers listeners.
     * <p>Configures property listeners for the {@code titleField} and {@code contentArea}
     * to trigger automatic state saving via {@link NoteManager}. Also wires mouse
     * events from UI children (like images) back to the {@code rootPane} to ensure
     * the entire note acts as a draggable surface.</p>
     */
    @FXML
    public void initialize(){
        NoteManager.registerController(this);
        rootPane.setPrefWidth(NOTE_WIDTH);
        rootPane.setPrefHeight(NOTE_HEIGHT);

        skinImageView.setFitWidth(NOTE_WIDTH);
        skinImageView.setFitHeight(NOTE_HEIGHT - DRAG_BAR_HEIGHT);
        StackPane.setMargin(skinImageView, new javafx.geometry.Insets(DRAG_BAR_HEIGHT, 0, 0, 0));

        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15) {
                titleField.setText(oldValue);
            } else if (this.note != null) {
                this.note.setTitle(newValue);
                NoteManager.saveNoteState(this.note);
            }
        });

        contentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > MAX_TEXT_CHARACTERS) {
                contentArea.setText(oldValue);
            } else {
                charCountLabel.setText(newValue.length() + " / " + MAX_TEXT_CHARACTERS);
                if (this.note != null) {
                    this.note.setContent(newValue);
                    NoteManager.saveNoteState(this.note);
                }
            }
        });
        javafx.scene.Node[] interactiveElements = { skinImageView, titleField, contentArea};
        for (javafx.scene.Node element : interactiveElements) {
            element.setOnMousePressed(event -> rootPane.fireEvent(event));
            element.setOnMouseDragged(event -> rootPane.fireEvent(event));
        }
    }

    /**
     * Toggles the pin state of the note.
     * <p>Updates the {@link Note} model's pinned status and persists the
     * change via the {@link NoteManager}.</p>
     */
    @FXML
    protected void handlePin() {
        if (this.note != null) {
            boolean newState = !note.isPinned();
            note.setPinned(newState);
            NoteManager.saveNoteState(this.note);
        }
    }

    /**
     * Triggers the removal of this note.
     * <p>Delegates the deletion request to {@link NoteManager}, which handles
     * both database record removal and UI node removal.</p>
     */
    @FXML
    protected void handleDelete() {
        NoteManager.deleteNote(this);
    }

    /**
     * Applies a skin theme to this specific note instance.
     * <p>Updates internal image views and refreshes the CSS stylesheet
     * applied to the {@code rootPane}.</p>
     *
     * @param skinName The identifier for the theme directory to load from.
     */
    public void applySkin(String skinName) {
        skinImageView.setImage(SkinManager.getSkinImage(skinName, "note.png"));
        pinIcon.setImage(SkinManager.getSkinImage(skinName, "pin.png"));
        closeIcon.setImage(SkinManager.getSkinImage(skinName, "close.png"));

        String cssPath = SkinManager.getSkinCssPath(skinName, "style.css");
        if (cssPath != null) {
            rootPane.getStylesheets().clear();
            rootPane.getStylesheets().add(cssPath);
        }
        if (!rootPane.getStyleClass().contains("skin-config")) {
            rootPane.getStyleClass().add("skin-config");
        }
    }

    /**
     * Binds a {@link Note} model to this controller and updates UI fields.
     *
     * @param note The {@link Note} instance to display.
     */
    public void setNote(Note note) {
        this.note = note;
        this.titleField.setText(note.getTitle());
        this.contentArea.setText(note.getContent());
        rootPane.setTranslateX(note.getXPosition());
        rootPane.setTranslateY(note.getYPosition());
    }

    /**
     * Updates the note's position and tells the NoteManager to it.
     * @param x the {@code double} x-position of the note.
     * @param y the {@code double} y-position of the note.
     */
    public void savePosition(double x, double y) {
        if (this.note != null) {
            this.note.setXPosition(x);
            this.note.setYPosition(y);
            NoteManager.saveNoteState(this.note);
        }
    }

    /**
     * Gets the root UI element of the Note.
     * @return The {@link StackPane} that represents the root of the Note XML UI.
     */
    public StackPane getRootPane() {
        return rootPane;
    }

    /**
     * Gets the current note.
     * @return The current {@link Note}.
     */
    public Note getNote() {
        return note;
    }
}
