package wcs.cps;
import org.json.JSONArray;
import org.json.JSONObject;

import wcs.cps.R;
import wcs.cps.http.HttpTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class actMain extends Activity
{
	private Intent m_actPick=null;
	private Intent m_actBlnMgr=null;	
	private Intent m_actSupply=null;
	private Intent m_actLog=null;
	private Intent m_actStock=null;
	private Intent m_actWorkLog=null;
	private Intent mactPickBoxMatch=null;
	private Intent mactSupplyAvtive=null;
	private Button bt_refresh=null;
	private Button bt_Exit=null;
	private Button bt_Pick=null;
	private Button bt_BlueMgr=null;
	private Button bt_BhWork=null;
	private Button bt_RkWork=null;
	private Button bt_Log=null;
	private Button bt_Pdwork=null;
	private Button bt_kccx=null;
	private Button bt_zyjl=null;
	private Button bt_pczy=null;
	private Button bt_zzxcx = null;
	private TextView tv_MessTs=null;
	private TextView tv_Cur=null;
	private ListView lv_mainTask=null;
	private ListView lv_SumTask=null;
	private String[][] m_receMainTask;
	private String[][] m_receSumData;
	private int m_iCurReceIndex=-1;
	private int m_iSumReceCount=-1;
	private String m_sPrnBoxNo="";  //打印标签条码
	private String m_sClsName="actMain";
	private int m_curType=0;
	
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
        setContentView(R.layout.main);
        this.setTitleColor(Color.BLUE);
        this.setTitle("WCPS-无线拣选小车系统 主界面 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】" +
                      "首选拣货区域【" + clsMyPublic.g_WorkAreaNo +"】");

        mSetSysData();
        mSendData(1);
    }
	@Override
	protected void onResume()
	{
	    super.onResume();
	    clsMyPublic.m_hdlSendMess=hd;
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
			_httpTool.m_params.put("func_code", "ShowTpcTask");  
			_httpTool.m_params.put("josnparas", "{}");  
			//_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\"}");  
			_httpTool.mStart();
		}
		else if (_Type==2)
		{
			m_curType=2;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ObtainTask");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\"}");  
			_httpTool.mStart();
		}
		else if (_Type==3)
		{
			m_curType=3;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowObtainedTask");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\"}");  
			_httpTool.mStart();
		}
		else if (_Type==7)
		{
			m_curType=7;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "Logout");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"IP\":\""+HttpTool.m_SvrIp+"\"}");  
			
			_httpTool.mStart();
		}

	}
	
	private void mSetSysData()
	{
    	bt_refresh=(Button)findViewById(R.id.bt_refresh);
    	bt_Exit=(Button)findViewById(R.id.bt_Exit);
    	bt_Pick=(Button)findViewById(R.id.bt_pickwork);
    	bt_BhWork=(Button)findViewById(R.id.bt_bhwork);
    	bt_kccx=(Button)findViewById(R.id.bt_kccx);
        bt_zyjl=(Button)findViewById(R.id.bt_zyjl);
        bt_pczy=(Button)findViewById(R.id.bt_pc);
        bt_zzxcx=(Button)findViewById(R.id.bt_zzxcx);
    	tv_MessTs=(TextView)findViewById(R.id.tv_ts);
    	lv_mainTask=(ListView)findViewById(R.id.listView1);
    	lv_SumTask=(ListView)findViewById(R.id.listView2);
    	//刷新事务事件
    	bt_refresh.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					if (clsMyPublic.Utils.isFastClick()) {
				        return ;
				    }
					mSendData(1);
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
    					mSendData(7);
    				}
    		    }	
    	);
    	
    	//拣货作业
    	bt_Pick.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					//mViewUi(1);
    					mSetButton(1);
    			    	mSendData(2);
    				}
    		    }	
        );
    	/*
    	bt_Pdwork.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mViewUi(12);
    				}
    		    }	
        );*/
    	
    	bt_zzxcx.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					//mViewUi(3);
    					//mSendData(4);
    					mViewUi(14);  //主动补货
    				}
    		    }	
    	);
    	
    	//补货作业
    	bt_BhWork.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					//mViewUi(3);
    					//mSendData(4);
    					mViewUi(11);  //主动补货
    				}
    		    }	
        );

    	bt_pczy.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mViewUi(13);
    				}
    		    }	
    	);
    	bt_kccx.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mViewUi(7);
    				}
    		    }	
    	);
    	bt_zyjl.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mViewUi(8);
    				}
    		    }	
    	);

	}
	
	private void mSetButton(int _Type)
	{
		if (_Type==1)
		{
			bt_refresh.setEnabled(false);
			bt_refresh.setBackgroundResource(R.drawable.clrunenabled);	
	    	bt_Pick.setEnabled(false);
	    	bt_Pick.setBackgroundResource(R.drawable.clrunenabled);	
	    	bt_BhWork.setEnabled(false);
	    	bt_BhWork.setBackgroundResource(R.drawable.clrunenabled);	
	
	    	bt_kccx.setEnabled(false);
	    	bt_kccx.setBackgroundResource(R.drawable.clrunenabled);	
	    	bt_zyjl.setEnabled(false);
	    	bt_zyjl.setBackgroundResource(R.drawable.clrunenabled);	
		}
		else if(_Type==2)
		{
			bt_refresh.setEnabled(true);
			bt_refresh.setBackgroundResource(R.drawable.clsbtnselect);
			bt_Pick.setEnabled(true);
			bt_Pick.setBackgroundResource(R.drawable.clsbtnselect);	
	    	bt_BhWork.setEnabled(true);
	    	bt_BhWork.setBackgroundResource(R.drawable.clsbtnselect);	
	    	
	    	bt_kccx.setEnabled(true);
	    	bt_kccx.setBackgroundResource(R.drawable.clsbtnselect);	
	    	bt_zyjl.setEnabled(true);
	    	bt_zyjl.setBackgroundResource(R.drawable.clsbtnselect);
		}
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
				LinearLayout ll_detail=new LinearLayout(actMain.this);
				ll_detail.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
				ll_detail.setPadding(5,5,5,5);//四周留白

				for(int i=0;i<msg.length;i++)//为每一行设置显示的数据
				{					    
					TextView s= new TextView(actMain.this);
					s.setText(msg[i][arg0]+" ");//TextView中显示的文字
					s.setTextSize(25);//字体大小
					s.setTextColor(Color.BLUE);
					s.setPadding(1,2,2,1);//四周留白
					//s.setWidth(clsMyPublic.dip2px(actMain.this,135));//宽度
					s.setHeight(50);
					
					
					TextView tv_tmp=null;
					if (i==0)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts);
					else if(i==1)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts3);
					else if(i==2)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts5);
					else if(i==3)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts6);
					else if(i==4)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts7);
					else if(i==5)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts8);
					else if(i==6)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts10);
					else if(i==7)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts2);
					else if(i==8)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts9);
					else if(i==9)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts4);
					

					s.setWidth(tv_tmp.getWidth());//宽度
					
					if (i==0)
						s.setGravity(Gravity.CENTER);	
					else
						s.setGravity(Gravity.CENTER);
				    ll_detail.addView(s);//放入LinearLayout
				}
				return ll_detail;//将此LinearLayout返回
			}        	
        };        
        lv_mainTask.setAdapter(ba_detail);
	}	
	private void mFillSumTaskData(String[][] _sData)
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
				LinearLayout ll_detail=new LinearLayout(actMain.this);
				ll_detail.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
				ll_detail.setPadding(5,5,5,5);//四周留白

				for(int i=0;i<msg.length;i++)//为每一行设置显示的数据
				{					    
					TextView s= new TextView(actMain.this);
					s.setText(" "+msg[i][arg0]);//TextView中显示的文字
					s.setTextSize(25);//字体大小
					s.setTextColor(Color.RED);
					s.setPadding(1,2,2,1);//四周留白
					//s.setWidth(clsMyPublic.dip2px(actMain.this,135));//宽度
					s.setHeight(50);
					
					TextView tv_tmp=null;
					if (i==0)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts);
					else if(i==1)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts3);
					else if(i==2)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts5);
					else if(i==3)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts6);
					else if(i==4)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts7);
					else if(i==5)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts8);
					else if(i==6)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts10);
					else if(i==7)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts2);
					else if(i==8)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts9);
					else if(i==9)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts4);
					

					s.setWidth(tv_tmp.getWidth());//宽度
					
					if (i==0)
						s.setGravity(Gravity.CENTER);	
					else
						s.setGravity(Gravity.CENTER);			    
				    ll_detail.addView(s);//放入LinearLayout
				}
				return ll_detail;//将此LinearLayout返回
			}        	
        };        
        lv_SumTask.setAdapter(ba_detail);
	}
	
	
	private void mViewUi(int _UiNo)
	{
		switch(_UiNo)
		{
		case 1:
			actPickWork.m_iWorkMode=clsMyPublic.JHZY_XJ;
			m_actPick=new Intent(this,actPickWork.class);
			startActivityForResult(m_actPick,1);
			break;
		case 2:
			break;
		case 3:
			actSupply.m_iWorkMode=clsMyPublic.SJZY_BH;
			m_actSupply=new Intent(this,actSupply.class);
			startActivityForResult(m_actSupply,3);
			break;
		case 4:
			
			break;
		case 5:
			actSupply.m_iWorkMode=clsMyPublic.SJZY_RK;
			m_actSupply=new Intent(this,actSupply.class);
			startActivityForResult(m_actSupply,5);
			break;
		case 6:
			actPickWork.m_iWorkMode=clsMyPublic.JHZY_PD;
			m_actPick=new Intent(this,actPickWork.class);
			startActivityForResult(m_actPick,6);
			break;
		case 7:
			m_actStock=new Intent(this,actStock.class);
			startActivityForResult(m_actStock,7);
			break;
		case 8:
			m_actWorkLog=new Intent(this,actWorkLog.class);
			startActivityForResult(m_actWorkLog,8);
			break;
		case 9:			
			actSupply.m_iWorkMode=clsMyPublic.SJZY_BDBH;
			m_actSupply=null;
			m_actSupply=new Intent(this,actSupply.class);
			startActivityForResult(m_actSupply,9);
			break;
		case 10:
			mactPickBoxMatch=null;
			mactPickBoxMatch=new Intent(this,actPickBoxMatch.class);
			startActivityForResult(mactPickBoxMatch,10);
			break;
		case 11:			
			actSupply.m_iWorkMode=clsMyPublic.SJZY_ZDBH;
			m_actSupply=null;
			m_actSupply=new Intent(this,actSupply.class);
			startActivityForResult(m_actSupply,11);
			break;
		case 12:
			//mactSupplyAvtive=new Intent(this,actSupplyActive.class);
			//startActivityForResult(mactSupplyAvtive,12);
			break;
		case 13:
			mactSupplyAvtive=new Intent(this,actCheckStock.class);
			startActivityForResult(mactSupplyAvtive,13);
			break;
		case 14:
			mactSupplyAvtive=new Intent(this,actQuery.class);
			startActivityForResult(mactSupplyAvtive,13);
			break;
		}

	}
	private void mDoReturnData(Message msg)
	{	
		String json="",sRet1="";
		json=(String)msg.obj;
		
		try
		{
			sRet1=json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length()-1).toString());//sRet1.substring(1, sRet1.length()-1).toString()
			
			//Log.v("zms","2"+json);
			//tv_MessTs.setText(json);
			//JSONObject jsonObject = new JSONObject(json.toString());
	    	//jsonObject = new JSONObject(jsonObject.getString(HttpTool.m_sktResult));
	    	String ErrOrMsg =jsonObject.getString("ERRORMSG"); 
	    	if(ErrOrMsg.isEmpty())
	    	{
	    		JSONArray jsonArray =new JSONArray(jsonObject.getString("StrJson")); 
	    		if(m_curType==1)
	    		{
		    		if (jsonArray.length()>0)
		    		{
		    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
		    			m_receMainTask=new String[10][jsonArray.length()];
		    			m_iSumReceCount=jsonArray.length();
						m_receSumData=new String[10][1];	
						
						for(int i=0;i<10;i++)
						{
							m_receMainTask[i]=new String[m_iSumReceCount];
							m_receSumData[i]=new String[1];
							m_receSumData[i][0]="0";
						}
						
		    			for (int i=0;i<jsonArray.length();i++)
	            	    {

	            	        JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
	            	       //   str="第"+i+"个,AREA_NO:"+jsonObject2.getString("AREA_NO")+"SELFSERVICE_OSH_NUM:"+jsonObject2.getString("SELFSERVICE_OSH_NUM");
	            	        m_receMainTask[0][i]=jsonObject2.getString("AREA_NO");
	            	        m_receMainTask[1][i]=jsonObject2.getString("GREENCHANNEL_NUM");
	            	        m_receMainTask[2][i]=jsonObject2.getString("SELFSERVICE_OSH_NUM");
	            	        m_receMainTask[3][i]=jsonObject2.getString("COMMON_OSH_NUM");
	            	        m_receMainTask[4][i]=jsonObject2.getString("PASSIVE_RESTOCKING_NUM");
	            	        m_receMainTask[5][i]=jsonObject2.getString("INITIATIVE_RESTOCKING_NUM");
	            	        m_receMainTask[6][i]=jsonObject2.getString("PURCHASERETURN_NUM");
	            	        m_receMainTask[7][i]=jsonObject2.getString("STOCKTAKING_NUM");
	            	        m_receMainTask[8][i]=jsonObject2.getString("IN_STOREHOUSE_UPSHELF");
	            	        m_receMainTask[9][i]=jsonObject2.getString("BINDED_TOTEBOX_NUM");
	            	        
	            	       for(int j=1;j<10;j++)
	            	        {
			    				m_receSumData[j][0]=String.valueOf(Integer.parseInt(m_receSumData[j][0])+Integer.parseInt(m_receMainTask[j][i]));	
	            	        }
	            	    }
		    			
		    			mFillMainTaskData(m_receMainTask);
						mFillSumTaskData(m_receSumData);
		    		}
	    		}
	    		else if(m_curType==2)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			String sWrkType="";
					
	    			if (jsonArray.length()>0)
		    		{
	    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(0); 
	    				sWrkType=jsonObject2.getString("v");
		    		}
	    			else
	    			{
	    				mSetButton(2);//20161116 whstruts 未获取到数据时UI处理
	    				return;
	    			}
					
					sWrkType=sWrkType.trim();
					if(sWrkType.compareTo(clsMyPublic.g_SqWorkType_Lstd)==0||
					   sWrkType.compareTo(clsMyPublic.g_SqWorkType_Zjjh)==0||
					   sWrkType.compareTo(clsMyPublic.g_SqWorkType_Ztzy)==0||
					   sWrkType.compareTo(clsMyPublic.g_SqWorkType_Ptzy)==0||
					   sWrkType.compareTo(clsMyPublic.g_SqWorkType_Tqjx)==0||
					   sWrkType.compareTo(clsMyPublic.g_SqWorkType_Gjtc)==0||
					   sWrkType.compareTo(clsMyPublic.g_SqWorkType_Tqjx2)==0)
					{
						//判断是否先关联周转箱，还是可以拣货作业
						mSendData(3);
						return;
					}
					else if (sWrkType.compareTo(clsMyPublic.g_SqWorkType_Pdzy)==0)
					{
						//盘点作业
						mViewUi(6);					
					}
					else if (sWrkType.compareTo(clsMyPublic.g_SqWorkType_Bdbh)==0)
					{
						mViewUi(9);
					}
					else if (sWrkType.compareTo(clsMyPublic.g_SqWorkType_Zdbh)==0)
					{
						mViewUi(11);
					}
					else if (sWrkType.compareTo(clsMyPublic.g_SqWorkType_Rksj)==0)
					{
						//入库上架
						mViewUi(5);					
					}
	    		}
	    		else if(m_curType==3)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			
	    			for (int i=0;i<jsonArray.length();i++)
		    		{
	    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 

	    				if(jsonObject2.getString("TOTEBOX_NO").isEmpty() && jsonObject2.getString("BASKET_NO").isEmpty())
	    				{
	    					mViewUi(10);
	    					return;
	    				}
	    				
	    				if(jsonObject2.getString("OPER_CATE") == clsMyPublic.g_SqWorkType_Tqjx)
	    				{
	    					if(jsonObject2.getString("BASKET_NO").isEmpty())
		    				{
		    					mViewUi(10);
		    					return;
		    				}
	    				}
	    				else
	    				{
	    					if(jsonObject2.getString("TOTEBOX_NO").isEmpty())
		    				{
		    					mViewUi(10);
		    					return;
		    				}
	    				}
		    		}
	    			//Log.v("zms","4");
	    			mViewUi(1);
	    			
	    		}
	    		else if(m_curType==7)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			mQueryExitSys();
	    		}
	    	}
	    	else
	    	{
	    		tv_MessTs.setText(ErrOrMsg);
	    		mSetButton(2);
	    	}
			

		}
		catch (Exception e)
		{
			 Log.v("zms", e.toString());
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
	        tv_MessTs.setText(sErrLog);
		}
		catch(UnknownError e)
		{
			Log.v("zms", e.toString());
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
	        tv_MessTs.setText(sErrLog);
		}
	}
	private void mDoDisMessage(Message msg)
	{
		String sMess=(String)msg.obj;
		tv_MessTs.setText(sMess);
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
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		mSetButton(2);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case 10:
			switch(resultCode)
			{
			case 2:
				//关联周转箱任务结束，直接进入拣货界面
				mViewUi(1);
				break;
			}			
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
		    						m_sPrnBoxNo=clsMyPublic.m_sKeys;
		    						mSendData(5);
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
	private void mQueryExitSys()
	{
        new AlertDialog.Builder(actMain.this)
               .setTitle("温馨提示")
               .setIcon(android.R.drawable.alert_dark_frame)
               .setMessage("您确定要退出系统吗？")
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
	    			    mSendData(6);
	   					Intent intent=new Intent();
	   					intent.putExtra("UINO", "MainUI");
	   					setResult(3,intent);
	      				finish();
	    		   }
	    		 }).show();
	}
}
