package wcs.cps;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import wcs.cps.http.HttpTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class actPickWork extends Activity 
{
	
	//拣货货位按钮
	private TextView[] m_WorkBt;
	private int m_iPickTaskCount=0;
	private int m_iSumPickBtCount=10;
	private int m_iCurMaxIndex=-1;
	private int m_iCurMinIndex=-1;
	private int m_iCurReceCount=0;
	private int m_iCurWorkIndex=-1;
	private int m_iCurBtIndex=-1;
	private int m_iUnWorkCount=0;   //未作业的任务数
	private int m_iSumBoxCount=0;
	private static ArrayList<clsPickTask> m_PickTaskList=null;
	private Button btUp=null;
	private Button btDown=null;
	private Button btJia=null; 			
	private Button btJian=null;
	private Button bt_Return=null;
	private Button bt_Save=null;
	private Button bt_HwZf=null;
		
	private TextView tv_jhhw=null;
	private TextView tv_spmc=null;
	private TextView tv_spgg=null;
	private TextView tv_zbz=null;
	private TextView tv_spph=null;
	private TextView tv_sccj=null;
	private TextView tv_BoxNo=null;
	private TextView tv_BoxNots=null;
	private TextView tv_sj=null;
	private TextView tv_Jh=null;
	private TextView tv_MessTs=null;
	private TextView tv_Cur=null;
	private TextView txt_jhbzdw=null;
	
	private TextView tv_Box01=null;
	private TextView tv_Box02=null;
	private TextView tv_Box03=null;
	private TextView tv_Box04=null;
	
	private TextView[] tv_zyBoxNo=null;
	private TextView[] tv_TqWz=null;
	
	private TextView[] tv_zyFHTNo=null;
	
	
	private TextView tv_zylb=null;
	private TextView tv_rwsl=null;
	private TextView tv_wwcsl=null;
	private TextView tv_ChngColor=null;
	
	
	private mThreadRun mThreadRun=null;
	private Intent mactPickBoxMatch=null;
	private Intent mactQuery=null;
	private String m_sCurBoxPosNo="";
	private String m_sCurBoxNo="";
	private String m_sZylb="";   //作业类别
	private String m_sDjbh="";   //单据编号
	private String m_sDjhh="";   //单据行号
	private String m_sJhsl="";   //拣货数量
	private String m_sJhhw="";   //拣货货位
	private String m_sPrnBoxNo=""; //打印条码
	private String m_sFendpGroup="";
	private String m_sAreaNO="";
	private String m_sYfcFlag=""; //易发错标志
	private String m_sYfcQk="";   //易发错原因
	
	private int  m_iDoPickConfirg=0;    //当前只能发送一个确认任务，必须等待成功后，再发送下一个
	public static int m_iWorkMode=0;  //作业模式
	private String[] m_sFpdno=null;
	private clsBoxTask[] m_BoxTask=null;
	private String m_sClsName="actPickWork";
	private int m_curType=0;
	
	private int m_iChangCount=0;
	//拣货货位按钮
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
					mDoReturnData(msg,m_curType);
					break;
				case 98:
					mSetBackColor(0);
					break;
				case 99:
					mSetBackColor(1);
					break;
				}
        	}
	};
	
	public class clsBoxTask
	{
		String _sFenpdNo="";
		String _sBoxNo="";
		String _sTQWZ="";
		String _sFHTNo="";
          int  _iRwsl=0;
	}
	public class clsPickTask 
	{
		String _sDanjNo="";
		String _sHangHao="";
		String _sBoxNo="";
		String _sPickHw="";
		String _sArtName;
		String _sArtGg;
		String _sArtZbz;
		String _sArtBzsl;
		String _sArtBzdw;
		String _sArtPh;
		String _sArtCj;
		String _sJhsl;
		String _sSjsl;
		String _sIsTQ;
		String _sTqHw;
		String _sZtwz;
		String _sZylb;
		String _sFpdno;
		String _sZbzck;
		String _sCfld;       //拆分粒度
		String _sYfcFlag;
		String _sYfcQk;
		int   _iDoStatus=0;  // 0 初始化 1拣货完成
		int   _isDoWork=0;
	}
	
	 class mThreadRun extends Thread
	{
		public void run()
		{
			int iChanged=0;
			while(true)
			{
				try 
				{
					if (iChanged==0)
					{
						hd.obtainMessage(98, 100,-1, "").sendToTarget();
						iChanged=1;
					}
					else if(iChanged==1)
					{
						hd.obtainMessage(99, 100,-1, "").sendToTarget();
						iChanged=0;
					}
					Thread.sleep(500);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	private void mSetBackColor(int _Type)
	{
		if (_Type==0)
		{
			if (m_sCurBoxPosNo.compareTo("1")==0)
				tv_Box01.setBackgroundResource(R.drawable.clspickmsgselect);
			else if (m_sCurBoxPosNo.compareTo("2")==0)
				tv_Box02.setBackgroundResource(R.drawable.clspickmsgselect);
			else if (m_sCurBoxPosNo.compareTo("3")==0)
				tv_Box03.setBackgroundResource(R.drawable.clspickmsgselect);
			else if (m_sCurBoxPosNo.compareTo("4")==0)
				tv_Box04.setBackgroundResource(R.drawable.clspickmsgselect);
		}
		else 
		{
			if (m_sCurBoxPosNo.compareTo("1")==0)
			{
				tv_Box01.setBackgroundResource(R.drawable.clrcurboxback);
				if (m_BoxTask[0]!=null)
					if (m_BoxTask[0]._iRwsl==0)
						tv_Box01.setBackgroundResource(R.drawable.clrendboxback);					
			}
			else if (m_sCurBoxPosNo.compareTo("2")==0)
			{
				tv_Box02.setBackgroundResource(R.drawable.clrcurboxback);
				if (m_BoxTask[1]!=null)
					if (m_BoxTask[1]._iRwsl==0)
						tv_Box02.setBackgroundResource(R.drawable.clrendboxback);
					
			}
			else if (m_sCurBoxPosNo.compareTo("3")==0)
			{
				tv_Box03.setBackgroundResource(R.drawable.clrcurboxback);
				if (m_BoxTask[2]!=null)
					if (m_BoxTask[2]._iRwsl==0)
						tv_Box03.setBackgroundResource(R.drawable.clrendboxback);
					
			}
			else if (m_sCurBoxPosNo.compareTo("4")==0)
			{
				tv_Box04.setBackgroundResource(R.drawable.clrcurboxback);
				if (m_BoxTask[3]!=null)
					if (m_BoxTask[3]._iRwsl==0)
						tv_Box04.setBackgroundResource(R.drawable.clrendboxback);	
			}
		}
	}
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actpick);
		this.setTitleColor(Color.BLUE);
		if (m_iWorkMode==clsMyPublic.JHZY_XJ)
			this.setTitle("WCPS-无线拣选小车系统 【拣货作业】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");
		else if (m_iWorkMode==clsMyPublic.JHZY_PD)
			this.setTitle("WCPS-无线拣选小车系统 【盘点作业】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");
        m_PickTaskList=new ArrayList<clsPickTask>();
        mSetSysData();
        mSetButtonIni();
        mSendData(1);
        //String _sendData="null";
        actQuery.m_sQueryBoxNo=null;
        actQuery.m_sQueryBoxNo=new String[4];
        //clsSocketMgr.hd.obtainMessage(98, 100,-1, _sendData).sendToTarget();
        m_sFpdno=new String[4];
        m_BoxTask=new clsBoxTask[4];       
        mThreadRun=new mThreadRun();
        mThreadRun.start();       
    }

	@Override
	protected void onResume()
	{
	    super.onResume();
	    clsMyPublic.m_hdlSendMess=hd;   
	    //clsSocketCln.m_CurHandler=hd;
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
			_httpTool.m_params.put("func_code", "ShowObtainedTaskDetail");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\"}");  
		}
		else if (_Type==2)
		{
			m_curType=2;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ConfirmTaskEx");  
			_httpTool.m_params.put("josnparas", "[{\"INDATA\":\"[{\\\"CMD_ID\\\":\\\""+m_sDjbh + "\\\" ,\\\"ACTUAL_QTY\\\":" + m_sJhsl + ",\\\"FLAG\\\":\\\"MODIFIED\\\"}]\",\"<STAFFNAME|ORAPARAS_STR><CMDID|CMDID>\":\"<STAFFNAME|" + clsMyPublic.g_WorkName + "><CMDID|" + m_sDjhh + ">\"}]");  
			Log.v("zms","拣货"+"[{\"INDATA\":\"[{\\\"CMD_ID\\\":\\\""+m_sDjbh + "\\\" ,\\\"ACTUAL_QTY\\\":" + m_sJhsl + ",\\\"FLAG\\\":\\\"MODIFIED\\\"}]\",\"<STAFFNAME|ORAPARAS_STR><CMDID|CMDID>\":\"<STAFFNAME|" + clsMyPublic.g_WorkName + "><CMDID|" + m_sDjhh + ">\"}]");
		}
		else if (_Type==4)
		{
			m_curType=4;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowBindedTask");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"TaskGroup\":\""+m_sFendpGroup+"\"}");  
		}
		_httpTool.mStart();
	
	}
	private void mSetSysData()
	{
    	bt_Return=(Button)findViewById(R.id.bt_Return);
    	bt_Save=(Button)findViewById(R.id.bt_save);
    	//bt_Check=(Button)findViewById(R.id.bt_Check);
    	bt_HwZf=(Button)findViewById(R.id.bt_px);
    	tv_BoxNots=(TextView)findViewById(R.id.tv_boxnots);
    	tv_BoxNo=(TextView)findViewById(R.id.tv_boxno);
    	btUp=(Button)findViewById(R.id.bt_up);
    	btDown=(Button)findViewById(R.id.bt_down);
    	btUp.setEnabled(false);
    	btDown.setEnabled(false);
    	tv_jhhw=(TextView)findViewById(R.id.tv_jhhw);
    	tv_spmc=(TextView)findViewById(R.id.tv_spmc);
    	tv_spgg=(TextView)findViewById(R.id.tv_ypgg);
    	tv_zbz=(TextView)findViewById(R.id.tv_zbz);
    	tv_spph=(TextView)findViewById(R.id.tv_spph);
    	tv_sccj=(TextView)findViewById(R.id.tv_sccj);
    	tv_MessTs=(TextView)findViewById(R.id.tv_ts);
    	tv_Box01=(TextView)findViewById(R.id.tv_box01);
    	tv_Box02=(TextView)findViewById(R.id.tv_box02);
    	tv_Box03=(TextView)findViewById(R.id.tv_box03);
    	tv_Box04=(TextView)findViewById(R.id.tv_box04);
    	
    	tv_zylb=(TextView)findViewById(R.id.tv_zylb);
    	tv_rwsl=(TextView)findViewById(R.id.tv_rwsl);
    	tv_wwcsl=(TextView)findViewById(R.id.tv_wwcsl);
    	txt_jhbzdw=(TextView)findViewById(R.id.txt_jhbzdw);
    	
    	tv_zyBoxNo=new TextView[4];
    	tv_zyBoxNo[0]=(TextView)findViewById(R.id.tv_zyBoxNo01);
    	tv_zyBoxNo[1]=(TextView)findViewById(R.id.tv_zyBoxNo02);
    	tv_zyBoxNo[2]=(TextView)findViewById(R.id.tv_zyBoxNo03);
    	tv_zyBoxNo[3]=(TextView)findViewById(R.id.tv_zyBoxNo04);
    	tv_TqWz=new TextView[4];
    	tv_TqWz[0]=(TextView)findViewById(R.id.tv_TqWz01);
    	tv_TqWz[1]=(TextView)findViewById(R.id.tv_TqWz02);
    	tv_TqWz[2]=(TextView)findViewById(R.id.tv_TqWz03);
    	tv_TqWz[3]=(TextView)findViewById(R.id.tv_TqWz04);
    	
     	tv_zyFHTNo=new TextView[4];
     	tv_zyFHTNo[0]=(TextView)findViewById(R.id.tv_zyFht01);
     	tv_zyFHTNo[1]=(TextView)findViewById(R.id.tv_zyFht02);
     	tv_zyFHTNo[2]=(TextView)findViewById(R.id.tv_zyFht03);
     	tv_zyFHTNo[3]=(TextView)findViewById(R.id.tv_zyFht04);
    	
    	tv_ChngColor=(TextView)findViewById(R.id.textView1);
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
    	//拣货货位按钮初始化
    	m_WorkBt=new TextView[m_iSumPickBtCount];
    	m_WorkBt[0]=(TextView)findViewById(R.id.tv_hw01);
    	m_WorkBt[1]=(TextView)findViewById(R.id.tv_hw02);
    	m_WorkBt[2]=(TextView)findViewById(R.id.tv_hw03);
    	m_WorkBt[3]=(TextView)findViewById(R.id.tv_hw04);
    	m_WorkBt[4]=(TextView)findViewById(R.id.tv_hw05);
    	m_WorkBt[5]=(TextView)findViewById(R.id.tv_hw06);
    	m_WorkBt[6]=(TextView)findViewById(R.id.tv_hw07);
    	m_WorkBt[7]=(TextView)findViewById(R.id.tv_hw08);
    	m_WorkBt[8]=(TextView)findViewById(R.id.tv_hw09);
    	m_WorkBt[9]=(TextView)findViewById(R.id.tv_hw10);
    	//货位按钮事件
    	for(int i=0;i<this.m_iSumPickBtCount;i++)
		{
    		m_WorkBt[i].setEnabled(false);
    		m_WorkBt[i].setOnClickListener
			(
				new OnClickListener()
			    {
					public void onClick(View v) 
					{
						mViewJhData(v);						
					}
			    }
			);
        }	
    	
    	//货位提示信息   	
    	tv_jhhw.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});   	
    	tv_spmc.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});  	
    	tv_spgg.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});
    	tv_zbz.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});
    	tv_spph.setOnClickListener(new OnClickListener(){public void onClick(View v) {}});	
    	tv_sccj.setOnClickListener(new OnClickListener(){public void onClick(View v) {}}); 
    	tv_Box01.setOnClickListener
    	(
    		new OnClickListener()
    		{
    			public void onClick(View v) 
    			{
    				
    				if (m_BoxTask[0]==null) return;
    				actQuery.m_sFpdNo=m_BoxTask[0]._sFenpdNo;
    				actQuery.m_iCurBoxIndex=0;
    				mVeiwUi(2);
    			}
    		}
    	); 
    	tv_Box02.setOnClickListener
    	(
    	    new OnClickListener()
    	    {
    	    	public void onClick(View v)
    	    	{
    	    		if (m_BoxTask[1]==null) return;
    	    		actQuery.m_sFpdNo=m_BoxTask[1]._sFenpdNo;
    	    		actQuery.m_iCurBoxIndex=1;
    				mVeiwUi(2);
    	    	}
    	    }
    	); 
    	tv_Box03.setOnClickListener
    	(
    	    new OnClickListener()
    	    {
    	    	public void onClick(View v)
    	    	{
    	    		if (m_BoxTask[2]==null) return;
    	    		actQuery.m_sFpdNo=m_BoxTask[2]._sFenpdNo;
    	    		actQuery.m_iCurBoxIndex=2;
    				mVeiwUi(2);
    	    	}
    	    }
    	); 
    	tv_Box04.setOnClickListener
    	(
    	    new OnClickListener()
    	    {
    	    	public void onClick(View v)
    	    	{
    	    		if (m_BoxTask[3]==null) return;
    	    		actQuery.m_sFpdNo=m_BoxTask[3]._sFenpdNo;
    	    		actQuery.m_iCurBoxIndex=3;
    				mVeiwUi(2);
    	    	}
    	    }
    	); 
    	//向下翻页事件
    	btDown.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					mVewPickData(1);
				}
		    }
        );
    	//向上翻页事件
    	btUp.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					mVewPickData(-1);
				}
		    }
        );    	
    	//加减
		btJia=(Button)findViewById(R.id.bt_jia); 			
    	btJian=(Button)findViewById(R.id.bt_jian);
    	tv_sj=(TextView)findViewById(R.id.tv_sjsl);
		tv_Jh=(TextView)findViewById(R.id.tv_jhsl);
		tv_sj.setVisibility(View.INVISIBLE);
		btJia.setVisibility(View.INVISIBLE);
		btJian.setVisibility(View.INVISIBLE);
		

    	bt_Save.setOnClickListener//关于按钮的监听
		(
		   new OnClickListener()
		   {
				public void onClick(View v) 
				{
					m_sJhsl=tv_sj.getText().toString();
					m_iDoPickConfirg=1;
					mSendData(2);
					bt_Save.setEnabled(false);
					bt_Save.setBackgroundResource(R.drawable.clrunenabled);
					//mLocalConfirg();
				}
		   }
		);
    	/*
    	bt_Check.setOnClickListener//关于按钮的监听
		(
			   new OnClickListener()
			   {
					public void onClick(View v) 
					{
						mVeiwUi(2);
					}
			   }
		);*/
    	bt_HwZf.setOnClickListener//关于按钮的监听
		(
			   new OnClickListener()
			   {
					public void onClick(View v) 
					{
						mChangePickOrder();
						m_iCurMaxIndex=-1;
						m_iCurMinIndex=-1;
						mVewPickData(0);
						mViewJhData(m_WorkBt[0]);
					}
			   }
		);
	}
	
	private void mSetButtonIni()
	{
		btJian.setEnabled(false);
		btJia.setEnabled(false);
		btUp.setEnabled(false);
		btDown.setEnabled(false);
		btUp.setEnabled(false);
		bt_Save.setEnabled(false);
		//bt_Check.setEnabled(false);
		bt_HwZf.setEnabled(false);
		tv_Box01.setEnabled(false);
		tv_Box02.setEnabled(false);
		tv_Box03.setEnabled(false);
		tv_Box04.setEnabled(false);
		btJian.setBackgroundResource(R.drawable.clrunenabled);
		btJia.setBackgroundResource(R.drawable.clrunenabled);
		btDown.setBackgroundResource(R.drawable.clrunenabled);
		btUp.setBackgroundResource(R.drawable.clrunenabled);
		bt_Save.setBackgroundResource(R.drawable.clrunenabled);
		//bt_Check.setBackgroundResource(R.drawable.clrunenabled);
		bt_HwZf.setBackgroundResource(R.drawable.clrunenabled);
		if(m_iWorkMode==clsMyPublic.JHZY_PD)
		{
			//bt_Check.setVisibility(View.INVISIBLE);
			tv_Box01.setVisibility(View.INVISIBLE);
			tv_Box02.setVisibility(View.INVISIBLE);
			tv_Box03.setVisibility(View.INVISIBLE);
			tv_Box04.setVisibility(View.INVISIBLE);
		}
	}
	private void mVeiwUi(int _Type)
	{
		if (_Type==1)
		{
			mactPickBoxMatch=null;
			mactPickBoxMatch=new Intent(this,actPickBoxMatch.class);
			startActivityForResult(mactPickBoxMatch,1);
		}
		else if (_Type==2)
		{
			mactQuery=new Intent(this,actQuery.class);
			startActivityForResult(mactQuery,1);
		}
	}
	
    private void mVewPickData(int _Type)
    {
    	int iCurIndex=0;
		clsPickTask _PickTask=new clsPickTask();
    	if (_Type==0)
    	{
    		iCurIndex=0;
    		this.m_iCurMaxIndex=-1;
    		this.m_iCurMinIndex=0;
    		btDown.setEnabled(false);
    		btUp.setEnabled(false);
    		btDown.setBackgroundResource(R.drawable.clrunenabled);
    		btUp.setBackgroundResource(R.drawable.clrunenabled);
    		for(int i=0;i<m_iSumPickBtCount;i++)
    		{
    			if (iCurIndex<this.m_iPickTaskCount)
    			{
    				_PickTask=m_PickTaskList.get(iCurIndex);
    				m_WorkBt[i].setVisibility(View.VISIBLE);
    				this.m_WorkBt[i].setText(_PickTask._sPickHw);
    				this.m_WorkBt[i].setTag(iCurIndex);
    				m_WorkBt[i].setEnabled(true);
    				if (_PickTask._iDoStatus==0)
        				mSetPickColor(i,iCurIndex);
    				else
    					m_WorkBt[i].setBackgroundResource(R.drawable.clrendboxback);
    				iCurIndex++;
    				this.m_iCurMaxIndex++;
    			}
    			else
    			{
    				m_WorkBt[i].setVisibility(View.INVISIBLE);
    			}
    		}
    		if ((m_iPickTaskCount-1)>m_iCurMaxIndex)
    		{
    			btDown.setEnabled(true); 
    			btDown.setBackgroundResource(R.drawable.clsbtnselect);
    		}
    	}
    	else if(_Type==1)
    	{
    		//向下翻页
    		if(m_iCurMaxIndex==(m_iPickTaskCount-1)) return;
    		
    		m_iCurMinIndex=m_iCurMaxIndex+1;
    		iCurIndex=m_iCurMinIndex;
    		for(int i=0;i<m_iSumPickBtCount;i++)
    		{
    			if (iCurIndex<this.m_iPickTaskCount)
    			{
    				_PickTask=m_PickTaskList.get(iCurIndex);
    				m_WorkBt[i].setVisibility(View.VISIBLE);
    				this.m_WorkBt[i].setText(_PickTask._sPickHw);
    				this.m_WorkBt[i].setTag(iCurIndex);
    				m_WorkBt[i].setEnabled(true);
    				if (_PickTask._iDoStatus==0)
        				mSetPickColor(i,iCurIndex);
    				else
    					m_WorkBt[i].setBackgroundResource(R.drawable.clrendboxback);
    				iCurIndex++;
    				this.m_iCurMaxIndex++;
    			}
    			else
    			{
    				m_WorkBt[i].setVisibility(View.INVISIBLE);
    			}
    		}
    		if ((m_iPickTaskCount-1)>m_iCurMaxIndex)
    		{
    			btDown.setEnabled(true);
    			btDown.setBackgroundResource(R.drawable.clsbtnselect);
    		}
    		else
    		{
    			btDown.setEnabled(false);
    			btDown.setBackgroundResource(R.drawable.clrunenabled);
    		}
    		btUp.setEnabled(true);
    		btUp.setBackgroundResource(R.drawable.clsbtnselect);
    	}
    	else if (_Type==-1)
    	{
    		if(m_iCurMinIndex==0) return;
    		m_iCurMaxIndex=m_iCurMinIndex-1;
    		iCurIndex=m_iCurMaxIndex;
    		for(int i=(m_iSumPickBtCount-1);i>=0;i--)
    		{
    			_PickTask=m_PickTaskList.get(iCurIndex);
    			m_WorkBt[i].setVisibility(View.VISIBLE);
				this.m_WorkBt[i].setText(_PickTask._sPickHw);
				this.m_WorkBt[i].setTag(iCurIndex);
				m_WorkBt[i].setEnabled(true);
				if (_PickTask._iDoStatus==0)
				{
					mSetPickColor(i,iCurIndex);
				}
				else
					m_WorkBt[i].setBackgroundResource(R.drawable.clrendboxback);
				this.m_iCurMinIndex=iCurIndex;
				iCurIndex--;
    		}
    		if (m_iCurMinIndex==0)
    		{
    			btUp.setEnabled(false);
    			btUp.setBackgroundResource(R.drawable.clrunenabled);
    		}
    		else
    		{
    			btUp.setEnabled(true);
    			btUp.setBackgroundResource(R.drawable.clsbtnselect);
    		}   		
    		if ((m_iPickTaskCount-1)>m_iCurMaxIndex)
    		{
    			btDown.setEnabled(true); 
    			btDown.setBackgroundResource(R.drawable.clsbtnselect);
    		}
    		else
    		{
    			btDown.setEnabled(false);
    			btDown.setBackgroundResource(R.drawable.clrunenabled);
    		}    		
    	}
    }
    
    private int tozth(String zthvalue)
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
	 
	private void mDoReturnData(Message msg,int curType)
	{	
		String json="",sFunNo="",sRet1="",sMess="";
		try
		{
			json=(String)msg.obj;
			
			sRet1=json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length()-1).toString());
	    	String ErrOrMsg =jsonObject.getString("ERRORMSG"); 
	    	if(ErrOrMsg.isEmpty())
	    	{
	    		JSONArray jsonArray =new JSONArray(jsonObject.getString("StrJson")); 
	    		if(m_curType==1)
	    		{
	    			//Log.v("zms","拣货数据条数"+Integer.toString(jsonArray.length()));
	    			
	    			m_iPickTaskCount=jsonArray.length();
	    			tv_rwsl.setText(String.valueOf(m_iPickTaskCount));
					tv_wwcsl.setText(String.valueOf(m_iPickTaskCount));
					m_iUnWorkCount=m_iPickTaskCount;
					
					if (m_iCurReceCount>=m_iPickTaskCount)
					{
						mShowMessageII("数据异常，请重新操作或者联系系统管理员....");
						return;
					}
					
	    			for (int i=0;i<jsonArray.length();i++)
		    		{
	    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
	    				
	    				clsPickTask _PickTask=new clsPickTask();
						_PickTask._sDanjNo=jsonObject2.getString("BILL_HDR_ID");
						_PickTask._sZylb=jsonObject2.getString("OPER_CATE");
						_PickTask._sHangHao=jsonObject2.getString("CMD_ID");
						if(_PickTask._sZylb.compareTo(clsMyPublic.g_SqWorkType_Tqjx)==0)
						{
							_PickTask._sBoxNo =jsonObject2.getString("BASKET_NO");
						}
						else
						{
							_PickTask._sBoxNo =jsonObject2.getString("TOTEBOX_NO");
						}
						_PickTask._sPickHw=jsonObject2.getString("DISPLAY_LOCATION");
						_PickTask._sArtName=jsonObject2.getString("GOODS_NAME");
						_PickTask._sArtGg=jsonObject2.getString("DRUG_SPEC");
						_PickTask._sArtBzsl=jsonObject2.getString("PACKAGE_QTY");
						_PickTask._sArtBzdw=jsonObject2.getString("PACKAGE_UNIT");
						_PickTask._sArtPh=jsonObject2.getString("GOODS_LOTNO");
						_PickTask._sArtCj=jsonObject2.getString("MANUFACTURER");
						_PickTask._sJhsl=jsonObject2.getString("PLANNED_QTY");
						_PickTask._sSjsl=jsonObject2.getString("ACTUAL_QTY");
						//_PickTask._sIsTQ=jsonObject2.getString("TOTEBOX_NO");
						//_PickTask._sTqHw=jsonObject2.getString("TOTEBOX_NO");
						_PickTask._sZtwz= Integer.toString(tozth(jsonObject2.getString("CART_BUFFER_NO")));
						
						_PickTask._sFpdno=jsonObject2.getString("BILL_HDR_ID");
						_PickTask._sArtZbz=jsonObject2.getString("M_PACKAGE_QTY");
						_PickTask._sZbzck=Float.toString(Float.parseFloat(_PickTask._sJhsl)/Float.parseFloat(_PickTask._sArtZbz));//jsonObject2.getString("TOTEBOX_NO");
						_PickTask._sCfld=jsonObject2.getString("SPLIT_GRANULARITY");
						actQuery.m_sAreaNo=jsonObject2.getString("AREA_NO");
						
						_PickTask._sYfcFlag="";//jsonObject2.getString("TOTEBOX_NO");
						_PickTask._sYfcQk="";//jsonObject2.getString("TOTEBOX_NO");
						
						m_PickTaskList.add(_PickTask);
						m_iCurReceCount++;		
						
						m_sAreaNO=jsonObject2.getString("AREA_NO");
						m_sFendpGroup=jsonObject2.getString("TASKBILL_GROUP");
						
						
						if (_PickTask._sZylb.compareTo(clsMyPublic.g_SqWorkType_Tqjx)==0)
						{
							m_iWorkMode=clsMyPublic.JHZY_TQ;
						}
										
						if (_PickTask._sZtwz.compareTo("1")==0)
							m_sFpdno[0]=_PickTask._sFpdno;
						else if (_PickTask._sZtwz.compareTo("2")==0)
							m_sFpdno[1]=_PickTask._sFpdno;
						else if (_PickTask._sZtwz.compareTo("3")==0)
							m_sFpdno[2]=_PickTask._sFpdno;
						else if (_PickTask._sZtwz.compareTo("4")==0)
							m_sFpdno[3]=_PickTask._sFpdno;
											
						if (m_iCurReceCount==m_iPickTaskCount)
						{
							mVewPickData(0);					
					    	//bt_Check.setEnabled(true);
					    	//bt_Check.setBackgroundResource(R.drawable.clsbtnselect);
					    	bt_HwZf.setEnabled(true);
					    	bt_HwZf.setBackgroundResource(R.drawable.clsbtnselect);
					    	bt_Save.setEnabled(true);
					    	bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
							tv_Box01.setEnabled(true);
							tv_Box02.setEnabled(true);
							tv_Box03.setEnabled(true);
							tv_Box04.setEnabled(true);
							//周转箱数据
							mSendData(4);
						}
		    		}
	    		}
	    		else if(m_curType==2)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			//if (jsonArray.length()>0)
	    			{
	    				mLocalConfirg();
	    			}
	    		}
	    		else if(m_curType==4)
	    		{
	    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
	    			int _iSumBoxCount=0;
					int iZcIndex=0;
					_iSumBoxCount=jsonArray.length();
					for (int i=0;i<jsonArray.length();i++)
		    		{
	    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
						iZcIndex=tozth(jsonObject2.getString("CART_BUFFER_NO"));
						iZcIndex=iZcIndex-1;
						m_BoxTask[iZcIndex]=new clsBoxTask();
						m_BoxTask[iZcIndex]._sFenpdNo=jsonObject2.getString("TASKBILL_NO");
						m_BoxTask[iZcIndex]._sFHTNo=jsonObject2.getString("BUFFER_NO");
						if (m_iWorkMode==clsMyPublic.JHZY_TQ)
							m_BoxTask[iZcIndex]._sBoxNo=jsonObject2.getString("CONTAINER_NO");
						else
							m_BoxTask[iZcIndex]._sBoxNo=jsonObject2.getString("CONTAINER_NO");
							m_BoxTask[iZcIndex]._sTQWZ="";//jsonObject2.getString("CONTAINER_NO");
						m_BoxTask[iZcIndex]._iRwsl=Integer.parseInt(jsonObject2.getString("UNPICKED_NUM"));
						m_iSumBoxCount++;
						if (m_BoxTask[iZcIndex]._iRwsl==0)
						{
							if (iZcIndex==0)
								tv_Box01.setBackgroundResource(R.drawable.clrendboxback);
							else if (iZcIndex==1)
								tv_Box02.setBackgroundResource(R.drawable.clrendboxback);
							else if (iZcIndex==2)
								tv_Box03.setBackgroundResource(R.drawable.clrendboxback);
							else if (iZcIndex==3)
								tv_Box04.setBackgroundResource(R.drawable.clrendboxback);
						}
						tv_zyBoxNo[iZcIndex].setText(m_BoxTask[iZcIndex]._sBoxNo);
						tv_zyFHTNo[iZcIndex].setText(m_BoxTask[iZcIndex]._sFHTNo); //20170605 whstruts add _sFHTNo
						tv_TqWz[iZcIndex].setText(m_BoxTask[iZcIndex]._sTQWZ);// _sTQWZ目前基本不用，提前提货流程才用 20170605
						actQuery.m_sQueryBoxNo[iZcIndex]=m_BoxTask[iZcIndex]._sFenpdNo;
						actQuery.m_iMaxBoxIndex=iZcIndex;
						if (_iSumBoxCount==m_iSumBoxCount)
						{
							mViewJhData(m_WorkBt[0]);
						}
		    		}
	    		}
	    	}
	    	else
	    	{
	    		tv_MessTs.setText(ErrOrMsg);
	    		mShowMessageII(ErrOrMsg);
	    		
	    		bt_Save.setEnabled(true);
		    	bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
	    	}
		}
		catch (Exception e)
		{
	        sMess=m_sClsName +";mDoReturnData;" + e.toString();
	        Log.v("zms", sMess);
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sMess);
	        mShowMessageII(sMess);
	        
	        bt_Save.setEnabled(true);
	    	bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
	    }
		catch(UnknownError e)
		{
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
			Log.v("zms", sErrLog);
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
	        
	        bt_Save.setEnabled(true);
	    	bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
		}
	}
	private void mLocalConfirg()
	{
		m_iDoPickConfirg=0;
    	clsPickTask _PickTask=new clsPickTask();
    	_PickTask=m_PickTaskList.get(m_iCurWorkIndex);
    	_PickTask._iDoStatus=1;
    	m_PickTaskList.set(m_iCurWorkIndex, _PickTask);
    	m_WorkBt[m_iCurBtIndex].setBackgroundResource(R.drawable.clrendboxback);
    	int iBoxIndex=0;
    	iBoxIndex=Integer.parseInt(_PickTask._sZtwz)-1;
    	m_BoxTask[iBoxIndex]._iRwsl--;
    	int iCurbtIndex=m_iCurBtIndex;
    	int iCurWrIndex=m_iCurWorkIndex;
    	m_iCurBtIndex=-1;
    	m_iUnWorkCount--;
    	tv_wwcsl.setText(String.valueOf(m_iUnWorkCount));
    	if (m_iUnWorkCount==0)
    	{
    		if(m_iWorkMode==clsMyPublic.JHZY_XJ)
    			mShowMessage("当前已索取任务全部拣货完成，请全部投放到输送线或者提前拣选货位上...");
    		else if (m_iWorkMode==clsMyPublic.JHZY_TQ)
    			mShowMessage("当前提前拣选作业全部完成，请投放到指定的暂存位...");	
    		else if (m_iWorkMode==clsMyPublic.JHZY_PD)
    			mShowMessage("盘点作业全部，请继续其他作业");
    		return;
    	}
    	//自动移动下一个货位
    	mAutoMoveNextHw(iCurbtIndex,iCurWrIndex);
	}
	private void mChangePickOrder()
	{
		//正序
		for(int i=0;i<m_PickTaskList.size();i++)
		{
			int j=0;
			j=m_PickTaskList.size()-i-1;
			if (i>=j) return;
			clsPickTask _PickTask1=new clsPickTask();
			_PickTask1=m_PickTaskList.get(i);
			clsPickTask _PickTask2=new clsPickTask();
			_PickTask2=m_PickTaskList.get(j);
			m_PickTaskList.set(i, _PickTask2);
			m_PickTaskList.set(j, _PickTask1);
		}		
	}
	
	private String zth(String zthvalue)
	{
		String abcd="";
		
		if(zthvalue.compareTo("1")==0)
		{
			abcd="A";
		}
		else if(zthvalue.compareTo("2")==0)
		{
			abcd="B";
		}
		else if(zthvalue.compareTo("3")==0)
		{
			abcd="C";
		}
		else if(zthvalue.compareTo("4")==0)
		{
			abcd="D";
		}
		
		return abcd;
	}
    private void mViewJhData(View v)
    {
    	if (m_iPickTaskCount==0) return;
    	if (m_iDoPickConfirg==1) return;
    	String _sCfldName="";
    	clsPickTask _PickTask=new clsPickTask();
    	int tag = (Integer) v.getTag();
    	_PickTask=m_PickTaskList.get(tag);
    	tv_jhhw.setText(_PickTask._sPickHw); 
    	tv_spmc.setText(_PickTask._sArtName);
    	if (_PickTask._sCfld.compareTo("1")==0)
    		_sCfldName="可拆零";
    	else if (_PickTask._sCfld.compareTo("2")==0)
    		_sCfldName="不拆中包装";
    	else if (_PickTask._sCfld.compareTo("3")==0)
    		_sCfldName="不拆大包装";
    	else if (_PickTask._sCfld.compareTo("4")==0)
    		_sCfldName="可以小数";
    	tv_spgg.setText(_PickTask._sArtGg);
    	txt_jhbzdw.setText(_PickTask._sArtBzdw);
    	tv_zbz.setText(_PickTask._sArtZbz  + _PickTask._sArtBzdw +  "  " +
    			      " 出库中包装个数:  " + _PickTask._sZbzck + "  【" + _sCfldName +"】");
    	tv_spph.setText(_PickTask._sArtPh);
    	tv_sccj.setText(_PickTask._sArtCj);   	
    	tv_Jh.setText(_PickTask._sJhsl);
    	tv_sj.setText(_PickTask._sJhsl);

		m_sYfcFlag=_PickTask._sYfcFlag;
		m_sYfcQk=_PickTask._sYfcQk;
    	
		tv_MessTs.setText(m_sYfcQk);
		
    	int iZcIndex=0;
    	iZcIndex=Integer.parseInt(_PickTask._sZtwz);
    	m_sCurBoxNo=m_BoxTask[iZcIndex-1]._sBoxNo;
    	
    	m_sDjbh=_PickTask._sDanjNo;
    	m_sDjhh=_PickTask._sHangHao;
    	m_sJhhw=_PickTask._sPickHw;
    	m_sZylb=_PickTask._sZylb;
    	tv_BoxNo.setText(m_sCurBoxNo);
    	tv_zylb.setText(clsMyPublic.GetZylbName(_PickTask._sZylb));
    	//当前周转箱的位置
		m_sCurBoxPosNo=_PickTask._sZtwz;
		tv_BoxNots.setText(zth(m_sCurBoxPosNo));
		
		tv_Box01.setBackgroundResource(R.drawable.clspickmsgselect);
		if (m_BoxTask[0]!=null)
			if (m_BoxTask[0]._iRwsl==0)
				tv_Box01.setBackgroundResource(R.drawable.clrendboxback);
			
		tv_Box02.setBackgroundResource(R.drawable.clspickmsgselect);
		if (m_BoxTask[1]!=null)
			if (m_BoxTask[1]._iRwsl==0)
				tv_Box02.setBackgroundResource(R.drawable.clrendboxback);
			
		tv_Box03.setBackgroundResource(R.drawable.clspickmsgselect);
		if (m_BoxTask[2]!=null)
			if (m_BoxTask[2]._iRwsl==0)
				tv_Box03.setBackgroundResource(R.drawable.clrendboxback);
		tv_Box04.setBackgroundResource(R.drawable.clspickmsgselect);
		
		if (m_BoxTask[3]!=null)
			if (m_BoxTask[3]._iRwsl==0)
				tv_Box04.setBackgroundResource(R.drawable.clrendboxback);
			
		
    	if (m_iCurBtIndex>-1)
    	{
    		m_WorkBt[m_iCurBtIndex].setBackgroundResource(R.drawable.clrhwselect);
    	}
    	if (_PickTask._iDoStatus==0)
    	{
    		v.setBackgroundResource(R.drawable.clrcurworkhw);
    		m_iCurBtIndex=tag%m_iSumPickBtCount;
    		m_iCurWorkIndex=tag;
			bt_Save.setEnabled(true);
	    	bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
	    	btJia.setEnabled(false);
	    	btJia.setBackgroundResource(R.drawable.clrunenabled);
	    	btJian.setEnabled(true);
	    	btJian.setBackgroundResource(R.drawable.clsbtnselect);
    	}
    	else
    	{
    		v.setBackgroundResource(R.drawable.clrendboxback);
			bt_Save.setEnabled(false);
	    	bt_Save.setBackgroundResource(R.drawable.clrunenabled);
    	}

    }
    private void mSetPickColor(int _iIndex,int _iTaskIndex)
    {
    	if (m_iCurWorkIndex==_iTaskIndex)
    		m_WorkBt[_iIndex].setBackgroundResource(R.drawable.clrcurworkhw);
    	else
    		m_WorkBt[_iIndex].setBackgroundResource(R.drawable.clrhwselect);
    	
    }
	private void mDoDisMessage(Message msg)
	{
		String sMess=(String)msg.obj;
		tv_MessTs.setText(sMess);
	}
	
	private void mAutoMoveNextHw(int _CurBtIndex,int _CurWrIndex)
	{		
		//删除已经做完的任务
		m_PickTaskList.remove(_CurWrIndex);
		
		m_iCurReceCount=0;
	    m_iPickTaskCount=0;
		m_iUnWorkCount=0;
		m_iCurBtIndex=-1;
		
		m_iPickTaskCount=m_PickTaskList.size();
		m_iUnWorkCount=m_iPickTaskCount;
		
		if(m_iCurMinIndex>=m_iPickTaskCount)
		{
			m_iCurMinIndex=m_iCurMinIndex-10;
		}
				
		clsPickTask _PickTask=new clsPickTask();
		int iCurIndex=m_iCurMinIndex;
		for(int i=0;i<m_iSumPickBtCount;i++)
		{
			if (iCurIndex<m_iPickTaskCount)
			{
				_PickTask=m_PickTaskList.get(iCurIndex);
				m_WorkBt[i].setVisibility(View.VISIBLE);
				this.m_WorkBt[i].setText(_PickTask._sPickHw);
				this.m_WorkBt[i].setTag(iCurIndex);
				m_WorkBt[i].setEnabled(true);
				if (_PickTask._iDoStatus==0)
				{
					mSetPickColor(i,iCurIndex);
				}
				else
					m_WorkBt[i].setBackgroundResource(R.drawable.clrendboxback);
				this.m_iCurMaxIndex=iCurIndex;
				iCurIndex++;
			}
			else
			{
				m_WorkBt[i].setVisibility(View.INVISIBLE);
			}
		}
			
		
		btDown.setEnabled(false);
		btUp.setEnabled(false);
		btDown.setBackgroundResource(R.drawable.clrunenabled);
		btUp.setBackgroundResource(R.drawable.clrunenabled);
				
		if (m_iCurMinIndex>=10)
		{
			//还可以向上翻页
			btUp.setEnabled(true);
			btUp.setBackgroundResource(R.drawable.clsbtnselect);
		}
		
		if (m_iCurMaxIndex<(m_iPickTaskCount-1))
		{
			//还可以向下翻页
			btDown.setEnabled(true);
			btDown.setBackgroundResource(R.drawable.clsbtnselect);
		}
		
		if (m_iCurWorkIndex>m_iCurMaxIndex)
		{
			m_iCurWorkIndex--;
			if (_CurBtIndex==0)
				_CurBtIndex=9;
			else
				_CurBtIndex--;
		}
		
		mViewJhData(m_WorkBt[_CurBtIndex]);
						
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
	public  void mShowMessage(String _sMess)
	{
		new AlertDialog.Builder(actPickWork.this)
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
   				//finish();
 		   }
 		 }).show();
	}
	private  void mShowMessageII(String _sMess)
	{
		try
		{
			new AlertDialog.Builder(actPickWork.this)
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
		catch(UnknownError e)
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
				.setView(loginLayout).create();
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
		    						mSendData(3);
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
