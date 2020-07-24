package com.ajobs.yuns.mapper.hall;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HallArticleMapper {

  @Update("CREATE TABLE `tb_publish_art` (\n"
      + "\t`articleLinkName` VARCHAR(20) NULL DEFAULT NULL,\n"
      + "\t`articleLink` VARCHAR(150) NULL DEFAULT NULL,\n"
      + "\t`articleTitleName` VARCHAR(50) NULL DEFAULT NULL,\n"
      + "\t`readCount` INT(11) NULL DEFAULT NULL,\n"
      + "\t`userId` VARCHAR(11) NULL DEFAULT NULL,\n"
      + "\tUNIQUE INDEX `articleLink` (`articleLink`) USING BTREE,\n"
      + "\tINDEX `userId` (`userId`) USING BTREE,\n"
      + "\tINDEX `readCount` (`readCount`) USING BTREE)\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createPublishArticleTable();

  @Select("SELECT table_name FROM information_schema.TABLES WHERE table_name='tb_publish_art'")
  String checkTableWhetherExisted();

  @Insert("insert into tb_publish_art(articleLinkName,articleLink,articleTitleName,userId) values(#{articleLinkName},#{articleLink},#{articleTitleName},#{userId})")
  Integer insertArt(String articleLinkName, String articleLink, String articleTitleName,
      String userId) throws Exception;

  @Select("select userId from tb_publish_art where articleLinkName=#{articleLinkName} and userId=#{userId}")
  String selectSingleArt(String articleLinkName, String userId);

  @Update("delete from tb_publish_art where userId=#{userId} and articleLinkName=#{articleLinkName}")
  Integer delSingleArt(String articleLinkName, String userId);

  @Select("select * from tb_publish_art order by readCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectPubArticleInfo(Integer offset, Integer numberOfPage);


  @Select({"<script>",
      "select articleLinkName,articleLink,articleTitleName,userId from tb_publish_art",
      " <where>",
      " <if test='userId !=null'>",
      "    userId=#{userId}",
      " </if>",
      "  <if test='keywords !=null'>   <foreach item='kw' index='index' collection='keywords' open='and articleLink in ( ' separator=',' close=')'>",
      "         #{kw}",
      "      </foreach> </if>",
      "</where>",
      "order by readCount desc <if test='offset != null'> limit #{offset},#{numberOfPage} </if>",
      "</script>"})
  Map<String, List<String>> selectPubArticleLikeInfo(List<String> keywords, Integer offset,
      Integer numberOfPage, String userId);


  @Select("select * from tb_publish_art where userId=#{userId} order by readCount desc limit #{offset},#{numberOfPage}")
  Map<String, List<String>> selectIndicatorPubArticleInfo(Integer offset, Integer numberOfPage,
      String userId);

  @Select("select readCount from  tb_publish_art where articleLinkName=#{articleLinkName} and userId=#{userId}")
  Integer selectArtReadCount(String articleLinkName, String userId);

  @Update("update tb_publish_art set readCount=#{number} where articleLinkName=#{articleLinkName} and userId=#{userId}")
  Integer insertArtReadCount(String articleLinkName, String userId, Integer number);

  @Update("update tb_publish_art set articleTitleName=#{articleTitleName} where articleLinkName=#{articleLinkName} and userId=#{userId}")
  Integer updateArtTitleName(String articleLinkName, String userId, String articleTitleName);
}
