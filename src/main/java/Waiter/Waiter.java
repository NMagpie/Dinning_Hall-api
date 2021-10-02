package Waiter;

import Order.Order;
import Tables.Table;
import Tables.TableState;

public class Waiter implements Runnable{

    private static int count = 0;

    private final int id = count++;

    Table[] tables;

    public Waiter(Table[] tables) {
        this.tables = tables;
    }

    @Override
    public void run() {

        Order order;

        while (true) {

            for (Table table : tables)
                synchronized (table){
                if (table.getState() == TableState.WaitingMakingOrder)
                {
                    try
                    {
                        order = table.makeOrder();
                        System.out.println("Waiter " + id + " taken order " + order.getId() + " table " + table.getId());
                    } catch (InterruptedException e) { e.printStackTrace(); }

                    //transmit HTTP request to the kitchen

                }
            }

            //if HTTP request is taken, give it to the table

            //change state to free

        }

    }
}
