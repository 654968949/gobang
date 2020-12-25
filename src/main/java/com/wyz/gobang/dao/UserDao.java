package com.wyz.gobang.dao;

import com.wyz.gobang.entity.User;
import com.wyz.gobang.utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 *     用户数据dao
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/22
 */
public class UserDao {
    /**
     * 添加新用户
     * @param user 传入的user
     * @return 是否添加成功
     */
    public boolean add(User user) {
        String sql = "insert into chess_user(account,password,regtime,score,totalnums,winnums,lostnums,drawnums)" +
                "values( ?,?,now(),0,0,0,0,0)";
            List<Object> params = new ArrayList<>();
            params.add(user.getAccount());
            params.add(user.getPassword());
            int count = DBUtils.executeUpdateSQL(sql,params);
            return count > 0;
    }

    /**
     * 更新密码
     * @param user 用户实体
     * @Return 更新或者插入成功的条数
     */
    public int update(User user) {
        String sql = "update chess_user set account = ?,password = ?,regtime = ?,score = ?,totalnums = ?,winnums = ?,lostnums = ?,drawnums = ?  where id = ?";
        List<Object> params = new ArrayList<>();
        params.add(user.getAccount());
        params.add(user.getPassword());
        params.add(user.getRegTime());
        params.add(user.getScore());
        params.add(user.getTotalNums());
        params.add(user.getWinNums());
        params.add(user.getLostNums());
        params.add(user.getDrawNums());
        params.add(user.getId());
        return DBUtils.executeUpdateSQL(sql,params);
    }

    public void delById(int id) {

    }

    /**
     * 判断账号是否重复
     * @param account 账号
     * @return 是否重复
     */
    public boolean isExist(String account) {
        Connection conn = DBUtils.getConnection();
        String sql = "select count(*) from chess_user where account = ?";
        int count = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,account);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count > 0;
    }
    /**
     * 判断账号密码是否正确
     *
     * @param account 账号
     * @param password 密码
     * @return 是否正确
     */
    public boolean isTrue(String account,String password) {
        Connection conn = DBUtils.getConnection();
        String sql = "select count(*) from chess_user where account = ? and password = ?";
        int count = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,account);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            System.out.println("是否是正确的密码");
            while (rs.next()) {
                 count = rs.getInt(1);
                System.out.println("count"+count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  查询某一条用户数据
     */
    public User getOneUser(String accout) {
        String sql = "select * from chess_user where account = ?";
        List<Object> param = new ArrayList<>();
        param.add(accout);
        ResultSet rs = DBUtils.executeQuerySQL(sql,param);
        User user = null;
        try {
            if (rs.next()) {
                System.out.println("存在该用户");
                user = new User();
                user.setId(rs.getInt("id"));
                user.setAccount(rs.getString("account"));
                user.setPassword(rs.getString("password"));
                user.setRegTime(rs.getString("regtime"));
                user.setScore(rs.getInt("score"));
                user.setTotalNums(rs.getInt("totalnums"));
                user.setWinNums(rs.getInt("winnums"));
                user.setLostNums(rs.getInt("lostnums"));
                user.setDrawNums(rs.getInt("drawnums"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 查询所有的用户
     *
     * @return 所有用户的集合
     */
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "select * from chess_user";
        ResultSet rs = DBUtils.executeQuerySQL(sql);
        if (Objects.isNull(rs)) {
            return null;
        }
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setAccount(rs.getString("account"));
            user.setPassword(rs.getString("password"));
            user.setRegTime(rs.getString("regtime"));
            user.setScore(rs.getInt("score"));
            user.setTotalNums(rs.getInt("totalnums"));
            user.setWinNums(rs.getInt("winnums"));
            user.setLostNums(rs.getInt("lostnums"));
            user.setDrawNums(rs.getInt("drawnums"));
            users.add(user);
        }
        return users;
    }

}
