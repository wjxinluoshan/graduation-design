var cPromise = checkWhoCommanding(0);
cPromise.then(value => {
  if (value !== 0) {
    window.location.href = '/yuns/user/main?data=' + value;
  }
});
const login_div_id_vue = new Vue({
  el: '#login_div_id',
  data: {
    username: '',
    password: '',
    btIsClick: false
  },
  methods: {
    click: function (data) {
      if (loadingDivIsShowing) {
        return;
      }
      switch (data) {
        case 0:
          //登录
          if (this.username.trim() === '' || this.password.trim() === '') {
            alertShow("请完善信息!!!");
            return;
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
                url: '/yuns/user/login',
                data: {
                  username: login_div_id_vue.username,
                  data: encrypt.encrypt(login_div_id_vue.password)
                },
                success: function (data) {
                  showOrHiddenLoadingDiv(false);
                  //登录成功
                  if (data) {
                    // window.location.href = '/yuns/hc/main?id=' + data;
                    // window.location.href = '/yuns/user/main?data=' + data;
                    window.location.href = '/yuns/jsp/main.html?data=' + data;
                    // $.ajax({
                    //   method:'post',
                    //   url:'/music/hc/main',
                    //   data:{
                    //     id:data
                    //   },
                    //   success:function () {
                    //
                    //   },
                    //   error:function () {
                    //
                    //   }
                    // });
                  } else {
                    alertShow('用户信息验证失败!!!!');
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
              console.log(error);
            }
          });

          break;
        case 1:
          showOrHiddenLoadingDiv(false);
          //注册
          window.location.href = 'registry.html';
          break;
        case 2:
          showOrHiddenLoadingDiv(false);
          //重置
          this.username = '';
          this.password = '';
          break;
        case 3:
          window.open('/yuns/html/findPsw.html','_blank');
          break;
      }
    }
  }
});

document.addEventListener('keydown', function (event) {
  if (event.code === 'Enter') {
    login_div_id_vue.click(0);
  }
});