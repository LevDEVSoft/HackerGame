package org.example.gameofhacks;

public class MediumLevel extends Level {
    private boolean solved = false;

    @Override
    public String getIntroText() {
        return "Target: google.com (medium security)";
    }

    @Override
    public String processCommand(String command) {
        if (isSolved(command)) {
            solved = true;
            return "Rootkit injected successfully. Admin console accessed.\n\nLevel completed! Returning to main menu...";
        }
        return switch (command.trim()) {
            case "scan google.com" -> "Ports 80 and 443 are open";
            case "connect 443" -> "Connection established to port 443";
            default -> "Unknown or incorrect command.";
        };
    }

    @Override
    public boolean isSolved(String command) {
        return "inject --port 443 --payload rootkit".equals(command.trim());
    }

    @Override
    public String getHint() {
        return "Try: scan google.com -> connect 443 -> inject --port 443 --payload rootkit";
    }

    @Override
    public boolean isLevelCompleted() {
        return solved;
    }
}