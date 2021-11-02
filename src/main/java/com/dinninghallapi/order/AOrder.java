package com.dinninghallapi.order;

import com.dinninghallapi.order.service.OrderService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@JsonSubTypes({
        @JsonSubTypes.Type(value = Order.class, name = "order"),
        @JsonSubTypes.Type(value = OrderService.class, name = "orderService")
})
public abstract class AOrder {
    @Getter
    protected static int count = 0;

    @JsonProperty("order_id")
    @Getter
    protected int id;

    @JsonProperty("items")
    @Getter
    protected ArrayList<Integer> items = new ArrayList<>();

    @JsonProperty("priority")
    @Getter
    protected int priority;

    @JsonProperty("max_wait")
    @Getter
    protected double max_wait;

    @JsonProperty(value = "cooking_time", access = JsonProperty.Access.WRITE_ONLY)
    @Getter
    @Setter
    protected long cooking_time = 0;

    @JsonProperty(value = "cooking_details", access = JsonProperty.Access.WRITE_ONLY)
    @Getter
    @Setter
    protected ArrayList<HashMap<String, Integer>> cooking_details = null;
}
