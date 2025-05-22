package org.example.gameofhacks;

public abstract class Level {
    public abstract String getIntroText();
    public abstract String processCommand(String command);
    public abstract boolean isSolved(String command);
    public abstract String getHint();
    public abstract boolean isLevelCompleted();

}
