/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.file;

import com.filemanagement.config.Config;
import com.filemanagement.config.ConfigManager;
import com.filemanagement.service.SqlService;
import com.filemanagement.util.CheckLogin;
import com.filemanagement.util.TypeChange;
import com.google.gson.JsonObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ubuntu
 */
public class fileDownServlet extends HttpServlet {

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
        // /fileDown?fileid=1
        try (PrintWriter out = response.getWriter()) {
            JsonObject oResult = new JsonObject();
            oResult = CheckLogin.getInstance().isLogin(request, response);
            if (!oResult.get(Config.RESULT).getAsBoolean()) {
                String fileid = request.getParameter("fileid");
                if (TypeChange.getInstance().isNotNull(fileid)) {
                    oResult.addProperty(Config.RESULT, Boolean.FALSE);
                    oResult.addProperty(Config.MESSAGE, "文件ID不能为空");
                    out.print(oResult.toString());
                } else {
                    //当前用户ID
                    int id = oResult.get(Config.USERID).getAsInt();
                    oResult = null;
                    int int_fileid = TypeChange.getInstance().stringToInt(fileid);
                    oResult = SqlService.getInstance().getFileInfo(int_fileid);
                    if (!oResult.get(Config.RESULT).getAsBoolean() && oResult.get("status").getAsInt() == 1) {
                        String currentFilePath = ConfigManager.getInstance().getFile_root() + File.separator + id + File.separator + oResult.get("targetname").getAsString();
                        File file = new File(currentFilePath);
                        if (file.exists()) {
                            // 以流的形式下载文件。
                            InputStream fis = new BufferedInputStream(new FileInputStream(currentFilePath));
                            // 清空response
                            response.reset();
                            // 设置response的Header
                            response.addHeader("Content-Disposition", "attachment;filename=" + new String(oResult.get("sourcename").getAsString().getBytes(), "ISO-8859-1"));
                            response.addHeader("Content-Length", "" + file.length());
                            response.setContentType(oResult.get("mime").getAsString());
                            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                            byte[] buffer = new byte[1024 * 1024 * 4];
                            int i = -1;
                            while ((i = fis.read(buffer)) != -1) {
                                toClient.write(buffer, 0, i);
                            }
                            fis.close();
                            toClient.write(buffer);
                            toClient.flush();
                            toClient.close();
                        }
                    }
                }
            } else {
                out.print(oResult.toString());
            }
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
