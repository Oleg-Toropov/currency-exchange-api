package com.currencyexchange;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "DBCPTestServlet", urlPatterns = "/testDBCP")
public class DBCPTestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection connection = DBCPDataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {

            if (resultSet.next()) {
                out.println("<h1>DBCP Connection Pool is working!</h1>");
            } else {
                out.println("<h1>DBCP Connection Pool test failed!</h1>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<h1>DBCP Connection Pool test failed with exception: " + e.getMessage() + "</h1>");
        }
    }
}
