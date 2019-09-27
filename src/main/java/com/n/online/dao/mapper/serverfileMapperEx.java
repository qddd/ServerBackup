package com.n.online.dao.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.n.online.dao.model.serverfile;

@Mapper
public interface serverfileMapperEx extends serverfileMapper{
    @Select({
        "select * from serverfile where id>=#{beginId} and filecatagory=#{filecatagory} limit #{span}"
    })
   
    List<serverfile> selectFileRecordsToDelete(int beginId,int span,String filecatagory);
    
    

    
    //获取某个目录(及哈希）下的所有文件
    @Select({
        "select * from serverfile where filedirhash=#{hashvalue} and filecatagory=#{filecatagory} and filedir=#{filedir}"
    })   
    List<serverfile> selectFileRecordsByDirHash(Long hashvalue,String filecatagory,String filedir);
    
    
    //获取用户当前接着下载的文件列表，下次, 注意排序与前端对应
    @Select({
        "select id,filepath,filemodifydate_self,filecatagory,isdelete,filemodifydate_scan,filemodifydate_scan_des,type,filehashormd5 from serverfile where filemodifydate_scan>#{filemodifydate_scan} and filecatagory=#{filecatagory} order by filemodifydate_scan  limit 100"
    })   
    List<HashMap<String, Object>> selectBackupDownloadList(String filecatagory,Long filemodifydate_scan);



}