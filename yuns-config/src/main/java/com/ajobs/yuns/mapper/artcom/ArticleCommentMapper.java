package com.ajobs.yuns.mapper.artcom;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleCommentMapper {

  @Update("CREATE TABLE `_${id}_${articleLinkName}` (\n"
      + "\t`comId` VARCHAR(11) NOT NULL,\n"
      + "\t`email` VARCHAR(50) NOT NULL,\n"
      + "\t`commentContent` VARCHAR(300) NOT NULL,\n"
      + "\t`commentDataTime` DATETIME NOT NULL\n"
      + ")\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";")
  void createArticleCommentRecordTable(Integer id, String articleLinkName);


  @Update("CREATE TABLE `_${id}_${articleLinkName}_response` (\n"
      + "\t`rId` VARCHAR(11) NOT NULL,\n"
      + "\t`cId` VARCHAR(11) NOT NULL\n"
      + ")\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createArticleCommentResponseTable(Integer id, String articleLinkName);

  @Update("CREATE TABLE `_${id}_${articleLinkName}_verify` (\n"
      + "\t`comId` INT(11) NOT NULL\n"
      + ")\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createArticleCommentVerifyTable(Integer id, String articleLinkName);

  @Update("drop table _${id}_${articleLinkName}")
  void delArticleCommentRecordTable(Integer id, String articleLinkName);

  @Update("drop table _${id}_${articleLinkName}_response")
  void delArticleCommentResponseTable(Integer id, String articleLinkName);

  @Update("drop table _${id}_${articleLinkName}_verify")
  void delArticleCommentVerifyTable(Integer id, String articleLinkName);

  @Select("SELECT table_name FROM information_schema.TABLES WHERE table_name=#{tableName}")
  String checkTableWhetherExisted(String tableName);

  @Update("insert into _${id}_${articleLinkName} values(#{comId},#{email},#{comment},#{commentDataTime})")
  Integer inertArticleRecordComment(Integer id, String articleLinkName, String comId, String email,
      String comment, Timestamp commentDataTime);

  @Update("insert into _${id}_${articleLinkName}_response values(#{rId},#{cId})")
  Integer inertArticleResponseComment(Integer id, String articleLinkName, String rId, String cId);

  @Update("insert into _${id}_${articleLinkName}_verify values(#{comId})")
  Integer inertArticleVerifyComment(Integer id, String articleLinkName, Integer comId);

  @Select("select comId from _${id}_${articleLinkName} order by commentDataTime DESC limit 1;")
  String articleCommentLastId(Integer id, String articleLinkName);

  @Select("select rId from _${id}_${articleLinkName}_response where cId=#{comId}")
  List<String> selectResponseArticleRIDs(Integer id, String articleLinkName, String comId);

  @Select("select cId from _${id}_${articleLinkName}_response where rId=#{comId}")
  String selectResponseArticleCIDs(Integer id, String articleLinkName, String comId);

  @Update("delete from _${id}_${articleLinkName} where comId=#{comId}")
  Integer delRecordArticleComment(Integer id, String articleLinkName, String comId);

  //  @Delete({
//      "<script>",
//      "delete from _${id}_${articleLinkName}_response",
//      "<where>",
//                   "<if test='comId != null'>cId=#{comId} ",
//                   "</if>",
//                  "<if test='responseId != null'>rId=#{responseId} ",
//                  "</if>",
//      "</where>",
//      "</script>"
//  })
  @Delete("delete from _${id}_${articleLinkName}_response where cId=#{comId} or rId=#{comId}")
  Integer delResponseArticleComment(Integer id, String articleLinkName, String comId);

  @Update("delete from _${id}_${articleLinkName}_verify where comId=#{comId}")
  Integer delVerifyArticleComment(Integer id, String articleLinkName, String comId);

  @Select("select * from _${id}_${articleLinkName} order by commentDataTime desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectRecordArticleComments(String id, String articleLinkName,
      Integer offset, Integer numberOfPage);

  @Select("select * from _${id}_${articleLinkName} where comId=#{comId} ")
  Map<String, Object> selectSingleRecordArticleComment(String id, String articleLinkName,
      String comId);

//  @Select("select * from _${id}_${articleLinkName} where commentDataTime>=(select commentDataTime from _${id}_${articleLinkName} limit #{offset},1) limit #{numberOfPage}")
//  Map<String, List<String>> selectRecordArticleMoreComments(String id, String articleLinkName,
//      Integer offset, Integer numberOfPage);

  @Select("select comId from _${id}_${articleLinkName}_verify order by comId desc limit #{offset},#{numberOfPage}")
  List<Integer> selectVerifyArticleComments(String id, String articleLinkName, Integer offset,
      Integer numberOfPage);

  @Select("select comId from _${id}_${articleLinkName}_verify where comId=#{comId}")
  Integer selectSingleVerifyArticleComment(String id, String articleLinkName, Integer comId);

  @Select("select count(*) from _${id}_${articleLinkName}_verify limit 1000")
  Integer selectVerifyArticleLikeCommentsNumber(String id, String articleLinkName);

}
