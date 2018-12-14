package wcs.cps;

import wcs.cps.http.HttpTool;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
public class clsWcsWelcome extends SurfaceView
implements SurfaceHolder.Callback   //ʵ���������ڻص��ӿ�
{
	WcpsActivity activity;//activity������
	Paint paint;      //����
	int currentAlpha=0;  //��ǰ�Ĳ�͸��ֵ
	
	DisplayMetrics dm = getResources().getDisplayMetrics();
	//int screenWidth=900;   //��Ļ���
	//int screenHeight=800;  //��Ļ�߶�
	int screenHeight=dm.heightPixels;
	int screenWidth=dm.widthPixels;
	
	//int screenWidth=1920;   //��Ļ���
	//int screenHeight=1200;  //��Ļ�߶�
	int sleepSpan=50;      //������ʱ��ms
	Bitmap[] logos=new Bitmap[1];//logoͼƬ����
	Bitmap currentLogo;  //��ǰlogoͼƬ����
	int currentX;      //ͼƬλ��
	int currentY;
	public static  Handler m_MainHandler;
	MyThread m_myThread=null;
    //clsSocketMgr m_SocketThread=null;
    LogDBHelper  m_LogDB=null;
	public static int m_iStartStoketThread=0;
	public static int m_iStartWelcome=0;

	
	public clsWcsWelcome(WcpsActivity activity)
	{
		super(activity);
		this.activity = activity;
		this.getHolder().addCallback(this);  //�����������ڻص��ӿڵ�ʵ����
		paint = new Paint();  //��������
		paint.setAntiAlias(true);  //�򿪿����
		//����ͼƬ
		logos[0]=BitmapFactory.decodeResource(activity.getResources(), R.drawable.welcome);	
	}
	
	public void onDraw(Canvas canvas)
	{	
		//���ƺ��������屳��
		paint.setColor(Color.BLACK);//���û�����ɫ
		paint.setAlpha(255);//���ò�͸����Ϊ255
		canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
		//����ƽ����ͼ
		if(currentLogo==null)return;
		paint.setAlpha(currentAlpha);		
		canvas.drawBitmap(currentLogo, currentX, currentY, paint);	
	}
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
	}
	class MyThread extends Thread
	{
		//@SuppressLint("WrongCall")
		@SuppressLint("WrongCall")
		public void run()
		{
			for(Bitmap bm:logos)
			{
				currentLogo=bm;//��ǰͼƬ������
				currentX=0;//ͼƬλ��
				currentY=80;
				for(int i=255;i>-10;i=i-10)
				{//��̬����ͼƬ��͸����ֵ�������ػ�	
					currentAlpha=i;
					if(currentAlpha<0)//�����ǰ��͸����С����
					{
						currentAlpha=0;//����͸������Ϊ��
					}
					SurfaceHolder myholder=clsWcsWelcome.this.getHolder();//��ȡ�ص��ӿ�
					Canvas canvas = myholder.lockCanvas();//��ȡ����
										
					try
					{
						synchronized(myholder)//ͬ��
						{
							onDraw(canvas);//���л��ƻ���
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						
					}
					finally
					{
						if(canvas!= null)//�����ǰ������Ϊ��
						{
							myholder.unlockCanvasAndPost(canvas);//��������
						}
					}
					
					try
					{
						if(i==255)//������ͼƬ����ȴ�һ��
						{
							Thread.sleep(1000);
						}
						Thread.sleep(sleepSpan);
					}
					catch(Exception e)//�׳��쳣
					{
						e.printStackTrace();
					}
				}
			}	
			m_MainHandler.sendEmptyMessage(1);
			return;
		}
		
	}
	public void surfaceCreated(SurfaceHolder holder) //����ʱ������	
	{
		if (m_LogDB==null)
		{
			m_LogDB = new LogDBHelper(activity.getApplicationContext());
			m_LogDB.gCreateTable();
			///clsSocketMgr.m_sktSrvIp=LogDBHelper.gGetColValue("SERIP");
			String SvrIp=null;
			SvrIp=LogDBHelper.gGetColValue("SERIP");
			if (SvrIp.length()>0)
				HttpTool.m_SvrIp=LogDBHelper.gGetColValue("SERIP");

			String sPort=null;
			sPort=LogDBHelper.gGetColValue("SERPORT");
			if (sPort.length()>0)
				HttpTool.m_SvrPort=sPort;

			String sWlzxno=null;
			sWlzxno=LogDBHelper.gGetColValue("WlzxNo");
			if (sWlzxno.length()>0) {
				clsMyPublic.g_WorkWlzxNo=sWlzxno;
				clsMyPublic.g_WorkWlzxName=LogDBHelper.gGetColValue("WlzxName");
			}
		}
        
		/*
		if (m_iStartStoketThread==0)
		{
			m_SocketThread=new clsSocketMgr();
			m_SocketThread.mStart();
			m_iStartStoketThread=1;
		}
		*/
		if (m_iStartWelcome==0)
		{
			m_myThread=new MyThread();
			m_myThread.start();
			m_iStartWelcome=1;
		}
	}
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		//����ʱ������
		//m_SocketThread.closeSocket();
	}	   
}
