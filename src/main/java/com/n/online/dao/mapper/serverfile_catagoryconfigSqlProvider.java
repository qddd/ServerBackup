package com.n.online.dao.mapper;

import com.n.online.dao.model.serverfile_catagoryconfig;
import org.apache.ibatis.jdbc.SQL;

public class serverfile_catagoryconfigSqlProvider {

    public String insertSelective(serverfile_catagoryconfig record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("serverfile_catagoryconfig");
        
        if (record.getFilecatagory() != null) {
            sql.VALUES("filecatagory", "#{filecatagory,jdbcType=VARCHAR}");
        }
        
        if (record.getRootpath() != null) {
            sql.VALUES("rootpath", "#{rootpath,jdbcType=VARCHAR}");
        }
        
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(serverfile_catagoryconfig record) {
        SQL sql = new SQL();
        sql.UPDATE("serverfile_catagoryconfig");
        
        if (record.getRootpath() != null) {
            sql.SET("rootpath = #{rootpath,jdbcType=VARCHAR}");
        }
        
        sql.WHERE("filecatagory = #{filecatagory,jdbcType=VARCHAR}");
        
        return sql.toString();
    }
}