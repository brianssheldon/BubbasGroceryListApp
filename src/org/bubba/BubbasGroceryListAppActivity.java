package org.bubba;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BubbasGroceryListAppActivity extends Activity
{
	private ItemLocUtils utils = new ItemLocUtils();
	private static AutoCompleteTextView textView;
	ArrayList<ItemLoc> groceryList = new ArrayList<ItemLoc>();
	private LinearLayout ll;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        ScrollView sv = new ScrollView(this);

        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll, 0);
        
        LinearLayout editAndAddLL = new LinearLayout(this);
        editAndAddLL.setOrientation(LinearLayout.HORIZONTAL);
        
	        textView = new AutoCompleteTextView(this);
	        textView.setWidth(400);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    	this,
		    	R.layout.list_item,
		    	getResources().getStringArray(R.array.food_array));
		    textView.setAdapter(adapter);
		    textView.setOnItemClickListener(new OnitemClick());
		    textView.setInputType(InputType.TYPE_CLASS_TEXT);
		    editAndAddLL.addView(textView, 0);
		    
		    Button addButton = new Button(this);
		    addButton.setText("Add");
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
		
		RelativeLayout row;
		EditText itemDesc;
		
		RelativeLayout.LayoutParams left = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		left.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		left.width = 400;
		
		RelativeLayout.LayoutParams right = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		right.width = 60;
		
		groceryList = utils.readGroceryListFile(this);
        ItemLoc ele;
        
        for (int i = 0; i < groceryList.size(); i++)
		{
        	row = new RelativeLayout(this);
			
			ele = groceryList.get(i);
			
			if(!"".equals(ele.toString()))
			{			
				CheckBox cb = new CheckBox(this);
	            cb.setId(i);
	            cb.setText(ele.getAisle() +  "  " + ele.getItem());
	            cb.setSoundEffectsEnabled(true);
	            cb.setButtonDrawable(R.drawable.trashcan);
	            cb.setLayoutParams(left);
				cb.setOnCheckedChangeListener(new DeleteRowListener());
				
				row.addView(cb);//, left);
				
				itemDesc = new EditText(this);
				itemDesc.setText(ele.getQuantity());
				itemDesc.setLayoutParams(right);
				itemDesc.setId(i + 99);
				itemDesc.setInputType(InputType.TYPE_CLASS_NUMBER);
				itemDesc.setOnFocusChangeListener(new OnFocusChangeListener()
				{
					@Override
					public void onFocusChange(View v,boolean hasFocus)
					{ /* When focus is lost check that the text field has valid values. */
						updateQuantity(v, hasFocus);
					}

					void updateQuantity(View v, boolean hasFocus) 
					{
						if (!hasFocus)
						{
							RelativeLayout child;
							int cc = ll.getChildCount();

							boolean recordUpdated = false;
							
							for (int j = 1; j < cc; j++)
							{
								child = (RelativeLayout) ll.getChildAt(j);
								CheckBox cb2 = (CheckBox) child.getChildAt(0);
								EditText et2 = (EditText) child.getChildAt(1);
								
								StringBuffer cbDesc = new StringBuffer( 
										cb2.getText().toString().trim());
								String newQty = et2.getText().toString();
								utils.removeLeadingSpacesAndNumbers(cbDesc);						
								
								for (Iterator<ItemLoc> iter = groceryList.iterator(); iter.hasNext();)
								{
									ItemLoc itemLoc = (ItemLoc) iter.next();
									
									if(itemLoc.getItem().contains(cbDesc)
											&& !itemLoc.getQuantity().equals(newQty))
									{
										itemLoc.setQuantity(newQty);
										recordUpdated = true;
										break;
									}
								}
								
								if(recordUpdated)
								{
									break;
								}
								
								String asdaaadd = "";
							}
							
							if(recordUpdated)
							{
								Collections.sort(groceryList);
								utils.saveFile(groceryList, v.getContext());
							}
						}
						else
						{
							EditText et = (EditText)v;
							et.setText("");
						}
					}
				});
				row.addView(itemDesc);
				
	            ll.addView(row);
			}
			else
			{
				String x = "";
			}
		}
	}
    
	// Click listener for the Add button.
    private final Button.OnClickListener btnAddOnClick = new Button.OnClickListener() 
    {
        public void onClick(View v) 
        {
			String name = ((TextView)textView).getText().toString();
			
			if("".equals(name)) return;
			
			if(groceryList.size() == 0)
			{
				groceryList = new ArrayList<ItemLoc>();
			}

			groceryList.add(new ItemLoc(name, "99", "1"));
			
			utils.saveFile(groceryList, v.getContext());

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
			StringBuffer sb = new StringBuffer(arg0.getText());
			utils.removeLeadingSpacesAndNumbers(sb);
			String deleteThisOne = sb.toString();
			
			ItemLoc il = null;
			ArrayList<ItemLoc> newList = new ArrayList<ItemLoc>();
			
			for (Iterator<ItemLoc> iterator = groceryList.iterator(); iterator.hasNext();)
			{
				il = iterator.next();
				if(!il.getItem().equals(deleteThisOne))
				{
					newList.add(il);
				}
			}
			groceryList = newList;
			
			utils.saveFile(groceryList, arg0.getContext());
			
			addCheckBoxesFromGroceryFile();
		}
	}
	
    private final class OnitemClick implements OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View textView, int position, long id)
		{
			if(groceryList.size() == 0)
			{
				groceryList = new ArrayList<ItemLoc>();
			}
			
			String name = ((TextView)textView).getText().toString();
			
			StringTokenizer st = new StringTokenizer(name, ",");
			
			if(st.countTokens() < 2) st = new StringTokenizer(name + ",99", ",");
			
			groceryList.add(new ItemLoc(st.nextToken(), st.nextToken(), "1"));
			
			Collections.sort(groceryList);
			
			LinearLayout linearLayout = (LinearLayout) ll.getChildAt(0);
			AutoCompleteTextView atv = (AutoCompleteTextView) linearLayout.getChildAt(0);
			atv.setText("");
	        
	        utils.saveFile(groceryList, parent.getContext());
	        
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
	    case R.id.exit:
	    	this.finish();
	    	return true;
	    	
	    case R.id.textgrocerylist1:
	    	sendTextMsg(getTxtPhoneNbr(0));
	    	return true;
	    	
	    case R.id.textgrocerylist2:
		    	sendTextMsg(getTxtPhoneNbr(1));
		    	return true;
		    	
		    case R.id.textgrocerylist3:
		    	sendTextMsg(getTxtPhoneNbr(2));
		    	return true;
		    	
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
		
		ArrayList<ItemLoc> list = utils.readGroceryListFile(this);
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