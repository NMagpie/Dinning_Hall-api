package com.example.dinninghallapi.tables;

import com.example.dinninghallapi.order.Order;

import java.util.concurrent.TimeUnit;

import static com.example.dinninghallapi.DinningHallApiApplication.*;

public class Table {

    private static int count = 0;

    private final int id = count++;

    private TableState state = TableState.Free;

    private Order order;

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

        //long pickupTime = timeUnit.convert((System.currentTimeMillis() / 1000L) - order.getPickupTime(), TimeUnit.SECONDS);

        long pickupTime = getTimeUnit().convert(System.nanoTime() - order.getPickupTimeNs(), TimeUnit.NANOSECONDS);

        rates++;

        if (pickupTime <= order.getMax_wait()) rating += 5;
        else if (pickupTime <= order.getMax_wait() * 1.1) rating += 4;
        else if (pickupTime <= order.getMax_wait() * 1.2) rating += 3;
        else if (pickupTime <= order.getMax_wait() * 1.3) rating += 2;
        else if (pickupTime <= order.getMax_wait() * 1.4) rating += 1;

        System.out.println("Table " + id + " has received his order " + order.getId() + " after " + pickupTime + " " + getTimeUnit().name());
        System.out.println("Rating: " + String.format("%.2f", rating / rates) + "*\n");

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
}
