# ☠ Terminal Hacker Game ☠

🖥️ **Terminal Hacker Game** on retrostiilis JavaFX-mäng, kus mängija sukeldub häkkeri rolli. Ülesandeks on murda läbi erinevate sihtmärkide turvalahendustest kasutades tekstipõhiseid käske, loogikat ja natuke nuputamist.

---

## 🎮 Funktsioonid

- 🧠 Mitmed raskusastmed: **Lihtne**, **Keskmine**, **Raske**
- 📟 Terminalilaadne liides realistliku kursori vilkumisega
- 📓 "Häkkeri märkmik" vihjetega ja kasuliku infot
- 🧩 Käskude töötlemine: `scan`, `connect`, `inject`, `hack`
- 🕹️ ASCII animeeritud splash-ekraan
- 🗂️ Mängulogi salvestamine automaatselt

---

## 🚀 Käivitamine

Veendu, et sul on Java 17+ ning JavaFX seadistatud.

```bash
git clone https://github.com/sinu-kasutajanimi/terminal-hacker-game.git
cd terminal-hacker-game
javac -d out --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml src/org/example/gameofhacks/*.java
java -cp out --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml org.example.gameofhacks.Main
