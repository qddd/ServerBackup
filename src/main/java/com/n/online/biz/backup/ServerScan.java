package com.n.online.biz.backup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.n.base.BaseService;
import com.n.online.dao.mapper.serverfileMapperEx;
import com.n.online.dao.mapper.serverfile_catagoryconfigMapperEx;
import com.n.online.dao.model.serverfile;
import com.n.online.dao.model.serverfile_catagoryconfig;
import com.n.util.DateUtil;
import com.n.util.fileHashUtil;

@Service
public class ServerScan extends BaseService {

	@Autowired
	private serverfileMapperEx oserverfilemapper;

	@Autowired
	private serverfile_catagoryconfigMapperEx serverfile_catagoryconfigmapper;

	private boolean isdebug = false;
	HashMap<String, String> hServerfile_catagoryconfig;
	private static Logger logger = LoggerFactory.getLogger("服务器文件备份扫描");

	public void scanServerFile(boolean _isdebug) throws Exception {
		isdebug = _isdebug;

		long timestart = System.currentTimeMillis();

		// 读取配置
		readfilecatagoryconfig();

		// 扫描文件
		logger.info("扫描比对文件开始------------------------------");

		Iterator<Entry<String, String>> iter = hServerfile_catagoryconfig.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
			String catagory = entry.getKey();
			String RootDir = entry.getValue();
			scanAPath(RootDir, catagory);
		}
		long timeend = System.currentTimeMillis();
		float timeused = (timeend - timestart) * 1.0f / 1000 / 60;
		logger.info("扫描比对文件结束-成功,耗时" + timeused + "分钟****************************");
		

		// 清理不存在文件相应的数据库记录，写入删 除标记
		logger.info("置删除文件标识开始------------------------------");
		Iterator<Entry<String, String>> iter1 = hServerfile_catagoryconfig.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter1.next();
			String catagory = entry.getKey();
			deleteNoexistRecord(0, catagory);
		}
		
		
		long timeend1 = System.currentTimeMillis();
		timeused = (timeend1 - timeend) * 1.0f / 1000 / 60;
		logger.info("置删除文件标识结束-成功,耗时" + timeused + "分钟****************************");

	}

	private void readfilecatagoryconfig() {
		hServerfile_catagoryconfig = new HashMap<String, String>();
		List<serverfile_catagoryconfig> l = serverfile_catagoryconfigmapper.selectAll();
		for (int i = 0; i < l.size(); i++) {
			serverfile_catagoryconfig o = l.get(i);
			hServerfile_catagoryconfig.put(o.getFilecatagory(), o.getRootpath());
		}
	}

	// 对已删除不存在的文件设置删除标记
	private void deleteNoexistRecord(int beginId,String catagory) {
		int span = 100; // 每次读100条记录
		List<serverfile> lserverfiles = oserverfilemapper.selectFileRecordsToDelete(beginId, span,catagory);

		String fileroot;
		String filepath;

		for (int i = 0; i < lserverfiles.size(); i++) {
			serverfile Aserverfile = lserverfiles.get(i);
			fileroot = hServerfile_catagoryconfig.get(Aserverfile.getFilecatagory());
		    if(isdebug)	logger.info("filecatagory: "+Aserverfile.getFilecatagory()+"\nhServerfile_catagoryconfig: "+hServerfile_catagoryconfig.toString()+"\nfileroot: "+fileroot);
			filepath = fileroot + Aserverfile.getFilepath();
			File f = new File(filepath);

			// 置删除标记
			if (!f.exists()) {
				Aserverfile.setIsdelete(true);
				Aserverfile.setFilesizeSelf(0L); 
				Long datescan=DateUtil.getNanoTimestamp();
				Aserverfile.setFilemodifydateScan(datescan);
				Aserverfile.setFilemodifydateScanDes(DateUtil.getNanoTimestampDes(datescan));
				oserverfilemapper.updateByPrimaryKey(Aserverfile);
			    logger.info("deleteNoexistRecord阶段：设置删除标记成功" + Aserverfile.getId()+":"+Aserverfile.getFilepath()+"全路径"+filepath);
			}
			beginId = Aserverfile.getId() + 1;
		}

		if (lserverfiles != null && lserverfiles.size() > 0) {
			deleteNoexistRecord(beginId,catagory); // 下一次轮循
		}
	}

	private void scanAPath(String ASubDir_Full, String catagory) throws Exception {
		// 扫描文件夹

		if (ASubDir_Full.endsWith(".svn"))
			return; // 不处理.svn文 件夹

		File dir = new File(ASubDir_Full);
		File[] fileList = dir.listFiles();
	
		// 获取所在文件夹相对路径的哈希
		String dirrelpath_tmp = getfilerelpath(ASubDir_Full, hServerfile_catagoryconfig.get(catagory));
		long dirpathhashvalue = (int) dirrelpath_tmp.hashCode();
		List<serverfile> lfilerecord = oserverfilemapper.selectFileRecordsByDirHash(dirpathhashvalue, catagory,
				dirrelpath_tmp);

		if (isdebug)
			logger.info("*********处理目录***********" + ASubDir_Full);
		
		
		//先处理文件
		String filerelpath_tmp;
		String[] strFilePaths = new String[fileList.length];
		int i_tmp = 0;
		for (File f : fileList) {
			    String type=f.isDirectory()?"folder":"file";	
			    String filehash="file".equals(type)?fileHashUtil.getFileMD5(f):"";
				// 根据哈希值(文件路径的哈希值)查数据库
			    if (isdebug)
			    logger.info("找到文件/夹-"+f.getPath()+"； type="+type+"catagory"+catagory+"---"+hServerfile_catagoryconfig.toString());
				filerelpath_tmp = getfilerelpath(f.getPath(), hServerfile_catagoryconfig.get(catagory));
				strFilePaths[i_tmp] = filerelpath_tmp;
				i_tmp++;
			
				// 总记录为空，进行新增
				if (lfilerecord == null || lfilerecord.isEmpty()) {
					serverfile newfile = new serverfile();
					newfile.setFilecatagory(catagory);
					newfile.setFiledir(dirrelpath_tmp);
					newfile.setFiledirhash(dirpathhashvalue);
					newfile.setFilemodifydateSelf(getdate(f.lastModified()));
					newfile.setFilepath(filerelpath_tmp);
				 	newfile.setFilesizeSelf(f.length());
				 	Long datescan=DateUtil.getNanoTimestamp();
				 	newfile.setFilemodifydateScan(datescan);
				 	newfile.setFilemodifydateScanDes(DateUtil.getNanoTimestampDes(datescan));
					//newfile.setFilemodifydateScan(new Date()); java中精确度不够
					newfile.setType(type);
					newfile.setFilehashormd5(filehash);
					newfile.setIsdelete(false);
					logger.info("...新增成功----" + f.getPath());
					oserverfilemapper.insert(newfile);
					continue;
				}

				// 总记录不为空
				boolean bfind = false;
				for (int i = lfilerecord.size() - 1; i >= 0; i--) {
					serverfile Afindrecord = lfilerecord.get(i);
					// 同一文件，但修改过（根据文件大小，文件判断是否修改过）
					boolean bsamefile = Afindrecord.getFilepath().equals(filerelpath_tmp) && type.equals("file");
					boolean bsamefolder = Afindrecord.getFilepath().equals(filerelpath_tmp) && type.equals("folder");

					if (bsamefile && (Afindrecord.getFilesizeSelf() != f.length()
							|| !Afindrecord.getFilemodifydateSelf().equals(getdate(f.lastModified())) || !filehash.equals(Afindrecord.getFilehashormd5()))) {
						Afindrecord.setFilecatagory(catagory);
						Afindrecord.setFiledir(dirrelpath_tmp);
						Afindrecord.setFiledirhash(dirpathhashvalue);
						Afindrecord.setFilemodifydateSelf(getdate(f.lastModified()));
						Afindrecord.setFilepath(filerelpath_tmp);
					 	Afindrecord.setFilesizeSelf(f.length());
					 	Long datescan=DateUtil.getNanoTimestamp();
					 	Afindrecord.setFilemodifydateScan(datescan);
					 	Afindrecord.setFilemodifydateScanDes(DateUtil.getNanoTimestampDes(datescan));
					
					 	Afindrecord.setType(type);
						Afindrecord.setIsdelete(false);
						Afindrecord.setFilehashormd5(filehash);
						//Afindrecord.setFilemodifydateScan(new Date()); //java中精确度不够
						oserverfilemapper.updateByPrimaryKeySelective(Afindrecord);
					    logger.info("...修改成功----" + f.getPath()+"修改原因"+bsamefile +"Afindrecord.getFilesizeSelf() != f.length()"+Afindrecord.getFilesizeSelf() +"-"+ f.length()
									+"!Afindrecord.getFilemodifydateSelf().equals(getdate(f.lastModified())))"+ Afindrecord.getFilemodifydateSelf()+"-"+getdate(f.lastModified())+"!filehash.equals(Afindrecord.getFilehashormd5()))"+  filehash+"-"+Afindrecord.getFilehashormd5());
						lfilerecord.remove(i);
						bfind = true;
						break;
					} else if (bsamefile && Afindrecord.getFilesizeSelf() == f.length()
							&& Afindrecord.getFilemodifydateSelf().equals(getdate(f.lastModified())) && filehash.equals(Afindrecord.getFilehashormd5())) {
						lfilerecord.remove(i);
						if (isdebug) logger.info("...文件没有变化，成功：----" + f.getPath());
						bfind = true;
						break;
					} else if (bsamefolder)
					{
						lfilerecord.remove(i);
						if (isdebug) logger.info("...文件夹没有变化，成功：----" + f.getPath());
						bfind = true;
						break;
					}
				}

				// 没有找到进行新增
				if (!bfind) {
					serverfile newfile = new serverfile();
					newfile.setFilecatagory(catagory);
					newfile.setFiledir(dirrelpath_tmp);
					newfile.setFiledirhash(dirpathhashvalue);
					newfile.setFilemodifydateSelf(getdate(f.lastModified()));
					newfile.setFilepath(filerelpath_tmp);
				 	newfile.setFilesizeSelf(f.length());
				 	Long datescan=DateUtil.getNanoTimestamp();
				 	newfile.setFilemodifydateScan(datescan);
				 	newfile.setFilemodifydateScanDes(DateUtil.getNanoTimestampDes(datescan));
					//newfile.setFilemodifydateScan(new Date());  java中精确度不够
					newfile.setType(type);
					newfile.setFilehashormd5(filehash);
					newfile.setIsdelete(false);
					oserverfilemapper.insert(newfile);
					logger.info("...新增成功：----" + f.getPath());
				}		  
		}

		/*********************
		 * 处理lfilerecord中的文件重复的记录，或已删除文件的老记录
		 ***********************/
		// 获取当前文件夹下所有文件路径列表
		if (lfilerecord.size() > 0) {
			for (int i = lfilerecord.size() - 1; i >= 0; i--) {
				serverfile Afindrecord = lfilerecord.get(i);
				if (!contains(strFilePaths, Afindrecord.getFilepath())) {
					// 置删除标志
					Afindrecord.setIsdelete(true);
					Afindrecord.setFilesizeSelf(0L);   //置0，如文件删后又重新恢复后能被扫描到
					Long datescan=DateUtil.getNanoTimestamp();
					Afindrecord.setFilemodifydateScan(datescan);
					Afindrecord.setFilemodifydateScanDes(DateUtil.getNanoTimestampDes(datescan));
					oserverfilemapper.updateByPrimaryKeySelective(Afindrecord);
					logger.info("设置删除标记，成功：----" + Afindrecord.getId() + ":" + Afindrecord.getFilepath()+"路径列表："+strFilePaths);
				} else {
					// 删除多条记录中重复数据
					oserverfilemapper.deleteByPrimaryKey(Afindrecord.getId()); // 真删除
					logger.info("删除重复记录，成功：----" + Afindrecord.getId() + ":" + Afindrecord.getFilepath()+"路径列表："+strFilePaths);
				}
			}
		}
		
		//再处理目录
		for (File f : fileList) {
	      if (f.isDirectory()) {
				scanAPath(f.getAbsolutePath(), catagory);
			}
		}

	}

	private boolean contains(String[] parent, String childstr) {

		for (String s : parent) {
			if (s.equals(childstr)) {
				return true;
			}
		}
		return false;
	}

	public static String getdate(long timestamp) {
		String result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(timestamp);
		return result;
	}

	// 从全路径中获取相对路径
	private String getfilerelpath(String fullpath, String rootpath) throws Exception {
		if (fullpath.startsWith(rootpath)) {
			return fullpath.replace(rootpath, "");
		} else {
			String err = ("路径不合法(不包含）：fullpath:" + fullpath + "rootpath" + rootpath);
			logger.info(err);
			throw new Exception(err);
		}
	}

}
