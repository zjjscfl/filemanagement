/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.file;

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
public class fileServlet extends HttpServlet {

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
                oResult = new JsonObject();
                String action = request.getParameter("action");
                if ("getUserFileList".equals(action)) {//获取用户文件列表
                    // /file?action=getUserFileList&pageSize=20&currentPage=1&search=nd&id=
                    String pageSize = request.getParameter("pageSize");
                    String currentPage = request.getParameter("currentPage");
                    String search = request.getParameter("search");
                    oResult = SqlService.getInstance().getUserFileList(id, pageSize, currentPage, search);
                } else if ("getFileList".equals(action)) {//获取用户文件列表
                    // /file?action=getFileList&pageSize=20&currentPage=1&search=nd&id=
                    String pageSize = request.getParameter("pageSize");
                    String currentPage = request.getParameter("currentPage");
                    String search = request.getParameter("search");
                    oResult = SqlService.getInstance().getFileList(id, pageSize, currentPage, search);
                } else if ("delFile".equals(action)) {//删除用户文件
                    // /file?action=delFile&fileid=1,2
                    String fileid = request.getParameter("fileid");
                    oResult = SqlService.getInstance().delFile(id, fileid);
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
