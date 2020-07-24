package com.ajobs.yuns.mapper.main;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ResourceNameMapper {

  @Insert("insert into tb_${id}_rn(${fieldName}) values(#{value})")
  Integer insert(Integer id, String fieldName, String value);

  @Select(
      {
          "<script>",
          "SELECT ${fieldName} FROM tb_${id}_rn",
          " <where>",
          "   ${fieldName} IS NOT null",
          "   <if test='kws != null'>",
          "     <foreach item='kw' index='index' collection='kws' open='and ' separator=' or ' close=''>",
          "        ${fieldName}=#{kw}",
          "      </foreach>",
          "   </if>",
          " </where>",
          " <if test='offset != null'>limit #{offset},#{numberOfPage}</if>",
          "</script>"
      })
  List<String> selectResourceName(Integer id, String fieldName, List<String> kws, Integer offset,
      Integer numberOfPage);

  @Update("delete from tb_${id}_rn where ${fieldName}=#{value}")
  Integer delResourceName(Integer id, String fieldName, String value);

}
