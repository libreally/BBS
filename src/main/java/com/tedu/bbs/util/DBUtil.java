package com.tedu.bbs.util;


import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 工具类
 */
public class DBUtil {
    //连接池作用：控制连接数量、重用连接
    private static DruidDataSource dds;
    static {
//        1.实例化连接池
        dds = new DruidDataSource();
//        2.设置连接池的基本信息 数据库名、密码 url 初始连接数 、 最大连接数
        dds.setUsername("root");//数据库名
        dds.setPassword("root");//数据库密码
        dds.setUrl("jdbc:mysql://localhost:3306/bbsdb?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true");
        dds.setInitialSize(5);//初始连接数
        dds.setMaxActive(10);//最大连接数
    }

    /**
     * 获取数据库连接
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        /*
            连接池返回的连接是在实际数据库驱动提供的连接对象上包装了一层连接池自己提供的连接对象，这个
            连接对象的close方法并不是真的断开连接，而是将当前连接归还到连接池中。
         */
        return dds.getConnection();
    }
}
/*
public class DBUtil {
    static {
try {
            //注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


/**
     * 获取数据库连接
     *
     * @return
     * @throws SQLException
     *//*

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/bbsdb?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true", "root", "root");

    }
}
*/
