package com.filemanagement.department;

import com.filemanagement.config.Config;
import com.filemanagement.service.SqlService;
import com.filemanagement.util.CheckLogin;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "department")
public class departmentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
