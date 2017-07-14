/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.service;

import com.filemanagement.config.Config;
import com.filemanagement.config.ConfigManager;
import com.filemanagement.util.CodecHelper;
import com.filemanagement.util.Salt;
import com.filemanagement.util.TypeChange;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ubuntu
 */
public class SqlService {

    static SqlService self = new SqlService();

    public static SqlService getInstance() {
        return self;
    }

    private final Logger Log = Logger.getLogger(SqlService.class.getName());

    //添加用户
    public JsonObject addUser(String name, String pwd, String space, String parent) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JsonObject request = new JsonObject();
        request.addProperty(Config.RESULT, Boolean.TRUE);
        request.addProperty(Config.MESSAGE, "用户添加失败");
        if (TypeChange.getInstance().isNotNull(name)) {
            request.addProperty(Config.MESSAGE, "用户名不能为空");
        } else if (name.length() > 32) {
            request.addProperty(Config.MESSAGE, "用户名不能超过32字符");
        } else if (TypeChange.getInstance().isNotNull(pwd)) {
            request.addProperty(Config.MESSAGE, "密码不能为空");
        } else if (pwd.length() > 32) {
            request.addProperty(Config.MESSAGE, "密码不能超过32字符");
        } else if (!TypeChange.getInstance().isNumeric(space)) {
            request.addProperty(Config.MESSAGE, "空间大小必需是数字");
        } else if (!TypeChange.getInstance().isNumeric(parent)) {
            request.addProperty(Config.MESSAGE, "上级ID必需是数字");
        } else {
            try {
                long int_space = TypeChange.getInstance().stringToLong(space);
                int int_parent = TypeChange.getInstance().stringToInt(parent);

                boolean mark = false;
                conn = ConfigManager.getInstance().getConnection();
                stmt = conn.prepareStatement("SELECT `User`.id FROM `User` WHERE `User`.id>0 AND `User`.`name` =?");
                stmt.setString(1, name);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    mark = true;
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
                if (mark) {
                    request.addProperty(Config.MESSAGE, "用户名重复");
                } else {
                    stmt = conn.prepareStatement("SELECT `User`.limitspace FROM `User` WHERE `User`.id = ?");
                    stmt.setInt(1, int_parent);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        if (int_space > rs.getLong(1)) {
                            mark = true;
                        }
                    }
                    rs.close();
                    rs = null;
                    stmt.close();
                    stmt = null;
                    if (mark) {
                        request.addProperty(Config.MESSAGE, "您的空间不足");
                    } else {
                        String salt = Salt.getInstance().generateShortUuid();
                        stmt = conn.prepareStatement("INSERT INTO `User` (`User`.`salt`, `User`.`name`, `User`.`pwd`, `User`.`limitspace`, `User`.`space`, `User`.`parent`) VALUES (?, ?, ?, ?, ?, ?)");
                        stmt.setString(1, salt);
                        stmt.setString(2, name);
                        stmt.setString(3, CodecHelper.calcMD5((salt + pwd).getBytes()));
                        stmt.setLong(4, int_space);
                        stmt.setLong(5, int_space);
                        stmt.setInt(6, int_parent);
                        if (stmt.executeUpdate() == 1) {
                            request.addProperty(Config.RESULT, Boolean.FALSE);
                            request.addProperty(Config.MESSAGE, "用户添加成功");
                        } else {
                            mark = true;
                        }
                        stmt.close();
                        stmt = null;
                        if (mark) {
                            request.addProperty(Config.RESULT, Boolean.TRUE);
                            request.addProperty(Config.MESSAGE, "添加用户失败");
                        } else {
                            stmt = conn.prepareStatement("UPDATE `User` SET `User`.`limitspace`=`User`.`limitspace`-? WHERE (`User`.`id`=?)");
                            stmt.setLong(1, int_space);
                            stmt.setInt(2, int_parent);
                            if (stmt.executeUpdate() == 1) {

                            } else {
                                request.addProperty(Config.RESULT, Boolean.TRUE);
                                request.addProperty(Config.MESSAGE, "添加用户成功，但扣除空间失败");
                            }
                        }
                        stmt.close();
                        stmt = null;
                    }
                }
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex1) {
                        Log.log(Level.SEVERE, null, ex1);
                    }
                }
                Log.log(Level.SEVERE, null, ex);
                request.addProperty(Config.MESSAGE, "用户添加失败,程序异常");
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();

                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    stmt = null;
                }
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
        }
        return request;
    }

    //修改用户
    public JsonObject updateUser(String name, String space, String id, String userid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JsonObject request = new JsonObject();
        request.addProperty(Config.RESULT, Boolean.TRUE);
        request.addProperty(Config.MESSAGE, "用户添加失败");
        if (TypeChange.getInstance().isNotNull(name)) {
            request.addProperty(Config.MESSAGE, "用户名不能为空");
        } else if (name.length() > 32) {
            request.addProperty(Config.MESSAGE, "用户名不能超过32字符");
        } else if (!TypeChange.getInstance().isNumeric(space)) {
            request.addProperty(Config.MESSAGE, "空间大小必需是数字");
        } else if (!TypeChange.getInstance().isNumeric(id)) {
            request.addProperty(Config.MESSAGE, "上级ID必需是数字");
        } else if (!TypeChange.getInstance().isNumeric(userid)) {
            request.addProperty(Config.MESSAGE, "用户ID必需是数字");
        } else {
            try {
                long int_space = TypeChange.getInstance().stringToLong(space);
                int int_id = TypeChange.getInstance().stringToInt(id);
                int int_userid = TypeChange.getInstance().stringToInt(userid);
                boolean mark = false;
                conn = ConfigManager.getInstance().getConnection();
                stmt = conn.prepareStatement("SELECT `User`.id,`User`.parent FROM `User` WHERE `User`.`name` =?");
                stmt.setString(1, name);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(2) == int_id) {
                        if (rs.getInt(1) == int_userid) {
                        } else {
                            mark = true;
                            request.addProperty(Config.MESSAGE, "用户名重复");
                        }
                    } else {
                        mark = true;
                        request.addProperty(Config.MESSAGE, "不能修改非下级数据");
                    }
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
                if (mark) {
                } else {
                    stmt = conn.prepareStatement("SELECT `User`.limitspace FROM `User` WHERE `User`.parent>0 AND `User`.id=?");
                    stmt.setInt(1, int_id);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        if (int_space > rs.getLong(1)) {
                            mark = true;
                        }
                    }
                    rs.close();
                    rs = null;
                    stmt.close();
                    stmt = null;
                    if (mark) {
                        request.addProperty(Config.MESSAGE, "您的空间不足");
                    } else {
                        stmt = conn.prepareStatement("UPDATE `User` SET `User`.`name`=?,`User`.`limitspace`=(?-`User`.`space`)+`User`.`limitspace`, `User`.`space`=? WHERE (`id`=?) AND ?>=`User`.`limitspace`");
                        stmt.setString(1, name);
                        stmt.setLong(2, int_space);
                        stmt.setLong(3, int_space);
                        stmt.setInt(4, int_userid);
                        stmt.setLong(5, int_space);
                        if (stmt.executeUpdate() == 1) {
                            request.addProperty(Config.RESULT, Boolean.FALSE);
                            request.addProperty(Config.MESSAGE, "用户修改成功");
                        } else {
                            request.addProperty(Config.RESULT, Boolean.TRUE);
                            request.addProperty(Config.MESSAGE, "修改用户空间大小必须大于等于可使用空间大小");
                        }
                        stmt.close();
                        stmt = null;
                    }
                }
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex1) {
                        Log.log(Level.SEVERE, null, ex1);
                    }
                }
                Log.log(Level.SEVERE, null, ex);
                request.addProperty(Config.MESSAGE, "用户修改失败,程序异常");
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();

                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    stmt = null;
                }
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
        }
        return request;
    }

    //获取用户列表
    public JsonObject getUserList(String parentId) {
        JsonObject request = new JsonObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        request.addProperty(Config.RESULT, Boolean.TRUE);
        try {
            if (TypeChange.getInstance().isNotNull(parentId)) {
                request.addProperty(Config.MESSAGE, "上级ID不能为空");
            } else {
                JsonArray ListArray = new JsonArray();
                int id = 0;
                int parent = 0;
                int int_parent = TypeChange.getInstance().stringToInt(parentId);
                HashMap<Integer, JsonObject> list_map = new HashMap<>();//存放所有的用户
                HashMap<Integer, Integer> temp_map = new HashMap<>();//存放用户ID 和父级ID
                conn = ConfigManager.getInstance().getConnection();
                stmt = conn.prepareStatement("SELECT `User`.id,`User`.`name`,`User`.limitspace,`User`.space,`User`.parent,`User`.`status` FROM `User`");
                rs = stmt.executeQuery();
                while (rs.next()) {
                    JsonObject temp = new JsonObject();
                    id = rs.getInt(1);
                    temp.addProperty("id", id);
                    temp.addProperty("name", rs.getString(2));
                    temp.addProperty("limitspace", TypeChange.getInstance().autoChangeB(rs.getLong(3)));
                    temp.addProperty("space", TypeChange.getInstance().autoChangeB(rs.getLong(4)));
                    parent = rs.getInt(5);
                    temp.addProperty("parent", parent);
                    temp.addProperty("status", rs.getInt(6));
                    list_map.put(id, temp);
                    temp_map.put(id, parent);
                    temp = null;
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
                ListArray = getChildren(int_parent, temp_map, list_map);
                if (ListArray.size() > 0) {
                    request.addProperty(Config.RESULT, Boolean.FALSE);
                }
                request.add("userList", ListArray);
                conn.close();
                conn = null;
                list_map = null;
                temp_map = null;
            }
        } catch (SQLException ex) {
            Log.log(Level.SEVERE, null, ex);
            request.addProperty(Config.MESSAGE, "发生错误,程序异常");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Log.log(Level.SEVERE, null, ex);
                }
                rs = null;
            }
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
        return request;
    }

    //获取子集
    public JsonArray getChildren(int parentId, HashMap<Integer, Integer> map, HashMap<Integer, JsonObject> list_map) {
        JsonArray ListArray = new JsonArray();
        Iterator iter = map.entrySet().iterator();
        int key = 0;
        int val = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            key = (int) entry.getKey();
            val = (int) entry.getValue();

            if (val == parentId) {
                JsonObject temp = (JsonObject) list_map.get(key);
                temp.add("children", getChildren(key, map, list_map));
                ListArray.add(temp);
                temp = null;
            }
        }
        return ListArray;
    }

    public JsonObject password(String id, String userid, String pwd) {
        JsonObject request = new JsonObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        request.addProperty(Config.RESULT, Boolean.TRUE);
        if (pwd == null) {
            pwd = Config.PROSSWORD;
        }
        if (userid == null) {
            userid = id;
        }
        if (!TypeChange.getInstance().isNumeric(id)) {
            request.addProperty(Config.MESSAGE, "上级ID必需是数字");
        } else if (!TypeChange.getInstance().isNumeric(id)) {
            request.addProperty(Config.MESSAGE, "用户ID必需是数字");
        } else if (TypeChange.getInstance().isNotNull(pwd)) {
            request.addProperty(Config.MESSAGE, "密码不能为空");
        } else if (pwd.length() > 32) {
            request.addProperty(Config.MESSAGE, "用户名不能超过32字符");
        } else {
            try {
                boolean mark = false;
                int int_id = TypeChange.getInstance().stringToInt(id);
                int int_userid = TypeChange.getInstance().stringToInt(userid);
                conn = ConfigManager.getInstance().getConnection();
                stmt = conn.prepareStatement("SELECT `User`.id,`User`.parent FROM `User` WHERE `User`.`id` =?");
                stmt.setInt(1, int_userid);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    if (!userid.equals(id)) {
                        if (rs.getInt(2) == int_id) {
                        } else {
                            mark = true;
                            request.addProperty(Config.MESSAGE, "不能修改非下级密码");
                        }
                    }
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
                if (mark) {
                } else {
                    String salt = Salt.getInstance().generateShortUuid();
                    stmt = conn.prepareStatement("UPDATE `User` SET `User`.`salt`=?,`User`.`pwd`=? WHERE (`User`.`id`=?)");
                    stmt.setString(1, salt);
                    stmt.setString(2, CodecHelper.calcMD5((salt + pwd).getBytes()));
                    stmt.setInt(3, int_userid);
                    if (stmt.executeUpdate() == 1) {
                        request.addProperty(Config.RESULT, Boolean.FALSE);
                        request.addProperty(Config.MESSAGE, "密码修改成功");
                    } else {
                        request.addProperty(Config.RESULT, Boolean.TRUE);
                        request.addProperty(Config.MESSAGE, "密码修改失败");
                    }
                    stmt.close();
                    stmt = null;
                }
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex1) {
                        Log.log(Level.SEVERE, null, ex1);
                    }
                }
                Log.log(Level.SEVERE, null, ex);
                request.addProperty(Config.MESSAGE, "密码修改失败,程序异常");
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    stmt = null;
                }
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
        }
        return request;
    }

    //登录
    public JsonObject Login(String name, String pwd) {
        JsonObject request = new JsonObject();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        request.addProperty(Config.RESULT, Boolean.TRUE);
        try {
            conn = ConfigManager.getInstance().getConnection();
            stmt = conn.prepareStatement("SELECT `User`.id,`User`.salt,`User`.`name`,`User`.pwd,`User`.`status` FROM `User` WHERE `User`.`name`=?");
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            if (rs.next()) {
                if (CodecHelper.calcMD5((rs.getString(2) + pwd).getBytes()).equals(rs.getString(4))) {
                    request.addProperty(Config.RESULT, Boolean.FALSE);
                    request.addProperty(Config.USERID, rs.getInt(1));
                    request.addProperty(Config.USERSTATUS, rs.getInt(5));
                } else {
                    request.addProperty(Config.MESSAGE, "密码错误");
                }
            } else {
                request.addProperty(Config.MESSAGE, "用户名不存在");
            }
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            Log.log(Level.SEVERE, null, ex);
            request.addProperty(Config.MESSAGE, "发生错误,程序异常");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Log.log(Level.SEVERE, null, ex);
                }
                rs = null;
            }
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
        return request;
    }

    //添加文件
    public JsonObject addFile(int userid, String hash, String sourcename, String targetname, String mime, long size, int status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JsonObject request = new JsonObject();
        request.addProperty(Config.RESULT, Boolean.TRUE);
        request.addProperty(Config.MESSAGE, "添加文件信息失败");
        if (TypeChange.getInstance().isNotNull(hash)) {
            request.addProperty(Config.MESSAGE, "文件hash不能为空");
        } else if (hash.length() > 32) {
            request.addProperty(Config.MESSAGE, "文件hash不能超过32字符");
        } else if (TypeChange.getInstance().isNotNull(sourcename)) {
            request.addProperty(Config.MESSAGE, "文件名不能为空");
        } else if (sourcename.length() > 255) {
            request.addProperty(Config.MESSAGE, "文件名不能超过255字符");
        } else if (TypeChange.getInstance().isNotNull(targetname)) {
            request.addProperty(Config.MESSAGE, "文件名不能为空");
        } else if (targetname.length() > 255) {
            request.addProperty(Config.MESSAGE, "文件名不能超过255字符");
        } else if (TypeChange.getInstance().isNotNull(mime)) {
            request.addProperty(Config.MESSAGE, "文件类型不能为空");
        } else if (mime.length() > 255) {
            request.addProperty(Config.MESSAGE, "文件类型不能超过255字符");
        } else {
            try {

                boolean mark = false;
                conn = ConfigManager.getInstance().getConnection();
                stmt = conn.prepareStatement("SELECT `User`.limitspace FROM `User` WHERE `User`.id = ?");
                stmt.setInt(1, userid);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    if (size > rs.getLong(1)) {
                        mark = true;
                    }
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
                if (mark) {
                    request.addProperty(Config.MESSAGE, "您的空间不足");
                } else {
                    stmt = conn.prepareStatement("INSERT INTO File (File.userid,File.`hash`,File.sourcename,File.targetname,File.mime,File.size,File.`status`,File.lasttime) VALUES (?, ?, ?, ?, ?, ?, ?,now())");
                    stmt.setInt(1, userid);
                    stmt.setString(2, hash);
                    stmt.setString(3, sourcename);
                    stmt.setString(4, targetname);
                    stmt.setString(5, mime);
                    stmt.setLong(6, size);
                    stmt.setInt(7, status);
                    if (stmt.executeUpdate() == 1) {
                        request.addProperty(Config.RESULT, Boolean.FALSE);
                        request.addProperty(Config.MESSAGE, "添加成功");
                    } else {
                        mark = true;
                    }
                    stmt.close();
                    stmt = null;
                    if (mark) {
                        request.addProperty(Config.RESULT, Boolean.TRUE);
                        request.addProperty(Config.MESSAGE, "添加失败");
                    } else {
                        stmt = conn.prepareStatement("UPDATE `User` SET `User`.`limitspace`=`User`.`limitspace`-? WHERE (`User`.`id`=?)");
                        stmt.setLong(1, size);
                        stmt.setInt(2, userid);
                        if (stmt.executeUpdate() == 1) {

                        } else {
                            request.addProperty(Config.RESULT, Boolean.TRUE);
                            request.addProperty(Config.MESSAGE, "添加成功，但扣除空间失败");
                        }
                    }
                    stmt.close();
                    stmt = null;
                }
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex1) {
                        Log.log(Level.SEVERE, null, ex1);
                    }
                }
                Log.log(Level.SEVERE, null, ex);
                request.addProperty(Config.MESSAGE, "添加失败,程序异常");
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();

                    } catch (SQLException ex) {
                        Log.log(Level.SEVERE, null, ex);
                    }
                    stmt = null;
                }
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
        }
        return request;
    }

}
