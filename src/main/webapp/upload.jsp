<%-- 
    Document   : upload
    Created on : 2017-7-13, 15:20:54
    Author     : ubuntu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>断点续传文件</title>
        <meta charset="utf-8">
        <script src="http://libs.baidu.com/jquery/2.1.1/jquery.min.js"></script>
        <script type="text/javascript">
            var msg = null;
            var paragraph = 1024 * 1024 * 2;  //每次分片传输文件的大小 2M
            var blob = null;//  分片数据的载体Blob对象
            var fileList = null; //传输的文件
            var uploadState = 0;  // 0: 无上传/取消， 1： 上传中， 2： 暂停

//初始化消息框
            function init() {
                msg = document.getElementById("msg");
            }
            function uploadFiles() {
                //将上传状态设置成1
                uploadState = 1;
                if (fileList.files.length > 0) {
                    for (var i = 0; i < fileList.files.length; i++) {
                        var file = fileList.files[i];
                        uploadFileInit(file, i);
                    }
                } else {
                    msg.innerHTML = "请选择上传文件！";
                }
            }
            /**
             * 获取服务器文件大小，开始续传
             * @param file
             * @param i
             */
            function uploadFileInit(file, i) {
                if (file) {
                    var startSize = 0;
                    var endSize = 0;
                    var date = file.lastModifiedDate;

                    //获取当前文件已经上传大小
                    jQuery.post("/filemanagement/getChunkedFileSize",
                            {"fileName": encodeURIComponent(file.name), "fileSize": file.size, "uuid": uuid, "chunkedFileSize": "chunkedFileSize"},
                            function (data) {
                                if (data != -1) {
                                    endSize = Number(data);
                                }
                                uploadFile(file, startSize, endSize, i);

                            });

                }
            }
            /**
             * 分片上传文件
             */
            function uploadFile(file, startSize, endSize, i) {
                var date = file.lastModifiedDate;
                var reader = new FileReader();
                reader.onload = function loaded(evt) {
                    // 构造 xmlHttpRequest 对象，发送文件 Binary 数据
                    var xhr = new XMLHttpRequest();
                    xhr.sendAsBinary = function (text) {
                        var data = new ArrayBuffer(text.length);
                        var ui8a = new Uint8Array(data, 0);
                        for (var i = 0; i < text.length; i++)
                            ui8a[i] = (text.charCodeAt(i) & 0xff);
                        this.send(ui8a);
                    }

                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4) {
                            //表示服务器的相应代码是200；正确返回了数据   
                            if (xhr.status == 200) {
                                //纯文本数据的接受方法   
                                var message = xhr.responseText;
                                message = Number(message);
                                uploadProgress(file, startSize, message, i);
                            } else {
                                msg.innerHTML = "上传出错，服务器相应错误！";
                            }
                        }
                    };//创建回调方法
                    xhr.open("POST",
                            "/filemanagement/appendUploadServer?fileName=" + encodeURIComponent(file.name) + "&fileSize=" + file.size + "&uuid=",
                            false);
                    xhr.overrideMimeType("application/octet-stream;charset=utf-8");
                    xhr.sendAsBinary(evt.target.result);
                };
                if (endSize < file.size) {
                    //处理文件发送（字节）
                    startSize = endSize;
                    if (paragraph > (file.size - endSize)) {
                        endSize = file.size;
                    } else {
                        endSize += paragraph;
                    }
                    if (file.webkitSlice) {
                        //webkit浏览器
                        blob = file.webkitSlice(startSize, endSize);
                    } else
                        blob = file.slice(startSize, endSize);
                    reader.readAsBinaryString(blob);
                } else {
                    document.getElementById('progressNumber' + i).innerHTML = '100%';
                }
            }

//显示处理进程
            function uploadProgress(file, startSize, uploadLen, i) {
                var percentComplete = Math.round(uploadLen * 100 / file.size);
                document.getElementById('progressNumber' + i).innerHTML = percentComplete.toString() + '%';
                //续传
                if (uploadState == 1) {
                    uploadFile(file, startSize, uploadLen, i);
                }
            }

            /*
             暂停上传
             */
            function pauseUpload() {
                uploadState = 2;
            }

            /**
             * 选择文件之后触发事件
             */
            function fileSelected() {
                fileList = document.getElementById('fileToUpload');
                var length = fileList.files.length;
                var frame = document.getElementById('fileFrame');
                frame.innerHTML = '';
                for (var i = 0; i < length; i++) {
                    file = fileList.files[i];
                    if (file) {
                        var fileSize = 0;
                        if (file.size > 1024 * 1024)
                            fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
                        else
                            fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';
                        var nameDiv = document.createElement("div");
                        nameDiv.setAttribute("id", "fileName" + i);
                        nameDiv.innerHTML = 'Name: ' + file.name;
                        var sizeDiv = document.createElement("div");
                        sizeDiv.setAttribute("id", "fileSize" + i);
                        sizeDiv.innerHTML = 'fileSize: ' + fileSize;
                        var typeDiv = document.createElement("div");
                        typeDiv.setAttribute("id", "progressNumber" + i);
                        typeDiv.innerHTML = '';
                    }
                    frame.appendChild(nameDiv);
                    frame.appendChild(sizeDiv);
                    frame.appendChild(typeDiv);
                }
            }
        </script>
    </head>


    <body onload="init();">
        <div class="row">
            <label for="fileToUpload">请选择需要上传的文件</label>
            <input type="file" name="fileToUpload" id="fileToUpload" onchange="fileSelected();" multiple/>
        </div>
        <div class="row" id="fileFrame">

        </div>
        <div class="row">
            <button onclick="uploadFiles()">上传</button>
            <button onclick="pauseUpload()">暂停</button>
            &nbsp;<label id="progressNumber"></label>
        </div>
        <div id="msg" style="max-height: 400px; overflow:auto;min-height: 100px;">
        </div>
        <br>
        <div><h6>支持批量，支持断点续传</h6></div>
    </body>
</html>