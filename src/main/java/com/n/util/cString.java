package com.n.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author DaiD
 *
 */
public class cString {


 
	
	public static String getNoNullTrimStr(Object str)
	{
		return (str==null)?"":str.toString().trim();
		
	}
	
 
	
 
	public static String getDateStr(Object str)
	{	
		String str1=(String)str;
		if(isnovalue(str1)) return "";
		return str1.split(" ",15)[0];
	}
	
 
	public static String getDateStr_parse(Object str) throws ParseException
	{	 
		if(cString.isnovalue(str)) return "";
		    String pattern = "yyyy-MM-dd";//.replaceAll("mm", "MM");
		    SimpleDateFormat f = new SimpleDateFormat(pattern);
		    return f.format(f.parse(getNoNullTrimStr(str)));
	}
	
	/**
	* @函数名: getDateStr
	* @功能描述: 获取字符串的日期及时 分部分  
 
	*/ 
	
	public static String getDateTimeStr(Object str)
	{	
		String str1=(String)str;
		if(isnovalue(str1)) return "";
		String[] sp=str1.split(":",30);
		return sp[0]+":"+ sp[1];
	}


	/**
	* @函数名: getDateStr
	* @功能描述: 获取字符串的日期及时 分部分 
	 
	* @版本号： V1.00
	*/ 
	
	public static String getTimeStr(Object str)
	{	
		String str1=(String)str;
		if(isnovalue(str1)) return "";
		String[] sp=str1.split(" ",30);
		return sp[1].substring(0,5);
	}
	
	/**
	* @函数名: isnovalue
	* @功能描述: 判断是否为空
 
	*/ 
	
	public static boolean isnovalue(Object object)
	{	
		if(object==null) return true;
		if (object instanceof String){
			String sobject = (String) object;
			return sobject.isEmpty() ||sobject.equals("");
		};
		
		return false;
	}

	
	/*
	 * Java文件操作 获取文件扩展名

	 */
	    public static String getExtensionName(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length() - 1))) { 
	                return filename.substring(dot + 1); 
	            } 
	        }
	        return filename.replace("\r\n", ""); 
	    } 
	/*
	 * Java文件操作 获取不带扩展名的文件名

	 */
	    public static String getFileNameNoEx(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length()))) { 
	                return filename.substring(0, dot); 
	            } 
	        } 
	        return filename; 
	    } 
	    
	    public static boolean isvalidemail(String semail)
	    {
	    	Pattern p = Pattern.compile("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$");
	    	  Matcher m = p.matcher(semail);
	    	 
	    	  boolean b = m.matches();
	    	   return b;
	    }
	   
	  
		/**
		 *  //判断是滞正确手机号
		 * @param telNum
		 * @return
		 */
		public static boolean isMobiPhoneNum(String telNum){
			if(StringUtils.isBlank(telNum)) return false;
			String regex = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
	        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
	        Matcher m = p.matcher(telNum);
	        boolean ret= m.matches();
	        return ret;
		}
 
	    
	    //判断是否为整数字
	    public static boolean isNumeric(String str){ 
	    	  if(str==null) return false;
	    	   Pattern pattern = Pattern.compile("[0-9]*"); 
	    	   Matcher isNum = pattern.matcher(str);
	    	   if( !isNum.matches() ){
	    	       return false; 
	    	   } 
	    	   return true; 
	    	}
	    
	    
	   /**
	* @函数名: getRadomNumber
	* @功能描述:  生成随机数

	*/ 
	
	public static String getRadomNumber(int count)
	   {
		   char[] codeSequence={'0','1', '2', '3', '4', '5', '6', '7', '8', '9' };
				  
				    //生成随机类   
				    Random random = new Random();   
				    // 取随机产生的认证码(4位数字)   
				    String sRand = "";   
				    for (int i = 0; i < count; i++) {
				        int r = random.nextInt(10);
				        String rand = String.valueOf(codeSequence[r]);
				        sRand += rand;   
				    }   
		   return sRand;
	   }
	
	
	   /**
	* @函数名: getRadomCode
	* @功能描述:  生成随机数
	 
	*/ 
	
	public static String getRadomCode(int count)
	   {
		/*
		   char[] codeSequence={'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
				   'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
				   'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				   'k',  'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				   'x', 'y', 'z','1', '2', '3', '4', '5', '6', '7', '8', '9' };
				   */
		
		   char[] codeSequence={'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				   'k',  'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				   'x', 'y', 'z','1', '2', '3', '4', '5', '6', '7', '8', '9' };
				    //生成随机类   
				    Random random = new Random();   
				    // 取随机产生的认证码(X位数字)   
				    String sRand = "";   
				    for (int i = 0; i < count; i++) {
				        int r = random.nextInt(codeSequence.length);
				        String rand = String.valueOf(codeSequence[r]);
				        sRand += rand;   
				    }   
		   return sRand;
	   }
	    
 
	        
	 
	    
	 
	    
	    //判断数组包含某个字符
	    public boolean Contains(String[] cs,String c)
	    {
	    for(String s: cs){
	    	if(s.equals(c))
	    	return true;
	    	}
	    	return false;
	    	}
}
