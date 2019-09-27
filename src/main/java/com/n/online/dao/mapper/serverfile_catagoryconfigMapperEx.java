package com.n.online.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.n.online.dao.model.serverfile_catagoryconfig;

@Mapper
public interface serverfile_catagoryconfigMapperEx extends serverfile_catagoryconfigMapper{
    @Select({
        "select filecatagory,rootpath from 	serverfile_catagoryconfig where istatus=1"
    })
   
    List<serverfile_catagoryconfig> selectAll();

}