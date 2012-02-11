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
        
        ScrollView sv = new ScrollView(this); // main view

        ll = new LinearLayout(this); // everything else goes in this view. do I need sv too?
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll, 0);	// add ll to sv view
        
        LinearLayout editAndAddLL = new LinearLayout(this);	// contains text view and add button
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
		    addButton.setOnClickListener(btnAddOnClick);	// add button listener
		    editAndAddLL.addView(addButton, 1);
	    
	    ll.addView(editAndAddLL, 0);	// add list view that contains text view and add button 
        
        addCheckBoxesFromGroceryFile();
        
        this.setContentView(sv);
    }

	private void addCheckBoxesFromGroceryFile()
	{
		while (ll.getChildCount() > 1)
		{
			ll.removeViewAt(1);	// remove all the checkboxes
		}
		
		RelativeLayout row; // holds checkbox and qty text view
		CheckBox cb;		// checkbox
		EditText itemQty;	// qty text view
		
		RelativeLayout.LayoutParams left = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		left.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		left.width = 400;	// could just use as 1st parm to 'left' variable
		
		RelativeLayout.LayoutParams right = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		right.width = 60;	// could just use as 1st parm to 'right' variable
		
		groceryList = utils.readGroceryListFile(this); // arraylist of ItemLoc pojo's
        ItemLoc ele;
        
        for (int i = 0; i < groceryList.size(); i++)
		{
        	row = new RelativeLayout(this);	// make a new row
			
			ele = groceryList.get(i);	// get next ItemLoc pojo
			
			if(!"".equals(ele.toString()))	// if ItemLoc is not blank, add it to the row
			{			
				cb = new CheckBox(this);
	            cb.setId(i);
	            cb.setText(ele.getAisle() +  "  " + ele.getItem());
	            cb.setSoundEffectsEnabled(true);
	            cb.setButtonDrawable(R.drawable.trashcan);	// trashcan icon
	            cb.setLayoutParams(left);	// place on left side of view
				cb.setOnCheckedChangeListener(new DeleteRowListener()); // add checked listener
				
				row.addView(cb);	// add cb to row view
				
				itemQty = new EditText(this);
				itemQty.setText(ele.getQuantity());
				itemQty.setLayoutParams(right);	// place on right side of view
				itemQty.setId(i + 99);	// not needed?
				itemQty.setInputType(InputType.TYPE_CLASS_NUMBER); // use the nbr keyboard for this view
				itemQty.setOnFocusChangeListener(new QuantityOnFocusChangeListener()); // add listener
				row.addView(itemQty); // add qty view to row
				
	            ll.addView(row);	// add row to list view
			}
		}
	}
    
	// Click listener for the Add button.
    private final Button.OnClickListener btnAddOnClick = new Button.OnClickListener() 
    {
        public void onClick(View v) 
        {
			String name = ((TextView)textView).getText().toString();
			
			if("".equals(name)) return; // bail if nothing is entered
			
			if(groceryList.size() == 0)
			{
				groceryList = new ArrayList<ItemLoc>();
			}

			groceryList.add(new ItemLoc(name, "99", "1"));	// since they hit the add button, 
															// we don't know the aisle so use 99
			
			utils.saveFile(groceryList, v.getContext());	// save arraylist of ItemLoc's to file

			addCheckBoxesFromGroceryFile(); // repopulate the screen
			
			// get the edit view so we can clear the contents since they added what was there.
			LinearLayout linearLayout = (LinearLayout) ll.getChildAt(0);
			AutoCompleteTextView atv = (AutoCompleteTextView) linearLayout.getChildAt(0);
			atv.setText("");
        }
    };
    
	private final class DeleteRowListener implements OnCheckedChangeListener
	{
		public void onCheckedChanged(CompoundButton arg0, boolean arg1)
		{	// they have clicked on the checkbox so remove this row - ***removes all occurences***
			StringBuffer sb = new StringBuffer(arg0.getText()); // get value of the checkbox text
			utils.removeLeadingSpacesAndNumbers(sb); // clean up sb for comparison
			String deleteThisOne = sb.toString();
			
			ItemLoc il = null;
			ArrayList<ItemLoc> newList = new ArrayList<ItemLoc>();
			
			for (Iterator<ItemLoc> iterator = groceryList.iterator(); iterator.hasNext();)
			{
				il = iterator.next();
				if(!il.getItem().equals(deleteThisOne))
				{
					newList.add(il);	// add rows to new list - except the one they selected
				}
			}
			groceryList = newList;
			
			utils.saveFile(groceryList, arg0.getContext());
			
			addCheckBoxesFromGroceryFile();	// repaint screen now that we've removed a row
		}
	}
	
    private final class OnitemClick implements OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View textView, int position, long id)
		{	// they have selected an item from the dropdown list. add it to the grocery list
			if(groceryList.size() == 0)
			{	// make sure groceryList is not empty
				groceryList = new ArrayList<ItemLoc>();
			}
			
			String name = ((TextView)textView).getText().toString(); // get selected item
			
			StringTokenizer st = new StringTokenizer(name, ","); // split into item and aisle
			
			// make sure st has 2 tokens, aisle and item desc.
			if(st.countTokens() < 2) st = new StringTokenizer(name + ",99", ","); 
			
			// add item to groceryList and set quantity to 1
			groceryList.add(new ItemLoc(st.nextToken(), st.nextToken(), "1"));
			
			Collections.sort(groceryList); // seems like I shouldn't have to do this here 
			
			// erase text view text
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
	{	// only called once - creates the menu
	    MenuInflater inflater = getMenuInflater();
	    
	    addToMenu(menu);

	    inflater.inflate(R.menu.mainmenu, menu);
	    
	    return true;
	}
	
	public boolean onPrepareOptionsMenu (Menu menu)
	{	// called everytime menu is launched. If textMsg nbrs have changed, 
		// I need to repopulate the text.
		menu.removeGroup(9); // remove existing textMsg Nbrs from menu
		    
	    addToMenu(menu); // add textMsg Nbrs to menu
	    
	    return true;
	}

	void addToMenu(Menu menu)
	{
		TextMsgUtils textUtils = new TextMsgUtils();
	    
	    String textNbrsString = textUtils.readTextMsgNumbersFile(getApplicationContext()).toString();
	    String[] textNbrs = textUtils.parseTxtMsgNbrs(textNbrsString);
	    
	    for (int i = 0; i < textNbrs.length; i++)
	    {
			menu.add(9, i + 1, i, textNbrs[i]);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{	// called when they have selected a menu option
    	SendTextMessage stm = new SendTextMessage();

	    int itemId = item.getItemId();
	    
		switch (itemId)
	    {
		    case R.id.exit:
		    	this.finish(); // quit app. is this good or bad??
		    	return true;
	
		    case 1:
		    case 2:
		    case 3:
		    	stm.sendTextMsg(item.getTitle().toString(), this, groceryList);
		    	return true;
		    	
		    case R.id.editTextMsgNbrlist:	// go to screen to edit phone numbers
	            Intent myIntent = new Intent(this, EditTextMsgNumbersActivity.class);
	            startActivityForResult(myIntent, 0);
		    	return true;
		    	
		    case R.id.addFromBigList:	// go to screen to select items from big list
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