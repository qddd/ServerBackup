package com.n.online.scheduler;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.n.online.biz.backup.ServerScan;

/*
 * 
 CRON表达式    含义 
"0 0 12 * * ?"    每天中午十二点触发 
"0 15 10 ? * *"    每天早上10：15触发 
"0 15 10 * * ?"    每天早上10：15触发 
"0 15 10 * * ? *"    每天早上10：15触发 
"0 15 10 * * ? 2005"    2005年的每天早上10：15触发 
"0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发 
"0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发 
"0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 
"0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发 
"0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发 
"0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发

 * 
 * */
@Controller
@Configuration          //证明这个类是一个配置文件
@EnableScheduling       //打开quartz定时器总开关
public class biztask {
	private static Logger logger = LoggerFactory.getLogger("业务处理任务biztask");
	 
	@Autowired
	public ServerScan scanserver;


 
	@Scheduled(cron = "0 35 7 ? * *" )//   每天早上4占触发
	public String Main() throws Exception
	{
		 	
		logger.info("扫描备份文件---开始");
		scanserver.scanServerFile(false);
		logger.info("扫描备份文件---结束");
		return "biztask 定时任务开始";
	}
	
	@RequestMapping(value = "/scanserverfile", method = RequestMethod.GET)
	public String scanserverfile(HttpServletRequest request) throws Exception
	{	
		scanserver.scanServerFile(request.getParameter("isdebug")!=null);
		return "扫描服务器文件";
	}
	
	 

}
