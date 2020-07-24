/**
 *  <div class="loading_div" id="loading_div">
 <div></div> <img src="./imgs/loading.gif" /></div>
 */
//div是否显示的状态机
var loadingDivIsShowing = false;

function showOrHiddenLoadingDiv(isShowing) {
    if (isShowing) {
        loadingDivIsShowing = true;
        //当数据加载超过0.4s时就显示进度div
        setTimeout(() => {
            if (loadingDivIsShowing)
                $('#load_div_id').css('display', 'block');
        }, 400);
    } else {
        loadingDivIsShowing = false;
        $('#load_div_id').css('display', 'none');
    }
}