package org.bubba;

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
        setContentView(R.layout.big_list);
        ScrollView sv = new ScrollView(this); // again with the scrollview. do I really need this?

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll, 0);
        
        String[] foodArray = getResources().getStringArray(R.array.food_array);
        
        for (int i = 0; i < foodArray.length; i++)
        {
			CheckBox cb = new CheckBox(this);
			cb.setId(i);
            cb.setText(foodArray[i]);
            cb.setSoundEffectsEnabled(true);
			cb.setOnCheckedChangeListener(new AddRowListener());
			
			ll.addView(cb);
		}

        this.setContentView(sv);
    }
}
