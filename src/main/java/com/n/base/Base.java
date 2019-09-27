package com.n.base;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 

public class Base {
	protected static Logger logger = LoggerFactory.getLogger("运行日志");
	protected static Logger logger_err = LoggerFactory.getLogger("错误日志");

	
	protected static void log_err(String loggername,Exception e,String errdes)
	{
		Logger ologger=(StringUtils.isEmpty(loggername))?logger: LoggerFactory.getLogger(loggername);
		 StringWriter out=new StringWriter();  
		 e.printStackTrace(new PrintWriter(out));  
		 ologger.error(errdes);
         ologger.error(out.toString( ));  
	}

	public Base() {
		super();
		
	}
	
	
	
}
