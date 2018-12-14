package wcs.cps;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import wcs.cps.http.HttpTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class actLogin extends Activity
{	
	private Button bt_Login=null;
	private Button bt_Exit=null;
	private Button bt_Cnnt=null;
	private Button bt_Update=null;
	private TextView tv_MessTs=null;
	private TextView tv_SerIp=null;
	private TextView tv_Port=null;
	private TextView tv_wrkno=null;
	private TextView tv_pwd=null;
    private TextView tv_Cur=null;
    private TextView tv_wlzxbm=null;
    private ListView listview=null;
    private AlertDialog mAlertDialog=null;
	private int m_iCurWorkIndex=-1;
	private int m_curType=0;
	private String[] m_WlzxArr;
	public  Handler hd=new Handler()//声明消息处理器
	{
			@Override
			public void handleMessage(Message msg)//重写方法
        	{	
				switch(msg.what)
				{
				case clsMyPublic.MsTypeDisMess:
					mDoDisMessage(msg);
					break;
				case clsMyPublic.MsTypeReceMess:
					mDoReturnData(msg);
				}
        	}
	};
	
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.actlogin); 
        mSetSysData();

        UpdateManager manager = new UpdateManager(actLogin.this);

		manager.checkUpdate();

    }
	
	public void mSendData(int _Type)
	{
		HttpTool _httpTool=new HttpTool();
		
		_httpTool.m_CurHandler=hd;
		_httpTool.m_urlPath = "http://"+HttpTool.m_SvrIp+":"+HttpTool.m_SvrPort + HttpTool.m_sktNetPath;
		
		//Log.v("zms", "获取数据 "+_httpTool.m_urlPath);
		if (_Type==1)
		{
			m_curType =1;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "GetUserNameByNo");  
			_httpTool.m_params.put("josnparas", "{\"STAFF_NUMBER\":\""+clsMyPublic.g_WorkNo+"\"}"); 
			Log.v("zms", "{\"STAFF_NUMBER\":\""+clsMyPublic.g_WorkNo+"\"}");
			
		}
		else if (_Type==2)
		{
			m_curType=2;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "Login");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"LCCODE\":\""+clsMyPublic.g_WorkWlzxNo+
					"\",\"PWD\":\""+clsMyPublic.g_WorkPass+"\",\"IP\":\""+getHostIP()+"\",\"TERMINALSTYPE\":\"TPC\"}");  
		}
		else if (_Type==3)
		{
			m_curType=3;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "Logout");  
			//HttpTool.m_SvrIp="127.0.0.1";
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"IP\":\""+getHostIP()+"\"}");  
		}
		else if (_Type==4)
		{
			_httpTool.m_urlPath = "http://"+HttpTool.m_SvrIp+":"+HttpTool.m_SvrPort +"/Api/WebServiceEx/Conn_test";
			m_curType=4;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
		}
		_httpTool.mStart();
			
	}
	
	private void showAlertDialog() 
	{        
		ListAdapter mAdapter = new ArrayAdapter(this, R.layout.item, m_WlzxArr);        
		LayoutInflater inflater = LayoutInflater.from(this);          
		View view = inflater.inflate(R.layout.alertdialog, null);              
		           
		listview = (ListView)view.findViewById(android.R.id.list);        
		listview.setAdapter(mAdapter);        
		listview.setOnItemClickListener(new MyListener());  
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);         
		mAlertDialog = builder.create();        
		mAlertDialog.show();        
		mAlertDialog.getWindow().setContentView(view);  
		
	}        
	
	private class MyListener implements  OnItemClickListener{  

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//String a= (String) listview.getItemAtPosition(arg2);   
				clsMyPublic.g_WorkWlzxNo=clsMyPublic.GetStrOutOfStr((String) listview.getItemAtPosition(arg2),0);
				tv_wlzxbm.setText(clsMyPublic.g_WorkWlzxNo);
				LogDBHelper.gUpdateColValue(0, "WlzxNo", clsMyPublic.g_WorkWlzxNo);
				mAlertDialog.hide();
			}  
		          
		  }  

	public static String getHostIP() {  
		  
	    String hostIp = null;  
	    try {  
	        Enumeration nis = NetworkInterface.getNetworkInterfaces();  
	        InetAddress ia = null;  
	        while (nis.hasMoreElements()) {  
	            NetworkInterface ni = (NetworkInterface) nis.nextElement();  
	            Enumeration<InetAddress> ias = ni.getInetAddresses();  
	            while (ias.hasMoreElements()) {  
	                ia = ias.nextElement();  
	                if (ia instanceof Inet6Address) {  
	                    continue;// skip ipv6  
	                }  
	                String ip = ia.getHostAddress();  
	                if (!"127.0.0.1".equals(ip)) {  
	                    hostIp = ia.getHostAddress();  
	                    break;  
	                }  
	            }  
	        }  
	    } catch (SocketException e) {  
	        Log.i("yao", "SocketException");  
	        e.printStackTrace();  
	    }  
	    return hostIp;  
	  
	}  
	public void mSetSysData()
	{
    	bt_Login=(Button)findViewById(R.id.bt_login);
    	bt_Exit=(Button)findViewById(R.id.bt_Exit);
    	bt_Cnnt=(Button)findViewById(R.id.bt_Cnnt);
    	bt_Update=(Button)findViewById(R.id.bt_Update);
    	tv_MessTs=(TextView)findViewById(R.id.tv_ts);
    	tv_SerIp=(TextView)findViewById(R.id.tv_SerIp);
    	tv_Port=(TextView)findViewById(R.id.tv_Port);
    	tv_wrkno=(TextView)findViewById(R.id.tv_wrkno);
    	tv_pwd=(TextView)findViewById(R.id.tv_pwd);
    	tv_wlzxbm=(TextView) findViewById(R.id.tv_wlzxbm);
    		
    	clsMyPublic.g_WorkNo=LogDBHelper.gGetColValue("WORKNO");
//    	if(LogDBHelper.gGetColValue("WlzxNo").length()>0)
//    	{
//    		clsMyPublic.g_WorkWlzxNo=LogDBHelper.gGetColValue("WlzxNo");
//    	}
    	tv_wrkno.setText(clsMyPublic.g_WorkNo);
    	tv_SerIp.setText(HttpTool.m_SvrIp);
    	tv_Port.setText(HttpTool.m_SvrPort);
    	tv_wlzxbm.setText(clsMyPublic.g_WorkWlzxName);
    	bt_Update.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				UpdateManager manager = new UpdateManager(actLogin.this);

				manager.checkUpdate();
			}
		});
    	tv_wrkno.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			public void afterTextChanged(Editable s) {
				if (s.toString()!=clsMyPublic.g_WorkNo){
					tv_wlzxbm.setText("");
					clsMyPublic.g_WorkWlzxName="";
					clsMyPublic.g_WorkWlzxNo="";
				}
			}
		});
    	bt_Cnnt.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					HttpTool.m_SvrIp=tv_SerIp.getText().toString();
    					HttpTool.m_SvrPort=tv_Port.getText().toString();
    					//clsMyPublic.g_WorkWlzxNo=tv_wlzxbm.getText().toString();
    			    	if (HttpTool.m_SvrIp.length()==0||HttpTool.m_SvrPort.length()==0 )
    			    	{   
    			    		Toast.makeText(actLogin.this, "请输入服务端IP或者服务端端口",
    								Toast.LENGTH_SHORT).show();
    			    		return;
    			    	}
						clsMyPublic.g_WorkNo=tv_wrkno.getText().toString();
    			    	mSendData(4);

    					//v.setEnabled(false);
    					//v.setBackgroundResource(R.drawable.clrunenabled);
    				}
    		    }	
    	);    	
    	//登陆事件
    	bt_Login.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{   
    					clsMyPublic.g_WorkNo=tv_wrkno.getText().toString();
    					clsMyPublic.g_WorkPass=tv_pwd.getText().toString();
    					if (clsMyPublic.g_WorkNo.length()==0 || clsMyPublic.g_WorkPass.length()==0)
    					{
    						Toast.makeText(actLogin.this, "请输入员工编号或者员工密码",
    								       Toast.LENGTH_SHORT).show();
    						return;
    					}
    					LogDBHelper.gUpdateColValue(0, "WORKNO", clsMyPublic.g_WorkNo);
    					mSendData(1);
    				    //v.setEnabled(false);
    				    //v.setBackgroundResource(R.drawable.clrunenabled);	
    				}
    		    }	
    	);
    	//退出事件
    	bt_Exit.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mSendData(3);
    					
    					Intent intent=new Intent();
    					intent.putExtra("UINO", "MainUI");
    					setResult(3,intent);
    					finish();   
    				}
    		    }	
    	);
    	tv_SerIp.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					m_iCurWorkIndex=1;
    					mCharKeys();   										
    				}
    		    }	
    	);
    	/*tv_wlzxbm.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					//m_iCurWorkIndex=5;
    					//mCharKeys(); 
    					tv_wlzxbm.setEnabled(false);
    					mSendData(4);
    					
    					//showAlertDialog() ;
    				}
    		    }	
    	);*/
    	tv_Port.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					m_iCurWorkIndex=2;
    					mCharKeys();   										
    				}
    		    }	
    	);
    	tv_wrkno.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					m_iCurWorkIndex=3;
    					mCharKeys(); 
    					v.setFocusable(true);
    					v.setFocusableInTouchMode(true);
    				}
    		    }	
    	);
    	tv_pwd.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					m_iCurWorkIndex=4;
    					mCharKeys(); 
    					v.setFocusable(true);
    					v.setFocusableInTouchMode(true);
    				}
    		    }	
    	);

    	
	}
	
	private void mDoReturnData(Message msg)
	{	
		String json="",sFunNo="",sRet1="";
		json=(String)msg.obj;
		
		try
		{
			sRet1=json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length()-1).toString());//sRet1.substring(1, sRet1.length()-1).toString()
			JSONArray jsonArray=new JSONArray();
			/*if(m_curType==4)
			{
				jsonObject = new JSONObject(jsonObject.getString("Conn_testResult"));
			}
			else
			{
				jsonObject = new JSONObject(jsonObject.getString(HttpTool.m_sktResult));
			}*/
	    	String ErrOrMsg =jsonObject.getString("ERRORMSG"); 
	    	if(ErrOrMsg.isEmpty())
	    	{
	    		if (m_curType==4)
				{
					String JsonData=jsonObject.getString("OUTDATA");
					JsonData=JsonData.substring(1,JsonData.length()-1);
					JSONObject jsonObject1 = new JSONObject(JsonData);
					jsonArray=new JSONArray(jsonObject1.getString("t"));
				}else{
					jsonArray =new JSONArray(jsonObject.getString("StrJson"));
				}
	    		if(m_curType==1)
	    		{
		    		if (jsonArray.length()>0)
		    		{
		    			JSONObject jsonObject2 = (JSONObject)jsonArray.opt(0); 
		    			clsMyPublic.g_WorkName=jsonObject2.getString("STAFF_NAME");
		    			mSendData(2);
		    			
		    		}
	    		}
	    		else if(m_curType==2)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			setResult(2,null);
	    			finish();
	    		}
	    		else if(m_curType==3)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			Intent intent=new Intent();
					intent.putExtra("UINO", "MainUI");
					setResult(3,intent);
					finish();   
	    		}
	    		else if(m_curType==4)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			if (jsonArray.length()>0)
	    			{
						JSONObject jsonObject2 = (JSONObject)jsonArray.opt(0);
						clsMyPublic.g_WorkWlzxNo=jsonObject2.getString("LC_CODE");
						clsMyPublic.g_WorkWlzxName=jsonObject2.getString("LC_NAME");
						tv_wlzxbm.setText(clsMyPublic.g_WorkWlzxName);
						tv_wrkno.setEnabled(false);
						mSaveLoginData();
						LogDBHelper.gUpdateColValue(0, "WORKNO", clsMyPublic.g_WorkNo);
						//m_WlzxArr = new String[jsonArray.length()];
	    				/*for (int i=0;i<jsonArray.length();i++)
			    		{
		    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
		    				m_WlzxArr[i]=jsonObject2.getString("LC_CODE") +":"+jsonObject2.getString("LC_NAME") ;
			    		}
	    				tv_wlzxbm.setText(m_WlzxArr[0]);
	    				showAlertDialog();*/
	    			}
	    			//tv_wlzxbm.setEnabled(true);
	    		}
	    	}
	    	else
	    	{
	    		tv_MessTs.setText(ErrOrMsg);
	    		//tv_wlzxbm.setEnabled(true);
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
			 Log.v("zms", "进去循环5"+e.toString());
			 tv_MessTs.setText(e.toString());
		}  
		
		/*
		sFunNo=clsMyPublic.GetStrInOfStr(sReceData, 0);
		if (sFunNo.compareTo(clsMyPublic.g_SktParamLogin)==0)
		{
			sRet1=clsMyPublic.GetStrInOfStr(sReceData, 1);
			if (sRet1.compareTo("1")==0)
			{
				clsMyPublic.g_WorkName=clsMyPublic.GetStrInOfStr(sReceData,3);
				clsMyPublic.g_WorkAreaNo=clsMyPublic.GetStrInOfStr(sReceData,4);
				setResult(2,null);
				finish();
			}
			else if (sRet1.compareTo("-1")==0)
			{	
				tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
				bt_Login.setEnabled(true);
				bt_Login.setBackgroundResource(R.drawable.clsbtnselect);
			}
			else if (sRet1.compareTo("-2")==0)
			{	
				tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
			}
		}*/
	}
	private void mDoDisMessage(Message msg)
	{
		String sMess=(String)msg.obj;
		tv_MessTs.setText(sMess);
	}
	private void mSaveLoginData()
	{

		LogDBHelper.gUpdateColValue(0, "SERIP", HttpTool.m_SvrIp);
		LogDBHelper.gUpdateColValue(0, "SERPORT", HttpTool.m_SvrPort);
		LogDBHelper.gUpdateColValue(0, "WlzxNo", clsMyPublic.g_WorkWlzxNo);
		LogDBHelper.gUpdateColValue(0, "WlzxName", clsMyPublic.g_WorkWlzxName);
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

	private void mCharKeys()
	{
		LinearLayout loginLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.actcharkeys, null);
		final AlertDialog adNumKey=
				new AlertDialog.Builder(this)
						.setView(loginLayout).create();//.setTitle("键盘")
		Window win=adNumKey.getWindow();
		WindowManager.LayoutParams lp=win.getAttributes();
		lp.x=0;
		lp.y=200;
		win.setAttributes(lp);
		clsMyPublic.m_sKeys="";
		if (m_iCurWorkIndex==1)
			tv_Cur=tv_SerIp;
		else if (m_iCurWorkIndex==2)
			tv_Cur=tv_Port;
		else if (m_iCurWorkIndex==3)
			tv_Cur=tv_wrkno;
		else if (m_iCurWorkIndex==4)
			tv_Cur=tv_pwd;
		else if (m_iCurWorkIndex==5)
			tv_Cur=tv_wlzxbm;
		//clsMyPublic.m_sKeys=tv_Cur.getText().toString();
		for (int i = 0; i < loginLayout.getChildCount(); i++)
		{
			View v=loginLayout.getChildAt(i);
			if (v instanceof RelativeLayout)
			{
				RelativeLayout KeyLayout=(RelativeLayout)v;
				for(int k=0;k<KeyLayout.getChildCount();k++)
				{
					View x=KeyLayout.getChildAt(k);
					x.setOnClickListener
							(
									new OnClickListener()
									{
										public void onClick(View v)
										{
											CharSequence sTmp="";
											String sKey="";
											Button btn=(Button)v;
											sTmp=btn.getText();
											sKey=(String)sTmp;
											if (sKey.compareTo("回车")==0)
											{
												adNumKey.cancel();
												return;
											}
											else if (sKey.compareTo("删除")==0)
											{
												int iLen=-1;
												iLen=clsMyPublic.m_sKeys.length();
												if (iLen>0)
												{
													clsMyPublic.m_sKeys=clsMyPublic.m_sKeys.substring(0,iLen-1);
													tv_Cur.setText(clsMyPublic.m_sKeys);
												}
												return;
											}
											else if (sKey.compareTo("清除")==0)
											{
												clsMyPublic.m_sKeys="";
												tv_Cur.setText("");
												return;
											}
											else if(sKey.compareTo("取消")==0)
											{
												adNumKey.cancel();
												return;
											}
											clsMyPublic.m_sKeys+=sKey.toUpperCase();
											tv_Cur.setText(clsMyPublic.m_sKeys);

										}
									}
							);
				}
			}
		}

		adNumKey.show();
	}
}
