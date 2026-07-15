# StickyNotePad

A lightweight sticky note application. Built with JavaFX, StickyNotePad features persistent local storage, global keyboard hook support for quick access, and a custom skinning engine.

---

### 📥 Download

**[Download StickyNoteBoard (Latest Version)](https://github.com/brodystewart-git/StickyNotePad/releases/latest/download/StickyNoteBoard.zip)**

---

### 🚀 Features

*   **Persistent Storage**: Keeps your notes safe using a local database backend.
*   **Global Hotkeys**: Uses native input hooks (`JNativeHook`) to respond to keyboard events even when the application is minimized.
*   **Customizable UI**: Fully themeable using CSS skinning support.
*   **Portable**: No installation required; simply extract and run.

---

### 🛠 Tech Stack

*   **Language**: Java
*   **GUI Framework**: JavaFX
*   **Native Hooks**: [JNativeHook](https://github.com/kwhat/jnativehook)
*   **Database**: SQLite/JDBC
*   **Build Tool**: Maven

---

### 💻 Getting Started

1. **Download**: Grab the latest `.zip` file from the [Releases page](https://github.com/brodystewart-git/StickyNotePad/releases).
2. **Extract**: Unzip the folder to your preferred location (e.g., Documents or Desktop).
3. **Run**: Double-click `StickyNoteBoard.exe` to launch the application.

> **Note:** Ensure you have a standard Java Runtime environment installed on your system if you are running the JAR directly.

---

### 📂 Project Structure

*   `/app`: Contains the main application JAR and native DLL dependencies.
*   `/runtime`: Bundled Java runtime environment for portable execution.
*   `/app/Skins`: CSS stylesheets and images for customizing the application look and feel.

To customize your own, please look at the template_skin folder in the /app/Skins directory. There you will see template images of the close button, pin button, note layout and a css sheet that can be used to change colors in the skin-config section. There's also an also image one can add for the tray background, an example being in the kitty skin folder.

---
Developed by **Brody Stewart**.
---

*Found a bug or have a feature request? Please open an issue in the [Issues tab](https://github.com/brodystewart-git/StickyNotePad/issues).*
