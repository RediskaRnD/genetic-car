package core;

public class Player {
    public String name;                                             // имя игрока
    public int score = 0;
    public int keys = 0;                                            // зажатые кнопки управления
    public String[] keyCodes = new String[Key.size.ordinal()];      // назначенные кнопки
    public Car car;                                                 // машина игрока

    public Player(String name) {
        this.name = name;
    }

    public void setKeys(String forward, String back, String left, String right) {
        keyCodes[Key.FORWARD.ordinal()] = forward;
        keyCodes[Key.BACK.ordinal()] = back;
        keyCodes[Key.LEFT.ordinal()] = left;
        keyCodes[Key.RIGHT.ordinal()] = right;
    }
}
