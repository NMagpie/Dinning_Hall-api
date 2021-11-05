package com.dinninghallapi.order;

import com.dinninghallapi.foods.Foods;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties({"pickupTimeNs", "generalPriority"})
public class Order extends AOrder {

    @JsonProperty("table_id")
    @Getter
    private final Integer table_id;

    @JsonProperty("waiter_id")
    @Setter
    @Getter
    private int waiter_id;

    @JsonProperty("pick_up_time")
    @Getter
    private long pickupTime;

    @Getter
    private long pickupTimeNs;

    public Order(int id, ArrayList<Integer> items, int priority, double max_wait, long pickupTime) {
        this.id = id;
        this.table_id = -1;
        this.waiter_id = -1;
        this.items = items;
        this.priority = priority;
        this.max_wait = max_wait;
        this.pickupTime = pickupTime;
    }

    public Order(int table_id) {

        this.id = count++;

        this.table_id = table_id;

        int numberOfItems = (int) (Math.random() * 5 + 1);

        while (numberOfItems > 0) {
            items.add((int) (Math.random() * 10 + 1));
            numberOfItems--;
        }

        this.priority = (int) (Math.random() * 5 + 1);

        for (Integer item : items)
            if (Foods.preparationTime(item) > max_wait) max_wait = Foods.preparationTime(item);

        max_wait = 1.3 * max_wait;

    }

    @JsonCreator
    public Order(@JsonProperty("order_id") int order_id,
                 @JsonProperty("table_id") int table_id,
                 @JsonProperty("waiter_id") int waiter_id,
                 @JsonProperty("items") ArrayList<Integer> items,
                 @JsonProperty("priority") int priority,
                 @JsonProperty("max_wait") double max_wait,
                 @JsonProperty("pick_up_time") long pickupTime,
                 @JsonProperty("cooking_time") long cooking_time,
                 @JsonProperty("cooking_details") ArrayList<HashMap<String, Integer>> cooking_details) {
        this.id = order_id;
        this.table_id = table_id;
        this.waiter_id = waiter_id;
        this.items = items;
        this.priority = priority;
        this.max_wait = max_wait;
        this.pickupTime = pickupTime;
        this.cooking_time = cooking_time;
        this.cooking_details = cooking_details;

        //this.generalPriority = pickupTime - priority;
    }

    public void setPickupTime() {
        pickupTimeNs = System.nanoTime();
        pickupTime = TimeUnit.SECONDS.convert(pickupTimeNs, TimeUnit.NANOSECONDS);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", items=" + items +
                ", priority=" + priority +
                ", max_wait=" + max_wait +
                ", cooking_time=" + cooking_time +
                ", cooking_details=" + cooking_details +
                '}';
    }
}
