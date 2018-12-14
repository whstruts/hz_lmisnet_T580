package wcs.cps;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class actPositionMove extends Activity 
{
	private Button bt_Return=null;
	private Button bt_sq=null;
	private Button bt_ok=null;
	private TextView tv_MessTs=null;
	
	
	private String[][] m_receMainTask;
	private int m_iCurReceIndex=-1;
	private int m_iCurBoxIndex=0;
	private TextView tv_Cur=null;
	private int m_iSumReceCount=-1;
	private String m_sQuery="";
	private EditText edtInput=null;
	private String m_sClsName="PositionMove";
	private String m_spid="";
	private String m_spph="";
	private String m_lccode="";
	private String m_ownerno="";
	
	private TextView tv_hwcode=null;
	private TextView tv_spbm=null;
	private TextView tv_chinaname = null;// 中文名
	private TextView tv_Manu = null;// 制造商信息
	private TextView tv_bzdw = null;// 包装单位
	private TextView tv_ypgg = null;// 药品规格
	private TextView tv_spph = null;// 批号
	private TextView tv_yxDate = null;// 有效期
	private TextView tv_hwsl=null;//库存数量
	private TextView tv_yksl=null;//库存数量
	private TextView tv_yrhw=null;//库存数量
	private TextView  tv_zbzs=null;//中包装
	private TextView  tv_yrhw_jp=null;
	private int m_curType = 0;
	
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
        setContentView(R.layout.actpositionmove);
        this.setTitleColor(Color.BLUE);
        this.setTitle("WCPS-无线拣选小车系统 【货位调整】 【" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"】");
        clsMyPublic.m_hdlSendMess=hd;
        mSetSysData();
        mSetButtonIni();
    }
	
	private void mSetButtonIni()
	{
		tv_hwcode.setText("");
		tv_spbm.setText("");
		tv_chinaname.setText("");
		tv_Manu.setText("");
		tv_bzdw.setText("");
		tv_ypgg.setText("");
		tv_spph.setText("");
		tv_yxDate.setText("");
		tv_hwsl.setText("");
		tv_yksl.setText("");
		tv_yrhw.setText("");
		tv_zbzs.setText("");
	}
	
	public void mSendData(int _Type)
	{
		String _sendData="";
		HttpTool _httpTool=new HttpTool();
		tv_MessTs.setText("");
		_httpTool.m_CurHandler=hd;
		_httpTool.m_urlPath = "http://"+HttpTool.m_SvrIp+":"+HttpTool.m_SvrPort + HttpTool.m_sktNetPath;

		if (_Type==1)
		{
			m_curType =1;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowTpcLocationAdjustDisplay");  
			_httpTool.m_params.put("josnparas", "{\"DataValue\":\""+m_sQuery+"\"}"); 
//			Log.v("zms", "{\"DisplayLocation\":\""+m_sQuery+"\",\"MnemonicCode\":\""+m_sZJM+"\",\"PageNum\":"+m_sPageNum+"}");
			_httpTool.mStart();	
		}
		else if (_Type==2)
		{
			m_curType =2;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ShowTpcLocationAdjustConfirm"); 
			_httpTool.m_params.put("josnparas", "{\"ivlccode\":\""+m_lccode+"\",\"ivownerno\":\""+m_ownerno+"\",\"ivoperator\":\""+
					clsMyPublic.g_WorkName+"\",\"ivgoodsid\":\""+m_spid+"\",\"ivlotnoid\":\""+
					m_spph+"\",\"ivdisplaylocationf\":\""+m_sQuery+"\",\"ivdisplaylocationt\":\""+tv_yrhw.getText().toString()+"\",\"inqty\":"+tv_yksl.getText().toString()+"}"); 
		}
		_httpTool.mStart();
	}
	
	
	private void mDoDisMessage(Message msg)
	{
		String sMess=(String)msg.obj;
		tv_MessTs.setText(sMess);
	}
	private void mSetSysData()
	{
		bt_Return=(Button)findViewById(R.id.bt_return);
		bt_sq=(Button)findViewById(R.id.bt_sq);
		bt_ok=(Button)findViewById(R.id.bt_ok);

		tv_MessTs=(TextView)findViewById(R.id.tv_ts);
		
		tv_hwcode=(TextView)findViewById(R.id.tv_hwcode);
    	tv_spbm=(TextView)findViewById(R.id.tv_spbm);
    	tv_chinaname=(TextView)findViewById(R.id.tv_chinaname);
		tv_Manu=(TextView)findViewById(R.id.tv_manu_v);
		tv_bzdw=(TextView)findViewById(R.id.tv_bzdw);
		tv_ypgg=(TextView)findViewById(R.id.tv_ypgg);
		tv_spph=(TextView)findViewById(R.id.tv_spph);
		tv_yxDate=(TextView)findViewById(R.id.tv_yxDate);
		tv_yksl=(TextView)findViewById(R.id.tv_yksl);
		tv_yrhw=(TextView)findViewById(R.id.tv_yrhw);
		tv_hwsl=(TextView)findViewById(R.id.tv_hwsl);
		tv_zbzs=(TextView)findViewById(R.id.tv_zbzs);
		tv_yrhw_jp=(TextView)findViewById(R.id.tv_yrhw_jp);
		
		edtInput=(EditText)findViewById(R.id.edt_ScnData);
		edtInput.setInputType(InputType.TYPE_NULL);
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
	    				
	    				//查询数据
	    				if (m_iCurBoxIndex==4 || m_iCurBoxIndex==3)
	    				{
	    					tv_yrhw.setText(s);
		    				edtInput.setText(""); 
		    				m_iCurBoxIndex=1;
	    				}
	    				else
	    				{
	    					tv_hwcode.setText(s);
		    				edtInput.setText("");  	
		    				m_iCurBoxIndex=3;
	    					m_receMainTask=null;
							m_sQuery=tv_hwcode.getText().toString();
							mSendData(1);
							tv_hwcode.setEnabled(false);
							tv_hwcode.setBackgroundResource(R.drawable.clrunenabled);
	    				}
	    				
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
					finish();
				}
		    }
        );
    	
    	bt_sq.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_sQuery=tv_hwcode.getText().toString();
					if (m_sQuery==null) return;
					if (m_sQuery.length()==0) return;
					mSendData(1);
				    v.setEnabled(false);
				    v.setBackgroundResource(R.drawable.clrunenabled);	
				}
		    }
        );
    	
    	bt_ok.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_sQuery=tv_hwcode.getText().toString();
					if (m_sQuery==null) return;
					if (m_sQuery.length()==0) return;
					mSendData(2);
				    v.setEnabled(false);
				    v.setBackgroundResource(R.drawable.clrunenabled);	
				}
		    }
        );
    	
    	tv_hwcode.setOnClickListener
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
    	
    	tv_yksl.setOnClickListener
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
    	
    	tv_yrhw.setOnClickListener
    	(
			new OnClickListener()
		    {
				public void onClick(View v) 
				{
					m_iCurBoxIndex=4;
				}
		    }
        );
    	
    	tv_yrhw_jp.setOnClickListener
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
    	
	}
	
	private void mDoReturnData(Message msg)
	{	
		try
		{		
			String json="",sRet1="";
			json=(String)msg.obj;
			tv_MessTs.setText(json.toString());
			sRet1=json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length()-1).toString());
	    	String ErrOrMsg =jsonObject.getString("ERRORMSG"); 
	    	if(ErrOrMsg.isEmpty())
	    	{
	    		JSONArray jsonArray =new JSONArray(jsonObject.getString("StrJson")); 
	    		if(m_curType==1)
	    		{
		    		if (jsonArray.length()>0)
		    		{
		    			
		    			for (int i = 0 ;i<jsonArray.length();i++)
		    			{
		    			  JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
		    			    tv_MessTs.setText(jsonObject2.toString());
							tv_hwcode.setText(jsonObject2.getString("DISPLAY_LOCATION"));
							tv_spbm.setText(jsonObject2.getString("GOODS_NO"));
							tv_chinaname.setText(jsonObject2.getString("GOODS_NAME"));
							tv_ypgg.setText(jsonObject2.getString("DRUG_SPEC"));
							tv_bzdw.setText(jsonObject2.getString("PACKAGE_UNIT"));
							tv_zbzs.setText(jsonObject2.getString("PACKAGE_QTY"));
							tv_Manu.setText(jsonObject2.getString("MANUFACTURER"));
							tv_spph.setText(jsonObject2.getString("GOODS_LOTNO"));
							tv_hwsl.setText(jsonObject2.getString("STOCK_QTY"));
							tv_yxDate.setText(jsonObject2.getString("PRODUCTION_DATE").substring(0, 10)+" ~ "+jsonObject2.getString("VALID_UNTIL").substring(0, 10));
							m_spid=jsonObject2.getString("GOODS_ID");
							m_spph=jsonObject2.getString("LOTNO_ID");
							m_lccode=jsonObject2.getString("LC_CODE");
							m_ownerno=jsonObject2.getString("OWNER_NO");
							tv_yksl.setText("");
							tv_yrhw.setText("");
		    			}
		    			bt_sq.setBackgroundResource(R.drawable.clsbtnselect);
						bt_sq.setEnabled(true);
		    		}

	    		}
	    		else if(m_curType==2)
	    		{
					tv_MessTs.setText(jsonObject.toString());
					mSetButtonIni();
					mShowMessage("货位调整成功");
				    bt_ok.setBackgroundResource(R.drawable.clsbtnselect);
				    bt_ok.setEnabled(true);
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
		finally{  
	        bt_ok.setBackgroundResource(R.drawable.clsbtnselect);
		    bt_ok.setEnabled(true);
        } 
	}
	
	private void mDoReturnData_old(Message msg)
	{	
		try
		{
			String sReceData="",sFunNo="",sRet1="";
			sReceData=(String)msg.obj;
			sFunNo=clsMyPublic.GetStrInOfStr(sReceData, 0);
			if (sFunNo.compareTo(clsMyPublic.g_sktParamHwPc)==0)
			{			
				sRet1=clsMyPublic.GetStrInOfStr(sReceData, 1);
				if (sRet1.compareTo("-1")==0||sRet1.compareTo("0")==0)
				{
					tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
					bt_sq.setBackgroundResource(R.drawable.clsbtnselect);
					bt_sq.setEnabled(true);
					mSetButtonIni();
					return;
				}
				else if (sRet1.compareTo("-2")==0)
				{	
					tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
					mSetButtonIni();
				}
				else
				{	
					tv_MessTs.setText(sReceData);
					tv_hwcode.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
					tv_spbm.setText(clsMyPublic.GetStrInOfStr(sReceData,3));
					tv_chinaname.setText(clsMyPublic.GetStrInOfStr(sReceData,4));
					tv_ypgg.setText(clsMyPublic.GetStrInOfStr(sReceData,5));
					tv_bzdw.setText(clsMyPublic.GetStrInOfStr(sReceData,6));
					tv_zbzs.setText(clsMyPublic.GetStrInOfStr(sReceData, 7));
					tv_Manu.setText(clsMyPublic.GetStrInOfStr(sReceData, 8));
					tv_spph.setText(clsMyPublic.GetStrInOfStr(sReceData,9));
					tv_hwsl.setText(clsMyPublic.GetStrInOfStr(sReceData, 10));
					tv_yxDate.setText(clsMyPublic.GetStrInOfStr(sReceData,11)+" ~ "+clsMyPublic.GetStrInOfStr(sReceData,12));
					m_spid=clsMyPublic.GetStrInOfStr(sReceData, 13);

					tv_yksl.setText("");
					tv_yrhw.setText("");
				}	
				
				bt_sq.setBackgroundResource(R.drawable.clsbtnselect);
				bt_sq.setEnabled(true);
			}
			else if (sFunNo.compareTo(clsMyPublic.g_sktParamHwTz)==0)
			{			
				sRet1=clsMyPublic.GetStrInOfStr(sReceData, 1);
				if (sRet1.compareTo("-1")==0||sRet1.compareTo("0")==0)
				{
					tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
					bt_ok.setBackgroundResource(R.drawable.clsbtnselect);
					bt_ok.setEnabled(true);
					return;
				}
				else if (sRet1.compareTo("-2")==0)
				{	
					tv_MessTs.setText(clsMyPublic.GetStrInOfStr(sReceData,2));
				}
				else
				{	
					tv_MessTs.setText(sReceData);
					mSetButtonIni();
					mShowMessage("货位调整成功");
				}	
				
				bt_ok.setBackgroundResource(R.drawable.clsbtnselect);
				bt_ok.setEnabled(true);
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
			new AlertDialog.Builder(actPositionMove.this)
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
		tv_hwcode.setText(readMessage);
		m_sQuery=readMessage;
		m_receMainTask=null;
		mSendData(1);
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
		lp.y=-100;
		win.setAttributes(lp);
		clsMyPublic.m_sKeys="";
		
		if (m_iCurBoxIndex==0 ||m_iCurBoxIndex==1)
			tv_Cur=tv_hwcode;
		else if (m_iCurBoxIndex==2)
			tv_Cur=tv_yksl;
		else if (m_iCurBoxIndex==3)
			tv_Cur=tv_yrhw;	
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
		    						if (m_iCurBoxIndex==1)
		    						{
			    						if(tv_Cur.getText().toString().length()>0)
			    						{
			    							m_receMainTask=null;
			    							m_sQuery=tv_hwcode.getText().toString();
			    							if (m_sQuery==null) return;
			    							if (m_sQuery.length()==0) return;
			    							mSendData(1);
			    							bt_sq.setEnabled(false);
			    							bt_sq.setBackgroundResource(R.drawable.clrunenabled);	
			    						}
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
		    					clsMyPublic.m_sKeys+=sKey.toUpperCase();
		    					tv_Cur.setText(clsMyPublic.m_sKeys);
		    					if(m_iCurBoxIndex==2)
		    					{
		    						m_iCurBoxIndex=3;
		    					}
		    				}
		    		    }
			    	);
				}
			}
	    }
		adNumKey.show();
	}
}
