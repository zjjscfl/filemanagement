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
}
