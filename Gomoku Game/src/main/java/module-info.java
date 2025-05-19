module com.example.javagomokugame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.javagomokugame to javafx.fxml;
    exports com.example.javagomokugame;
}