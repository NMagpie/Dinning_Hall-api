package Order;

import java.util.ArrayList;

public class Order {

    private static int count = 0;

    private int id;

    private ArrayList<Integer> items;

    private int priority;

    private int max_wait;

    public Order(ArrayList<Integer> items) {
        id = count++;
        this.items = items;

        

    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getItems() {
        return items;
    }

    public int getPriority() {
        return priority;
    }

    public int getMax_wait() {
        return max_wait;
    }
}
