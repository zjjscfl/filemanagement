/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


(function () {
    'use strict';
    var userNameShow,
            userLogout;

    function initTarget()
    {
        userNameShow = $("#userNameShow");
        userNameShow.html(syfm.user.USERNAME);
        userLogout = $("#userLogout");
    }

    function initAction()
    {
        userLogout.click(function () {
            $.post(syfm.apiUriRoot+'logout').then(function(){
                location.href=syfm.apiUriRoot+'login.html';
            });
        });
    }


    $(document).ready(function () {
        initTarget();
        initAction();

    });
})();