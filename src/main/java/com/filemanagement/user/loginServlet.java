/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.user;

import com.filemanagement.config.Config;
import com.filemanagement.service.SqlService;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ubuntu
 */
public class loginServlet extends HttpServlet {

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
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            if (username == null) {
                oResult = new JsonObject();
                oResult.addProperty(Config.RESULT, Boolean.TRUE);
                oResult.addProperty(Config.MESSAGE, "用户名不可为空");
            } else if (password == null) {
                oResult = new JsonObject();
                oResult.addProperty(Config.RESULT, Boolean.TRUE);
                oResult.addProperty(Config.MESSAGE, "密码不可为空");
            } else {
                oResult = SqlService.getInstance().Login(username, password);
                if (!oResult.get(Config.RESULT).getAsBoolean()) {
                    int userid = oResult.get(Config.USERID).getAsInt();
                    int status = oResult.get(Config.USERSTATUS).getAsInt();
                    if (status >= 0) {
                        HttpSession session = request.getSession();
                        User user = new User();
                        user.userid = userid;
                        user.username = username;
                        user.status = status;
                        session.setAttribute(Config.USER, user);
                        oResult.addProperty(Config.RESULT, Boolean.FALSE);
                    } else {
                        oResult = null;
                        oResult.addProperty(Config.RESULT, Boolean.TRUE);
                        oResult.addProperty(Config.MESSAGE, "帐号被禁用");
                    }
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
