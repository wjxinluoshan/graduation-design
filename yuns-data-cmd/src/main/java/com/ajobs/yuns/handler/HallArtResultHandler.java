package com.ajobs.yuns.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

public class HallArtResultHandler implements ResultHandler<Map<String, Object>> {

  private Map<String, List<String>> map;
  private List<String> resNames;
  private List<String> resUrls;
  private List<String> resTitleNames;
  private List<String> ids;
  private List<String> reads;

  public HallArtResultHandler() {
    map = new HashMap<>();
    resNames = new ArrayList<>();
    resUrls = new ArrayList<>();
    resTitleNames = new ArrayList<>();
    ids = new ArrayList<>();
    reads = new ArrayList<>();
  }

  @Override
  public void handleResult(ResultContext<? extends Map<String, Object>> resultContext) {
    Map<String, Object> map = resultContext.getResultObject();
    resNames.add((String) map.get("articleLinkName"));
    resUrls.add((String) map.get("articleLink"));
    resTitleNames.add((String) map.get("articleTitleName"));
    ids.add((String) map.get("userId"));
    if (map.get("readCount") == null) {
      reads.add("0");
    } else {
      reads.add(map.get("readCount").toString());
    }
  }

  public Map<String, List<String>> getHallArtResultHandler() {
    map.put("resNames", resNames);
    map.put("resUrls", resUrls);
    map.put("resTitleNames", resTitleNames);
    map.put("ids", ids);
    map.put("reads", reads);
    return map;
  }
}
