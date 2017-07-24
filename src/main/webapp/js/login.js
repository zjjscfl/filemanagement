/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


(function (win, doc) {
    'use strict';
    var syfm = win.syfm;

    var loginForm;
    var username;
    var password;
    var loginSubmit;
    var loginState;

    function initTarget()
    {
        loginForm = $("#loginForm");
        username = $("#username");
        password = $("#password");
        loginSubmit = $("#loginSubmit");
    }

    function initAction()
    {
        loginSubmit.click(function () {
            if (loginState && loginState.state() === 'pending')
            {
                return;
            }
            if (username.val() === '')
            {
                syfm.showNotify('error', '账号不能为空。');
                return;
            }
            if (password.val() === '')
            {
                syfm.showNotify('error', '密码不能为空。');
                return;
            }
            loginState = $.post(syfm.apiUriRoot + 'login', {
                username: username.val(),
                password: password.val()
            }).then(function (data) {
                if (data && typeof data.RESULT === 'boolean')
                {
                    if (!data.RESULT)
                    {
                        location.href = syfm.apiUriRoot + 'index.html';
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
    }


    $(doc).ready(function () {
        initTarget();
        initAction();
    });

})(window, document);