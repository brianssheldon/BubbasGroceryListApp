package org.bubba;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BubbasGroceryListAppActivity extends Activity
{
	private ItemLocUtils utils;
	private static AutoCompleteTextView textView;
	private String[] groceryList = new String[100];
	private LinearLayout ll;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        utils = new ItemLocUtils();
    
        ScrollView sv = new ScrollView(this);

        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll, 0);
        
        LinearLayout editAndAddLL = new LinearLayout(this);
        editAndAddLL.setOrientation(LinearLayout.HORIZONTAL);
        
	        textView = new AutoCompleteTextView(this);
	        textView.setWidth(350);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    	this,
		    	R.layout.list_item,
		    	getResources().getStringArray(R.array.food_array));
		    textView.setAdapter(adapter);
		    textView.setOnItemClickListener(new OnitemClick());
		    editAndAddLL.addView(textView, 0);
		    
		    Button addButton = new Button(this);
		    addButton.setText("Addx");
		    addButton.setWidth(90);
		    addButton.setOnClickListener(btnAddOnClick);
		    editAndAddLL.addView(addButton, 1);
	    
	    ll.addView(editAndAddLL, 0);
        
        addCheckBoxesFromGroceryFile();
        
        this.setContentView(sv);
    }

	private void addCheckBoxesFromGroceryFile()
	{
		while (ll.getChildCount() > 1)
		{
			ll.removeViewAt(1);
		}
		
		LinearLayout row;
		TextView itemDesc;
		
		ArrayList<ItemLoc> list = readGroceryListFile();
        ItemLoc ele;
        
        for (int i = 0; i < list.size(); i++)
		{
        	row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);
			
			ele = list.get(i);
			
			if(!"".equals(ele.toString()))
			{			
				CheckBox cb = new CheckBox(this);
	            cb.setId(i);
	            cb.setText(ele.toString());
	            cb.setSoundEffectsEnabled(true);
	            cb.setButtonDrawable(R.drawable.trashcan);
				cb.setOnCheckedChangeListener(new DeleteRowListener());
				
				row.addView(cb);
				
//				itemDesc = new TextView(this);
//				itemDesc.setText(ele.toString());
//				row.addView(itemDesc);S
				
	            ll.addView(row);
			}
			else
			{
				String x = "";
			}
		}
	}
    
	private ArrayList<ItemLoc> readGroceryListFile()
	{
    	StringBuffer sb = new StringBuffer();
    	ArrayList<ItemLoc> arrayList = new ArrayList<ItemLoc>();
    	String FILENAME = "grocerylist.txt";
    	
		try
    	{
            FileInputStream fis = openFileInput(FILENAME);
	    	fis.close();
    	}
    	catch(Exception e)
    	{
    		try
    		{
	    		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		    	fos.write("".getBytes());
		    	fos.close();
    		}
    		catch(Exception e2)
        	{
    			// eeek - can't create file
        	}
    	}

		try
    	{
	    	FileInputStream fis = openFileInput(FILENAME);
	    	
	    	int ch = 0;
	    	
	        while( (ch = fis.read()) != -1)
	        {
	        	sb.append((char)ch);
	        }
	    	fis.close();
	    	
	    	groceryList = utils.getGroceryList_As_StringArray(sb);
	    	arrayList = utils.getGroceryList_As_ArrayBuffer(groceryList);
    	}
    	catch(Exception e){}
    	
    	if(arrayList.size() == 0)
    	{
    		arrayList.add(new ItemLoc("empty","1"));
    	}
    	
		Collections.sort(arrayList);
    	
    	return arrayList;
	}

    // Click listener for the Add button.
    private final Button.OnClickListener btnAddOnClick = new Button.OnClickListener() 
    {
        public void onClick(View v) 
        {
			String name = ((TextView)textView).getText().toString();
			String[] newArray;
			
			if(groceryList.length == 0)
			{
				groceryList = new String[]{name};
				newArray = groceryList;
			}
			else
			{
				int size = utils.getSizeOfGroceryList(groceryList) + 2;
				newArray = new String[size];
				System.arraycopy(groceryList, 0, newArray, 0, groceryList.length);
				newArray[size - 1] = name;
			}
			
			groceryList = newArray;
			ArrayList<ItemLoc> sortedArray = utils.getGroceryList_As_ArrayBuffer(newArray);
//			StringBuffer sb = utils.getSortedStringBuffer(sortedArray);
			
			utils.saveFile(newArray, v.getContext());

			addCheckBoxesFromGroceryFile();
			
			LinearLayout linearLayout = (LinearLayout) ll.getChildAt(0);
			AutoCompleteTextView atv = (AutoCompleteTextView) linearLayout.getChildAt(0);
			atv.setText("");
        }
    };
    
	private final class DeleteRowListener implements OnCheckedChangeListener
	{
		public void onCheckedChanged(CompoundButton arg0, boolean arg1)
		{
			String deleteThisOne = arg0.getText().toString().substring(7).trim();
			
			String[] newList = new String[groceryList.length - 1];
			int j = 0;
			
			for (int i = 0; i < groceryList.length; i++)
			{
				if(groceryList[i].startsWith(deleteThisOne))
				{ // skip this row
				}
				else
				{
					newList[j] = groceryList[i];
					j = j + 1;
				}
			}
			
			groceryList = newList;
			ItemLocUtils utils = new ItemLocUtils();
			utils.saveFile(groceryList, arg0.getContext());
			
			addCheckBoxesFromGroceryFile();
		}
	}
	
    private final class OnitemClick implements OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View textView, int position, long id)
		{
			String name = ((TextView)textView).getText().toString();
			String[] newArray;
			Context context = parent.getContext();
			
			if(groceryList.length == 0)
			{
				groceryList = new String[]{name};
				newArray = groceryList;
			}
			else
			{
				int size = utils.getSizeOfGroceryList(groceryList) + 2;
				newArray = new String[size];
				System.arraycopy(groceryList, 0, newArray, 0, groceryList.length);
				newArray[size - 1] = name;
				groceryList = newArray;
			}
			
			ArrayList<ItemLoc> sortedArray = utils.getGroceryList_As_ArrayBuffer(newArray);
			Collections.sort(sortedArray);
			
			LinearLayout linearLayout = (LinearLayout) ll.getChildAt(0);
			AutoCompleteTextView atv = (AutoCompleteTextView) linearLayout.getChildAt(0);
			atv.setText("");
	        
	        utils.saveFile(groceryList, context);
	        
	        addCheckBoxesFromGroceryFile();
		}
	}
    
    // ---------------------------------------------menu ---------------------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu); 
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {
		    case R.id.textgrocerylist1:
		    	sendTextMsg(getTxtPhoneNbr(0));
		    	return true;
		    	
		    case R.id.textgrocerylist2:
		    	sendTextMsg(getTxtPhoneNbr(1));
		    	return true;
		    	
		    case R.id.textgrocerylist3:
		    	sendTextMsg(getTxtPhoneNbr(2));
		    	return true;
		    	
//		    case R.id.cleargrocerylist:
//		    	txt1.setText("");
//		    	
//		    	for (int i = 0; i < groceryList.length; i++)
//				{
//		    		groceryList = new String[]{};
//				}
//		    	
//		    	return true;
		    case R.id.editTextMsgNbrlist:
                Intent myIntent = new Intent(this, EditTextMsgNumbersActivity.class);
                startActivityForResult(myIntent, 0);
		    	
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

	private String getTxtPhoneNbr(int i)
	{
		String[] parsedTxtMsgNbrs = parseTxtMsgNbrs(readTextMsgNumbersFile().toString());
		
		return parsedTxtMsgNbrs[i];
	}

	private String[] parseTxtMsgNbrs(String readTxtMsgNbrs)
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
	
	private CharSequence readTextMsgNumbersFile()
	{
		StringBuffer record = new StringBuffer();
		
		try
		{
	    	int ch = 0;
			FileInputStream fis = openFileInput("tstMsgNbrlist.txt");
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
	
	private void sendTextMsg(String phonenbr)
	{             
		SmsManager sms = SmsManager.getDefault();
		
		ArrayList<ItemLoc> list = readGroceryListFile();
		StringBuffer sb = new StringBuffer(list.size());
		sb.append("\n");
        ItemLoc ele;
        
        for (int i = 0; i < list.size(); i++)
		{
			ele = list.get(i);
            sb.append(ele.toString() + "\n");
		}
        
		String string = sb.toString();
		sms.sendTextMessage(phonenbr, null, string, null, null);
	}
}