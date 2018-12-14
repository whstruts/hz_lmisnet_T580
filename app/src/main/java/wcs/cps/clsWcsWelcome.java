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
implements SurfaceHolder.Callback   //实现生命周期回调接口
{
	WcpsActivity activity;//activity的引用
	Paint paint;      //画笔
	int currentAlpha=0;  //当前的不透明值
	
	DisplayMetrics dm = getResources().getDisplayMetrics();
	//int screenWidth=900;   //屏幕宽度
	//int screenHeight=800;  //屏幕高度
	int screenHeight=dm.heightPixels;
	int screenWidth=dm.widthPixels;
	
	//int screenWidth=1920;   //屏幕宽度
	//int screenHeight=1200;  //屏幕高度
	int sleepSpan=50;      //动画的时延ms
	Bitmap[] logos=new Bitmap[1];//logo图片数组
	Bitmap currentLogo;  //当前logo图片引用
	int currentX;      //图片位置
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
		this.getHolder().addCallback(this);  //设置生命周期回调接口的实现者
		paint = new Paint();  //创建画笔
		paint.setAntiAlias(true);  //打开抗锯齿
		//加载图片
		logos[0]=BitmapFactory.decodeResource(activity.getResources(), R.drawable.welcome);	
	}
	
	public void onDraw(Canvas canvas)
	{	
		//绘制黑填充矩形清背景
		paint.setColor(Color.BLACK);//设置画笔颜色
		paint.setAlpha(255);//设置不透明度为255
		canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
		//进行平面贴图
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
				currentLogo=bm;//当前图片的引用
				currentX=0;//图片位置
				currentY=80;
				for(int i=255;i>-10;i=i-10)
				{//动态更改图片的透明度值并不断重绘	
					currentAlpha=i;
					if(currentAlpha<0)//如果当前不透明度小于零
					{
						currentAlpha=0;//将不透明度置为零
					}
					SurfaceHolder myholder=clsWcsWelcome.this.getHolder();//获取回调接口
					Canvas canvas = myholder.lockCanvas();//获取画布
										
					try
					{
						synchronized(myholder)//同步
						{
							onDraw(canvas);//进行绘制绘制
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						
					}
					finally
					{
						if(canvas!= null)//如果当前画布不为空
						{
							myholder.unlockCanvasAndPost(canvas);//解锁画布
						}
					}
					
					try
					{
						if(i==255)//若是新图片，多等待一会
						{
							Thread.sleep(1000);
						}
						Thread.sleep(sleepSpan);
					}
					catch(Exception e)//抛出异常
					{
						e.printStackTrace();
					}
				}
			}	
			m_MainHandler.sendEmptyMessage(1);
			return;
		}
		
	}
	public void surfaceCreated(SurfaceHolder holder) //创建时被调用	
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
		//销毁时被调用
		//m_SocketThread.closeSocket();
	}	   
}
