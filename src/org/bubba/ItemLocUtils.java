package org.bubba;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ItemLocUtils
{
	private String FILENAME = "grocerylist.txt";
//	String[] getGroceryList_As_StringArray(StringBuffer sb)
//	{
//    	int index = 0;
//    	
////    	sb = new StringBuffer("cheddar - block,0,1|cheddar - shredded,0,1|chicken nuggets,12,1|");
//    	
//    	StringTokenizer st = new StringTokenizer(sb.toString(), "|");
//    	String[] stringArray = new String[st.countTokens()];
//    	// 'cheddar - block,0|cheddar - shredded,0|chicken nuggets,12|'
//    	
//    	while(st.hasMoreElements())
//    	{
//    		stringArray[index] = st.nextToken();
//    		index = index + 1;
//    	}
//    	
//    	return stringArray;
//	}

//	ArrayList<ItemLoc> getGroceryList_As_ArrayBuffer(String[] stringArray)
//	{
//		ArrayList<ItemLoc> al = new ArrayList<ItemLoc>();
//    	
//		for (int i = 0; i < stringArray.length; i++)
//		{
//			String string = stringArray[i];
//			
//			StringTokenizer st = new StringTokenizer(string, ",");
//			int nbrOfTokens = st.countTokens();
//			
//			if(nbrOfTokens == 0)
//			{
//				string = string + "empty,99,1";
//			}
//			else if(nbrOfTokens == 1)
//			{
//				string = string + ",99,1";
//			}
//			else if(nbrOfTokens == 2)
//			{
//				string = string + ",1";
//			}
//			
//			st = new StringTokenizer(string, ",");
//			
////			int index = string.indexOf(",");
////			if(index < 0) string = string + ",99";
////			index = string.indexOf(",");
////			
////			int index2 = string.substring(index + 1).indexOf(",");
////			if(index2 < 0) string = string + ",1";
////			index2 = string.substring(index + 1).indexOf(",");
//			
//			al.add(new ItemLoc(st.nextToken(),st.nextToken(),st.nextToken()));
//		}
//    	
//    	return al;
//	}
	
//	int getSizeOfGroceryList(String[] groceryList)
//	{
//		int size = 0;
//		
//		for (int i = 0; i < groceryList.length; i++)
//		{
//			if(groceryList[i] != null)
//			{
//				size = i;
//			}
//			else
//			{
//				break;
//			}
//		}
//		return size;
//	}

//	ArrayList getSortedStringBuffer(ArrayList<ItemLoc> arrayList)
//	{
//		StringBuffer sb = new StringBuffer();
//		Collections.sort(arrayList);
//		return arrayList;
////		for (Iterator<ItemLoc> iterator = arrayList.iterator(); iterator.hasNext();)
////		{
////			sb.append(iterator.next().toString() + "\n");
////		}
////		return sb;
//	}

//	String[] getSortedArray(ArrayList<ItemLoc> arrayList)
//	{
//		String[] sa = new String[arrayList.size()];
//		Collections.sort(arrayList);
//		
//		for (int i = 0; i < arrayList.size(); i++)
//		{
//			sa[i] = arrayList.get(i).getItem() + ","
//						+ arrayList.get(i).getAisle() + ","
//						+ arrayList.get(i).getQuantity();
//		}
//		
//		return sa;
//	}
	
	void saveFile(ArrayList<ItemLoc> groceryList, Context context)
	{
    	try
    	{
//			ArrayList<ItemLoc> list = new ArrayList<ItemLoc>();
//			list.add(new ItemLoc("veLveeta", "1", "1"));
//			list.add(new ItemLoc("taco shells", "7", "1"));
//			list.add(new ItemLoc("milk", "6", "3"));
			
    		FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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

	void makeSureGroceryFileExists(BubbasGroceryListAppActivity bubbasGroceryListAppActivity)//, String FILENAME)
	{
		try
		{
	        FileInputStream fis = bubbasGroceryListAppActivity.openFileInput(FILENAME);
	    	fis.close();
		}
		catch(Exception e)
		{
			try
			{
				ArrayList<ItemLoc> list = new ArrayList<ItemLoc>();
				list.add(new ItemLoc("empty", "1", "1"));
				
	    		FileOutputStream fos = bubbasGroceryListAppActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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

	ArrayList<ItemLoc> readGroceryListFile(BubbasGroceryListAppActivity bubbasGroceryListAppActivity)
	{
		ArrayList<ItemLoc> arrayList = new ArrayList<ItemLoc>();
		
		makeSureGroceryFileExists(bubbasGroceryListAppActivity);
	
		try
		{
	    	FileInputStream fix = bubbasGroceryListAppActivity.openFileInput(FILENAME);
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
			if(x == '0'
					|| x == '1'
					|| x == '2'
					|| x == '3'
					|| x == '4'
					|| x == '5'
					|| x == '6'
					|| x == '7'
					|| x == '8'
					|| x == '9')
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
			if(cbDesc.charAt(0) == '0'
					|| cbDesc.charAt(0) == '1'
					|| cbDesc.charAt(0) == '2'
					|| cbDesc.charAt(0) == '3'
					|| cbDesc.charAt(0) == '4'
					|| cbDesc.charAt(0) == '5'
					|| cbDesc.charAt(0) == '6'
					|| cbDesc.charAt(0) == '7'
					|| cbDesc.charAt(0) == '8'
					|| cbDesc.charAt(0) == '9')
			{
				cbDesc.deleteCharAt(0);
				continue;
			}
			charFound = true;
		}
	}
}