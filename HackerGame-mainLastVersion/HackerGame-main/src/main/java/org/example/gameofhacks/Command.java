package org.example.gameofhacks;

/**
 * See klass on mängu "Sabloon".
 * Selle klassi peamine ülesanne on määratleda, kuidas iga mängu käsklus peaks toimima.
 * Igal käsul peab olema "execute" meetod, mis võtab kasutaja teksti ja tagastav vastuse.
 */

public interface Command {
    String execute(String input);
}
