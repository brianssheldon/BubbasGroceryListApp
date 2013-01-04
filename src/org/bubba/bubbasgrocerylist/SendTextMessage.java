package org.bubba.bubbasgrocerylist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

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
	
	@SuppressLint("NewApi")
	void sendTextMsg(String nbr, final BubbasGroceryListAppActivity act, ArrayList<ItemLoc> list)
	{
		SmsManager sms = SmsManager.getDefault();
		
		StringBuffer sb = new StringBuffer(153);
		sb.append("\n");
        ItemLoc ele;

		PendingIntent sentPI = PendingIntent.getBroadcast(act.getApplicationContext(), 0, new Intent("sent"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(act.getApplicationContext(), 0, new Intent("delivered"), 0);
        
        act.registerReceiver(new BroadcastReceiver()
        {
			@Override
			public void onReceive(Context context, Intent arg1)
			{
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(context.getApplicationContext(), "message sent", Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(context.getApplicationContext(), "Generic failure", Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(context.getApplicationContext(), "No service", Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(context.getApplicationContext(), "Null PDU", Toast.LENGTH_SHORT).show();
					break;
					
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(context.getApplicationContext(), "Radio off", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		},  new IntentFilter("sent"));
        
		act.registerReceiver(new BroadcastReceiver(){
		    @Override
		    public void onReceive(Context context, Intent arg1) {
		        switch (getResultCode())
		        {
		            case Activity.RESULT_OK:
		                Toast.makeText(context.getApplicationContext(), "message delivered", 
		                        Toast.LENGTH_SHORT).show();
		                break;
		            case Activity.RESULT_CANCELED:
		                Toast.makeText(context.getApplicationContext(), "message not delivered", 
		                        Toast.LENGTH_SHORT).show();
		                break;                        
		        }
		    }
		}, new IntentFilter("delivered"));
		
        for (int i = 0; i < list.size(); i++)
		{
			ele = list.get(i);
			
			if(150 < sb.length() + ele.toString().length())
			{
				sms.sendTextMessage(nbr, null, sb.toString(), sentPI, deliveredPI);
				sb = new StringBuffer(153);
				sb.append("\n");
			}
            sb.append(ele.toString() + "\n");
		}

        
        sms.sendTextMessage(nbr, null, sb.toString(), sentPI, deliveredPI);
	}
}
