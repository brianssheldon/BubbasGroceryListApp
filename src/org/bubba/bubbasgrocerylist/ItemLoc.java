package org.bubba.bubbasgrocerylist;

import java.io.Serializable;


public class ItemLoc implements Comparable<Object>, Serializable
{
	private static final long serialVersionUID = 666L;
	private String aisle;
	private String item;
	private String quantity;
	
	public ItemLoc(){}
	public ItemLoc(String item, String aisle)
	{
		this.item = item.replace('|', ' ');
		this.aisle = aisle.replace('|', ' ');
		this.quantity = "1";
	}

	public ItemLoc(String item, String aisle, String quantity)
	{
		this.item = item.replace('|', ' ');
		this.aisle = aisle.replace('|', ' ');
		this.quantity = quantity.replace('|', ' ');
	}
	
	public String toString()
	{
		if(aisle.length() > 3)
		{
			return "";
		}
		return "   ".substring(0, 3 - aisle.length()) + aisle + "    " + item + "    " + quantity;
	}
	
	public int compareTo(Object another)
	{
		String a = "000".substring(0, 3 - aisle.length()) + aisle;
		
		String b = ((ItemLoc)another).getAisle();
		b = "000".substring(0, 3 - b.length()) + b;
		
		return a.compareTo(b);
	}
	
	@Override
	public boolean equals(Object o)
	{
		ItemLoc other = (ItemLoc)o;
		
		return aisle.equals(other.getAisle())
				&& item.equals(other.getItem())
				&& quantity.equals(other.getQuantity());
	}
	
	public String getAisle()
	{
		return aisle;
	}
	public void setAisle(String aisle)
	{
		this.aisle = aisle;
	}
	public String getItem()
	{
		return item;
	}
	public void setItem(String item)
	{
		this.item = item;
	}
	public String getQuantity()
	{
		return quantity;
	}
	public void setQuantity(String quantity)
	{
		this.quantity = quantity;
	}
}
