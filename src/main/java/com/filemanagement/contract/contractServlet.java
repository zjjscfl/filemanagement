package com.filemanagement.contract;

import com.filemanagement.config.Config;
import com.filemanagement.service.SqlService;
import com.filemanagement.util.CheckLogin;
import com.filemanagement.util.TypeChange;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class contractServlet extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            JsonObject oResult = new JsonObject();
            oResult = CheckLogin.getInstance().isLogin(request, response);
            if (!oResult.get(Config.RESULT).getAsBoolean()) {
                oResult = new JsonObject();
                oResult.addProperty(Config.RESULT, Boolean.TRUE);
                String action = request.getParameter("action");
                if ("add".equals(action)) {
                    int department_id = TypeChange.getInstance().stringToInt(request.getParameter("department_id"));
                    String name = request.getParameter("name");
                    String type = request.getParameter("type");

                    if (department_id > 0 && !TypeChange.getInstance().isNotNull(name) && !TypeChange.getInstance().isNotNull(type)) {
                        oResult = SqlService.getInstance().addContract(department_id, name, type);
                    }else{
                        oResult.addProperty(Config.MESSAGE, "参数错误");
                    }

                } else {

                    oResult.addProperty(Config.MESSAGE, "不存在的操作选项");
                }
            }
            out.print(oResult.toString());
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
