<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>主页</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link type="images/x-icon" rel="shortcut icon" href="/yuns/img/logo.png">

    <link rel="stylesheet" href="/yuns/css/bootstrap.min.css">

    <script src="/yuns/js/jquery-3.4.1.min.js"></script>
    <script src="/yuns/js/vue.js"></script>
    <script src="/yuns/js/bootstrap.min.js"></script>
    <script src="/yuns/js/popper.min.js"></script>
    <link rel="stylesheet" href="/yuns/css/cusstyle.css">
    <style>
        ul li {
            text-align: left;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light shadow-sm ">
    <button class="navbar-toggler" type="button" data-toggle="collapse"
            data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
            aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item ">
                <a class=" nav-link " href="/yuns/show/main.html" target="_blank">大厅</a></li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle " href="#" id="navbarDropdown" role="button"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    我的
                </a>
                <div class="dropdown-menu " aria-labelledby="navbarDropdown">
                    <a class="dropdown-item " id="my_upload_a_id" target="_blank">已上传</a>
                    <a class="dropdown-item " id="my_doc_a_id" target="mainFrame">文档</a>
                    <a class="dropdown-item " id="my_pic_a_id" target="mainFrame">图片</a>
                    <a class="dropdown-item " id="my_art_a_id" target="mainFrame">文章</a>
                    <a class="dropdown-item " id="my_os_a_id" target="mainFrame">其他资源(.zip,.exe,.txt)</a>
                </div>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle " href="#" role="button" data-toggle="dropdown"
                   aria-haspopup="true" aria-expanded="false">
                    上传资源
                </a>
                <div class="dropdown-menu " aria-labelledby="navbarDropdown">
                    <a class="dropdown-item " id="upload_doc_a_id" target="mainFrame">文档</a>
                    <a class="dropdown-item " id="upload_pic_a_id" target="mainFrame">图片</a>
                    <a class="dropdown-item " id="upload_art_a_id" target="mainFrame">文章</a>
                    <a class="dropdown-item " id="upload_os_a_id" target="mainFrame">其他资源(.zip,.exe,.txt)</a>
                </div>
            </li>
        </ul>
    </div>
    <div id="nav_ul_id">
        <img :src="pictureUrl" id="profile_img_id"
             style="width:30px;height:30px;margin-right:10px;border-radius:50%" >
        <span id="username_span_id">{{username}}</span>
        <img style="cursor:pointer;margin-left:10px;width:30px;height:30px;border-radius:50%"
             src="/yuns/sysimg/logout.png" @click="logout"
             title="下线">
        <img src="/yuns/img/setting.png" class="dropdown-toggle"
             style="cursor:pointer;width:30px;height:30px;margin-left:10px;border-radius:50%" data-toggle="modal" data-target="#modify_info_div_id" id="setting_img_id"  title="设置">

    </div>
</nav>
    <!--
     修改信息
     -->
    <div class="modal fade" style="min-height: 80%;" id="modify_info_div_id"  data-backdrop="static" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-scrollable modal-dialog-centered" role="document">
            <div class="modal-content" style="height:100%">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">设置</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body" style="height: 80%;">
                    <div id="carouselExampleInterval" class="carousel slide " style="text-align: center; height: 100%;" data-interval="false" data-ride="carousel">
                        <div class="carousel-inner" style=" height: 100%;">
                            <div class="carousel-item active ">
                                <div class="d-block  w-100" style="text-align: center;height: 100%;">
                                    <h6>信息修改</h6>
                                    <iframe id="info_iframe_id" width="100%" height="100%" frameborder="0"></iframe>
                                </div>
                            </div>
<%--                            <div class="carousel-item  ">--%>
<%--                                <div class="d-block w-100" style="text-align: center;height: 100%;">j</div>--%>
<%--                            </div>--%>
<%--                            <div class="carousel-item">--%>
<%--                                <div class="d-block w-100">x</div>--%>
<%--                            </div>--%>
                        </div>
                    </div>
                    <a style="position: fixed;z-index: 1;top:50%;transform: translateY(-150%);" href="#carouselExampleInterval" role="button" data-slide="prev">
                        <span class="carousel-control-prev-icon " style="background-color: tomato;
                            border-radius: 50%;" aria-hidden="true"></span>
                        <span class="sr-only">Previous</span>
                    </a>
                    <a style="position: fixed;z-index: 1;top:50%;transform: translateY(50%);" href="#carouselExampleInterval" role="button" data-slide="next">
                        <span class="carousel-control-next-icon" style="background-color: tomato;
                        border-radius: 50%;" aria-hidden="true"></span>
                        <span class="sr-only">Next</span>
                    </a>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
<%--                    <button type="button" class="btn btn-primary">Save changes</button>--%>
                </div>
            </div>
        </div>
    </div>

    <div style="height: 95%;overflow-y: auto">
    <iframe name="mainFrame" width="100%" height="100%" frameborder="0"></iframe>
</div>
</body>
<script src="/yuns/js/userCommand.js"></script>
<script>
  var id = ${id};
</script>
<script src="/yuns/js/main.js"></script>

</html>