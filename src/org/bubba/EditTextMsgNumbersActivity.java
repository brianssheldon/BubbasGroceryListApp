package org.bubba;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditTextMsgNumbersActivity extends Activity
{
	private EditText txt1;
	private EditText txt2;
	private EditText txt3;
    private static Button saveButton;
    private static Button exitButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_msg_phone_number_list);

        txt1 = (EditText) findViewById(R.id.entry1);
        txt2 = (EditText) findViewById(R.id.entry2);
        txt3 = (EditText) findViewById(R.id.entry3);
		
        TextMsgUtils textUtils = new TextMsgUtils();
		String[] parsedTxtMsgNbrs = textUtils.parseTxtMsgNbrs(readTextMsgNumbersFile().toString());
		
		txt1.setText(parsedTxtMsgNbrs[0]);		
		txt2.setText(parsedTxtMsgNbrs[1]);		
		txt3.setText(parsedTxtMsgNbrs[2]);
		
		saveButton = (Button) findViewById(R.id.save2);
		saveButton.setOnClickListener(btnSaveOnClick);
		
		exitButton = (Button) findViewById(R.id.exit2);
		exitButton.setOnClickListener(btnExitOnClick);
    }
    
    // Click listener for the Add button.
    private final Button.OnClickListener btnSaveOnClick = new Button.OnClickListener() 
    {
        public void onClick(View v) 
        {
			saveFile();
        }
    };
    
    // Click listener for the Add button.
    private final Button.OnClickListener btnExitOnClick = new Button.OnClickListener() 
    {
        public void onClick(View v) 
        {
	    	Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edittextmsgnbrmenu, menu); 
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {
//		    case R.id.ok:
//		    	saveFile();
//		        return true;
		        
		    case R.id.exit:
		    	Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
                
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

	private void saveFile()
	{
		try
		{
			String nbrs = txt1.getText().toString() + "|"
				+ txt2.getText().toString() + "|"
				+ txt3.getText().toString();
			
			FileOutputStream fos = openFileOutput("tstMsgNbrlist.txt", Context.MODE_PRIVATE);
			fos.write(nbrs.getBytes());
			fos.close();
		}
		catch(Exception e)
		{
			Log.getStackTraceString(e);
		}
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
}
