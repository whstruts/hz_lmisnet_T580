package wcs.cps.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import wcs.cps.clsMyPublic;
import android.R.string;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;



public final class HttpTool 
{
	public static String m_SvrIp="10.66.21.20";
	public static String m_SvrPort="8000";
	public static Handler m_CurHandler; 
	public static final String  m_sktNetPath="/api/WebServiceEx/WebSerCall";    //交替显示颜色
	public static final String  m_sktResult ="WebSerCallResult";
	public String m_urlPath="http://10.66.21.20:80/WebSer/WebSerCallEx";
	public static String m_surldata="";
	
	public  Map<String, String> m_params = new HashMap<String, String>(); 
		  
	public static String sendGetRequest(String path,   
		       Map<String, String> params, String enc) throws Exception{  
		 
		try
		{
		   String json="";
		   StringBuilder sb = new StringBuilder(path);  
		   sb.append('?');  
		   //?method=save&title=12345678&timelength=26&  
		   //迭代Map拼接请求参数  
		   for(Map.Entry<String, String> entry : params.entrySet()){  
		       sb.append(entry.getKey()).append('=')  
		            .append(URLEncoder.encode(entry.getValue(), enc)).append('&');  
		    }  
		    sb.deleteCharAt(sb.length()-1);//删除最后一个"&"  
		    
		    //Log.v("zms", sb.toString());
		    URL url = new URL(sb.toString());  
		    m_surldata=url.toString();
		    HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		    conn.setRequestMethod("GET");  
		  
		    conn.setConnectTimeout(5 * 1000);  
		    int a=  conn.getResponseCode();
		    if(a==200)
		    {  
		    	return changeInputStream(conn.getInputStream());  
		    }  
		    return json;  
		    }
		catch (Exception e) {  
            throw e;
        }  
	}  
	
	 /**  
     * 将一个输入流转换成指定编码的字符串    
     * @param inputStream  
     * @return  
     */  
    private static String changeInputStream(InputStream inputStream) {  
        String jsonString = "";  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        int len = 0;  
        byte[] data = new byte[2048];  
        try 
        {  
        	
            while((len=inputStream.read(data))!=-1){  

                outputStream.write(data,0,len);  
            }  

            jsonString = new String(outputStream.toByteArray());  
        
            return jsonString ;

        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return jsonString;  
    }  

		  
		public static boolean sendPostRequest(String path,   
		        Map<String, String> params, String enc) throws Exception{  
		      
		    // title=dsfdsf&timelength=23&method=save  
		    StringBuilder sb = new StringBuilder();  
		    if(params!=null && !params.isEmpty()){  
		        //迭代Map拼接请求参数  
		        for(Map.Entry<String, String> entry : params.entrySet()){  
		            sb.append(entry.getKey()).append('=')  
		                .append(URLEncoder.encode(entry.getValue(), enc)).append('&');  
		        }  
		        sb.deleteCharAt(sb.length()-1);//删除最后一个"&"  
		    }  
		    byte[] entitydata = sb.toString().getBytes();//得到实体的二进制数据  
		    URL url = new URL(path);  
		    HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		    conn.setRequestMethod("POST");  
		    conn.setConnectTimeout(5 * 1000);  
		    //如果通过post提交数据，必须设置允许对外输出数据  
		    conn.setDoOutput(true);  
		    //此两参数必须设置  
		    //Content-Type: application/x-www-form-urlencoded  
		    //Content-Length: 38  
		    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
		    conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length));  
		    OutputStream outStream = conn.getOutputStream();  
		    outStream.write(entitydata);  
		    outStream.flush();  
		    outStream.close();  
		    if(conn.getResponseCode()==200){  
		        return true;  
		    }  
		    return false;  
		} 
		
		
		public class CnntThread extends Thread
	    {
	    	public void run()
	    	{
	    	    String sMess="";
	    		Date curDate_start = new Date(System.currentTimeMillis());
	    		try 
	    		{
					String json = sendGetRequest(m_urlPath, m_params, "UTF-8");
					Date curDate_end = new Date(System.currentTimeMillis());
					
					long _diff=curDate_end.getTime()-curDate_start.getTime();
					sMess="usetime:"+String.valueOf(_diff)+";";//+m_surldata;
					if (m_CurHandler!=null)
						m_CurHandler.obtainMessage(clsMyPublic.MsTypeDisMess, 100,-1, sMess).sendToTarget();
					if(json.length()>0)
					{
						if (m_CurHandler!=null)
							m_CurHandler.obtainMessage(clsMyPublic.MsTypeReceMess, 100,-1, json).sendToTarget();

					}
				} 
	    		catch (Exception e) 
				{
	    			sMess=e.getMessage().toString();
	    			m_CurHandler.obtainMessage(clsMyPublic.MsTypeDisMess, 100,-1, sMess).sendToTarget();
				}  
	    	}
	    }
		
		public void mStart()
		{		
			CnntThread myCnntThread=new CnntThread();
			myCnntThread.start();
		}

}
