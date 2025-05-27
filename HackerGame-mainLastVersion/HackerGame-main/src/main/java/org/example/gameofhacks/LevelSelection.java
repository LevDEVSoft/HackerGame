package org.example.gameofhacks;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LevelSelection {
    public static void show(Stage primaryStage, Main mainApp) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: black;");
        layout.setAlignment(Pos.CENTER);

        Button easyButton = new Button("🟢 Easy - YouTube");
        Button mediumButton = new Button("🟡 Medium - Google");
        Button hardButton = new Button("🔴 Hard - Pentagon");

        for (Button btn : new Button[]{easyButton, mediumButton, hardButton}) {
            btn.setStyle("-fx-background-color: black; -fx-text-fill: lime; -fx-border-color: lime; -fx-border-width: 2px; -fx-font-family: monospace; -fx-font-size: 14px; -fx-padding: 10px 20px;");
            btn.setMinWidth(250);
        }

        easyButton.setOnAction(e -> mainApp.launchLevel(primaryStage, new EasyLevel()));
        mediumButton.setOnAction(e -> mainApp.launchLevel(primaryStage, new MediumLevel()));
        hardButton.setOnAction(e -> mainApp.launchLevel(primaryStage, new HardLevel()));

        layout.getChildren().addAll(easyButton, mediumButton, hardButton);
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}