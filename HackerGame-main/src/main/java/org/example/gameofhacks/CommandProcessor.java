package org.example.gameofhacks;

public class CommandProcessor {
    private Level currentLevel;

    public CommandProcessor(Level level) {
        this.currentLevel = level;
    }

    public String process(String input) {
        // Сначала проверяем, является ли введенная команда командой для решения уровня
        if (currentLevel.isSolved(input.trim())) { // Добавил trim() на всякий случай
            // Если команда является решением, не нужно ее передавать в processCommand уровня,
            // так как isSolved уже подтвердило, что это команда решения,
            // и специфическая логика решения уровня (например, установка solved = true)
            // должна быть внутри isSolved или в processCommand, если isSolved возвращает true.
            // В текущей структуре MediumLevel и HardLevel, isSolved просто проверяет команду,
            // а фактическое изменение состояния solved происходит в их processCommand.
            // Это нужно будет унифицировать или оставить как есть, но CommandProcessor должен
            // корректно это обработать.

            // Давайте вызовем processCommand уровня, чтобы он мог обновить свое состояние (например, solved = true)
            // и вернуть сообщение об успешном завершении.
            return currentLevel.processCommand(input.trim());
        }

        // Затем проверяем, является ли команда "hint"
        if (input.equalsIgnoreCase("hint")) {
            return currentLevel.getHint();
        }

        // Если это не команда решения и не "hint", передаем команду на обработку текущему уровню
        // Этот вызов должен обрабатывать все остальные команды, специфичные для уровня (scan, connect и т.д.)
        return currentLevel.processCommand(input.trim()); // Добавил trim()
    }
}