package com.n.online.biz.backup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.n.base.Base;
import com.n.util.DateUtil;
import com.n.util.cFile;
import com.n.util.cString;
import com.n.util.downloadutil;
import com.n.util.fileHashUtil;

import net.sf.json.JSONObject;

public class Client extends Base implements Observer {
	public Client() {
		super();
	}

	String sessionid = null;
	List<HashMap<String, Object>> lstToDownload = new ArrayList<HashMap<String, Object>>();;
	HashMap<String, HashMap<String, Object>> hToDownload = new HashMap<String, HashMap<String, Object>>();;

	public static void main(String[] args) throws Exception {
	 

		// 获取用户帐号
		Client client = new Client();
		client.start();
	}

	private void start() {
		while (true) {
			try {
				// 读取配置
				Properties settings = getproperties();

				String clientid = settings.getProperty("backup.clientid");
				String key = settings.getProperty("backup.key");
				String[] catagorys = settings.getProperty("backup.catagory").split(",");
				String localRootDir = settings.getProperty("backup.localRootDir");

				// 逐个类别处理，每类每次个文 件下载
				for (int i = 0; i < catagorys.length; i++) {
					// 设置请求参数
					String catagory = catagorys[i];
					String marker_locationTimestamp_lastdownload = settings.getProperty("backup.locationTimestamp." + catagory);
					if (cString.isnovalue(marker_locationTimestamp_lastdownload))
						marker_locationTimestamp_lastdownload = "0";

					String domainorip = settings.getProperty("backup.server.domainorip");
					String src_url_getlist = "http://" + domainorip + "/getbackupDownloadList?key=" + key + "&c=" + clientid + "&catagory=" + catagory + "&m=" + marker_locationTimestamp_lastdownload;

					// 开始处理, 获取下载的文件列表100个,获取请求的地址：
					// 1.URL类封装了大量复杂的实现细节，这里将一个字符串构造成一个URL对象
					logger.info("从服务器获取增量列表url" + src_url_getlist);
					URL url_getlist = new URL(src_url_getlist);
					// 2.获取HttpURRLConnection对象
					HttpURLConnection connection = (HttpURLConnection) url_getlist.openConnection();
					if (!cString.isnovalue(sessionid)) {
						connection.setRequestProperty("Cookie", sessionid);
					}

					// 3.调用connect方法连接远程资源
					connection.connect();

					// 4.访问资源数据，使用getInputStream方法获取一个输入流用以读取信息
					BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

					// 对数据进行访问
					String line = null;
					StringBuilder stringBuilder = new StringBuilder();
					while ((line = bReader.readLine()) != null) {
						stringBuilder.append(line);
					}

					// 第一次运行的时候，记录下来session的值
					String cookieVal = connection.getHeaderField("Set-Cookie");
					if (cookieVal != null) {
						sessionid = cookieVal.substring(0, cookieVal.indexOf(";"));// .replace("SESSION=", "");
					}

					// 关闭流
					bReader.close();
					connection.disconnect();
					logger.info("从服务器获取增量列表，返回：" + stringBuilder.toString());

					// 将获得的String对象转为JSON格式
					ObjectMapper mapper = new ObjectMapper();
					mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS"));
					// 解析器支持解析单引号
					mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
					// 解析器支持解析结束符
					mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

					JSONObject ojson = JSONObject.fromObject(stringBuilder.toString());
					if (!ojson.get("success").equals(1)) {
						logger.info("从服务器获取增量列表失败，具体信息见上一条日志");
						return;
					}

					String datastr = ojson.get("data").toString();

					lstToDownload = mapper.readValue(datastr, new TypeReference<List<HashMap<String, Object>>>() {
					});
					hToDownload.clear();

					// 通过利用JSON键值对的key，来查询value
					outterLoop: for (int j = 0; j < lstToDownload.size(); j++) {
						HashMap<String, Object> ADownloadrecord = lstToDownload.get(j);
						hToDownload.put(ADownloadrecord.get("id").toString(), ADownloadrecord);
						logger.info("第"+j+"个处理中........文件ID" + ADownloadrecord.get("id") + ";名称:" + ADownloadrecord.get("filepath") + ";   文件修改时间" + ADownloadrecord.get("filemodifydate_scan"));
						String dest_filepath = localRootDir + "/" + catagory + "/" + ADownloadrecord.get("filepath");
						dest_filepath = dest_filepath.replace("\\/", "/");
						// 判断文件的增删改
						boolean isdelete = (boolean) ADownloadrecord.get("isdelete");
						String type = (String) ADownloadrecord.get("type");
						if (isdelete) {
							File file = new File(dest_filepath);
							// 先备份再删除
							if (file.exists())
								file.renameTo(new File(dest_filepath + ".delete." + DateUtil.getTimeStamp() + ".bak"));
							boolean bdeleted = file.delete();
							
							updateClientLocaltionMark(ADownloadrecord);
							logger.info("........删除文件：" + (bdeleted ? "成功" : "失败") + dest_filepath);						 
							 
						} else if ("folder".equals(type)) // 创建文件夹
						{
							File file = new File(dest_filepath);
							if (!file.exists()) {
								file.mkdirs();
							}
							updateClientLocaltionMark(ADownloadrecord);
							logger.info("........创建文件夹：" + dest_filepath);
							
						} else {// 下载文件
							String dest_filedir = cFile.getFileDirectory(dest_filepath);
							File file = new File(dest_filedir);
							if (!file.exists()) {
								file.mkdirs();
							}

							// ???需处理服务器端文件不存在的问题，如获取请求后刚刚被服务器删 除
							// 先备份再下载
							file = new File(dest_filepath);
							if (file.exists()) {
								// 获取远端文件hash与本文件哈希比较
								String hash_local = fileHashUtil.getFileMD5(file);
								String hash_remote = (String) ADownloadrecord.get("filehashormd5");
								if (hash_local.equals(hash_remote)) {
									logger.info("........文件Hash或MD5相同：" + dest_filepath);
									updateClientLocaltionMark(ADownloadrecord);
									continue;
								} else {
									file.renameTo(new File(dest_filepath + ".update." + DateUtil.getTimeStamp() + ".bak"));
								}
							}

							iLastDownloadStatus = downloadutil.DOWNLOADING;
							String src_url_download = "http://" + domainorip + "/getbackupServerfile?key=" + key + "&c=" + clientid + "&uid=" + ADownloadrecord.get("uuid") + "&i=" + ADownloadrecord.get("id"); // uid为uuid i为id
							logger.info("........下载文件——地址" + src_url_download);
							downloadutil odownloadutil = new downloadutil(src_url_download, dest_filepath, sessionid, ADownloadrecord.get("id").toString(), (String) (ADownloadrecord.get("filemodifydate_self")));
							odownloadutil.addObserver(this);
							odownloadutil.download();
							// 此处需处理下载完继续

							Thread.sleep(500);

							// 遇到错误处理下一分类
							while (iLastDownloadStatus != downloadutil.COMPLETE) {
								Thread.sleep(1000);
								if (iLastDownloadStatus == downloadutil.ERROR || iLastDownloadStatus == downloadutil.CANCELLED) {
									break outterLoop;
								}
							}

						}
					}
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("外层函数错误：" + e.getStackTrace());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	private String propertyfileName = "system.properties";

	public Properties getproperties() throws IOException {
		String filePath = Client.class.getClassLoader().getResource(propertyfileName).getFile(); // 文件的路径
		Properties props = new Properties();
		BufferedReader br = null;

		// 从输入流中读取属性列表（键和元素对）
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		props.load(br);
		br.close();

		return props;

	}

	/**
	 * 传递键值对的Map，更新properties文件
	 * 
	 * @param fileName    文件名(放在resource源包目录下)，需要后缀
	 * @param keyValueMap 键值对Map
	 */
	public void updateProperties(String[] key, String value[]) {
		// InputStream inputStream =
		// Client.class.getClassLoader().getResourceAsStream(fileName); //输入流
		String filePath = Client.class.getClassLoader().getResource(propertyfileName).getFile(); // 文件的路径
		logger.info("........更新属性文件propertiesPath:" + filePath + "键值" + key + "-" + value);
		Properties props = new Properties();
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			// 从输入流中读取属性列表（键和元素对）
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			props.load(br);
			br.close();

			// 写入属性文件
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
			for (int i = 0; i < key.length; i++) {
				props.setProperty(key[i], value[i]);
			}

			props.store(bw, "上次下载的服务器端的位置");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int iLastDownloadStatus = downloadutil.DOWNLOADING; // 上一次下载是否完成？

	@Override
	public void update(Observable o, Object arg) {
		downloadutil odownloadutil = (downloadutil) o;
		iLastDownloadStatus = odownloadutil.getStatus();
		if (odownloadutil.getStatus() == 2) {
			HashMap<String, Object> ADownloadrecord = hToDownload.get(odownloadutil.getFile_Id_onServer());
			updateClientLocaltionMark(ADownloadrecord);
		}
		; // 更新标记
		logger.info("........Data has changed to " + odownloadutil.getStatus());

	}

	private void updateClientLocaltionMark(HashMap<String, Object> ADownloadrecord)
	{
		String[] key = { "backup.locationTimestamp." + ADownloadrecord.get("filecatagory"), "backup.locationTimestamp." + ADownloadrecord.get("filecatagory") + ".des", "backup.locationTimestamp."+ADownloadrecord.get("filecatagory")+".id_filepath" };
		String[] value = { ADownloadrecord.get("filemodifydate_scan").toString(), (String) ADownloadrecord.get("filemodifydate_scan_des"), (String) ADownloadrecord.get("filepath") };
		updateProperties(key, value);
	}
	}
