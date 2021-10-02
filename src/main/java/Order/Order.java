package Order;

import Foods.Foods;

import java.util.ArrayList;

public class Order {

    private static int count = 0;

    private final int id = count++;

    private ArrayList<Integer> items;

    private int priority;

    private double max_wait=0;

    private long pickupTime;

    public Order() {

        items= new ArrayList<>();

        int numberOfItems = (int) (Math.random()*3+1);

        while (numberOfItems>0) {
            items.add((int) (Math.random()*10+1));
            numberOfItems--;
        }

        this.priority = (int) (Math.random()*5+1);

        for (Integer item : items)
            if (new Foods(item).getPreparation_time() >max_wait) max_wait= new Foods(item).getPreparation_time();

            max_wait = 1.3 * max_wait;

    }

    public void setPickupTime() {
        pickupTime= System.currentTimeMillis() / 1000L;
    }

    public long getPickupTime() {
        return pickupTime;
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
