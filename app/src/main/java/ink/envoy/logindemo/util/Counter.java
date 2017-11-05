package ink.envoy.logindemo.util;

public class Counter {
    private int value;
    private int initialValue;

    public Counter(int initialValue) {
        this.initialValue = this.value = initialValue;
    }
    public int decrease() {
        return --this.value;
    }
    public int getValue() {
        return this.value;
    }
    public int reset() {
        return this.value = this.initialValue;
    }
}
