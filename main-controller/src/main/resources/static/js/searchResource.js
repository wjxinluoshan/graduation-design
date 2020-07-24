/**
 * 模糊查询用户名的模块
 */
var userNameSearchTimer = '';
var loadUserNameSearchPost = '';
var show_username_ul_id_vue = '';
var likeSearchUrls = '';

var clickBtn = '';
var clickNBtn = 'n';
var clickPBtn = 'p';
var likeSearchOffset = 0;
var likeSearchNumberOfPage = 3;

var keyword = '';

var sIsDel = false;

function querySearchLink(cmd) {
  showOrHiddenLoadingDiv(true);
  loadUserNameSearchPost = $.ajax({
    type: 'post',
    url: queryUrl,
    data: {
      id: id,
      fileType: fileType,
      kw: keyword,
      offset: likeSearchOffset,
      numberOfPage: likeSearchNumberOfPage
    },
    success: function (data) {
      showOrHiddenLoadingDiv(false);
      if (data && data.rnames.length > 0) {
        $('#show_username_ul_id').css('display', 'block');
        show_username_ul_id_vue.urls = [];
        show_username_ul_id_vue.resNames = [];
        show_username_ul_id_vue.dateTime = [];
        if (type === 'm_art') {
          likeSearchUrls = data.rlinks;
        } else {
          likeSearchUrls = data.rnames;
        }
        let index = 0;
        for (let resourceName of data.rnames) {
          if (type === 'm_art') {
            let linkNameArr = likeSearchUrls[index].split('/');
            index++;
            show_username_ul_id_vue.urls.push(
                linkNameArr[linkNameArr.length - 1]);
            show_username_ul_id_vue.resNames.push(resourceName);
          } else {
            show_username_ul_id_vue.resNames.push(
                resourceName.split("?name=")[1]);
            var date = new Date();
            date.setTime(
                parseInt(resourceName.split("?name=")[0].split('_')[1].split(
                    '.')[0]));
            show_username_ul_id_vue.dateTime.push(
                date.getFullYear() + '.' + (date.getMonth() + 1) + '.'
                + date.getDate() + ' ' + date.getHours() + ':'
                + date.getMinutes() + ':' + date.getSeconds());
          }
          if (type === 'm_doc') {
            if (resourceName.endsWith('.doc')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/doc_icon.png');
            } else if (resourceName.endsWith('.docx')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/docx_icon.png');
            } else if (resourceName.endsWith('.pdf')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/pdf_icon.png');
            } else if (resourceName.endsWith('.xls')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/xls_icon.png');
            } else if (resourceName.endsWith('.xlsx')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/xlsx_icon.png');
            } else if (resourceName.endsWith('.ppt')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/ppt_icon.png');
            } else {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/pptx_icon.png');
            }
          } else if (type === 'm_pic') {
            show_username_ul_id_vue.urls.push(resourceName);
          } else if (type === 'm_os') {
            if (resourceName.endsWith('.zip')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/zip.png');
            } else if (resourceName.endsWith('.txt')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/txt.png');
            } else if (resourceName.endsWith('.exe')) {
              show_username_ul_id_vue.urls.push('/yuns/sysimg/exe.png');
            }
          }
        }
        if (clickBtn === clickPBtn) {
          show_username_ul_id_vue.currentPageIndex--;
        } else if (clickBtn === clickNBtn) {
          show_username_ul_id_vue.currentPageIndex++;
        }
        if (sIsDel) {
          show_username_ul_id_vue.currentPageIndex--;
          sIsDel = false;
        }
      } else {
        if (cmd === 'del') {
          if (show_username_ul_id_vue.currentPageIndex > 1) {
            likeSearchOffset -= likeSearchNumberOfPage;
            sIsDel = true;
            querySearchLink('del');
            clickBtn = '';
            return;
          } else {
            show_username_ul_id_vue.urls = [];
            show_username_ul_id_vue.resNames = [];
            $('#show_username_ul_id').css('display', 'none');
            return;
          }
        }
        if (clickBtn === clickPBtn) {
          if (show_username_ul_id_vue.currentPageIndex !== 1) {
            likeSearchOffset += likeSearchNumberOfPage;
          }
        } else if (clickBtn === clickNBtn) {
          likeSearchOffset -= likeSearchNumberOfPage;
        }
      }
      clickBtn = '';
    }, error: function (e) {
      console.log(e);
      showOrHiddenLoadingDiv(false);
      if (clickBtn === clickPBtn) {
        if (show_username_ul_id_vue.currentPageIndex !== 1) {
          likeSearchOffset += likeSearchNumberOfPage;
        }
      } else if (clickBtn === clickNBtn) {
        likeSearchOffset -= likeSearchNumberOfPage;
      }
      clickBtn = '';
    }
  })
}

$('#btn_name_id').on('click', () => {
  keyword = $('#input_name_id').val().trim();
  if (!keyword) {
    return;
  }
  if (userNameSearchTimer) {
    clearTimeout(userNameSearchTimer);
  }
  //查询
  if (loadUserNameSearchPost) {
    showOrHiddenLoadingDiv(false);
    loadUserNameSearchPost.abort();
  }
  createShowArea();
  likeSearchOffset = 0;
  show_username_ul_id_vue.currentPageIndex = 1;
  querySearchLink();
});
$("#input_name_id").keyup(function (event) {
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
      showOrHiddenLoadingDiv(false);
      loadUserNameSearchPost.abort();
    }
    likeSearchOffset = 0;
    show_username_ul_id_vue.currentPageIndex = 1;
    querySearchLink();
  }, 300);

});

function createShowArea() {
  if (!document.getElementById('show_username_ul_id')) {
    if (type === 'm_art') {
      $('<ul class="table-responsive shadow mt-2" id="show_username_ul_id" style="display:none;list-style:none;position: fixed;z-index: 1;background-color: pink;width: 100%;top:40px;max-height: 350px;overflow-y: auto;">'
          + '<li>'
          + '<div v-if="urls.length>0" class="pl-2 mt-2">\n'
          + '    <button style="button"style="width: 80px" class="btn btn-info" @click="flyPage(-1)">上一页</button>\n'
          + '    <button style="button" style="width: 80px"class="btn btn-info" @click="flyPage(1)">下一页</button>\n'
          + '    <span>{{currentPageIndex}}</span>\n'
          + '  </div>'
          + ' <table class="table">' +
          '<tbody>' +
          ' <tbody>\n' +
          '<tr><td style="text-align: center"><button type="button"style="width: 80px" class="btn btn-outline-danger"><a  style="cursor: pointer" @click="ellipse">收起</a></button>'
          + '        <button type="button" class="btn btn-warning" style="width: 80px" @click="refreshPic">刷新</button></td></tr>'
          + '    <tr v-for="(url, index) in urls">\n'
          + '      <td ><div style=" max-height: 100px; width: 150px;  overflow: auto;">{{url}}</div></td>\n'
          + '<td><div style=" max-height: 100px; width: 150px;  overflow: auto;">{{resNames[index]}}</div></td>'
          + '      <td>\n'
          + '       <div> <img @click="shareToQQFriend(index)" src="/yuns/sysimg/QQ.png" style="width: 30px;" title="点击进入分享页面"/>'
          + '        <img :id="index+\'' + 'search\''
          + '" @mouseover="shareUseRCode(index)" @mouseleave="unShareUseRCode(index)" src="/yuns/sysimg/wechat.png" style="width: 30px;"/>'
          + '        </div><button type="button"style="width: 80px"  class="btn btn-outline-primary" @click="lookRes(index)">查看\n'
          + '        </button>\n'
          + '      </td>\n'
          + '      <td>\n'
          + '        <button type="button"style="width: 80px" class="btn btn-outline-info" @click="editRes(index)">修改\n'
          + '        </button>\n'
          + '      </td>\n'
          + '      <td>\n'
          + '        <button type="button"style="width: 80px" class="btn btn-outline-success" @click="checkComments(index)">评论\n'
          + '        </button>\n'
          + '      </td>\n'
          + '   <td>\n'
          + '          <button type="button" style="width: 100px" class="btn btn-outline-dark" @click="copyLink(index)">复制链接</button>\n'
          + '        </td>'
          + '      <td style="text-align: center">\n'
          + '    <div class="btn-group" role="group" aria-label="Button group with nested dropdown">\n'
          + '          <button type="button"style="width: 80px" @click="publish(index)" class="btn btn-outline-success">发布\n'
          + '          </button>\n'
          + '          <button type="button"style="width: 80px" @click="unPublish(index)" class="btn btn-outline-danger">撤销\n'
          + '          </button>\n'
          + '        </div>'
          + '      </td>\n'
          + '      <td>\n'
          + '        <button type="button"style="width: 80px" class="btn btn-danger" @click="deleteRes(index)">删除\n'
          + '        </button>\n'
          + '      </td>\n'
          + '    </tr>\n'
          + '    </tbody>'
          + '</tbody>' +
          '</table></li></ul>').appendTo(
          $('body'));
    } else {
      $('<ul class="table-responsive shadow mt-2" id="show_username_ul_id" style="display:none;list-style:none;position: fixed;z-index: 1;background-color: pink;width: 100%;top:40px;max-height: 350px;overflow-y: auto;">'
          + '<li>'
          + '<div v-if="urls.length>0" class="pl-2 mt-2">\n'
          + '    <button type="button" style="width: 80px"class="btn btn-info" @click="flyPage(-1)">上一页</button>\n'
          + '    <button type="button" style="width: 80px"class="btn btn-info" @click="flyPage(1)">下一页</button>\n'
          + '    <span>{{currentPageIndex}}</span>\n'
          + '  </div>'
          + '<table class="table">' +
          '<tbody>' +
          '<tr><td style="text-align: center"><button type="button"style="width: 80px" class="btn btn-outline-danger"><a  style="cursor: pointer" @click="ellipse">收起</a></button></td></tr>'
          + '    <tr v-for="(url, index) in urls">\n'
          + '      <td ><img :src="url" style="max-width: 230px"></td>\n'
          + '      <td ><div style=" max-height: 100px; width: 150px;  overflow: auto;">{{resNames[index]}}</div></td>\n'
          + '       <td><div style="width: max-content">{{dateTime[index]}}</div></td>\n'
          + '      <td>\n'
          + '        <button type="button" style="width: 80px" class="btn btn-outline-primary" @click="downloadRes(index)">下载\n'
          + '        </button>\n'
          + '        <div><img @click="shareToQQFriend(index)" src="/yuns/sysimg/QQ.png" style="width: 30px;" title="点击进入分享页面"/>'
          + '        <img :id="index+\'' + 'search\''
          + '" @mouseover="shareUseRCode(index)" @mouseleave="unShareUseRCode(index)" src="/yuns/sysimg/wechat.png" style="width: 30px;"/>'
          + '     </div> </td>\n'
          + '   <td>\n'
          + '          <button type="button" style="width: 100px" class="btn btn-outline-dark" @click="copyLink(index)">复制链接</button>\n'
          + '        </td>'
          + '      <td style="text-align: center">\n'
          + '    <div class="btn-group" role="group" aria-label="Button group with nested dropdown">\n'
          + '          <button type="button" style="width: 80px"@click="publish(index)" class="btn btn-outline-success">发布\n'
          + '          </button>\n'
          + '          <button type="button"style="width: 80px" @click="unPublish(index)" class="btn btn-outline-danger">撤销\n'
          + '          </button>\n'
          + '        </div>'
          + '      </td>\n'
          + '      <td>\n'
          + '        <button type="button" style="width: 80px"class="btn btn-danger" @click="deleteRes(index)">删除\n'
          + '        </button>\n'
          + '      </td>\n'
          + '    </tr>\n'
          + '</tbody>' +
          '</table></li></ul>').appendTo(
          $('body'));
    }
    show_username_ul_id_vue = new Vue({
      el: '#show_username_ul_id',
      data: {
        urls: [],
        resNames: [],
        dateTime: [],
        currentPageIndex: 1
      },
      methods: {
        refreshPic: function () {
          querySearchLink();
          query();
        },
        copyLink: function (index) {
          let d = '?data=' + id + '__yuns__' + likeSearchUrls[index];
          let ta = document.createElement('textarea');
          ta.value = sharePageLink + d;
          // ta.style.display = 'none';
          document.body.appendChild(ta);
          ta.select();
          if (document.execCommand('Copy')) {
            alertShow("您已复制该资源链接!!!");
          } else {
            alertShow("出现错误，该资源链接复制不成功!!!");
          }
          document.body.removeChild(ta);
        },
        shareToQQFriend: function (index) {
          let d = '?data=' + id + '__yuns__' + likeSearchUrls[index];
          let title = show_username_ul_id_vue.resNames[index];
          if (title.length > 20) {
            title = title.substring(0, parseInt(title.length * 0.5)) + '...';
          }
          switch (type) {
            case "m_pic":
              shareQQFriend(sharePageLink + d, '',
                  title, "点击进入下载页面", "");
              break;
            case "m_doc":
              shareQQFriend(sharePageLink + d, '',
                  title, "点击进入下载页面", "");
              break;
            case "m_os":
              shareQQFriend(sharePageLink + d, '',
                  title, "点击进入下载页面", "");
              break;
            case "m_art":
              shareQQFriend(sharePageLink + d, '',
                  title
                  , "点击进入阅读页面", "");
              break;
          }
        },
        shareUseRCode: function (index) {
          let d = '?data=' + id + '__yuns__' + likeSearchUrls[index];
          $('<div id="s_rcode_div_id" style="position: fixed;z-index: 1;width: 100px;height: 100px"></div>').appendTo(
              'body');
          let x = $('#' + index + 'search').offset().left;
          let y = $('#' + index + 'search').offset().top;
          $('#s_rcode_div_id').offset({
            left: x + $('#s_rcode_div_id').width() / 3,
            top: y - 100
          });
          shareWechat(sharePageLink + d, 's_rcode_div_id');
        },
        unShareUseRCode: function (index) {
          $('#s_rcode_div_id').remove();
        },
        checkComments: function (index) {
          window.open(
              '/yuns/html/art_comment.html?id=' + id + '&articleLinkName='
              + this.urls[index], '_blank');
        },
        lookRes: function (index) {
          window.open(likeSearchUrls[index], '_blank');
        },
        editRes: function (index) {
          window.open(
              '/yuns/html/u_art.html?id=' + id + '&aln=' + this.urls[index],
              '_blank');
        },
        flyPage: function (index) {
          if (loadingDivIsShowing) {
            return;
          }
          if (index === -1) {
            if (this.currentPageIndex === 1) {
              return;
            }
            clickBtn = clickPBtn;
            likeSearchOffset -= likeSearchNumberOfPage;
          } else {
            likeSearchOffset += likeSearchNumberOfPage;
            clickBtn = clickNBtn;
          }
          querySearchLink();
        },
        unPublish: function (index) {
          if (loadingDivIsShowing) {
            return;
          }
          showOrHiddenLoadingDiv(true);
          switch (type) {
            case "m_pic":
              unPublishResource({
                picName: likeSearchUrls[index],
                id: id
              }, '/yuns/pubres/delp');
              break;
            case "m_doc":
              unPublishResource({
                docName: likeSearchUrls[index],
                id: id
              }, '/yuns/pubres/deld');
              break;
            case "m_os":
              unPublishResource({
                oresName: likeSearchUrls[index],
                id: id
              }, '/yuns/pubres/delo');
              break;
            case "m_art":
              unPublishResource({
                al: this.urls[index],
                id: id
              }, '/yuns/pubart/delart');
              break;
          }
        },
        /**
         *将资源上传至分享
         * @param index
         */
        publish: function (index) {
          if (loadingDivIsShowing) {
            return;
          }
          showOrHiddenLoadingDiv(true);
          switch (type) {
            case "m_pic":
              publishResource({
                picName: likeSearchUrls[index],
                id: id
              }, '/yuns/pubres/ppic');
              break;
            case "m_doc":
              publishResource({
                docName: likeSearchUrls[index],
                id: id
              }, '/yuns/pubres/pdoc');
              break;
            case "m_os":
              publishResource({
                oresName: likeSearchUrls[index],
                id: id
              }, '/yuns/pubres/pores');
              break;
            case "m_art":
              publishResource({
                al: this.urls[index],
                at: this.resNames[index],
                id: id
              }, '/yuns/pubart/part');
              break;
          }
        },
        deleteRes: function (index) {
          if (loadingDivIsShowing) {
            return;
          }
          showOrHiddenLoadingDiv(true);
          let formData = new FormData();
          formData.append('id', id);
          if (index === -1) {
            if (type === 'm_art') {
              for (let url of likeSearchUrls) {
                formData.append('filesName', url);
              }
            } else {
              for (let fn of likeSearchUrls) {
                formData.append('filesName', fn);
              }
            }
          } else {
            if (type === 'm_art') {
              formData.append('filesName', likeSearchUrls[index])
            } else {
              formData.append('filesName', likeSearchUrls[index])
            }
          }
          deleteCmd(formData, true);
        },
        downloadRes: function (index) {
          let link = document.createElement('a');
          link.download = this.resNames[index];
          link.href = likeSearchUrls[index];
          link.click();
        },
        ellipse: function () {
          $('#show_username_ul_id').css('display', 'none');
        },
        flyPage: function (index) {
          if (index === 1) {
            clickBtn = clickNBtn;
            likeSearchOffset += likeSearchNumberOfPage;
          } else {
            if (likeSearchOffset > 0) {
              clickBtn = clickPBtn;
              likeSearchOffset -= likeSearchNumberOfPage;
            } else {
              return;
            }
          }
          querySearchLink();
        }
      }
    });
  }

}



