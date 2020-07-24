window.addEventListener('offline', function (e) {
  addNetworkInfo(true);
});

window.addEventListener('online', function (e) {
  addNetworkInfo(false);
});

function addNetworkInfo(offline) {
  if (!document.getElementById('network_state_modal_id')) {
    $('<div class="modal fade" id="network_state_modal_id" data-backdrop="static" tabindex="-1" role="dialog" aria-labelledby="staticBackdropLabel" aria-hidden="true">\n'
        + '  <div class="modal-dialog modal-dialog-centered" role="document">\n'
        + '    <div class="modal-content">\n'
        + '      <div class="modal-header">\n'
        + '        <h5 class="modal-title" id="staticBackdropLabel">网络提醒</h5>\n'
        + '      </div>\n'
        + '      <div class="modal-body">\n'
        + '        您的<b style="color: indianred">网络已断开</b>，服务不能够使用，为了保证您的服务正常使用，请<b>务必保证您的网络连接正常</b>!!!\n'
        + '      </div>\n'
        + '      <div class="modal-footer">\n'
        + '      </div>\n'
        + '    </div>\n'
        + '  </div>\n'
        + '</div>').appendTo($('body'))
  }
  if (offline) {
      $('#network_state_modal_id').modal('show');
  } else {
      $('#network_state_modal_id').modal('hide');
  }
}

if (navigator.onLine) {
  addNetworkInfo(false);

} else {
  addNetworkInfo(true);
}
