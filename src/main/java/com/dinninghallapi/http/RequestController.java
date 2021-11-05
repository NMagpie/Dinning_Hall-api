package com.dinninghallapi.http;

import com.dinninghallapi.order.Order;
import com.dinninghallapi.order.service.OrderService;
import com.dinninghallapi.order.service.V2ResponseId;
import com.dinninghallapi.order.service.V2ResponseS;
import com.dinninghallapi.waiter.Waiter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@RestController
public class RequestController {

    private static final ObjectMapper mapperS;

    private static final ObjectMapper mapperId;

    private static final RestTemplate restTemplate;

    private static final HttpHeaders headers;

    private static ArrayList<Waiter> waiters = null;

    private static ArrayList<OrderService> orderServices = null;

    private static String url = null;

    static {
        mapperS = new ObjectMapper();
        SimpleModule moduleS = new SimpleModule();
        moduleS.addSerializer(OrderService.class, new V2ResponseS());
        mapperS.registerModule(moduleS);

        mapperId = new ObjectMapper();
        SimpleModule moduleId = new SimpleModule();
        moduleId.addSerializer(OrderService.class, new V2ResponseId());
        mapperId.registerModule(moduleId);

        restTemplate = new RestTemplateBuilder().build();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public static void setWaiters(ArrayList<Waiter> waitersArrayList) {
        waiters = waitersArrayList;
    }

    public static void setOrderServices(ArrayList<OrderService> orderServices) {
        RequestController.orderServices = orderServices;
    }

    public static void setUrl(String url) {
        RequestController.url = url + "/order";
    }

    private static void orderToSOrder(Order order) {

        OrderService orderS = serviceOrderById(order.getId());

        if (orderS != null) orderS.makePrepared(order.getCooking_time(), order.getCooking_details());

    }

    private static OrderService serviceOrderById(int id) {

        if (orderServices == null) return null;

        for (OrderService orderS : orderServices)
            if (orderS.getId() == id) return orderS;

        return null;
    }

    @PostMapping(value = "/distribution", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getOrder(@RequestBody Order order) {

        if (order.getTable_id() != -1) {
            if (waiters != null && order.getId() != -1) {
                int waiterId = order.getWaiter_id();

                waiters.get(waiterId).addFinishedOrder(order.getTable_id());
            }
        } else orderToSOrder(order);

        return "Success!";
    }

    @PostMapping(value = "/v2/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getOrderS(@RequestBody OrderService orderS) throws JsonProcessingException {

        orderServices.add(orderS);

        Order order = orderS.transformToOrder();

        HttpEntity<Order> entity = new HttpEntity<>(order, headers);

        restTemplate.postForObject(url, entity, String.class);

        return mapperS.writeValueAsString(orderS);
    }

    @GetMapping(value = "/v2/order/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String giveOrderId(@PathVariable("id") int id) throws JsonProcessingException {

        OrderService orderS = serviceOrderById(id);

        if (orderS != null) {
            if (orderS.isReady()) orderServices.remove(orderS);

            return mapperId.writeValueAsString(orderS);
        }

        return null;
    }

}
