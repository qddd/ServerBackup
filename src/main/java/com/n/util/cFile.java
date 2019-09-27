package com.n.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

 

/**
 * @author DaiD
 *
 */
public class cFile {

	/**
	 * 
	 */
	public cFile() {
		// TODO Auto-generated constructor stub
	}

	public void str2File(String content, String path) throws Exception {
		File f = new File(path);
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;
		try {
			OutputStream os = new FileOutputStream(f);
			writer = new OutputStreamWriter(os, "UTF-8");
			bw = new BufferedWriter(writer);
			bw.write(content.toString());

			bw.flush();
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				throw new Exception(e);
			}
		}
	}

	/**
	 * 文件夹创建函数
	 * 
	 * @param sPath
	 *            文件夹路径
	 * @return String
	 * @throws IOException
	 * @auther
	 */
	public static void createFolder(String sPath) throws IOException {
		sPath = sPath.replaceAll("\\\\", "/");
		File f = null;
		String sTmpPath;
		String[] aP;
		sTmpPath = sPath;
		if (sTmpPath.indexOf("/") == -1) {
			throw new IOException("路径错误！");
		} else {
			aP = sPath.split("/");
			for (int i = 0; i < aP.length; i++) {
				sTmpPath = "";
				for (int j = 0; j <= i; j++) {
					sTmpPath += aP[j] + "/";
				}
				f = new File(sTmpPath);
				if (!f.exists()) {
					f.mkdir();
				}
			}
		}
	}

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[5120];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			if (inBuff != null) {
				inBuff.close();
			}
			if (outBuff != null) {
				outBuff.close();
			}
		}
	}

	/**
	 * 文本文件保存
	 * 
	 * @param path
	 *            String 文件路径
	 * @param content
	 *            String 文件内容
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @auther
	 */
	public static void writeFile(String path, String content, String codetype)
			throws FileNotFoundException, IOException {
		// 创建文件夹
		String path_dir = path.replaceAll("\\\\", "/");
		path_dir = path_dir.substring(0, path_dir.lastIndexOf("/"));
		createFolder(path_dir);
		File file = new File(path);
		Writer writer = new OutputStreamWriter(new FileOutputStream(file), codetype);

		writer.write(content);
		writer.close();
	}

	/**
	 * 获取WEB-INF目录下面server.xml文件的路径
	 * 
	 * @return
	 */
	public static String getWebRootPath(HttpServletRequest req) {
		String path =req.getServletContext().getRealPath("/");
		return path;
	}
	

 
	 

	/**
	 * 获取一个文本文件的内容
	 * 
	 * @param file
	 *            File
	 * @return String 文件内容
	 * @auther
	 */
	public static String getFileContent(File file) {
		String path = file.getPath();
		StringBuffer sb = new StringBuffer();
		try {
			FileReader fr = new FileReader(path);
			// 关键在于读取过程中，要判断所读取的字符是否已经到了文件的末尾，并且这个字符是不是文件中的断行符，即判断该字符值是否为13。
			int c = fr.read(); // 从文件中读取一个字符
			// 判断是否已读到文件结尾
			while (c != -1) {
				sb.append((char) c);
				c = fr.read(); // 从文件中继续读取数据

			}
			fr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}

	/*
	 * 写文件到指定路径 byte[] b 文件数据数组 outputFile 输出文件路径
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
			// log.error("字节转file时出现异常", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					// log.error("字节转file关闭流时出现异常", e1);
				}
			}
		}
		return file;
	}

	public static String readFileS(String file) throws IOException {
		try {
			FileReader f = new FileReader(file);
			char[] buffer = new char[1024];
			StringBuffer b = new StringBuffer();
			for (;;) {
				int i = f.read(buffer, 0, 1024);
				if (i == -1) {
					break;
				}
				b.append(buffer, 0, i);
			}
			f.close();
			return b.toString();
		} catch (IOException e) {
			return null;
		} catch (NullPointerException ie) {
		}
		return null;
	}

	public static void writeFileS(String file, String text) throws IOException {
		try {
			FileWriter f = new FileWriter(file);
			f.write(text.toCharArray());
			f.close();
		} catch (IOException e) {
		}
	}

	public static byte[] readFileB(String file) throws IOException {
		try {
			FileInputStream f = new FileInputStream(file);
			int n = f.available();
			byte[] b = new byte[n];
			f.read(b, 0, n);
			f.close();

			return b;
		} catch (IOException e) {
			return null;
		} catch (NullPointerException ie) {
		}
		return null;
	}

	public static void writeFileB(String file, byte[] buf) throws IOException {
		try {
			FileOutputStream f = new FileOutputStream(file);
			f.write(buf);
			f.close();
		} catch (IOException localIOException) {
		}
	}

	public static String getFileDirectory(String fullpath) {
		File file = new File(fullpath);
		String strParentDirectory = file.getParent();
		return strParentDirectory;
	}

	public static String getFileName_Ext(String fullpath) {
		File file = new File(fullpath);
		return file.getName();
	}

	public static String getFileNameNoExt(String NameWithExt) {
		return NameWithExt.substring(0, NameWithExt.lastIndexOf("."));
	}

	public static String getFileExt(String NameOrPath) {
		return NameOrPath.substring(NameOrPath.lastIndexOf(".") + 1);
	}

	//获取文件夹下所有文件
	public static List<String> getAllFilePaths(String sfilepath,List<String> filePaths){
		  File filePath=new File(sfilepath);
          File[] files = filePath.listFiles();
          if(files == null){
              return filePaths;    
          }    
          for(File f:files){
              if(f.isDirectory()){
                  filePaths.add(f.getPath());
                  getAllFilePaths(f.getPath(),filePaths);
              }else{
                  filePaths.add(f.getPath());
              }    
          }
          return filePaths;
      }
	
	
	
}
