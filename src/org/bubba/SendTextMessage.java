package org.bubba;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.telephony.SmsManager;

public class SendTextMessage
{
	String[] parseTxtMsgNbrs(String readTxtMsgNbrs)
	{
		String[] nbrs = new String[]{" ", " ", " "};
		
		if(null == readTxtMsgNbrs || "".equals(readTxtMsgNbrs.trim()))
		{
			return nbrs;
		}
		
		StringTokenizer st = new StringTokenizer(readTxtMsgNbrs, "|");
		
		for (int i = 0; i < 3; i++)
		{
			if(st.hasMoreElements())
			{
				nbrs[i] = st.nextToken();
			}
			else
			{
				nbrs[i] = " ";
			}
		}
				
		return nbrs;
	}
	
	CharSequence readTextMsgNumbersFile(BubbasGroceryListAppActivity act)
	{
		StringBuffer record = new StringBuffer();
		
		try
		{
	    	int ch = 0;
			FileInputStream fis = act.openFileInput("tstMsgNbrlist.txt");
			while( (ch = fis.read()) != -1)
	        {
	        	record.append((char)ch);
	        }
	    	fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(record.length() ==0)
		{
			record.append(" | | ");
		}
		
    	return record;
	}
	
	void sendTextMsg(String nbr, BubbasGroceryListAppActivity act, ArrayList<ItemLoc> list)
	{
		SmsManager sms = SmsManager.getDefault();
		
		StringBuffer sb = new StringBuffer(list.size());
		sb.append("\n");
        ItemLoc ele;
        
        for (int i = 0; i < list.size(); i++)
		{
			ele = list.get(i);
            sb.append(ele.toString() + "\n");
		}
        
		String string = sb.toString();
		sms.sendTextMessage(nbr, null, string, null, null);
	}
}
