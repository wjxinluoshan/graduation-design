var arr = window.location.href.split('&');
var id = arr[0].split('=')[1];
var articleLinkName = arr[1].split('=')[1];
var numberOfPage = 4;
var offset = 0;
var preOffset = 0;
// history.replaceState({}, "", "/yuns/art_comment");

var clickNextPageBtn = 'n';
var clickPrePageBtn = 'p';
var clickSearchPageBtn = 'sear';
var clickNOrPOrSBtn = '';

function checkCommentsWhetherVerify() {
  let formData = new FormData();
  formData.append('id', id);
  formData.append('articleLinkName', articleLinkName);
  for (let comId of show_comments_table_id_vue.comIds) {
    formData.append('comIds', comId);
  }
  $.ajax({
    type: "POST",
    url: '/yuns/artcomc/cver',
    data: formData,
    contentType: false, //禁止设置请求类型
    processData: false, //禁止jquery对DAta数据的处理,默认会处理
    success: function (data) {
      show_comments_table_id_vue.passText = [];
      if (data && data.length > 0) {
        for (let d of data) {
          if (d === '0') {
            show_comments_table_id_vue.passText.push('通过');
          } else {
            show_comments_table_id_vue.passText.push('已通过');
          }
        }
        return;
      } else {
        for (let i = 0; i < show_comments_table_id_vue.comIds.length; i++) {
          show_comments_table_id_vue.passText.push('通过');
        }
      }
    },
    error: function (e) {
      console.log(e);
      show_comments_table_id_vue.passText = [];
      for (let i = 0; i < show_comments_table_id_vue.comIds.length; i++) {
        show_comments_table_id_vue.passText.push('通过');
      }
    }
  });
}

function queryRecordCommentCmd(cmd) {
  showOrHiddenLoadingDiv(true);
  $.ajax({
    type: 'post',
    url: '/yuns/artcomc/qrec',
    data: {
      id: id,
      articleLinkName: articleLinkName,
      offset: offset,
      numberOfPage: numberOfPage
    },
    success: function (data) {
      if (data && data.comIds.length > 0) {
        show_comments_table_id_vue.comIds = data.comIds;
        show_comments_table_id_vue.emails = data.emails;
        show_comments_table_id_vue.comments = data.comments;
        show_comments_table_id_vue.dateTimes = data.dateTimes;
        if (clickNOrPOrSBtn === clickNextPageBtn) {
          show_comments_table_id_vue.currentPageIndex++;
        } else if (clickNOrPOrSBtn === clickPrePageBtn) {
          show_comments_table_id_vue.currentPageIndex--;
        } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
          show_comments_table_id_vue.currentPageIndex = show_comments_table_id_vue.pageNumber;
        }
        checkCommentsWhetherVerify();
      } else {
        if (clickNOrPOrSBtn === clickNextPageBtn) {
          offset -= numberOfPage;
          clickNOrPOrSBtn = '';
          showOrHiddenLoadingDiv(false);
          return;
        } else if (clickNOrPOrSBtn === clickPrePageBtn) {
          offset += numberOfPage;
          if (cmd !== 'loadPre') {
            clickNOrPOrSBtn = '';
            showOrHiddenLoadingDiv(false);
            return;
          }
        } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
          offset = preOffset;
          clickNOrPOrSBtn = '';
          showOrHiddenLoadingDiv(false);
          return;
        }
        if (cmd === 'delete') {
          if (show_comments_table_id_vue.currentPageIndex !== 1) {
            clickNOrPOrSBtn = clickPrePageBtn;
            offset -= numberOfPage;
            queryRecordCommentCmd('loadPre');
            return;
          }
        }
        if (show_comments_table_id_vue.currentPageIndex === 1) {
          show_comments_table_id_vue.comIds = [];
          show_comments_table_id_vue.emails = [];
          show_comments_table_id_vue.comments = [];
          show_comments_table_id_vue.dateTimes = [];
        }
      }
      clickNOrPOrSBtn = '';
      showOrHiddenLoadingDiv(false);
    }, error: function (e) {
      console.log(e);
      if (clickNOrPOrSBtn === clickPrePageBtn) {
        if (show_comments_table_id_vue.currentPageIndex !== 1) {
          offset += numberOfPage;
        }
      } else if (clickNOrPOrSBtn === clickNextPageBtn) {
        offset -= numberOfPage;
      } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
        offset = preOffset;
        clickNOrPOrSBtn = '';
      }
      clickNOrPOrSBtn = '';
      showOrHiddenLoadingDiv(false);
    }
  })
}

const show_comments_table_id_vue = new Vue({
  el: '#show_comments_table_id',
  data: {
    comIds: [],
    emails: [],
    dateTimes: [],
    comments: [],
    passText: [],
    pageNumber: '',
    currentPageIndex: 1
  },
  methods: {
    replyCom: function (index) {
      if (loadingDivIsShowing) {
        return;
      }
      if (!document.getElementById('reply_com_div_id')) {
        $('<div  class="modal-dialog modal-dialog-centered" id="reply_com_div_id" style="position: fixed;z-index: 1;top: 40px;left: 50%;transform: translateX(-50%)"   role="document" >\n'
            + '  <div class="modal-content shadow">\n'
            + '    <div class="modal-header">\n'
            + '      <h5 class="modal-title" id="exampleModalCenterTitle">回复</h5>\n'
            + '    </div>\n'
            + '    <div class="modal-body" style="text-align: center">\n' +
            '<textarea class="m-1" style="width: 300px;\n'
            + '    height: 335px;" id="reply_com_textarea_id" placeholder="写下你的评论,评论最多300字!!!"></textarea>'
            + '    </div>\n'
            + '    <div class="modal-footer">\n'
            + '   <button type="button" class="btn btn-outline-danger" style="width: 80px" id="cancel_btn_id">取消</button>'
            +
            '<button type="button" class="btn btn-outline-success"style="width: 80px" id="commit_reply_btn_id">提交</button> </div>\n'
            + '  </div>\n'
            + '</div>').appendTo($('body'));
      } else {
        return;
      }
      $('#cancel_btn_id').click(function () {
        $('#reply_com_div_id').remove();
      });
      $('#commit_reply_btn_id').click(function () {
        let replyCom = $('#reply_com_textarea_id').val().trim().replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>').replace(/\s/g,'&nbsp;');
        if (!replyCom) {
          alertShow('回复不合理!!!');
          return;
        }
        if (replyCom.length > 300) {
          alertShow('回复内容最多300字!!!');
          return;
        }
        showOrHiddenLoadingDiv(true);
        if (!email) {
          loadEmail(false);
          if (!email) {
            alertShow('您的邮箱号加载失败!!!');
            showOrHiddenLoadingDiv(false);
            return;
          }
        }
        let data = {
          id: id,
          articleLinkName: articleLinkName,
          email: email,
          comment: replyCom,
          cId: show_comments_table_id_vue.comIds[index]
        };

        $.post('/yuns/artcomc/rec', data, function (data) {
          showOrHiddenLoadingDiv(false);
          if (data === '') {
            alertShow('评论上传失败!!!');
          } else {
            $('#reply_com_div_id').remove();
            queryRecordCommentCmd();
          }

        }).fail(function (e) {
          console.log(e);
          alertShow('评论上传失败!!!');
          showOrHiddenLoadingDiv(false);
          $('#reply_com_div_id').remove();
        });

      });
    },
    refreshCom: function () {
      if (this.comIds.length === 0) {
        offset = 0;
      }
      queryRecordCommentCmd();
    },
    deleteCom: function (index) {
      delOrPassCommentsCmd('/yuns/artcomc/delc', index, '删除成功!!!', '删除失败!!!');
    },
    passCom: function (index) {
      delOrPassCommentsCmd('/yuns/artcomc/recvf', index, '验证成功!!!', '验证失败!!!');
    },
    flyPage: function (index) {
      if (loadingDivIsShowing) {
        return;
      }
      if (index === -1) {
        if (this.currentPageIndex === 1) {
          return;
        }
        offset -= numberOfPage;
        clickNOrPOrSBtn = clickPrePageBtn;
      } else {
        offset += numberOfPage;
        clickNOrPOrSBtn = clickNextPageBtn;
      }
      queryRecordCommentCmd();
    },
    indicatorCurPAge: function () {
      if (parseInt(this.pageNumber) % 1 !== 0) {
        alertShow('输入不合法!!!');
        return;
      }
      this.pageNumber = parseInt(this.pageNumber);
      if (this.pageNumber < 1) {
        alertShow('请输入大于0的整数!!!');
        return;
      }
      clickNOrPOrSBtn = clickSearchPageBtn;
      preOffset = offset;
      offset = numberOfPage * (this.pageNumber - 1);
      queryRecordCommentCmd();
    }
  }
});

function delOrPassCommentsCmd(url, index, s, e) {
  if (loadingDivIsShowing) {
    return;
  }
  showOrHiddenLoadingDiv(true);
  let formData = new FormData();
  formData.append('id', id);
  formData.append('articleLinkName', articleLinkName);
  if (index === -1) {
    let i = 0;
    for (let comIds of show_comments_table_id_vue.comIds) {
      if (s.includes('验证')) {
        if (show_comments_table_id_vue.passText[i] === '通过') {
          formData.append('comIds', comIds);
        }

      } else {
        formData.append('comIds', comIds);
      }
    }

  } else {
    if (s.includes('验证')) {
      if (show_comments_table_id_vue.passText[index] === '通过') {
        formData.append('comIds', show_comments_table_id_vue.comIds[index]);
      }
    } else {
      formData.append('comIds', show_comments_table_id_vue.comIds[index]);
    }
  }
  if (formData.getAll('comIds').length === 0) {
    showOrHiddenLoadingDiv(false);
    return;
  }
  $.ajax({
    type: "POST",
    url: url,
    data: formData,
    contentType: false, //禁止设置请求类型
    processData: false, //禁止jquery对DAta数据的处理,默认会处理
    success: function (data) {
      showOrHiddenLoadingDiv(false);
      if (data === '') {
        alertShow(e);
        return;
      }
      if (s.includes('验证')) {
        if (index === -1) {
          for (let i = 0; i < show_comments_table_id_vue.comIds.length; i++) {
            show_comments_table_id_vue.passText[i] = '已通过';
          }
        } else {
          show_comments_table_id_vue.passText[index] = '已通过';
        }
        queryRecordCommentCmd();
      } else if (s.includes('删除')) {
        queryRecordCommentCmd('delete');
      }
      alertShow(s);
    },
    error: function (err) {
      console.log(err);
      alertShow(e);
      showOrHiddenLoadingDiv(false);
    }
  });
}

let checkPromise = checkWhoCommanding(id);
checkPromise.then(value => {
  if (value && value > 0) {
    queryRecordCommentCmd();
    /**
     * 加载文章名
     */
    $.post('/yuns/artcc/arttitle', {
      id: id,
      articleLinkName: articleLinkName
    }, function (data) {
      if (data) {
        $('#art_title_h3_id').html(data);
      }
    }).fail(function (e) {
      console.log(e)
    });
    loadEmail(true);
  } else {
    confirm('您当前不是该用户的操作对象!!!');
    window.location.href = '/yuns/html/login.html';
  }
});


var email = '';

function loadEmail(async) {
  $.ajax({
    method: 'post',
    url: '/yuns/user/info',
    data: {
      id: id
    },
    async: async,
    success: function (data) {
      email = data.email;
    },
    error: function (e) {
      console.log(e);
    }
  });
}