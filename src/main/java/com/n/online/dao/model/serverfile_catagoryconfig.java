package com.n.online.dao.model;

public class serverfile_catagoryconfig {
    private String filecatagory;

    private String rootpath;

    public String getFilecatagory() {
        return filecatagory;
    }

    public void setFilecatagory(String filecatagory) {
        this.filecatagory = filecatagory == null ? null : filecatagory.trim();
    }

    public String getRootpath() {
        return rootpath;
    }

    public void setRootpath(String rootpath) {
        this.rootpath = rootpath == null ? null : rootpath.trim();
    }
}