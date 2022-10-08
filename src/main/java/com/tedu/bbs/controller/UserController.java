package com.tedu.bbs.controller;


import com.tedu.bbs.entity.User;
import com.tedu.bbs.util.DBUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class UserController {


    @RequestMapping("/regUser")
    public void reg(User user, HttpServletResponse response) {
        System.out.println("开始处理注册!!!!");
        System.out.println("User:"+user);
        //获取表单信息
        String username = user.getUsername();
        String password = user.getPassword();
        String nickname = user.getNickname();
        int age = user.getAge();
        //必要验证
        if(username==null||username.isEmpty()||
                password==null||password.isEmpty()||
                nickname==null||nickname.isEmpty()
        ){
            //信息输入有误提示页面
            try {
                response.sendRedirect("/reg_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        System.out.println(username+","+password+","+nickname+","+age);
        //2 将用户信息插入到数据库的userinfo表中
        try (
                Connection connection = DBUtil.getConnection();
        ){
            //验证该用户是否存在，若存在则直接响应have_user.html,否则才执行插入操作
            String sql = "SELECT 1 FROM userinfo WHERE username=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){//判断结果集是否有一条记录
                response.sendRedirect("/have_user.html");
                return;
            }
            String sql1 = "INSERT INTO userinfo (username,password,nickname,age) VALUES(?,?,?,?)";
            ps = connection.prepareStatement(sql1);
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,nickname);
            ps.setInt(4,age);
            int num = ps.executeUpdate();
            if(num>0) {
                //利用响应对象要求浏览器访问注册成功页面
                response.sendRedirect("/reg_success.html");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/loginUser")
    public void longin(HttpServletRequest request,HttpServletResponse response){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username+","+password);
        if (username==null||username.isEmpty()||
        password==null||password.trim().isEmpty()){
            try {
                response.sendRedirect("/login_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try(Connection connection=DBUtil.getConnection()){
            String sql="SELECT username,password FROM bbsdb.userinfo WHERE username=? AND password=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,password);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()){
                response.sendRedirect("/login_success.html");
            }else {
                response.sendRedirect("/login_fail.html");
            }
            resultSet.close();
            ps.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequestMapping("/userList")
    public void userList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理动态页面");
        /*1.从数据库将所有信息查出
        2.将用户信息体现到页面*/
        try(
                Connection connection=DBUtil.getConnection()
                ){
            String sql="SELECT id,username,password,nickname,age FROM bbsdb.userinfo";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("    <meta charset=\"UTF-8\">");
            pw.println("    <title>用户列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("    <center>");
            pw.println("        <h1>用户列表</h1>");
            pw.println("        <table border=\"1\">");
            pw.println("            <tr>");
            pw.println("                <td>ID</td>");
            pw.println("                <td>用户名</td>");
            pw.println("                <td>密码</td>");
            pw.println("                <td>昵称</td>");
            pw.println("                <td>年龄</td>");
            pw.println("            </tr>");
            while(rs.next()) {
                int id=rs.getInt("id");
                pw.println("            <tr>");
                pw.println("                <td>"+rs.getInt("id")+"</td>");
                pw.println("                <td><a href='/articleList?userId="+id+"'>"+rs.getString("username")+"</a></td>");
                pw.println("                <td>"+rs.getString("password")+"</td>");
                pw.println("                <td>"+rs.getString("nickname")+"</td>");
                pw.println("                <td>"+rs.getInt("age")+"</td>");
                pw.println("            </tr>");
            }
            pw.println("        </table>");
            pw.println("    </center>");
            pw.println("</body>");
            pw.println("</html>");

            rs.close();//结果集释放
            ps.close();//执行计划释放

        }catch (Exception e){
            e.printStackTrace();
        }
    }

       /*
   /**
     * 注册方法二
     * @param username
     * @param password
     * @param nickname
     * @param age
     * @param response

   public void reg(String username,String password,String nickname,int age, HttpServletResponse response){
        if (username==null||username.isEmpty()||
                password==null||password.isEmpty()||
                nickname==null||nickname.isEmpty()){
            try {
                response.sendRedirect("/reg_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try(Connection connection= DBUtil.getConnection()){
            String sql="SELECT username FROM bbsdb.userinfo WHERE username=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                response.sendRedirect("/have_user.html");
                return;
            }
            String sql1="INSERT INTO bbsdb.userinfo(username,password,nickname,age) " +
                    "VALUES (?,?,?,?)";
            ps = connection.prepareStatement(sql1);
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,nickname);
            ps.setInt(4,age);
            int i = ps.executeUpdate();
            if (i>0){
                System.out.println("success");
                response.sendRedirect("/reg_success.html");
            }else {
                System.out.println("fail");
            }
            rs.close();
            ps.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
       /*      @RequestMapping("/regUser")
       public void reg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始处理注册!!!!");
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        String nickname=request.getParameter("nickname");
        String agestr=request.getParameter("age");
        int age=Integer.parseInt(agestr);
        if (username==null||username.isEmpty()||
        password==null||password.isEmpty()||
        nickname==null||nickname.isEmpty()||
        agestr==null||agestr.isEmpty()||!agestr.matches("[0-9]+")){

            try {
                response.sendRedirect("/reg_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        System.out.println(username+","+password+","+nickname+","+agestr);
        try(Connection connection= DBUtil.getConnection()){
            String sql="SELECT username FROM bbsdb.userinfo WHERE username=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                response.sendRedirect("/have_user.html");
                return;
            }
            String sql1="INSERT INTO bbsdb.userinfo(username,password,nickname,age) " +
                    "VALUES (?,?,?,?)";
            ps = connection.prepareStatement(sql1);
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,nickname);
            ps.setInt(4,age);
            int i = ps.executeUpdate();
            if (i>0){
                System.out.println("success");
                response.sendRedirect("/reg_success.html");
            }else {
                System.out.println("fail");
            }
            rs.close();
            ps.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
