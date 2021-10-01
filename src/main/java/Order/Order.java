package Order;

import Foods.Foods;

import java.util.ArrayList;

public class Order {

    private static int count = 0;

    private int id;

    private ArrayList<Integer> items;

    private int priority;

    private double max_wait=0;

    public Order(ArrayList<Integer> items) {
        id = count++;
        this.items = items;
        this.priority = (int)(Math.random()*5+1);

        for (Integer item : items)
            if (new Foods(item).getPreparation_time() >max_wait) max_wait= new Foods(item).getPreparation_time();

            max_wait = 1.3 * max_wait;

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

    public double getMax_wait() {
        return max_wait;
    }
}
