package org.bubba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
	public LinearLayout ll;
	
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
				itemDesc.setOnFocusChangeListener(new QuantityOnFocusChangeListener());
				row.addView(itemDesc);
				
	            ll.addView(row);
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
    	SendTextMessage stm = new SendTextMessage();
    	
	    switch (item.getItemId())
	    {
	    case R.id.exit:
	    	this.finish();
	    	return true;
	    	
	    case R.id.textgrocerylist1:
	    	stm.sendTextMsg(0 ,this, groceryList);
	    	return true;
	    	
	    case R.id.textgrocerylist2:
	    	stm.sendTextMsg(0 ,this, groceryList);
		    	return true;
		    	
	    case R.id.textgrocerylist3:
	    	stm.sendTextMsg(0 ,this, groceryList);
	    	return true;
	    	
	    case R.id.editTextMsgNbrlist:
            Intent myIntent = new Intent(this, EditTextMsgNumbersActivity.class);
            startActivityForResult(myIntent, 0);
	    	return true;
	    	
	    case R.id.addFromBigList:
	    	Intent bigListIntent = new Intent(this, BigListActivity.class);
	    	startActivityForResult(bigListIntent, 0);
	    	addCheckBoxesFromGroceryFile();
	    	return true;
	    	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {	// after we get back from BigListActivity etc, reload page
	     super.onActivityResult(requestCode, resultCode, data);
	
	     addCheckBoxesFromGroceryFile();
    }
}