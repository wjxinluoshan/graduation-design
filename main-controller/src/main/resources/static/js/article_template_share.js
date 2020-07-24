window.onscroll = function () {
  /*获取距离页面顶部的距离*/
  let toTop = document.documentElement.scrollTop || document.body.scrollTop;
  /*div距离顶部的距离
   $("#test").offset().top;*/
  if (toTop > 300) {
    $('#top_ul_id').stop();
    $('#top_ul_id').animate({
      opacity: 1
    }, 100);
  } else {
    $('#top_ul_id').stop();
    $('#top_ul_id').animate({
      opacity: 0
    }, 100);
  }
};
var arr = window.location.href.split("//")[1];
arr = arr.split("/");
var id = arr[3];
var articleLinkName = arr[5];
var offset = 0;
var numberOfPage = 5;
//history.replaceState({}, "", "/yuns/article/" + articleLinkName);

var clickNextPageBtn = 'n_btn';
var clickPrePageBtn = 'p_btn';
var clickFlyPageBtn = '';
const show_comment_area_div_vue = new Vue({
  el: '#show_comment_area_div',
  data: {
    commentsNumber: 0,
    /**
     *    被评论区
     */

    byCommentedDivIsShowing: 'none',
    previousCommentedUserId: '',
    comment1: '',
    /**
     * 评论
     */

    comment2: '',
    email: '',

    /**
     * 评论显示区
     */
    commentAreaIsShow: 'none',
    emails: [],
    dateTimes: [],
    comments: [],
    comIds: [],
    byComEmails: [],
    byComments: [],
    rIds: [],

    cId: '',

    currentPageIndex: 1
  },
  methods: {
    flyPage: function (index) {
      if (index === -1) {
        if (this.currentPageIndex === 1) {
          return;
        }
        offset -= numberOfPage;
        clickFlyPageBtn = clickPrePageBtn;
      } else {
        offset += numberOfPage;
        clickFlyPageBtn = clickNextPageBtn;
      }
      queryComment();
    },
    reply: function (index) {
      this.cId = this.comIds[index];
      this.byCommentedDivIsShowing = 'block';
      this.previousCommentedUserId = this.emails[index];
      this.comment1 = this.comments[index];
      if (!document.getElementById('to_replay_area_a_id')) {
        $('<a id="to_replay_area_a_id" href="#replay_area_div_id" style="display: none"></a>').appendTo(
            $('body'));
      }
      document.getElementById('to_replay_area_a_id').click();
    },
    cancelCommentedDiv: function () {
      this.byCommentedDivIsShowing = 'none';
      this.comment1 = '';
      this.comment2 = '';
      this.email = '';
    },
    publish: function () {
      if (loadingDivIsShowing) {
        return
      }
      let tempComment2 = this.comment2.trim();
      tempComment2 = tempComment2.replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>').replace(/\s/g,'&nbsp;');
      this.email = this.email.trim();
      if (!tempComment2) {
        alertShow('评论不能为空!!!');
        return;
      }
      if (tempComment2.length > 300) {
        alertShow('评论最多300字!!!');
        return;
      }
      /**
       * 检邮箱格式
       */

      let reg = new RegExp(
          "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
      /**
       *     邮箱输入不合法
       */

      if (!reg.test(this.email)) {
        alertShow('邮箱输入不合法!!!');
        return;
      }

      if (this.email.length > 50) {
        alertShow('邮箱长度限制为50!!!');
        return;
      }

      let data = '';
      if (this.byCommentedDivIsShowing !== 'none') {
        data = {
          id: id,
          articleLinkName: articleLinkName,
          email: this.email,
          comment: tempComment2,
          cId: this.cId
        }
      } else {
        data = {
          id: id,
          articleLinkName: articleLinkName,
          email: this.email,
          comment: tempComment2
        }
      }
      showOrHiddenLoadingDiv(true);
      /**
       * 发布评论
       */
      $.post('/yuns/artcomc/rec', data, function (data) {
        showOrHiddenLoadingDiv(false);
        if (data === '0') {
          alertShow('评论上传失败!!!');
        } else {
          alertShow('评论等待被验证!!!');
          show_comment_area_div_vue.cancelCommentedDiv();
        }
      }).fail(function (e) {
        console.log(e);
        alertShow('评论上传失败!!!');
        showOrHiddenLoadingDiv(false);
      });
    },
    refreshCom: function () {
      queryCommentNumber();
      queryComment();
    }
  }
});
function queryComment() {
  $.post('/yuns/artcomc/qver', {
    id: id,
    articleLinkName: articleLinkName,
    offset: offset,
    numberOfPage: numberOfPage
  }, function (data) {
    if (data && data.comIds.length > 0) {
      show_comment_area_div_vue.byComEmails = [];
      show_comment_area_div_vue.byComments = [];
      if (clickFlyPageBtn === clickNextPageBtn) {
        show_comment_area_div_vue.currentPageIndex++;
      } else if (clickFlyPageBtn === clickPrePageBtn) {
        show_comment_area_div_vue.currentPageIndex--;
      }
      clickFlyPageBtn = '';
      show_comment_area_div_vue.comIds = data.comIds;
      show_comment_area_div_vue.emails = [];
      data.emails.forEach(function (value) {
        show_comment_area_div_vue.emails.push(value.substr(0,
            parseInt(value.length * 0.6)));
      });
      show_comment_area_div_vue.comments = data.comments;
      show_comment_area_div_vue.dateTimes = data.dateTimes;
      show_comment_area_div_vue.commentAreaIsShow = 'block';
      show_comment_area_div_vue.rIds = data.rIds;
      let formData = new FormData();
      if (data.cIds.length > 0) {
        formData.append('id', id);
        formData.append('articleLinkName', articleLinkName);
        data.cIds.forEach(function (value) {
          formData.append('comIds', value);
        });
        $.ajax({
          type: "POST",
          url: '/yuns/artcomc/qrec2',
          data: formData,
          contentType: false, /*禁止设置请求类型*/
          processData: false, /*禁止jquery对DAta数据的处理,默认会处理*/
          success: function (data) {
            let emails = data.emails;
            let comments = data.comments;
            let tempAttO = [];
            let tempArrT = [];
            outer:
                for (let index = 0; index < show_comment_area_div_vue.comIds.length; index++) {
                  for (let i = 0; i < show_comment_area_div_vue.rIds.length; i++) {
                    if (show_comment_area_div_vue.rIds[i]
                        === show_comment_area_div_vue.comIds[index]) {
                      tempAttO.push(emails[i].substr(0, parseInt(emails[i].length * 0.6)));
                      tempArrT.push(comments[i]);
                      continue outer;
                    }
                  }
                  tempAttO.push('');
                  tempArrT.push('');
                }
            show_comment_area_div_vue.byComEmails = tempAttO;
            show_comment_area_div_vue.byComments = tempArrT;
          },
          error: function (e) {
          }
        });
      }
    } else {
      if (!clickFlyPageBtn) {
        show_comment_area_div_vue.commentAreaIsShow = 'none';
      }
      if (clickFlyPageBtn === clickNextPageBtn) {
        offset -= numberOfPage;
      } else if (clickFlyPageBtn === clickPrePageBtn) {
        offset += numberOfPage;
      }
      clickFlyPageBtn = '';
    }
  }).fail(function (e) {
    console.log(e);
    alertShow('评论加载失败!!!');
  });
}

queryComment();

function queryCommentNumber(){
  $.post('/yuns/artcomc/vernum', {
    id: id,
    articleLinkName: articleLinkName
  }, function (data) {
    if (parseInt(data) > 999) {
      show_comment_area_div_vue.commentsNumber = '999+ ';
    } else {
      show_comment_area_div_vue.commentsNumber = data;
    }
  }).fail(function (e) {
    console.log(e);
  });
}
queryCommentNumber();
