/**
 *分享给QQ好友接口
 */
function shareQQFriend(url, picUrl, title, summary, desc) {
  window.open('http://connect.qq.com/widget/shareqq/index.html?'
      + 'sharesource=qzone&title='
      + title
      + '&summary=' + summary + '&desc=' + desc + '&url=' + url + '&pic='
      + picUrl, '_blank');
}

/**
 *
 */
function shareWechat(url,divId) {
  $("#"+divId).qrcode({
    text: url,
    width:100,
    height:100,
    background: "#ffffff",
    foreground: "#000000"
  });
}