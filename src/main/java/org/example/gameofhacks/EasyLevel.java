package org.example.gameofhacks;

/**
 * See klass esindab mängu kerget taset.
 * Sisaldab lihtsat loogikat ja käske, mis on mõeldud algajatele.
 * Taseme lahendamine on seotud kindla käskude jadaga.
 */

public class EasyLevel extends Level {

    private boolean levelCompleted = false;

    @Override
    public String getIntroText() {
        return "Target: youtube.com (test system)";
    }

    @Override
    public String processCommand(String command) {
        command = command.trim().toLowerCase();

        return switch (command) {
            case "hint" -> getHint();
            case "scan youtube.com" -> "Port 443 is open.";
            case "inject --port 443 --payload bypass" -> {
                levelCompleted = true;
                yield "Access granted. Welcome to YouTube admin panel.";
            }
            default -> "Unknown or incorrect command.";
        };
    }



    @Override
    public boolean isSolved(String command) {
        return levelCompleted;
    }

    @Override
    public String getHint() {
        return "Hint: first scan youtube.com, then use inject with the correct port and payload.";
    }

    @Override
    public boolean isLevelCompleted() {
        return levelCompleted;
    }
}