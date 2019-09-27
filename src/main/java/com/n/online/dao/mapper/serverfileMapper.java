package com.n.online.dao.mapper;

import com.n.online.dao.model.serverfile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface serverfileMapper {
    @Delete({
        "delete from serverfile",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into serverfile (filecatagory, filepath, ",
        "filedir, filedirhash, ",
        "filesize_self, filemodifydate_self, ",
        "filemodifydate_scan, filemodifydate_scan_des, ",
        "isdelete, type, filehashormd5)",
        "values (#{filecatagory,jdbcType=VARCHAR}, #{filepath,jdbcType=VARCHAR}, ",
        "#{filedir,jdbcType=VARCHAR}, #{filedirhash,jdbcType=BIGINT}, ",
        "#{filesizeSelf,jdbcType=BIGINT}, #{filemodifydateSelf,jdbcType=VARCHAR}, ",
        "#{filemodifydateScan,jdbcType=BIGINT}, #{filemodifydateScanDes,jdbcType=VARCHAR}, ",
        "#{isdelete,jdbcType=BIT}, #{type,jdbcType=VARCHAR}, #{filehashormd5,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(serverfile record);

    @InsertProvider(type=serverfileSqlProvider.class, method="insertSelective")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insertSelective(serverfile record);

    @Select({
        "select",
        "id, filecatagory, filepath, filedir, filedirhash, filesize_self, filemodifydate_self, ",
        "filemodifydate_scan, filemodifydate_scan_des, isdelete, type, filehashormd5",
        "from serverfile",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="filecatagory", property="filecatagory", jdbcType=JdbcType.VARCHAR),
        @Result(column="filepath", property="filepath", jdbcType=JdbcType.VARCHAR),
        @Result(column="filedir", property="filedir", jdbcType=JdbcType.VARCHAR),
        @Result(column="filedirhash", property="filedirhash", jdbcType=JdbcType.BIGINT),
        @Result(column="filesize_self", property="filesizeSelf", jdbcType=JdbcType.BIGINT),
        @Result(column="filemodifydate_self", property="filemodifydateSelf", jdbcType=JdbcType.VARCHAR),
        @Result(column="filemodifydate_scan", property="filemodifydateScan", jdbcType=JdbcType.BIGINT),
        @Result(column="filemodifydate_scan_des", property="filemodifydateScanDes", jdbcType=JdbcType.VARCHAR),
        @Result(column="isdelete", property="isdelete", jdbcType=JdbcType.BIT),
        @Result(column="type", property="type", jdbcType=JdbcType.VARCHAR),
        @Result(column="filehashormd5", property="filehashormd5", jdbcType=JdbcType.VARCHAR)
    })
    serverfile selectByPrimaryKey(Integer id);

    @UpdateProvider(type=serverfileSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(serverfile record);

    @Update({
        "update serverfile",
        "set filecatagory = #{filecatagory,jdbcType=VARCHAR},",
          "filepath = #{filepath,jdbcType=VARCHAR},",
          "filedir = #{filedir,jdbcType=VARCHAR},",
          "filedirhash = #{filedirhash,jdbcType=BIGINT},",
          "filesize_self = #{filesizeSelf,jdbcType=BIGINT},",
          "filemodifydate_self = #{filemodifydateSelf,jdbcType=VARCHAR},",
          "filemodifydate_scan = #{filemodifydateScan,jdbcType=BIGINT},",
          "filemodifydate_scan_des = #{filemodifydateScanDes,jdbcType=VARCHAR},",
          "isdelete = #{isdelete,jdbcType=BIT},",
          "type = #{type,jdbcType=VARCHAR},",
          "filehashormd5 = #{filehashormd5,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(serverfile record);
}