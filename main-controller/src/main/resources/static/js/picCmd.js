var id = window.location.href.split('=')[1];

var loadUserStorage = -1;
var maxStorage = 3 * 1024 * 1024;
let checkPromise = checkWhoCommanding(id);
checkPromise.then(value => {
  if (value && value > 0) {
    let userInfoPromise = loadSingleUserInfo(id);
    userInfoPromise.then(data => {
      if (data.storage) {
        loadUserStorage = data.storage;
        maxStorage = data.maxStorage;
      }
    }).catch(value => console.log(value));
  } else {
    confirm('您当前不是该用户的操作对象!!!');
    window.location.href = '/yuns/html/login.html';
  }
});
var type = '';
if (window.location.href.includes('u_pic')) {
  type = 'u_pic';
  // history.replaceState({}, "", "/yuns/html/u_pic.html");
} else if (window.location.href.includes('u_doc')) {
  type = 'u_doc';
  // history.replaceState({}, "", "/yuns/html/u_doc.html");
} else if (window.location.href.includes('u_os')) {
  type = 'u_os';
  // history.replaceState({}, "", "/yuns/html/u_os.html");
}

var choosePArr = [];
var existedPArr = [];
var existedPNameArr = [];
var existingPDataUrlArr = [];

/**
 * 1.寻图片
 * 2.存图片名
 * 3.将图片readAsDataURL
 * 4.修改show_pic_table_id_vue data
 */

$('#choose_file_input_id').on('change', function () {
  choosePArr = this.files;
  if (type === 'u_pic') {
    picReadAsDataURL();
  } else if (type === 'u_doc') {
    docCmd();
  } else if (type === 'u_os') {
    osCmd();
  }
});

$('#choose_file_input_id').on('click', () => {
  $('#choose_file_input_id').val('')
});

var temp = 0;
var temp2 = 0;

/**
 * 其他资源操作
 */
function osCmd() {
  showOrHiddenLoadingDiv(true);
  for (let i = 0; i < choosePArr.length; i++) {
    if (existedPArr.indexOf(choosePArr[i]) === -1) {
      existedPArr.push(choosePArr[i]);
      let name = choosePArr[i].name;
      existedPNameArr.push(name);
      /**
       * 判断图片
       */
      if (name.endsWith('.zip')) {
        existingPDataUrlArr.push('/yuns/sysimg/zip.png');
      } else if (name.endsWith('.txt')) {
        existingPDataUrlArr.push('/yuns/sysimg/txt.png');
      } else if (name.endsWith('.exe')) {
        existingPDataUrlArr.push('/yuns/sysimg/exe.png');
      }
    }
  }
  show_pic_table_id_vue.picArr = existingPDataUrlArr;
  show_pic_table_id_vue.picNameArr = existedPNameArr;
  showOrHiddenLoadingDiv(false);
}

/**
 * 文档操作
 */
function docCmd() {
  showOrHiddenLoadingDiv(true);
  for (let i = 0; i < choosePArr.length; i++) {
    if (existedPArr.indexOf(choosePArr[i]) === -1) {
      existedPArr.push(choosePArr[i]);
      let name = choosePArr[i].name;
      existedPNameArr.push(name);
      /**
       * 判断图片
       */
      if (name.endsWith('.doc')) {
        existingPDataUrlArr.push('/yuns/sysimg/doc_icon.png');
      } else if (name.endsWith('.docx')) {
        existingPDataUrlArr.push('/yuns/sysimg/docx_icon.png');
      } else if (name.endsWith('.pdf')) {
        existingPDataUrlArr.push('/yuns/sysimg/pdf_icon.png');
      } else if (name.endsWith('.xls')) {
        existingPDataUrlArr.push('/yuns/sysimg/xls_icon.png');
      } else if (name.endsWith('.xlsx')) {
        existingPDataUrlArr.push('/yuns/sysimg/xlsx_icon.png');
      } else if (name.endsWith('.ppt')) {
        existingPDataUrlArr.push('/yuns/sysimg/ppt_icon.png');
      } else {
        existingPDataUrlArr.push('/yuns/sysimg/pptx_icon.png');
      }
    }
  }
  show_pic_table_id_vue.picArr = existingPDataUrlArr;
  show_pic_table_id_vue.picNameArr = existedPNameArr;
  showOrHiddenLoadingDiv(false);
}

/*
 *1
 */
function picReadAsDataURL() {
  temp = temp2 = 0;
  for (let i = 0; i < choosePArr.length; i++) {
    if (!loadingDivIsShowing) {
      showOrHiddenLoadingDiv(true);
    }
    if (existedPArr.indexOf(choosePArr[i]) !== -1) {
      continue;
    }
    temp += i;
    let reader = new FileReader();
    //文件内容的加载是异步的所以说加载序列可能不同
    reader.tag = i;
    reader.onload = function () {
      temp2 += this.tag;
      /*
       *2
       */
      existedPNameArr.push(choosePArr[this.tag].name);
      //3
      existingPDataUrlArr.push(this.result);
      existedPArr.push(choosePArr[this.tag]);
      /*
       *  4
       */
      show_pic_table_id_vue.picArr = existingPDataUrlArr;
      show_pic_table_id_vue.picNameArr = existedPNameArr;
      if (temp2 === temp) {
        showOrHiddenLoadingDiv(false);
      }
    };
    reader.readAsDataURL(choosePArr[i]);
  }
}

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
    picNameArr: []
  },
  methods: {
    /*
     *1.
     */
    deletePic: function (index) {
      if (loadingDivIsShowing) {
        return;
      }
      showOrHiddenLoadingDiv(true);
      if (index === -1) {
        this.picArr = [];
        existingPDataUrlArr = [];
        this.picNameArr = [];
        existedPNameArr = [];
        existedPArr = [];
      } else {
        this.picArr.splice(index, 1);
        existingPDataUrlArr = this.picArr;
        this.picNameArr.splice(index, 1);
        existedPNameArr = this.picNameArr;
        existedPArr.splice(index, 1);
      }
      showOrHiddenLoadingDiv(false);
    },
    /*
     *2.
     */
    uploadPic: function (index) {
      if (loadingDivIsShowing) {
        return;
      }
      let totalUploadSize = 0;
      for (let existedPArrElement of existedPArr) {
        totalUploadSize += existedPArrElement.size;
      }
      totalUploadSize /= 1024;
      if ((totalUploadSize / 1024) > 500) {
        alertShow('上传资源过大（最大额量500MB');
        return;
      }
      /**
       * 云存储扩容
       */
      if ((loadUserStorage + parseInt(totalUploadSize)) > maxStorage) {
        alertShow('您的上传空间会超标，请扩容!!!');
        return;
      }
      showOrHiddenLoadingDiv(true);
      let formData = new FormData();
      if (index === -1) {
        existedPArr.forEach(function (value, index) {
          formData.append('files', value);
        });
      } else {
        formData.append('files', existedPArr[index]);
      }

      formData.append('id', id);
      upload(formData, index);
    }
  }
});

function upload(formData, index) {
  let url = '/yuns/pcc/up';
  if (type === 'u_doc') {
    url = '/yuns/fcc/uf'
  } else if (type === 'u_os') {
    url = '/yuns/fcc/ud'
  }
  $.ajax({
    type: "POST",
    url: url,
    data: formData,
    contentType: false, //禁止设置请求类型
    processData: false, //禁止jquery对DAta数据的处理,默认会处理
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
            $('#progressText').html(progressRate)
          }
        });
        return xhr;
      }
    },
    success: function (data) {
      showOrHiddenLoadingDiv(false);
      $('#progress > div').css('width', "0");
      $('#progress').css('display', 'none');
      if (data === '0') {
        alertShow('上传失败!!!');
        return;
      }else {
        alertShow('上传成功!!!');
      }
      show_pic_table_id_vue.deletePic(index);
    },
    error: function (e) {
      $('#progress > div').css('width', "0");
      $('#progress').css('display', 'none');
      showOrHiddenLoadingDiv(false);
      alertShow('上传失败!!!');
      console.log(e);
    }
  });
}