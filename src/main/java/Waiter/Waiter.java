package Waiter;

import Order.Order;
import Tables.Table;
import Tables.TableState;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Waiter implements Runnable{

    private static int count = 0;

    private final int id = count++;

    Table[] tables;

    public Waiter(Table[] tables) {
        this.tables = tables;
    }

    @Override
    public void run() {

        try {
            URL url = new URL("www.google.com");
            URLConnection con = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Order order = null;

        JSONObject jo;

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

                    jo = new JSONObject();

                    jo.put("order_id",order.getId());
                    jo.put("table_id",table.getId());
                    jo.put("waiters_id",id);
                    jo.put("items",order.getItems());
                    jo.put("priority",order.getPriority());
                    jo.put("max_wait",order.getMax_wait());
                    jo.put("pick_up_time",order.getPickupTime());

                    //transmit HTTP request to the kitchen

                }
            }

            //if HTTP request is taken, give it to the table

            //change state to free

        }

    }
}
