let alertHideTimer = '';

function alertShow(text) {

  if (document.getElementById('alert_div_id')) {
    if ($("#alert_div_id").css('display') !== 'none') {
      if (alertHideTimer) {
        clearTimeout(alertHideTimer);
      }
      $("#alert_div_id").stop()
      $("#alert_div_id").css({
        display: 'none',
        opacity: 0,
        top: '-20px'
      })
    }

  } else {
    $('  <div id="alert_div_id" class="alert alert-primary shadow" style="position:fixed;z-index:1;width:50%;left:50%;transform:translateX(-50%);display: none;opacity:0;top:-20px; text-align: center" role="alert">\n'
        + '    </div>').appendTo($('body'));
  }
  $("#alert_div_id").show();
  $("#alert_div_id").animate({
    opacity: 1,
    top: "3px"
  }, 500, function () {
    alertHideTimer = setTimeout(() => {
      alertHide();
      alertHideTimer = '';
    }, 800)
  });
  $('#alert_div_id').html(text);
}

function alertHide() {
  if (document.getElementById('alert_div_id')) {
    $("#alert_div_id").animate({
      opacity: 0,
      top: "-20px"
    }, 500, function () {
      $("#alert_div_id").hide()
    });
  }
}