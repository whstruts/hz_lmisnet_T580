package wcs.cps;

import java.util.ArrayList;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class actSupply extends Activity
{
	private TextView[] m_WorkBt;
	private int m_iSumPickBtCount=10;
	private int m_iSupTaskCount=0;
	private int m_iCurDoIndex=-1;
	private int m_iCurViewIndex=0;
	private int m_iUnDoTaskCount=-1;
	private int m_iCurMaxIndex=-1;
	private int m_iCurMinIndex=-1;
	private Button bt_Return=null;
	private Button bt_Log=null;	
	private Button bt_SQ=null;
	private Button bt_Save=null;
	private Button btUp=null;
	private Button btDown=null;
	private TextView edt_Bar=null;
	private TextView edt_Loc=null;
	private TextView edt_Sl=null;
	private Intent m_actBlnMgr=null;	
	private Intent m_actLog=null;
	private TextView tv_Cur=null;
	private TextView txt_zyts=null;
	private TextView tv_Lot=null;
	private EditText edt_ScnData=null;

	private TextView tv_PlanLoc = null;// �ƻ���λ
	private TextView tv_PlanCount = null;// �ƻ�����
	private TextView tv_ChinaName = null;// ������
	private TextView tv_Manu = null;// ��������Ϣ
	private TextView tv_PackUnit = null;// ��װ��λ
	private TextView tv_DrugSpec = null;// ҩƷ���
	private TextView tv_LotNumber = null;// ����
	private TextView tv_ValidUntil = null;// ��Ч��
	private TextView tv_RunNum = null;// ��ˮ��
	private TextView tv_MessTs=null;
	
	private String m_sZylb="";   //��ҵ���
	private String m_sDjbh="";   //���ݱ��
	private String m_sDjhh="";   //�����к�
	private String m_sJhsl="";   //�������
	private String m_sJhhw="";   //�����λ
	private String m_sCode="";   //�����λ
	private String m_sRealloc="";//ʵ�ʻ�λ
    private String m_sCurCmdid="";//��ǰCMDID
    private String m_CurStatus="";//�������ȡ״̬

	private String m_sCurBarCode="";
	public static int m_iWorkMode=0;  //��ҵģʽ  
	private String m_sClsName="actSupply";
	
	private static ArrayList<clsSupTask> m_SupTaskList=null;
	private int m_curType=0;
	
	public  Handler hd=new Handler()//������Ϣ������
	{
		public void handleMessage(Message msg)//��д����
		{			
			switch(msg.what)
			{
			case clsMyPublic.MsTypeDisMess:
				mDoDisMessage(msg);
				break;
			case clsMyPublic.MsTypeReceMess:
				mDoReturnData(msg);
				break;
		   // case clsBluetoothMgr.STATE_SEND_DATA:
		   // 	mViewBlueData(msg);
		   // 	break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actsupply);
		this.setTitleColor(Color.BLUE);
		mSetSysData();
		if (m_iWorkMode==clsMyPublic.SJZY_ZDBH)
		{
		//	this.setTitle("WCPS-����������ҵ  ��" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"��");
			this.setTitle("WCPS-������ҵ  ��" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"��");
			m_sZylb="7";
		//	txt_zyts.setText("��������");
			txt_zyts.setText("����");
		}
		else if (m_iWorkMode==clsMyPublic.SJZY_RK)
		{
			this.setTitle("WCPS-����ϼ���ҵ  ��" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"��");
			m_sZylb="9";
		}
		else if (m_iWorkMode==clsMyPublic.SJZY_BDBH)
		{
		//	this.setTitle("WCPS-����������ҵ  ��" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"��");
			this.setTitle("WCPS-������ҵ  ��" +clsMyPublic.g_WorkNo +" "+ clsMyPublic.g_WorkName +"��");
			m_sZylb="6";
		//	txt_zyts.setText("��������");
			txt_zyts.setText("����");
		}
		m_SupTaskList=new ArrayList<clsSupTask>();
		clsMyPublic.m_hdlSendMess=hd;
	//	clsBluetoothMgr.m_CurHandler=hd;
		mSetButtonIni();	
		mSendData(1);
	}
	
	
	public class clsSupTask 
	{
		String _sDanjNo="";
		String _sHangHao="";
		String _sBoxNo="";
		String _sPickHw="";
		String _sArtName;
		String _sArtGg;
		String _sArtZbz;
		String _sArtBzdw;
		String _sArtPh;
		String _sArtCj;
		String _sJhsl;
		String _sSjsl;
		String _sZylb;
		String _sFpdno;
		String _sBarCode;
		String _sArtDate;   //��Ч����
		int   _iDoStatus=0;  // 0 ��ʼ�� 2 ��ȡ�ɹ� 3 �ϼ�ȷ��
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
			_httpTool.m_params.put("func_code", "ShowRestockingTask");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\"}");  
		}
		else if (_Type==2)
		{
			m_curType=2;
            m_CurStatus="";
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ObtainBarcode");  
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"SeqBarcode\":\""+m_sCurBarCode+"\"}");  
		}
		else if (_Type==3)
		{
			m_curType=3;
			m_sRealloc=edt_Loc.getText().toString();
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ConfirmBarcodeEX");
			_httpTool.m_params.put("josnparas", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"SeqBarcode\":\""+m_sCode+"\",\"CmdId\":\""+m_sDjhh+"\",\" DisplayLocation\":\""+m_sRealloc+"\"}");
			Log.v("zms", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"SeqBarcode\":\""+m_sCode+"\",\"CmdId\":\""+m_sDjhh+"\",\" DisplayLocation\":\""+m_sRealloc+"\"}");
		}
		else if (_Type==4)
		{
			m_curType=4;
			_httpTool.m_params.put("user", clsMyPublic.g_WorkNo);  
			_httpTool.m_params.put("password", clsMyPublic.g_WorkPass);  
			_httpTool.m_params.put("lccode", clsMyPublic.g_WorkWlzxNo);  
			_httpTool.m_params.put("func_code", "ActivateBarcode");  
			_httpTool.m_params.put("josnparas", "{\"SeqBarcode\":\""+edt_Bar.getText().toString()+"\"}"); 
			Log.v("zms", "{\"STAFFNAME\":\""+clsMyPublic.g_WorkName+"\",\"SeqBarcode\":\""+edt_Bar.getText().toString()+"\"}");
		}
		_httpTool.mStart();
		
	}
	
	private void mSetButtonIni()
	{
		for(int i=0;i<m_iSumPickBtCount;i++)
		{
			m_WorkBt[i].setEnabled(false);
			m_WorkBt[i].setBackgroundResource(R.drawable.clrunenabled);
		}
		
		tv_PlanLoc.setText("");
		tv_PlanCount.setText("");	
		tv_ChinaName.setText(""); //��Ʒ����		
		tv_Manu.setText("");  //��������		
		tv_DrugSpec.setText("");  //ҩƷ���		
		tv_LotNumber.setText("");  //ҩƷ����		
		tv_PackUnit.setText("");   //ҩƷ��װ��λ����		
		tv_ValidUntil.setText("");    //��ת����
		tv_RunNum.setText(""); 
		
		bt_Save.setEnabled(false);
		bt_Save.setBackgroundResource(R.drawable.clrunenabled);
		
		bt_SQ.setEnabled(true);
		bt_SQ.setBackgroundResource(R.drawable.clsbtnselect);
	}
	
	private void mSetSysData()
	{
		bt_Return=(Button)findViewById(R.id.bt_return);
		//bt_Log=(Button)findViewById(R.id.bt_log);
		bt_SQ=(Button)findViewById(R.id.bt_boxtake);
		bt_Save=(Button)findViewById(R.id.bt_save);
		edt_Bar=(TextView)findViewById(R.id.et_barcode);
		edt_Loc=(TextView)findViewById(R.id.et_realloc);
		edt_Sl=(TextView)findViewById(R.id.edt_sjsl);

		btUp=(Button)findViewById(R.id.bt_Up);
		btDown=(Button)findViewById(R.id.bt_Down);
		btDown.setEnabled(false);
		btUp.setEnabled(false);
		btDown.setBackgroundResource(R.drawable.clrunenabled);
		btUp.setBackgroundResource(R.drawable.clrunenabled);

    	edt_ScnData=(EditText)findViewById(R.id.edt_ScnData);
    	edt_ScnData.setInputType(InputType.TYPE_NULL);
    	
    	tv_PlanLoc = (TextView) findViewById(R.id.tv_taskloc_v);
		tv_PlanCount = (TextView) findViewById(R.id.tv_plancount_v);
		tv_ChinaName = (TextView) findViewById(R.id.tv_chinaname_v);
		tv_Manu = (TextView) findViewById(R.id.tv_manu_v);
		tv_PackUnit = (TextView) findViewById(R.id.tv_bzdw);
		tv_DrugSpec = (TextView) findViewById(R.id.tv_ypgg);
		tv_LotNumber = (TextView) findViewById(R.id.tv_spph);
		tv_ValidUntil = (TextView) findViewById(R.id.tv_yxDate);
		tv_RunNum = (TextView) findViewById(R.id.tv_BarNo);
		txt_zyts= (TextView) findViewById(R.id.txt_zyts);
		
		//edt_Bar.setText("3020d0100038082");
		tv_MessTs=(TextView)findViewById(R.id.tv_pi);
    	//�����λ��ť��ʼ��
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
    	
    	for(int i=0;i<10;i++)
    	{
    		m_WorkBt[i].setVisibility(View.INVISIBLE);
    	}
    	
    	for(int i=0;i<this.m_iSumPickBtCount;i++)
		{
    		m_WorkBt[i].setEnabled(false);
    		m_WorkBt[i].setOnClickListener
			(
				new OnClickListener()
			    {
					public void onClick(View v) 
					{
						mViewSupData(v);
						m_sCurBarCode="";
						//edt_Bar.setText("");
					}
			    }
			);
        }	
		
		tv_PlanLoc.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});   	
		tv_PlanCount.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});  
		tv_ChinaName.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});   	
		tv_Manu.setOnClickListener (new OnClickListener(){public void onClick(View v) {}}); 
		tv_PackUnit.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});   	
		tv_DrugSpec.setOnClickListener (new OnClickListener(){public void onClick(View v) {}}); 
		tv_LotNumber.setOnClickListener (new OnClickListener(){public void onClick(View v) {}});   	
		tv_ValidUntil.setOnClickListener (new OnClickListener(){public void onClick(View v) {}}); 
		tv_RunNum.setOnClickListener (new OnClickListener(){public void onClick(View v) {}}); 
		
		
		edt_ScnData.setOnKeyListener
    	(
    		new View.OnKeyListener()
	    	{
	    		public boolean onKey(View v, int keyCode, KeyEvent event)
	    		{
	    			if (keyCode==KeyEvent.KEYCODE_ENTER)
	    			{		    				
	    				String s="";
	    				keyCode=0;
	    				s=edt_ScnData.getText().toString();
	    				s=s.trim();
	    				if (s.length()==0) 
	    				{  
	    					return true;
	    				}
	    				edt_Bar.setText(s);
	    				edt_ScnData.setText("");
	    				m_sCurBarCode=s;
	    				mSQBhZyData(s);
	    			}
	    			return false;
	    		}
	    	}
    	);
		
		//�����¼�
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

//���·�ҳ�¼�
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
		//���Ϸ�ҳ�¼�
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

		//��־�鿴
    	/*
    	bt_Log.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mViewUi(4);
    				}
    		    }	
    	);*/
    	//��־�鿴
    	bt_SQ.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					m_sCurBarCode=edt_Bar.getText().toString();
    					if (m_sCurBarCode.length()==0)
    					{
    						mShowMessage("��ɨ�����������ҵ��ǩ����......");
    						return;
    					}
    					mSQBhZyData(m_sCurBarCode);
    				}
    		    }	
    	);
    	bt_Save.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{
    					mSendData(3);
    					v.setEnabled(false);
    					v.setBackgroundResource(R.drawable.clrunenabled);
    				}
    		    }	
    	);
    	edt_Bar.setOnClickListener
    	(
    			new OnClickListener()
    		    {
    				public void onClick(View v) 
    				{  
    					v.setBackgroundResource(R.drawable.clrpressyes);
    					edt_Loc.setBackgroundResource(R.drawable.clspickmsgselect);
    					edt_Sl.setBackgroundResource(R.drawable.clspickmsgselect);
    					mCharKeys();
    				}
    		    }		
    	);
		edt_Loc.setOnClickListener(
				new OnClickListener()
				{
					public void onClick(View v)
					{
						if(m_sZylb=="9")
						{
							v.setBackgroundResource(R.drawable.clrpressyes);
							mLotCharKeys();
						}
					}
				}
				);
    	edt_Sl.setOnClickListener(new OnClickListener(){public void onClick(View v) {}});

	}
	private void mViewUi(int _UiNo)
	{
		switch(_UiNo)
		{
		case 1:
			
			break;
		case 2:
		//	m_actBlnMgr=new Intent(this,actBlueMgr.class);
		//	startActivityForResult(m_actBlnMgr,2);
		//	break;
		case 3:
			break;
		case 4:
			m_actLog=new Intent(this,LogDBHelper.class);
			startActivityForResult(m_actLog,4);
			break;
		}
	}
	
	private void mViewSupData(View v)
	{
		clsSupTask _SupTask=new clsSupTask();
    	int tag = (Integer) v.getTag();
    	_SupTask=m_SupTaskList.get(tag);
		if (m_iCurDoIndex>=0)
		{
			//int curOldIndex=(Integer) m_WorkBt[m_iCurDoIndex].getTag();
			clsSupTask _SupTask1=new clsSupTask();
			_SupTask1=m_SupTaskList.get(m_iCurDoIndex);
			//_SupTask1=m_SupTaskList.get(curOldIndex);
			if (_SupTask1._iDoStatus==1)
				m_WorkBt[m_iCurDoIndex%10].setBackgroundResource(R.drawable.clrendboxback);
			else
				m_WorkBt[m_iCurDoIndex%10].setBackgroundResource(R.drawable.clsbtnselect);
		}
		m_iCurDoIndex=tag;
		tv_PlanLoc.setText(_SupTask._sPickHw);
		edt_Loc.setText(_SupTask._sPickHw);
		tv_PlanCount.setText(_SupTask._sJhsl);
		edt_Sl.setText(_SupTask._sJhsl);
		tv_ChinaName.setText(_SupTask._sArtName); //��Ʒ����		
		tv_Manu.setText(_SupTask._sArtCj);  //��������		
		tv_DrugSpec.setText(_SupTask._sArtGg);  //ҩƷ���		
		tv_LotNumber.setText(_SupTask._sArtPh);  //ҩƷ����		
		tv_PackUnit.setText(_SupTask._sArtBzdw);   //ҩƷ��װ��λ����		
		tv_ValidUntil.setText(_SupTask._sArtDate);    //��ת����
		tv_RunNum.setText(_SupTask._sBarCode);     //����				

		m_sDjbh=_SupTask._sDanjNo;   //���ݱ��
		m_sDjhh=_SupTask._sHangHao;   //�����к�
		m_sJhsl=_SupTask._sJhsl;   //�������
		m_sJhhw=_SupTask._sPickHw;   //�����λ_SupTask._sDanjNo
		m_sCode=_SupTask._sBarCode;

		//������ҵ
		if (_SupTask._iDoStatus==0)
		{	
			edt_Bar.setText(_SupTask._sBarCode);
			bt_Save.setEnabled(true);
			bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
			bt_SQ.setEnabled(true);
			bt_SQ.setBackgroundResource(R.drawable.clsbtnselect);
			v.setBackgroundResource(R.drawable.clrcurworkhw);
		}
		else if (_SupTask._iDoStatus==1)
		{
			v.setBackgroundResource(R.drawable.clrunenabled);
			mShowMessage("�Ѿ��ϼ�ȷ�ϣ���˶�....");
			bt_SQ.setEnabled(false);
			bt_SQ.setBackgroundResource(R.drawable.clrunenabled);
			bt_Save.setEnabled(false);
			bt_Save.setBackgroundResource(R.drawable.clrunenabled);
		}	
	}
	private void mDoReturnData(Message msg)
	{	
		String json="",sFunNo="",sRet1="";
		json=(String)msg.obj;
		try
		{
			//tv_MessTs.setText(json);
			Log.v("zms","2"+json);
			//JSONObject jsonObject = new JSONObject(json.toString());
	    	//jsonObject = new JSONObject(jsonObject.getString(HttpTool.m_sktResult));
			sRet1=json.replace("\\", "");
			JSONObject jsonObject = new JSONObject(sRet1.substring(1, sRet1.length()-1).toString());
	    	String ErrOrMsg =jsonObject.getString("ERRORMSG"); 
	    	if(ErrOrMsg.isEmpty()||m_curType==4)
	    	{
	    		JSONArray jsonArray =new JSONArray(jsonObject.getString("StrJson")); 
	    		edt_Bar.setText("");
	    		if(m_curType==1)
	    		{
		    		if (jsonArray.length()>0)
		    		{

		    			//Log.v("zms","2"+jsonObject.getString("StrJson"));
		    			
		    			m_iSupTaskCount=jsonArray.length();
						m_iUnDoTaskCount=m_iSupTaskCount;

		    			for (int i=0;i<jsonArray.length();i++)
			    		{
		    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
		    				clsSupTask _SupTask=new clsSupTask();
		    				_SupTask._sPickHw=jsonObject2.getString("DISPLAY_LOCATION");  //�����λ
		    				_SupTask._sJhsl=jsonObject2.getString("PLANNED_QTY"); //�������
		    				_SupTask._sArtName=jsonObject2.getString("GOODS_NAME"); //��Ʒ����
		    				_SupTask._sArtCj=jsonObject2.getString("MANUFACTURER");//��������
		    				_SupTask._sArtGg=jsonObject2.getString("DRUG_SPEC");//ҩƷ���
		    				_SupTask._sArtPh=jsonObject2.getString("GOODS_LOTNO"); //ҩƷ����
		    				_SupTask._sArtBzdw=jsonObject2.getString("PACKAGE_QTY")+jsonObject2.getString("PACKAGE_UNIT"); //ҩƷ��װ��λ����
		    				_SupTask._sArtDate=jsonObject2.getString("PRODUCTION_DATE"); //��ת����
		    				_SupTask._sBarCode=jsonObject2.getString("SEQ_BARCODE");   //�ϼܱ�ǩ
		    				_SupTask._sDanjNo="";//jsonObject2.getString("GOODS_NAME");
							_SupTask._sHangHao=jsonObject2.getString("CMD_ID");
							_SupTask._sZylb=jsonObject2.getString("OPER_CATE");
							_SupTask._iDoStatus=0; //��ȡ��������
							
							if(_SupTask._sZylb.compareTo("6") ==0)
							{
								txt_zyts.setText("��������");
							}
							else if(_SupTask._sZylb.compareTo("7") ==0)
							{
								txt_zyts.setText("��������");
							}else if(_SupTask._sZylb.compareTo("9") ==0)
							{
								m_iWorkMode=clsMyPublic.SJZY_RK;
								txt_zyts.setText("����ϼ�");
								m_sZylb="9";
							}
							
							if(m_iCurViewIndex<10)
							{
								m_WorkBt[m_iCurViewIndex].setText(_SupTask._sPickHw);
								m_WorkBt[m_iCurViewIndex].setTag(m_iCurViewIndex);
								m_WorkBt[m_iCurViewIndex].setEnabled(true);
								m_WorkBt[m_iCurViewIndex].setVisibility(View.VISIBLE);
								m_WorkBt[m_iCurViewIndex].setBackgroundResource(R.drawable.clsbtnselect);
							}
							if (m_iCurViewIndex>=10)
							{
								mVewPickData(0);
							}
                            m_SupTaskList.add(_SupTask);

							m_iCurViewIndex++;
							
							bt_SQ.setEnabled(true);
							bt_SQ.setBackgroundResource(R.drawable.clsbtnselect);
							bt_Save.setEnabled(false);
							bt_Save.setBackgroundResource(R.drawable.clrunenabled);

							if ((m_SupTaskList.size()- m_iUnDoTaskCount)==m_iSupTaskCount)
							{
								m_iSupTaskCount =m_SupTaskList.size();
								m_iUnDoTaskCount=m_iSupTaskCount;

								if (m_iWorkMode==clsMyPublic.SJZY_BDBH)
									mViewSupData(m_WorkBt[0]);   //ָ��ɨ�� ��������Ĭ�ϵ�һ������
								else
								{
									if(m_iCurViewIndex>=10)
									{
										mViewSupData(m_WorkBt[0]); //��������Ĭ�����һ������
									}
									else {
										mViewSupData(m_WorkBt[m_iCurViewIndex-1]); //��������Ĭ�����һ������
									}

								}
							}
							//if (m_iCurViewIndex==m_iSupTaskCount)
							//{
							//	if (m_iWorkMode==clsMyPublic.SJZY_BDBH)
							//		mViewSupData(m_WorkBt[0]);   //ָ��ɨ�� ��������Ĭ�ϵ�һ������
							//	else
							//		mViewSupData(m_WorkBt[m_iCurViewIndex-1]); //��������Ĭ�����һ������
							//}
			    		}
		    		}
		    		
	    		}
	    		else if(m_curType==2)
	    		{
		    		if (jsonArray.length()>0)
		    		{
		    			Log.v("zms","2"+jsonObject.getString("StrJson"));
		    			
		    			m_iSupTaskCount=jsonArray.length();
						m_iUnDoTaskCount=m_iSupTaskCount;

		    			for (int i=0;i<jsonArray.length();i++)
			    		{
		    				JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
		    				clsSupTask _SupTask=new clsSupTask();
		    				_SupTask._sPickHw=jsonObject2.getString("DISPLAY_LOCATION");  //�����λ
		    				_SupTask._sJhsl=jsonObject2.getString("PLANNED_QTY"); //�������
		    				_SupTask._sArtName=jsonObject2.getString("GOODS_NAME"); //��Ʒ����
		    				_SupTask._sArtCj=jsonObject2.getString("MANUFACTURER");//��������
		    				_SupTask._sArtGg=jsonObject2.getString("DRUG_SPEC");//ҩƷ���
		    				_SupTask._sArtPh=jsonObject2.getString("GOODS_LOTNO"); //ҩƷ����
		    				_SupTask._sArtBzdw=jsonObject2.getString("PACKAGE_QTY")+jsonObject2.getString("PACKAGE_UNIT"); //ҩƷ��װ��λ����
		    				_SupTask._sArtDate=jsonObject2.getString("PRODUCTION_DATE"); //��ת����
		    				_SupTask._sBarCode=jsonObject2.getString("SEQ_BARCODE");   //�ϼܱ�ǩ
		    				_SupTask._sDanjNo="";//jsonObject2.getString("GOODS_NAME");
							_SupTask._sHangHao=jsonObject2.getString("CMD_ID");
							_SupTask._sZylb=jsonObject2.getString("OPER_CATE");
							_SupTask._iDoStatus=0; //��ȡ��������
							
							if(_SupTask._sZylb.compareTo("6") ==0)
							{
								txt_zyts.setText("��������");
							}
							else if(_SupTask._sZylb.compareTo("7") ==0)
							{
								txt_zyts.setText("��������");
							}else if(_SupTask._sZylb.compareTo("9") ==0)
							{
								m_iWorkMode=clsMyPublic.SJZY_RK;
								txt_zyts.setText("����ϼ�");
								m_sZylb="9";
							}
							if(m_iCurViewIndex<10)
							{
								m_WorkBt[m_iCurViewIndex].setText(_SupTask._sPickHw);
								m_WorkBt[m_iCurViewIndex].setTag(m_iCurViewIndex);
								m_WorkBt[m_iCurViewIndex].setEnabled(true);
								m_WorkBt[m_iCurViewIndex].setVisibility(View.VISIBLE);
								m_WorkBt[m_iCurViewIndex].setBackgroundResource(R.drawable.clsbtnselect);
							}
							if (m_iCurViewIndex>=10)
							{
								mVewPickData(0);
							}
                            m_SupTaskList.add(_SupTask);

							m_iCurViewIndex++;
							
							bt_SQ.setEnabled(true);
							bt_SQ.setBackgroundResource(R.drawable.clsbtnselect);
							bt_Save.setEnabled(false);
							bt_Save.setBackgroundResource(R.drawable.clrunenabled);
			    		}
		    			//if (m_iWorkMode==clsMyPublic.SJZY_BDBH||m_iWorkMode==clsMyPublic.SJZY_RK)
						//	mViewSupData(m_WorkBt[0]);   //ָ��ɨ�� ��������Ĭ�ϵ�һ������
						//else
						//	mViewSupData(m_WorkBt[m_iCurViewIndex-1]); //��������Ĭ�����һ������
						if ((m_SupTaskList.size()- m_iUnDoTaskCount)==m_iSupTaskCount)
						{
							m_iSupTaskCount = m_SupTaskList.size();
							m_iUnDoTaskCount = m_iSupTaskCount;

							if (m_iWorkMode == clsMyPublic.SJZY_BDBH || m_iWorkMode == clsMyPublic.SJZY_RK)
								mViewSupData(m_WorkBt[0]);   //ָ��ɨ�� ��������Ĭ�ϵ�һ������
							else
								{
								if (m_iCurViewIndex >= 10)
								{
									mViewSupData(m_WorkBt[0]); //��������Ĭ�����һ������
								}
								else
								{
									mViewSupData(m_WorkBt[m_iCurViewIndex - 1]); //��������Ĭ�����һ������
								}
							}
						}
		    		}
	    		}
	    		else if(m_curType==3)
	    		{
	    			bt_Save.setEnabled(true);
					bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
					m_WorkBt[m_iCurDoIndex%10].setBackgroundResource(R.drawable.clrendboxback);
					clsSupTask _CurSupTask=new clsSupTask();
					_CurSupTask=m_SupTaskList.get(m_iCurDoIndex);
					_CurSupTask._iDoStatus=1;
					m_SupTaskList.set(m_iCurDoIndex, _CurSupTask);
					mClrea();
					m_iUnDoTaskCount--;
					if (m_iUnDoTaskCount==0)
						mShowMessageII("ȫ����ҵȷ�ϳɹ����������ҵ;�Ƿ�Ҫ�˳�������?");
					else
					{
						//�Զ���ʾ��һ����λ
						if(m_iCurDoIndex<(m_iSupTaskCount-1))
							mViewSupData(m_WorkBt[(m_iCurDoIndex+1)%10]);
					}
	    		}
	    		else if (m_curType==4) 
	    		{
                    bt_SQ.setEnabled(true);
					bt_SQ.setBackgroundResource(R.drawable.clsbtnselect);
					bt_Save.setEnabled(false);
					bt_Save.setBackgroundResource(R.drawable.clrunenabled);
					mSendData(2);
				}
	    	}
	    	else
	    	{
	    		bt_SQ.setEnabled(true);
				bt_SQ.setBackgroundResource(R.drawable.clsbtnselect);
				bt_Save.setEnabled(true);
				bt_Save.setBackgroundResource(R.drawable.clsbtnselect);
	    		tv_MessTs.setText(ErrOrMsg);
	    	}
			
		}
		catch (Exception e)
		{
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
			Log.v("zms","3"+sErrLog);
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
	        mShowMessage(sErrLog);
	    }
		catch(UnknownError e)
		{
			String sErrLog="";
			sErrLog=m_sClsName +";mDoReturnData;" + e.toString();
			Log.v("zms","3"+sErrLog);
	        LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,m_sClsName,clsMyPublic.g_WorkName,sErrLog);
		}
	}
	private void mSQBhZyData(String _BarCode)
	{
		int iFind=-1;
		
		mSendData(4);
		
		for (int i=0;i<m_SupTaskList.size();i++)
		{
			clsSupTask _SupTask=new clsSupTask();
			_SupTask=m_SupTaskList.get(i);
			if (_SupTask._sBarCode.compareTo(_BarCode)==0)
			{	
				m_WorkBt[i].setBackgroundResource(R.drawable.clrcurworkhw);
				mViewSupData(m_WorkBt[i]);
				iFind=1;
				break;
			}
		}		
		//�������		
		if (iFind==-1)
		{
			m_sDjbh="";   //���ݱ��
			m_sDjhh="";   //�����к�
			m_sJhsl="";   //�������
			m_sJhhw="";   //�����λ_SupTask._sDanjNo
			bt_SQ.setEnabled(true);
			bt_SQ.setBackgroundResource(R.drawable.clsbtnselect);
			bt_Save.setEnabled(false);
			bt_Save.setBackgroundResource(R.drawable.clrunenabled);
			//���û���ҵ�����LMIS��ȡ����
			if (m_sZylb=="7")
			{
				//mSendData(2);
				bt_SQ.setEnabled(false);
				bt_SQ.setBackgroundResource(R.drawable.clrunenabled);
			}
			else if (m_sZylb=="6")
			{
				edt_Bar.setText("");
				//����Ǳ�����������ʾ����������
				mShowMessage("�����ָ����Χ�ڵ���ҵ��ǩ��Ȼ����ҵ......");
			}
		}
	}
	private void mDoDisMessage(Message msg)
	{
		String sMess=(String)msg.obj;
		tv_MessTs.setText(sMess);
	}
	private void mViewBlueData(Message msg)
	{
		byte[] readBuf = (byte[]) msg.obj;
		String readMessage = new String(readBuf, 0, msg.arg1);
		readMessage=readMessage.trim();
		edt_Bar.setText(readMessage);
		m_sCurBarCode=readMessage;
		mSQBhZyData(m_sCurBarCode);
	}
	
	private void mClrea()
	{
		tv_PlanLoc.setText("");
		tv_PlanCount.setText("");
		tv_ChinaName.setText("");
		tv_Manu.setText("");
		tv_DrugSpec.setText("");
		tv_LotNumber.setText("");
		tv_PackUnit.setText("");
		tv_ValidUntil.setText("");
		tv_RunNum.setText("");
		edt_Bar.setText("");
		edt_Loc.setText("");
		edt_Sl.setText("");
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
				.setTitle("����").setView(loginLayout).create();
		Window win=adNumKey.getWindow();
		WindowManager.LayoutParams lp=win.getAttributes();
		lp.x=-500;
		lp.y=500;
		win.setAttributes(lp);
		clsMyPublic.m_sKeys="";
		tv_Cur=edt_Bar;
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
		    					if (sKey.compareTo("�س�")==0)
		    					{
		    						m_sCurBarCode=clsMyPublic.m_sKeys;
		    						if(m_sCurBarCode.length()>0)
		    						{
		    							//m_sCurBarCode="";
		    							mSQBhZyData(m_sCurBarCode);
		    						}
		    						adNumKey.cancel();
		    						return;
		    					}
		    					else if (sKey.compareTo("ɾ��")==0)
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
		    					else if (sKey.compareTo("���")==0)
		    					{
		    						clsMyPublic.m_sKeys="";
		    						tv_Cur.setText("");
		    						return;
		    					}
		    					else if(sKey.compareTo("ȡ��")==0)
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
	private void mLotCharKeys()
	{
		LinearLayout loginLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.actcharkeys, null);
		final AlertDialog adNumKey=
				new AlertDialog.Builder(this)
						.setTitle("����").setView(loginLayout).create();
		Window win=adNumKey.getWindow();
		WindowManager.LayoutParams lp=win.getAttributes();
		lp.x=-500;
		lp.y=500;
		win.setAttributes(lp);
		clsMyPublic.m_sKeys="";
		tv_Lot=edt_Loc;
		clsMyPublic.m_sKeys=tv_Lot.getText().toString();
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
											if (sKey.compareTo("�س�")==0)
											{
												//m_sCurBarCode=clsMyPublic.m_sKeys;
												//if(m_sCurBarCode.length()>0)
												//{
													//m_sCurBarCode="";
												//	mSQBhZyData(m_sCurBarCode);
												//}
												adNumKey.cancel();
												return;
											}
											else if (sKey.compareTo("ɾ��")==0)
											{
												int iLen=-1;
												iLen=clsMyPublic.m_sKeys.length();
												if (iLen>0)
												{
													clsMyPublic.m_sKeys=clsMyPublic.m_sKeys.substring(0,iLen-1);
													tv_Lot.setText(clsMyPublic.m_sKeys);
												}
												return;
											}
											else if (sKey.compareTo("���")==0)
											{
												clsMyPublic.m_sKeys="";
												tv_Lot.setText("");
												return;
											}
											else if(sKey.compareTo("ȡ��")==0)
											{
												adNumKey.cancel();
												return;
											}
											clsMyPublic.m_sKeys+=sKey.toUpperCase();
											tv_Lot.setText(clsMyPublic.m_sKeys);
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
			new AlertDialog.Builder(actSupply.this)
	        .setTitle("��ܰ��ʾ")
	        .setIcon(android.R.drawable.alert_dark_frame)
	        .setMessage(_sMess)
	        .setNegativeButton("ȷ��", new DialogInterface.OnClickListener()
	 	    {
	 		   public void onClick(DialogInterface dialog, int which) 
	 		   {
	 			   if (m_iUnDoTaskCount==0)
	 				   finish();
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
	
	public  void mShowMessageII(String _sMess)
	{
		new AlertDialog.Builder(actSupply.this)
        .setTitle("��ܰ��ʾ")
        .setIcon(android.R.drawable.alert_dark_frame)
        .setMessage(_sMess+"\nȷ��Ҫ�˳���������?")
        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener()
		   {
			   public void onClick(DialogInterface dialog, int which)
			   {
				   
			   }
		   }
		   ).setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
 	    {
 		   public void onClick(DialogInterface dialog, int which) 
 		   {
				finish();
 		   }
 		 }).show();
	}

	private void mVewPickData(int _Type)
	{
		int iCurIndex=0;
		clsSupTask _PickTask=new clsSupTask();
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
				if (iCurIndex<this.m_iSupTaskCount)
				{
					_PickTask=m_SupTaskList.get(iCurIndex);
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
			if ((m_iSupTaskCount-1)>m_iCurMaxIndex)
			{
				btDown.setEnabled(true);
				btDown.setBackgroundResource(R.drawable.clsbtnselect);
			}
		}
		else if(_Type==1)
		{
			//���·�ҳ
			if(m_iCurMaxIndex==(m_iSupTaskCount-1)) return;

			m_iCurMinIndex=m_iCurMaxIndex+1;
			iCurIndex=m_iCurMinIndex;
			for(int i=0;i<m_iSumPickBtCount;i++)
			{
				if (iCurIndex<this.m_iSupTaskCount)
				{
					_PickTask=m_SupTaskList.get(iCurIndex);
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
			if ((m_iSupTaskCount-1)>m_iCurMaxIndex)
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
				_PickTask=m_SupTaskList.get(iCurIndex);
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
			if ((m_iSupTaskCount-1)>m_iCurMaxIndex)
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

	private void mSetPickColor(int _iIndex,int _iTaskIndex)
	{
		if (m_iCurDoIndex==_iTaskIndex)
			m_WorkBt[_iIndex].setBackgroundResource(R.drawable.clrcurworkhw);
		else
			m_WorkBt[_iIndex].setBackgroundResource(R.drawable.clrhwselect);

	}

}
