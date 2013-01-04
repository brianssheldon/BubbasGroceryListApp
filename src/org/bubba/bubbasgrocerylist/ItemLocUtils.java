package org.bubba.bubbasgrocerylist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.Log;

public class ItemLocUtils
{
	private String FILENAME = "grocerylist.txt";
	
	void saveFile(ArrayList<ItemLoc> groceryList, Context context)
	{
    	try
    	{
    		if(groceryList.size() > 1
    				&& groceryList.contains(new ItemLoc("empty","1", "1")))
    		{
	    		groceryList.remove(new ItemLoc("empty","1", "1"));
	    	}
			
    		FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
//			fos.write(buffer);
    		ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(groceryList);
			out.close();
	    	fos.close();
    	}
    	catch(Exception e)
    	{
    		Log.getStackTraceString(e);
    	}
	}

	void makeSureGroceryFileExists(Context context)
	{
		try
		{
	        FileInputStream fis = context.openFileInput(FILENAME);
	    	fis.close();
		}
		catch(Exception e)
		{
			try
			{
				
				ArrayList<ItemLoc> list = new ArrayList<ItemLoc>();
				list.add(new ItemLoc("empty", "1", "1"));
				
	    		FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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

	ArrayList<ItemLoc> readGroceryListFile(Context context)
	{
		ArrayList<ItemLoc> arrayList = new ArrayList<ItemLoc>();
	
		try
		{
	    	FileInputStream fix = context.openFileInput(FILENAME);
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

	String[] getKnownItemsArray(Context context)
	{
		String[] hardCodedItems = context.getResources().getStringArray(R.array.food_array);
		String[] scannedItems = (new KnownItemUtils()).readKnownItemsListFileAsArray(context);

		int length = hardCodedItems.length;
		int length2 = scannedItems.length;

		String[] newArray = new String[length + length2];
		ArrayList<String> newList = new ArrayList();

		for (int i = 0; i < length; i++)
		{
			newArray[i] = hardCodedItems[i];
			newList.add(hardCodedItems[i]);
		}
		
		int x = 0;
		for (int i = length; i < length + scannedItems.length; i++)
		{
			if(scannedItems[x].contains("empty, 1")) break;
			
			newArray[i] = scannedItems[x];
			newList.add(scannedItems[x]);
			x += 1;
		}
		
		Collections.sort(newList);
		newArray = (String[]) newList.toArray(newArray);
		return newArray;
	}
	
	void removeTrailingSpacesAndNumbers(StringBuffer cbDesc)
	{
		boolean charFound = false;
		
		while (!charFound)
		{
			char x = cbDesc.charAt(cbDesc.length() - 1);
			
			if(x == ' ')
			{
				cbDesc.deleteCharAt(cbDesc.length() - 1);
				continue;
			}
			if(isNumeric(x))
			{
				cbDesc.deleteCharAt(cbDesc.length() - 1);
				continue;
			}
			charFound = true;
		}
	}

	void removeLeadingSpacesAndNumbers(StringBuffer cbDesc) 
	{
		boolean charFound = false;
		
		while (!charFound)
		{
			if(cbDesc.charAt(0) == ' ')
			{
				cbDesc.deleteCharAt(0);
				continue;
			}
			if(isNumeric(cbDesc.charAt(0)))
			{
				cbDesc.deleteCharAt(0);
				continue;
			}
			charFound = true;
		}
	}

	boolean isNumeric(char x)
	{
		return x == '0'
				|| x == '1'
				|| x == '2'
				|| x == '3'
				|| x == '4'
				|| x == '5'
				|| x == '6'
				|| x == '7'
				|| x == '8'
				|| x == '9';
	}
}