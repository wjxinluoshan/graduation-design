/**
 * 模糊查询用户名的模块
 */
var userNameSearchTimer = '';
var loadUserNameSearchPost = '';
var show_username_ul_id_vue = '';

var likeQOffset = 0;
var likeQNumberOfPage = 8;
var clickBtn = '';
var clickNBtn = 'n';
var clickPBtn = 'p';

var keyword = '';
$('#btn_name_id').on('click', () => {
  keyword = $('#input_artname_id').val().trim();
  if (!keyword) {
    return;
  }
  createShowArea();
  //查询
  if (loadUserNameSearchPost) {
    loadUserNameSearchPost.abort();
  }
  if (userNameSearchTimer) {
    clearTimeout(userNameSearchTimer);
  }
  likeQOffset = 0;
  queryLikeArt();
});
$("#input_artname_id").keyup(function (event) {
  keyword = $(this).val().trim();
  if (!keyword) {
    return;
  }
  createShowArea();
  if (userNameSearchTimer) {
    clearTimeout(userNameSearchTimer);
  }
  /**
   * 0.3s的反应响应时间
   * @type {number}
   */
  userNameSearchTimer = setTimeout(function () {
    //查询
    if (loadUserNameSearchPost) {
      loadUserNameSearchPost.abort();
    }
    likeQOffset = 0;
    queryLikeArt();
  }, 300);

});

function createShowArea() {
  if (!document.getElementById('show_username_ul_id')) {
    $('<ul id="show_username_ul_id" class="shadow"style="display:none;list-style:none;position: fixed;z-index: 1;background-color: pink;width: 100%;top:50px;max-height: 350px;overflow-y: auto;">'
        +
        '<li> <div class="mt-1"><button type="button"style="width: 80px" class="btn btn-outline-danger"><a  style="cursor: pointer" @click="ellipse">收起</a></button></div>'
        +
        '<div class="mt-2 mb-2" v-if="artNames.length>0"><button type="button"style="width: 80px" class="btn btn-info" @click="flyPage(-1)">上一页</button>\n'
        + '      <button type="button" style="width: 80px"class="btn btn-info" @click="flyPage(1)">下一页</button></div>'
        +
        '<table class="table"style="width: 100%">' +
        '<tbody>' +
        '<tr v-for="(artn,index) in artNames">' +
        '<td ><div style=" max-height: 100px;  width: 150px;  overflow: auto;"><a @click="readArt(index)">{{artn}}</a></div></td></tr>'
        +
        '</tbody>' +
        '</table></li></ul>').appendTo(
        $('body'));
    show_username_ul_id_vue = new Vue({
      el: '#show_username_ul_id',
      data: {
        artUrls: [],
        artNames: [],
        ids: [],
        artLinkNames: [],
      },
      methods: {
        ellipse: function () {
          $('#show_username_ul_id').css('display', 'none');
        },
        readArt: function (index) {
          $.post('/yuns/pubart/iartrc',
              {al: this.artLinkNames[index], id: this.ids[index]},
              function (data) {
                window.open(show_username_ul_id_vue.artUrls[index], '_blank');
              }).fail(function (e) {
            console.log(e);
            window.open(show_username_ul_id_vue.artUrls[index], '_blank');
          });
        },
        flyPage: function (index) {
          if (index === 1) {
            clickBtn = clickNBtn;
            likeQOffset += likeQNumberOfPage;
          } else {
            if (likeQOffset > 0) {
              clickBtn = clickPBtn;
              likeQOffset -= likeQNumberOfPage;
            } else {
              return;
            }
          }
          queryLikeArt();
        }
      }
    });
  }
}

function queryLikeArt() {
  loadUserNameSearchPost = $.ajax({
    method: 'post',
    url: '/yuns/pubart/sartis',
    data: {
      kw: keyword,
      offset: likeQOffset,
      numberOfPage: likeQNumberOfPage,
      id: id
    },
    success: function (data) {
      loadUserNameSearchPost = '';
      if (data && data.resNames.length > 0) {
        $('#show_username_ul_id').css('display', 'block');
        show_username_ul_id_vue.artUrls = data.resUrls;
        show_username_ul_id_vue.artNames = data.resTitleNames;
        show_username_ul_id_vue.artLinkNames = data.resNames;
        show_username_ul_id_vue.ids = data.ids;
      } else {
        if (clickBtn === clickNBtn) {
          if (likeQOffset > 0) {
            likeQOffset -= likeQNumberOfPage;
          }
        } else if (clickBtn === clickPBtn) {
          likeQOffset += likeQNumberOfPage;
        }
        clickBtn = '';
      }
    },
    error: function (err) {
      console.log(err);
      loadUserNameSearchPost = '';
      if (clickBtn === clickNBtn) {
        if (likeQOffset > 0) {
          likeQOffset -= likeQNumberOfPage;
        }
      } else if (clickBtn === clickPBtn) {
        likeQOffset += likeQNumberOfPage;
      }
      clickBtn = '';
    }
  });
}

var id = window.location.href.split("=")[1];

var offset = 0;
var preOffset = 0;
var numberOfPage = 10;

var clickNOrPOrSBtn = '';
var clickNextPageBtn = 'n';
var clickPrePageBtn = 'p';
var clickSearchPageBtn = 's';

const show_res_table_id_vue = new Vue({
  el: '#show_res_table_id',
  data: {
    resTitleNames: [],
    resUrls: [],
    resLinkNames: [],
    currentPageIndex: 1,
    pageNumber: '',
    reads: []
  },
  methods: {
    gotoRes: function (index) {
      $.post('/yuns/pubart/iartrc', {al: this.resLinkNames[index], id: id},
          function (data) {
            show_res_table_id_vue.reads[index] = show_res_table_id_vue.reads[index]
                + 1;
            let arr = show_res_table_id_vue.reads;
            show_res_table_id_vue.reads = [];
            show_res_table_id_vue.reads = arr;
            window.open(show_res_table_id_vue.resUrls[index], '_blank');
          }).fail(function (e) {
        console.log(e);
        window.open(show_res_table_id_vue.resUrls[index], '_blank');
      })

    },
    indicatorCurPAge: function () {
      if (parseInt(this.pageNumber) % 1 !== 0) {
        alert('输入不合法!!!');
        return;
      }
      this.pageNumber = parseInt(this.pageNumber);
      if (this.pageNumber < 1) {
        alert('请输入大于0的整数!!!');
        return;
      }
      preOffset = offset;
      //whhen this.pageNumber is 1,the offset equal 0;
      //one by one this.pageNumber == 2,the offset equal is numberOfPage;
      offset = numberOfPage * (this.pageNumber - 1);
      clickNOrPOrSBtn = clickSearchPageBtn;
      queryPubResource({offset: offset, numberOfPage: numberOfPage, id: id});
    },
    flyPage: function (index) {
      if (loadingDivIsShowing) {
        return;
      }
      if (index === -1) {
        if (this.currentPageIndex === 1) {
          return;
        }
        clickNOrPOrSBtn = clickPrePageBtn;
        offset -= numberOfPage;
      } else {
        offset += numberOfPage;
        clickNOrPOrSBtn = clickNextPageBtn;
      }
      queryPubResource({offset: offset, numberOfPage: numberOfPage, id: id});
    }
  }
});

function queryPubResource(data) {
  showOrHiddenLoadingDiv(true);
  $.post('/yuns/pubart/sartis', data, function (data) {
    showOrHiddenLoadingDiv(false);
    if (data && data.ids.length > 0) {
      show_res_table_id_vue.resTitleNames = data.resTitleNames;
      show_res_table_id_vue.resUrls = data.resUrls;
      show_res_table_id_vue.resLinkNames = data.resNames;
      for (let i = 0; i < data.reads.length; i++) {
        data.reads[i] = parseInt(data.reads[i]);
      }
      show_res_table_id_vue.reads = data.reads;
      if (clickNOrPOrSBtn === clickPrePageBtn) {
        show_res_table_id_vue.currentPageIndex--;
      } else if (clickNOrPOrSBtn === clickNextPageBtn) {
        show_res_table_id_vue.currentPageIndex++;
      } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
        show_res_table_id_vue.currentPageIndex = show_res_table_id_vue.pageNumber;
      }
    } else {
      if (clickNOrPOrSBtn === clickPrePageBtn) {
        if (show_res_table_id_vue.currentPageIndex !== 1) {
          offset += numberOfPage;
        }
      } else if (clickNOrPOrSBtn === clickNextPageBtn) {
        offset -= numberOfPage;
      } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
        offset = preOffset;
      }
    }
    clickNOrPOrSBtn = '';
  }).fail(function (e) {
    console.log(e);
    showOrHiddenLoadingDiv(false);
    if (clickNOrPOrSBtn === clickPrePageBtn) {
      if (show_res_table_id_vue.currentPageIndex !== 1) {
        offset += numberOfPage;
      }
    } else if (clickNOrPOrSBtn === clickNextPageBtn) {
      offset -= numberOfPage;
    } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
      offset = preOffset;
    }
    clickNOrPOrSBtn = '';
  })
}

queryPubResource({offset: offset, numberOfPage: numberOfPage, id: id});