package com.grallien.stickynoteboard;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.prefs.Preferences;

/**
 * Manages application settings and user preferences.
 * <p>This controller utilizes {@link java.util.prefs.Preferences} to save
 * settings across application restarts and provides a way for the
 * rest of the application to know when configurations change.</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class SettingsController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);
    public static String activeSkin = "default_paper";
    private static final String PREF_SKIN_KEY = "last_used_skin";
    @FXML private ListView<String> skinListView;

    /**
     * Callback executed whenever the skin is changed.
     * Components should register logic here to update their UI dynamically.
     */
    public static Runnable onSkinChanged;

    /**
     * Initializes the settings UI by populating the list with available skins.
     * <p>Filters out any skins that do not pass {@link SkinManager#isSkinValid(String)}
     * validation.</p>
     */
    @FXML
    public void initialize() {
        List<String> validSkins = SkinManager.getAvailableSkins().stream()
                .filter(SkinManager::isSkinValid)
                .toList();
        skinListView.getItems().addAll(validSkins);
    }

    /**
     * Updates the active skin, updates the system preferences,
     * and triggers the {@link #onSkinChanged} callback.
     *
     * @param newSkin The identifier of the new skin to apply.
     */
    public static void setActiveSkin(String newSkin) {
        activeSkin = newSkin;
        Preferences prefs = Preferences.userNodeForPackage(StickyNoteApp.class);
        prefs.put(PREF_SKIN_KEY, newSkin);
        try {
            prefs.flush();
        } catch (Exception e) {
            logger.error("Failed to save active skin", e);
        }
        if (onSkinChanged != null) {
            onSkinChanged.run();
        }
    }

    /**
     * FXML event handler for the "Apply" button.
     * <p>Reads the current selection from the {@link ListView} and changes it via
     * {@link #setActiveSkin(String)}.</p>
     */
    @FXML
    private void handleApply() {
        String selected = skinListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SettingsController.setActiveSkin(selected);
        }
    }

    /**
     * Loads the user's preferred skin from storage.
     * <p>Defaults to {@code "default_paper"} if no preference is found or if the
     * saved skin is no longer valid.</p>
     */
    public static void loadSavedSkin() {
        Preferences prefs = Preferences.userNodeForPackage(StickyNoteApp.class);
        String savedSkin = prefs.get(PREF_SKIN_KEY, "default_paper");
        if (SkinManager.isSkinValid(savedSkin)) {
            activeSkin = savedSkin;
        } else {
            activeSkin = "default_paper";
        }
    }
}
