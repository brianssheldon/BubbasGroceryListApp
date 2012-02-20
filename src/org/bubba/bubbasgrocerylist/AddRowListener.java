package org.bubba.bubbasgrocerylist;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AddRowListener implements OnCheckedChangeListener
{
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
	{	// adds an ItemLoc pojo everytime it's selected - may cause dups...
		String text = arg0.getText().toString();
		if("".equals(text)) return;
		
		ItemLoc item = addItemLoc(text);
		
		ItemLocUtils utils = new ItemLocUtils();
		
		ArrayList<ItemLoc> itemList = utils.readGroceryListFile(arg0.getContext());
		itemList.add(item);
		utils.saveFile(itemList, arg0.getContext());
	}

	private ItemLoc addItemLoc(String text)
	{
		ItemLoc item = null;
		StringTokenizer st = new StringTokenizer(text, ",");
		
		if(st.countTokens() == 2)
		{
			item = new ItemLoc();
			item.setItem(st.nextToken());
			item.setAisle(st.nextToken());
			item.setQuantity("1");
		}
		
		return item;
	}
}
