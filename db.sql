 
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for serverfile
-- ----------------------------
DROP TABLE IF EXISTS `serverfile`;
CREATE TABLE `serverfile`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `filecatagory` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '如resourcedoc,coursezip等',
  `filepath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '除根目录后的相对路径',
  `filedir` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件所在目录(除根目录后的相对路径)，为了批量处理提高效率算法时比对',
  `filedirhash` bigint(64) NOT NULL COMMENT '文件所在目录的合希值，为了批量处理提高效率',
  `filesize_self` bigint(64) NOT NULL COMMENT '文件本身大小',
  `filemodifydate_self` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件本身修改日期',
  `filemodifydate_scan` bigint(20) NOT NULL COMMENT '文件最后被更新的扫描时间,这是比对基准',
  `filemodifydate_scan_des` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件最后被更新的扫描时间,这是比对基准',
  `isdelete` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'file' COMMENT 'file 文件  folder 文件夹 ',
  `filehashormd5` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `filemodifydate_scan_index`(`filemodifydate_scan`) USING BTREE,
  INDEX `filedirhash_last`(`filedirhash`) USING BTREE,
  INDEX `filecatagory`(`filecatagory`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for serverfile_catagoryconfig
-- ----------------------------
DROP TABLE IF EXISTS `serverfile_catagoryconfig`;
CREATE TABLE `serverfile_catagoryconfig`  (
  `filecatagory` varchar(50) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '如resourcedoc,coursezip等',
  `rootpath` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '根目录',
  `istatus` bit(1) NOT NULL,
  PRIMARY KEY (`filecatagory`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = gbk COLLATE = gbk_chinese_ci COMMENT = '文件表' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
