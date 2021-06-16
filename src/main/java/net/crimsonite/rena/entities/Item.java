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

public class Item {
	
	private String itemName;
	private String description;
	private String tier;
	private String action;
	private String primaryTargetActionField;
	private String subTargetActionField;
	private int primaryTargetValue;
	private int subTargetValue;
	private boolean isConsumable;
	
	public Item(String itemId) {
		
	}
	
	public String getItemName() {
		return this.itemName; 
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getTier() {
		return this.tier;
	}
	
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
	
	public boolean isConsumable() {
		return this.isConsumable;
	}
	
}
