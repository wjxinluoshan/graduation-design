package com.ajobs.yuns.mapperImp;

import com.ajobs.yuns.component.Npl;
import com.ajobs.yuns.mapper.main.ResourceNameMapper;
import java.util.ArrayList;
import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ResourceNameMapperImp implements ResourceNameMapper {

  @Qualifier("resourceNameMapper")
  @Autowired
  private ResourceNameMapper resourceNameMapper ;

//  @Autowired
//  @Qualifier("dbSqlSessionTemplate")
//  private SqlSessionTemplate sqlSessionTemplate;

  @Autowired
  private Npl npl;

//  @Autowired
//  @Qualifier("rSAPrivateKeyController")
//  private RSAPrivateKeyController rsaPrivateKeyController;


//  private void initResourceNameMapper() {
//    if (resourceNameMapper == null) {
//      synchronized (this) {
//        if (resourceNameMapper == null) {
//          resourceNameMapper = sqlSessionTemplate.getMapper(ResourceNameMapper.class);
//        }
//      }
//    }
//  }

  @Override
  public Integer insert(Integer id, String fieldName, String value) {
    return resourceNameMapper.insert(id, fieldName, value);
  }

  @Override
  public List<String> selectResourceName(Integer id, String fieldName, List<String> kws,
      Integer offset, Integer numberOfPage) {
    return resourceNameMapper.selectResourceName(id, fieldName, kws, offset, numberOfPage);
  }

  public List<String> selectResourceName(Integer id, String fieldName, String kw, Integer offset,
      Integer numberOfPage) {
    List<String> kws = null;
    if (kw != null) {
      try {
        List<String> tempL;
        switch (fieldName) {
          case "pic_name":
            tempL = npl.queryUserDocument(kw, id, npl.PICTURE, offset + numberOfPage).get(1);
            break;
          case "doc_name":
            tempL = npl.queryUserDocument(kw, id, npl.DOC, offset + numberOfPage).get(1);
            break;
          case "resource_name":
            tempL = npl.queryUserDocument(kw, id, npl.RES, offset + numberOfPage).get(1);
            break;
          default:
            throw new IllegalStateException("Unexpected value: " + fieldName);
        }
        if (tempL.size() > 0) {
          kws = new ArrayList<>();
          for (int i = offset; i < offset + numberOfPage; i++) {
            if (i >= tempL.size()) {
              break;
            }
            kws.add(tempL.get(i));
          }
        }
      } catch (Exception e) {
        //e.printStackTrace();
      }
    }
      if (kws != null && kws.size() > 0) {
//      return resourceNameMapper.selectResourceName(id, fieldName, kws, null, null);
        return kws;
      } else {
      return resourceNameMapper.selectResourceName(id, fieldName, kws, offset, numberOfPage);
    }

  }

  @Override
  public Integer delResourceName(Integer id, String fieldName, String value) {
    return resourceNameMapper.delResourceName(id, fieldName, value);
  }


}
