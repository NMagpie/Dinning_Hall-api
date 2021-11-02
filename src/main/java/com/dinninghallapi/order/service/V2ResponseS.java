package com.dinninghallapi.order.service;

import com.dinninghallapi.DinningHallApiApplication;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class V2ResponseS extends StdSerializer<OrderService> {
    public V2ResponseS() {
        this(null);
    }

    public V2ResponseS(Class<OrderService> serviceClass) {
        super((serviceClass));
    }

    @Override
    public void serialize(OrderService orderService, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("restaurant_id", DinningHallApiApplication.getRestaurant().getId());

        jsonGenerator.writeNumberField("order_id", orderService.getId());

        jsonGenerator.writeNumberField("estimated_waiting_time", orderService.getEstimatedWaitingTime());

        jsonGenerator.writeNumberField("created_time", orderService.getCreatedTime());

        jsonGenerator.writeNumberField("registered_time", orderService.getRegisteredTime());

        jsonGenerator.writeEndObject();

    }
}
