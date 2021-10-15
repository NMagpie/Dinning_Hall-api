package com.example.dinninghallapi.order;

import com.example.dinninghallapi.tables.Table;
import com.example.dinninghallapi.tables.TableState;

import static com.example.dinninghallapi.DinningHallApiApplication.timeUnit;

public class OrderGeneration implements Runnable {

    private final Table[] tables;

    public OrderGeneration(Table[] tables) {
        this.tables = tables;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("OrderGen");

        int tableId;

        while (true)
        {

            if (Math.random()>0.85)
            {
                do tableId = (int) (Math.random() * tables.length-1);
                while (tables[tableId].getState()!= TableState.Free);

                tables[tableId].generateOrder();
                tables[tableId].switchState(TableState.WaitingMakingOrder);
            }

            try { timeUnit.sleep(5); }
            catch (InterruptedException e) { e.printStackTrace(); }

        }

    }



}
