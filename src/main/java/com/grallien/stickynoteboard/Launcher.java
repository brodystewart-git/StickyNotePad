package com.grallien.stickynoteboard;

import javafx.application.Application;

/**
 * Application entry point for the StickyNoteBoard utility.
 * <p>
 * This class is responsible for initializing the JavaFX runtime environment
 * and bootstrapping the primary application lifecycle.
 * </p>
 *
 * @author Brody Stewart
 * @version 1.0
 * @since 2026-07-15
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(StickyNoteApp.class, args);
    }
}
