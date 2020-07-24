package com.ajobs.yuns.mapper.main;

import com.ajobs.yuns.pojo.User;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

  @Select("SELECT table_name FROM information_schema.TABLES WHERE table_name='tb_user'")
  String checkTableWhetherExisted();

  @Update("CREATE TABLE `tb_user` (\n"
      + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
      + "\t`username` VARCHAR(50) NULL DEFAULT NULL,\n"
      + "\t`password` VARCHAR(16) NULL DEFAULT NULL,\n"
      + "\t`pictureUrl` VARCHAR(100) NULL DEFAULT NULL,\n"
      + "\t`email` VARCHAR(100) NULL DEFAULT NULL,\n"
      + "\t`storage` INT(10) NULL DEFAULT '0',\n"
      + "\t`maxStorage` INT(10) NULL DEFAULT '3145728',\n"
      + "\tPRIMARY KEY (`id`) USING BTREE,\n"
      + "\tUNIQUE INDEX `username` (`username`) USING BTREE,\n"
      + "\tUNIQUE INDEX `email` (`email`) USING BTREE\n"
      + ")\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createUserTable();

  @Select("select id from tb_user where username=#{username} and password=#{password}")
  Integer login(String username, String password) throws Exception;

  @Select("select username,pictureUrl,email,storage,maxStorage from tb_user where id=#{id}")
  User userInfo(int id);

  @Select("select id,username,pictureUrl from tb_user limit #{offset},#{numberOfPage} ")
  Map<String, List<String>> userInfos(Integer offset, Integer numberOfPage);

  @Select("select id,username,pictureUrl from tb_user where username like #{keyword} limit 1")
  Map<String, List<String>> selectLikeUserInfos(String keyword);

  @Insert("insert into tb_user(username,password,pictureUrl,email) values(#{username},#{password},#{pictureUrl},#{emailNumber})")
  Integer registryUser(String pictureUrl, String username, String password, String emailNumber);

  //  @Select("select id from tb_user where username=#{username} or email=#{email}")
  @Select({
      "<script>",
      "select id from tb_user ",
      " <where>",
      "     <if test='username!=null'>username=#{username}</if>",
      "     <if test='email!=null'> or email=#{email}</if>",
      " </where>",
      "</script>"
  })
  Integer checkUser(String username, String email);

  @Delete("delete from tb_user where id=#{id}")
  Integer deleteUser(Integer userId);

  @Update({"<script>",
      "update tb_user",
      "  <set>",
      "    <foreach item='fieldName' index='index' collection='fieldNames' open='' separator=', ' close=''>",
      "             <foreach item='value' index='i' collection='values' open='' separator=' ' close=''> ",
      "                 <if test='index == i'>  ${fieldName} =#{value} </if>",
      "              </foreach>",
      "     </foreach>",
      "  </set>",
      "<where>",
      "    <foreach item='conditionFieldName' index='index' collection='conditionFieldNames' open='' separator=' and ' close=''>",
      "             <foreach item='value' index='i' collection='conditionValues' open='' separator=' ' close=''> ",
      "                 <if test='index == i'>  ${conditionFieldName} =#{value} </if>",
      "              </foreach>",
      "     </foreach>",
      "</where>",
      "</script>"})
  Integer updateUser(List<? super String> values, List<String> fieldNames,
      List<String> conditionFieldNames,
      List<? super String> conditionValues) throws Exception;


  @Update("CREATE TABLE `tb_${id}_rn` (\n"
      + "\t`pic_name` VARCHAR(200) NULL DEFAULT NULL,\n"
      + "\t`doc_name` VARCHAR(200) NULL DEFAULT NULL,\n"
      + "\t`resource_name` VARCHAR(200) NULL DEFAULT NULL,\n"
      + "\tUNIQUE INDEX `pic_name` (`pic_name`) USING BTREE,\n"
      + "\tUNIQUE INDEX `doc_name` (`doc_name`) USING BTREE,\n"
      + "\tUNIQUE INDEX `resource_name` (`resource_name`) USING BTREE\n"
      + ")\n"
      + "COLLATE='utf8_general_ci'\n"
      + "ENGINE=InnoDB\n"
      + ";")
  Integer createUserResourceNameTable(Integer id);

  @Select("select username from tb_user where id=#{id}")
  String getUserName(int id);


  @Update("CREATE TABLE `tb_ex_storage_err` (\n"
      + "\t`out_trade_no` VARCHAR(64) NULL DEFAULT NULL\n"
      + ")\n"
      + "ENGINE=InnoDB\n"
      + ";\n")
  void createExtendStorageErrTable();

  @Select("SELECT table_name FROM information_schema.TABLES WHERE table_name='tb_ex_storage_err'")
  String checkExtendStorageTableErrWhetherExisted();

  @Delete("delete from tb_ex_storage_err where out_trade_no=#{data}")
  Integer delErr(String data);

  @Delete("insert into  tb_ex_storage_err values(data)")
  Integer insertErr(String data);
}
