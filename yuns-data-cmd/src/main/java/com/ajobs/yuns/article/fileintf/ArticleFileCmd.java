package com.ajobs.yuns.article.fileintf;

public interface ArticleFileCmd {
  void createArticle(String userId, String title, String Content, String et);

  String articleMainContent(String userId, String articleLinkName);

  void editArticleContent(String userId, String title, String content, String articleLinkName,
      String et);
}
