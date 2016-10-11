package com.example.main;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

import static org.springframework.http.ResponseEntity.status;

import com.example.main.requests.CreateThread;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class threadController {

    private static final String url = "jdbc:mysql://localhost:3306/forum?autoReconnect=true&useSSL=false";
    private static final String username = "root";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    @RequestMapping(path = "/db/api/thread/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody CreateThread body){

        String query = "insert into threads VALUES (NULL, \"" + body.getForum() + "\", \"" + body.getTitle() + "\", " +
                body.isClosed() + ", \"" + body.getUser() + "\", \"" + body.getDate() + "\", \"" + body.getMessage() + "\"" +
                ", \"" + body.getSlug() + "\"" +
                ", " + body.isDeleted() + ")";

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

        query = "select id, forum, title, isClosed, user, date, message, slug, isDeleted from threads where id = (select max(id) from threads);";

        final int id;
        final String dbForum;
        final String dbTitle;
        final boolean dbIsClosed;
        final String dbUser;
        final Timestamp dbDate;
        final String dbMessage;
        final String dbSlug;
        final boolean dbDeleted;

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            dbForum = rs.getString(2);
            dbTitle = rs.getString(3);
            dbIsClosed = rs.getBoolean(4);
            dbUser = rs.getString(5);
            dbDate = rs.getTimestamp(6);
            dbMessage = rs.getString(7);
            dbSlug = rs.getString(8);
            dbDeleted = rs.getBoolean(9);


        } catch (SQLException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST).body("{" + e.getMessage() + "}");
        }finally {
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        }

        return ResponseEntity.ok("{ \"code\": 0, \"response\": { \"date\": \"" + dbDate + "\", \"forum\": \"" +
                dbForum + "\", \"id\" : \"" + id + "\", \"isClosed\" : \"" + dbIsClosed + "\", \"isDeleted\" : \"" + dbDeleted + "\"" +
                ", \"message\" : \"" + dbMessage + "\", \"slug\" : \"" + dbSlug + "\", \"title\" : \"" + dbTitle + "\", \"user\" : \"" + dbUser + "\"}}" );
    }

}
