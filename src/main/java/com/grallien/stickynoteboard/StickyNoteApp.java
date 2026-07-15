package com.grallien.stickynoteboard;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;

/**
 * Main application manager for the Sticky Note App.
 * <p>This class manages the lifecycle of the JavaFX application, handles
 * global keyboard shortcuts via {@link NativeKeyListener}, and integrates
 * the application into the system tray.</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class StickyNoteApp extends Application implements NativeKeyListener {
    private static final Logger logger = LoggerFactory.getLogger(StickyNoteApp.class);

    private boolean isTrayVisible = false;
    private final Rectangle2D monitor = Screen.getPrimary().getVisualBounds();
    private Parent root;

    /** The width of the tray, imported from {@link TrayController}. */
    public static final double TRAY_WIDTH = TrayController.TRAY_WIDTH;

    /**
     * Orchestrates the startup sequence of the application.
     * <p>Initializes the database, loads user settings, configures the
     * transparent stage, registers the global native hook for hotkeys,
     * and sets up the system tray integration.</p>
     *
     * @param stage The primary stage of the application.
     * @throws IOException If the FXML resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize data and settings
        DatabaseManager.startDatabase();
        SettingsController.loadSavedSkin();

        // Prevent JavaFX from exiting when the last window is closed
        Platform.setImplicitExit(false);

        stage.initStyle(StageStyle.UTILITY);
        stage.setOpacity(0);

        // Load UI
        FXMLLoader fxmlLoader = new FXMLLoader(StickyNoteApp.class.getResource("tray.fxml"));
        root = fxmlLoader.load();
        TrayController trayController = fxmlLoader.getController();
        trayController.applyTraySkin(SettingsController.activeSkin);

        if (root instanceof javafx.scene.layout.Region region) {
            region.setPrefWidth(TRAY_WIDTH);
            region.setPrefHeight(monitor.getHeight());
        }
        root.setTranslateX(0);

        // Scene and Stage setup for the slide-out tray
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        Stage mainStage = new Stage(StageStyle.TRANSPARENT);
        mainStage.initOwner(stage);
        mainStage.initStyle(StageStyle.TRANSPARENT);
        mainStage.setScene(scene);
        mainStage.setHeight(monitor.getHeight());
        mainStage.setWidth(TRAY_WIDTH);
        mainStage.setX(monitor.getMaxX() - TRAY_WIDTH);
        mainStage.setY(monitor.getMinY());
        mainStage.setAlwaysOnTop(true);

        // Register Global Native Hook for shortcuts
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            logger.warn("Failed to initialize the native hook.");
        }
        GlobalScreen.addNativeKeyListener(this);

        // Setup system tray in AWT thread
        javax.swing.SwingUtilities.invokeLater(this::addAppToSystemTray);

        NoteManager.loadAllNotes();
        stage.show();
        mainStage.show();

        // Initial state: hide tray off-screen
        Platform.runLater(() -> {
            if (!isTrayVisible) {
                root.setTranslateX(TRAY_WIDTH);
            }
        });
    }

    /**
     * Listens for global key events.
     * <p>Detects the {@code Alt + S} combination to toggle the tray visibility.</p>
     *
     * @param e The native key event captured globally.
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        boolean isAltDown = (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0;
        if (isAltDown && e.getKeyCode() == NativeKeyEvent.VC_S) {
            Platform.runLater(this::toggleTray);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

    /**
     * Animates the slide-in and slide-out behavior of the tray.
     * <p>Uses a {@link TranslateTransition} to smoothly shift the {@code root}
     * node based on the current {@code isTrayVisible} state.</p>
     */
    public void toggleTray(){
        TranslateTransition transition = new TranslateTransition(Duration.millis(350), root);
        transition.setInterpolator(Interpolator.SPLINE(0.1, 0.9, 0.2, 1.0));

        if (isTrayVisible) {
            transition.setToX(TRAY_WIDTH);
            isTrayVisible = false;
        } else {
            transition.setToX(0);
            isTrayVisible = true;
        }
        transition.play();
    }

    /**
     * Handles application shutdown cleanup.
     * <p>Unregisters the global native hook to prevent resource leaks.</p>
     */
    @Override
    public void stop() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            logger.error("Failed to unregister native hook", e);
        }
    }

    /**
     * Configures the system tray icon and context menu.
     * <p>Uses AWT {@link java.awt.SystemTray} to provide OS-level access
     * to the application (Exit, Toggle).</p>
     */
    private void addAppToSystemTray() {
        try {
            java.awt.Toolkit.getDefaultToolkit();

            if (!java.awt.SystemTray.isSupported()) {
                logger.warn("System tray is not supported on this OS");
                return;
            }

            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imageURL = StickyNoteApp.class.getResource("logo.png");
            java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(imageURL);

            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Sticky Note Board");
            trayIcon.addActionListener(e -> Platform.runLater(this::toggleTray));

            java.awt.PopupMenu popup = new java.awt.PopupMenu();
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit Application");

            exitItem.addActionListener(e -> {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException ex) {
                    logger.error("Failed to unregister native hook {}", e);
                }
                Platform.exit();
                System.exit(0);
            });

            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            tray.add(trayIcon);

        } catch (Exception e) {
            logger.error("Unable to load System Tray", e);
        }
    }
}