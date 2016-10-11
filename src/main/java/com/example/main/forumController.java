package com.example.main;

import com.example.main.requests.DetailsForum;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.example.main.requests.CreateForum;


import static org.springframework.http.ResponseEntity.status;

/**
 * Created by sergeigavrilko on 05.10.16.
 */

@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class forumController {
    private static final String url = "jdbc:mysql://localhost:3306/forum?autoReconnect=true&useSSL=false";
    private static final String username = "root";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    @RequestMapping(path = "/db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody CreateForum body){
        final String name = body.getName();
        final String short_name = body.getShort_name();
        final String user = body.getUser();

        String query = "insert into forums VALUES (NULL, \"" + name + "\", \"" + short_name + "\", \"" +
                user + "\")";

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
        }

        query = "select id, name, short_name, user from forums where id = (select max(id) from forums);";

        final int id;
        final String dbname;
        final String dbshort_name;
        final String dbuser;

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            dbname = rs.getString(2);
            dbshort_name = rs.getString(3);
            dbuser = rs.getString(4);

        } catch (SQLException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST).body("{" + e.getMessage() + "}");
        }finally {
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        }

        return ResponseEntity.ok("{ \"code\": 0, \"response\": { \"id\": \"" + id + "\", \"name\": \"" +
                dbname + "\", \"short_name\" : \"" + dbshort_name + "\", \"user\" : \"" + dbuser + "\"}}" );
    }


    @RequestMapping(path = "/db/api/forum/details", method = RequestMethod.GET)
    public ResponseEntity detailsForum(@RequestBody DetailsForum body) {
        final String forum = body.getForum();
        final Array related = body.getRelated();

        String query = "select * from Forums join Users on Forums.user = Users.email where short_name = " + forum + ");";
        return ResponseEntity.ok("{}" );
    }
}


