module com.example.bfst22group27 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens com.example.bfst22group27 to javafx.fxml;

    exports com.example.bfst22group27;
}