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
        USERNAME: "欢迎您",
        USERSTATUS: 0
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
