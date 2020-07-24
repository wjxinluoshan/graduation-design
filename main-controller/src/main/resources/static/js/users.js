/**
 * 模糊查询用户名的模块
 */
var userNameSearchTimer = '';
var loadUserNameSearchPost = '';
var show_username_ul_id_vue = '';
let keyword = '';
$('#btn_name_id').on('click', () => {
  keyword = $('#input_username_id').val().trim();
  if (!keyword) {
    return;
  }
  createShowArea();
  if (userNameSearchTimer) {
    clearTimeout(userNameSearchTimer);
  }
  queryLikeSearch();
});
$("#input_username_id").keyup(function (event) {
  keyword = $(this).val().trim();
  if (!keyword) {
    return;
  }
  createShowArea();
  if (userNameSearchTimer) {
    clearTimeout(userNameSearchTimer);
  }
  //查询
  if (loadUserNameSearchPost) {
    loadUserNameSearchPost.abort();
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
    queryLikeSearch();
  }, 300);

});

function queryLikeSearch() {
  loadUserNameSearchPost = $.ajax({
    method: 'post',
    url: '/yuns/user/lquis',
    data: {
      kw: keyword
    },
    success: function (data) {
      loadUserNameSearchPost = '';
      if (data && data.comIds.length > 0) {
        $('#show_username_ul_id').css('display', 'block');
        show_username_ul_id_vue.unames = data.usernames;
        show_username_ul_id_vue.picUrls = data.pictureUrls;
        show_username_ul_id_vue.ids = data.comIds;
      }
    },
    error: function (err) {
      console.log(err);
      loadUserNameSearchPost = '';
    }
  });
}

function createShowArea() {
  if (!document.getElementById('show_username_ul_id')) {
    $('<ul id="show_username_ul_id" class="table-responsive shadow mt-2 " style="list-style:none;position: fixed;z-index: 1;background-color: pink;width: 100%;top:40px;max-height: 350px;overflow-y: auto;">'
        +
        '<li><div class="mt-2 mb-2">' +
        '<button type="button"style="width: 80px" class="btn btn-outline-danger"><a  style="cursor: pointer" @click="ellipse">收起</a></button></div><table class="table" style="width: 100%">'
        +
        '<tbody>' +
        '<tr v-for="(pic,index) in picUrls"><td ><img :src="pic" style="width: 230px"/></td><td >'
        +
        '<div style=" max-height: 100px;  width: 150px;  overflow: auto;"><a @click="toUserName(index)">{{unames[index]}}</a></div></td></tr></tbody>'
        +
        '</table></li></ul>').appendTo(
        $('body'));
    show_username_ul_id_vue = new Vue({
      el: '#show_username_ul_id',
      data: {
        picUrls: [],
        unames: [],
        ids: [],
      },
      methods: {
        ellipse: function () {
          $('#show_username_ul_id').css('display', 'none');
        },
        toUserName: function (index) {
          window.open('/yuns/show/showuser.html?id=' + this.ids[index], '_blank');
        }
      }
    });
  }
}



var offset = 0;
var preOffset = 0;

var numberOfPage = 10;

var clickNOrPOrSBtn = '';
var clickNextPageBtn = 'n';
var clickPrePageBtn = 'p';
var clickSearchPageBtn = 's';

const show_user_table_id_vue = new Vue({
  el: '#show_user_table_id',
  data: {
    comIds: [],
    pics: [],
    usernames: [],
    currentPageIndex: 1,
    pageNumber: ''
  },
  methods: {
    /**
     * 点击用户名前往用户主页
     * @param index
     */
    toUsername: function (index) {
      window.open('/yuns/show/showuser.html?id=' + this.comIds[index], '_blank');
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
      queryUsers();
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
      queryUsers();
    }
  }
});

function queryUsers() {
  showOrHiddenLoadingDiv(true);
  $.post('/yuns/user/infos', {
    offset: offset,
    numberOfPage: numberOfPage
  }, function (data) {
    showOrHiddenLoadingDiv(false);
    if (data && data.comIds.length > 0) {
      show_user_table_id_vue.comIds = data.comIds;
      show_user_table_id_vue.pics = data.pictureUrls;
      show_user_table_id_vue.usernames = data.usernames;
      if (clickNOrPOrSBtn === clickPrePageBtn) {
        show_user_table_id_vue.currentPageIndex--;
      } else if (clickNOrPOrSBtn === clickNextPageBtn) {
        show_user_table_id_vue.currentPageIndex++;
      } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
        show_user_table_id_vue.currentPageIndex = show_user_table_id_vue.pageNumber;
      }
    } else {
      if (clickNOrPOrSBtn === clickPrePageBtn) {
        if (show_user_table_id_vue.currentPageIndex !== 1) {
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
    if (clickNOrPOrSBtn === clickPrePageBtn) {
      if (show_user_table_id_vue.currentPageIndex !== 1) {
        offset += numberOfPage;
      }
    } else if (clickNOrPOrSBtn === clickNextPageBtn) {
      offset -= numberOfPage;
    } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
      offset = preOffset;
    }
    clickNOrPOrSBtn = '';
    console.log(e);
    showOrHiddenLoadingDiv(false);
  });
}

queryUsers();