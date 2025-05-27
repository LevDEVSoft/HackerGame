package org.example.gameofhacks;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main extends Application {

    private TextArea terminalArea;
    private TextArea notebookContent;
    private Label pageTitle;
    private Button prevButton;
    private Button nextButton;
    private int currentPageIndex = 0;
    private Timeline cursorTimeline;
    private boolean cursorVisible = true;
    private Timeline levelCompletionDelayTimeline;

    private StringBuilder terminalHistory = new StringBuilder();
    private String currentInputLine = "";

    private static final String LOG_FILE_NAME = "game_log.txt";

    private static class NotebookPage {
        String title;
        String content;

        NotebookPage(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    private List<NotebookPage> notebookPages;
    private Level currentLevel;
    private CommandProcessor commandProcessor;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/logo.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.err.println("Could not load window icon: " + e.getMessage());
        }

        primaryStage.setTitle("☠ Terminal Hacker Game ☠");
        showSplashScreen(primaryStage);
    }

    private void showSplashScreen(Stage primaryStage) {
        TextArea splashText = new TextArea();
        splashText.setEditable(false);
        splashText.setWrapText(false);
        splashText.setStyle("-fx-control-inner-background:black; -fx-font-family:'monospace'; -fx-text-fill: lime; -fx-font-size: 12px; -fx-border-color: lime; -fx-border-width: 2px;");
        splashText.setFont(Font.font("Consolas", 12));

        VBox splashLayout = new VBox(splashText);
        splashLayout.setStyle("-fx-background-color: black;");
        splashLayout.setPadding(new Insets(0));
        VBox.setVgrow(splashText, Priority.ALWAYS);

        Scene splashScene = new Scene(splashLayout, 1100, 600);
        primaryStage.setScene(splashScene);
        primaryStage.show();

        List<String> loadingSteps = List.of(
                "Initializing secure connection...",
                "Bypassing firewall...",
                "Authenticating..."
        );

        Timeline loadingTimeline = new Timeline();
        for (int i = 0; i < loadingSteps.size(); i++) {
            int index = i;
            loadingTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i + 1), e -> {
                splashText.appendText(loadingSteps.get(index) + "\n");
            }));
        }

        loadingTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(loadingSteps.size() + 1), e -> playAsciiAnimation(splashText, primaryStage)));
        loadingTimeline.play();
    }

    private void playAsciiAnimation(TextArea splashText, Stage primaryStage) {
        try {
            InputStream in = getClass().getResourceAsStream("/earth_animation.txt");
            if (in == null) throw new IOException("Animation file not found in resources.");
            String raw = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<String> frames = Arrays.stream(raw.split("---FRAME---"))
                    .map(frame -> Arrays.stream(frame.split("\\R"))
                            .map(line -> line.replaceFirst("(?i)(?<=^)\\s*echo\\s?", ""))
                            .collect(Collectors.joining("\n")))
                    .collect(Collectors.toList());

            Timeline animation = new Timeline();
            int frameDelayMs = 100;
            int repeatCount = 1;
            int totalFrames = Math.min(frames.size(), frames.size()) * repeatCount;

            for (int i = 0; i < totalFrames; i++) {
                int index = i % frames.size();
                animation.getKeyFrames().add(new KeyFrame(Duration.millis(frameDelayMs * i), e -> {
                    splashText.setText(frames.get(index));
                }));
            }
            animation.getKeyFrames().add(new KeyFrame(Duration.millis(frameDelayMs * totalFrames + 500), e -> {
                if (cursorTimeline != null) {
                    cursorTimeline.stop();
                }
                launchLevel(primaryStage, new DummyLevel());
            }));
            animation.play();
        } catch (IOException e) {
            splashText.setText("Error loading animation\n" + e.getMessage());
            Timeline errorTimeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
                if (cursorTimeline != null) {
                    cursorTimeline.stop();
                }
                launchLevel(primaryStage, new DummyLevel());
            }));
            errorTimeline.play();
        }
    }

    private void appendToLog(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            bw.write(timestamp + " - " + message);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public void launchLevel(Stage primaryStage, Level level) {
        if (levelCompletionDelayTimeline != null) {
            levelCompletionDelayTimeline.stop();
        }

        this.currentLevel = level;
        this.commandProcessor = new CommandProcessor(level);
        this.currentInputLine = "";

        terminalHistory.setLength(0);
        String welcomeMsg = "Welcome to Terminal Hacker Game\n";
        String introMsg = currentLevel.getIntroText() + "\n";
        String typeCmdMsg = "Type your command below:\n\n";

        terminalHistory.append(welcomeMsg);
        terminalHistory.append(introMsg);
        terminalHistory.append(typeCmdMsg);

        appendToLog("LEVEL_START: " + level.getClass().getSimpleName() + " - Target: " + level.getIntroText().split("\n")[0]);
        appendToLog("GAME_RESPONSE: " + welcomeMsg.trim());
        appendToLog("GAME_RESPONSE: " + introMsg.trim());


        if (terminalArea == null) {
            launchMainUI(primaryStage);
        } else {
            primaryStage.setTitle("☠ Terminal Hacker Game ☠ - " + level.getIntroText().split("\n")[0]);
            updateTerminalDisplay();
            updateNotebook();
            terminalArea.setDisable(false);
            terminalArea.requestFocus();
        }
        if (cursorTimeline != null) {
            cursorTimeline.playFromStart();
        } else {
            startCursorBlinking();
        }
    }

    private void launchMainUI(Stage primaryStage) {
        terminalArea = new TextArea();
        terminalArea.setStyle("-fx-control-inner-background:black; -fx-font-family:'monospace'; -fx-highlight-fill: green; -fx-highlight-text-fill: black; -fx-text-fill: lime; -fx-border-color: lime; -fx-border-width: 2px;");
        terminalArea.setFont(Font.font("Consolas", 14));
        terminalArea.setWrapText(true);
        terminalArea.setEditable(false);

        terminalArea.setOnKeyPressed(event -> {
            if (terminalArea.isDisabled()) {
                event.consume();
                return;
            }

            if (!terminalArea.isFocused()) {
                terminalArea.requestFocus();
            }
            terminalArea.positionCaret(terminalArea.getText().length());

            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                String commandToProcess = currentInputLine;
                terminalHistory.append(commandToProcess).append("\n");
                currentInputLine = "";
                handleCommand(primaryStage, commandToProcess);
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                if (!currentInputLine.isEmpty()) {
                    currentInputLine = currentInputLine.substring(0, currentInputLine.length() - 1);
                    updateTerminalDisplay();
                }
                event.consume();
            } else if (event.getText() != null && !event.getText().isEmpty() && !event.isControlDown() && !event.isMetaDown() && !event.isAltDown()) {
                if (event.getText().matches("\\P{Cntrl}")) {
                    currentInputLine += event.getText();
                    updateTerminalDisplay();
                }
                event.consume();
            }
        });

        notebookPages = List.of(
                new NotebookPage("Basic Commands", "scan <url> - scan for open ports\nconnect <port> - connect to a port\ninject --payload <data> - run a payload\nhack <target> - begin hack mission\nexit - abort current hacking session\nshowlog - display game log"),
                new NotebookPage("Encryption Tips", "Caesar cipher: shift each letter\nExample: hacker -> khfnhu (shift +3)"),
                new NotebookPage("Target List", "youtube.com - test system\ngoogle.com - medium security\npentagon.gov - high security"),
                new NotebookPage("Payload Catalog", "bypass, rootkit, spoof, flood\nUse: inject --port <port> --payload <name>"),
                new NotebookPage("Strategy Note", "Not all clues are obvious. Think critically and explore all notebook pages!")
        );

        pageTitle = new Label();
        pageTitle.setStyle("-fx-text-fill: lime; -fx-font-size: 14px; -fx-font-family: monospace;");

        notebookContent = new TextArea();
        notebookContent.setStyle("-fx-control-inner-background:black; -fx-font-family:'monospace'; -fx-highlight-fill: green; -fx-highlight-text-fill: black; -fx-text-fill: lime; -fx-border-color: lime; -fx-border-width: 2px;");
        notebookContent.setFont(Font.font("Consolas", 14));
        notebookContent.setWrapText(true);
        notebookContent.setEditable(false);

        String hackerStyle = "-fx-background-color: black; -fx-text-fill: lime; -fx-border-color: lime; -fx-border-width: 2px; -fx-font-family: monospace; -fx-font-size: 12px;";

        prevButton = new Button("<< BACKTRACE");
        nextButton = new Button("EXEC >>");
        prevButton.setStyle(hackerStyle);
        nextButton.setStyle(hackerStyle);
        prevButton.setOnAction(e -> changePage(-1));
        nextButton.setOnAction(e -> changePage(1));

        HBox navButtons = new HBox(10, prevButton, nextButton);

        VBox notebookBox = new VBox(new Label("Hacker Notebook"), pageTitle, notebookContent, navButtons);
        notebookBox.setStyle("-fx-background-color: black;");
        notebookBox.setPadding(new Insets(10));
        VBox.setVgrow(notebookContent, Priority.ALWAYS);


        VBox leftPane = new VBox(new Label("Terminal"), terminalArea);
        leftPane.setStyle("-fx-background-color: black;");
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(600);
        VBox.setVgrow(terminalArea, Priority.ALWAYS);


        HBox root = new HBox(leftPane, notebookBox);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 1100, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("☠ Terminal Hacker Game ☠ - " + currentLevel.getIntroText().split("\n")[0]);
        primaryStage.show();

        updateNotebook();
        updateTerminalDisplay();
    }

    private void startCursorBlinking() {
        if (cursorTimeline != null) {
            cursorTimeline.stop();
        }
        cursorTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            cursorVisible = !cursorVisible;
            updateTerminalDisplay();
        }));
        cursorTimeline.setCycleCount(Timeline.INDEFINITE);
        cursorTimeline.play();
    }

    private void updateTerminalDisplay() {
        if (terminalArea == null) return;
        String cursor = (cursorVisible && !terminalArea.isDisabled()) ? "_" : " ";
        terminalArea.setText(terminalHistory.toString() + currentInputLine + cursor);
        terminalArea.positionCaret(terminalArea.getText().length());
    }

    private void changePage(int delta) {
        currentPageIndex += delta;
        if (currentPageIndex < 0) currentPageIndex = 0;
        if (currentPageIndex >= notebookPages.size()) currentPageIndex = notebookPages.size() - 1;
        updateNotebook();
    }

    private void updateNotebook() {
        NotebookPage page = notebookPages.get(currentPageIndex);
        pageTitle.setText("Page " + (currentPageIndex + 1) + ": " + page.title);
        notebookContent.setText(page.content);
    }

    private void handleCommand(Stage primaryStage, String commandRaw) {
        String command = commandRaw.trim().toLowerCase();
        appendToLog("USER_COMMAND: " + commandRaw);

        if (command.startsWith("hack ")) {
            String target = command.substring(5).trim();
            appendToLog("TARGET_SELECTION: " + target);
            boolean levelLaunched = false;
            switch (target) {
                case "youtube.com":
                    launchLevel(primaryStage, new EasyLevel());
                    levelLaunched = true;
                    break;
                case "google.com":
                    launchLevel(primaryStage, new MediumLevel());
                    levelLaunched = true;
                    break;
                case "pentagon.gov":
                    launchLevel(primaryStage, new HardLevel());
                    levelLaunched = true;
                    break;
                default:
                    String unknownTargetMsg = "Unknown target: " + target;
                    terminalHistory.append(unknownTargetMsg).append("\n\n");
                    appendToLog("GAME_RESPONSE: " + unknownTargetMsg);
            }
            if (levelLaunched) {
                return;
            }
        } else if (command.equals("exit")) {
            String exitMsg = "Aborting hacking... Returning to menu.";
            terminalHistory.append(exitMsg).append("\n\n");
            appendToLog("GAME_RESPONSE: " + exitMsg);
            updateTerminalDisplay();


            if (cursorTimeline != null) {
                cursorTimeline.stop();
            }
            launchLevel(primaryStage, new DummyLevel());
            return;
        } else if (command.equals("showlog")) {
            appendToLog("USER_COMMAND: showlog");
            File logFile = new File(LOG_FILE_NAME);
            if (logFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_NAME))) {
                    terminalHistory.append("--- Game Log Start ---\n");
                    appendToLog("GAME_RESPONSE: Displaying game log.");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        terminalHistory.append(line).append("\n");
                    }
                    terminalHistory.append("--- Game Log End ---\n\n");
                } catch (IOException e) {
                    String errorMsg = "Error reading log file: " + e.getMessage();
                    terminalHistory.append(errorMsg).append("\n\n");
                    appendToLog("GAME_RESPONSE_ERROR: " + errorMsg);
                }
            } else {
                String noLogMsg = "Log file does not exist yet.";
                terminalHistory.append(noLogMsg).append("\n\n");
                appendToLog("GAME_RESPONSE: " + noLogMsg);
            }
        } else {
            String response = commandProcessor.process(command);
            terminalHistory.append(response).append("\n\n");
            appendToLog("GAME_RESPONSE: " + response.replace("\n", " | "));

            if (currentLevel.isLevelCompleted()) {
                appendToLog("LEVEL_COMPLETED: " + currentLevel.getClass().getSimpleName());
                updateTerminalDisplay();

                terminalArea.setDisable(true);
                if (cursorTimeline != null) {
                    cursorTimeline.stop();
                    updateTerminalDisplay();
                }

                levelCompletionDelayTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                    launchLevel(primaryStage, new DummyLevel());
                }));
                levelCompletionDelayTimeline.play();
                return;
            }
        }
        updateTerminalDisplay();
    }
}