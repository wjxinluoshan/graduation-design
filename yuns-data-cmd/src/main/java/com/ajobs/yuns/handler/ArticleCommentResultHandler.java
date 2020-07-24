package com.ajobs.yuns.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

public class ArticleCommentResultHandler implements ResultHandler<Map<String, Object>> {

  private Map<String, List<String>> map;
  private List<String> comIds;
  private List<String> emails;
  private List<String> comments;
  private List<String> dataTimes;

  public ArticleCommentResultHandler() {
    map = new HashMap<>();
    comIds = new ArrayList<>();
    emails = new ArrayList<>();
    comments = new ArrayList<>();
    dataTimes = new ArrayList<>();
  }

  @Override
  public void handleResult(ResultContext<? extends Map<String, Object>> resultContext) {
    Map<String, Object> map = resultContext.getResultObject();
    comIds.add(map.get("comId").toString());
    emails.add(map.get("email").toString());
    comments.add(map.get("commentContent").toString());
    String dataTime = map.get("commentDataTime").toString();
    int index = dataTime.indexOf(".");
    dataTimes.add(dataTime.substring(0, index));
  }

  public Map<String, List<String>> getArticleCommentResultHandler() {
    map.put("comIds", comIds);
    map.put("emails", emails);
    map.put("comments", comments);
    map.put("dataTimes", dataTimes);
    return map;
  }
}
