package wcs.cps;


import android.content.Context;
import android.os.Handler;


public class clsMyPublic
{

	public static  Handler m_hdlSendMess;
	public static final int MsTypeDisMess=0;  //显示提示信息
	public static final int MsTypeSendMess=1;  //发送消息
	public static final int MsTypeReceMess=2;  //接受消息
	public static final int LyTypeReceMess=3;  //蓝牙消息
	
	public static final int SJZY_BH=1;  //补货作业
	public static final int SJZY_RK=2;  //入库作业
	public static final int SJZY_BDBH=3;  //被动补货作业
	public static final int SJZY_ZDBH=4;  //主动补货作业
	
	public static final int JHZY_XJ=1;   //下架作业
	public static final int JHZY_PD=2;   //盘点作业
	public static final int JHZY_TQ=3;   //提前拣选作业
	
	public static  String m_sKeys="";
	public static  String g_WorkWlzxNo="";   //物流中心编码
	public static  String g_WorkWlzxName=""; //物流中心名字
	public static  String g_WorkNo="";   //员工编号
	public static  String g_WorkName="North"; //员工名称
	public static  String g_WorkAreaNo="";   //作业区域
	public static  String g_WorkPass="";  //员工密码
	public static  String g_LocalIp="";   //本地IP
	
	
	//向服务端发送消息的格式
	// 标示 + 返回格式 + 参数个数+ 参数 串
	public static final String  g_ResultTypeStr="01";    //返回字符串的
	public static final String  g_ResultTypeCln="02";    //返回结果集
	
	public static final String  g_SktParamLogin="PBS0001";  //登陆
	public static final String  g_SktParamExit="PBS0002";   //退出
	public static final String  g_SktParamMainTask="PBS0003";     //查看任务
	public static final String  g_SktParamPickTask="PBS0004";     //发起拣货作业任务
	public static final String  g_SktParamQRPick="PBS0005";       //拣货确认作业
	public static final String  g_SktParamSQPick="PBS0006";       //索取拣货作业任务
	public static final String  g_SktParamMatch="PBS0007";        //关联周转箱作业任务
	public static final String  g_SktParamSQZDBh="PBS0008";       //索取主动补货作业任务
	public static final String  g_SktParamQRZDBH="PBS0009";       //确认主动补货作业任务
	public static final String  g_SktParamCXPick="PBS0010";       //查询拣货作业明细
	public static final String  g_sktParamSQWrkType="PSB0011";    //索取作业类型
	public static final String  g_sktParamSFWGL="PSB0012";        //判断是否有未关联的任务
	public static final String  g_sktParamCXWork="PSB0013";       //查询作业记录
	public static final String  g_sktParamCXStock="PSB0014";      //查询库存
    public static final String  g_sktParamFpdGroup="PSB0015";   //分配单组
    public static final String  g_sktParamBhActiv="PSB0016";      //补货条码激活
    public static final String  g_sktParamBhDanwei="PSB0017";     //补货到位激活
    
    public static final String  g_sktParamHwPc="PSB0018";      //货位盘查
    public static final String  g_sktParamHwTz="PSB0019";      //货位调整
    public static final String  g_sktParamHwDp="PSB0020";      //货位动盘
    
    public static final String  g_sktParamPrnData = "PSB9999";      //测试打印
    public static final String  g_sktParamPrnTest = "PSB9998";      //测试打印
    public static final String  g_sktParamLsZtData = "PSB9001";   //绿色自提打印
    public static final String  g_sktParamCnntState = "CPS0000";    //判断连线是否正常
    
    public static final String  g_sktParamCountChg="CPS0001";    //交替显示颜色
    
    
	
	
	//作业类型
	public static final String g_SqWorkType_Tqjx="TQ";    //提前拣选
	public static final String g_SqWorkType_Pdzy="1";     //盘点作业
	public static final String g_SqWorkType_Lstd="2";     //绿色通道
	public static final String g_SqWorkType_Zjjh="13";     //追加拣货
	public static final String g_SqWorkType_Ztzy="4";     //自提作业
	public static final String g_SqWorkType_Ptzy="5";     //普通出库
	public static final String g_SqWorkType_Bdbh="6";     //被动补货作业
	public static final String g_SqWorkType_Zdbh="7";     //主动补货作业	
	public static final String g_SqWorkType_Rksj="9";     //主动补货作业	
	public static final String g_SqWorkType_Gjtc="3";     //购进退出	
	public static final String g_SqWorkType_Tqjx2="5TQ";  //提前拣选
	
	
	public clsMyPublic()
	{
		
	}
	public static String GetZylbName(String _No)
	{
		String sRlt="";
		if(_No.compareTo(g_SqWorkType_Tqjx)==0)
			sRlt="提前拣选";
		else if (_No.compareTo(g_SqWorkType_Pdzy)==0)
			sRlt="盘点作业";
		else if (_No.compareTo(g_SqWorkType_Lstd)==0)
			sRlt="绿色通道";
		else if (_No.compareTo(g_SqWorkType_Zjjh)==0)
			sRlt="追加拣货";
		else if (_No.compareTo(g_SqWorkType_Ztzy)==0)
			sRlt="自提出库";
		else if (_No.compareTo(g_SqWorkType_Ptzy)==0)
			sRlt="普通出库";
		else if (_No.compareTo(g_SqWorkType_Bdbh)==0)
			sRlt="被动补货";
		else if (_No.compareTo(g_SqWorkType_Gjtc)==0)
			sRlt="购进退出";
		return sRlt;
	}
	public static String GetZylbNo(String _Name)
	{
		String sRlt="";
		if(_Name.compareTo("提前拣选")==0)
			sRlt=g_SqWorkType_Tqjx;
		else if (_Name.compareTo("盘点作业")==0)
			sRlt=g_SqWorkType_Pdzy;
		else if (_Name.compareTo("绿色通道")==0)
			sRlt=g_SqWorkType_Lstd;
		else if (_Name.compareTo("追加拣货")==0)
			sRlt=g_SqWorkType_Zjjh;
		else if (_Name.compareTo("自提出库")==0)
			sRlt=g_SqWorkType_Ztzy;
		else if (_Name.compareTo("普通出库")==0)
			sRlt=g_SqWorkType_Ptzy;
		else if (_Name.compareTo("被动补货")==0)
			sRlt=g_SqWorkType_Bdbh;
		else if (_Name.compareTo("购进退出")==0)
			sRlt=g_SqWorkType_Ptzy;
		return sRlt;
	}
	//返回指定位置的字符串
	public static String GetStrInOfStr(String _str1,int _startIndex)
	{
		String sRet="";
		String stmpStr="";
		int iCurIndex=-1;
		int iCurPos=-1;
		stmpStr=_str1;
	
		while(stmpStr.indexOf(";")>=0)
		{
			iCurPos=stmpStr.indexOf(";");
			iCurIndex++;
			if (iCurIndex==_startIndex)
			{	
				sRet=stmpStr.substring(0,iCurPos);
				break;
			}
			stmpStr=stmpStr.substring(iCurPos+1);
		}
		return sRet;
	}
	
	public static String GetStrOutOfStr(String _str1,int _startIndex)
	{
		String sRet="";
		String stmpStr="";
		int iCurIndex=-1;
		int iCurPos=-1;
		stmpStr=_str1;
	
		while(stmpStr.indexOf(":")>=0)
		{
			iCurPos=stmpStr.indexOf(":");
			iCurIndex++;
			if (iCurIndex==_startIndex)
			{	
				sRet=stmpStr.substring(0,iCurPos);
				break;
			}
			stmpStr=stmpStr.substring(iCurPos+1);
		}
		return sRet;
	}
	public final static int gSendDisMess(String _sData)
	{
		try
		{
			if (m_hdlSendMess==null) return 0;
			m_hdlSendMess.obtainMessage(MsTypeDisMess, 100,-1, _sData).sendToTarget();
			//LogDBHelper.gInsetLogData(LogDBHelper.Logs.LOGS_TABLE_NAME,"UI001","0000",_sData);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public static class Utils {
	    private static long lastClickTime;
	    public synchronized static boolean isFastClick() {
	        long time = System.currentTimeMillis();   
	        if ( time - lastClickTime < 500) {   
	            return true;   
	        }   
	        lastClickTime = time;   
	        return false;   
	    }
	}
	
	public static int dip2px(Context context, float dipValue)
	{
		float m=context.getResources().getDisplayMetrics().density ;
		return (int)(dipValue * m + 0.5f) ;
	}
	
}
