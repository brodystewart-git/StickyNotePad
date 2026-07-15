module com.grallien.stickynoteboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires com.github.kwhat.jnativehook;
    requires java.prefs;


    opens com.grallien.stickynoteboard to javafx.fxml;
    exports com.grallien.stickynoteboard;
}