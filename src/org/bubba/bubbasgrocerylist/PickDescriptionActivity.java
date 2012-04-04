package org.bubba.bubbasgrocerylist;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PickDescriptionActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ScrollView sv = new ScrollView(this); 

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll, 0);
        
        String[] googleApiArray = getIntent().getExtras().getStringArray("googleApiArray");
        if(googleApiArray == null || googleApiArray.length == 0
        		|| googleApiArray[0] == null)
        {
        	setResult(92);
        	finish();
        }
        
        for (int i = 0; i < googleApiArray.length; i++)
        {
			CheckBox cb = new CheckBox(this);
			cb.setId(i);
            cb.setText(googleApiArray[i]);
            cb.setSoundEffectsEnabled(false);
			cb.setOnCheckedChangeListener(new AddItemFromGoogleListener());
			
			ll.addView(cb);
		}

        this.setContentView(sv);
    }
    
    public class AddItemFromGoogleListener implements OnCheckedChangeListener
    {
    	@Override
    	public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
    	{	
    		String text = arg0.getText().toString();
    		if("".equals(text)) return;
    		
    		text = text.replaceAll(",", " ");
    		
    		KnownItemUtils utils = new KnownItemUtils();
    		
    		ArrayList<ItemLoc> list = utils.readKnownItemsListFile(arg0.getContext());
    		list.add(new ItemLoc(text, "99", "1"));
    		utils.saveFile(list, arg0.getContext());
    		setResult(93);
    		finish();
    	}
    }
}
