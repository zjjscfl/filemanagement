/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


(function (win) {
    'use strict';
    var syfm = win.syfm || {};
    var pnotify = win.PNotify;
    syfm.apiUriRoot = "/filemanagement/";
    syfm.user = {
        USERID: 0,
        USERNAME: "",
        USERSTATUS: 0,
        space: '0M',
        limitspace: '0M'
    };
    //显示通知
    syfm.showNotify = function (type, msg) {
        pnotify.removeAll();
        new pnotify({
            title: '提示',
            text: msg,
            type: type
        });
    };

    syfm.getUser = function () {
        $.post(syfm.apiUriRoot + 'userManger', {
            action: 'getUser'
        }, function (data) {
            if (data && typeof data.RESULT === 'boolean' && !data.RESULT && data.user)
            {
                syfm.user.space = data.user.space;
                syfm.user.limitspace = data.user.limitspace;
                $("#spaceShow").html(syfm.user.space);
                $("#limitSpaceShow").html(syfm.user.limitspace);
            }
        });
    };


    syfm.initPwd = function () {
        var userPwd = $("#userPwd"), pwdModal = $("#pwdModal"), pwdForm = $("#pwdForm"), pwdPwd = $("#pwdPwd"), pwdConfirm = $("#pwdConfirm"), pwdSubmit = $("#pwdSubmit");

        userPwd.click(function () {
            pwdModal.modal('show');
            pwdForm[0].reset();
        });

        pwdSubmit.click(function () {
            if (pwdPwd.val().length === 0)
            {
                syfm.showNotify('error', '请输入新密码。');
                return;
            }
            if (pwdPwd.val().length < 6)
            {
                syfm.showNotify('error', '密码长度太短。');
                return;
            }
            if (pwdPwd.val() !== pwdConfirm.val())
            {
                syfm.showNotify('error', '两次密码不一致。');
                return;
            }
            $.post(syfm.apiUriRoot+'userManger', {
                action: 'password',
                userid:syfm.user.USERID,
                pwd: pwdPwd.val()
            }).then(function (data) {
                if (data && typeof data.RESULT === 'boolean')
                {
                    if (!data.RESULT)
                    {
                        syfm.showNotify('success', data.MESSAGE);
                        pwdModal.modal('hide');
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
    };

    win.syfm = syfm;
    //初始化pnotify配置
    if (pnotify)
    {
        pnotify.prototype.options.delay = 2000;
        pnotify.prototype.options.styling = 'bootstrap3';
    }
})(window);


(function (win) {
    'use strict';
    var syfm = win.syfm;
    if (location.pathname.indexOf('login.html') === -1)
    {
        $.ajax(syfm.apiUriRoot + 'checkLogin', {
            async: false,
            dataType: 'json',
            method: 'POST'
        }).done(function (data) {
            var isLogin = false;
            if (data && typeof data.RESULT === 'boolean' && !data.RESULT)
            {
                syfm.user = {
                    USERID: data.USERID,
                    USERNAME: data.USERNAME,
                    USERSTATUS: data.USERSTATUS
                };
                isLogin = true;
            }
            if (!isLogin)
            {
                location.href = syfm.apiUriRoot + 'login.html';
            }
        }).fail(function () {
            location.href = syfm.apiUriRoot + 'login.html';
        });
    }


})(window);
