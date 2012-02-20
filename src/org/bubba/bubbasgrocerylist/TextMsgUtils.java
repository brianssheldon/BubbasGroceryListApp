package org.bubba.bubbasgrocerylist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import android.content.Context;

public class TextMsgUtils
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
				nbrs[i] = st.nextToken().trim();
			}
			else
			{
				nbrs[i] = " ";
			}
		}
				
		return nbrs;
	}
	
	CharSequence readTextMsgNumbersFile(Context context)
	{
		StringBuffer record = new StringBuffer();
		
		try
		{
	    	int ch = 0;
			FileInputStream fis = context.openFileInput("tstMsgNbrlist.txt");
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
}
