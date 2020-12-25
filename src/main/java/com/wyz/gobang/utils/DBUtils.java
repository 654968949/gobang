package com.wyz.gobang.utils;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 *     数据库工具类
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/22
 */
public class DBUtils {

    //私有化构造无法实例化，只能通过getConnection获取连接
    private DBUtils() {
    }

    //静态 实例化对象，在类加载的时候就获取到连接，只要不关闭一直用这个连接
    private static Connection connection = getConnection();

    /**
     * 获取数据库连接,锁方法确保单例
     * @return
     */
    public static  Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        Properties props = new Properties();
        try {
            props.load(DBUtils.class.getResourceAsStream("/db.properties"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String className = props.getProperty("classname");
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        // 1 ,加载驱动(Java8中可以省略)
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 2,获取连接
        try {
            connection = DriverManager.getConnection(url,
                    username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(connection);
        return connection;
    }

    /**
     * 有参数的查询
     * @param sql sql语句
     * @param params 众多参数
     */
    public static ResultSet executeQuerySQL(String sql, List<Object> params) {
        Connection conn = getConnection();
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 没参数的查询
     * @param sql sql语句
     */
    public static ResultSet executeQuerySQL(String sql) {
        Connection conn = getConnection();
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *   修改更新操作
     * @param sql sql语句
     * @param params 众多参数
     * @Return 返回更新或者插入成功的条数
     */
    public static int executeUpdateSQL(String sql, List<Object> params) {
        Connection conn = getConnection();
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i+1, params.get(i));
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("抛出个运行时异常");
        }
    }

    @Deprecated
    public static void executeSQL(String sql) {
        Connection conn = getConnection();
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
