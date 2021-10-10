package com.example.dinninghallapi.waiter;

import com.example.dinninghallapi.order.Order;
import com.example.dinninghallapi.tables.Table;
import com.example.dinninghallapi.tables.TableState;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.dinninghallapi.DinningHallApiApplication.timeUnit;

public class Waiter implements Runnable{

    private static int count = 0;

    private final int id = count++;

    private final Table[] tables;

    private final ReentrantLock[] locks;

    private static final String url = "http://localhost:8081/distribution";

    private HttpURLConnection con;

    private OutputStream os;

    private final ArrayList<Integer> tablesReady = new ArrayList<>();

    private final ArrayList<Integer> orderIds = new ArrayList<>();

    public void addFinishedOrder(int tableReady, int orderId) {
        tablesReady.add(tableReady);
        orderIds.add(orderId);
    }

    private void openConnection(){
        try {
        URL url = new URL(Waiter.url);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        os = con.getOutputStream();
        } catch (IOException e) {
            System.out.println("ERROR: connection was canceled: "+e.getMessage());
            System.out.println("Exiting program...");
            System.exit(0);
        }
    }

    public Waiter(Table[] tables, ReentrantLock[] locks) {
        this.locks = locks;
        this.tables = tables;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("Waiter-"+id);

        //openConnection();

        Order order = null;

        JSONObject jo;

        while (true) {

            try {
                timeUnit.sleep( 1 );
            }
            catch (InterruptedException e) { e.printStackTrace(); }

            for (Table table : tables)
                if ((table.getState() == TableState.WaitingMakingOrder)&&(locks[table.getId()].tryLock()))
                {
                    try
                    {
                        order = table.makeOrder();
                        System.out.println("Waiter " + id + " taken order " + order.getId() + " table " + table.getId());
                    } catch (InterruptedException e) { e.printStackTrace(); }

                    jo = new JSONObject();

                    jo.put("order_id",order.getId());
                    jo.put("table_id",table.getId());
                    jo.put("waiter_id",id);
                    jo.put("items",order.getItems());
                    jo.put("priority",order.getPriority());
                    jo.put("max_wait",order.getMax_wait());
                    jo.put("pick_up_time",order.getPickupTime());

/*                    try {
                        byte[] out = jo.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    //transmit HTTP request to the kitchen

                    locks[table.getId()].unlock();

            }

            while (!tablesReady.isEmpty()) {
                int tableId = tablesReady.get(0);
                System.out.println("Table "+tableId+" has received his order "+orderIds.get(0));
                tables[tableId].receiveOrder();
                tablesReady.remove(0);
                orderIds.remove(0);
            }

        }

    }
}
