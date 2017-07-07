/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.config;

import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author ubuntu
 */
public class ConfigManager {

    private static final ConfigManager instance = new ConfigManager();
    DataSource ds = null;
    public String File_root = "";//根目录

    private ConfigManager() {
        try {
            Context envContext = (Context) new InitialContext().lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup("jdbc/filemanagement");
            File_root = (String) envContext.lookup("file_root");

        } catch (NamingException ex) {

        }
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public java.sql.Connection getConnection() throws SQLException {
        if (ds == null) {
            return null;
        }
        return ds.getConnection();
    }

    public String getFile_root() {
        return File_root;
    }

}
