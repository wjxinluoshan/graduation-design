package com.ajobs.yuns.mapper.main;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleMapper {

  @Update("CREATE TABLE `tb_${id}_art` (\n"
      + "\t`article_link` VARCHAR(150) NULL DEFAULT NULL,\n"
      + "\t`article_name` VARCHAR(50) NULL DEFAULT NULL,\n"
      + "\tUNIQUE INDEX `article_link` (`article_link`) USING BTREE\n"
      + ")\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";")
  Integer createArticleTable(Integer id);

  @Select("select article_link from tb_${id}_art where article_link=#{articleLink}")
  String selectSingleRecordArticleLink(Integer id, String articleLink);

  @Select("select article_name from tb_${id}_art where article_link=#{articleLink}")
  String selectSingleArticleNameUseLink(Integer id, String articleLink);

  @Insert("insert into tb_${id}_art(article_link,article_name) values(#{articleLink},#{articleName})")
  Integer insertArticleLinkAndName(Integer id, String articleLink, String articleName);

  @Select(
      {
          "<script>",
          "select * from tb_${id}_art",
          " <where>",
          "   <if test='kws != null'>",
          "     <foreach item='kw' index='index' collection='kws' open='' separator=' or ' close=''>",
          "        article_link=#{kw}",
          "      </foreach>",
          "   </if>",
          " </where>",
          " <if test='offset != null'> limit #{offset},#{numberOfPage}</if>",
          "</script>"
      })
  Map<String, List<String>> selectArtLinkAndName(Integer id, List<String> kws, Integer offset,
      Integer numberOfPage);

  @Update("delete from tb_${id}_art where article_link=#{articleLink}")
  Integer delArticleLink(Integer id, String articleLink);

  @Update("update tb_${id}_art set article_name=#{articleName} where article_link=#{articleLink}")
  Integer updateArticleLinkAndName(Integer id, String articleLink, String articleName);
}
