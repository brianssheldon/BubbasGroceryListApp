package org.bubba;


public class ItemLoc implements Comparable<Object>
{
	private String aisle;
	private String item;
	
	public ItemLoc(){}
	public ItemLoc(String item, String aisle)
	{
		this.item = item.replace('|', ' ');
		this.aisle = aisle.replace('|', ' ');
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
	
	public String toString()
	{
		if(aisle.length() > 3)
		{
			return "";
		}
		return "   ".substring(0, 3 - aisle.length()) + aisle + "    " + item;
	}
	
	public int compareTo(Object another)
	{
		String a = "000".substring(0, 3 - aisle.length()) + aisle;
		
		String b = ((ItemLoc)another).getAisle();
		b = "000".substring(0, 3 - b.length()) + b;
		
		return a.compareTo(b);
	}
}
