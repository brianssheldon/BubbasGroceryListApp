package org.bubba.bubbasgrocerylist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.Log;

public class KnownItemUtils
{
	private String KNOWN_ITEMS_FILENAME = "knownitems.txt";
	
	void saveFile(ArrayList<ItemLoc> knownItemsList, Context context)
	{
    	try
    	{
    		if(knownItemsList.size() > 1
    				&& knownItemsList.contains(new ItemLoc("empty","1", "1")))
    		{
    			knownItemsList.remove(new ItemLoc("empty","1", "1"));
	    	}
			
    		FileOutputStream fos = context.openFileOutput(KNOWN_ITEMS_FILENAME, Context.MODE_PRIVATE);
    		ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(knownItemsList);
			out.close();
	    	fos.close();
    	}
    	catch(Exception e)
    	{
    		Log.getStackTraceString(e);
    	}
	}

	void makeSureKnownItemsFileExists(Context context)
	{
		try
		{
	        FileInputStream fis = context.openFileInput(KNOWN_ITEMS_FILENAME);
	    	fis.close();
		}
		catch(Exception e)
		{
			try
			{
				
				ArrayList<ItemLoc> list = new ArrayList<ItemLoc>();
				list.add(new ItemLoc("empty", "1", "1"));
				
	    		FileOutputStream fos = context.openFileOutput(KNOWN_ITEMS_FILENAME, Context.MODE_PRIVATE);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(list);
				out.close();
		    	fos.close();
			}
			catch(Exception e2)
	    	{
				// eeek - can't create file
	    	}
		}
	}

	ArrayList<ItemLoc> readKnownItemsListFile(Context context)
	{
		ArrayList<ItemLoc> arrayList = new ArrayList<ItemLoc>();
	
		try
		{
	    	FileInputStream fix = context.openFileInput(KNOWN_ITEMS_FILENAME);
	    	ObjectInputStream in = new ObjectInputStream(fix);
	    	arrayList = (ArrayList<ItemLoc>) in.readObject();
		}
		catch(Exception e){}
		
		if(arrayList.size() == 0)
		{
			arrayList.add(new ItemLoc("empty","1", "1"));
		}
		
		Collections.sort(arrayList);
		
		return arrayList;
	}

	public String[] readKnownItemsListFileAsArray(Context context)
	{
		ArrayList<ItemLoc> list = readKnownItemsListFile(context);
		String[] array = new String[list.size()];
		ItemLoc loc = null;
		
		for (int i = 0; i < list.size(); i++)
		{
			loc = list.get(i);
			array[i] = loc.getItem() + "," + loc.getAisle();
		}
		
		return array;
	}

//	void removeTrailingSpacesAndNumbers(StringBuffer cbDesc)
//	{
//		boolean charFound = false;
//		
//		while (!charFound)
//		{
//			char x = cbDesc.charAt(cbDesc.length() - 1);
//			
//			if(x == ' ')
//			{
//				cbDesc.deleteCharAt(cbDesc.length() - 1);
//				continue;
//			}
//			if(isNumeric(x))
//			{
//				cbDesc.deleteCharAt(cbDesc.length() - 1);
//				continue;
//			}
//			charFound = true;
//		}
//	}
//
//	void removeLeadingSpacesAndNumbers(StringBuffer cbDesc) 
//	{
//		boolean charFound = false;
//		
//		while (!charFound)
//		{
//			if(cbDesc.charAt(0) == ' ')
//			{
//				cbDesc.deleteCharAt(0);
//				continue;
//			}
//			if(isNumeric(cbDesc.charAt(0)))
//			{
//				cbDesc.deleteCharAt(0);
//				continue;
//			}
//			charFound = true;
//		}
//	}
//
//	boolean isNumeric(char x)
//	{
//		return x == '0'
//				|| x == '1'
//				|| x == '2'
//				|| x == '3'
//				|| x == '4'
//				|| x == '5'
//				|| x == '6'
//				|| x == '7'
//				|| x == '8'
//				|| x == '9';
//	}
}