package com.grallien.stickynoteboard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller responsible for the main tray interface and user interaction.
 * <p>This class manages the main application window, the creation and dragging of notes,
 * and synchronizes global skin changes across all UI components.</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class TrayController {
    private static final Logger logger = LoggerFactory.getLogger(TrayController.class);

    @FXML private AnchorPane canvasArea;
    @FXML private VBox rootPane;
    @FXML private StackPane mainContainer;

    private ImageView backgroundImageView;
    private double nextX = 20.0;
    private double nextY = 20.0;

    /** Constant for the tray width, ensuring layout consistency. */
    public static final double TRAY_WIDTH = 600.0;

    /**
     * Initializes the tray controller.
     * <p>Registers this instance with the {@link NoteManager} and sets up the listener that updates
     * the UI when user preferences change.</p>
     */
    @FXML
    public void initialize() {
        SettingsController.onSkinChanged = () -> Platform.runLater(() -> {
            String newSkin = SettingsController.activeSkin;
            applyTraySkin(newSkin);
            NoteManager.updateAllSkins(newSkin);
        });
        NoteManager.setTrayController(this);
    }

    /**
     * Handles the creation of a new note.
     * <p>This method follows a specific sequence:
     * <ol>
     *     <li>Persists the new {@link Note} to the database.</li>
     *     <li>Loads the FXML UI component.</li>
     *     <li>Applies the current skin and coordinates.</li>
     *     <li>Enables interactivity via {@link #makeDraggable(StackPane, NoteController)}.</li>
     * </ol>
     * </p>
     */
    @FXML
    protected void handleCreateNewNote() {
        try {
            Note newNote = new Note("", "");

            NoteDAO.insertNote(newNote);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("note.fxml"));
            StackPane noteNode = loader.load();
            NoteController controller = loader.getController();

            controller.setNote(newNote);
            controller.applySkin(SettingsController.activeSkin);

            rootPane.setPrefWidth(TRAY_WIDTH);
            AnchorPane.setLeftAnchor(noteNode, nextX);
            AnchorPane.setTopAnchor(noteNode, nextY);

            // Stagger next position
            nextX = (nextX + 30) % 200;
            nextY = (nextY + 30) % 400;

            makeDraggable(noteNode, controller);
            canvasArea.getChildren().add(noteNode);
        }catch (IOException e){
            logger.error("Failed to create note",e);
        }
    }

    /**
     * Opens the application settings window in a new {@link Stage}.
     */
    @FXML
    protected void handleSettings() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            logger.error("Failed to open settings", e);
        }
        stage.setTitle("Settings");
        stage.show();
    }

    /**
     * Enables mouse interaction for dragging notes across the canvas.
     * <p>This method implements boundary checking to ensure notes do not exit
     * the viewable area and honors the {@link Note#isPinned()} status to
     * prevent movement of locked notes.</p>
     *
     * @param node The UI container to make draggable.
     * @param controller The {@link NoteController} associated with the note.
     */
    public void makeDraggable(StackPane node, NoteController controller) {
        final double[] dragStart = new double[2];

        node.setOnMousePressed(mouseEvent -> {
            if (controller.getNote() != null && controller.getNote().isPinned()) {
                return;
            }
            dragStart[0] = mouseEvent.getSceneX() - node.getTranslateX();
            dragStart[1] = mouseEvent.getSceneY() - node.getTranslateY();
            node.toFront();
        });

        node.setOnMouseDragged(mouseEvent -> {
            if (controller.getNote() != null && controller.getNote().isPinned()) {
                return;
            }
            double newTranslateX = mouseEvent.getSceneX() - dragStart[0];
            double newTranslateY = mouseEvent.getSceneY() - dragStart[1];

            double maxAllowedX = canvasArea.getWidth() - NoteController.NOTE_WIDTH;
            double maxAllowedY = canvasArea.getHeight() - NoteController.NOTE_HEIGHT;

            // Enforce bounds
            if (newTranslateX >= 0 && newTranslateX <= maxAllowedX) {
                node.setTranslateX(newTranslateX);
            }
            if (newTranslateY >= 0 && newTranslateY <= maxAllowedY) {
                node.setTranslateY(newTranslateY);
            }
        });
        node.setOnMouseReleased(mouseEvent -> {
            if (controller.getNote() != null && !controller.getNote().isPinned()) {
                controller.savePosition(node.getTranslateX(), node.getTranslateY());
            }
        });
    }

    /**
     * Updates the tray UI to match the selected skin.
     * <p>This method:
     * <ul>
     *     <li>Replaces the background image overlay.</li>
     *     <li>Binds the image dimensions to the container.</li>
     *     <li>Applies the new CSS stylesheet to the {@code mainContainer}.</li>
     * </ul>
     * </p>
     *
     * @param skinName The identifier for the theme to apply.
     */
    public void applyTraySkin(String skinName) {
        if (backgroundImageView != null) {
            mainContainer.getChildren().remove(backgroundImageView);
        }

        Image trayImage = SkinManager.getSkinImage(skinName, "tray.png");

        if (trayImage != null) {
            backgroundImageView = new ImageView(trayImage);
            backgroundImageView.setPreserveRatio(false);

            backgroundImageView.fitWidthProperty().bind(mainContainer.widthProperty());
            backgroundImageView.fitHeightProperty().bind(mainContainer.heightProperty());

            // Add as the very bottom layer (index 0)
            mainContainer.getChildren().add(0, backgroundImageView);
        }

        String cssPath = SkinManager.getSkinCssPath(skinName, "style.css");
        if (cssPath != null) {
            if (!mainContainer.getStyleClass().contains("skin-config")) {
                mainContainer.getStyleClass().add("skin-config");
            }
            mainContainer.getStylesheets().clear();
            mainContainer.getStylesheets().add(cssPath);
        }
    }

    /**
     * Provides access to the canvas area where notes are rendered.
     * @return The {@link AnchorPane} used as the note canvas.
     */
    public AnchorPane getCanvasArea() {
        return canvasArea;
    }
}