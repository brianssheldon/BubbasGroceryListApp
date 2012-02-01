package org.bubba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class QuantityOnFocusChangeListener implements OnFocusChangeListener
{
	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{	/* When focus is lost check that the text field has valid values. */
		if (!hasFocus)
		{
			BubbasGroceryListAppActivity act = (BubbasGroceryListAppActivity) v.getContext();
			ArrayList<ItemLoc> groceryList = act.groceryList;
			LinearLayout ll = act.ll;
			
			ItemLocUtils utils = new ItemLocUtils();
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
}
