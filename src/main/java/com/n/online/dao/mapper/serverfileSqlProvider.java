package com.n.online.dao.mapper;

import com.n.online.dao.model.serverfile;
import org.apache.ibatis.jdbc.SQL;

public class serverfileSqlProvider {

    public String insertSelective(serverfile record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("serverfile");
        
        if (record.getFilecatagory() != null) {
            sql.VALUES("filecatagory", "#{filecatagory,jdbcType=VARCHAR}");
        }
        
        if (record.getFilepath() != null) {
            sql.VALUES("filepath", "#{filepath,jdbcType=VARCHAR}");
        }
        
        if (record.getFiledir() != null) {
            sql.VALUES("filedir", "#{filedir,jdbcType=VARCHAR}");
        }
        
        if (record.getFiledirhash() != null) {
            sql.VALUES("filedirhash", "#{filedirhash,jdbcType=BIGINT}");
        }
        
        if (record.getFilesizeSelf() != null) {
            sql.VALUES("filesize_self", "#{filesizeSelf,jdbcType=BIGINT}");
        }
        
        if (record.getFilemodifydateSelf() != null) {
            sql.VALUES("filemodifydate_self", "#{filemodifydateSelf,jdbcType=VARCHAR}");
        }
        
        if (record.getFilemodifydateScan() != null) {
            sql.VALUES("filemodifydate_scan", "#{filemodifydateScan,jdbcType=BIGINT}");
        }
        
        if (record.getFilemodifydateScanDes() != null) {
            sql.VALUES("filemodifydate_scan_des", "#{filemodifydateScanDes,jdbcType=VARCHAR}");
        }
        
        if (record.getIsdelete() != null) {
            sql.VALUES("isdelete", "#{isdelete,jdbcType=BIT}");
        }
        
        if (record.getType() != null) {
            sql.VALUES("type", "#{type,jdbcType=VARCHAR}");
        }
        
        if (record.getFilehashormd5() != null) {
            sql.VALUES("filehashormd5", "#{filehashormd5,jdbcType=VARCHAR}");
        }
        
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(serverfile record) {
        SQL sql = new SQL();
        sql.UPDATE("serverfile");
        
        if (record.getFilecatagory() != null) {
            sql.SET("filecatagory = #{filecatagory,jdbcType=VARCHAR}");
        }
        
        if (record.getFilepath() != null) {
            sql.SET("filepath = #{filepath,jdbcType=VARCHAR}");
        }
        
        if (record.getFiledir() != null) {
            sql.SET("filedir = #{filedir,jdbcType=VARCHAR}");
        }
        
        if (record.getFiledirhash() != null) {
            sql.SET("filedirhash = #{filedirhash,jdbcType=BIGINT}");
        }
        
        if (record.getFilesizeSelf() != null) {
            sql.SET("filesize_self = #{filesizeSelf,jdbcType=BIGINT}");
        }
        
        if (record.getFilemodifydateSelf() != null) {
            sql.SET("filemodifydate_self = #{filemodifydateSelf,jdbcType=VARCHAR}");
        }
        
        if (record.getFilemodifydateScan() != null) {
            sql.SET("filemodifydate_scan = #{filemodifydateScan,jdbcType=BIGINT}");
        }
        
        if (record.getFilemodifydateScanDes() != null) {
            sql.SET("filemodifydate_scan_des = #{filemodifydateScanDes,jdbcType=VARCHAR}");
        }
        
        if (record.getIsdelete() != null) {
            sql.SET("isdelete = #{isdelete,jdbcType=BIT}");
        }
        
        if (record.getType() != null) {
            sql.SET("type = #{type,jdbcType=VARCHAR}");
        }
        
        if (record.getFilehashormd5() != null) {
            sql.SET("filehashormd5 = #{filehashormd5,jdbcType=VARCHAR}");
        }
        
        sql.WHERE("id = #{id,jdbcType=INTEGER}");
        
        return sql.toString();
    }
}