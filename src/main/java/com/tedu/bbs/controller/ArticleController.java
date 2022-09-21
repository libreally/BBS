package com.tedu.bbs.controller;


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
public class ArticleController {

    @RequestMapping("/writeArticle")
    public void write(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理发表文章功能！！！！");
        //1获取表单信息
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        /*
            有一个验证:如果作者无效，要提示一个页面，该作者不存在!
            1:先根据该文章的作者名去userinfo表中找到该用户，并得到该用户的id(主键值)
            2:将文章信息插入到article表中，u_id字段插入该用户的主键值
         */
        try(
                Connection connection = DBUtil.getConnection();
        ){
            String sql = "SELECT id FROM userinfo WHERE username=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,author);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){

                int id = rs.getInt("id");
                String sql1 = "INSERT INTO article (title,content,u_id) VALUES (?,?,?)";
                ps = connection.prepareStatement(sql1);
                ps.setString(1,title);
                ps.setString(2,content);
                ps.setInt(3,id);
                int num = ps.executeUpdate();
                if(num>0){
                    response.sendRedirect("/writeArticle_success.html");
                }else{
                    response.sendRedirect("/writeArticle_fail.html");
                }

            }else{//没有此作者
                response.sendRedirect("/author_have.html");
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }


    }

    @RequestMapping("/articleList")
    public void articleList(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理文章列表");
        String uid = request.getParameter("userId");
        System.out.println("该用户的id是："+uid);
        try(
                Connection connection=DBUtil.getConnection()
        ) {
            String sql = "SELECT u.username,a.id,a.title FROM bbsdb.userinfo u,bbsdb.article a WHERE u.id=a.u_id AND a.u_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,Integer.parseInt(uid));
            ResultSet rs = ps.executeQuery();
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("    <meta charset=\"UTF-8\">");
            pw.println("    <title>文章列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("    <center>");
            pw.println("        <h1>xxx的文章</h1>");
            pw.println("        <table border=\"1\">");
            pw.println("            <tr>");
            pw.println("                <td>标题</td>");
            pw.println("                <td>作者</td>");
            pw.println("            </tr>");
            while(rs.next()) {
                String uname = rs.getString(1);
                int aid = rs.getInt(2);
                String title = rs.getString(3);
                pw.println("<tr>");
                pw.println("<td>"+title+"</td>");
                pw.println("<td>"+uname+"</td>");
                pw.println("</tr>");
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
}
