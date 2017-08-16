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
            actionList,
            updateBtn,
            delBtn,
            addModal,
            addForm,
            addName,
            addSpace,
            addSpaceType,
            addSubmit,
            updateModal,
            updateForm,
            updateId,
            updateName,
            updateSpace,
            updateSpaceType,
            updateSubmit,
            listBox;

    function initTarget()
    {
        userNameShow = $("#userNameShow");
        userNameShow.html(syfm.user.USERNAME);
        userLogout = $("#userLogout");
        selectBtn = $("#selectBtn");
        addBtn = $("#addBtn");
        refreshBtn = $("#refreshBtn");
        actionList = $("#actionList");
        updateBtn = $("#updateBtn");
        delBtn = $("#delBtn");
        addModal = $("#addModal");
        addForm = $("#addForm");
        addName = $("#addName");
        addSpace = $("#addSpace");
        addSpaceType = $("#addSpaceType");
        addSubmit = $("#addSubmit");
        updateModal = $("#updateModal");
        updateForm = $("#updateForm");
        updateId = $("#updateId");
        updateName = $("#updateName");
        updateSpace = $("#updateSpace");
        updateSpaceType = $("#updateSpaceType");
        updateSubmit = $("#updateSubmit");
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
            var tagList = $("ul", listBox), user;
            if (this.checked)
            {
                if (tagList.length > 0)
                {
                    tagList.addClass("listActive").find('.listFileName input[type="checkbox"]').prop('checked', true);
                    actionList.show();
                    if (tagList.length === 1)
                    {
                        updateBtn.show();
                    } else
                    {
                        updateBtn.hide();
                    }
                }
            } else
            {
                tagList.removeClass("listActive").find('.listFileName input[type="checkbox"]').prop('checked', false);
                actionList.hide();
            }
        });
        
        

        addBtn.click(function () {
            addModal.modal('show');
            addForm[0].reset();
        });

        addSubmit.click(function () {
            var space = 0;
            if (addName.val().length === 0)
            {
                syfm.showNotify('error', '请输入姓名。');
                return;
            }
            if (addSpace.val().length === 0)
            {
                syfm.showNotify('error', '请输入空间。');
                return;
            }
            if (isNaN(parseInt(addSpace.val())))
            {
                syfm.showNotify('error', '请输入有效的空间，只能为数字。');
                return;
            }
            space = parseInt(addSpace.val());
            if (addSpaceType.val() === 'gb')
            {
                space = space * 1024;
            }
            $.post(syfm.apiUriRoot + 'userManger', {
                action: 'add',
                name: addName.val(),
                space: space
            }).then(function (data) {
                if (data && typeof data.RESULT === 'boolean')
                {
                    if (!data.RESULT)
                    {
                        syfm.showNotify('success', data.MESSAGE);
                        addModal.modal('hide');
                        refreshUserList();
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

        });


        updateBtn.click(function () {
            var selectList = [];
            $('ul .listFileName input[type="checkbox"]', listBox).each(function (idx, item) {
                var user = $(item).closest("ul").data("user");
                if (this.checked)
                {
                    selectList.push(user);
                }
            });
            if (selectList.length === 0)
            {
                syfm.showNotify('error', "请选择一位用户。");
                return;
            }
            updateModal.modal('show');
            updateForm[0].reset();
            updateId.val(selectList[0].id);
            updateName.val(selectList[0].name);
        });

        updateSubmit.click(function () {
            var space = 0;
            if (updateId.val().length === 0)
            {
                syfm.showNotify('error', '数据异常，请关闭编辑框并重试。');
                return;
            }
            if (updateName.val().length === 0)
            {
                syfm.showNotify('error', '请输入姓名。');
                return;
            }
            if (updateSpace.val().length === 0)
            {
                syfm.showNotify('error', '请输入空间。');
                return;
            }
            if (isNaN(parseInt(updateSpace.val())))
            {
                syfm.showNotify('error', '请输入有效的空间，只能为数字。');
                return;
            }
            space = parseInt(updateSpace.val());
            if (updateSpaceType.val() === 'gb')
            {
                space = space * 1024;
            }
            $.post(syfm.apiUriRoot + 'userManger', {
                action: 'update',
                userid: updateId.val(),
                name: updateName.val(),
                space: space
            }).then(function (data) {
                if (data && typeof data.RESULT === 'boolean')
                {
                    if (!data.RESULT)
                    {
                        syfm.showNotify('success', data.MESSAGE);
                        updateModal.modal('hide');
                        refreshUserList();
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

        });

        refreshBtn.click(function () {
            refreshUserList();
        });


        delBtn.click(function () {
            var selectList = [];
            $('ul .listFileName input[type="checkbox"]', listBox).each(function (idx, item) {
                var user = $(item).closest("ul").data("user");
                if (this.checked)
                {
                    selectList.push(user.id);
                }
            });
            if (selectList.length === 0)
            {
                syfm.showNotify('error', "请至少选择一位用户。");
                return;
            }
            $.post(syfm.apiUriRoot + 'file', {
                action: 'delFile',
                fileid: selectList.join(',')
            }).then(function (data) {
                if (data && typeof data.RESULT === 'boolean')
                {
                    if (!data.RESULT)
                    {
                        syfm.showNotify('success', data.MESSAGE);
                        refreshFileList();
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


        window.onresize = function () {
            setListHeight();
        };
    }



    function setListHeight()
    {
        var boxHeight = $(".syfmMain").height();
        $(".listBody").height(parseInt(boxHeight) - 96);
    }

    function initUserList()
    {
        showUserList();
    }


    function getUserList()
    {
        return $.getJSON(syfm.apiUriRoot + 'userManger', {
            action: 'getList',
            id: syfm.user.USERID
        });
    }

    function refreshUserList()
    {
        syfm.getUser();
        showUserList();
    }


    function showUserList()
    {
        getUserList().then(function (data) {
            if (data && typeof data.RESULT === 'boolean')
            {
                if (!data.RESULT)
                {
                    listBox.html('');
                    selectBtn.prop('checked', false);
                    actionList.hide();
                    $.each(data.userList, function (idx, item) {
                        showUserItem(item);
                    });
                    $('ul', listBox).mouseenter(function () {
                        $(this).addClass('listHover');
                    }).mouseleave(function () {
                        $(this).removeClass('listHover');
                    }).each(function (idx, item) {
                        var user = data.userList[idx];
                        $(item).data("user", user);
                        if (user.status === -1)
                        {
                            $(item).addClass("user-disable");
                        } else if (user.status === 0)
                        {
                            $(item).addClass("file-normal");
                        }
                    });
                    $('ul .listFileName input[type="checkbox"]', listBox).click(function () {
                        var grepArray, tagList, itemBox, user;
                        itemBox = $(this).closest('ul');
                        user = itemBox.data("user");
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
                                updateBtn.show();
                            } else
                            {
                                updateBtn.hide();
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
                                    updateBtn.show();
                                } else
                                {
                                    updateBtn.hide();
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


    function showUserItem(user)
    {
        var ul = $("<ul></ul>").addClass("clearfix").appendTo(listBox);
        ul.append('<li class="listFileName"><input type="checkbox"/><span>' + user.name + '</span></li>');
        ul.append('<li class="listFileSize"><span>' + user.space + '</span></li>');
        ul.append('<li class="listFileLastTime"><span>' + user.limitspace + '</span></li>');
        ul.append('<li class="listFileStatus"><span>' + formatStatus(user.status) + '</span></li>');
    }


    function formatStatus(status)
    {
        var statusStr = '';
        switch (parseInt(status))
        {
            case 0:
                statusStr = '正常';
                break;
            case - 1:
                statusStr = '禁用';
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
        syfm.getUser();
        syfm.initPwd();
        initUserList();
    });
})();