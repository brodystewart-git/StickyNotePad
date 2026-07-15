package com.grallien.stickynoteboard;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for skin and asset loading.
 * <p>This manager bridges the gap between the file system
 * and the ClassLoader to allow for skin management.</p>
 *
 * @author Brody Stewart
 * @version 1.0
 */
public class SkinManager {
    private static final Logger logger = LoggerFactory.getLogger(SkinManager.class);
    private static final File SKINS_DIR = initSkinsDir();

    /**
     * Initializes the directory path for skin assets.
     * <p>Attempts to locate the 'Skins' directory relative to the running
     * application's executable path. If the directory detection fails,
     * it defaults to the current working directory as a fallback.</p>
     *
     * @return A {@link File} object representing the directory where skins are stored.
     */
    private static File initSkinsDir() {
        try {
            String path = SkinManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File appDir = new File(path).getParentFile();
            return new File(appDir, "Skins");
        } catch (Exception e) {
            logger.error("Failed to detect application directory, falling back to local path", e);
            return new File("Skins");
        }
    }

    /**
     * Loads an image resource from a specific skin directory.
     *
     * @param skinName The folder name within {@code /Skins/}.
     * @param fileName The name of the file to load (e.g., "note.png").
     * @return The {@link Image} if found; {@code null} if the path is invalid or missing.
     */
    public static Image getSkinImage(String skinName, String fileName) {
        File file = new File(SKINS_DIR, skinName + "/" + fileName);
        if (!file.exists()) return null;
        return new Image(file.toURI().toString());
    }

    /**
     * Retrieves the URL path for a skin's stylesheet.
     *
     * @param skinName    The target skin folder.
     * @param cssFileName The CSS file name (e.g., "style.css").
     * @return An external URL string compatible with JavaFX stylesheets.
     */
    public static String getSkinCssPath(String skinName, String cssFileName) {
        File file = new File(SKINS_DIR, skinName + "/" + cssFileName);
        if (!file.exists()) return null;
        return file.toURI().toString();
    }

    /**
     * Scans the {@code folder} directory for valid skin folders.
     * @return A list of directory names found in the Skins folder.
     */
    public static List<String> getAvailableSkins() {
        if (!SKINS_DIR.exists()) {
            SKINS_DIR.mkdir(); // Creates the folder if it doesn't exist
        }

        File[] folders = SKINS_DIR.listFiles(File::isDirectory);
        if (folders != null) {
            return Arrays.stream(folders)
                    .map(File::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Validates a skin's integrity by verifying all required assets exist.
     * <p>A skin is considered "valid" if it contains:
     * <ul>
     *     <li>{@code note.png}</li>
     *     <li>{@code close.png}</li>
     *     <li>{@code pin.png}</li>
     *     <li>{@code style.css}</li>
     * </ul>
     * This prevents runtime crashes where the UI tries to load non-existent assets.</p>
     *
     * @param skinName The identifier of the skin to validate.
     * @return {@code true} if all required files are present; {@code false} otherwise.
     */
    public static boolean isSkinValid(String skinName) {
        String[] requiredFiles = {"note.png", "close.png", "pin.png", "style.css"};

        for (String file : requiredFiles) {
            File f = new File(SKINS_DIR, skinName + "/" + file);
            if (!f.exists()) {
                return false;
            }
        }
        return true;
    }
}