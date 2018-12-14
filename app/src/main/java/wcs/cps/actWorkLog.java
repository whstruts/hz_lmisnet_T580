package wcs.cps;

import java.sql.Date;
import java.text.SimpleDateFormat;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class actWorkLog extends Activity
{
	private Button bt_Return=null;
	private Button bt_Refresh=null;
	
	private Button bt_PgUp=null;
	private Button bt_PgDown=null;
	
	private ListView lv_mainTask=null;
    private ListView lv_HzTask=null;
	private TextView tv_MessTs=null;
	private TextView tv_ygxx=null;
	private TextView tv_startDate=null;
	private TextView tv_endDate=null;
	private TextView tv_boxno=null;
	private TextView tv_Cur=null;
	
	private EditText edtInput=null;
	
	private String[][] m_receMainTask;
    private String[][] m_HzMainTask;
	private String m_sStartDate="";
	private String m_sEndDate="";
	private int m_iCurReceIndex=-1;
	private int m_iSumReceCount=-1;
	private int m_iCurBoxIndex=0;
	private int m_iReceHzCount=0;
	private String m_sCurBarCode="";
	private int m_sPageNum = 1;
	
	private Intent m_actLog=null;
	
	private String m_sClsName="actWorkLog";

	//private int[] m_selectColor = new int[50];
	private int[] m_selectColor = null;
	
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
        setContentView(R.layout.actworklog);
        this.setTitleColor(Color.BLUE);
        this.setTitle("WCPS-无线拣选小车系统 【员工作业记录查询】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");
        clsMyPublic.m_hdlSendMess=hd;
        mSetSysData();
    }
	
	public void mSendData(int _Type)
	{

		m_sStartDate=tv_startDate.getText().toString();
		m_sEndDate=tv_endDate.getText().toString();
		m_sCurBarCode=tv_boxno.getText().toString();
		HttpTool _httpTool=new HttpTool();		
		_httpTool.m_CurHandler=hd;
		_httpTool.m_urlPath = "http://"+HttpTool.m_SvrIp+":"+HttpTool.m_SvrPort + HttpTool.m_sktNetPath;
		m_sCurBarCode=tv_boxno.getText().toString();
		if (_Type==1)
		{
			m_curType =1;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowOperationRecord"); 
			_httpTool.m_params.put("josnparas", "{\"StaffName\":\""+clsMyPublic.g_WorkName+"\",\"StartDate\":\""+m_sStartDate+
					"\",\"FinishDate\":\""+m_sEndDate+"\",\"ContainerNo\":\""+m_sCurBarCode+"\",\"PageNum\":"+m_sPageNum+"}");  
			_httpTool.mStart();
		}
		if(_Type==2)
		{
			m_curType=2;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);
			_httpTool.m_params.put("func_code", "ShowOperationRecordSum");
			_httpTool.m_params.put("josnparas", "{\"StaffName\":\""+clsMyPublic.g_WorkName+"\"}");
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
		
		bt_PgUp=(Button)findViewById(R.id.bt_uppage);
		bt_PgDown=(Button)findViewById(R.id.bt_downpage);
		
    	tv_MessTs=(TextView)findViewById(R.id.tv_ts);
		lv_mainTask=(ListView)findViewById(R.id.listView1);
		lv_HzTask=(ListView)findViewById(R.id.listView2);
		tv_ygxx=(TextView)findViewById(R.id.tv_ygxx);
		tv_startDate=(TextView)findViewById(R.id.tv_startdate);
		tv_endDate=(TextView)findViewById(R.id.tv_enddate);
		tv_boxno=(TextView)findViewById(R.id.tv_boxno);
		
		
		bt_PgUp.setEnabled(false);
		bt_PgUp.setBackgroundResource(R.drawable.clrunenabled);	
		
		bt_PgDown.setEnabled(false);
		bt_PgDown.setBackgroundResource(R.drawable.clrunenabled);
		
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		m_sStartDate = str+" 00:00:00";
		m_sEndDate = str+" 23:59:59";
		tv_startDate.setText(m_sStartDate);
		tv_endDate.setText(m_sEndDate);
		tv_ygxx.setText(clsMyPublic.g_WorkName);
		
		edtInput=(EditText)findViewById(R.id.edtInput);
		edtInput.setInputType(InputType.TYPE_NULL);
		//edtInput.setFocusable(true);
		//edtInput.setFocusableInTouchMode(true);
		//edtInput.requestFocus();

//		edtInput.setOnKeyListener
//    	(
//    		new View.OnKeyListener()
//	    	{
//	    		public boolean onKey(View v, int keyCode, KeyEvent event)
//	    		{
//	    			if (keyCode==KeyEvent.KEYCODE_ENTER)
//	    			{	
//	    				String s="";
//	    				s=edtInput.getText().toString();
//	    				s=s.trim();
//	    				if (s.length()==0) 
//	    				{
//	    					edtInput.setText("");
//	    					return true;
//	    				}
//	    				if(m_iCurBoxIndex==3 ||m_iCurBoxIndex==0)
//	    				{
//		    				tv_boxno.setText(s);
//		    				m_sCurBarCode=tv_boxno.getText().toString();
//		    				edtInput.setText("");  	
//		    				
//		    				m_receMainTask=null;
//							m_sStartDate=tv_startDate.getText().toString();
//							m_sEndDate=tv_endDate.getText().toString();
//							if (m_sStartDate.length()==0 ||m_sEndDate.length()==0 )
//							{
//								Toast.makeText(actWorkLog.this, "请输入开始日期或者结束日期",
//										Toast.LENGTH_SHORT).show();
//							}
//							int iDayCount=0;
//							iDayCount=Integer.valueOf(m_sEndDate)-Integer.valueOf(m_sStartDate);
//							if (iDayCount>2)
//							{
//								Toast.makeText(actWorkLog.this, "只能查询两天内的数据",
//										Toast.LENGTH_SHORT).show();
//								//return;
//							}
//							mSendData(1);
//	    				}
//	    			}
//	    			return false;
//	    		}
//	    	}
//    	);
		
    	
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
//					m_sStartDate=tv_startDate.getText().toString();
//					m_sEndDate=tv_endDate.getText().toString();
//					if (m_sStartDate.length()==0 ||m_sEndDate.length()==0 )
//					{
//						Toast.makeText(actWorkLog.this, "请输入开始日期或者结束日期",
//								Toast.LENGTH_SHORT).show();
//						return;
//					}
//					int iDayCount=0;
//					iDayCount=Integer.valueOf(m_sEndDate)-Integer.valueOf(m_sStartDate);
//					if (iDayCount>2)
//					{
//						Toast.makeText(actWorkLog.this, "只能查询两天内的数据",
//								Toast.LENGTH_SHORT).show();
//						//return;
//					}
					m_sPageNum = 1;
					mSendData(1);
				    v.setEnabled(false);
				    v.setBackgroundResource(R.drawable.clrunenabled);	
				}
		    }
        );
    	
    	//上翻事务事件
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
    	
     	//上翻事务事件
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
    	
    	
    	tv_ygxx.setOnClickListener(new OnClickListener(){public void onClick(View v) {}});
    	tv_startDate.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_iCurBoxIndex=1;
					mNumKeys();
				}
		    }
        ); 
    	tv_endDate.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_iCurBoxIndex=2;
					mNumKeys();
				}
		    }
        ); 
    	tv_boxno.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_iCurBoxIndex=3;
					mNumKeys();
				}
		    }
        ); 
    	/*
    	lv_mainTask.setOnTouchListener(
        		new OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						mFillMainTaskData(m_receMainTask);
						return true;
					}
					
				
        	
        }
        );*/	
	}
	
	public void mViewUi()
	{
		return;
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
				LinearLayout ll_detail=new LinearLayout(actWorkLog.this);
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
				}

				for(int i=0;i<msg.length;i++)//为每一行设置显示的数据
				{					    
					TextView s= new TextView(actWorkLog.this);
					s.setText(msg[i][arg0]+" ");//TextView中显示的文字
					s.setTextSize(20);//字体大小
					s.setTextColor(Color.BLUE);
					s.setPadding(1,2,2,1);//四周留白
					s.setHeight(clsMyPublic.dip2px(actWorkLog.this,40));
					/*
					if ( i==0)
						s.setWidth(clsMyPublic.dip2px(actWorkLog.this,100));//宽度
					if (  i==2 ||i==5 ||i==6 ||i==7||i==8)
						s.setWidth(clsMyPublic.dip2px(actWorkLog.this,180));//宽度
					else if (i==3 || i==16)
						s.setWidth(clsMyPublic.dip2px(actWorkLog.this,300));//宽度
					else if(i==9)
						s.setWidth(clsMyPublic.dip2px(actWorkLog.this,500));
					else
						s.setWidth(clsMyPublic.dip2px(actWorkLog.this,120));
					
					*/
					
					
					TextView tv_tmp=null;
					if (i==0)
						tv_tmp=(TextView)findViewById(R.id.tv_sphh);
					else if(i==1)
						tv_tmp=(TextView)findViewById(R.id.tv_sccjts);
					else if(i==2)
						tv_tmp=(TextView)findViewById(R.id.tv_jhhwts);
					else if(i==3)
						tv_tmp=(TextView)findViewById(R.id.tv_jhslts);
					else if(i==4)
						tv_tmp=(TextView)findViewById(R.id.tv_sjslts1);
					else if(i==5)
						tv_tmp=(TextView)findViewById(R.id.tv_spphts);
					else if(i==6)
						tv_tmp=(TextView)findViewById(R.id.tv_pdts);
					else if(i==7)
						tv_tmp=(TextView)findViewById(R.id.tv_lstdts);
					else if(i==8)
						tv_tmp=(TextView)findViewById(R.id.tv_ztckts);
					else if(i==9)
						tv_tmp=(TextView)findViewById(R.id.tv_sjslts);
					else if(i==10)
						tv_tmp=(TextView)findViewById(R.id.tv_zbzts);
					else if(i==11)
						tv_tmp=(TextView)findViewById(R.id.tv_bzdwts);
					else if(i==12)
						tv_tmp=(TextView)findViewById(R.id.tv_jxlbhts);
					else if(i==13)
						tv_tmp=(TextView)findViewById(R.id.tv_zjslts);
					else if(i==14)
						tv_tmp=(TextView)findViewById(R.id.tv_lsslts);
					else if(i==15)
						tv_tmp=(TextView)findViewById(R.id.tv_zchwts);
					else if(i==16)
						tv_tmp=(TextView)findViewById(R.id.tv_qyts);
					else if(i==17)
						tv_tmp=(TextView)findViewById(R.id.tv_fht_no);
					

					s.setWidth(tv_tmp.getWidth());//宽度
					
					if(i==4)
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
					String sMess="";
					sMess=  "【商品货位】: " + msg[2][arg2]  + "\n" +
							"【周转箱号】: " + msg[1][arg2]  + "\n" +
						    "【商品名称】: " + msg[3][arg2]  + "\n" +
						    "【拣货数量】: " + msg[4][arg2]  + "\n" +
						    "【商品批号】: " + msg[5][arg2]  + "\n" +
						    "【单据编号】: " + msg[6][arg2]  + "\n" +
						    "【作业类型】: " + msg[7][arg2]  + "\n" +
							"【商品编号】: " + msg[8][arg2]  + "\n" +
							"【    中包装】: " + msg[11][arg2] + "\n" +
							"【整件数量】: " + msg[12][arg2] + "\n" +
							"【零货数量】: " + msg[13][arg2] + "\n" +
							"【生产厂家】: " + msg[9][arg2]  + "\n" +
							"【包装单位】: " + msg[10][arg2] ;
					if(m_selectColor[arg2]!=1)
					{
						m_selectColor[arg2]=1;
						arg1.setBackgroundColor(Color.RED);	
					}
					else
					{
						m_selectColor[arg2]=0;
						arg1.setBackgroundColor(Color.TRANSPARENT);	
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
				LinearLayout ll_detail=new LinearLayout(actWorkLog.this);
				ll_detail.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
				ll_detail.setPadding(5,5,5,5);//四周留白

				for(int i=0;i<msg.length;i++)//为每一行设置显示的数据
				{					    
					TextView s= new TextView(actWorkLog.this);
					s.setText(msg[i][arg0]+" ");//TextView中显示的文字
					s.setTextSize(20);//字体大小
					s.setTextColor(Color.RED);
					s.setPadding(1,2,2,1);//四周留白
					if (i==0 || i==1 || i==3 || i==5 || i==7)
						s.setWidth(80); 
					else 
					{
						s.setWidth(100);
						s.setGravity(Gravity.LEFT);
					}
				    ll_detail.addView(s);//放入LinearLayout
				}
				return ll_detail;//将此LinearLayout返回
			}   	
        }; 
        lv_HzTask.setAdapter(ba_detail);
	}

	private void mDoReturnData(Message msg)
	{
		String json="",sFunNo="",sRet1="";
		json=(String)msg.obj;
		
		try {
			Log.v("zms", "2" + json);
			//tv_MessTs.setText(json);
			sRet1 = json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length() - 1).toString());
			String ErrOrMsg = jsonObject.getString("ERRORMSG");
			if (ErrOrMsg.isEmpty()) {
				JSONArray jsonArray = new JSONArray(jsonObject.getString("StrJson"));
				if (m_curType == 1) {
					if (jsonArray.length() < 20) {
						bt_PgDown.setEnabled(false);
						bt_PgDown.setBackgroundResource(R.drawable.clrunenabled);
					} else {
						bt_PgDown.setEnabled(true);
						bt_PgDown.setBackgroundResource(R.drawable.clsbtnselect);
					}

					if (jsonArray.length() > 0) {
						m_iSumReceCount = jsonArray.length();
						m_receMainTask = new String[18][m_iSumReceCount];

						//m_selectColor = new int[m_iSumReceCount+1];

						m_HzMainTask = new String[8][3];
						for (int i = 0; i < 8; i++) {
							m_HzMainTask[i] = new String[3];
						}

						m_iReceHzCount = 0;

						for (int i = 0; i < 18; i++) {
							m_receMainTask[i] = new String[m_iSumReceCount];
						}
						m_selectColor = new int[jsonArray.length()];
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
							m_receMainTask[0][i] = jsonObject2.getString("RN");
							m_receMainTask[1][i] = jsonObject2.getString("TOTEBOX_NO");
							m_receMainTask[2][i] = jsonObject2.getString("DISPLAY_LOCATION");
							m_receMainTask[3][i] = jsonObject2.getString("GOODS_NAME");
							m_receMainTask[4][i] = jsonObject2.getString("ACTUAL_QTY");
							m_receMainTask[5][i] = jsonObject2.getString("GOODS_LOTNO");
							m_receMainTask[6][i] = jsonObject2.getString("BUSINESSBILL_NO");
							m_receMainTask[7][i] = jsonObject2.getString("OPER_CATE");
							m_receMainTask[8][i] = jsonObject2.getString("GOODS_NO");
							m_receMainTask[9][i] = jsonObject2.getString("MANUFACTURER");
							m_receMainTask[10][i] = jsonObject2.getString("PACKAGE_UNIT");
							m_receMainTask[11][i] = jsonObject2.getString("PACKAGE_QTY");
							m_receMainTask[12][i] = jsonObject2.getString("BASKET_NO");
							m_receMainTask[13][i] = jsonObject2.getString("ACTUAL_PCS");
							m_receMainTask[14][i] = jsonObject2.getString("ACTUAL_SPS");
							m_receMainTask[15][i] = jsonObject2.getString("PREPICK_ADDR");
							m_receMainTask[16][i] = jsonObject2.getString("TPC_PICKING_FINISH_TIME");
							m_receMainTask[17][i] = jsonObject2.getString("FHT_NO");

						}

						mFillMainTaskData(m_receMainTask);

						bt_Refresh.setEnabled(true);
						bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
					}
					mSendData(2);
				}
				if (m_curType == 2) {
					if (jsonArray.length() > 0) {
						m_HzMainTask = new String[10][2];
						for (int i = 0; i < 10; i++) {
							m_HzMainTask[i] = new String[2];
						}
						m_iReceHzCount = 0;
						JSONObject jsonObject2 = (JSONObject) jsonArray.opt(0);
						m_HzMainTask[0][0] = "合计";
						m_HzMainTask[1][0] = "当天";
						m_HzMainTask[2][0] = "总条目:";
						m_HzMainTask[3][0] = jsonObject2.getString("CNT_DAY_GROSS");
						m_HzMainTask[4][0] = "补货:";
						m_HzMainTask[5][0] = jsonObject2.getString("RESTOCKING_CNT_DAY");
						m_HzMainTask[6][0] = "零货";
						m_HzMainTask[7][0] = jsonObject2.getString("DOWN_CNT_DAY");
						m_HzMainTask[8][0] = "上架";
						m_HzMainTask[9][0] = jsonObject2.getString("UP_CNT_DAY");
						m_HzMainTask[0][1] = "";
						m_HzMainTask[1][1] = "本月";
						m_HzMainTask[2][1] = "总条目:";
						m_HzMainTask[3][1] = jsonObject2.getString("CNT_MONTH_GROSS");
						m_HzMainTask[4][1] = "补货:";
						m_HzMainTask[5][1] = jsonObject2.getString("RESTOCKING_CNT_MONTH");
						m_HzMainTask[6][1] = "零货";
						m_HzMainTask[7][1] = jsonObject2.getString("DOWN_CNT_MONTH");
						m_HzMainTask[8][1] = "上架";
						m_HzMainTask[9][1] = jsonObject2.getString("UP_CNT_MONTH");


						mFillHzTaskData(m_HzMainTask);
						bt_Refresh.setEnabled(true);
						bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
					} else {
						bt_Refresh.setEnabled(true);
						bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
						tv_MessTs.setText(ErrOrMsg);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			 Log.v("zms", "actWorkLog"+e.toString());
			 tv_MessTs.setText(e.toString());
			 bt_Refresh.setEnabled(true);
 			 bt_Refresh.setBackgroundResource(R.drawable.clsbtnselect);
		}  
	}
	
	private void mDoReturnData_old(Message msg)
	{	
		try
		{
			String sReceData="",sFunNo="",sRet1="";
			sReceData=(String)msg.obj;
			sFunNo=clsMyPublic.GetStrInOfStr(sReceData, 0);
			if (sFunNo.compareTo(clsMyPublic.g_sktParamCXWork)==0)
			{			
				sRet1=clsMyPublic.GetStrInOfStr(sReceData, 1);
				if (sRet1.compareTo("-1")==0 ||sRet1.compareTo("0")==0 )
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
						m_iSumReceCount=Integer.parseInt(sRet1)-3;
						if (m_iSumReceCount>0)
						{
							m_receMainTask=new String[18][m_iSumReceCount];
							for(int i=0;i<18;i++)
							{
								m_receMainTask[i]=new String[m_iSumReceCount];
							}
						}
						m_iCurReceIndex=-1;
					}
                 
                   	if (m_HzMainTask==null)
					{
						m_HzMainTask=new String[8][3];										
						for(int i=0;i<8;i++)
						{
							m_HzMainTask[i]=new String[3];
						}
						m_iReceHzCount=0;
					}
					
					String sTmpData="XX";
					sTmpData=clsMyPublic.GetStrInOfStr(sReceData, 18);
					if (sTmpData.compareTo("当天")==0||
						sTmpData.compareTo("本周")==0||
				        sTmpData.compareTo("本月")==0)
						m_iReceHzCount++;
					else
				   m_iCurReceIndex++;
            	if (sTmpData.compareTo("当天")==0 )
					{
						m_HzMainTask[0][0]="合计:";
						m_HzMainTask[1][0]=clsMyPublic.GetStrInOfStr(sReceData,18);
						m_HzMainTask[2][0]=clsMyPublic.GetStrInOfStr(sReceData,8);
						m_HzMainTask[3][0]=clsMyPublic.GetStrInOfStr(sReceData,9);
						m_HzMainTask[4][0]=clsMyPublic.GetStrInOfStr(sReceData,10);
						m_HzMainTask[5][0]=clsMyPublic.GetStrInOfStr(sReceData,5);
						m_HzMainTask[6][0]=clsMyPublic.GetStrInOfStr(sReceData,11);
						m_HzMainTask[7][0]=clsMyPublic.GetStrInOfStr(sReceData,12);
					}
					else if (sTmpData.compareTo("本周")==0)
					{
						m_HzMainTask[0][1]=" ";
						m_HzMainTask[1][1]=clsMyPublic.GetStrInOfStr(sReceData,18);
						m_HzMainTask[2][1]=clsMyPublic.GetStrInOfStr(sReceData,8);
						m_HzMainTask[3][1]=clsMyPublic.GetStrInOfStr(sReceData,9);
						m_HzMainTask[4][1]=clsMyPublic.GetStrInOfStr(sReceData,10);
						m_HzMainTask[5][1]=clsMyPublic.GetStrInOfStr(sReceData,5);
						m_HzMainTask[6][1]=clsMyPublic.GetStrInOfStr(sReceData,11);
						m_HzMainTask[7][1]=clsMyPublic.GetStrInOfStr(sReceData,12);
					}
					else if (sTmpData.compareTo("本月")==0)
					{							
						m_HzMainTask[0][2]=" ";
						m_HzMainTask[1][2]=clsMyPublic.GetStrInOfStr(sReceData,18);
						m_HzMainTask[2][2]=clsMyPublic.GetStrInOfStr(sReceData,8);
						m_HzMainTask[3][2]=clsMyPublic.GetStrInOfStr(sReceData,9);
						m_HzMainTask[4][2]=clsMyPublic.GetStrInOfStr(sReceData,10);
						m_HzMainTask[5][2]=clsMyPublic.GetStrInOfStr(sReceData,5);
						m_HzMainTask[6][2]=clsMyPublic.GetStrInOfStr(sReceData,11);
						m_HzMainTask[7][2]=clsMyPublic.GetStrInOfStr(sReceData,12);
					}


					for(int i=0;i<18;i++)
					{											

                          
                          if (m_iSumReceCount==0)
                            return;
							
							if (m_iCurReceIndex==m_iSumReceCount)
							{
								mShowMessage("接受数据有异常，请重新尝试...或者联系系统管理员");
								return;
							}
							else if (m_iCurReceIndex>m_iSumReceCount)
							{
								return;
							}
							m_receMainTask[i][m_iCurReceIndex]=clsMyPublic.GetStrInOfStr(sReceData, i+2);
					}
					
					if (m_receMainTask!=null)
						mFillMainTaskData(m_receMainTask);
	
					mFillHzTaskData(m_HzMainTask);
					
					if (m_iSumReceCount==(m_iCurReceIndex+1) || m_iReceHzCount==3)
					{
						//保证一次查询借宿后，才开始下次查询
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
	        mShowMessage(sErrLog);
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
	private void mNumKeys()
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
		
		if (m_iCurBoxIndex==1)
			tv_Cur=tv_startDate;
		else if (m_iCurBoxIndex==2)
			tv_Cur=tv_endDate;
		else if (m_iCurBoxIndex==3 || m_iCurBoxIndex==0)
			tv_Cur=tv_boxno;	
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
	private  void mShowMessage(String _sMess)
	{
		try
		{
			new AlertDialog.Builder(actWorkLog.this)
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
}
