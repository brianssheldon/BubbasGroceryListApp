package org.bubba.bubbasgrocerylist;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

public class BubbasGroceryListAppActivity extends Activity
{	// listactivty  populate with an adapter
	private ItemLocUtils utils = new ItemLocUtils();
	private KnownItemUtils knownItemsUtil = new KnownItemUtils();
	private static AutoCompleteTextView textView;
	LinearLayout editAndAddLL;
	ArrayList<ItemLoc> groceryList = new ArrayList<ItemLoc>();
	ArrayList<ItemLoc> knownItemList = new ArrayList<ItemLoc>();
	public LinearLayout ll;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        ScrollView sv = new ScrollView(this); // main view

        ll = new LinearLayout(this); // everything else goes in this view. do I need sv too?
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll, 0);	// add ll to sv view
        
        createTextViewAndAddButton();
        
        addCheckBoxesFromGroceryFile();
        
        this.setContentView(sv);
    }

	void createTextViewAndAddButton()
	{
		editAndAddLL = new LinearLayout(this);	// contains text view and add button
        editAndAddLL.setOrientation(LinearLayout.HORIZONTAL);

			textView = new AutoCompleteTextView(this);
			textView.setWidth(400); // tableview or within linear layout
	        addTextView();
		    
	        editAndAddLL.addView(textView, 0);
		    
		    Button addButton = new Button(this);
		    addButton.setText("Add");
		    addButton.setWidth(90);
		    addButton.setOnClickListener(btnAddOnClick);	// add button listener
		    editAndAddLL.addView(addButton, 1);
	    
	    ll.addView(editAndAddLL, 0);	// add list view that contains text view and add button 
	}

	void addTextView()
	{
		String[] foodArray = utils.getKnownItemsArray(this);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			this,
			R.layout.list_item,
			foodArray);
		textView.setAdapter(adapter);
		textView.setOnItemClickListener(new OnitemClick()); // selected item from drop down
		textView.setInputType(InputType.TYPE_CLASS_TEXT); // make keyboard popup
	}

//	private String[] getKnownItemsArray(Context context)
//	{
//		String[] hardCodedItems = getResources().getStringArray(R.array.food_array);
//		String[] scannedItems = knownItemsUtil.readKnownItemsListFileAsArray(context);
//
//		int length = hardCodedItems.length;
//		int length2 = scannedItems.length;
//		
//		String[] newArray = new String[length + length2];
//
//		for (int i = 0; i < length; i++)
//		{
//			newArray[i] = hardCodedItems[i];
//		}
//		
//		int x = 0;
//		for (int i = length; i < length + scannedItems.length; i++)
//		{
//			newArray[i] = scannedItems[x];
//			x += 1;
//		}
//		
//		return newArray;
//	}

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
			
			ArrayList<ItemLoc> knownItemList = knownItemsUtil.readKnownItemsListFile(v.getContext());
			knownItemList.add(new ItemLoc(name, "99", "1"));
			knownItemsUtil.saveFile(knownItemList, v.getContext());
			addTextView();
			
			utils.saveFile(groceryList, v.getContext());	// save arraylist of ItemLoc's to file

			addCheckBoxesFromGroceryFile(); // repopulate the screen
			
			// why can't I just do this? answer - I can 
			((TextView)textView).setText("");
        }
    };
    
	private final class DeleteRowListener implements OnCheckedChangeListener
	{
		public void onCheckedChanged(CompoundButton arg0, boolean arg1)
		{
			final CompoundButton arg00 = arg0;
			String textString = "";
			
			int index = arg0.getText().toString().trim().indexOf(" ") + 1;
			
			if(index > 1)
			{
				textString = arg0.getText().toString().trim().substring(index);
			}
			
	        new AlertDialog.Builder(arg0.getContext())
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Delete Item?")
	        .setMessage("Do you want to delete\n\n" + textString + "?")
	        .setPositiveButton("Delete", new DialogInterface.OnClickListener() 
	        {
	            @Override
	            public void onClick(DialogInterface dialog, int which)
	            {
	            	// they have clicked on the checkbox so remove this row - ***removes all occurences***
	    			StringBuffer sb = new StringBuffer(arg00.getText()); // get value of the checkbox text
	    			utils.removeLeadingSpacesAndNumbers(sb); // clean up sb for comparison
	    			String deleteThisOne = sb.toString();
	    			
	    			ItemLoc il = null;
	    			ArrayList<ItemLoc> newList = new ArrayList<ItemLoc>();

	    			// there has got to be a better way to do this
	    			for (Iterator<ItemLoc> iterator = groceryList.iterator(); iterator.hasNext();)
	    			{
	    				il = iterator.next();
	    				if(!il.getItem().equals(deleteThisOne))
	    				{
	    					newList.add(il);	// add rows to new list - except the one they selected
	    				}
	    			}
	    			groceryList = newList;
	    			
	    			utils.saveFile(groceryList, arg00.getContext());
	    			
	    			addCheckBoxesFromGroceryFile();	// repaint screen now that we've removed a row
	            	
	            }
	        })
	        .setNegativeButton("cancel", null)
	        .show();
			
//			// they have clicked on the checkbox so remove this row - ***removes all occurences***
//			StringBuffer sb = new StringBuffer(arg0.getText()); // get value of the checkbox text
//			utils.removeLeadingSpacesAndNumbers(sb); // clean up sb for comparison
//			String deleteThisOne = sb.toString();
//			
//			ItemLoc il = null;
//			ArrayList<ItemLoc> newList = new ArrayList<ItemLoc>();
//
//			// there has got to be a better way to do this
//			for (Iterator<ItemLoc> iterator = groceryList.iterator(); iterator.hasNext();)
//			{
//				il = iterator.next();
//				if(!il.getItem().equals(deleteThisOne))
//				{
//					newList.add(il);	// add rows to new list - except the one they selected
//				}
//			}
//			groceryList = newList;
//			
//			utils.saveFile(groceryList, arg0.getContext());
//			
//			addCheckBoxesFromGroceryFile();	// repaint screen now that we've removed a row
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
			
			// erase text view text - don't need all this code...
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
	
	public boolean onPrepareOptionsMenu (Menu menu)
	{	// called everytime menu is launched. If textMsg nbrs have changed, 
		// I need to repopulate the text.
		menu.removeGroup(9); // remove existing textMsg Nbrs from menu
		    
	    addToMenu(menu); // add textMsg Nbrs to menu
	    
	    return true;
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{	// called when they have selected a menu option
    	SendTextMessage stm = new SendTextMessage();

	    int itemId = item.getItemId();
	    
		switch (itemId)
	    {
		    case R.id.exit:
		    	this.finish(); // quit app. is this good or bad?? understand life cycle & fragments
		    	return true;
	
		    case 1:
		    case 2:
		    case 3:
		    	stm.sendTextMsg(item.getTitle().toString(), this, groceryList);
		    	return true;
		    
		    case R.id.scanBarcode:
		    	try
		    	{
		    		if(isNetworkAvailable())
		    		{
		    			IntentIntegrator.initiateScan(this);
		    		}
		    		else
		    		{
		    			Toast.makeText(this, "you need an internet connection to scan a bar code", Toast.LENGTH_LONG).show();
		    		}
		    	}
		    	catch (Exception e)
		    	{
					e.printStackTrace();
				}
		    	return true;
		    	
		    case R.id.editTextMsgNbrlist:	// go to screen to edit phone numbers > add to AndroidManifest.xml
	            Intent myIntent = new Intent(this, EditTextMsgNumbersActivity.class);
	            startActivityForResult(myIntent, 100);
		    	return true;
		    	
		    case R.id.addFromBigList:	// go to screen to select items from big list
		    	Intent bigListIntent = new Intent(this, BigListActivity.class);
		    	startActivityForResult(bigListIntent, 101);
		    	addCheckBoxesFromGroceryFile();//dont need?
		    	return true;
		    	
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

	String[] readGoogleApi(String upc)
	{
		String[] results = new String[25];
		
		try
		{
			URL url = new URL(
					"https://www.googleapis.com/shopping/search/v1/public/products?key=AIzaSyBwV4tNao1xC68ilalkKytyXICfrPH91MA&country=US&q="
							+ upc + "&alt=json");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream stream = connection.getInputStream();
			InputSource inputSource = new InputSource(stream);
			InputStream byteStream = inputSource.getByteStream();

			StringBuffer record = new StringBuffer();

			int ch = 0;

			while ((ch = byteStream.read()) != -1)
			{
				record.append((char) ch);
			}
			
			JSONTokener jsonTokener = new JSONTokener(record.toString());
			JSONObject nextValue = (JSONObject) jsonTokener.nextValue();
			JSONArray jsonArray = (JSONArray) nextValue.getJSONArray("items");

			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject oobbjj = (JSONObject) jsonArray.opt(i);
				JSONObject product = (JSONObject) oobbjj.get("product");
				results[i] = (String) product.get("title");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return results;
	}
		
	@Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {	// after we get back from BigListActivity etc, reload page
	     super.onActivityResult(requestCode, resultCode, data);

	     switch(resultCode) 
	     {
	     	case 92:
	     		Toast.makeText(this, "barcode not found. sorry", Toast.LENGTH_SHORT).show();
	     		break;
	     	case 93:
	     		addTextView();
	     		break;
	     }

   	     switch(requestCode) 
   	     {
	     	case 101:
	     		addCheckBoxesFromGroceryFile();
	     		break;
	     		
	     	case IntentIntegrator.REQUEST_CODE: 
		    {
			     if (resultCode != RESULT_CANCELED) 
			     {
			    	 IntentResult scanResult =
			    			 IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
			    	 if (scanResult != null) 
			    	 {
			    		 String upc = scanResult.getContents();
			    		 String[] results = readGoogleApi(upc);
			    		 
			    		  Intent myIntent = new Intent(this, PickDescriptionActivity.class);
			    		  myIntent.putExtra("googleApiArray", results);
				          startActivityForResult(myIntent, 113);
//					      return true;
			    		 
//			    		 groceryList.add(new ItemLoc(results[0], "99", "1"));
//			    		 utils.saveFile(groceryList, this);
//			    		 addCheckBoxesFromGroceryFile();
			    	 }
			     }
		    }
	     }
    }
}