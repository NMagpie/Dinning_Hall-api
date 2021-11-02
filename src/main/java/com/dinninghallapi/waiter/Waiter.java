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

import java.util.PriorityQueue;

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
    private final PriorityQueue<Integer> finishedOrders;
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    public Waiter(Table[] tables) {
        this.tables = tables;

        finishedOrders = new PriorityQueue<>();

    }

    private static synchronized void noResponse() {
        System.out.println("No response! Exiting program...");
        System.exit(0);
    }

    public void addFinishedOrder(int tableId) {
        finishedOrders.add(tableId);
    }

    private void bringOrders() {

        //TODO to change logic for receiving orders, cause it needs so much time

        while (!finishedOrders.isEmpty()) {

            int tableId = finishedOrders.poll();

            tables[tableId].receiveOrder();

        }

    }

    private void waitRest() {
        try {
            getRestTime().sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendPostRequest(Order order) {

        //TODO to change restTemplate on smth

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

        while (true) {

            for (Table table : tables) {

                if (table.getState() == TableState.WaitingMakingOrder && table.tryLock()) {

                    order = table.makeOrder(id);

                    System.out.println("Waiter " + id + " taken order " + order.getId() + " table " + table.getId() + " " + order.getItems() + " priority " + order.getPriority());

                    sendPostRequest(order);

                    table.unlock();

                }

                bringOrders();

            }

            waitRest();

        }
    }
}