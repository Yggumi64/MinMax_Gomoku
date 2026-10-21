package fr.esisar.in450.gomoku.gamecore.enums;

public enum XInARow {
    NONE(0, 0),
    IAR1(1, 1),
    IAR2(2, 2),
    IAR3(8, 3),
    IAR4(16, 4),
    IAR5(1000, 5);

    private final int weight;
    private final int value;

    XInARow(int weight, int value) {
        this.weight = weight;
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }
}
