package edf;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Task implements Comparable<Task> {

    private static int count = 0;
    private final SimpleIntegerProperty eT;
    private final SimpleIntegerProperty period;
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;


    public Task(int eT, int period) {
        this.eT = new SimpleIntegerProperty(eT);
        this.period = new SimpleIntegerProperty(period);
        this.id = new SimpleIntegerProperty(count);
        this.name = new SimpleStringProperty("T" + Integer.toString(count));
        count++;
    }

    public String getName() {
        return name.get();
    }

    public int getId() {
        return this.id.get();
    }

    public int getET() {
        return this.eT.get();
    }

    public int getPeriod() {
        return this.period.get();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int compareTo(Task other) {
            return Integer.compare(this.getPeriod(), other.getPeriod());
    }
}
