module com.example.aichatbot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires okhttp;


    opens com.example.aichatbot to javafx.fxml;
    exports com.example.aichatbot;
}