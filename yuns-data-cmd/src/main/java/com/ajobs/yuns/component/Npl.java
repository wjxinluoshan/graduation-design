package com.ajobs.yuns.component;

import com.ajobs.yuns.analyzer.MyIKAnalyzer;
import com.ajobs.yuns.tool.FileHelper;
import com.ajobs.yuns.tool.FileHelper.Companion;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 自然语言处理: 中文单字分（缺点）；
 */
@Component
public class Npl {

  // 0.创建分析器
  private MyIKAnalyzer analyzer = new MyIKAnalyzer();

  public final String ARTICLE = "art";
  public final String PICTURE = "pic";
  public final String DOC = "doc";
  public final String RES = "res";

  private ReentrantLock reentrantLock = new ReentrantLock();

  @Autowired
  private ExecutorService executorService;

  public void createHallDocument(List<String> datas, List<String> tags, String type)
      throws Exception {
    Directory directory = FSDirectory
        .open(
            new File(FileHelper.Companion.getClasspath(), "indexDir/hall/" + type)
                .toPath());
    reentrantLock.lock();
    IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
    createDocument(indexWriter, datas, tags, type);
    reentrantLock.unlock();
  }

  public void createUserDocument(List<String> datas, List<String> tags, String id, String type)
      throws Exception {
    Directory directory = FSDirectory
        .open(
            new File(FileHelper.Companion.getClasspath(), "indexDir/user/" + id + "/" + type)
                .toPath());
    reentrantLock.lock();
    IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
    createDocument(indexWriter, datas, tags, type);
    reentrantLock.unlock();
  }

  private void createDocument(IndexWriter indexWriter, List<String> datas, List<String> tags,
      String type) {
    try {
      ArrayList<Future<Integer>> futures = new ArrayList<>(datas.size());
      for (String data : datas) {
        futures.add(executorService.submit(() -> {
          Document document = new Document();
          switch (type) {
            case ARTICLE:
              document.add(new TextField("artTitle", data, Store.YES));
              document
                  .add(new StringField("artTitleLink", tags.get(datas.indexOf(data)), Store.YES));
              break;
            case PICTURE:
              document.add(new TextField("picName", data, Store.YES));
              document
                  .add(new StringField("picNameLink", tags.get(datas.indexOf(data)), Store.YES));

              break;
            case DOC:
              document.add(new TextField("docName", data, Store.YES));
              document
                  .add(new StringField("docNameLink", tags.get(datas.indexOf(data)), Store.YES));
              break;
            case RES:
              document.add(new TextField("resName", data, Store.YES));
              document
                  .add(new StringField("resNameLink", tags.get(datas.indexOf(data)), Store.YES));
              break;
          }
          try {
            indexWriter.addDocument(document);
          } catch (IOException e) {
          }
        }, 1));
      }
      for (Future<Integer> future : futures) {
        future.get();
      }
    } catch (Exception e) {
    } finally {
      try {
        indexWriter.close();
      } catch (IOException e) {
      }
    }
  }


  public List<List<String>> queryUserDocument(String kw, Integer id, String type,
      Integer queryLength)
      throws Exception {
    reentrantLock.lock();
    IndexReader reader = DirectoryReader.open(FSDirectory.open(
        new File(FileHelper.Companion.getClasspath(), "indexDir/user/" + id + "/" + type).toPath()
    ));
    try {
      return queryDocument(reader, kw, type, queryLength, null);
    } finally {
      reentrantLock.unlock();
    }
  }

  public List<List<String>> queryHallDocument(String kw, String id, String type,
      Integer queryLength)
      throws Exception {
    reentrantLock.lock();
    IndexReader reader = DirectoryReader.open(FSDirectory.open(
        new File(FileHelper.Companion.getClasspath(), "indexDir/hall/" + type).toPath()
    ));
    try {
      if (id == null) {
        return queryDocument(reader, kw, type, queryLength, null);
      } else {
        return queryDocument(reader, kw, type, Integer.MAX_VALUE, id);
      }
    } finally {
      reentrantLock.unlock();
    }

  }


  private List<List<String>> queryDocument(IndexReader indexReader, String kw, String type,
      Integer queryLength, String id) {
    List<String> fileNames = new ArrayList<>();
    List<String> datas = new ArrayList<>();
    try {
      String fieldName = "";
      String queryFieldName = "";
      switch (type) {
        case ARTICLE:
          fieldName = "artTitle";
          queryFieldName = "artTitleLink";
          break;
        case PICTURE:
          fieldName = "picName";
          queryFieldName = "picNameLink";
          break;
        case DOC:
          fieldName = "docName";
          queryFieldName = "docNameLink";
          break;
        case RES:
          fieldName = "resName";
          queryFieldName = "resNameLink";
          break;
      }
      // 2. query queryText
      Query q = new QueryParser(fieldName, analyzer).parse(kw);

      // 3. 开始查询
      IndexSearcher searcher = new IndexSearcher(indexReader);
      TopDocs docs = searcher.search(q, queryLength);
      ScoreDoc[] hits = docs.scoreDocs;
      // 4.读取资源信息
      for (int i = 0; i < hits.length; ++i) {
        int docId = hits[i].doc;
        Document d = searcher.doc(docId);
        if (id != null) {
          if (d.get(queryFieldName).contains("/yuns/user/" + id + "/article/")) {
            datas.add(d.get(queryFieldName));
            fileNames.add(d.get(fieldName));
          }
        } else {
          datas.add(d.get(queryFieldName));
          fileNames.add(d.get(fieldName));
        }

      }
    } catch (Exception e) {
      //e.printStackTrace();
    } finally {
      try {
        indexReader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    List<List<String>> lists = new ArrayList<>();
    //添加资源名称
    lists.add(fileNames);
    //添加资源url
    lists.add(datas);
    return lists;
  }

  public void deleteOrUpdateUserDocument(List<String> tags, String id, String type,
      boolean isUpdate, String... args) throws Exception {
    Directory directory = FSDirectory.open(
        new File(FileHelper.Companion.getClasspath(), "indexDir/user/" + id + "/" + type).toPath());
    reentrantLock.lock();
    IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
    deleteOrUpdateDocument(indexWriter, tags, type, isUpdate, args);
    reentrantLock.unlock();
  }

  public void deleteOrUpdateHallDocument(List<String> tags, String type,
      boolean isUpdate, String... args) throws Exception {
    Directory directory = FSDirectory.open(
        new File(FileHelper.Companion.getClasspath(), "indexDir/hall/" + type).toPath());
    reentrantLock.lock();
    IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
    deleteOrUpdateDocument(indexWriter, tags, type, isUpdate, args);
    reentrantLock.unlock();
  }


  private void deleteOrUpdateDocument(IndexWriter indexWriter, List<String> tags, String type,
      boolean isUpdate, String[] args) {
    try {
      String queryFieldName = "";
      switch (type) {
        case ARTICLE:
          queryFieldName = "artTitleLink";
          break;
        case PICTURE:
          queryFieldName = "picNameLink";
          break;
        case DOC:
          queryFieldName = "docNameLink";
          break;
        case RES:
          queryFieldName = "resNameLink";
          break;
      }
      ArrayList<Future<Integer>> futures = new ArrayList<>(tags.size());
      for (String tag : tags) {
        String finalQueryFieldName = queryFieldName;
        futures.add(executorService.submit(() -> {
          try {
            indexWriter.deleteDocuments(new Term(finalQueryFieldName, tag));
            if (isUpdate) {
              Document document = new Document();
              document.add(new TextField("artTitle", args[0], Store.YES));
              document.add(new StringField("artTitleLink", args[1], Store.YES));
              indexWriter.addDocument(document);
            }
          } catch (Exception e) {
            //e.printStackTrace();
          }
        }, 1));
      }
      for (Future<Integer> future : futures) {
        future.get();
      }
    } catch (Exception e) {
    } finally {
      try {
        indexWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }


  private List<String> participleKeyword(String kw) throws Exception {
//    initFieldValue();
    TokenStream tokenStream = analyzer.tokenStream("content", kw);
    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();
    List<String> pWords = new ArrayList<>();
    while (tokenStream.incrementToken()) {
      pWords.add(charTermAttribute.toString());
    }
    tokenStream.close();
    /**
     *将分词结果的长度降序
     */
    pWords.sort((t1, t2) -> {
      if (t1.length() == t2.length()) {
        return 0;
      } else if (t1.length() > t2.length()) {
        return -1;
      } else {
        return 1;
      }
    });
    return pWords;
  }

//  private void initFieldValue() {
//    if (analyzer == null) {
//      synchronized (this) {
//        if (analyzer == null) {
//          analyzer = new MyIKAnalyzer();
//        }
//      }
//    }
//  }

}
