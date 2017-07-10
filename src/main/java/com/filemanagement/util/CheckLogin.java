/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.util;

import com.filemanagement.config.Config;
import com.filemanagement.user.User;
import com.google.gson.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ubuntu
 */
public class CheckLogin {

    private final Logger Log = Logger.getLogger(CheckLogin.class.getName());

    static CheckLogin self = new CheckLogin();

    public static CheckLogin getInstance() {
        return self;
    }

    //检测是否登录
    public JsonObject isLogin(HttpServletRequest request, HttpServletResponse response) {
        JsonObject oResult = new JsonObject();
        HttpSession session = request.getSession();
        oResult.addProperty(Config.RESULT, Boolean.TRUE);
        try {
            if (session == null || session.getAttribute(Config.USER) == null) {
                oResult.addProperty(Config.MESSAGE, "尚未登录");
            } else {
                User user = (User) session.getAttribute(Config.USER);
                oResult.addProperty(Config.RESULT, Boolean.FALSE);
                oResult.addProperty(Config.USERID, user.userid);
                oResult.addProperty(Config.USERNAME, user.username);
                oResult.addProperty(Config.USERSTATUS, user.status);
            }
        } catch (Exception ex) {
            Log.log(Level.SEVERE, null, ex);
            oResult.addProperty(Config.MESSAGE, "程序异常");
        }
        return oResult;
    }
}
