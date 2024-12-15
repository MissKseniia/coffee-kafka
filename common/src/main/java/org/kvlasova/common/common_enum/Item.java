package org.kvlasova.common.common_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Item {
    
    LATTE("латте", 250),
    CAPPUCCINO("каппучино", 270),
    MOCHA("мокко", 290),
    FLAT_WHITE("флэт уайт", 260),
    RAF("раф", 280);

    private final String itemName;
    private final Integer itemPrice;

    public Map<String, Item> getItemsMap() {
        return Arrays.stream(Item.values()).collect(Collectors.toMap(Item::getItemName, Function.identity()));
    }
}
