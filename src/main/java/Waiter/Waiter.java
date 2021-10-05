package Waiter;

import Order.Order;
import Tables.Table;
import Tables.TableState;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.dinninghallapi.DinningHallApiApplication.timeUnit;

public class Waiter implements Runnable{

    private static int count = 0;

    private final int id = count++;

    Table[] tables;

    ReentrantLock[] locks;

    public Waiter(Table[] tables, ReentrantLock[] locks) {
        this.locks = locks;
        this.tables = tables;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("Waiter-"+id);

        URL url;
        HttpURLConnection con = null;
        OutputStream wr;

        try {
            url = new URL("https://postman-echo.com/post");
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Order order = null;

        JSONObject jo;

        while (true) {

            try { timeUnit.sleep(1); }
            catch (InterruptedException e) { e.printStackTrace(); }

            for (Table table : tables)
                if ((table.getState() == TableState.WaitingMakingOrder)&&(locks[table.getId()].tryLock()))
                {
                    //synchronized (table){
                    try
                    {
                        order = table.makeOrder();
                        System.out.println("Waiter " + id + " taken order " + order.getId() + " table " + table.getId());
                        //System.out.println("Time: "+ new Date(System.currentTimeMillis()));
                    } catch (InterruptedException e) { e.printStackTrace(); }

                    jo = new JSONObject();

                    jo.put("order_id",order.getId());
                    jo.put("table_id",table.getId());
                    jo.put("waiters_id",id);
                    jo.put("items",order.getItems());
                    jo.put("priority",order.getPriority());
                    jo.put("max_wait",order.getMax_wait());
                    jo.put("pick_up_time",order.getPickupTime());

                    try {
                        wr = con.getOutputStream();
                        byte[] out = jo.toString().getBytes(StandardCharsets.UTF_8);
                        wr.write(out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //transmit HTTP request to the kitchen

                //}

                    //table.receiveOrder();

                    locks[table.getId()].unlock();

            }

            //if HTTP request is taken, give it to the table

            //change state to free

        }

    }
}
