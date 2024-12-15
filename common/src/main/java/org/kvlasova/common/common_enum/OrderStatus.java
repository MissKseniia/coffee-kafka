package org.kvlasova.common.common_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    IS_NOT_PAID("Не оплачен"),
    IS_PAID("Оплачен");

    private final String orderStatusDescription;
}
