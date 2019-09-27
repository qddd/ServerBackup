package com.n.online.dao.mapper;

import com.n.online.dao.model.serverfile_catagoryconfig;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface serverfile_catagoryconfigMapper {
    @Delete({
        "delete from serverfile_catagoryconfig",
        "where filecatagory = #{filecatagory,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String filecatagory);

    @Insert({
        "insert into serverfile_catagoryconfig (filecatagory, rootpath)",
        "values (#{filecatagory,jdbcType=VARCHAR}, #{rootpath,jdbcType=VARCHAR})"
    })
    int insert(serverfile_catagoryconfig record);

    @InsertProvider(type=serverfile_catagoryconfigSqlProvider.class, method="insertSelective")
    int insertSelective(serverfile_catagoryconfig record);

    @Select({
        "select",
        "filecatagory, rootpath",
        "from serverfile_catagoryconfig",
        "where filecatagory = #{filecatagory,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="filecatagory", property="filecatagory", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="rootpath", property="rootpath", jdbcType=JdbcType.VARCHAR)
    })
    serverfile_catagoryconfig selectByPrimaryKey(String filecatagory);

    @UpdateProvider(type=serverfile_catagoryconfigSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(serverfile_catagoryconfig record);

    @Update({
        "update serverfile_catagoryconfig",
        "set rootpath = #{rootpath,jdbcType=VARCHAR}",
        "where filecatagory = #{filecatagory,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(serverfile_catagoryconfig record);
}