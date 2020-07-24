package com.ajobs.yuns.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

public class HallResourceResultHandler implements ResultHandler<Map<String, Object>> {

  private Map<String, List<String>> map;
  private List<String> resNames;
  private List<String> resUrls;
  private List<String> ids;
  private List<String> downloads;

  private String name;
  private String url;
  private String id = "userId";
  private String download = "downloadCount";

  public HallResourceResultHandler(String resType) {
    map = new HashMap<>();
    resNames = new ArrayList<>();
    resUrls = new ArrayList<>();
    ids = new ArrayList<>();
    downloads = new ArrayList<>();
    switch (resType) {
      case "pic":
        name = "picName";
        url = "picUrl";
        break;
      case "doc":
        name = "docName";
        url = "docUrl";
        break;
      case "ores":
        name = "oresName";
        url = "oresUrl";
        break;
    }
  }

  @Override
  public void handleResult(ResultContext<? extends Map<String, Object>> resultContext) {
    Map<String, Object> map = resultContext.getResultObject();
    resNames.add((String) map.get(name));
    resUrls.add((String) map.get(url));
    ids.add((String) map.get(id));
    if (map.get(download) == null) {
      downloads.add("0");
    } else {
      downloads.add( map.get(download).toString());
    }
  }

  public Map<String, List<String>> getHallResourceResultHandler() {
    map.put("resNames", resNames);
    map.put("resUrls", resUrls);
    map.put("ids", ids);
    map.put("downloads", downloads);
    return map;
  }
}
