package com.example.dinninghallapi.order;

import com.example.dinninghallapi.foods.Foods;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Order {

    private static int count = 0;

    private final int id = count++;

    private final ArrayList<Integer> items = new ArrayList<>();

    private final int priority;

    private double max_wait=0;

    private long pickupTime;

    private long pickupTimeNs;

    public Order() {

        int numberOfItems = (int) (Math.random()*4+1);

        while (numberOfItems>0) {
            items.add((int) (Math.random()*9+1));
            numberOfItems--;
        }

        this.priority = (int) (Math.random()*4+1);

        for (Integer item : items)
            if (new Foods(item).getPreparation_time() >max_wait) max_wait= new Foods(item).getPreparation_time();

            max_wait = 1.3 * max_wait;

    }

    public void setPickupTime() {
        pickupTimeNs = System.nanoTime();
        pickupTime = TimeUnit.MILLISECONDS.convert(pickupTimeNs, TimeUnit.NANOSECONDS) / 1000L;
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

    public long getPickupTimeNs() {
        return pickupTimeNs;
    }
}
