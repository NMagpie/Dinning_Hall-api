package com.dinninghallapi.order;

import com.dinninghallapi.tables.Table;
import com.dinninghallapi.tables.TableState;

import java.util.concurrent.TimeUnit;

import static com.dinninghallapi.DinningHallApiApplication.getTimeUnit;

public class OrderGeneration implements Runnable {

    private final Table[] tables;

    private double chance = 0.75;

    public OrderGeneration(Table[] tables) {
        this.tables = tables;
    }

    @Override
    public void run() {

        if (getTimeUnit().ordinal() < TimeUnit.SECONDS.ordinal())
            chance = 0.9;

        Thread.currentThread().setName("OrderGen");

        int tableId;

        while (true) {

            if (Math.random() > chance) {
                do tableId = (int) (Math.random() * tables.length - 1);
                while (tables[tableId].getState() != TableState.Free);

                tables[tableId].generateOrder();
                tables[tableId].switchState(TableState.WaitingMakingOrder);
            }

            try {
                getTimeUnit().sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


}
