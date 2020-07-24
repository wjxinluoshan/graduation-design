package com.ajobs.yuns.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

public class UserInfoResultHandler implements ResultHandler<Map<String, Object>> {

  private Map<String, List<String>> map;
  private List<String> comIds;
  private List<String> usernames;
  private List<String> pictureUrls;

  public UserInfoResultHandler() {
    map = new HashMap<>();
    comIds = new ArrayList<>();
    usernames = new ArrayList<>();
    pictureUrls = new ArrayList<>();
  }

  @Override
  public void handleResult(ResultContext<? extends Map<String, Object>> resultContext) {
    Map<String, Object> map = resultContext.getResultObject();
    comIds.add(map.get("id").toString());
    usernames.add(map.get("username").toString());
    pictureUrls.add(map.get("pictureUrl").toString());
  }

  public Map<String, List<String>> geUserInfoResultHandler() {
    map.put("comIds", comIds);
    map.put("usernames", usernames);
    map.put("pictureUrls", pictureUrls);
    return map;
  }
}
