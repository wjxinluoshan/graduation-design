/**
 * 修改信息
 * @returns {boolean}
 */

var id = '';
if (window.location.href.includes("=")) {
  id = window.location.href.split('=')[1];
  checkWhoCommanding(id).then(
      value => {
        if (!value || value == 0) {
          confirm('您当前不是该用户的操作对象!!!');
          window.location.href = '/yuns/html/login.html';
        }
        $('#confirm_btn_id').removeAttr('disabled');
      }
  );
}

/**
 * 注册
 * @returns {boolean}
 */
function preCheck() {
  if (signup_div_id_vue.profileBinaryData.trim() === ''
      || signup_div_id_vue.username.trim() === '' ||
      signup_div_id_vue.password.trim() === '' || signup_div_id_vue.email.trim()
      === ''
      || signup_div_id_vue.passwordConfirm === '') {
    alertShow('请完善您的注册信息!!!!');
    return false;
  }
  if ($('#pwdAlert_div_id').css('display') === 'block') {
    if (!signup_div_id_vue.pwdConfirm()) {
      return false;
    }
  }
  if (signup_div_id_vue.username.length > 20) {
    alertShow('用户名过长(最多20字)!!!');
    return false;
  }
  //密码检测
  /*
        * 1.8 or more characters, but not more than 16 characters
          2.one or more uppercase characters
          3.one or more lowercase characters
          4.one or more digits
          5.one or more special characters (like $, @, or !)
   * */
  let regPwd = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[~.,;!@#$%^&*])[\da-zA-Z~.,;!@#$%^&*]{8,16}$/;
  if (!regPwd.test(signup_div_id_vue.password)) {
    alertShow('密码不合法!!!');
    return false;
  }
  return true;
}

var profileFile = '';
const signup_div_id_vue = new Vue({
  el: '#signup_div_id',
  data: {
    profileBinaryData: '',
    username: '',
    password: '',
    email: '',
    btIsClick: false,
    passwordConfirm: '',
    code: '',
  },
  methods: {
    pwdConfirm: function () {
      if (this.password !== this.passwordConfirm) {
        $('#pwdAlert_div_id').css({display: 'block'});
        return false;
      }
      $('#pwdAlert_div_id').css({display: 'none'});
      return true;
    },
    profileImgClick: function () {
      if (document.getElementById("_ef")) {
        document.getElementById("_ef").remove();
      }
      let inputObj = document.createElement('input');
      inputObj.setAttribute('id', '_ef');
      inputObj.setAttribute('type', 'file');
      //设置input所选取文件的为图片
      inputObj.setAttribute("accept", "image/*");
      inputObj.setAttribute("style", 'visibility:hidden');
      document.body.appendChild(inputObj);
      inputObj.click();
      //进行图片的选择
      inputObj.onchange = function (event) {
        let resultFile = profileFile = inputObj.files[0];
        if (resultFile) {
          //将图像转换成可以预览的
          let reader = new FileReader();
          reader.onload = function () {
            let urlData = signup_div_id_vue.profileBinaryData = this.result;
            $('#profile_img_id').css({
              width: 'max-content',
              height: 'max-content',
              display: 'none'
            });
            //预览图片
            $('#profile_img_id').attr("src", urlData);
            let img = document.getElementById('profile_img_id');
            img.onload = function () {
              let width = parseInt($('#profile_img_id').css('width'));
              let height = parseInt($('#profile_img_id').css('height'));
              let h = 150 * height / width + 'px';
              $('#profile_img_id').css({
                width: '150px',
                height: h,
                display: 'inline-block'
              })
            }
          };
          reader.readAsDataURL(resultFile);
        }
        $("#_ef").remove();
      }
    },
    click: function (param) {
      if (loadingDivIsShowing) {
        return;
      }
      switch (param) {
          //确认
        case 0:
          let encrypt = '';
          /**
           * 修改信息
           */
          if (id) {
            //检查密码
            this.pwdConfirm();
            if ($('#pwdAlert_div_id').css('display') === 'block') {
              this.passwordConfirm = this.password = '';
              this.pwdConfirm();
            }
            if (this.password) {
              let regPwd = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[~.,;!@#$%^&*])[\da-zA-Z~.,;!@#$%^&*]{8,16}$/;
              if (!regPwd.test(this.password)) {
                alertShow('密码不合法!!!');
                this.passwordConfirm = this.password = '';
              }
            }
            let canChange = false;
            let formData = new FormData();
            //是否包含密码
            if (this.password) {
              encrypt = new JSEncrypt();
              pk().then(data => {
                encrypt.setPublicKey(
                    '-----BEGIN PUBLIC KEY-----' + data
                    + '-----END PUBLIC KEY-----');
                formData.append("data",
                    encrypt.encrypt(this.password.trim()));
                canChange = true;
                changeUserInfo(formData, canChange);
              });
            } else {
              changeUserInfo(formData, canChange);
            }
            return;
          } else {
            /**
             * 注册
             */
            if (!preCheck()) {
              return;
            }
          }
          encrypt = new JSEncrypt();
          showOrHiddenLoadingDiv(true);
          pk().then(
              data => {
                encrypt.setPublicKey(
                    '-----BEGIN PUBLIC KEY-----' + data
                    + '-----END PUBLIC KEY-----');
                let formData = new FormData();
                formData.append("profileBinaryData", profileFile);
                formData.append("username", signup_div_id_vue.username);
                formData.append("data",
                    encrypt.encrypt(signup_div_id_vue.password));
                formData.append("email", signup_div_id_vue.email);
                $.ajax({
                  method: 'post',
                  url: '/yuns/user/registry',
                  data: formData,
                  contentType: false, //禁止设置请求类型
                  processData: false, //禁止jquery对DAta数据的处理,默认会处理
                  success: function (data) {
                    showOrHiddenLoadingDiv(false);
                    //注册成功
                    if (data) {
                      if (data === 'userExisted') {
                        alertShow('用户名或邮箱已经被注册!!!');
                        return;
                      }
                      // window.location.href = '/yuns/user/main?data=' + data;
                      window.location.href = '/yuns/jsp/main.html?data=' + data;

                    } else {
                      if (data === 'userExisted') {
                        alertShow('用户名或邮箱已经被注册!!!');
                      }
                    }
                  },
                  error: function (error) {
                    showOrHiddenLoadingDiv(false);
                    console.log(error);
                  }
                });
              }
          );
          break;
          //返回登录页面
        case 1:
          showOrHiddenLoadingDiv(false);
          window.location.href = '/yuns/html/login.html';
          break;
          //重置
        case 2:
          showOrHiddenLoadingDiv(false);
          $.post('/yuns/user/eamilsf',{vCookie:cookieCode});
          signup_div_id_vue.username = '';
          signup_div_id_vue.password = '';
          signup_div_id_vue.email = '';
          signup_div_id_vue.passwordConfirm = '';
          signup_div_id_vue.code = '';

          document.getElementById('v_code_input_id').setAttribute('disabled',
              'disabled');
          document.getElementById('v_code_btn_id').setAttribute('disabled',
              'disabled');

          if (timeTextTimer) {
            clearInterval(timeTextTimer);
          }

          if (timerOfAcquireCode) {
            clearTimeout(timerOfAcquireCode);
          }
          if (acquireCodePostTimer) {
            clearTimeout(acquireCodePostTimer);
          }
          if (acquireCodePost) {
            acquireCodePost.abort();
          }
          $('#get_code_btn_id').html('获取验证码');
          $('#email_input_id').removeAttr('disabled');
          $('#get_code_btn_id').removeAttr('disabled');

          if (!id) {
            document.getElementById('confirm_btn_id').setAttribute('disabled',
                'disabled');
          }

          $('#confirm_btn_id').removeAttr('tag');

          $('#pwdAlert_div_id').css({display: 'none'});

          break;
          //邮箱
        case 3:
          let reg = new RegExp(
              "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
          //邮箱输入合法
          if (reg.test(signup_div_id_vue.email)) {
            if (signup_div_id_vue.email.length > 30) {
              alertShow('邮箱长度限制为30!!!');
              return;
            }
            findPsw(signup_div_id_vue.email.trim());
          } else {
            alertShow('邮箱输入不合法!!!');
            signup_div_id_vue.btIsClick = false;
            return;
          }
          break;

        case 4:
          let code = signup_div_id_vue.code.trim();
          if (code) {
            acquireCode(code);
          } else {
            alertShow('验证码不能为空!!!');
          }
          break;
      }
    }
  }
});

function changeUserInfo(formData, canChange) {
  if (profileFile) {
    formData.append("pf", profileFile);
    canChange = true;
  }
  if (signup_div_id_vue.username.length > 20) {
    alertShow('用户名过长(最多20字)!!!');
    this.username = '';
  } else {
    if (signup_div_id_vue.username.trim()) {
      formData.append("uname", signup_div_id_vue.username.trim());
      canChange = true;
    }
  }
  if ($('#confirm_btn_id').attr('tag')) {
    formData.append("email", signup_div_id_vue.email.trim());
    canChange = true;
  }
  if (canChange) {
    formData.append("id", id);
    showOrHiddenLoadingDiv(true);
    $.ajax({
      method: 'post',
      url: '/yuns/user/upi',
      data: formData,
      contentType: false, //禁止设置请求类型
      processData: false, //禁止jquery对DAta数据的处理,默认会处理
      success: function (data) {
        showOrHiddenLoadingDiv(false);
        if (data === '1') {
          signup_div_id_vue.click(2);
          alertShow('信息修改完毕!!!');
        } else if (data === '0') {
          signup_div_id_vue.click(2);
          alertShow('用户名或邮箱已经被注册，其它信息修改完毕!!!');
        } else {
          alertShow('信息修改失败!!!');
        }
      },
      error: function (e) {
        showOrHiddenLoadingDiv(false);
        console.log(e);
        alertShow('信息修改失败!!!');
      }
    });
  } else {
    alertShow('您未填写要需改的信息或修改不合法!!!');
  }

}

function pk() {
  return new Promise((resolve, reject) => {
    $.ajax({
      method: 'post',
      url: '/yuns/k/pubk',
      success: function (data) {
        resolve(data);
      },
      error: function (error) {
        showOrHiddenLoadingDiv(false);
        signup_div_id_vue.btIsClick = false;
        console.log(error);
      }
    });
  });

}
