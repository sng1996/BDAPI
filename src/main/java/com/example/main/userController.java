package com.example.main;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

import static org.springframework.http.ResponseEntity.status;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class userController {

    private static final String url = "jdbc:mysql://localhost:3306/forum?autoReconnect=true&useSSL=false";
    private static final String username = "root";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    @RequestMapping(path = "/db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody userController.CreateUser body){

        String query = "insert into users VALUES (NULL, \"" + body.getUsername() + "\", \"" + body.getAbout() + "\", " +
                body.getIsAnonymous() + ", \"" + body.getName() + "\", \"" + body.getEmail() + "\")";

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

        query = "select id, username, about, isAnonymous, name, email from users where id = (select max(id) from users);";

        final int id;
        final String dbUsername;
        final String dbAbout;
        final boolean dbIsAnonymous;
        final String dbName;
        final String dbEmail;

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            dbUsername = rs.getString(2);
            dbAbout = rs.getString(3);
            dbIsAnonymous = rs.getBoolean(4);
            dbName = rs.getString(5);
            dbEmail = rs.getString(6);


        } catch (SQLException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST).body("{" + e.getMessage() + "}");
        }finally {
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        }

        return ResponseEntity.ok("{ \"code\": 0, \"response\": { \"about\": \"" + dbAbout + "\", \"email\": \"" +
                dbEmail + "\", \"id\" : \"" + id + "\", \"isAnonymous\" : \"" + dbIsAnonymous + "\", \"name\" : \"" + dbName + "\"" +
                ", \"username\" : \"" + dbUsername + "\"}}" );
    }

    private static final class CreateUser{
        private String username;
        private String about;
        private boolean isAnonymous;
        private String name;
        private String email;

        private CreateUser(){
        }

        public CreateUser(String username, String about, boolean isAnonymous, String name, String email) {
            this.username = username;
            this.about = about;
            this.isAnonymous = isAnonymous;
            this.name = name;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getAbout() {
            return about;
        }

        public boolean getIsAnonymous() {
            return isAnonymous;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

}
