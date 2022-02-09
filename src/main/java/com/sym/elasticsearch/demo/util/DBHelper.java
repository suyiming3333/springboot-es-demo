package com.sym.elasticsearch.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper {
    public static final String url = "jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
    public static final String name = "com.mysql.cj.jdbc.Driver";
    public static final String user = "root";
    public static final String password = "";
    private static Connection connection = null;

    public static Connection getConn() {
        try {
            Class.forName(name);
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
