package org.bubba.bubbasgrocerylist;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;


public class BigListActivity extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ScrollView sv = new ScrollView(this); 

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll, 0);
        
        String[] foodArray = (new ItemLocUtils()).getKnownItemsArray(this);
        		//getResources().getStringArray(R.array.food_array);
        
        for (int i = 0; i < foodArray.length; i++)
        {
        	if(null == foodArray[i]
        		|| "".equals(foodArray[i])) continue;
        		
			CheckBox cb = new CheckBox(this);
			cb.setId(i);
            cb.setText(foodArray[i]);
			cb.setOnCheckedChangeListener(new AddRowListener());
			
			ll.addView(cb);
		}

        this.setContentView(sv);
    }
}
