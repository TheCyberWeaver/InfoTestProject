package io.github.infotest.util;

import io.github.infotest.item.Apple;
import io.github.infotest.item.Item;

public class ItemFactory {
    /**
     * Creates an Item instance based on the itemName string.
     * You can expand this as needed for more item types.
     *
     * @param itemName The string representing the item (e.g., "Sword", "Shield", etc.)
     * @return An instance of a class extending Item
     * @throws IllegalArgumentException if the item name is not recognized
     */
    public static Item createItem(String itemName) {
        switch (itemName) {
            case "Apple":
                return new Apple();
            // 如果有更多类型，继续加 case ...
            default:
                throw new IllegalArgumentException("Unknown item: " + itemName);
        }
    }
}
