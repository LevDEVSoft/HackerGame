package org.example.gameofhacks;

/**
 * See klass esindab mängu alg- või ooteseisundit.
 * Annab mängijale esmased juhised ja suunab ta ülesannet valima.
 * Sellel tasemel ei ole konkreetset lahendust ega keerulist loogikat.
 */

public class DummyLevel extends Level {

    @Override
    public String getIntroText() {
        return "Waiting for mission...\nTry: hack youtube.com, hack google.com, hack pentagon.gov";
    }

    @Override
    public String processCommand(String command) {
        return "Type 'hack <target>' to begin.";
    }

    @Override
    public boolean isSolved(String command) {
        return false;
    }

    @Override
    public boolean isLevelCompleted() {
        return false;
    }

    @Override
    public String getHint() {
        return "Try typing: hack youtube.com";
    }
}
