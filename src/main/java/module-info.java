module org.example.gameofhacks {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gameofhacks to javafx.fxml;
    exports org.example.gameofhacks;
}