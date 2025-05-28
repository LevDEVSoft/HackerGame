package org.example.gameofhacks;

public class HardLevel extends Level {
    private boolean solved = false;

    @Override
    public String getIntroText() {
        return "Target: pentagon.gov (high security)";
    }

    @Override
    public String processCommand(String command) {
        if (isSolved(command)) {
            solved = true;
            return "Zero-day exploit executed. Root shell obtained.\n\nLevel completed! Returning to main menu...";
        }
        return switch (command.trim()) {
            case "scan pentagon.gov" -> "Firewall detected. Obfuscation needed.";
            case "spoof firewall" -> "Firewall spoofed successfully";
            case "scan pentagon.gov --stealth" -> "Ports 22 and 8080 open";
            case "connect 22" -> "SSH connection established. Requires credentials.";
            default -> "Unknown or incorrect command.";
        };
    }

    @Override
    public boolean isSolved(String command) {
        return "inject --port 22 --payload zero-day".equals(command.trim());
    }

    @Override
    public String getHint() {
        return "Try: spoof firewall -> scan pentagon.gov --stealth -> connect 22 -> inject zero-day";
    }

    @Override
    public boolean isLevelCompleted() {
        return solved;
    }
}