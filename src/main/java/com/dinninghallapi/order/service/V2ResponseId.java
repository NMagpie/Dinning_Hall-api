package com.dinninghallapi.order.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class V2ResponseId extends StdSerializer<OrderService> {
    public V2ResponseId() {
        this(null);
    }

    public V2ResponseId(Class<OrderService> serviceClass) {
        super((serviceClass));
    }

    @Override
    public void serialize(OrderService orderService, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("order_id", orderService.getId());

        jsonGenerator.writeBooleanField("is_ready", orderService.isReady());

        jsonGenerator.writeNumberField("estimated_waiting_time", orderService.getEstimatedWaitingTime());

        jsonGenerator.writeNumberField("priority", orderService.getPriority());

        jsonGenerator.writeNumberField("max_wait", orderService.getMax_wait());

        jsonGenerator.writeNumberField("created_time", orderService.getCreatedTime());

        jsonGenerator.writeNumberField("registered_time", orderService.getRegisteredTime());

        jsonGenerator.writeNumberField("prepared_time", orderService.getPreparedTime());

        jsonGenerator.writeNumberField("cooking_time", orderService.getCooking_time());

        jsonGenerator.writeObjectField("cooking_details", orderService.getCooking_details());

        jsonGenerator.writeEndObject();

    }
}
