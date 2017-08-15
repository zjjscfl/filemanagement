/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.user;

import com.filemanagement.config.Config;
import com.filemanagement.service.SqlService;
import com.filemanagement.util.CheckLogin;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ubuntu
 */
public class userMangerServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            JsonObject oResult = new JsonObject();
            oResult = CheckLogin.getInstance().isLogin(request, response);
            if (!oResult.get(Config.RESULT).getAsBoolean()) {
                String id = request.getParameter("id");//当前用户ID
                if (id == null || id == "") {
                    id = oResult.get(Config.USERID).getAsString();
                }
                String action = request.getParameter("action");
                if ("add".equals(action)) {//添加用户
                    // /userManger?action=add&name=admin&space=500&id=0
                    String name = request.getParameter("name");//用户名
                    String space = request.getParameter("space");//空间大小  
                    oResult = SqlService.getInstance().addUser(name, Config.PROSSWORD, space, id);
                } else if ("update".equals(action)) {//修改用户信息
                    // /userManger?action=update&name=admin2&space=200&userid=2&id=1
                    String name = request.getParameter("name");//用户名
                    String space = request.getParameter("space");//空间大小
                    String userid = request.getParameter("userid");//要修改的用户ID
                    oResult = SqlService.getInstance().updateUser(name, space, id, userid);
                } else if ("getList".equals(action)) {//获取用户层级列表根据上级ID
                    // /userManger?action=getList&id=0
                    oResult = SqlService.getInstance().getUserList(id);
                } else if ("password".equals(action)) {//用户修改密码,或重置,若userid为null则表示修改自己的密码
                    // /userManger?action=password&userid=12&id=1&pwd=123456
                    // /userManger?action=password&userid=&id=&pwd=
                    String userid = request.getParameter("userid");//要修改的用户ID
                    String pwd = request.getParameter("pwd");
                    oResult = SqlService.getInstance().password(id, userid, pwd);
                } else if ("getUser".equals(action)) {//获取用户层级列表根据上级ID
                    //userManger?action=getUser&id=1
                    oResult = SqlService.getInstance().getUser(id);
                } else {
                    oResult = new JsonObject();
                    oResult.addProperty(Config.RESULT, Boolean.TRUE);
                    oResult.addProperty(Config.MESSAGE, "非法的请求");
                }
            }
            out.print(oResult.toString());
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
