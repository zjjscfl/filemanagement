/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.service;

import com.filemanagement.config.ConfigManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author ubuntu
 */
public class AutoUpdateListener implements ServletContextListener {

    private final Logger Log = Logger.getLogger(AutoUpdateListener.class.getName());
    private Timer timer = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        timer = new Timer();
        Date date = new Date();
        timer.schedule(new autoUpdateTask(), date);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (timer != null) {
            timer.cancel();
            Log.log(Level.INFO, "自动更新任务结束");
            timer = null;
        }
    }

    class autoUpdateTask extends TimerTask {

        @Override
        public void run() {
            setUp();
        }
    }

    //初始化建立数据库中的表
    boolean setUp() {
        Connection conn = null;
        Statement stmt = null;

        boolean mark = false;
        try {

            conn = ConfigManager.getInstance().getConnection();
            List<String> list = new ArrayList<>();
            list = loadSqlCreate();
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            for (String sql : list) {
                stmt.addBatch(sql);
            }
            stmt.executeBatch();

            conn.commit();
            mark = true;
            stmt.close();
            stmt = null;
            conn.close();
            conn = null;
            SqlService.getInstance().autoAddUser();
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    Log.log(Level.SEVERE, null, ex1);
                }
            }
            Log.log(Level.SEVERE, null, ex);
        } finally {

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Log.log(Level.SEVERE, null, ex);
                }
                stmt = null;
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Log.log(Level.SEVERE, null, ex);
                }
                conn = null;
            }
        }
        return mark;
    }

    private List<String> loadSqlCreate() {
        List<String> sqlList = new ArrayList<String>();
        try {
            String sql = "CREATE TABLE If NOT EXISTS `File` (  `id` int(11) NOT NULL AUTO_INCREMENT,  `userid` int(11) NOT NULL,  `hash` varchar(32) COLLATE utf8_unicode_ci NOT NULL,  `sourcename` varchar(255) COLLATE utf8_unicode_ci NOT NULL,  `targetname` varchar(255) COLLATE utf8_unicode_ci NOT NULL,  `mime` varchar(255) COLLATE utf8_unicode_ci NOT NULL,  `lasttime` datetime DEFAULT NULL,  `size` int(11) NOT NULL DEFAULT '0',  `status` int(11) NOT NULL DEFAULT '0' COMMENT ' -1已经删除,0上传中,1上传完成',  PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8;\n"
                    + "CREATE TABLE If NOT EXISTS `User` (  `id` int(11) NOT NULL AUTO_INCREMENT,  `salt` char(8) COLLATE utf8_unicode_ci NOT NULL,  `name` varchar(32) COLLATE utf8_unicode_ci NOT NULL,  `pwd` varchar(32) COLLATE utf8_unicode_ci NOT NULL,  `limitspace` int(11) NOT NULL DEFAULT '0',  `space` int(11) NOT NULL DEFAULT '0',  `parent` int(11) NOT NULL,`status` int(11) NOT NULL DEFAULT '0' COMMENT '-1 禁用,0 正常',`type` char(1) NOT NULL DEFAULT 'a' COMMENT '用户性质:a-abidance,t-temp', PRIMARY KEY (`id`)) DEFAULT CHARSET=utf8;";

// Windows 下换行是 \r\n, Linux 下是 \n 
            String[] sqlArr = sql.split("(;\\s*\\r\\n)|(;\\s*\\n)");
            for (String sqlArr1 : sqlArr) {
                String _sql = sqlArr1.replaceAll("--.*", "").trim();
                _sql = new String(_sql.getBytes(), "UTF-8");
                if (!_sql.equals("")) {
                    sqlList.add(_sql);
                }
            }
        } catch (Exception ex) {
            Log.log(Level.SEVERE, null, ex);
        }
        return sqlList;
    }

}
