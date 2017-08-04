/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


(function () {
    'use strict';
    var userNameShow,
            userLogout,
            selectBtn,
            uploadInput,
            uploadOpt = {
                total: 0,
                current: 0,
                success: 0,
                error: 0,
                paragraph: 1024 * 1024 * 2,
                state: 0
            },
            listBox;

    function initTarget()
    {
        userNameShow = $("#userNameShow");
        userNameShow.html(syfm.user.USERNAME);
        userLogout = $("#userLogout");
        selectBtn = $("#selectBtn");
        uploadInput = $("#uploadInput");
        listBox = $("#listBox");
    }

    function initAction()
    {
        userLogout.click(function () {
            $.post(syfm.apiUriRoot + 'logout').then(function () {
                location.href = syfm.apiUriRoot + 'login.html';
            });
        });

        selectBtn.click(function () {

            if (this.checked)
            {
                $("ul", listBox).addClass("listActive").find('.listFileName input[type="checkbox"]').prop('checked', true);
            } else
            {
                $("ul", listBox).removeClass("listActive").find('.listFileName input[type="checkbox"]').prop('checked', false);
            }
        });

        uploadInput.change(function () {
            var files = this.files, i = 0, file;
            if (uploadOpt.state)
            {
                return;
            }
            if (files.length > 0)
            {
                uploadOpt.total = files.length;
                for (i = 0; i < files.length; i++) {
                    file = files[i];
                    uploadFile(file, i);
                }
            }
        });


        window.onresize = function () {
            setListHeight();
        };
    }

    function uploadFile(file, i)
    {
        var startSize = 0;
        var endSize = 0;

        //获取当前文件已经上传大小
        jQuery.post(syfm.apiUriRoot + "getChunkedFileSize", {
            "fileName": encodeURIComponent(file.name),
            "fileSize": file.size,
            "uuid": UUIDjs.create().hex,
            "chunkedFileSize": "chunkedFileSize",
            "fileHash": "A877A0B1DBEC83C243CA3FE458A29DCB"
        }, function (data) {
            if (data !== -1) {
                endSize = Number(data);
            }
            uploadFileAction(file, startSize, endSize, i);

        });
    }


    /**
     * 分片上传文件
     */
    function uploadFileAction(file, startSize, endSize, i) {
        var reader = new FileReader();
        reader.onload = function (evt) {
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
                if (xhr.readyState === 4) {
                    //表示服务器的相应代码是200；正确返回了数据   
                    if (xhr.status === 200) {
                        //纯文本数据的接受方法   
                        var message = xhr.responseText;
                        message = Number(message);
                        uploadProgress(file, startSize, message, i);
                    } else {
                        msg.innerHTML = "上传出错，服务器相应错误！";
                    }
                }
            };//创建回调方法
            xhr.open("POST",syfm.apiUriRoot+"appendUploadServer?fileName=" + encodeURIComponent(file.name) + "&fileSize=" + file.size + "&uuid=4f14cdcb-ea15-49a7-8697-4b8a31b5b135" + "&fileHash=A877A0B1DBEC83C243CA3FE458A29DCB",
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
            uploadFileAction(file, startSize, uploadLen, i);
        }
    }

    /*
     暂停上传
     */
    function pauseUpload() {
        uploadState = 2;
    }


    function setListHeight()
    {
        var boxHeight = $(".syfmMain").height();
        $(".listBody").height(parseInt(boxHeight) - 96);
    }

    function initFileList()
    {
        showFileList();
    }


    function getFileList()
    {
        return $.getJSON(syfm.apiUriRoot + 'file', {
            action: 'getUserFileList',
            pageSize: 20,
            currentPage: 1
        });
    }

    function showFileList()
    {
        getFileList().then(function (data) {
            if (data && typeof data.RESULT === 'boolean')
            {
                if (!data.RESULT)
                {
                    listBox.html('');
                    $.each(data.ListArray, function (idx, item) {
                        showFileItem(item);
                    });
                    $('ul', listBox).mouseenter(function () {
                        $(this).addClass('listHover');
                    }).mouseleave(function () {
                        $(this).removeClass('listHover');
                    });
                    $('ul .listFileName input[type="checkbox"]', listBox).click(function () {
                        var grepArray;
                        if (this.checked)
                        {
                            $(this).closest('ul').addClass('listActive');
                            grepArray = $.grep($('ul .listFileName input[type="checkbox"]', listBox), function (val) {
                                if (!val.checked)
                                {
                                    return true;
                                }
                            });
                            if (grepArray.length === 0)
                            {
                                selectBtn.prop('checked', true);
                            }
                        } else
                        {
                            $(this).closest('ul').removeClass('listActive');
                            selectBtn.prop('checked', false);
                        }
                    });
                } else
                {
                    syfm.showNotify('error', data.MESSAGE);
                }
            } else
            {
                syfm.showNotify('error', '服务器相应异常。');
            }
        }, function () {
            syfm.showNotify('error', '服务器相应异常。');
        });
    }


    function showFileItem(file)
    {
        var ul = $("<ul></ul>").addClass("clearfix").appendTo(listBox);
        ul.append('<li class="listFileName"><input type="checkbox"/><span>' + file.sourcename + '</span></li>');
        ul.append('<li class="listFileSize"><span>' + file.size + '</span></li>');
        ul.append('<li class="listFileLastTime"><span>' + file.lasttime + '</span></li>');
        ul.append('<li class="listFileStatus"><span>' + formatStatus(file.status) + '</span></li>');
    }


    function formatStatus(status)
    {
        var statusStr = '';
        switch (parseInt(status))
        {
            case 0:
                statusStr = '上传未完成';
                break;
            case 1:
                statusStr = '正常';
                break;
            case - 1:
                statusStr = '文件已删除';
                break;
            default:
                statusStr = '未知状态';
        }
        return statusStr;
    }


    $(document).ready(function () {
        initTarget();
        setListHeight();
        initAction();
        initFileList();
    });
})();