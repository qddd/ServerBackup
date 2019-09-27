# ServerBackup
轻量级服务器备份程序（适应于IT运维，适用于远程应用服务器文件或资源备份至本地容灾)
1、服务器扫描文件变化，可配置多个根文件夹。
2、客户端文件下载：实现增量下载，具有时间戳和文件md5判断，不重复下载。
3、服务器与客户端不缓存文件版本，轻量控制。

基本框架：
springboot2+mybatis+定时任务

安装：
1、数据库参见DB.sql

2、服务端安装
要求java应用服务器 tomcat等. jdk:1.8.
部署后定时扫描文件夹（建议晚间）
需备份的文件夹设置参见数据库表：serverfile_catagoryconfig，表中每条记录配置一个下载的根文件夹路径。
    
3、客户端：
运行backupclient.bat，关联的backupclient.jar由本程序导出生成。
对应配置文件为：system.properties

兼职公司有这方面的需求，写个程序实现异地备份功能，实现每天数据备份。有效防勒索病毒和容灾。

遵照GPL协议（GNU General Public License），商用自用都没有限制，开心就好。

     
     
