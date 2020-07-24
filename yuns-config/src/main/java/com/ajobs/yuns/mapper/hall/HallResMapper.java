package com.ajobs.yuns.mapper.hall;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HallResMapper {

  @Update("CREATE TABLE `tb_publish_pic` (\n"
      + "\t`picName` VARCHAR(50) NULL DEFAULT NULL,\n"
      + "\t`picUrl` VARCHAR(200) NULL DEFAULT NULL,\n"
      + "\t`userId` VARCHAR(11) NULL DEFAULT NULL,\n"
      + "\t`downloadCount` INT(11) NULL DEFAULT NULL,\n"
      + "\tINDEX `userId` (`userId`) USING BTREE,\n"
      + "\tINDEX `downloadCount` (`downloadCount`) USING BTREE)\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createPublishPicTable();

  @Update("CREATE TABLE `tb_publish_doc` (\n"
      + "\t`docName` VARCHAR(50) NULL DEFAULT NULL,\n"
      + "\t`docUrl` VARCHAR(200) NULL DEFAULT NULL,\n"
      + "\t`userId` VARCHAR(11) NULL DEFAULT NULL,\n"
      + "\t`downloadCount` INT(11) NULL DEFAULT NULL,\n"
      + "\tINDEX `userId` (`userId`) USING BTREE,\n"
      + "\tINDEX `downloadCount` (`downloadCount`) USING BTREE)\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createPublishDocTable();

  @Update("CREATE TABLE `tb_publish_ores` (\n"
      + "\t`oresName` VARCHAR(50) NULL DEFAULT NULL,\n"
      + "\t`oresUrl` VARCHAR(200) NULL DEFAULT NULL,\n"
      + "\t`userId` VARCHAR(11) NULL DEFAULT NULL,\n"
      + "\t`downloadCount` INT(11) NULL DEFAULT NULL,\n"
      + "\tINDEX `userId` (`userId`) USING BTREE,\n"
      + "\tINDEX `downloadCount` (`downloadCount`) USING BTREE)\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createPublishOresTable();

  @Select("SELECT table_name FROM information_schema.TABLES WHERE table_name=#{tableName}")
  String checkTableWhetherExisted(String tableName);

  /**
   * 图片
   */
  @Insert("insert into tb_publish_pic(picName,picUrl,userId) values(#{picName},#{picUrl},#{userId})")
  Integer insertPic(String picName, String picUrl, String userId);

  @Select("select userId from tb_publish_pic where picName=#{picName} and userId=#{userId}")
  String selectSinglePic(String picName, String userId);

  @Update("delete from tb_publish_pic where userId=#{userId} and picUrl like #{picName}")
  Integer delSinglePic(String picName, String userId);

  @Select("select * from tb_publish_pic order by downloadCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectPics(Integer offset, Integer numberOfPage);

  @Select("select * from tb_publish_pic where userId=#{userId} order by downloadCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectIndicatorPics(Integer offset, Integer numberOfPage, String userId);

  @Select("select downloadCount from  tb_publish_pic where picUrl=#{picName} and userId=#{userId}")
  Integer selectDownloadPicCount(String picName, String userId);

  @Update("update tb_publish_pic set downloadCount=#{number} where picUrl=#{picName} and userId=#{userId}")
  Integer insertDownloadPicCount(String picName, String userId, Integer number);

  /**
   * 文档
   */
  @Insert("insert into tb_publish_doc(docName,docUrl,userId) values(#{docName},#{docUrl},#{userId})")
  Integer insertDoc(String docName, String docUrl, String userId);

  @Update("delete from tb_publish_doc where userId=#{userId} and docUrl like #{docName}")
  Integer delSingleDoc(String docName, String userId);

  @Select("select userId from tb_publish_doc where docName=#{docName} and userId=#{userId}")
  String selectSingleDoc(String docName, String userId);

  @Select("select * from tb_publish_doc order by downloadCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectDocs(Integer offset, Integer numberOfPage);

  @Select("select * from tb_publish_doc where userId=#{userId} order by downloadCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectIndicatorDocs(Integer offset, Integer numberOfPage, String userId);

  @Select("select downloadCount from  tb_publish_doc where docUrl=#{docName} and userId=#{userId}")
  Integer selectDownloadDocCount(String docName, String userId);

  @Update("update tb_publish_doc set downloadCount=#{number} where docUrl=#{docName} and userId=#{userId}")
  Integer insertDownloadDocCount(String docName, String userId, Integer number);
  /**
   * 其他资源
   */
  @Insert("insert into tb_publish_ores(oresName,oresUrl,userId) values(#{oresName},#{oresUrl},#{userId})")
  Integer insertOres(String oresName, String oresUrl, String userId);

  @Update("delete from tb_publish_ores where userId=#{userId} and oresUrl like #{oresName}")
  Integer delSingleOres(String oresName, String userId);

  @Select("select userId from tb_publish_ores where oresName=#{oresName} and userId=#{userId}")
  String selectSingleOres(String oresName, String userId);

  @Select("select * from tb_publish_ores order by downloadCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectOreses(Integer offset, Integer numberOfPage);

  @Select("select * from tb_publish_ores where userId=#{userId} order by downloadCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectIndicatorOreses(Integer offset, Integer numberOfPage,
      String userId);


  @Select("select downloadCount from  tb_publish_ores where oresUrl=#{oresName} and userId=#{userId}")
  Integer selectDownloadOresCount(String oresName, String userId);

  @Update("update tb_publish_ores set downloadCount=#{number} where oresUrl=#{oresName} and userId=#{userId}")
  Integer insertDownloadOresCount(String oresName, String userId, Integer number);
}
