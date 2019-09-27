/*
 * FileName：AccountCtrl.java 
 * <p>
 * Copyright (c) 2017-2020, .
 * <p>
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * www.gnu.org/licenses/gpl-3.0.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.n.online.biz.backup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.n.online.dao.mapper.serverfileMapperEx;
import com.n.online.dao.mapper.serverfile_catagoryconfigMapperEx;
import com.n.online.dao.model.serverfile;
import com.n.online.dao.model.serverfile_catagoryconfig;
import com.n.util.DateUtil;
import com.n.util.WebUtils;
import com.n.util.cString;

/**
 *
 * 
 * @version 2.0
 * @date 2018-04-17 10:54:58
 */
@Controller
public class ServerCtrl  {

	private static Logger logger = LoggerFactory.getLogger("服务器文件下载服务端");

	@Autowired
	private serverfileMapperEx oserverfilemapper;

	@Autowired
	private serverfile_catagoryconfigMapperEx serverfile_catagoryconfigmapper;

	@RequestMapping(value = "/getbackupDownloadList", method = RequestMethod.GET)
	@ResponseBody
	public String getbackupDownloadList(@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "c", required = false) String clientid,
			@RequestParam(value = "catagory", required = true) String catagory,
			@RequestParam(value = "m", required = true) Long locationTimestamp, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		if (session == null || session.isNew() || !"1".equals(request.getSession().getAttribute("logined"))) {
			boolean logined = loginclient(request.getSession(), clientid, key);
			if (!logined)
				return "";
		}

		// 验证成功，接着获取列表

		List<HashMap<String, Object>> l = oserverfilemapper.selectBackupDownloadList(catagory, locationTimestamp);
		// 转为内存中的hashmap存入session

		HashMap<Integer, HashMap<String, Object>> h = new HashMap<Integer, HashMap<String, Object>>();
		for (int i = 0; i < l.size(); i++) {
			HashMap<String, Object> o = l.get(i);

			o.put("uuid", UUID.randomUUID());
			h.put((Integer) o.get("id"), o);
		}

		session.setAttribute("list_fordownload", h);

		return WebUtils.getSuccessJson("获取下载列表", l);

	}

	// getbackupServerfile?key=" + key + "&c=" + clientid + "&uid=" + "&i=";
	@RequestMapping(value = "/getbackupServerfile", method = RequestMethod.GET)
	public ResponseEntity<Resource> getbackupServerfile(@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "c", required = false) String clientid,
			@RequestParam(value = "uid", required = true) String uuid,
			@RequestParam(value = "i", required = true) Integer id, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
	
		if (session == null || session.isNew() || !"1".equals(session.getAttribute("logined"))) {
			boolean logined = loginclient(session, clientid, key);
			if (!logined)
				return null;
		}

		// */ 验证成功，读取文件进行下载
		@SuppressWarnings("unchecked")
		HashMap<Integer, HashMap<String, Object>> h = (HashMap<Integer, HashMap<String, Object>>) session
				.getAttribute("list_fordownload");
		HashMap<String, Object> arecord = h.get(id);
		if (!cString.getNoNullTrimStr(arecord.get("uuid")).equals(uuid))
			return null; // 返回空内容

		// 初始化文件流，提供客户端下载
		HashMap<String, String> hServerfile_catagoryconfig = new HashMap<String, String>();
		List<serverfile_catagoryconfig> l = serverfile_catagoryconfigmapper.selectAll();
		for (int i = 0; i < l.size(); i++) {
			serverfile_catagoryconfig o = l.get(i);
			hServerfile_catagoryconfig.put(o.getFilecatagory(), o.getRootpath());
		}

		String srcFileFullpath = hServerfile_catagoryconfig.get(arecord.get("filecatagory")) + arecord.get("filepath");
		// ***/
		// String srcFileFullpath
		// ="Z:\\Elearning\\master\\课件与文档\\resourcedoc\\certifications\\camp\\2016\\12\\28\\0a58e42e-6f2f-4047-837e-8654976f05a7.jpg";
		File srcfile = new File(srcFileFullpath);// 构造要下载的文件
		if (!srcfile.exists()) {
			serverfile Afindrecord = new serverfile();
			Afindrecord.setId((Integer) arecord.get("id"));
			;
			Afindrecord.setIsdelete(true);
			Long datescan = DateUtil.getNanoTimestamp();
			Afindrecord.setFilemodifydateScan(datescan);
			Afindrecord.setFilemodifydateScanDes(DateUtil.getNanoTimestampDes(datescan));
			oserverfilemapper.updateByPrimaryKeySelective(Afindrecord);
			logger.info("提供下载出错,文件不存在:ID" + id + "路径：" + srcFileFullpath + "客户端ip：" + getIpAddress(request));
			return null;
		}

		try {
			Resource resource = loadFileAsResource(srcFileFullpath);

			// Try to determine file's content type
			String contentType = null;

			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			logger.info("提供下载:ID" + id + "路径：" + srcFileFullpath + "客户端ip：" + getIpAddress(request));
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception ex) {
			if (ex.getMessage().contains("Server File not found ")) {
				serverfile Afindrecord = new serverfile();
				Afindrecord.setId((Integer) arecord.get("id"));
				;
				Afindrecord.setIsdelete(true);
				// Afindrecord.setFilemodifydateScan(new Date()); //java中精确度不够
				oserverfilemapper.updateByPrimaryKeySelective(Afindrecord);

			}
			ex.printStackTrace();
			logger.info(
					"提供下载出错:ID" + id + "路径：" + srcFileFullpath + "客户端ip：" + getIpAddress(request) + ex.getStackTrace());
		}
		return null;

	}

	/**
	 * 加载文件
	 * 
	 * @param fileName 文件名
	 * @return 文件
	 * @throws Exception
	 */
	public Resource loadFileAsResource(String filefullPath) throws Exception {
		try {
			Path filePath = Paths.get(filefullPath).toAbsolutePath().normalize();

			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new Exception("Server File not found " + filefullPath);
			}
		} catch (MalformedURLException ex) {
			throw new Exception("解析文件地址出错" + filefullPath, ex);
		}
	}

	private Properties settings =null;
	private boolean loginclient(HttpSession session, String clientid, String password) throws IOException {
		if(settings ==null) settings =new Client().getproperties();
		String _clientid = settings.getProperty("backup.clientid");
		String _key = settings.getProperty("backup.key");

		boolean ret = clientid.equals(_clientid) && password.equals(_key);
		if (ret)
			session.setAttribute("logined", "1");
		return ret;
	}

	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
