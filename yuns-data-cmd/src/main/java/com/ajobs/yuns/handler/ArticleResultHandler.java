package com.ajobs.yuns.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

public class ArticleResultHandler implements ResultHandler<Map<String, String>> {

  private Map<String, List<String>> map;
  private List<String> rnames;
  private List<String> rlinks;
  private List<String> comments;
  private List<String> dataTimes;

  public ArticleResultHandler() {
    map = new HashMap<>();
    rnames = new ArrayList<>();
    rlinks = new ArrayList<>();

  }

  @Override
  public void handleResult(ResultContext<? extends Map<String, String>> resultContext) {
    Map<String, String> map = resultContext.getResultObject();
    rnames.add(map.get("article_name"));
    rlinks.add(map.get("article_link"));
  }

  public Map<String, List<String>> getArticleResultHandler() {
    map.put("rnames", rnames);
    map.put("rlinks", rlinks);
    return map;
  }
}
