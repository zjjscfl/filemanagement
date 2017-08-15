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
import com.filemanagement.util.FileUtil;
import com.filemanagement.util.TypeChange;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ubuntu
 */
public class appendUploadServerServlet extends HttpServlet {

    private final Logger Log = Logger.getLogger(CheckLogin.class.getName());

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
        //  response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            JsonObject oResult = new JsonObject();
            oResult = CheckLogin.getInstance().isLogin(request, response);
            if (!oResult.get(Config.RESULT).getAsBoolean()) {
                //当前用户ID
                int id = oResult.get(Config.USERID).getAsInt();
                oResult = null;
                String fileSize = request.getParameter("fileSize");
                long totalSize = TypeChange.getInstance().stringToLong(fileSize);
                RandomAccessFile randomAccessfile = null;
                long currentFileLength = 0;// 记录当前文件大小，用于判断文件是否上传完成
                String currentFilePath = ConfigManager.getInstance().getFile_root() + File.separator + id + File.separator;// 记录当前文件的绝对路径
                FileUtil.getInstance().creatDirectory(currentFilePath);
                String fileName = request.getParameter("fileName");
                String targetname = request.getParameter("uuid") + fileName.substring(fileName.lastIndexOf("."));
                String fileHash = request.getParameter("fileHash");
                File file = new File(currentFilePath + targetname);
                // 存在
                if (file.exists()) {
                    randomAccessfile = new RandomAccessFile(file, "rw");
                } else {
                    // 不存在文件，根据文件标识创建文件
                    randomAccessfile = new RandomAccessFile(currentFilePath + targetname, "rw");

                    String mine = new MimetypesFileTypeMap().getContentType(fileName); //获取mime type
                    oResult = SqlService.getInstance().addFile(id, fileHash, fileName, targetname, mine, totalSize, 0);
                }
                boolean mark = false;
                if (oResult == null) {
                    mark = true;
                } else {
                    if (!oResult.get(Config.RESULT).getAsBoolean()) {
                        mark = true;
                    } else {
                        file.delete();
                    }
                }
                if (mark) {
                    // 开始文件传输
                    InputStream in = request.getInputStream();
                    randomAccessfile.seek(randomAccessfile.length());
                    byte b[] = new byte[1024];
                    int n;
                    while ((n = in.read(b)) != -1) {
                        randomAccessfile.write(b, 0, n);
                    }

                    currentFileLength = randomAccessfile.length();

                    // 关闭文件
                    closeRandomAccessFile(randomAccessfile);
                    randomAccessfile = null;
                    // 整个文件上传完成,修改文件后缀
                    if (currentFileLength == totalSize) {
                        //更改文件状态
                        oResult = SqlService.getInstance().updateFile(fileHash, 1, id, targetname);
                    }
                    out.print(currentFileLength);
                } else {
                    out.print(oResult.toString());
                }
            } else {
                out.print(oResult.toString());
            }
        }
    }

    /**
     * 关闭随机访问文件
     *
     * @param randomAccessfile
     */
    public void closeRandomAccessFile(RandomAccessFile rfile) {
        if (null != rfile) {
            try {
                rfile.close();
            } catch (Exception ex) {
                Log.log(Level.SEVERE, null, ex);
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
