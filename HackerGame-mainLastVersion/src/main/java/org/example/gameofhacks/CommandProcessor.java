package org.example.gameofhacks;

public class CommandProcessor {
    private Level currentLevel;

    public CommandProcessor(Level level) {
        this.currentLevel = level;
    }

    public String process(String input) {
        if (currentLevel.isSolved(input)) {
            return "Access granted! Level complete.";
        }
        if (input.equalsIgnoreCase("hint")) {
            return currentLevel.getHint();
        }
        return "Unknown or incorrect command.";
    }
}
