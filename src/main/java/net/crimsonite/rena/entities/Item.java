/*
 * Copyright (C) 2020-2021  Nhalrath
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.crimsonite.rena.entities;

import java.io.IOException;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.crimsonite.rena.core.I18n;

public class Item {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final String itemName;
    private final String description;
    private final String tier;
    private final String action;
    private final String primaryTargetActionField;
    private final String subTargetActionField;
    private final int primaryTargetValue;
    private final int subTargetValue;
    private final boolean isConsumable;

    /**
     * @param userId (Nullable) The Discord ID of the user. It is used to get the locale for the name and description.
     * @param itemId The ID of the item.
     * @throws IOException if the provided item ID doesn't exist.
     */
    public Item(String itemId, @Nullable String userId) throws IOException {
        System.out.println(itemId);
        JsonNode itemData = mapper.readTree(getClass().getClassLoader().getResourceAsStream("assets/items.json")).get(itemId);

        System.out.println(itemData.asText());

        this.tier = itemData.get("TIER").asText();
        this.action = itemData.get("USE").get("ACTION").asText();
        this.primaryTargetActionField = itemData.get("USE").get("TARGET_FIELD_0").asText();
        this.subTargetActionField = itemData.get("USE").get("TARGET_FIELD_1").asText();
        this.primaryTargetValue = itemData.get("USE").get("VALUE_0").asInt();
        this.subTargetValue = itemData.get("USE").get("VALUE_1").asInt();
        this.isConsumable = itemData.get("CONSUMABLE").asBoolean();

        if (userId == null) {
            this.itemName = I18n.getMessage(itemData.get("NAME_KEY").asText());
            this.description = I18n.getMessage(itemData.get("DESCRIPTION_KEY").asText());
        } else {
            this.itemName = I18n.getMessage(userId, itemData.get("NAME_KEY").asText());
            this.description = I18n.getMessage(userId, itemData.get("DESCRIPTION_KEY").asText());
        }
    }

    /**
     * @return Name of the item. (Defaults in English)
     */
    public String getItemName() {
        return this.itemName;
    }

    /**
     * @return Description of the item. (Defaults in English)
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return Tier of the item.
     */
    public String getTier() {
        return this.tier;
    }

    /**
     * @return Effect of the item when used.
     */
    public String getAction() {
        return this.action;
    }

    public String getPrimaryTargetActionField() {
        return this.primaryTargetActionField;
    }

    public String getSubTargetActionField() {
        return this.subTargetActionField;
    }

    public int getPrimaryTargetValue() {
        return this.primaryTargetValue;
    }

    public int getSubTargetValue() {
        return this.subTargetValue;
    }

    /**
     * @return true if the item is consumable.
     */
    public boolean isConsumable() {
        return this.isConsumable;
    }

}
