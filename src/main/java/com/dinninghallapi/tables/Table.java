package com.dinninghallapi.tables;

import com.dinninghallapi.order.Order;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.dinninghallapi.DinningHallApiApplication.*;

public class Table {

    private static int count = 0;

    private final int id = count++;

    private TableState state = TableState.Free;

    private Order order;

    private ReentrantLock lock = new ReentrantLock();

    public Table() {
    }

    public void generateOrder() {
        order = new Order();

        //System.out.println("Table "+id+ " Order " +order.getId()+" was generated!");
    }

    public Order makeOrder() throws InterruptedException {
        getTimeUnit().sleep((long) (Math.random() * 4 + 2));

        state = TableState.WaitingOrder;

        order.setPickupTime();

        return order;
    }

    public void receiveOrder() {
        state = TableState.Free;

        long pickupTime = getTimeUnit().convert(System.nanoTime() - order.getPickupTimeNs(), TimeUnit.NANOSECONDS);

        int rating = 0;

        if (pickupTime <= order.getMax_wait()) rating = 5;
        else if (pickupTime <= order.getMax_wait() * 1.1) rating = 4;
        else if (pickupTime <= order.getMax_wait() * 1.2) rating = 3;
        else if (pickupTime <= order.getMax_wait() * 1.3) rating = 2;
        else if (pickupTime <= order.getMax_wait() * 1.4) rating = 1;

        System.out.println("Table " + id + " has received his order " + order.getId() + " after " + pickupTime + " " + getTimeUnit().name());
        System.out.println("Rating: " + String.format("%.2f", addRating(rating)) + "*\n");

    }

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void switchState(TableState state) {
        this.state = state;
    }

    public TableState getState() {
        return state;
    }

    public boolean tryLock() {
        return this.lock.tryLock();
    }

    public void unlock() {
        this.lock.unlock();
    }
}
