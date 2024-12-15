package org.kvlasova.common.entity;

import org.kvlasova.common.common_enum.Item;
import org.kvlasova.common.common_enum.OrderStatus;

import java.util.List;

public record Order(
        String orderNumber,
        Integer orderTotalAmount,
        OrderStatus orderStatus,
        List<Item> orderItems
) {
}
