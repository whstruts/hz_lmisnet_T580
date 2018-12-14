package wcs.cps;

import org.json.JSONArray;
import org.json.JSONObject;

import wcs.cps.http.HttpTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class actPickBoxMatch extends Activity
{
	private int m_iCurBoxIndex=0;
	private int m_iCurWorkIndex=-1;
	private int m_iCurDoMatch=0;
	private int m_iUnWorkCount=0;
	private int m_iFinished=-1;
	private int[] m_iSFGL=null;
	
	private TextView[] edt_BoxNo;
	private TextView[] tv_Pos;
	private TextView[] tv_boxPos;
	private TextView[] tv_FenpdNo;
	private TextView[] tv_taskstatus;
	private TextView[] tv_BoxValue;
	
	private TextView tv_Cur=null;	
	private TextView tv_MessTs=null;
	private TextView tv_zyts=null;
	private EditText edtInput=null;
	
	private String m_sFpdj=""; //分配单据
	private String m_sBoxNo=""; //周转箱编号
	private String m_sZylbName=""; //作业类别名称
	private String m_sAreaNo=""; //作业区域
	
	private Button bt_Save=null;
	private Button bt_Return=null;
	private Button bt_BPrint=null;
	public int m_isBPrint=0;
	public String m_sPrnBoxNo="";
	private String m_sClsName="actPickBoxMatch";
	private int m_ts_zt=0;
	private int m_curType=0;
		
	public  Handler hd=new Handler()//声明消息处理器
	{
		public void handleMessage(Message msg)//重写方法
		{			
			switch(msg.what)
			{
			case clsMyPublic.MsTypeDisMess:
				mDoDisMessage(msg);
				break;
			case clsMyPublic.MsTypeReceMess:
				mDoReturnData(msg);
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actpickboxmatch);
		this.setTitleColor(Color.BLUE);
		this.setTitle("WCPS-无线拣选小车系统 【关联容器作业】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");		
		m_iSFGL=new int[4];
		for (int i=0;i<4;i++)
			m_iSFGL[i]=0;
		mSetSysData();
		mSetButtonIni();
		mSendData(1);	
	}
	public void mSendData(int _Type)
	{
		String _sendData="";
		HttpTool _httpTool=new HttpTool();
		_httpTool.m_CurHandler=hd;
		_httpTool.m_urlPath = "http://"+HttpTool.m_SvrIp+":"+HttpTool.m_SvrPort + HttpTool.m_sktNetPath;
		
		tv_MessTs.setText("");
		if (_Type==1)
		{
			m_curType=1;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowObtainedTask");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\"}");  
		}
		else if (_Type==2)
		{
			m_curType=2;
			
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			if (m_sZylbName.compareTo(clsMyPublic.g_SqWorkType_Tqjx)==0)
			{
				_httpTool.m_params.put("func_code", "BindBasket");  
			}
			else
			{
				_httpTool.m_params.put("func_code", "BindToteBox");  
			}
			
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"ToteBoxNo\":\""+m_sBoxNo+"\",\"TaskBillNo\":\""+m_sFpdj+"\"}");  
			//Log.v("zms","{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"ToteBoxNo\":\""+m_sBoxNo+"\",\"TaskBillNo\":\""+m_sFpdj+"\"}");
		}

		_httpTool.mStart();
		/*
		String _sendData="";
		clsSocketCln.m_CurHandler=hd;
		if (_Type==1)
		{
			_sendData=clsMyPublic.g_SktParamSQPick +";" +
					  clsMyPublic.g_WorkName+";";
		}
		else if (_Type==2)
		{
			_sendData=clsMyPublic.g_SktParamMatch +";"+
					  m_sBoxNo +";" +
		              m_sFpdj  +";" +
		              m_sZylbName +";" +
		              clsMyPublic.g_WorkName +";" +
		              m_sAreaNo +";";
		}
		else if (_Type==3)
		{
			_sendData=clsMyPublic.g_sktParamPrnData +";"+
		              clsMyPublic.g_sktParamLsZtData +";"+"1;"+m_sPrnBoxNo+";"+
		              clsMyPublic.g_WorkName+";";			
		}
		clsSocketCln.m_SendData=_sendData;
		clsSocketCln _SktCln=new clsSocketCln();
		_SktCln.mStart();
		*/
	}
	private void mSetSysData()
	{
	    bt_Return=(Button)findViewById(R.id.bt_Return);
		bt_Save=(Button)findViewById(R.id.bt_save);
		bt_BPrint=(Button)findViewById(R.id.bt_BPrint);
		
		tv_zyts=(TextView)findViewById(R.id.tv_zyts);
		
		tv_MessTs=(TextView)findViewById(R.id.tv_ts);
		
		edtInput=(EditText)findViewById(R.id.edtInput);
		edtInput.setInputType(InputType.TYPE_NULL);
		edtInput.setFocusable(true);
		edtInput.setFocusableInTouchMode(true);
		edtInput.requestFocus();
		edtInput.setOnKeyListener
    	(
    		new View.OnKeyListener()
	    	{
	    		public boolean onKey(View v, int keyCode, KeyEvent event)
	    		{
	    			if (keyCode==KeyEvent.KEYCODE_ENTER)
	    			{	
	    				String s="";
	    				s=edtInput.getText().toString();
	    				s=s.trim();
	    				if (s.length()==0) 
	    				{
	    					edtInput.setText("");
	    					return true;
	    				}
	    				if (m_iCurDoMatch==1) return true;
	    				edt_BoxNo[m_iCurBoxIndex-1].setText(s);
	    				edtInput.setText("");
	    				m_sBoxNo=edt_BoxNo[m_iCurWorkIndex-1].getText().toString();
	    				mSendData(2);
    					m_iCurDoMatch=1;
    					bt_Save.setEnabled(false);
						bt_Save.setBackgroundResource(R.drawable.clrunenabled);	    				
	    			}
	    			return false;
	    		}
	    	}
    	);
		
		tv_Pos=new TextView[4];
		tv_Pos[0]=(TextView)findViewById(R.id.tv_Pos01);
		tv_Pos[1]=(TextView)findViewById(R.id.tv_Pos02);
		tv_Pos[2]=(TextView)findViewById(R.id.tv_Pos03);
		tv_Pos[3]=(TextView)findViewById(R.id.tv_Pos04);
					
		edt_BoxNo=new TextView[4];
		edt_BoxNo[0]=(TextView)findViewById(R.id.edt_BoxNo01);
		edt_BoxNo[1]=(TextView)findViewById(R.id.edt_BoxNo02);
		edt_BoxNo[2]=(TextView)findViewById(R.id.edt_BoxNo03);
		edt_BoxNo[3]=(TextView)findViewById(R.id.edt_BoxNo04);
		

		
		tv_boxPos=new TextView[4];
		tv_boxPos[0]=(TextView)findViewById(R.id.tv_boxPos01);
		tv_boxPos[1]=(TextView)findViewById(R.id.tv_boxPos02);
		tv_boxPos[2]=(TextView)findViewById(R.id.tv_boxPos03);
		tv_boxPos[3]=(TextView)findViewById(R.id.tv_boxPos04);


		tv_FenpdNo=new TextView[4];
		tv_FenpdNo[0]=(TextView)findViewById(R.id.tv_FenpdNo01);
		tv_FenpdNo[1]=(TextView)findViewById(R.id.tv_FenpdNo02);
		tv_FenpdNo[2]=(TextView)findViewById(R.id.tv_FenpdNo03);
		tv_FenpdNo[3]=(TextView)findViewById(R.id.tv_FenpdNo04);
		
		

		tv_taskstatus=new TextView[4];
		
		tv_taskstatus[0]=(TextView)findViewById(R.id.tv_taskstatus01);
		tv_taskstatus[1]=(TextView)findViewById(R.id.tv_taskstatus02);
		tv_taskstatus[2]=(TextView)findViewById(R.id.tv_taskstatus03);
		tv_taskstatus[3]=(TextView)findViewById(R.id.tv_taskstatus04);
		
		tv_BoxValue=new TextView[4];

		tv_BoxValue[0]=(TextView)findViewById(R.id.tv_BoxValue01);
		tv_BoxValue[1]=(TextView)findViewById(R.id.tv_BoxValue02);
		tv_BoxValue[2]=(TextView)findViewById(R.id.tv_BoxValue03);
		tv_BoxValue[3]=(TextView)findViewById(R.id.tv_BoxValue04);
		
		//返回事件
    	bt_Return.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					if (m_iFinished==1)
					{   						
						Intent intent=new Intent();
						intent.putExtra("_Finish", "OK");
						setResult(2,intent);
					}
					finish();
				}
		    }
        );

    	bt_Save.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{ 
    					
    				    m_sBoxNo=edt_BoxNo[m_iCurWorkIndex-1].getText().toString();
   
    					if (m_sBoxNo.length()==0)
    					{
    						Toast.makeText(actPickBoxMatch.this, "您输入周转箱编号!",
    								Toast.LENGTH_SHORT).show();
    						return ;
    					}
    					mSendData(2);
    					m_iCurDoMatch=1;
    					//bt_Save.setEnabled(false);
						//bt_Save.setBackgroundResource(R.drawable.clrunenabled);
    				}
    		    }	
    	);
    	bt_BPrint.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_isBPrint=1;
					mNumKeys();
				}
		    }
        );
    	tv_Pos[0].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					if (m_iCurDoMatch==1) return;   					    					 					
    					mSetBoxData(1);
    				}
    		    }	
    	);
    	edt_BoxNo[0].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{      					 
    					mSetBoxData(1);
    					mNumKeys(); 
    				}
    		    }		
    	);
    	tv_Pos[1].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					if (m_iCurDoMatch==1) return;   					
    					mSetBoxData(2);
    				}
    		    }	
    	);
    	edt_BoxNo[1].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mSetBoxData(2);
    					mNumKeys(); 
    				}
    		    }		
    	);
    	tv_Pos[2].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mSetBoxData(3);
    					
    				}
    		    }	
    	);
    	edt_BoxNo[2].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					
    					mSetBoxData(3);
    					mNumKeys(); 
    				}
    		    }		
    	);
    	
    	tv_Pos[3].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mSetBoxData(4); 					
    				}
    		    }	
    	);
    	edt_BoxNo[3].setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mSetBoxData(4);
    					mNumKeys(); 
    				}
    		    }		
    	);
	}
	private void mSetBoxData(int _Index)
	{
		if (m_iCurDoMatch==1) return; 
        //当前作业控件
		
		m_iCurWorkIndex=_Index;
		m_iCurBoxIndex=_Index;
		
		edt_BoxNo[_Index-1].setBackgroundResource(R.drawable.clrpressyes); 
		tv_Pos[_Index-1].setBackgroundResource(R.drawable.clrcurworkhw);
		tv_boxPos[_Index-1].setBackgroundResource(R.drawable.clrcurboxback);
		m_sFpdj=tv_FenpdNo[_Index-1].getText().toString();
		m_sZylbName=clsMyPublic.GetZylbNo(tv_taskstatus[_Index-1].getText().toString());
		if (m_sZylbName.compareTo(clsMyPublic.g_SqWorkType_Tqjx)==0)
		{
			m_sFpdj+="1";
		}
		if (m_iSFGL[_Index-1]==0)
		{    						
			bt_Save.setEnabled(true);
			bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
		}
	}
	private void mSetButtonIni()
	{
		for(int i=0;i<4;i++)
		{
			edt_BoxNo[i].setEnabled(false);
			edt_BoxNo[i].setBackgroundResource(R.drawable.clrunenabled);
		}
		
		for(int i=0;i<4;i++)
		{
			tv_Pos[i].setEnabled(false);
			tv_Pos[i].setBackgroundResource(R.drawable.clrunenabled);
		}
		
		bt_Save.setEnabled(false);
		bt_Save.setBackgroundResource(R.drawable.clrunenabled);
	}
	
	private int zth(String zthvalue)
	{
		int abcd=0;
		
		if(zthvalue.compareTo("A")==0)
		{
			abcd=1;
		}
		else if(zthvalue.compareTo("B")==0)
		{
			abcd=2;
		}
		else if(zthvalue.compareTo("C")==0)
		{
			abcd=3;
		}
		else if(zthvalue.compareTo("D")==0)
		{
			abcd=4;
		}
		
		return abcd;
	}
	
	private void mDoReturnData(Message msg)
	{
		String json="",sFunNo="",sRet1="";
		
		String sFpdj="";  //分配单号
		String sZcbh="";  //暂存编号
		String sZylb="";  //作业类型
		String sZylbName="";  //作业类别名称
		String sRqbh="";  //容器编号
		int iZcbh=-1;
		
		try
		{
			json=(String)msg.obj;
			//Log.v("zms","2"+json);
			//JSONObject jsonObject = new JSONObject(json.toString());
	    	//jsonObject = new JSONObject(jsonObject.getString(HttpTool.m_sktResult));
			sRet1=json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length()-1).toString());
			
	    	String ErrOrMsg =jsonObject.getString("ERRORMSG"); 
	    	if(ErrOrMsg.isEmpty())
	    	{
	    		JSONArray jsonArray =new JSONArray(jsonObject.getString("StrJson")); 
	    		if(m_curType==1)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			
	    			for (int i=0;i<jsonArray.length();i++)
		    		{
	    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
	    				
	    				
						sFpdj=jsonObject2.getString("TASKBILL_NO");
						sZylb=jsonObject2.getString("OPER_CATE");
						sZcbh=jsonObject2.getString("CART_BUFFER_NO");
						if (sZylb.compareTo(clsMyPublic.g_SqWorkType_Tqjx)==0)
							sRqbh=jsonObject2.getString("BASKET_NO");
						else
							sRqbh=jsonObject2.getString("TOTEBOX_NO");
						m_sAreaNo=jsonObject2.getString("AREA_NO");
						sZylbName=clsMyPublic.GetZylbName(sZylb);				
						iZcbh=zth(sZcbh)-1;
						tv_FenpdNo[iZcbh].setText(sFpdj);
						edt_BoxNo[iZcbh].setText(sRqbh);
						tv_BoxValue[iZcbh].setText(sRqbh);
						tv_taskstatus[iZcbh].setText(sZylbName);
						tv_zyts.setText(sZylbName);
						
						if( sZylb.compareTo(clsMyPublic.g_SqWorkType_Lstd)==0 || 
							sZylb.compareTo(clsMyPublic.g_SqWorkType_Ztzy)==0 || 
							sZylb.compareTo(clsMyPublic.g_SqWorkType_Gjtc)==0)
						{
							m_ts_zt++;
						}
						
						if (sRqbh.length()<=1)
						{
							//有作业任务的位置且未关联周转箱的显示黄色
							tv_boxPos[iZcbh].setBackgroundResource(R.drawable.clrpressyes);
							if (m_iUnWorkCount<=0)
							{
								m_iCurBoxIndex=iZcbh+1;
								edt_BoxNo[iZcbh].setEnabled(true);
								//edt_BoxNo[iZcbh].setBackgroundResource(R.drawable.clspickmsgselect);	
								tv_Pos[iZcbh].setEnabled(true);
								tv_Pos[iZcbh].setBackgroundResource(R.drawable.clspickmsgselect);	
								mSetBoxData(m_iCurBoxIndex);
							}
							m_iUnWorkCount++;
							m_iSFGL[iZcbh]=0;
						}
						else if (sRqbh.length()>1)
						{
							//已经关联成功的显示已完成的绿色
							tv_boxPos[iZcbh].setBackgroundResource(R.drawable.clrendboxback);
							tv_Pos[iZcbh].setEnabled(false);
							tv_Pos[iZcbh].setBackgroundResource(R.drawable.clrunenabled);					
							edt_BoxNo[iZcbh].setEnabled(false);
							edt_BoxNo[iZcbh].setBackgroundResource(R.drawable.clrunenabled);
							m_iSFGL[iZcbh]=1;
						}
	    				
		    		}
	    		}
	    		else if(m_curType==2)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			m_iCurDoMatch=0;
					m_iUnWorkCount--;
					//已经关联成功的分配单，相应位置的控件的颜色要修改
					edt_BoxNo[m_iCurBoxIndex-1].setEnabled(false);
					edt_BoxNo[m_iCurBoxIndex-1].setBackgroundResource(R.drawable.clrendboxback);
					tv_boxPos[m_iCurBoxIndex-1].setBackgroundResource(R.drawable.clrendboxback);
					tv_Pos[m_iCurBoxIndex-1].setEnabled(false);
					tv_Pos[m_iCurBoxIndex-1].setBackgroundResource(R.drawable.clrunenabled);
					m_iSFGL[m_iCurBoxIndex-1]=0;
					tv_BoxValue[m_iCurBoxIndex-1].setText(edt_BoxNo[m_iCurWorkIndex-1].getText());
					if (m_iUnWorkCount==0)
					{
						m_iFinished=1;
						if(m_ts_zt>0)
						{
							//mShowMessage("当前已经索取的分配单据，周转箱关联已经全部完成，请核对周转箱暂存位置是否正确，然后开始拣货...");
							mShowMessage("周转箱关联已经全部完成，特殊任务，请到标签打印点，领取标签...");
						}
						else
						{
							mShowMessage("当前已经索取的分配单据，周转箱关联已经全部完成，请核对周转箱暂存位置是否正确，然后开始拣货...");
						}
					}
					else 
					{
						edt_BoxNo[m_iCurBoxIndex].setEnabled(true);
						tv_Pos[m_iCurBoxIndex].setEnabled(true);						
						mSetBoxData(m_iCurBoxIndex+1);
					}
	    		}
	    	}
	    	else
	    	{
	    		m_iCurDoMatch=0;
	    		tv_MessTs.setText(ErrOrMsg);
	    	}
			
		}
		catch (Exception e)
		{
			Log.v("zms", e.toString());
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
	        mShowMessageII(sErrLog);
	    }
	}
	private void mDoDisMessage(Message msg)
	{
		tv_MessTs=(TextView)findViewById(R.id.tv_ts);
		String sMess=(String)msg.obj;
		tv_MessTs.setText(sMess);
	}
	private void mViewBlueData(Message msg)
	{
		byte[] readBuf = (byte[]) msg.obj;
		String readMessage = new String(readBuf, 0, msg.arg1);
		readMessage=readMessage.trim();
		if (m_iCurDoMatch==1) return;
		edt_BoxNo[m_iCurBoxIndex-1].setText(readMessage);
		m_sBoxNo=edt_BoxNo[m_iCurWorkIndex-1].getText().toString();
		mSendData(2);
		m_iCurDoMatch=1;
		bt_Save.setEnabled(false);
		bt_Save.setBackgroundResource(R.drawable.clrunenabled);	 		
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
	private void mNumKeys()
	{
		LinearLayout loginLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.actnumkeys, null);
		final AlertDialog adNumKey=
		new AlertDialog.Builder(this)
				.setView(loginLayout).create();
		Window win=adNumKey.getWindow();
		WindowManager.LayoutParams lp=win.getAttributes();
		lp.x=300;
		lp.y=200;
		win.setAttributes(lp);
		clsMyPublic.m_sKeys="";
		
		if (m_isBPrint==0)
			tv_Cur=edt_BoxNo[m_iCurBoxIndex-1];
		else
			tv_Cur=tv_MessTs;
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
		    						if (m_isBPrint==1)
		    						{	
		    							m_isBPrint=0;
		    							m_sPrnBoxNo=clsMyPublic.m_sKeys;
		    							if (m_sPrnBoxNo.length()>0)
		    								mSendData(3);
		    							
		    						}
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
		    					clsMyPublic.m_sKeys+=sKey;
		    					tv_Cur.setText(clsMyPublic.m_sKeys);
		    				}
		    		    }
			    	);
				}
			}
	    }
		adNumKey.show();
	}
	
	private  void mShowMessageII(String _sMess)
	{
		try
		{
			new AlertDialog.Builder(actPickBoxMatch.this)
	        .setTitle("温馨提示")
	        .setIcon(android.R.drawable.alert_dark_frame)
	        .setMessage(_sMess)
	        .setNegativeButton("确定", new DialogInterface.OnClickListener()
	 	    {
	 		   public void onClick(DialogInterface dialog, int which) 
	 		   {
	 			  
	 		   }
	 		 }).show();
		}
		catch (Exception e)
		{
			String sErrLog="";
			sErrLog=m_sClsName +";mShowMessageII;" + e.toString();
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
		}
	}
	
	public  void mShowMessage(String _sMess)
	{
		new AlertDialog.Builder(actPickBoxMatch.this)
        .setTitle("温馨提示")
        .setIcon(android.R.drawable.alert_dark_frame)
        .setMessage(_sMess+"\n确定要退出本界面吗?")
        .setNegativeButton("取消", new DialogInterface.OnClickListener()
		   {
			   public void onClick(DialogInterface dialog, int which)
			   {
				   
			   }
		   }
		   ).setPositiveButton("确定", new DialogInterface.OnClickListener()
 	    {
 		   public void onClick(DialogInterface dialog, int which) 
 		   {
 			  if (m_iFinished==1)
				{   						
					Intent intent=new Intent();
					intent.putExtra("_Finish", "OK");
					setResult(2,intent);
				}
				finish();
 		   }
 		 }).show();
	}
	
}
