var id = window.location.href.split('=')[1];
var type = '';
var queryUrl = '';
var fileType = '';
var offset = 0;
var preOffset = 0;
var numberOfPage = 4;

/**
 * 部署修改
 * @type {string}
 */
// var sharePageLink = 'http://119.3.182.178:8080/yuns/show/share.html';
var sharePageLink = 'http://localhost:8888/yuns/show/share.html';

var clickNOrPOrSBtn = '';
var clickNextPageBtn = 'n';
var clickPrePageBtn = 'p';
var clickSearchPageBtn = 's';

var resourceArtLinkArr = [];

if (window.location.href.includes('m_pic')) {
    type = 'm_pic';
    // history.replaceState({}, "", "/yuns/html/m_pic.html");
    queryUrl = '/yuns/pcc/pnm';
} else if (window.location.href.includes('m_doc')) {
    type = 'm_doc';
    // history.replaceState({}, "", "/yuns/html/m_doc.html");
    queryUrl = '/yuns/fcc/fnm';
    fileType = 'doc';
} else if (window.location.href.includes('m_os')) {
    type = 'm_os';
    // history.replaceState({}, "", "/yuns/html/m_os.html");
    queryUrl = '/yuns/fcc/fnm';
    fileType = 'ores';
} else if (window.location.href.includes('m_art')) {
    type = 'm_art';
    numberOfPage = 5;
    // history.replaceState({}, "", "/yuns/html/m_art.html");
    queryUrl = '/yuns/artcc/artname';
    fileType = 'art';
}

let checkPromise = checkWhoCommanding(id);
checkPromise.then(value => {
    if (value && value > 0) {
        query();
    } else {
        confirm('您当前不是该用户的操作对象!!!');
        window.location.href = '/yuns/html/login.html';
    }
});

var isDel = false;

/**
 * 其他资源操作
 */
function query(cmd) {
    showOrHiddenLoadingDiv(true);
    $.ajax({
        type: 'post',
        url: queryUrl,
        data: {
            id: id,
            fileType: fileType,
            offset: offset,
            numberOfPage: numberOfPage
        },
        success: function(data) {
            showOrHiddenLoadingDiv(false);

            if (data && data.rnames.length > 0) {
                show_pic_table_id_vue.picArr = [];
                show_pic_table_id_vue.picNameArr = [];
                show_pic_table_id_vue.dateTime = [];

                if (type === 'm_art') {
                    resourceArtLinkArr = data.rlinks;
                } else {
                    resourceArtLinkArr = data.rnames;
                }
                let index = 0;
                for (let resourceName of data.rnames) {
                    if (type === 'm_art') {
                        let linkNameArr = resourceArtLinkArr[index].split('/');
                        index++;
                        show_pic_table_id_vue.picArr.push(
                            linkNameArr[linkNameArr.length - 1]);
                        show_pic_table_id_vue.picNameArr.push(resourceName);
                    } else {
                        show_pic_table_id_vue.picNameArr.push(
                            resourceName.split("?name=")[1]);
                        var date = new Date();
                        date.setTime(
                            parseInt(resourceName.split("?name=")[0].split('_')[1].split(
                                '.')[0]));
                        show_pic_table_id_vue.dateTime.push(
                            date.getFullYear() + '.' + (date.getMonth() + 1) + '.' +
                            date.getDate() + ' ' + date.getHours() + ':' +
                            date.getMinutes() + ':' + date.getSeconds());
                    }
                    if (type === 'm_doc') {
                        if (resourceName.endsWith('.doc')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/doc_icon.png');
                        } else if (resourceName.endsWith('.docx')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/docx_icon.png');
                        } else if (resourceName.endsWith('.pdf')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/pdf_icon.png');
                        } else if (resourceName.endsWith('.xls')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/xls_icon.png');
                        } else if (resourceName.endsWith('.xlsx')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/xlsx_icon.png');
                        } else if (resourceName.endsWith('.ppt')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/ppt_icon.png');
                        } else {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/pptx_icon.png');
                        }
                    } else if (type === 'm_pic') {
                        show_pic_table_id_vue.picArr.push(resourceName);
                    } else if (type === 'm_os') {
                        if (resourceName.endsWith('.zip')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/zip.png');
                        } else if (resourceName.endsWith('.txt')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/txt.png');
                        } else if (resourceName.endsWith('.exe')) {
                            show_pic_table_id_vue.picArr.push('/yuns/sysimg/exe.png');
                        }
                    }
                }
                if (clickNOrPOrSBtn === clickPrePageBtn) {
                    show_pic_table_id_vue.currentPageIndex--;
                } else if (clickNOrPOrSBtn === clickNextPageBtn) {
                    show_pic_table_id_vue.currentPageIndex++;
                } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
                    show_pic_table_id_vue.currentPageIndex = show_pic_table_id_vue.pageNumber;
                }

                if (isDel) {
                    show_pic_table_id_vue.currentPageIndex--;
                    isDel = false;
                }
            } else {
                if (cmd === 'del') {
                    if (show_pic_table_id_vue.currentPageIndex > 1) {
                        offset -= numberOfPage;
                        isDel = true;
                        query('del');
                        clickNOrPOrSBtn = '';
                        return;
                    } else {
                        show_pic_table_id_vue.picArr = [];
                        show_pic_table_id_vue.picNameArr = [];
                        return;
                    }
                }
                if (clickNOrPOrSBtn === clickPrePageBtn) {
                    if (show_pic_table_id_vue.currentPageIndex !== 1) {
                        offset += numberOfPage;
                    }
                } else if (clickNOrPOrSBtn === clickNextPageBtn) {
                    offset -= numberOfPage;
                } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
                    offset = preOffset;
                }
            }
            clickNOrPOrSBtn = '';
        },
        error: function(e) {
            console.log(e);
            showOrHiddenLoadingDiv(false);
            if (clickNOrPOrSBtn === clickPrePageBtn) {
                if (show_pic_table_id_vue.currentPageIndex !== 1) {
                    offset += numberOfPage;
                }
            } else if (clickNOrPOrSBtn === clickNextPageBtn) {
                offset -= numberOfPage;
            } else if (clickNOrPOrSBtn === clickSearchPageBtn) {
                offset = preOffset;
            }
            clickNOrPOrSBtn = '';
        }
    })
}

var isEnterShareDiv = false;
var isEnterShareBtn = [false, false, false, false];
var shareIndex = 0;

window.onresize = function() {
    if (document.getElementById('share_div_id')) {
        let x = $("#" + shareIndex).offset().left;
        let y = $("#" + shareIndex).offset().top;
        $('#share_div_id').css('display', 'block');
        $('#share_div_id').offset({
            left: x + $('#share_div_id').width() - 20,
            top: y - 30
        });
    }
    if (document.getElementById('rcode_div_id')) {
        $('#rcode_div_id').remove();
    }

};
/**
 * 图片VUE:
 *
 * 1.图片显示删除
 * 2.图片上传
 */
const show_pic_table_id_vue = new Vue({
    el: '#show_pic_table_id',
    data: {
        picArr: [],
        picNameArr: [],
        dateTime: [],
        currentPageIndex: 1,
        pageNumber: Number
    },
    methods: {
        //articleRefresh
        refreshPic: function() {
            query();
            if (document.getElementById('show_username_ul_id')) {
                if ($('#show_username_ul_id').css('display') !== 'none') {
                    querySearchLink();
                }
            }
        },
        copyLink: function(index) {
            let d = '?data=' + id + '__yuns__' + resourceArtLinkArr[index];
            let ta = document.createElement('textarea');
            ta.value = sharePageLink + d;
            //ta.style.display='none';
            document.body.appendChild(ta);
            ta.select();
            if (document.execCommand('Copy')) {
                alertShow("您已复制该资源链接!!!");
            } else {
                alertShow("出现错误，该资源链接复制不成功!!!");
            }
            document.body.removeChild(ta);
        },
        unSharePic: function(index) {
            isEnterShareBtn[index] = false;
            setTimeout(function() {
                if (!isEnterShareDiv) {
                    if (document.getElementById('share_div_id')) {
                        $('#share_div_id').remove();
                    }
                }
            }, 30);
        },
        sharePic: function(index) {
            shareIndex = index;
            isEnterShareBtn[index] = true;
            if (document.getElementById('share_div_id')) {
                setTimeout(function(i) {
                    if (!document.getElementById('share_div_id')) {
                        show_pic_table_id_vue.sharePic(i);
                    }
                }, 35, index);
                return;
            }
            let x = $("#" + index).offset().left;
            let y = $("#" + index).offset().top;
            $('<div id="share_div_id" style="width: max-content"><img id="qq_img_id" src="/yuns/sysimg/QQ.png" style="width: 30px;" title="点击进入分享页面"/>' +
                '<img id="wchat_img_id" src="/yuns/sysimg/wechat.png" style="width: 30px;"/>' +
                '</div>').appendTo($('body'));
            $('#share_div_id').mouseenter(function() {
                isEnterShareDiv = true;
            });
            $('#share_div_id').mouseleave(function() {
                isEnterShareDiv = false;
                setTimeout(function(i) {
                    if (!isEnterShareBtn[i]) {
                        if (document.getElementById('share_div_id')) {
                            $('#share_div_id').remove();
                        }
                    }
                }, 30, index);
            });
            $('#share_div_id').css('display', 'block');
            $('#share_div_id').offset({
                left: x + $('#share_div_id').width() - 20,
                top: y - 30
            });
            $('#wchat_img_id').mouseenter(function() {
                let d = '?data=' + id + '__yuns__' + resourceArtLinkArr[index];
                $('<div id="rcode_div_id" style="position: fixed;z-index: 1;width: 100px;height: 100px"></div>').appendTo(
                    'body');
                let x = $(this).offset().left;
                let y = $(this).offset().top;
                $('#rcode_div_id').offset({
                    left: x + $('#wchat_img_id').width(),
                    top: y - 103
                });
                shareWechat(sharePageLink + d, 'rcode_div_id');
            });

            $('#wchat_img_id').mouseleave(function() {
                $('#rcode_div_id').remove();
            });

            $('#qq_img_id').click(function() {
                let d = '?data=' + id + '__yuns__' + resourceArtLinkArr[index];
                let title = show_pic_table_id_vue.picNameArr[index];
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
                            title, "点击进入阅读页面", "");
                        break;
                }
            });

        },
        unPublish: function(index) {
            if (loadingDivIsShowing) {
                return;
            }
            showOrHiddenLoadingDiv(true);
            switch (type) {
                case "m_pic":
                    unPublishResource({
                        picName: resourceArtLinkArr[index],
                        id: id
                    }, '/yuns/pubres/delp');
                    break;
                case "m_doc":
                    unPublishResource({
                        docName: resourceArtLinkArr[index],
                        id: id
                    }, '/yuns/pubres/deld');
                    break;
                case "m_os":
                    unPublishResource({
                        oresName: resourceArtLinkArr[index],
                        id: id
                    }, '/yuns/pubres/delo');
                    break;
                case "m_art":
                    unPublishResource({
                        al: this.picArr[index],
                        id: id
                    }, '/yuns/pubart/delart');
                    break;
            }
        },
        /**
         *将资源上传至分享
         * @param index
         */
        publish: function(index) {
            if (loadingDivIsShowing) {
                return;
            }
            showOrHiddenLoadingDiv(true);
            switch (type) {
                case "m_pic":
                    publishResource({
                        picName: resourceArtLinkArr[index],
                        id: id
                    }, '/yuns/pubres/ppic');
                    break;
                case "m_doc":
                    publishResource({
                        docName: resourceArtLinkArr[index],
                        id: id
                    }, '/yuns/pubres/pdoc');
                    break;
                case "m_os":
                    publishResource({
                        oresName: resourceArtLinkArr[index],
                        id: id
                    }, '/yuns/pubres/pores');
                    break;
                case "m_art":
                    publishResource({
                        al: this.picArr[index],
                        at: this.picNameArr[index],
                        id: id
                    }, '/yuns/pubart/part');
                    break;
            }
        },
        checkComments: function(index) {
            window.open('/yuns/html/art_comment.html?id=' + id + '&articleLinkName=' +
                this.picArr[index], '_blank');
        },
        lookPic: function(index) {
            window.open(resourceArtLinkArr[index], '_blank');
        },
        editPic: function(index) {
            window.open(
                '/yuns/html/u_art.html?id=' + id + '&aln=' + this.picArr[index],
                '_blank');
        },
        downloadPic: function(index) {
            let link = document.createElement('a');
            link.download = this.picNameArr[index];
            link.href = resourceArtLinkArr[index];
            link.click();
        },
        /*
         *1.
         */
        deletePic: function(index) {
            if (loadingDivIsShowing) {
                return;
            }
            showOrHiddenLoadingDiv(true);
            let formData = new FormData();
            formData.append('id', id);
            if (index === -1) {
                if (type === 'm_art') {
                    for (let url of resourceArtLinkArr) {
                        formData.append('filesName', url);
                    }
                } else {
                    for (let fn of resourceArtLinkArr) {
                        formData.append('filesName', fn);
                    }
                }
            } else {
                if (type === 'm_art') {
                    formData.append('filesName', resourceArtLinkArr[index])
                } else {
                    formData.append('filesName', resourceArtLinkArr[index])
                }
            }
            deleteCmd(formData);
        },
        indicatorCurPAge: function() {
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
            query();
        },
        flyPage: function(index) {
            if (loadingDivIsShowing) {
                return;
            }
            //上一页
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
            query();
        },
    }
});

function publishResource(data, url) {
    $.post(url, data, function(data) {
        showOrHiddenLoadingDiv(false);
        if (data) {
            alertShow('发布成功!!!');
        }
    }).fail(function(e) {
        console.log(e);
        showOrHiddenLoadingDiv(false);
    });
}

function unPublishResource(data, url) {
    $.post(url, data, function(data) {
        showOrHiddenLoadingDiv(false);
        if (data) {
            alertShow('撤销成功!!!');
        }
    }).fail(function(e) {
        console.log(e);
        showOrHiddenLoadingDiv(false);
    });
}

function deleteCmd(formData, isLikeSearch) {
    let url = '/yuns/pcc/pdel';
    if (type === 'm_doc' || type === 'm_os') {
        url = '/yuns/fcc/fdel';
        if (type === 'm_doc') {
            formData.append('fileType', 'doc');
        } else if (type === 'm_os') {
            formData.append('fileType', 'ores');
        }
    } else if (type === 'm_art') {
        url = '/yuns/artcc/artdel';
        formData.append('fileType', 'article');
    }
    $.ajax({
        type: "POST",
        url: url,
        data: formData,
        contentType: false, //禁止设置请求类型
        processData: false, //禁止jquery对DAta数据的处理,默认会处理
        success: function(data) {
            showOrHiddenLoadingDiv(false);
            if (data === '0') {
                alertShow('服务器端出现了某种问题，删除失败!!!');
                return;
            }
            /**
             * 更新视图
             */
            query('del');
            if (document.getElementById('show_username_ul_id')) {
                if (isLikeSearch || $('#show_username_ul_id').css('display') !==
                    'none') {
                    querySearchLink('del');
                }
            }
        },
        error: function(e) {
            showOrHiddenLoadingDiv(false);
            alertShow('删除操作失败!!!');
            console.log(e);
        }
    });
}