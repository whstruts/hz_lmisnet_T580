
package wcs.cps;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LogDBActivity extends Activity 
{
	
	private Button bt_Return=null;
	private Button bt_Delete=null;
	private Button bt_query=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setTitleColor(Color.BLUE);
		this.setTitle("WCPS-无线拣选小车系统 【系统日志查询】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");
		setContentView(R.layout.loglist);
		mSetSysData();
		fillLogList();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
	public void fillLogList()
	{
        Cursor mCursor = LogDBHelper.gRetrunCursor(LogDBHelper.Logs.LOGS_TABLE_NAME);
        startManagingCursor(mCursor);      
        ListAdapter adapter =
            new SimpleCursorAdapter(
                this,
                R.layout.log_item,
                mCursor,
                new String[]
                {
            		LogDBHelper.Logs.LOGS_D,
            		LogDBHelper.Logs.LOGS_EI,
            		LogDBHelper.Logs.LOGS_FW,
            		LogDBHelper.Logs.LOGS_OPER 
                },
                new int[]
                {
                    R.id.TextView_Data,
                    R.id.TextView_ErrorInfo,
                    R.id.TextView_FunWindow,
                    R.id.TextView_Operator 
                });
 
        ListView av = (ListView)findViewById(R.id.lst_log);
        av.setAdapter(adapter);
        
	}

	private void mSetSysData()
	{
		bt_Return=(Button)findViewById(R.id.bt_Return);
		bt_Delete=(Button)findViewById(R.id.bt_delete);
		bt_query=(Button)findViewById(R.id.bt_query);
		//返回事件
    	bt_Return.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					finish();
				}
		    }
        );

    	bt_Delete.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					LogDBHelper.gDeleteLogData();
    					fillLogList();
    				}
    		    }	
    	);
    	bt_query.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					fillLogList();
    				}
    		    }	
    	);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    switch (keyCode)
	    {
	        case KeyEvent.KEYCODE_BACK:
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
