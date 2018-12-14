package wcs.cps;




import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;
import android.database.Cursor;

public class LogDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jzt_sqlite_database.db";
    private static final int DATABASE_VERSION = 1;
	private static SQLiteDatabase mDB = null;
    
	public static final class Logs implements BaseColumns {

		private Logs()
		{			
		}
		public static final String LOGS_TABLE_NAME = "tbl_errlogs";
		public static final String LOGS_D = "date";           //日期时间
		public static final String LOGS_FW = "funwindow";     //界面
		public static final String LOGS_OPER = "operator";    //操作员
		public static final String LOGS_EI = "errorinfo";     //错误信息 
		public static final String DEFAULT_SORT_ORDER = "data ASC";
	}
    
	public static final class clsLogData
	{
		public  clsLogData()
		{			
		}
		String _LogFw;   //界面
		String _LogOper; //操作员 
		String _LogEi;   //错误信息
	}
	
    public LogDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }	
	public void gCreateTable() 
	{
		mDB =getWritableDatabase();
		if(!tabIsExist(Logs.LOGS_TABLE_NAME)) 
		{
			//创建日志表
			mDB.execSQL("CREATE TABLE " + Logs.LOGS_TABLE_NAME+ " ("
						+ Logs._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
		                + Logs.LOGS_D + " TEXT,"
		                + Logs.LOGS_FW + " TEXT,"
		                + Logs.LOGS_OPER + " TEXT,"
		                + Logs.LOGS_EI +  " TEXT"
		                + ");");
		}
		if(!tabIsExist("SYS_CS")) 
		{
			//创建日志表
			mDB.execSQL("CREATE TABLE " + " SYS_CS "+ " ("
						+ Logs._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
		                + "COL_NAME VARCHAR(30)," 
		                + "COL01 VARCHAR(300)," 
		                + "COL02 VARCHAR(300));");
		}
	}
	
	public static void gUpdateColValue(int _Type,String _COL_NAME,String _COL_Value)
	{
		String _SQL="";
		try
		{
			if (_Type==0)
			{
				//新关联周转箱
				_SQL=" Delete from SYS_CS Where COL_NAME='" + _COL_NAME +"'";
				mDB.execSQL(_SQL);
				
				_SQL=" Insert Into SYS_CS (COL_NAME,COL01) VALUES('" + _COL_NAME +"','" + _COL_Value +"')";
				mDB.execSQL(_SQL);
			}
		}
		catch (Exception e) 
        {
			Log.v("zms", e.toString());
        }         
	}
	public static String gGetColValue(String _COL_NAME)
	{
		String sRet="";
		String _SQL="";
		try
		{
			
	        Cursor cursor = null;
			_SQL=" select COL01 from SYS_CS Where COL_NAME='" + _COL_NAME +"'";
			cursor = mDB.rawQuery(_SQL, null);
	        if(cursor.moveToNext())
	        {
	          //  int count = cursor.getInt(0);
	           // if(count>0)
	            	sRet=cursor.getString(0);
	        }           
		}
		catch (Exception e) 
        {
			Log.v("zms", e.toString());
        }         
		return sRet;
	}
	
	public boolean tabIsExist(String tabName)
	{
        boolean result = false;
        Cursor cursor = null;
        try
        {               
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
            cursor = mDB.rawQuery(sql, null);
            if(cursor.moveToNext())
            {
                int count = cursor.getInt(0);
                if(count>0)
                	result = true;
            }               
        } 
        catch (Exception e) 
        {
        }                
        return result;
    }
	public static Cursor gRetrunCursor(String _tblName)
	{
		Cursor rCurSor=null;
		try
		{		
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(_tblName);
			String sql = " select * from " +_tblName;
			rCurSor = mDB.rawQuery(sql, null);
		}
        catch (Exception e) 
        {
        }  
		return rCurSor;
	}
	public static void gDeleteLogData()
	{
		String _SQL="";
		_SQL="delete from " + Logs.LOGS_TABLE_NAME;
		mDB.execSQL(_SQL);
	}
	public static int gInsetLogData(String _tblName,String _Fw,String _WrkNo,String _Info)
	{
		try
		{	
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(_tblName);
			ContentValues LogRecordToAdd = new ContentValues();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());
			String str = formatter.format(curDate);
			LogRecordToAdd.put(Logs.LOGS_D, str);
			LogRecordToAdd.put(Logs.LOGS_FW,_Fw);
			LogRecordToAdd.put(Logs.LOGS_OPER,_WrkNo);
			LogRecordToAdd.put(Logs.LOGS_EI,_Info);
	        mDB.beginTransaction();
	        String _SQL=" Delete from tbl_errlogs Where date not like  '%" + str.substring(0,10) +"%'";
			mDB.execSQL(_SQL);
			mDB.insert(Logs.LOGS_TABLE_NAME,Logs.LOGS_D,LogRecordToAdd);
			mDB.setTransactionSuccessful();
			mDB.endTransaction();
			return 0;
		}
        catch (Exception e) 
        {
        	return -1;
        }
		catch(UnknownError e)
		{
			return -1;
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Housekeeping here. Implement how "move" your application data during an upgrade of schema versions		
	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

}
