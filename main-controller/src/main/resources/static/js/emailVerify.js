const codeDemo = '0Qq1Aa2Zz3Ee4Dd5Cc6Rr7Ff8Vv9Tt0Gg9Bb8Yy7Hh6Nn5Uu4Jj3Mm2Ii1Kk_Oo!LlPp';
var timerOfAcquireCode = '';
var timeTextTimer = '';
var timerIndex = 60;

var acquireCodePost = '';
var acquireCodePostTimer = '';

var cookieCode='';

function findPsw(email) {
  cookieCode='';
  for (let i = 0; i < 15; i++) {
    cookieCode += codeDemo[parseInt(Math.random() * codeDemo.length)];
  }
  document.getElementById('email_input_id').setAttribute('disabled',
      'disabled');
  document.getElementById('get_code_btn_id').setAttribute('disabled',
      'disabled');
  $.post('/yuns/user/fpwd', {email: email.trim(), code: cookieCode}, () => {
    alertShow('邮件发送中...');
    /**
     * 修改时间
     */
    timeTextTimer = setInterval(() => {
      $('#get_code_btn_id').html(timerIndex + ' s');
      timerIndex--;
    }, 1000);
    /**
     * 定时器
     * @type {number}
     */
    timerOfAcquireCode = setTimeout(() => {
      clearInterval(timeTextTimer);
      timerIndex = 60;
      $('#get_code_btn_id').html('获取验证码');
      $('#email_input_id').removeAttr('disabled');
      $('#get_code_btn_id').removeAttr('disabled');
      alertShow('上一个邮箱请求失败或者无效!!!');

      if (acquireCodePostTimer) {
        clearTimeout(acquireCodePostTimer);
      }
      if (acquireCodePost) {
        acquireCodePost.abort();
      }
    }, 60000);
    acquireCode();
  }).fail(function (err) {
    console.log(err);
  })
}

function acquireCode(code) {
  let data = {};
  if (code) {
    data = {
      vCode: code
    }
  }
  acquireCodePost = $.ajax({
    type: "POST",
    url: '/yuns/user/eamilsf',
    data: data,
    success: (data) => {
      if (data) {
        if (!code) {
          if (timerOfAcquireCode) {
            clearTimeout(timerOfAcquireCode);
          }
          if (timeTextTimer) {
            clearInterval(timeTextTimer);
            timerIndex = 0;
            $('#get_code_btn_id').html('获取验证码');
          }
          $('#v_code_input_id').removeAttr('disabled');
          $('#v_code_btn_id').removeAttr('disabled');
          alertShow('请在5分钟内输入您收取到的邮箱验证码!!!');
          return;
        }

        document.getElementById('v_code_input_id').setAttribute('disabled',
            'disabled');
        document.getElementById('v_code_btn_id').setAttribute('disabled',
            'disabled');

        $('#confirm_btn_id').removeAttr('disabled');

        /**
         * 修改信息
         */
        try {
          if (id) {
            document.getElementById('confirm_btn_id').setAttribute('tag',
                1);
          }
        } catch (e) {
        }

        if (window.location.href.includes('findPsw.html')) {
          $('#psw_div_id').css('display', 'block');
        }

        if (acquireCodePostTimer) {
          clearTimeout(acquireCodePostTimer);
        }
      } else {
        if (!code) {
          if (acquireCodePostTimer) {
            clearTimeout(acquireCodePostTimer);
          }
          acquireCodePostTimer = setTimeout(() => {
            acquireCode();
          }, 2000);
        } else {
          alertShow('验证码输入不正确或者验证码超时无效!!!');
        }
      }
    },
    error: (err) => {
      console.log(err);
    }
  });
}