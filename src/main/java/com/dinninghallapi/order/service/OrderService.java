package com.dinninghallapi.order.service;

import com.dinninghallapi.foods.Foods;
import com.dinninghallapi.order.AOrder;
import com.dinninghallapi.order.Order;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.dinninghallapi.DinningHallApiApplication.getTimeUnit;

public class OrderService extends AOrder {

    @JsonProperty("created_time")
    @Getter
    private final long createdTime;
    @JsonProperty("registered_time")
    @Getter
    private final long registeredTime = System.currentTimeMillis() / 1000L;
    @JsonIgnore
    private final long registeredTimeNs = System.nanoTime();
    @JsonProperty("is_ready")
    @Getter
    @Setter
    private boolean isReady = false;
    @JsonProperty("estimated_waiting_time")
    private long estimatedWaitingTime;
    @JsonProperty("prepared_time")
    @Getter
    private long preparedTime = 0;

    @JsonCreator
    public OrderService(@JsonProperty("items") ArrayList<Integer> items,
                        @JsonProperty("priority") int priority,
                        @JsonProperty("max_wait") double max_wait,
                        @JsonProperty("created_time") long createdTime) {
        this.id = count++;
        this.items = items;
        this.priority = priority;
        this.max_wait = max_wait;
        this.createdTime = createdTime;
        this.cooking_time = 0;
        this.cooking_details = null;
        calculateEstTime();

        //this.generalPriority = registeredTime - priority;
    }

    public Order transformToOrder() {
        return new Order(id, items, priority, max_wait);
    }

    private void calculateEstTime() {

        for (Integer item : items)
            estimatedWaitingTime += Foods.preparationTime(item);

        estimatedWaitingTime *= 1.4;

    }

    public long getEstimatedWaitingTime() {
        if (isReady) return 0;

        long timePassed = getTimeUnit().convert(System.nanoTime() - registeredTimeNs, TimeUnit.NANOSECONDS);

        long timeRemained = estimatedWaitingTime - timePassed;

        if (timeRemained > 0)
            return estimatedWaitingTime - timePassed;
        else
            return 0;
    }

    public void makePrepared(long cooking_time, ArrayList<HashMap<String, Integer>> cooking_details) {

        this.cooking_time = cooking_time;

        this.cooking_details = cooking_details;

        preparedTime = System.currentTimeMillis() / 1000L;

        estimatedWaitingTime = 0;

        isReady = true;
    }
}
