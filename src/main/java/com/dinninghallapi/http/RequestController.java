package com.dinninghallapi.http;

import com.dinninghallapi.order.Order;
import com.dinninghallapi.waiter.Waiter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class RequestController {

    private static ArrayList<Waiter> waiters = null;

    public static void setWaiters(ArrayList<Waiter> waitersArrayList) {
        waiters = waitersArrayList;
    }

    @PostMapping(value = "/distribution", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getOrder(@RequestBody Order order) {

        if (waiters != null && order.getId() != -1) {
            int waiterId = order.getWaiter_id();
            waiters.get(waiterId).addFinishedOrder(order.getTable_id());
        }

        return "Success!";
    }
}
