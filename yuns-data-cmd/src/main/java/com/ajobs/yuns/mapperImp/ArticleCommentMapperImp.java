package com.ajobs.yuns.mapperImp;

import com.ajobs.yuns.mapper.artcom.ArticleCommentMapper;
import com.ajobs.yuns.handler.ArticleCommentResultHandler;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ArticleCommentMapperImp implements ArticleCommentMapper, DisposableBean {

  @Autowired
  @Qualifier("dbSqlSessionTemplateArt")
  private SqlSessionTemplate sqlSessionTemplate;

  @Qualifier("articleCommentMapper")
  @Autowired
  private ArticleCommentMapper articleCommentMapper;


  @Override
  public void createArticleCommentRecordTable(Integer id, String articleLinkName) {
    articleCommentMapper.createArticleCommentRecordTable(id, articleLinkName.replace(".html", ""));
  }

  @Override
  public void createArticleCommentResponseTable(Integer id, String articleLinkName) {
    articleCommentMapper
        .createArticleCommentResponseTable(id, articleLinkName.replace(".html", ""));
  }

  @Override
  public void createArticleCommentVerifyTable(Integer id, String articleLinkName) {
    articleCommentMapper.createArticleCommentVerifyTable(id, articleLinkName.replace(".html", ""));
  }

  @Override
  public void delArticleCommentRecordTable(Integer id, String articleLinkName) {
    articleCommentMapper.delArticleCommentRecordTable(id, articleLinkName);
  }

  @Override
  public void delArticleCommentResponseTable(Integer id, String articleLinkName) {
    articleCommentMapper.delArticleCommentResponseTable(id, articleLinkName);
  }

  @Override
  public void delArticleCommentVerifyTable(Integer id, String articleLinkName) {
    articleCommentMapper.delArticleCommentVerifyTable(id, articleLinkName);
  }

  public void delArticleCommentTable(Integer id, String articleLinkName) {
    articleLinkName = articleLinkName.replace(".html", "");
    delArticleCommentRecordTable(id, articleLinkName);
    delArticleCommentResponseTable(id, articleLinkName);
    delArticleCommentVerifyTable(id, articleLinkName);
  }

  @Override
  public String checkTableWhetherExisted(String tableName) {
    return articleCommentMapper.checkTableWhetherExisted(tableName);
  }

  @Override
  public Integer inertArticleRecordComment(Integer id, String articleLinkName, String comId,
      String email, String comment, Timestamp commentDataTime) {
    return articleCommentMapper
        .inertArticleRecordComment(id, articleLinkName.replace(".html", ""), comId, email, comment,
            commentDataTime);
  }

  @Override
  public Integer inertArticleResponseComment(Integer id, String articleLinkName, String rId,
      String cId) {
    return articleCommentMapper
        .inertArticleResponseComment(id, articleLinkName.replace(".html", ""), rId, cId);
  }

  @Override
  public Integer inertArticleVerifyComment(Integer id, String articleLinkName, Integer comId) {
    return articleCommentMapper
        .inertArticleVerifyComment(id, articleLinkName.replace(".html", ""), comId);

  }

  @Override
  public String articleCommentLastId(Integer id, String articleLinkName) {
    return articleCommentMapper.articleCommentLastId(id, articleLinkName.replace(".html", ""));
  }

  @Override
  public List<String> selectResponseArticleRIDs(Integer id, String articleLinkName, String comId) {
    return articleCommentMapper
        .selectResponseArticleRIDs(id, articleLinkName.replace(".html", ""), comId);
  }

  @Override
  public String selectResponseArticleCIDs(Integer id, String articleLinkName, String comId) {
    return articleCommentMapper
        .selectResponseArticleCIDs(id, articleLinkName.replace(".html", ""), comId);
  }

  @Override
  public Integer delResponseArticleComment(Integer id, String articleLinkName, String comId) {
    return articleCommentMapper
        .delResponseArticleComment(id, articleLinkName.replace(".html", ""), comId);

  }

  @Override
  public Integer delVerifyArticleComment(Integer id, String articleLinkName, String comId) {
    return articleCommentMapper
        .delVerifyArticleComment(id, articleLinkName.replace(".html", ""), comId);

  }

  @Override
  public Map<String, List<String>> selectRecordArticleComments(String id, String articleLinkName,
      Integer offset, Integer numberOfPage) {
    ArticleCommentResultHandler articleCommentResultHandler = new ArticleCommentResultHandler();
    ArticleQuery articleQuery = new ArticleQuery();
    articleQuery.id = id;
    articleQuery.articleLinkName = articleLinkName.replace(".html", "");
    articleQuery.offset = offset;
    articleQuery.numberOfPage = numberOfPage;
    sqlSessionTemplate.select(
        "com.ajobs.yuns.mapper.artcom.ArticleCommentMapper.selectRecordArticleComments",
        articleQuery,
        articleCommentResultHandler);
    return articleCommentResultHandler.getArticleCommentResultHandler();
  }

  @Override
  public Map<String, Object> selectSingleRecordArticleComment(String id, String articleLinkName,
      String comId) {
    return articleCommentMapper
        .selectSingleRecordArticleComment(id, articleLinkName.replace(".html", ""), comId);
  }

  @Override
  public List<Integer> selectVerifyArticleComments(String id, String articleLinkName,
      Integer offset,
      Integer numberOfPage) {
    return articleCommentMapper
        .selectVerifyArticleComments(id, articleLinkName.replace(".html", ""), offset,
            numberOfPage);
  }

  @Override
  public Integer selectSingleVerifyArticleComment(String id, String articleLinkName,
      Integer comId) {
    return articleCommentMapper
        .selectSingleVerifyArticleComment(id, articleLinkName.replace(".html", ""), comId);
  }

  @Override
  public Integer selectVerifyArticleLikeCommentsNumber(String id, String articleLinkName) {
    return articleCommentMapper
        .selectVerifyArticleLikeCommentsNumber(id, articleLinkName.replace(".html", ""));
  }


  class ArticleQuery {

    public String id;
    public String articleLinkName;
    public Integer offset;
    public Integer numberOfPage;

  }

  @Override
  public Integer delRecordArticleComment(Integer id, String articleLinkName, String comId) {
    return articleCommentMapper
        .delRecordArticleComment(id, articleLinkName.replace(".html", ""), comId);
  }

  public void createArticleCommentTable(Integer id, String articleLinkName) {
    createArticleCommentRecordTable(id, articleLinkName);
    createArticleCommentResponseTable(id, articleLinkName);
    createArticleCommentVerifyTable(id, articleLinkName);
  }

//  private void initArticleCommentMapper() {
//    if (articleCommentMapper == null) {
//      synchronized (this) {
//        if (articleCommentMapper == null) {
//          articleCommentMapper = sqlSessionTemplate.getMapper(ArticleCommentMapper.class);
//        }
//      }
//    }
//  }

  @Override
  public void destroy() throws Exception {
    System.out.println("---------------------------->destory");
    sqlSessionTemplate.clearCache();
    sqlSessionTemplate.close();
  }
}
