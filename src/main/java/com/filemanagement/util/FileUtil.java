/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cfl
 */
public class FileUtil {

    private static FileUtil instance = new FileUtil();

    public static FileUtil getInstance() {
        return instance;
    }
    private final Logger Log = Logger.getLogger(FileUtil.class.getName());

    /**
     * 删除目录(文件夹)以及目录下的文件
     *
     * @param deleteUrl 删除的路径
     * @return 目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String deleteUrl) {
        boolean flag = true;
        try {

            File dirFile = new File(deleteUrl);
            //如果dir对应的文件不存在，或者不是一个目录，则退出
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return false;
            }

            //删除文件夹下的所有文件(包括子目录)
            File[] files = dirFile.listFiles();
            for (File file : files) {
                //删除子文件
                if (file.isFile()) {
                    flag = deleteFile(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                } //删除子目录
                else {
                    flag = deleteDirectory(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }
            if (!flag) {
                return false;
            }
            //删除当前目录
            return dirFile.delete();
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        try {
            File file = new File(sPath);
            // 路径为文件且不为空则进行删除
            if (file.isFile() && file.exists()) {
                file.delete();
                flag = true;
            }
        } catch (Exception ex) {
            Log.log(Level.SEVERE, null, ex);
        }
        return flag;
    }

    //创建文件夹
    public boolean creatDirectory(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        //如果文件夹不存在则创建    
        if (!file.exists() && !file.isDirectory()) {
            flag = true;
            file.mkdirs();
        }
        return flag;
    }

    //写text
    public boolean writeTXT(String Content, String filePath, String fileName) {

        FileWriter writer;
        boolean mark = false;
        try {
            File sf = new File(filePath);
            if (!sf.exists()) {
                sf.mkdirs();
            }
            writer = new FileWriter(filePath + fileName);
            writer.write(Content);
            writer.flush();
            writer.close();
            mark = true;
        } catch (Exception ex) {
            Log.log(Level.SEVERE, null, ex);
        }
        return mark;
    }

    //得到json内容
    public String getJson(String filePath) {
        String s = "";
        try {

            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    s = s + lineTxt;
                }
                read.close();
                return s;
            } else {
                Log.log(Level.INFO, "找不到指定的文件: {0}", filePath);
                s = null;
            }
        } catch (Exception ex) {
            Log.log(Level.SEVERE, null, ex);
        }
        return s;
    }

}
