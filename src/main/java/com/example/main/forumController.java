package com.example.main;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;

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

    private static final class CreateForum{
        private String name;
        private String short_name;
        private String user;

        private CreateForum(){
        }

        private CreateForum(String name, String short_name, String user){
            this.name = name;
            this.short_name = short_name;
            this.user = user;
        }


        public String getName() {
            return name;
        }

        public String getShort_name() {
            return short_name;
        }

        public String getUser() {
            return user;
        }
    }

}


