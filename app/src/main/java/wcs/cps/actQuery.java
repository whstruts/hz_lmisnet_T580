package wcs.cps;


import org.json.JSONArray;
import org.json.JSONObject;

import wcs.cps.actPickWork.clsPickTask;
import wcs.cps.http.HttpTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class actQuery extends Activity
{
	
	private Button bt_Return=null;
	private Button bt_Refresh=null;
	private Button bt_Up=null;
	private Button bt_Down=null;
	private ListView lv_mainTask=null;
	private ListView lv_HzTask=null;
	private TextView tv_MessTs=null;
	private TextView tv_Cur=null;	
	private TextView tv_cxtj=null;
	
	private String[][] m_receMainTask;
	private String[][] m_HzMainTask;
	
	private int m_iCurReceIndex=-1;
	private int m_iSumReceCount=-1;
	
	public static String m_sFpdNo="";
	public static String m_sAreaNo="";
	public static String m_sBoxNo="";
	public static int m_iCurBoxIndex=0;
	public static String[] m_sQueryBoxNo;
	public static int m_iMaxBoxIndex=0;
	
	private int[] m_selectColor = null;
	
	private EditText edtInput=null;
	
	private String m_sClsName="actQuery";
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
				mDoReturnData(msg,m_curType);
				break;
			}
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actquery);
        this.setTitleColor(Color.BLUE);
        this.setTitle("WCPS-无线拣选小车系统 【拣货复查】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");
        clsMyPublic.m_hdlSendMess=hd;   
        mSetSysData();
        mSetButtonEanble();
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
			_httpTool.m_params.put("func_code", "ShowTaskBillTask");  
			_httpTool.m_params.put("josnparas", "{\"AreaNo\":\""+m_sAreaNo+"\",\"TaskBillNo\":\""+m_sFpdNo+"\"}");  
		}
		else if(_Type==2)
		{
			m_curType=2;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowTpcToteboxQuery");  
			_httpTool.m_params.put("josnparas", "{\"DataValue\":\""+m_sFpdNo+"\"}");  
		}
		
		_httpTool.mStart();
		/*
		String _sendData="";
		clsSocketCln.m_CurHandler=hd;
		if (_Type==1)
		{
			_sendData=clsMyPublic.g_SktParamCXPick +";" +
					  m_sFpdNo +";" +
					  m_sAreaNo +";";
		    bt_Refresh.setEnabled(false);
		    bt_Refresh.setBackgroundResource(R.drawable.clrunenabled);	
		}
		clsSocketCln.m_SendData=_sendData;
		clsSocketCln _SktCln=new clsSocketCln();
		_SktCln.mStart();
		*/
	}
	
	private void mSetButtonEanble()
	{
		
		//向上翻
		if (m_iCurBoxIndex<=0)
		{
			bt_Up.setEnabled(false);
			bt_Up.setBackgroundResource(R.drawable.clrunenabled);	
			bt_Down.setEnabled(false);
			bt_Down.setBackgroundResource(R.drawable.clrunenabled);
		}
		else
		{
			bt_Up.setEnabled(true);
			bt_Up.setBackgroundResource(R.drawable.clsbtnselect);
			
			bt_Down.setEnabled(false);
			bt_Down.setBackgroundResource(R.drawable.clrunenabled);
		}
		
		if(m_iMaxBoxIndex==m_iCurBoxIndex)
		{			
			bt_Down.setEnabled(false);
			bt_Down.setBackgroundResource(R.drawable.clrunenabled);
		}
		else
		{
			bt_Down.setEnabled(true);
			bt_Down.setBackgroundResource(R.drawable.clsbtnselect);
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
		bt_Up=(Button)findViewById(R.id.bt_Up);
		bt_Down=(Button)findViewById(R.id.bt_Down);
		
		tv_cxtj=(TextView)findViewById(R.id.tv_cxtj);
		
    	tv_MessTs=(TextView)findViewById(R.id.tv_ts);
		lv_mainTask=(ListView)findViewById(R.id.listView1);
		lv_HzTask=(ListView)findViewById(R.id.listView2);
		
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
	    				tv_cxtj.setText(s);
	    				m_sFpdNo=tv_cxtj.getText().toString();
	    				
	    				m_receMainTask=null;
	    				m_iCurBoxIndex=0;
	    				
						mSendData(2);
						
	    				edtInput.setText("");  				
	    			}
	    			return false;
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
					m_sFpdNo="";
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
					m_sFpdNo=tv_cxtj.getText().toString();
    				m_iCurBoxIndex=0;
    				
					mSendData(2);
				    v.setEnabled(false);
				    v.setBackgroundResource(R.drawable.clrunenabled);	
				}
		    }
        );
    	tv_cxtj.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					mNumKeys(); 
				}
		    }
        );
    	
    	bt_Up.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					if(m_iCurBoxIndex>0)
					{
						m_iCurBoxIndex--;
						m_sFpdNo=m_sQueryBoxNo[m_iCurBoxIndex];
						m_receMainTask=null;
						mSetButtonEanble(); 
						mSendData(1);
					}
				}
		    }
        );
    	bt_Down.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					if(m_iCurBoxIndex<m_iSumReceCount)
					{
						
						m_sFpdNo=m_sQueryBoxNo[m_iCurBoxIndex];
						m_receMainTask=null;
						mSetButtonEanble(); 
						mSendData(1);
						m_iCurBoxIndex++;
					}
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
				LinearLayout ll_detail=new LinearLayout(actQuery.this);
				ll_detail.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
				ll_detail.setPadding(5,5,5,5);//四周留白
								
				if(m_selectColor[arg0]==1)
				{
					ll_detail.setBackgroundColor(Color.TRANSPARENT);	
					ll_detail.setBackgroundColor(Color.RED);	
				}
				else
				{
					ll_detail.setBackgroundColor(Color.TRANSPARENT);	
					
					if (msg[10][arg0]!=null)
					{
						if (msg[10][arg0].compareTo("索取")!=0)
							ll_detail.setBackgroundColor(Color.GREEN);
					}
				}
				
				for(int i=0;i<msg.length;i++)//为每一行设置显示的数据
				{	
															
					TextView s= new TextView(actQuery.this);
				
					s.setText(msg[i][arg0]+" ");//TextView中显示的文字
					s.setTextSize(20);//字体大小
					s.setTextColor(Color.BLUE);
					s.setPadding(1,2,2,1);//四周留白
					/*					
					if (i==0 || i==4 || i==5|| i==6 || i==7|| i==11 || i==12)
						s.setWidth(clsMyPublic.dip2px(actQuery.this,120));//宽度
					else if (i==1||i==3)
						s.setWidth(clsMyPublic.dip2px(actQuery.this,180));//宽度
					else if (i==2 || i==8|| i==10)
						s.setWidth(clsMyPublic.dip2px(actQuery.this,300));
					else if(i==9)
						s.setWidth(clsMyPublic.dip2px(actQuery.this,500));
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
						tv_tmp=(TextView)findViewById(R.id.tv_jhslts);
					else if(i==5)
						tv_tmp=(TextView)findViewById(R.id.tv_sjslts);
					else if(i==6)
						tv_tmp=(TextView)findViewById(R.id.tv_zbzts);
					else if(i==7)
						tv_tmp=(TextView)findViewById(R.id.tv_bzdwts);
					else if(i==8)
						tv_tmp=(TextView)findViewById(R.id.tv_spphts);
					else if(i==9)
						tv_tmp=(TextView)findViewById(R.id.tv_sccjts);
					else if(i==10)
						tv_tmp=(TextView)findViewById(R.id.tv_rwztts);
					else if(i==11)
						tv_tmp=(TextView)findViewById(R.id.tv_tqjxts);
					else if(i==12)
						tv_tmp=(TextView)findViewById(R.id.tv_jxlts);
					else if(i==13)
						tv_tmp=(TextView)findViewById(R.id.tv_fhtno);
					

					s.setWidth(tv_tmp.getWidth());//宽度
					
					
					if (i==4|| i==5 || i==6|| i==7)
						s.setGravity(Gravity.RIGHT);
					else if (i==8)
						s.setGravity(Gravity.CENTER);
					else
						s.setGravity(Gravity.LEFT);					
				    ll_detail.addView(s);//放入LinearLayout
				}
				return ll_detail;//将此LinearLayout返回
			}        	
        };        
        lv_mainTask.setAdapter(ba_detail);
        
        lv_mainTask.setOnItemClickListener//为列表添加监听
        (
           new OnItemClickListener()
           {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) //arg2为点击的第几项
				{
					
					if(m_selectColor[arg2]!=1)
					{
						m_selectColor[arg2]=1;
						arg1.setBackgroundColor(Color.RED);
					}
					else
					{
						m_selectColor[arg2]=0;
						if (msg[10][arg2].compareTo("索取")!=0)
						{
							arg1.setBackgroundColor(Color.GREEN);
						}
						else
						{
							arg1.setBackgroundColor(Color.TRANSPARENT);	
						}
					}
					 
					//mShowMessage(sMess);
				}        	   
           }
        );
        
        lv_mainTask.deferNotifyDataSetChanged();
	}
	
	private void mFillHzTaskData(String[][] _sData)
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
				LinearLayout ll_detail=new LinearLayout(actQuery.this);
				ll_detail.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
				ll_detail.setPadding(5,5,5,5);//四周留白				
				for(int i=0;i<msg.length;i++)//为每一行设置显示的数据
				{					    
					TextView s= new TextView(actQuery.this);
					s.setText(msg[i][arg0]+" ");//TextView中显示的文字
					s.setTextSize(20);//字体大小
					s.setTextColor(Color.RED);
					s.setPadding(1,2,2,1);//四周留白
					if (i==0 || i==1 || i==3 || i==5 || i==7)
						s.setWidth(100); 
					else 
					{
						s.setWidth(150);
						s.setGravity(Gravity.LEFT);
					}
				    ll_detail.addView(s);//放入LinearLayout
				}
				return ll_detail;//将此LinearLayout返回
			}   	
        }; 
        lv_HzTask.setAdapter(ba_detail);
	}
	
	private void mDoReturnData(Message msg,int curType)
	{	
		String json="",sFunNo="",sRet1="",sMess="";
		try
		{
			json=(String)msg.obj;
			Log.v("zms","拣货"+json);
			//JSONObject jsonObject = new JSONObject(json.toString());
	    	//jsonObject = new JSONObject(jsonObject.getString(HttpTool.m_sktResult));
			sRet1=json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length()-1).toString());
	    	String ErrOrMsg =jsonObject.getString("ERRORMSG"); 
	    	if(ErrOrMsg.isEmpty())
	    	{
	    		JSONArray jsonArray =new JSONArray(jsonObject.getString("StrJson")); 
	    		if((m_curType==1||m_curType==2)&& jsonArray.length()>0)
	    		{
	    			//Log.v("zms","拣货数据条数"+Integer.toString(jsonArray.length()));
	    			m_iSumReceCount=jsonArray.length();
					m_iMaxBoxIndex = m_iSumReceCount;
					
					m_receMainTask=null;
					m_iCurBoxIndex=0;
					
					if (m_receMainTask==null)
					{
						m_HzMainTask=new String[3][1];
						m_HzMainTask[0]=new String[1];
						m_HzMainTask[0][0]="合计:";					
						m_HzMainTask[1]=new String[1];
						m_HzMainTask[1][0]="品规数:";
						m_HzMainTask[2]=new String[1];					
						m_HzMainTask[2][0]=String.valueOf(m_iSumReceCount);
						mFillHzTaskData(m_HzMainTask);
						m_iCurReceIndex=-1;
					}
					
					m_receMainTask=new String[14][m_iSumReceCount];	
					m_selectColor = new int[m_iSumReceCount+1];
					
					for(int i=0;i<13;i++)
					{
						m_receMainTask[i]=new String[m_iSumReceCount];
					}
					
	    			for (int i=0;i<jsonArray.length();i++)
		    		{
	    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
	    				
						m_iCurReceIndex++;
										
						if (m_iCurReceIndex==m_iSumReceCount)
						{
							bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
							bt_Refresh.setEnabled(true);
							mShowMessage("接受数据有异常，请重新尝试...或者联系系统管理员");
							return;
						}
						else if (m_iCurReceIndex>m_iSumReceCount)
						{
							bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
							bt_Refresh.setEnabled(true);
							return;
						}
						
						m_receMainTask[0][i]=jsonObject2.getString("TOTEBOX_NO");
            	        m_receMainTask[1][i]=jsonObject2.getString("DISPLAY_LOCATION");
            	        m_receMainTask[2][i]=jsonObject2.getString("GOODS_NAME");
            	        m_receMainTask[3][i]=jsonObject2.getString("DRUG_SPEC");
            	        m_receMainTask[4][i]=jsonObject2.getString("PLANNED_QTY");
            	        m_receMainTask[5][i]=jsonObject2.getString("ACTUAL_QTY");
            	        m_receMainTask[6][i]=jsonObject2.getString("M_PACKAGE_QTY");
            	        m_receMainTask[7][i]="";//jsonObject2.getString("SPLIT_GRANULARITY_CN");
            	        m_receMainTask[8][i]=jsonObject2.getString("GOODS_LOTNO");
            	        m_receMainTask[9][i]=jsonObject2.getString("MANUFACTURER");
            	        if(m_curType==1)
            	        {
            	          m_receMainTask[10][i]=jsonObject2.getString("TASK_STATUS_CN");
            	          m_receMainTask[11][i]=jsonObject2.getString("IS_PREPICK");
            	        }
            	        else if(m_curType==2)
            	        {
            	          m_receMainTask[10][i]=jsonObject2.getString("TASK_STATUS");
              	          m_receMainTask[11][i]=jsonObject2.getString("IS_PREPICK_LOC");
            	        }
            	        m_receMainTask[12][i]=jsonObject2.getString("BASKET_NO");
            	        m_receMainTask[13][i]=jsonObject2.getString("BUFFER_NO");
            	        
						mFillMainTaskData(m_receMainTask);
						if (m_iSumReceCount==(m_iCurReceIndex+1))
						{
							//保证一次查询借宿后，才开始下次查询
							bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
							bt_Refresh.setEnabled(true);
							mSetButtonEanble();
						}
						//jsonObject2.getString("BILL_HDR_ID");
		    		}
	    		}
	    		else 
	    		{
	    			bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
					bt_Refresh.setEnabled(true);
		    		tv_MessTs.setText(ErrOrMsg);
				}
	    	}
	    	else
	    	{
	    		bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
				bt_Refresh.setEnabled(true);
	    		tv_MessTs.setText(ErrOrMsg);
	    	}
			/*
			String sReceData="",sFunNo="",sRet1="";
			sReceData=(String)msg.obj;
			sFunNo=clsMyPublic.GetStrInOfStr(sReceData, 0);
			if (sFunNo.compareTo(clsMyPublic.g_SktParamCXPick)==0)
			{			
				sRet1=clsMyPublic.GetStrInOfStr(sReceData, 1);
				if (sRet1.compareTo("-1")==0||sRet1.compareTo("0")==0)
				{
					//mShowMessage(clsMyPublic.GetStrInOfStr(sReceData, 2));
					bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
					bt_Refresh.setEnabled(true);
					mSetButtonEanble();
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
						m_iMaxBoxIndex = m_iSumReceCount;
						m_receMainTask=new String[13][m_iSumReceCount];				
						for(int i=0;i<13;i++)
						{
							m_receMainTask[i]=new String[m_iSumReceCount];
						}
						
						m_HzMainTask=new String[3][1];
						m_HzMainTask[0]=new String[1];
						m_HzMainTask[0][0]="合计:";					
						m_HzMainTask[1]=new String[1];
						m_HzMainTask[1][0]="品规数:";
						m_HzMainTask[2]=new String[1];					
						m_HzMainTask[2][0]=String.valueOf(m_iSumReceCount);
						mFillHzTaskData(m_HzMainTask);
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
					
					for(int i=0;i<13;i++)
					{
						m_receMainTask[i][m_iCurReceIndex]=clsMyPublic.GetStrInOfStr(sReceData, i+2);
					}					
					mFillMainTaskData(m_receMainTask);
					if (m_iSumReceCount==(m_iCurReceIndex+1))
					{
						//保证一次查询借宿后，才开始下次查询
						bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
						bt_Refresh.setEnabled(true);
						mSetButtonEanble();
					}
				}

			}*/
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
		tv_cxtj.setText(readMessage);			
	}
	
	private  void mShowMessage(String _sMess)
	{
		try
		{
			new AlertDialog.Builder(actQuery.this)
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
	
	private void mNumKeys()
	{
		LinearLayout loginLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.actnumkeys, null);
		final AlertDialog adNumKey=
		new AlertDialog.Builder(this)
				.setTitle("键盘").setView(loginLayout).create();
		Window win=adNumKey.getWindow();
		WindowManager.LayoutParams lp=win.getAttributes();
		lp.x=200;
		lp.y=200;
		win.setAttributes(lp);
		clsMyPublic.m_sKeys="";
		
		tv_Cur=tv_cxtj;
		
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
}
