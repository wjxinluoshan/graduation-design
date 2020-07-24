//检查是谁在操作
function checkWhoCommanding(id) {
  return new Promise((resolve, reject) => {
    $.post('/yuns/user/cwuc', {id: id}, function (data) {
      if (data) {
        resolve(parseInt(data));
      } else {
        resolve(0);
      }
    }).fail(function (err) {
      console.log(err);
      resolve(0);
    })
  });
}

function loadSingleUserInfo(id) {

  return new Promise((resolve, reject) => {
    /**
     * 加载用户信息
     */
    $.post('/yuns/user/info', {id: id}, function (data) {
      resolve(data);
    }).fail(function (data) {
      reject(data)
    })
  });
}
