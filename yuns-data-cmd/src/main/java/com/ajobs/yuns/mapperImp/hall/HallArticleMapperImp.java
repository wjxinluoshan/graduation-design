package com.ajobs.yuns.mapperImp.hall;

import com.ajobs.yuns.component.Npl;
import com.ajobs.yuns.handler.HallArtResultHandler;
import com.ajobs.yuns.mapper.hall.HallArticleMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class HallArticleMapperImp implements HallArticleMapper {

  @Autowired
  @Qualifier("dbSqlSessionTemplatePub")
  private SqlSessionTemplate sqlSessionTemplate;

  @Autowired
  @Qualifier("dbTransactionManagerPub")
  private DataSourceTransactionManager dbTransactionManagerPub;

  @Qualifier("hallArticleMapper")
  @Autowired
  private HallArticleMapper hallArticleMapper;

  @Autowired
  private Npl npl;

  @Override
  public void createPublishArticleTable() {
    hallArticleMapper.createPublishArticleTable();
  }

  @Override
  public String checkTableWhetherExisted() {
    return hallArticleMapper.checkTableWhetherExisted();
  }

  @Override
  public Integer insertArt(String articleLinkName, String articleLink, String articleTitleName,
      String userId) throws Exception {
    List<String> datas = new ArrayList<>();
    datas.add(articleTitleName);
    List<String> tags = new ArrayList<>();
    tags.add(articleLink);
    npl.createHallDocument(datas, tags, npl.ARTICLE);
    return hallArticleMapper.insertArt(articleLinkName, articleLink, articleTitleName, userId);
  }

  @Override
  public String selectSingleArt(String articleLinkName, String userId) {
    return hallArticleMapper.selectSingleArt(articleLinkName, userId);
  }

  @Override
  public Integer delSingleArt(String articleLinkName, String userId) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(dbTransactionManagerPub);
    return transactionTemplate.execute(txStatus -> {
      try {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("/yuns/user/" + userId + "/article/" + articleLinkName);

        npl.deleteOrUpdateHallDocument(tags, npl.ARTICLE, false);

        if (checkTableWhetherExisted() != null) {
          return hallArticleMapper.delSingleArt(articleLinkName, userId);
        } else {
          return null;
        }
      } catch (Exception e) {
        txStatus.setRollbackOnly();
        e.printStackTrace();
      }
      return null;
    });
  }

  @Override
  public Map<String, List<String>> selectPubArticleInfo(Integer offset, Integer numberOfPage) {
    HallArtResultHandler hallArtResultHandler = new HallArtResultHandler();
    Page page = new Page();
    page.offset = offset;
    page.numberOfPage = numberOfPage;
    sqlSessionTemplate
        .select("com.ajobs.yuns.mapper.hall.HallArticleMapper.selectPubArticleInfo", page,
            hallArtResultHandler);
    return hallArtResultHandler.getHallArtResultHandler();
  }

  @Override
  public Map<String, List<String>> selectPubArticleLikeInfo(List<String> keywords, Integer offset,
      Integer numberOfPage, String userId) {
    HallArtResultHandler hallArtResultHandler = new HallArtResultHandler();
    PageTh page = new PageTh();
    page.offset = offset;
    page.numberOfPage = numberOfPage;
    page.keywords = keywords;
    page.userId = userId;
    sqlSessionTemplate
        .select("com.ajobs.yuns.mapper.hall.HallArticleMapper.selectPubArticleLikeInfo", page,
            hallArtResultHandler);
    return hallArtResultHandler.getHallArtResultHandler();
  }

  public Map<String, List<String>> selectPubArticleLikeInfo(String keyword, Integer offset,
      Integer numberOfPage, String userId) {
    List<String> kws = null;
    try {
      List<String> tempL = npl
          .queryHallDocument(keyword, userId, npl.ARTICLE, offset + numberOfPage).get(1);
//      for (String s : npl.participleKeyword(keyword)) {
//        kws.add("%" + s + "%");
//      }
      if (tempL.size() > 0) {
        kws = new ArrayList<>();
        for (int i = offset; i < offset + numberOfPage; i++) {
          if (i > tempL.size()) {
            break;
          }
          kws.add(tempL.get(i));
        }
      }
    } catch (Exception e) {
    }
    if (kws != null && kws.size() > 0) {
      return selectPubArticleLikeInfo(kws, null, null, userId);
    } else {
      return selectPubArticleLikeInfo(kws, offset, numberOfPage, userId);
    }

  }

  @Override
  public Map<String, List<String>> selectIndicatorPubArticleInfo(Integer offset,
      Integer numberOfPage, String userId) {
    HallArtResultHandler hallArtResultHandler = new HallArtResultHandler();
    PageT page = new PageT();
    page.offset = offset;
    page.numberOfPage = numberOfPage;
    page.userId = userId;
    sqlSessionTemplate
        .select("com.ajobs.yuns.mapper.hall.HallArticleMapper.selectIndicatorPubArticleInfo", page,
            hallArtResultHandler);
    return hallArtResultHandler.getHallArtResultHandler();
  }


  @Override
  public Integer selectArtReadCount(String articleLinkName, String userId) {
    return hallArticleMapper.selectArtReadCount(articleLinkName, userId);
  }

  @Override
  public Integer insertArtReadCount(String articleLinkName, String userId, Integer number) {
    return hallArticleMapper.insertArtReadCount(articleLinkName, userId, number);
  }

  @Override
  public Integer updateArtTitleName(String articleLinkName, String userId,
      String articleTitleName) {
    return hallArticleMapper.updateArtTitleName(articleLinkName, userId, articleTitleName);
  }

  public Integer insertArtReadCount(String articleLinkName, String userId) {
    Integer integer = selectArtReadCount(articleLinkName, userId);
    if (integer == null) {
      integer = 0;
    }
    return insertArtReadCount(articleLinkName, userId, integer + 1);
  }

  class Page {

    public Integer offset;
    public Integer numberOfPage;
  }

  class PageT {

    public Integer offset;
    public Integer numberOfPage;
    public String userId;
  }

  class PageTh {

    public Integer offset;
    public Integer numberOfPage;
    public List<String> keywords;
    public String userId;
  }

//  private void initPubArticleMapper() {
//    if (hallArticleMapper == null) {
//      synchronized (this) {
//        if (hallArticleMapper == null) {
//          hallArticleMapper = sqlSessionTemplate.getMapper(HallArticleMapper.class);
//        }
//      }
//    }
//  }
}
