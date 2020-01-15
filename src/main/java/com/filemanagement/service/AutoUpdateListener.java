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
            String sql = "CREATE TABLE If NOT EXISTS `contract`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',\n" +
                    "  `department_id` int(11) NOT NULL COMMENT '部门编号',\n" +
                    "  `number` int(11) NOT NULL COMMENT '部门合同顺序',\n" +
                    "  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '合同名称',\n" +
                    "  `date` date NOT NULL COMMENT '合同时间',\n" +
                    "  `status` int(2) NOT NULL COMMENT '状态',\n" +
                    "  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '合同类型',\n" +
                    "  PRIMARY KEY (`id`) USING BTREE\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;\n" +
                    "\n" +
                    "CREATE TABLE If NOT EXISTS `contract_file`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '合同文件编号',\n" +
                    "  `contract_id` int(11) NOT NULL COMMENT '合同编号',\n" +
                    "  `file_id` int(11) NOT NULL COMMENT '文件编号',\n" +
                    "  `number` int(11) NOT NULL COMMENT '文件序号',\n" +
                    "  PRIMARY KEY (`id`) USING BTREE\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;\n" +
                    "\n" +
                    "CREATE TABLE If NOT EXISTS `department`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '部门编号',\n" +
                    "  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门名称',\n" +
                    "  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门代码',\n" +
                    "  `status` int(2) NOT NULL COMMENT '部门状态',\n" +
                    "  PRIMARY KEY (`id`) USING BTREE\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;\n" +
                    "\n" +
                    "CREATE TABLE If NOT EXISTS `file`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `userid` int(11) NOT NULL,\n" +
                    "  `hash` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,\n" +
                    "  `sourcename` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,\n" +
                    "  `targetname` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,\n" +
                    "  `mime` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,\n" +
                    "  `lasttime` datetime(0) NULL DEFAULT NULL,\n" +
                    "  `size` int(11) NOT NULL DEFAULT 0,\n" +
                    "  `status` int(11) NOT NULL DEFAULT 0 COMMENT ' -1已经删除,0上传中,1上传完成',\n" +
                    "  PRIMARY KEY (`id`) USING BTREE\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;\n" +
                    "\n" +
                    "CREATE TABLE If NOT EXISTS `t_order`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `time` datetime(0) NULL DEFAULT NULL COMMENT '订单时间',\n" +
                    "  `openid` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,\n" +
                    "  `amount` int(11) NOT NULL DEFAULT 0,\n" +
                    "  `couponid` int(11) NOT NULL DEFAULT 0,\n" +
                    "  `pay` int(11) NOT NULL DEFAULT 0 COMMENT '应该支付的单个产品的金额，单位为分',\n" +
                    "  `cash` int(11) NOT NULL DEFAULT 0 COMMENT '实际支付的金额，单位为分',\n" +
                    "  `name` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,\n" +
                    "  `phone` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,\n" +
                    "  `address` varchar(400) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,\n" +
                    "  `status` int(11) NULL DEFAULT NULL,\n" +
                    "  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`) USING BTREE\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;\n" +
                    "\n" +
                    "CREATE TABLE If NOT EXISTS `user`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `salt` char(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,\n" +
                    "  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,\n" +
                    "  `pwd` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,\n" +
                    "  `limitspace` int(11) NOT NULL DEFAULT 0,\n" +
                    "  `space` int(11) NOT NULL DEFAULT 0,\n" +
                    "  `parent` int(11) NOT NULL,\n" +
                    "  `status` int(11) NOT NULL DEFAULT 0 COMMENT '-1 禁用,0 正常',\n" +
                    "  `type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'a' COMMENT '用户性质:a-abidance,t-temp',\n" +
                    "  PRIMARY KEY (`id`) USING BTREE\n" +
                    ") ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;\n";
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
