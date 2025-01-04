package com.kvlasova.kstream.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.kvlasova.common.entity.Order;

public class OrderSerde implements Serde<Order> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void close() {
        Serde.super.close();
    }

    @Override
    public Serializer<Order> serializer() {
        return (topic, order) -> {
            try {
                return order != null ? objectMapper.writeValueAsBytes(order) : null;
            } catch (Exception e) {
                throw new RuntimeException("Error serializing Order", e);
            }
        };
    }

    @Override
    public Deserializer<Order> deserializer() {
        return (topic, data) -> {
            try {
                return data != null ? objectMapper.readValue(data, Order.class) : null;
            } catch (Exception e) {
                throw new RuntimeException("Error deserializing Order", e);
            }
        };
    }
}
