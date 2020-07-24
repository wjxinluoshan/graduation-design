package com.ajobs.yuns.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class MyIKAnalyzer extends Analyzer {

  private boolean useSmart;

  public boolean useSmart() {
    return this.useSmart;
  }

  public void setUseSmart(boolean useSmart) {
    this.useSmart = useSmart;
  }

  public MyIKAnalyzer() {
    this(false);
  }

  public MyIKAnalyzer(boolean useSmart) {
    this.useSmart = useSmart;
  }

//  protected TokenStreamComponents createComponents(String fieldName, Reader in) {
//    Tokenizer _IKTokenizer = new IKTokenizer(in, this.useSmart());
//    return new TokenStreamComponents(_IKTokenizer);
//  }

  @Override
  protected TokenStreamComponents createComponents(String s) {
    Tokenizer _IKTokenizer = new MyIKTokenizer(this.useSmart());
    return new TokenStreamComponents(_IKTokenizer);
  }
}
