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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class actStock extends Activity 
{
	private Button bt_Return=null;
	private Button bt_Refresh=null;
	private Button bt_PgUp=null;
	private Button bt_PgDown=null;
	private ListView lv_mainTask=null;
	private TextView tv_MessTs=null;
	private TextView tv_cxtj=null;
	private TextView tv_spzjm=null;
	
	private TextView tv_Cur=null;
    private int m_iCurWorkIndex=-1;
	
	private String[][] m_receMainTask;
	private int m_iCurReceIndex=-1;
	private int m_iSumReceCount=-1;
	private String m_sQuery="";
	private String m_sZJM="";
	private EditText edtInput=null;
	private String m_sClsName="actStock";
	
	private int m_curType=0; 
	private int m_sPageNum = 1;
	
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
        setContentView(R.layout.actstock);
        this.setTitleColor(Color.BLUE);
        this.setTitle("WCPS-无线拣选小车系统 【库存查询】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");
        clsMyPublic.m_hdlSendMess=hd;
        mSetSysData();
    }
	public void mSendData(int _Type)
	{

		
		String _sendData="";
		HttpTool _httpTool=new HttpTool();
		tv_MessTs.setText("");
		_httpTool.m_CurHandler=hd;
		_httpTool.m_urlPath = "http://"+HttpTool.m_SvrIp+":"+HttpTool.m_SvrPort + HttpTool.m_sktNetPath;
		
		//Log.v("zms", "获取数据 "+_httpTool.m_urlPath);
		if (_Type==1)
		{
			m_curType =1;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowStock");  
			_httpTool.m_params.put("josnparas", "{\"DisplayLocation\":\""+m_sQuery+"\",\"MnemonicCode\":\""+m_sZJM+"\",\"PageNum\":"+m_sPageNum+"}"); 
			Log.v("zms", "{\"DisplayLocation\":\""+m_sQuery+"\",\"MnemonicCode\":\""+m_sZJM+"\",\"PageNum\":"+m_sPageNum+"}");
			_httpTool.mStart();
		}
	}
	private void mDoDisMessage(Message msg)
	{
		String sMess=(String)msg.obj;
		tv_MessTs.setText(sMess);
	}
	private void mSetSysData()
	{
		bt_Return=(Button)findViewById(R.id.bt_return);
		bt_Refresh=(Button)findViewById(R.id.bt_Refresh);
		bt_PgUp=(Button)findViewById(R.id.bt_pageUp);
		bt_PgDown=(Button)findViewById(R.id.bt_pageDown);
		
    	tv_MessTs=(TextView)findViewById(R.id.tv_ts);
    	tv_cxtj=(TextView)findViewById(R.id.tv_cxtj);
		lv_mainTask=(ListView)findViewById(R.id.listView1);
		
		tv_spzjm = (TextView)findViewById(R.id.tv_spzjm);
		
		edtInput=(EditText)findViewById(R.id.edtInput);
		edtInput.setInputType(InputType.TYPE_NULL);
		edtInput.setFocusable(true);
		edtInput.setFocusableInTouchMode(true);
		edtInput.requestFocus();
		
		bt_PgUp.setEnabled(false);
		bt_PgUp.setBackgroundResource(R.drawable.clrunenabled);
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
	    				tv_cxtj.setText(s);
	    				edtInput.setText("");  	
	    				//查询数据
	    				m_receMainTask=null;
						m_sQuery=tv_cxtj.getText().toString();
						mSendData(1);
						bt_Refresh.setEnabled(false);
						bt_Refresh.setBackgroundResource(R.drawable.clrunenabled);
	    			}
	    			return false;
	    		}
	    	}
    	);
//		
		bt_PgUp.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					if(m_sPageNum<=2)
					{
						m_sPageNum=1;
						bt_PgUp.setEnabled(false);
						bt_PgUp.setBackgroundResource(R.drawable.clrunenabled);
						
						bt_PgDown.setEnabled(true);
						bt_PgDown.setBackgroundResource(R.drawable.clsbtnselect);
					}
					else
					{
						m_sPageNum--;
					}
					mSendData(1);
				}
		    }
        );
		bt_PgDown.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_sPageNum++;
					bt_PgUp.setEnabled(true);
					bt_PgUp.setBackgroundResource(R.drawable.clsbtnselect);
					mSendData(1);
				}
		    }
        );
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
    	//刷新事务事件
    	bt_Refresh.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_receMainTask=null;
					m_sQuery=tv_cxtj.getText().toString();
					m_sZJM = tv_spzjm.getText().toString();
					//if (m_sQuery==null || m_sZJM ==null ) return;
					//if (m_sQuery.length()==0 || m_sZJM.length()==0) return;
					mSendData(1);
				    v.setEnabled(false);
				    v.setBackgroundResource(R.drawable.clrunenabled);	
				}
		    }
        );
    	//
    	tv_cxtj.setOnClickListener
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
    	
    	tv_spzjm.setOnClickListener
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
	}
	
	private void mFillMainTaskData(String[][] _sData)
	{
		final String[][]msg=_sData;
        BaseAdapter ba_detail=new BaseAdapter()//新建适配器
        {
			public int getCount() 
			{
				return msg[0].length;//得到列表的长度
			}
			public Object getItem(int arg0){return null;}
			public long getItemId(int arg0){return 0;}
			public View getView(int arg0, View arg1, ViewGroup arg2)//为每一项添加内容
			{
				LinearLayout ll_detail=new LinearLayout(actStock.this);
				ll_detail.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
				ll_detail.setPadding(5,5,5,5);//四周留白

				for(int i=0;i<msg.length;i++)//为每一行设置显示的数据
				{					    
					TextView s= new TextView(actStock.this);
					s.setText(msg[i][arg0]+" ");//TextView中显示的文字
					s.setTextSize(20);//字体大小
					s.setTextColor(Color.BLUE);
					s.setPadding(1,2,2,1);//四周留白
					/*
					if (i==0||i==9)
						s.setWidth(clsMyPublic.dip2px(actStock.this,200));//宽度
					else if (i==1 || i==3)
						s.setWidth(clsMyPublic.dip2px(actStock.this,180));//宽度
					else if (i==2)
						s.setWidth(clsMyPublic.dip2px(actStock.this,300));
					else if (i==8)
						s.setWidth(clsMyPublic.dip2px(actStock.this,500));
					else 
						s.setWidth(clsMyPublic.dip2px(actStock.this,120));
				    */
					s.setHeight(40);
					
					TextView tv_tmp=null;
					if (i==0)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts);
					else if(i==1)
						tv_tmp=(TextView)findViewById(R.id.tv_pdts);
					else if(i==2)
						tv_tmp=(TextView)findViewById(R.id.tv_lstdts);
					else if(i==3)
						tv_tmp=(TextView)findViewById(R.id.tv_ztckts);
					else if(i==4)
						tv_tmp=(TextView)findViewById(R.id.tv_sccjts);
					else if(i==5)
						tv_tmp=(TextView)findViewById(R.id.tv_spphts);
					else if(i==6)
						tv_tmp=(TextView)findViewById(R.id.tv_jhslts);
					else if(i==7)
						tv_tmp=(TextView)findViewById(R.id.tv_sjslts);
					else if(i==8)
						tv_tmp=(TextView)findViewById(R.id.tv_zbzts);
					else if(i==9)
						tv_tmp=(TextView)findViewById(R.id.tv_bzdwts);
					else if(i==10)
						tv_tmp=(TextView)findViewById(R.id.tv_kcztts);
					else if(i==11)
						tv_tmp=(TextView)findViewById(R.id.tv_rkyzts);
					else if(i==12)
						tv_tmp=(TextView)findViewById(R.id.tv_ckyzts);
					else if(i==13)
						tv_tmp=(TextView)findViewById(R.id.tv_bhyzts);
					else if(i==14)
						tv_tmp=(TextView)findViewById(R.id.tv_bhykts);
					else if(i==15)
						tv_tmp=(TextView)findViewById(R.id.tv_ztkcts);
					else if(i==16)
						tv_tmp=(TextView)findViewById(R.id.tv_sdkcs);
					
					s.setWidth(tv_tmp.getWidth());//宽度
					
					if (i==0|| i==1 || i==2 ||i==6||i==7||i==8)
						s.setGravity(Gravity.LEFT);
					else
						s.setGravity(Gravity.RIGHT);				    
				    ll_detail.addView(s);//放入LinearLayout
				}
				return ll_detail;//将此LinearLayout返回
			}        	
        };        
        lv_mainTask.setAdapter(ba_detail);
	}
	
	
	private void mDoReturnData(Message msg)
	{
		String json="",sFunNo="",sRet1="";
		json=(String)msg.obj;
		
		try
		{
			//Log.v("zms","2"+json);
			//tv_MessTs.setText(json);
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
	    			if(jsonArray.length()<20)
	    			{
	    				bt_PgDown.setEnabled(false);
	    				bt_PgDown.setBackgroundResource(R.drawable.clrunenabled);
	    			}
		    		if (jsonArray.length()>0)
		    		{
		    			m_iSumReceCount=jsonArray.length();
						m_receMainTask=new String[17][m_iSumReceCount];	
						
						for(int i=0;i<17;i++)
						{
							m_receMainTask[i]=new String[m_iSumReceCount];
						}
		    			for (int i = 0 ;i<jsonArray.length();i++)
		    			{
		    			  JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
		    			  m_receMainTask[0][i]=jsonObject2.getString("DISPLAY_LOCATION");
	            	      m_receMainTask[1][i]=jsonObject2.getString("GOODS_NO");
	            	      m_receMainTask[2][i]=jsonObject2.getString("GOODS_NAME");
	            	      m_receMainTask[3][i]=jsonObject2.getString("DRUG_SPEC");
	            	      m_receMainTask[4][i]=jsonObject2.getString("SPS");
	            	      m_receMainTask[5][i]=jsonObject2.getString("PCS");
	            	      m_receMainTask[6][i]=jsonObject2.getString("PACKAGE_UNIT");
	            	      m_receMainTask[7][i]=jsonObject2.getString("PACKAGE_QTY");
	            	      m_receMainTask[8][i]=jsonObject2.getString("MANUFACTURER");
	            	      m_receMainTask[9][i]=jsonObject2.getString("GOODS_LOTNO");
	            	      m_receMainTask[10][i]=jsonObject2.getString("STOCK_STATUS");

	            	      m_receMainTask[11][i]=jsonObject2.getString("INSTOREHOUSE_PREPLUS_QTY");
	            	      m_receMainTask[12][i]=jsonObject2.getString("OUTSTOREHOUSE_PREMINUS_QTY");
	            	      m_receMainTask[13][i]=jsonObject2.getString("RESTOCKING_PREPLUS_QTY");
	            	      m_receMainTask[14][i]=jsonObject2.getString("RESTOCKING_PREMINUS_QTY");
	            	      m_receMainTask[15][i]=jsonObject2.getString("INTRANSIT_QTY");
	            	      m_receMainTask[16][i]=jsonObject2.getString("IS_LOCKED");
		    			}
		    			
		    			mFillMainTaskData(m_receMainTask);
		    		}
		    		bt_Refresh.setEnabled(true);
	    			bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
	    		}
	    	}
	    	else
	    	{
	    		bt_Refresh.setEnabled(true);
	    		bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);	
	    		tv_MessTs.setText(ErrOrMsg);
	    	}
		}
		catch (Exception e) {
			e.printStackTrace();
			 Log.v("zms", "进去循环5"+e.toString());

		}  
	}
		private void mDoReturnData_old(Message msg)
		{
		try
		{
			String sReceData="",sFunNo="",sRet1="";
			sReceData=(String)msg.obj;
			sFunNo=clsMyPublic.GetStrInOfStr(sReceData, 0);
			if (sFunNo.compareTo(clsMyPublic.g_sktParamCXStock)==0)
			{			
				sRet1=clsMyPublic.GetStrInOfStr(sReceData, 1);
				if (sRet1.compareTo("-1")==0||sRet1.compareTo("0")==0)
				{
					tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
					bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
					bt_Refresh.setEnabled(true);
					return;
				}
				else if (sRet1.compareTo("-2")==0)
				{	
					tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
				}
				else
				{	
					tv_MessTs.setText(sReceData);
					if (m_receMainTask==null)
					{
						m_iSumReceCount=Integer.parseInt(sRet1);
						m_receMainTask=new String[17][m_iSumReceCount];				
						for(int i=0;i<17;i++)
						{
							m_receMainTask[i]=new String[m_iSumReceCount];
						}
						m_iCurReceIndex=-1;
					}
					m_iCurReceIndex++;
					if (m_iCurReceIndex==m_iSumReceCount)
					{
						mShowMessage("接受数据有异常，请重新尝试...或者联系系统管理员");
						return;
					}
					else if (m_iCurReceIndex>m_iSumReceCount)
					{
						return;
					}
					for(int i=0;i<17;i++)
					{
						m_receMainTask[i][m_iCurReceIndex]=clsMyPublic.GetStrInOfStr(sReceData, i+2);
					}
						
					mFillMainTaskData(m_receMainTask);
					if (m_iSumReceCount==(m_iCurReceIndex+1))
					{
						bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
						bt_Refresh.setEnabled(true);
					}
				}	
			}
		}
		catch (Exception e)
		{
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
	        mShowMessage(sErrLog);
	    }
		catch(UnknownError e)
		{
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
		}
	}	
	private  void mShowMessage(String _sMess)
	{
		try
		{
			new AlertDialog.Builder(actStock.this)
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
			sErrLog=m_sClsName +";mShowMessage;" + e.toString();
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
		}
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
	private void mViewBlueData(Message msg)
	{
		byte[] readBuf = (byte[]) msg.obj;
		String readMessage = new String(readBuf, 0, msg.arg1);
		readMessage=readMessage.trim();
		tv_cxtj.setText(readMessage);
		m_sQuery=readMessage;
		m_receMainTask=null;
		mSendData(1);
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
			tv_Cur=tv_cxtj;
		else if (m_iCurWorkIndex==2)
			tv_Cur=tv_spzjm;

		clsMyPublic.m_sKeys=tv_Cur.getText().toString();
		
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
