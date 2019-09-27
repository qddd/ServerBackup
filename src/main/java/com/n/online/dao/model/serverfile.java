package com.n.online.dao.model;

public class serverfile {
    private Integer id;

    private String filecatagory;

    private String filepath;

    private String filedir;

    private Long filedirhash;

    private Long filesizeSelf;

    private String filemodifydateSelf;

    private Long filemodifydateScan;

    private String filemodifydateScanDes;

    private Boolean isdelete;

    private String type;

    private String filehashormd5;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilecatagory() {
        return filecatagory;
    }

    public void setFilecatagory(String filecatagory) {
        this.filecatagory = filecatagory == null ? null : filecatagory.trim();
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath == null ? null : filepath.trim();
    }

    public String getFiledir() {
        return filedir;
    }

    public void setFiledir(String filedir) {
        this.filedir = filedir == null ? null : filedir.trim();
    }

    public Long getFiledirhash() {
        return filedirhash;
    }

    public void setFiledirhash(Long filedirhash) {
        this.filedirhash = filedirhash;
    }

    public Long getFilesizeSelf() {
        return filesizeSelf;
    }

    public void setFilesizeSelf(Long filesizeSelf) {
        this.filesizeSelf = filesizeSelf;
    }

    public String getFilemodifydateSelf() {
        return filemodifydateSelf;
    }

    public void setFilemodifydateSelf(String filemodifydateSelf) {
        this.filemodifydateSelf = filemodifydateSelf == null ? null : filemodifydateSelf.trim();
    }

    public Long getFilemodifydateScan() {
        return filemodifydateScan;
    }

    public void setFilemodifydateScan(Long filemodifydateScan) {
        this.filemodifydateScan = filemodifydateScan;
    }

    public String getFilemodifydateScanDes() {
        return filemodifydateScanDes;
    }

    public void setFilemodifydateScanDes(String filemodifydateScanDes) {
        this.filemodifydateScanDes = filemodifydateScanDes == null ? null : filemodifydateScanDes.trim();
    }

    public Boolean getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Boolean isdelete) {
        this.isdelete = isdelete;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getFilehashormd5() {
        return filehashormd5;
    }

    public void setFilehashormd5(String filehashormd5) {
        this.filehashormd5 = filehashormd5 == null ? null : filehashormd5.trim();
    }
}