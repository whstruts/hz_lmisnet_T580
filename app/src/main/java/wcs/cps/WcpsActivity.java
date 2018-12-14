package wcs.cps;


import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class WcpsActivity extends Activity {
    /** Called when the activity is first created. */
	Intent m_actLogin=null;
	Intent m_actMain=null;	
	
	clsWcsWelcome m_WelCome=null;	
	//clsSocketMgr m_SocketThread=null;
    LogDBHelper  m_LogDB=null;
		
    /** Called when the activity is first created. */
	Handler hd=new Handler()//声明消息处理器
	{
			@Override
			public void handleMessage(Message msg)//重写方法
        	{
        		switch(msg.what)
        		{
        		case 99:
        			//myThread();
        			break;
        		default:
        			mGotoCurWorkUi(msg.what);
        			break;	
        		}
        	}
	};
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
	              WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.hd.sendEmptyMessage(0);         
    }
    private void mGotoCurWorkUi(int _UiNo)
    {
    	if (_UiNo==0)
    	{
    		clsWcsWelcome.m_MainHandler=this.hd;
    		m_WelCome=new clsWcsWelcome(this);   		
    		setContentView(m_WelCome);
    	}
    	else if (_UiNo==1)
    	{
    		m_actLogin=new Intent(this,actLogin.class);//actPickWork
    		startActivityForResult(m_actLogin,1);			
    	}
    	else if (_UiNo==2)
    	{
    		m_actMain=new Intent(this,actMain.class);//actMain
    		startActivityForResult(m_actMain,1);
    	}
    	else if (_UiNo==3)
    	{
    		//killAll(getApplicationContext()); 
    		android.os.Process.killProcess(android.os.Process.myPid());
    		finish();
    	}
    }
    @Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
    	super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
			case 1:
				switch(resultCode)
				{
					case 2:
						mGotoCurWorkUi(2);
						break;
					case 3:
						//android.os.Process.killProcess(android.os.Process.myPid());
						//finish();
						this.hd.sendEmptyMessage(3);
						break;
				}
			default:
				break;
		}
	}
    
  
    public static void killAll(Context context)
	{ 
		//获取一个ActivityManager对象
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
        //获取系统中所有正在运行的进程
        List<RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses(); 
        //获取当前activity所在的进程
        String currentProcess=context.getApplicationInfo().processName;  
        //对系统中所有正在运行的进程进行迭代，如果进程名不是当前进程，则Kill掉
        for (RunningAppProcessInfo appProcessInfo : appProcessInfos)
        { 
        	String processName=appProcessInfo.processName; 
        	if (!processName.equals(currentProcess))
        	{  
        		System.out.println("ApplicationInfo-->"+processName); 
                activityManager.killBackgroundProcesses(processName); 
            } 
         } 
    } 
}