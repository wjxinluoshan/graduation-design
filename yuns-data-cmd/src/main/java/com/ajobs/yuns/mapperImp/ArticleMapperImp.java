package com.ajobs.yuns.mapperImp;

import com.ajobs.yuns.component.Npl;
import com.ajobs.yuns.handler.ArticleResultHandler;
import com.ajobs.yuns.mapper.main.ArticleMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ArticleMapperImp implements ArticleMapper {

  @Qualifier("articleMapper")
  @Autowired
  private ArticleMapper articleMapper;

  @Autowired
  @Qualifier("dbSqlSessionTemplate")
  private SqlSessionTemplate sqlSessionTemplate;

  @Autowired
  private Npl npl;

  @Override
  public Integer createArticleTable(Integer id) {
    return articleMapper.createArticleTable(id);
  }

  @Override
  public String selectSingleRecordArticleLink(Integer id, String articleLink) {
    return articleMapper.selectSingleRecordArticleLink(id, articleLink);
  }

  @Override
  public String selectSingleArticleNameUseLink(Integer id, String articleLink) {
    return articleMapper.selectSingleArticleNameUseLink(id, articleLink);
  }


  class Page {

    public Integer id;
    public List<String> kws = null;
    public Integer offset;
    public Integer numberOfPage;
  }

  @Override
  public Integer delArticleLink(Integer id, String articleLink) {
    return articleMapper.delArticleLink(id, articleLink);
  }

  @Override
  public Integer updateArticleLinkAndName(Integer id, String articleLink, String articleName) {
    return articleMapper.updateArticleLinkAndName(id, articleLink, articleName);
  }

  @Override
  public Integer insertArticleLinkAndName(Integer id, String articleLink, String articleName) {
    return articleMapper.insertArticleLinkAndName(id, articleLink, articleName);
  }

  @Override
  public Map<String, List<String>> selectArtLinkAndName(Integer id, List<String> kws,
      Integer offset, Integer numberOfPage) {
    Page page = new Page();
    page.id = id;
    page.kws = kws;
    page.offset = offset;
    page.numberOfPage = numberOfPage;
    ArticleResultHandler articleResultHandler = new ArticleResultHandler();
    sqlSessionTemplate.select("com.ajobs.yuns.mapper.main.ArticleMapper.selectArtLinkAndName", page,
        articleResultHandler);
    return articleResultHandler.getArticleResultHandler();
  }

  public Map<String, List<String>> selectArtLinkAndName(Integer id, String kw, Integer offset,
      Integer numberOfPage) {
    List<List<String>> kws = null;
    if (kw != null) {
      try {
        List<List<String>> tempL = npl
            .queryUserDocument(kw, id, npl.ARTICLE, offset + numberOfPage);
        if (tempL.size() > 0) {
          kws = new ArrayList<>();
          List<String> fileNames = new ArrayList<>();
          List<String> datas = new ArrayList<>();
          for (int i = offset; i < offset + numberOfPage; i++) {
            if (i >= tempL.get(0).size()) {
              break;
            }
            fileNames.add(tempL.get(0).get(i));
            datas.add(tempL.get(1).get(i));
          }
          if (!fileNames.isEmpty()) {
            kws.add(fileNames);
            kws.add(datas);
          }
        }
      } catch (Exception e) {
//        e.printStackTrace();
      }

    }
    if (kws != null && kws.size() > 0) {
      Map<String, List<String>> map = new HashMap<>();
      map.put("rnames", kws.get(0));
      map.put("rlinks", kws.get(1));
      return map;
    } else {
      return selectArtLinkAndName(id, (List<String>) null, offset, numberOfPage);
    }
  }

//  private void initArticleMapper() {
//    if (articleMapper == null) {
//      synchronized (this) {
//        if (articleMapper == null) {
//          articleMapper = sqlSessionTemplate.getMapper(ArticleMapper.class);
//        }
//      }
//    }
//  }

}
