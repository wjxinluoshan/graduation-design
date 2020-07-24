var id = '';
var articleLinkName = '';
/**
 * 异步加载js资源
 */
$.getScript('/yuns/js/bootstrap.min.js', function (data) {
  $('<script>' + data + '</script>').appendTo($('html'));
  if (window.location.href.includes('&')) {
    $.getScript('/yuns/js/network.js', (data) => {
      $('<script>' + data + '</script>').appendTo($('html'));
    })
  }
});
if (window.location.href.includes('&')) {
  var arr = window.location.href.split('&');
  id = arr[0].split('=')[1];
  articleLinkName = arr[1].split('=')[1];
  $('#btn_id').html('修改');
  // history.replaceState({}, "", "/yuns/article/" + articleLinkName);
} else {
  id = window.location.href.split('=')[1];
}
/**
 * 富文本编辑器
 */
// Replace the <textarea id="editor_textarea"> with a CKEditor
// instance, using default configuration.
CKEDITOR.replace('editor_textarea', {height: '500px'});
CKEDITOR.instances.editor_textarea.on('instanceReady', function (event) {
  this.document.on("paste", function (e) {
    if (loadingDivIsShowing) {
      return;
    }
    //获取该ckeditor实例的所有剪切板数据
    let items = e.data.$.clipboardData.items;
    for (let i = 0; i < items.length; ++i) {
      let item = items[i];
      if (item.kind === 'file' && item.type.includes('image/')) {
        let imgFile = item.getAsFile();
        if (!imgFile) {
          continue;
        }
        let reader = new FileReader();
        reader.readAsDataURL(imgFile);
        showOrHiddenLoadingDiv(true);
        reader.onload = function (e) {
          if (this.result.includes('base64')) {
            CKEDITOR.instances["editor_textarea"].insertHtml(
                '<img src="' + this.result + '" alt="加载失败!!!" />');
          }
          showOrHiddenLoadingDiv(false);
        };
      }
    }
  });
});
$('#editor_textarea_div').css('display', 'none');

/**
 * Markdown编辑器
 */
var markdownEditor;

function markDownDefaultValue(value, isNotEdit) {
  markdownEditor = editormd("editor_textarea_markdown", {
    width: "100%",
    value: value,
    placeholder: '请在这里编写您的文章!!!',
    htmlDecode: true,
    height: 640,
    path: "/yuns/editor.md/lib/",
    // pluginPath: '/yuns/editor.md/plugins/',
    emoji: true,
    onload: function () {
      if (isNotEdit) {
        $('#editor_textarea_markdown').css('display', 'none');
      }
    }
  });
}

const editor_div_id_vue = new Vue({
  el: '#editor_div_id',
  data: {
    titleName: '',
    editorType: 1
  },
  methods: {
    /**
     *切换文本编辑器
     */
    chooseRichEditor: function () {
      $('#editor_textarea_div').css('display', 'block');
      $('#editor_textarea_markdown').css('display', 'none');
      this.editorType = 0;
    }
    ,
    /**
     * 切换markdown编辑器
     */
    chooseMDEditor: function () {
      $('#editor_textarea_markdown').css('display', 'block');
      $('#editor_textarea_div').css('display', 'none');
      this.editorType = 1;
    }
    ,
    uploadArt: function () {
      if (loadingDivIsShowing) {
        alertShow('请稍后操作!!!');
        return;
      }
      this.titleName = this.titleName.trim();
      if (!this.titleName) {
        alertShow('请输入文章名!!!');
        return;
      }
      if (this.titleName.length > 50) {
        alertShow('文章名最多50字!!!');
        return;
      }
      showOrHiddenLoadingDiv(true);
      let url = '';
      if ($('#btn_id').html() === '修改') {
        url = '/yuns/artcc/edtart';
      } else {
        url = '/yuns/artcc/uart';
      }
      let data;
      if (this.editorType === 0) {
        data = {
          titleName: this.titleName,
          //html数据
          content: CKEDITOR.instances.editor_textarea.getData(),
          id: id,
          articleLinkName: articleLinkName,
          et: "0"
        };
      } else {
        data = {
          titleName: this.titleName,
          //html数据
          content: markdownEditor.markdownTextarea[0].value,
          id: id,
          articleLinkName: articleLinkName,
          et: "1"
        }
      }
      $.ajax({
        method: 'post',
        url: url,
        data: data,
        xhr: function () {
          if (loadingDivIsShowing) {
            let xhr = new XMLHttpRequest();
            /*
             *实现进度条的效果
             */
            xhr.upload.addEventListener('progress', function (e) {
              if (!document.getElementById('progress')) {
                $('<div id="progress" style="position: fixed;top: 80%;'
                    + 'z-index: 102;width: 100%;"> <div style="width:0;height: 5px;background-color: aqua;"></div>\n'
                    + '<span id="progressText" class="mt-2" style="margin-left:50%;transform:translateX(-50%);font-size:larger;font-weight: bold;font-style: italic;"></span>'
                    + '</div>').appendTo($('body'));
              } else {
                $('#progress').css('display', 'block');
              }
              let progressRate = parseInt(e.loaded / e.total * 100) + '%';
              if ($('#load_div_id').css('display') === 'block') {
                $('#progress > div').css('width', progressRate);
                $('#progressText').html(progressRate);
              }
            });
            return xhr;
          }
        },
        success: function (data) {
          showOrHiddenLoadingDiv(false);
          $('#progress > div').css('width', "0");
          $('#progress').css('display', 'none');
          if (data === '1') {
            if (articleLinkName) {
              alertShow('修改成功!!!');
              return;
            }
            alertShow('上传成功!!!');
            CKEDITOR.instances.editor_textarea.setData('请在这里编写您的文章!!!');
            editor_div_id_vue.titleName = '';
          } else {
            if (articleLinkName) {
              alertShow('修改失败,可能您的上传空间不够了，需要扩容!!!');
              return;
            }
            alertShow('上传失败，可能您的上传空间不够了，需要扩容!!!');
          }
        },
        error: function (e) {
          showOrHiddenLoadingDiv(false);
          $('#progress > div').css('width', "0");
          $('#progress').css('display', 'none');
          alertShow('上传失败!!!');
          console.log(e);
        }
      });
    }
  }
});
let checkPromise = checkWhoCommanding(id);
checkPromise.then(value => {
  if (value && value > 0) {
    /**
     * 加载文章内容
     */
    if (articleLinkName) {
      showOrHiddenLoadingDiv(true);
      $.post('/yuns/artcc/artcont', {
        id: id,
        articleLinkName: articleLinkName
      }, function (data) {
        showOrHiddenLoadingDiv(false);
        if (data === '0') {
          alertShow('文章加载失败!!!');
        } else {
          if (data.content.startsWith("---richEditor---")) {
            CKEDITOR.instances.editor_textarea.setData(
                data.content.split('---richEditor---')[1]);
            $('#editor_textarea_div').css('display', 'block');
            markDownDefaultValue('', true);
            editor_div_id_vue.editorType = 0;
          } else {
            markDownDefaultValue(data.content.split('---mdEditor---')[1]);
          }
          editor_div_id_vue.titleName = data.title;
        }

      }).fail(function (e) {
        showOrHiddenLoadingDiv(false);
        alertShow('文章加载失败!!!');
        console.log(e);
      })
    } else {
      markDownDefaultValue('');
    }
  } else {
    confirm('您当前不是该用户的操作对象!!!');
    window.location.href = '/yuns/html/login.html';
  }
});
