package com.ajobs.yuns.mapperImp;

import com.ajobs.yuns.mapper.main.UserMapper;
import com.ajobs.yuns.tool.FileHelper;
import com.ajobs.yuns.tool.RSAUtil;
import com.ajobs.yuns.component.RSAPrivateKeyComponent;
import com.ajobs.yuns.pojo.User;
import com.ajobs.yuns.handler.UserInfoResultHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserMapperImp implements UserMapper, DisposableBean {

  @Autowired
  @Qualifier("userMapper")
  private UserMapper userMapper;

  @Autowired
  @Qualifier("dbSqlSessionTemplate")
  private SqlSessionTemplate sqlSessionTemplate;

  @Autowired
  @Qualifier("rSAPrivateKeyComponent")
  private RSAPrivateKeyComponent rSAPrivateKeyComponent;

  @Override
  public String checkTableWhetherExisted() {
    return userMapper.checkTableWhetherExisted();
  }

  @Override
  public void createUserTable() {
    userMapper.createUserTable();
  }

  @Override
  public Integer login(String username, String password) throws Exception {
    /*
     *密码的解密
     */
    password = RSAUtil.decrypt(password, rSAPrivateKeyComponent.privateKey());
    return userMapper.login(username, password);
  }


  @Override
  public User userInfo(int id) {
    return userMapper.userInfo(id);
  }

  public Map<String, List<String>> userInfos(Integer offset, Integer numberOfPage) {
    Page page = new Page();
    page.offset = offset;
    page.numberOfPage = numberOfPage;
    UserInfoResultHandler userInfoResultHandler = new UserInfoResultHandler();
    sqlSessionTemplate
        .select("com.ajobs.yuns.mapper.main.UserMapper.userInfos", page, userInfoResultHandler);
    return userInfoResultHandler.geUserInfoResultHandler();

  }

  @Override
  public Map<String, List<String>> selectLikeUserInfos(String keyword) {
    UserInfoResultHandler userInfoResultHandler = new UserInfoResultHandler();
    sqlSessionTemplate
        .select("com.ajobs.yuns.mapper.main.UserMapper.selectLikeUserInfos", "%" + keyword + "%",
            userInfoResultHandler);
    return userInfoResultHandler.geUserInfoResultHandler();
  }

  class Page {

    public Integer offset;
    public Integer numberOfPage;
  }

  @Override
  public Integer registryUser(String pictureUrl, String username, String password,
      String emailNumber) {
    return userMapper.registryUser(pictureUrl, username, password, emailNumber);
  }

  @Override
  public Integer checkUser(String username, String email) {
    return userMapper.checkUser(username, email);
  }

  @Override
  public Integer deleteUser(Integer userId) {
    return userMapper.deleteUser(userId);
  }

  /**
   * 在main.jsp加载出来后就使用
   *
   * @param data
   * @return
   */
  @Override
  public Integer delErr(String data) {
    return userMapper.delErr(data);
  }

  @Override
  public Integer insertErr(String data) {
    return userMapper.insertErr(data);
  }

  @Override
  public Integer updateUser(List<? super String> values, List<String> fieldNames,
      List<String> conditionFieldNames, List<? super String> conditionValues) throws Exception {
    int index = fieldNames.indexOf("password");
    if (index > -1) {
      values.set(index,
          RSAUtil.decrypt((String) values.get(index), rSAPrivateKeyComponent.privateKey()));
    }
    index = fieldNames.indexOf("pictureUrl");
    if (index > -1) {
      int i = conditionFieldNames.indexOf("id");
      MultipartFile multipartFile = (MultipartFile) values.get(index);
      File profileFile = new File(FileHelper.Companion.getClasspath(),
          FileHelper.Companion.getUserPath() + "/" + conditionValues.get(i));
      if (!profileFile.exists()) {
        profileFile.mkdirs();
      }
      //删除原图像文件
      for (File file : profileFile.listFiles()) {
        if (file.isFile()) {
          file.delete();
          break;
        }
      }
      String pictureUrl = userInfo((Integer) conditionValues.get(i)).pictureUrl;
      if (pictureUrl != null) {
        String[] arr = userInfo((Integer) conditionValues.get(i)).pictureUrl
            .split("/");
        new File(profileFile, arr[arr.length - 1]).delete();
      }
      File newFile = new File(profileFile.getPath(),
          multipartFile.getOriginalFilename());
      try (FileOutputStream fileOutputStream = new FileOutputStream(
          newFile
      )) {
        fileOutputStream.write(multipartFile.getBytes());
        values.set(index, "/yuns/user/" + conditionValues.get(i) + "/" + newFile.getName());
      } catch (Exception e) {
        throw e;
      }
    }
    return userMapper.updateUser(values, fieldNames, conditionFieldNames, conditionValues);
  }

  @Override
  public Integer createUserResourceNameTable(Integer id) {
    return userMapper.createUserResourceNameTable(id);
  }

  @Override
  public String getUserName(int id) {
    return userMapper.getUserName(id);
  }

  @Override
  public void createExtendStorageErrTable() {
    if (userMapper.checkExtendStorageTableErrWhetherExisted() == null) {
      userMapper.createExtendStorageErrTable();
    }
  }

  @Override
  public String checkExtendStorageTableErrWhetherExisted() {
    return userMapper.checkExtendStorageTableErrWhetherExisted();
  }

  public Integer registry(String username, String password,
      String emailNumber) throws Exception {
//      if (profileBinaryData.contains("image/jpeg")) {
//        profileBinaryData = profileBinaryData.replace("data:image/jpeg;base64,", "");
//        imgName += ".jpg";
//      } else if (profileBinaryData.contains("image/png")) {
//        profileBinaryData = profileBinaryData.replace("data:image/png;base64,", "");
//        imgName += ".png";
//      }
    /**
     * 密码解密
     */
    password = RSAUtil.decrypt(password, rSAPrivateKeyComponent.privateKey());

    return registryUser(null, username, password, emailNumber);
  }

//  private void initUserMapper() {
//    if (userMapper == null) {
//      synchronized (this) {
//        if (userMapper == null) {
//          userMapper = sqlSessionTemplate.getMapper(UserMapper.class);
//        }
//      }
//    }
//  }

  @Override
  public void destroy() throws Exception {
    sqlSessionTemplate.clearCache();
    sqlSessionTemplate.close();
  }
}
