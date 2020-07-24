let checkPromise = checkWhoCommanding(id);
checkPromise.then(value => {
  if (value && value > 0) {
    /**
     * 加载用户信息
     */
    let userInfoPromise = loadSingleUserInfo(id);
    userInfoPromise.then(data => {
      if (data) {
        //返回数据json形式
        if (data.username.length > 5) {
          nav_ul_id_vue.username = data.username.substring(0, 5) + '...';
        } else {
          nav_ul_id_vue.username = data.username;
        }
        nav_ul_id_vue.pictureUrl = data.pictureUrl;
        email = data.email;
      }
    }).catch(value => console.log(value));
  } else {
    confirm('您当前不是该用户的操作对象!!!');
    window.location.href = '/yuns/html/login.html';
  }
});

$('#modify_info_div_id').on('hidden.bs.modal', function () {
  nav_ul_id_vue.pictureUrl = '';
  loadSingleUserInfo(id).then(data => {
    if (data) {
      nav_ul_id_vue.pictureUrl = data.pictureUrl+'?id='+parseInt(Math.random()*10000);
      if (data.username.length > 5) {
        nav_ul_id_vue.username = data.username.substring(0, 5) + '...';
      } else {
        nav_ul_id_vue.username = data.username;
      }
    }
  });
});

/**
 *
 * @type {Vue}
 */
$('#info_iframe_id').attr('src', '/yuns/html/changeuserinfo.html?id=' + id);

const nav_ul_id_vue = new Vue({
  el: '#nav_ul_id',
  data: {
    username: 'wjx',
    pictureUrl: '/yuns/img/logo.png'
  },
  methods: {
    logout: function () {
      $.post('/yuns/user/logout', {
        id: id
      }, function () {
        window.location.href = '/yuns/html/login.html';
      }).fail(function (e) {
        console.log(e);
      });
    }
  }
});
$('#my_upload_a_id').attr('href', '/yuns/show/showuser.html?data=' + id);
$('#my_doc_a_id').attr('href', '/yuns/html/m_doc.html?data=' + id);
$('#my_pic_a_id').attr('href', '/yuns/html/m_pic.html?data=' + id);
$('#my_os_a_id').attr('href', '/yuns/html/m_os.html?data=' + id);
$('#my_art_a_id').attr('href', '/yuns/html/m_art.html?data=' + id);

$('#upload_doc_a_id').attr('href', '/yuns/html/u_doc.html?data=' + id);
$('#upload_pic_a_id').attr('href', '/yuns/html/u_pic.html?data=' + id);
$('#upload_os_a_id').attr('href', '/yuns/html/u_os.html?data=' + id);
$('#upload_art_a_id').attr('href', '/yuns/html/u_art.html?data=' + id);

var email = '';