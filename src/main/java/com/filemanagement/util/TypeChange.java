/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.filemanagement.util;

import java.util.regex.Pattern;

/**
 *
 * @author ubuntu
 */
public class TypeChange {

    private static TypeChange instance = new TypeChange();

    public static TypeChange getInstance() {
        return instance;
    }

//判断是否为数字
    public boolean isNumeric(String str) {
        if (str == null || "".equals(str)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(str).matches();
        }
    }

    //判断是否非空字符串
    public boolean isNotNull(String str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        return false;
    }

//change the string type to the int type
    public int stringToInt(String intstr) {
        Integer integer;
        if (intstr == null || "".equals(intstr)) {
            integer = 0;
        } else {
            integer = Integer.valueOf(intstr);
        }
        return integer.intValue();
    }

    //change the string type to the int type
    public Long stringToLong(String longstr) {
        Long l;
        if (longstr == null || "".equals(longstr)) {
            l = (long) 0;
        } else {
            l = Long.valueOf(longstr);
        }
        return l;
    }

//change int type to the string type
    public String intToString(int value) {
        Integer integer = new Integer(value);
        return integer.toString();
    }

//change the string type to the float type
    public float stringToFloat(String floatstr) {
        Float floatee;
        floatee = Float.valueOf(floatstr);
        return floatee.floatValue();
    }

//change the float type to the string type
    public String floatToString(float value) {
        Float floatee = new Float(value);
        return floatee.toString();
    }

    //B转KB
    public String BToKB(long value) {
        return (double) Math.round(value * 100 / 1024) / 100 + "KB";
    }

    //B转MB
    public String BToMB(long value) {
        return (double) Math.round(value * 100 / (1024 * 1024)) / 100 + "MB";
    }

    //B转GB
    public String BToGB(long value) {
        return (double) Math.round(value * 100 / (1024 * 1024 * 1024)) / 100 + "GB";
    }

    //B转MB,有小数+1
    public int BToM(long value) {
        return (int) Math.ceil(value / (1024 * 1024));
    }

    //自动转换B为KB，MB，GB
    public String autoChangeB(long value) {
        String request = null;
        if (value >= (1024 * 1024 * 1024)) {
            request = (double) Math.round(value * 100 / (1024 * 1024 * 1024)) / 100 + "GB";
        } else if (value >= (1024 * 1024)) {
            request = (double) Math.round(value * 100 / (1024 * 1024)) / 100 + "MB";
        } else if (value >= 1024) {
            request = (double) Math.round(value * 100 / 1024) / 100 + "KB";
        } else {
            request = value + "B";
        }
        return request;
    }

    //自动转换MB为GB，TB
    public String autoChangeMB(int value) {
        String request = null;
        if (value >= (1024 * 1024)) {
            request = (double) Math.round(value * 100 / (1024 * 1024)) / 100 + "TB";
        } else if (value >= 1024) {
            request = (double) Math.round(value * 100 / 1024) / 100 + "GB";
        } else {
            request = value + "MB";
        }
        return request;
    }
}
