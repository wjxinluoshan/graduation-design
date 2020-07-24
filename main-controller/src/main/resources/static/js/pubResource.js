var offset = 0;
var preOffset = 0;
var numberOfPage = 5;
var type = '';

var resourcesUrl = [];
var userIds = [];

var queryUrl = '';
var insertDownloadResUrl = '';

if (window.location.href.includes('pic')) {
  type = 'pic';
  queryUrl = '/yuns/pubres/qpics';
  insertDownloadResUrl = '/yuns/pubres/idpc';
} else if (window.location.href.includes('doc')) {
  type = 'doc';
  queryUrl = '/yuns/pubres/qdocs';
  insertDownloadResUrl = '/yuns/pubres/iddc';
} else if (window.location.href.includes('ores')) {
  type = 'ores';
  queryUrl = '/yuns/pubres/qoreses';
  insertDownloadResUrl = '/yuns/pubres/idorsc';
}

var clickNOrPOrSBtn = '';
var clickNextPageBtn = 'n';
var clickPrePageBtn = 'p';
var clickSearchPageBtn = 's';

const show_res_table_id_vue = new Vue({
  el: '#show_res_table_id',
  data: {
    resUrls: [],
    resNames: [],
    dateTime: [],
    uploaders: [],
    downloads: [],
    currentPageIndex: 1,
    pageNumber: ''
  },
  methods: {
    indicatorCurPAge: function () {
      if (loadingDivIsShowing) {
        return;
      }
      if (parseInt(this.pageNumber) % 1 !== 0) {
        alertShow('输入不合法!!!');
        return;
      }
      this.pageNumber = parseInt(this.pageNumber);
      if (this.pageNumber < 1) {
        alertShow('请输入大于0的整数!!!');
        return;
      }
      preOffset = offset;
      //whhen this.pageNumber is 1,the offset equal 0;
      //one by one this.pageNumber == 2,the offset equal is numberOfPage;
      offset = numberOfPage * (this.pageNumber - 1);
      clickNOrPOrSBtn = clickSearchPageBtn;
      queryPubResource(queryUrl, {offset: offset, numberOfPage: numberOfPage});
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
      queryPubResource(queryUrl, {offset: offset, numberOfPage: numberOfPage});
    },
    download: function (index) {
      let link = document.createElement('a');
      link.download = this.resNames[index];
      link.href = resourcesUrl[index];
      link.click();
      $.post(insertDownloadResUrl,
          {rn: resourcesUrl[index], id: userIds[index]}, function (data) {
            show_res_table_id_vue.downloads[index] = show_res_table_id_vue.downloads[index]
                + 1;
            let arr = show_res_table_id_vue.downloads;
            show_res_table_id_vue.downloads = [];
            show_res_table_id_vue.downloads = arr;
          }).fail(function (e) {

        console.log(e);
      });
    }
    ,
    toUserName: function (index) {
      window.open('/yuns/show/showuser.html?id=' + userIds[index], '_blank');
    }
  }
});

function queryPubResource(url, data) {
  showOrHiddenLoadingDiv(true);
  $.post(url, data, function (data) {
    showOrHiddenLoadingDiv(false);
    if (data && data.ids.length > 0) {
      userIds = data.ids;
      let formData = new FormData();
      for (let ele of data.ids) {
        formData.append('ids', ele);
      }
      $.ajax({
        method: 'post',
        url: '/yuns/user/uns',
        data: formData,
        contentType: false, //禁止设置请求类型
        processData: false, //禁止jquery对DAta数据的处理,默认会处理
        success: function (d) {
          if (d) {
            show_res_table_id_vue.uploaders = d;
            show_res_table_id_vue.resNames = data.resNames;
            for (let i = 0; i < data.downloads.length; i++) {
              data.downloads[i] = parseInt(data.downloads[i]);
            }
            show_res_table_id_vue.downloads = data.downloads;
            resourcesUrl = data.resUrls;
            let dateTime = [];
            for (let resourcesUrlElement of resourcesUrl) {
              var date = new Date();
              date.setTime(
                  parseInt(resourcesUrlElement.split("?date=")[1]));
              dateTime.push(
                  date.getFullYear() + '.' + (date.getMonth() + 1) + '.'
                  + date.getDate() + ' ' + date.getHours() + ':'
                  + date.getMinutes() + ':' + date.getSeconds())
            }
            show_res_table_id_vue.dateTime = dateTime;

            if (type === 'doc') {
              show_res_table_id_vue.resUrls = [];
              for (let name of show_res_table_id_vue.resNames) {
                if (name.endsWith('.doc')) {
                  show_res_table_id_vue.resUrls.push(
                      '/yuns/sysimg/doc_icon.png');
                } else if (name.endsWith('.docx')) {
                  show_res_table_id_vue.resUrls.push(
                      '/yuns/sysimg/docx_icon.png');
                } else if (name.endsWith('.pdf')) {
                  show_res_table_id_vue.resUrls.push(
                      '/yuns/sysimg/pdf_icon.png');
                } else if (name.endsWith('.xls')) {
                  show_res_table_id_vue.resUrls.push(
                      '/yuns/sysimg/xls_icon.png');
                } else if (name.endsWith('.xlsx')) {
                  show_res_table_id_vue.resUrls.push(
                      '/yuns/sysimg/xlsx_icon.png');
                } else if (name.endsWith('.ppt')) {
                  show_res_table_id_vue.resUrls.push(
                      '/yuns/sysimg/ppt_icon.png');
                } else {
                  show_res_table_id_vue.resUrls.push(
                      '/yuns/sysimg/pptx_icon.png');
                }
              }
            } else if (type === 'pic') {
              show_res_table_id_vue.resUrls = data.resUrls;
            } else if (type === 'ores') {
              show_res_table_id_vue.resUrls = [];
              for (let name of show_res_table_id_vue.resNames) {
                if (name.endsWith('.zip')) {
                  show_res_table_id_vue.resUrls.push('/yuns/sysimg/zip.png');
                } else if (name.endsWith('.txt')) {
                  show_res_table_id_vue.resUrls.push('/yuns/sysimg/txt.png');
                } else if (name.endsWith('.exe')) {
                  show_res_table_id_vue.resUrls.push('/yuns/sysimg/exe.png');
                }
              }
            }
          }
        },
        error: function (e) {
          console.log(e);
        }
      });
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

queryPubResource(queryUrl, {offset: offset, numberOfPage: numberOfPage});
