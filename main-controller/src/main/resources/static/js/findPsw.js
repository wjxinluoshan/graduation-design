const find_psw_div_id_vue = new Vue({
  el: '#find_psw_div_id',
  data: {
    email: '',
    code: '',
    password: '',
    passwordConfirm: ''
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
    click: function (index) {
      switch (index) {
          //email
        case 0:
          //检邮箱格式
          let reg = new RegExp(
              "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
          let em = this.email.trim();
          if (em) {
            if (em.length > 30) {
              alertShow('邮箱长度限制为30!!!');
              return;
            }
            if (!reg.test(em)) {
              alertShow('邮箱输入不合法!!!');
              return;
            }
          } else {
            alertShow('邮箱输入不能为空!!!');
            return;
          }
          //开始获取验证码
          findPsw(em);
          break;
          //code
        case 1:
          let code = this.code.trim();
          if (code) {
            acquireCode(code);
          } else {
            alertShow('验证码不能为空!!!');
          }
          break;
          //confirm
        case 2:
          if (this.password.trim() === '' || this.passwordConfirm.trim() === '') {
            alertShow('请完善您的信息!!!!');
            return;
          }
          if ($('#pwdAlert_div_id').css('display') === 'block') {

            if (!this.pwdConfirm()) {
              return false;
            }

          }
          let regPwd = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[~.,;!@#$%^&*])[\da-zA-Z~.,;!@#$%^&*]{8,16}$/;
          if (!regPwd.test(this.password)) {
            alertShow('密码不合法!!!');
            return false;
          }
          let encrypt = new JSEncrypt();
          showOrHiddenLoadingDiv(true);
          $.ajax({
            method: 'post',
            url: '/yuns/k/pubk',
            success: function (data) {
              encrypt.setPublicKey(
                  '-----BEGIN PUBLIC KEY-----' + data + '-----END PUBLIC KEY-----');
              $.ajax({
                method: 'post',
                url: '/yuns/user/epsw',
                data: {
                  data: encrypt.encrypt(find_psw_div_id_vue.password),
                  email: find_psw_div_id_vue.email
                },
                success: function (data) {
                  showOrHiddenLoadingDiv(false);
                  //注册成功
                  if (data) {
                    document.getElementById('confirm_btn_id').setAttribute('disabled',
                        'disabled');
                    alertShow('密码已找回，将前往登陆页面!!!');
                    setTimeout(() => {
                      window.location.href = '/yuns/html/login.html';
                    }, 2000);
                  } else {
                    if (data === 'userExisted') {
                      alertShow('用户名和邮箱已经被注册!!!');
                    }
                  }
                },
                error: function (error) {
                  showOrHiddenLoadingDiv(false);
                  console.log(error);
                }
              });
            },
            error: function (error) {
              showOrHiddenLoadingDiv(false);
              signup_div_id_vue.btIsClick = false;
              console.log(error);
            }
          });
          break;
      }
    }

  }
});