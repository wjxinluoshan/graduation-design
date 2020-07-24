package com.ajobs.yuns.mapperImp.hall;

import com.ajobs.yuns.mapper.hall.HallResMapper;
import com.ajobs.yuns.handler.HallResourceResultHandler;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class HallResMapperImp implements HallResMapper {

  @Autowired
  @Qualifier("dbSqlSessionTemplatePub")
  private SqlSessionTemplate sqlSessionTemplate;

  @Qualifier("hallResMapper")
  @Autowired
  private HallResMapper hallResMapper;

  @Override
  public void createPublishPicTable() {
    hallResMapper.createPublishPicTable();
  }

  @Override
  public void createPublishDocTable() {
    hallResMapper.createPublishDocTable();
  }

  @Override
  public void createPublishOresTable() {
    hallResMapper.createPublishOresTable();
  }

  @Override
  public String checkTableWhetherExisted(String tableName) {
    return hallResMapper.checkTableWhetherExisted(tableName);
  }

  @Override
  public Integer insertPic(String picName, String picUrl, String userId) {
    return hallResMapper.insertPic(picName, picUrl, userId);
  }

  @Override
  public String selectSinglePic(String picName, String userId) {
    return hallResMapper.selectSinglePic(picName, userId);
  }

  @Override
  public Integer delSinglePic(String picName, String userId) {
    if (checkTableWhetherExisted("tb_publish_pic") != null) {
      return hallResMapper.delSinglePic(picName, userId);
    } else {
      return null;
    }
  }

  @Override
  public Map<String, List<String>> selectPics(Integer offset, Integer numberOfPage) {
    return selectResources(offset, numberOfPage, "pic",
        "com.ajobs.yuns.mapper.hall.HallResMapper.selectPics");
  }

  private Map<String, List<String>> selectResources(
      Integer offset, Integer numberOfPage,
      String resType, String queryStatement) {
    Page p = new Page();
    p.offset = offset;
    p.numberOfPage = numberOfPage;
    HallResourceResultHandler hallResourceResultHandler = new HallResourceResultHandler(resType);
    sqlSessionTemplate.select(queryStatement, p,
        hallResourceResultHandler);
    return hallResourceResultHandler.getHallResourceResultHandler();
  }

  private Map<String, List<String>> selectIndicatorResources(
      Integer offset, Integer numberOfPage, String userId,
      String resType, String queryStatement) {
    PageT p = new PageT();
    p.offset = offset;
    p.numberOfPage = numberOfPage;
    p.userId = userId;
    HallResourceResultHandler hallResourceResultHandler = new HallResourceResultHandler(resType);
    sqlSessionTemplate.select(queryStatement, p,
        hallResourceResultHandler);
    return hallResourceResultHandler.getHallResourceResultHandler();
  }

  class PageT {

    public Integer offset;
    public Integer numberOfPage;
    public String userId;
  }

  @Override
  public Map<String, List<String>> selectIndicatorPics(Integer offset, Integer numberOfPage,
      String userId) {
    return selectIndicatorResources(offset, numberOfPage, userId, "pic",
        "com.ajobs.yuns.mapper.hall.HallResMapper.selectIndicatorPics");
  }

  @Override
  public Integer selectDownloadPicCount(String picName, String userId) {
    return hallResMapper.selectDownloadPicCount(picName, userId);
  }

  @Override
  public Integer insertDownloadPicCount(String picName, String userId, Integer number) {
    return hallResMapper.insertDownloadPicCount(picName, userId, number);
  }

  public Integer insertDownloadPicCount(String picName, String userId) {
    Integer integer = selectDownloadPicCount(picName, userId);
    if (integer == null) {
      integer = 0;
    }
    return hallResMapper.insertDownloadPicCount(picName, userId, integer + 1);
  }

  @Override
  public Integer insertDoc(String docName, String docUrl, String userId) {
    return hallResMapper.insertDoc(docName, docUrl, userId);
  }

  @Override
  public Integer delSingleDoc(String docName, String userId) {
    if (checkTableWhetherExisted("tb_publish_doc") != null) {
      return hallResMapper.delSingleDoc(docName, userId);
    } else {
      return null;
    }
  }

  @Override
  public String selectSingleDoc(String docName, String userId) {
    return hallResMapper.selectSingleDoc(docName, userId);
  }

  @Override
  public Map<String, List<String>> selectDocs(Integer offset, Integer numberOfPage) {
    return selectResources(offset, numberOfPage, "doc",
        "com.ajobs.yuns.mapper.hall.HallResMapper.selectDocs");
  }

  @Override
  public Map<String, List<String>> selectIndicatorDocs(Integer offset, Integer numberOfPage,
      String userId) {
    return selectIndicatorResources(offset, numberOfPage, userId, "doc",
        "com.ajobs.yuns.mapper.hall.HallResMapper.selectIndicatorDocs");
  }

  @Override
  public Integer selectDownloadDocCount(String docName, String userId) {
    return hallResMapper.selectDownloadDocCount(docName, userId);
  }

  @Override
  public Integer insertDownloadDocCount(String docName, String userId, Integer number) {
    return hallResMapper.insertDownloadDocCount(docName, userId, number);
  }

  public Integer insertDownloadDocCount(String docName, String userId) {
    Integer integer = selectDownloadDocCount(docName, userId);
    if (integer == null) {
      integer = 0;
    }
    return insertDownloadDocCount(docName, userId, integer + 1);
  }

  @Override
  public Integer insertOres(String oresName, String oresUrl, String userId) {
    return hallResMapper.insertOres(oresName, oresUrl, userId);
  }

  @Override
  public Integer delSingleOres(String oresName, String userId) {
    if (checkTableWhetherExisted("tb_publish_ores") != null) {
      return hallResMapper.delSingleOres(oresName, userId);
    } else {
      return null;
    }
  }

  @Override
  public String selectSingleOres(String oresName, String userId) {
    return hallResMapper.selectSingleOres(oresName, userId);
  }

  @Override
  public Map<String, List<String>> selectOreses(Integer offset, Integer numberOfPage) {
    return selectResources(offset, numberOfPage, "ores",
        "com.ajobs.yuns.mapper.hall.HallResMapper.selectOreses");

  }

  @Override
  public Map<String, List<String>> selectIndicatorOreses(Integer offset, Integer numberOfPage,
      String userId) {
    return selectIndicatorResources(offset, numberOfPage, userId, "ores",
        "com.ajobs.yuns.mapper.hall.HallResMapper.selectIndicatorOreses");
  }

  @Override
  public Integer selectDownloadOresCount(String oresName, String userId) {
    return hallResMapper.selectDownloadOresCount(oresName, userId);
  }

  @Override
  public Integer insertDownloadOresCount(String oresName, String userId, Integer number) {
    return hallResMapper.insertDownloadOresCount(oresName, userId, number);
  }

  public Integer insertDownloadOresCount(String oresName, String userId) {
    Integer integer = selectDownloadOresCount(oresName, userId);
    if (integer == null) {
      integer = 0;
    }
    return insertDownloadOresCount(oresName, userId, integer + 1);
  }

//  private void initPubResMapper() {
//    if (hallResMapper == null) {
//      synchronized (this) {
//        if (hallResMapper == null) {
//          hallResMapper = sqlSessionTemplate.getMapper(HallResMapper.class);
//        }
//      }
//    }
//  }

  class Page {

    public Integer offset;
    public Integer numberOfPage;
  }
}
