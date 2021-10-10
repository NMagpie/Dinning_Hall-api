package com.example.dinninghallapi.http;

import com.example.dinninghallapi.waiter.Waiter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class RequestController {

    private static ArrayList<Waiter> waiters= null;

    public static void setWaiters(ArrayList<Waiter> waitersArrayList) {
        waiters = waitersArrayList;
    }

    @PostMapping (value = "/distribution", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void getOrder(@RequestBody RequestForm object){
        if (waiters!=null) {

        int waiterId = object.getWaiter_id();
        int tableId = object.getTable_id();
        int orderId = object.getOrder_id();

        waiters.get(waiterId).addFinishedOrder(tableId,orderId);
        }
    }
}
