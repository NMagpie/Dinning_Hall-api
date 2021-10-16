package com.example.dinninghallapi.waiter;

import com.example.dinninghallapi.order.Order;
import com.example.dinninghallapi.tables.Table;
import com.example.dinninghallapi.tables.TableState;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.dinninghallapi.DinningHallApiApplication.*;

public class Waiter implements Runnable{

    private static int count = 0;

    private final int id = count++;

    private final Table[] tables;

    private final ReentrantLock[] locks;

    private static final String url = getURL()+"/order";

    private final ArrayList<Integer> tablesReady = new ArrayList<>();

    private final ArrayList<Integer> orderIds = new ArrayList<>();

    private static final HttpHeaders headers = new HttpHeaders() {{setContentType(MediaType.APPLICATION_JSON);}};

    private final RestTemplate restTemplate = new RestTemplate();

    public void addFinishedOrder(int tableReady, int orderId) {
        tablesReady.add(tableReady);
        orderIds.add(orderId);
    }

    private void sendPostRequest(JSONObject object) {
        HttpEntity<String> request = new HttpEntity<>(object.toString(),headers);
        String response = restTemplate.postForObject(url,request,String.class);
        if (response == null) {
            System.out.println("No response! Exiting program...");
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

        Order order = null;

        JSONObject jo;

        while (true) {

            try {
                getRestTime().sleep( 1 );
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

                    sendPostRequest(jo);

                    locks[table.getId()].unlock();

            }

            while (!tablesReady.isEmpty()) {
                int tableId = tablesReady.get(0);
                //System.out.println("Table "+tableId+" has received his order "+orderIds.get(0));
                tables[tableId].receiveOrder();
                tablesReady.remove(0);
                orderIds.remove(0);
            }

        }

    }
}
