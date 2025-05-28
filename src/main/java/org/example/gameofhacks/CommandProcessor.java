package org.example.gameofhacks;

/**
 * See klass töötleb mängus sisestatud käske.
 * Otsustab, mida käsuga teha ja edastab selle vastavale tasemele.
 * Haldab ka üldisi käske nagu "hint".
 */

public class CommandProcessor {
    private Level currentLevel;

    public CommandProcessor(Level level) {
        this.currentLevel = level;
    }

    public String process(String input) {

        if (currentLevel.isSolved(input.trim())) {

            return currentLevel.processCommand(input.trim());
        }

        if (input.equalsIgnoreCase("hint")) {
            return currentLevel.getHint();
        }

        return currentLevel.processCommand(input.trim());
    }
}