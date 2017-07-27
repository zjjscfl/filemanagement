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
            listBox;

    function initTarget()
    {
        userNameShow = $("#userNameShow");
        userNameShow.html(syfm.user.USERNAME);
        userLogout = $("#userLogout");
        selectBtn = $("#selectBtn");
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

        window.onresize = function () {
            setListHeight();
        };
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