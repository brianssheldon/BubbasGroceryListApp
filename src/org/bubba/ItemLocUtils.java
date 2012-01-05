package org.bubba;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;

public class ItemLocUtils
{
	String[] getGroceryList_As_StringArray(StringBuffer sb)
	{
    	int index = 0;
    	
    	StringTokenizer st = new StringTokenizer(sb.toString(), "|");
    	String[] stringArray = new String[st.countTokens()];
    	
    	while(st.hasMoreElements())
    	{
    		stringArray[index] = st.nextToken();
    		index = index + 1;
    	}
    	
    	return stringArray;
	}
	
	ArrayList<ItemLoc> getGroceryList_As_ArrayBuffer(String[] stringArray)
	{
		ArrayList<ItemLoc> al = new ArrayList<ItemLoc>();
    	
		for (int i = 0; i < stringArray.length; i++)
		{
			String string = stringArray[i];
			
			int index = string.indexOf(",");
			
			if(index < 0) string = string + ",99";
			
			index = string.indexOf(",");
			
			al.add(new ItemLoc(
					string.substring(0, index),
					string.substring(index + 1)));
		}
    	
    	return al;
	}
	
	int getSizeOfGroceryList(String[] groceryList)
	{
		int size = 0;
		
		for (int i = 0; i < groceryList.length; i++)
		{
			if(groceryList[i] != null)
			{
				size = i;
			}
			else
			{
				break;
			}
		}
		return size;
	}

	StringBuffer getSortedStringBuffer(ArrayList<ItemLoc> arrayList)
	{
		StringBuffer sb = new StringBuffer();
		Collections.sort(arrayList);
		
		for (Iterator<ItemLoc> iterator = arrayList.iterator(); iterator.hasNext();)
		{
			sb.append(iterator.next().toString() + "\n");
		}
		return sb;
	}
	
	void saveFile(String[] groceryList, Context context)
	{
		String FILENAME = "grocerylist.txt";
    	try
    	{
    		StringBuffer sb = new StringBuffer();
    		for (int i = 0; i < groceryList.length; i++)
			{
    			if(groceryList[i] != null)
    			{
    				sb.append(groceryList[i] + "|");
    			}
			}
    		
	    	FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
	    	fos.write(sb.toString().getBytes());
	    	fos.close();
    	}
    	catch(Exception e)
    	{
    		Log.getStackTraceString(e);
    	}
	}
}