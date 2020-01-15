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
        addBtn,
            refreshBtn,
            searchTxt,
            searchBtn,
            uploadInput,
            actionList,
        updateBtn,
showBtn,
            goonInputBox,
            goonInput,
            downBtn,
            delBtn,
            getOpt = {
                action: 'getFileList',
                pageSize: 100,
                currentPage: 1,
                search: ''
            },
            uploadOpt = {
                total: 0,
                current: 0,
                success: 0,
                error: 0,
                paragraph: 1024 * 1024 * 2,
                state: 0,
                uuid: '',
                md5: ''
            },
            blob,
            uploadMsg,
            listBox,
    addModal,
        addModalLabel,
        addForm,
        addId,
        addName,
        addDepartment,
        addType,
        addDate,
        addSubmit;

    var checkContract;

    function initTarget()
    {
        userNameShow = $("#userNameShow");
        userNameShow.html(syfm.user.USERNAME);
        userLogout = $("#userLogout");
        selectBtn = $("#selectBtn");
        addBtn=$("#addBtn");
        refreshBtn = $("#refreshBtn");
        actionList = $("#actionList");

        updateBtn=$("#updateBtn");
        showBtn=$("#showBtn");
        goonInputBox = $("#goonInputBox");
        goonInput = $("#goonInput");
        downBtn = $("#downBtn");
        delBtn = $("#delBtn");
        searchTxt = $("#searchTxt");
        searchBtn = $("#searchBtn");
        uploadInput = $("#uploadInput");
        uploadMsg = $(".uploadMsg");
        listBox = $("#listBox");

        addModal=$("#addModal");
            addModalLabel=$("#addModalLabel");
            addForm=$("#addForm");
            addId=$("#addId");
            addName=$("#addName");
            addDepartment=$("#addDepartment");
            addType=$("#addType");
            addDate=$("#addDate");
        addSubmit=$("#addSubmit");
    }

    function initAction()
    {



        userLogout.click(function () {
            $.post(syfm.apiUriRoot + 'logout').then(function () {
                location.href = syfm.apiUriRoot + 'login.html';
            });
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
                uploadOpt.state = 1;
                uploadFile(files, i);
            }
        });

        refreshBtn.click(function () {
            refreshContractList();
        });




        searchBtn.click(function () {
            //getOpt.search = searchTxt.val();
            //searchFileList();
        });



        window.onresize = function () {
            setListHeight();
        };


        addBtn.click(function () {
            addModalLabel.html('添加合同');
            addForm.get(0).reset();
            addDepartment.prop("disabled","");
            addModal.modal('show');
        });

        updateBtn.click(function () {
            if(!checkContract)
            {
                syfm.showNotify('error', "请选择一个合同。");
                return;
            }
            addModalLabel.html('编辑合同');
            addForm.get(0).reset();
            addId.val(checkContract.id);
            addName.val(checkContract.name);
            addDepartment.val(checkContract.department_id);
            addDepartment.prop("disabled","disable");
            addType.val(checkContract.type);
            addDate.val(checkContract.date);
            addModal.modal('show');
        });

        addSubmit.click(function () {
            var idVal=addId.val();
            var nameVal=addName.val();
            var departmentVal=addDepartment.val();
            var typeVal=addType.val();
            var dateVal=addDate.val();
            var action;
            if(!nameVal)
            {
                syfm.showNotify('error', "请输入名称。");
                return;
            }
            if(!departmentVal)
            {
                syfm.showNotify('error', "请选择一个部门。");
                return;
            }
            if(!dateVal)
            {
                syfm.showNotify('error', "请选择一个日期。");
                return;
            }
            if(idVal!=="0")
            {
                action='update';
            }
            else
            {
                action='add';
            }
            $.post(syfm.apiUriRoot + 'contract', {
                action:action,
                contract_id:idVal,
                name:nameVal,
                type:typeVal,
                department_id:departmentVal,
                date:dateVal
            }).then(function (data) {
                if (data && typeof data.RESULT === 'boolean')
                {
                    if (!data.RESULT)
                    {
                        syfm.showNotify('success', data.MESSAGE);
                        refreshContractList();
                        addModal.modal('hide');
                    } else
                    {
                        syfm.showNotify('error', data.MESSAGE);
                    }
                } else
                {
                    syfm.showNotify('error', "服务器响应异常。");
                }
            }, function () {
                syfm.showNotify('error', "服务器响应异常。");
            });
        });
    }

    function uploadFile(files, i)
    {
        var startSize = 0;
        var endSize = 0;
        var file = files[i];
        var reader = new FileReader();
        reader.onload = function (evt) {
            var uuid = UUIDjs.create().hex;
            var md5hash = md5(evt.target.result);

            uploadOpt.uuid = uuid;
            uploadOpt.md5 = md5hash;
            uploadOpt.current = i;

            //获取当前文件已经上传大小
            $.post(syfm.apiUriRoot + "getChunkedFileSize", {
                "fileName": encodeURIComponent(file.name),
                "fileSize": file.size,
                "uuid": uuid,
                "chunkedFileSize": "chunkedFileSize",
                "fileHash": md5hash
            }, function (data) {
                if (data !== '-1') {
                    endSize = Number(data);
                }
                uploadFileAction(files, startSize, endSize, i);

            });
        };
        reader.readAsArrayBuffer(file);
    }


    /**
     * 分片上传文件
     */
    function uploadFileAction(files, startSize, endSize, i) {
        var reader = new FileReader();
        var file = files[i];
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
                        uploadProgress(files, startSize, message, i);
                    } else {
                        uploadMsg.html('上传：' + (uploadOpt.current + 1) + '/' + uploadOpt.total + ' ，当前上传文件：' + file.name + '，' + formatFileSize(file.size) + '，文件上传错误');
                        uploadMsg.show();
                    }
                }
            };//创建回调方法
            xhr.open("POST", syfm.apiUriRoot + "appendUploadServer?fileName=" + encodeURIComponent(file.name) + "&fileSize=" + file.size + "&uuid=" + uploadOpt.uuid + "&fileHash=" + uploadOpt.md5,
                    false);
            xhr.overrideMimeType("application/octet-stream;charset=utf-8");
            xhr.sendAsBinary(evt.target.result);
        };
        if (endSize < file.size) {
            //处理文件发送（字节）
            startSize = endSize;
            if (uploadOpt.paragraph > (file.size - endSize)) {
                endSize = file.size;
            } else {
                endSize += uploadOpt.paragraph;
            }
            if (file.webkitSlice) {
                //webkit浏览器
                blob = file.webkitSlice(startSize, endSize);
            } else
            {
                blob = file.slice(startSize, endSize);
            }
            reader.readAsBinaryString(blob);
        } else {
            uploadMsg.html('上传：' + (uploadOpt.current + 1) + '/' + uploadOpt.total + ' ，当前上传文件：' + file.name + '，' + formatFileSize(file.size) + '，100%');
            uploadMsg.show();
            setTimeout(function () {
                if (i < files.length - 1)
                {
                    i++;
                    uploadFile(files, i);
                } else
                {
                    uploadMsg.hide();
                    uploadOpt.state = 0;
                    refreshFileList();
                }
            }, 1000);
        }
    }

//显示处理进程
    function uploadProgress(files, startSize, uploadLen, i) {
        var file = files[i];
        var percentComplete = Math.round(uploadLen * 100 / file.size);
        uploadMsg.html('上传：' + (uploadOpt.current + 1) + '/' + uploadOpt.total + ' ，当前上传文件：' + file.name + '，' + formatFileSize(file.size) + '，' + percentComplete + '%');
        uploadMsg.show();
        //续传
        if (uploadOpt.state === 1) {
            uploadFileAction(files, startSize, uploadLen, i);
        }
    }

    function formatFileSize(size)
    {
        var fileSize = 0;
        if (size > 1024 * 1024)
        {
            fileSize = (Math.round(size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
        } else {
            fileSize = (Math.round(size * 100 / 1024) / 100).toString() + 'KB';
        }
        return fileSize;
    }

    /*
     暂停上传
     */
    function pauseUpload() {
        uploadOpt.state = 2;
    }


    function setListHeight()
    {
        var boxHeight = $(".syfmMain").height();
        $(".listBody").height(parseInt(boxHeight) - 96);
    }
    
    function initContractList() {
        showContractList();
    }

    function getContractList() {
        return $.getJSON(syfm.apiUriRoot + 'contract', {action:'getAll',currentPage:1,pageSize:100});
    }

    function showContractList() {
        getContractList().then(function (data) {
            if (data && typeof data.RESULT === 'boolean')
            {
                if (!data.RESULT)
                {
                    listBox.html('');
                    actionList.hide();
                    checkContract=null;
                    $.each(data.ListArray, function (idx, item) {
                        showContractItem(item);
                    });
                    $('ul', listBox).mouseenter(function () {
                        $(this).addClass('listHover');
                    }).mouseleave(function () {
                        $(this).removeClass('listHover');
                    }).each(function (idx, item) {
                        var file = data.ListArray[idx];
                        $(item).data("contract", file);
                    });
                    $('ul', listBox).click(function () {
                        if (!$(this).hasClass("listActive"))
                        {
                            checkContract=$(this).data("contract");
                            $(this).addClass('listActive').siblings().removeClass("listActive");
                            actionList.show();
                        } else
                        {
                            $(this).removeClass('listActive');
                            actionList.hide();
                        }
                    });
                } else
                {
                    syfm.showNotify('error', data.MESSAGE);
                }
            } else
            {
                syfm.showNotify('error', '服务器响应异常。');
            }
        }, function () {
            syfm.showNotify('error', '服务器响应异常。');
        });
    }

    function showContractItem(data) {
        var ul = $("<ul></ul>").addClass("clearfix").appendTo(listBox);
        ul.append('<li class="listFileName"><span>' + data.name + '</span></li>');
        ul.append('<li class="listFileSize"><span>' + formatCode(data) + '</span></li>');
        ul.append('<li class="listFileLastTime"><span>' + data.department_name + '</span></li>');
        ul.append('<li class="listFileStatus"><span>' + data.number + '</span></li>');
        ul.append('<li class="listFileStatus"><span>' + data.date + '</span></li>');
        ul.append('<li class="listFileStatus"><span>' + formatType(data.type) + '</span></li>');
        ul.append('<li class="listFileStatus"><span>' + formatStatus(data.status) + '</span></li>');
    }

    function formatCode(item) {
        return item.code+item.date.replace(/-/g,'')+item.type+(Array(3).join(0) + item.number).slice(-3);
    }

    function formatType(type) {
        var result='';
        switch (type) {
            case "A":result="销售合同";break;
            case "B":result="采购合同";break;
        }
        return result;
    }

    function refreshContractList()
    {
        showContractList();
    }

    function initFileList()
    {
        showFileList();
    }


    function getFileList()
    {
        return $.getJSON(syfm.apiUriRoot + 'file', getOpt);
    }

    function refreshFileList()
    {
        syfm.getUser();
        showFileList();
    }


    function searchFileList()
    {
        showFileList();
    }

    function showFileList()
    {
        getFileList().then(function (data) {
            if (data && typeof data.RESULT === 'boolean')
            {
                if (!data.RESULT)
                {
                    listBox.html('');
                    selectBtn.prop('checked', false);
                    actionList.hide();
                    $.each(data.ListArray, function (idx, item) {
                        showFileItem(item);
                    });
                    $('ul', listBox).mouseenter(function () {
                        $(this).addClass('listHover');
                    }).mouseleave(function () {
                        $(this).removeClass('listHover');
                    }).each(function (idx, item) {
                        var file = data.ListArray[idx];
                        $(item).data("file", file);
                        if (file.status === -1)
                        {
                            $(item).addClass("file-delete");
                        } else if (file.status === 0)
                        {
                            $(item).addClass("file-init");
                        } else if (file.status === 1)
                        {
                            $(item).addClass("file-normal");
                        }
                    });
                    $('ul .listFileName input[type="checkbox"]', listBox).click(function () {
                        var grepArray, tagList, itemBox, file;
                        itemBox = $(this).closest('ul');
                        file = itemBox.data("file");
                        tagList = $('ul .listFileName input[type="checkbox"]', listBox);
                        if (this.checked)
                        {
                            itemBox.addClass('listActive');
                            grepArray = $.grep(tagList, function (val) {
                                if (!val.checked)
                                {
                                    return true;
                                }
                            });
                            if (grepArray.length === 0)
                            {
                                selectBtn.prop('checked', true);
                            } else if (grepArray.length === tagList.length - 1)
                            {
                                if (file.status === 0)
                                {
                                    goonInputBox.show();
                                    downBtn.hide();
                                } else if (file.status === 1)
                                {
                                    downBtn.attr("href", syfm.apiUriRoot + "fileDown?fileid=" + file.uuid + "&userid=" + file.userid);
                                    downBtn.show();
                                    goonInputBox.hide();
                                }
                            } else
                            {
                                goonInputBox.hide();
                                downBtn.hide();
                            }
                            actionList.show();
                        } else
                        {
                            $(this).closest('ul').removeClass('listActive');
                            grepArray = $.grep(tagList, function (val) {
                                if (val.checked)
                                {
                                    return true;
                                }
                            });
                            if (grepArray.length === 0)
                            {
                                actionList.hide();
                            } else
                            {
                                actionList.show();
                                if (grepArray.length === 1)
                                {
                                    if (file.status === 0)
                                    {
                                        goonInputBox.show();
                                        downBtn.hide();
                                    } else if (file.status === 1)
                                    {
                                        downBtn.attr("href", syfm.apiUriRoot + "fileDown?fileid=" + file.uuid + "&userid=" + file.userid);
                                        downBtn.show();
                                        goonInputBox.hide();
                                    }
                                } else
                                {
                                    goonInputBox.hide();
                                    downBtn.hide();
                                }
                            }
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

    function initDatepicker()
    {
        addDate.datepicker({
            todayBtn: "linked",
            language: "zh-CN",
            autoclose: true,
            todayHighlight: true
        });
    }

    function initDepartment()
    {
        $.post(syfm.apiUriRoot + "department", {
            "action": 'getAll'
        },function (data) {
            var html=[];
            if(data&&typeof data.RESULT==='boolean'&&!data.RESULT&&data.departmentList)
            {
                addDepartment.html('');
                html.push('<option value="">请选择一个部门</option>');
                $.each(data.departmentList,function (index,item) {
                    html.push('<option value="'+item.id+'">'+item.name+'</option>');
                })
                addDepartment.html(html.join(''));
            }
        },'json');
    }


    $(document).ready(function () {
        initTarget();
        setListHeight();
        initAction();
        syfm.getUser();
        syfm.initPwd();
        initDepartment();
        initContractList();
        //initFileList();
        initDatepicker();
    });
})();