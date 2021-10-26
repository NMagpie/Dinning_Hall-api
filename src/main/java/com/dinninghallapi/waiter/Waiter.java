package com.dinninghallapi.waiter;

import com.dinninghallapi.order.Order;
import com.dinninghallapi.tables.Table;
import com.dinninghallapi.tables.TableState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static com.dinninghallapi.DinningHallApiApplication.getRestTime;
import static com.dinninghallapi.DinningHallApiApplication.getURL;

public class Waiter implements Runnable {

    private static final String url = getURL() + "/order";
    private static final HttpHeaders headers = new HttpHeaders() {{
        setContentType(MediaType.APPLICATION_JSON);
    }};
    private static int count = 0;
    private final int id = count++;
    private final Table[] tables;
    private final ArrayList<Order> finishedOrders = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    public Waiter(Table[] tables) {
        this.tables = tables;
    }

    private static synchronized void noResponse() {
        System.out.println("No response! Exiting program...");
        System.exit(0);
    }

    public void addFinishedOrder(Order order) {
        finishedOrders.add(order);
    }

    private void sendPostRequest(Order order) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(order);

            HttpEntity<String> request = new HttpEntity<>(json, headers);

            restTemplate.postForObject(url, request, String.class);

        } catch (ResourceAccessException e) {
            noResponse();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        Thread.currentThread().setName("Waiter-" + id);

        Order order;

        try {

            while (true) {

                getRestTime().sleep(1);

                for (Table table : tables)
                    if ((table.getState() == TableState.WaitingMakingOrder) && (table.tryLock())) {

                        order = table.makeOrder(id);
                        System.out.println("Waiter " + id + " taken order " + order.getId() + " table " + table.getId() + " " + order.getItems() + " priority " + order.getPriority());

                        sendPostRequest(order);

                        table.unlock();

                    }

                while (!finishedOrders.isEmpty()) {
                    order = finishedOrders.get(0);
                    int tableId = order.getTable_id();
                    tables[tableId].receiveOrder();
                    finishedOrders.remove(0);
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}