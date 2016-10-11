package com.example.main;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.springframework.http.ResponseEntity.status;

/**
 * Created by sergeigavrilko on 10.10.16.
 */

@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class postController {
    private static final String url = "jdbc:mysql://localhost:3306/forum?autoReconnect=true&useSSL=false";
    private static final String username = "root";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    @RequestMapping(path = "/db/api/post/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody postController.CreatePost body){

        String query = "insert into posts VALUES (NULL, " + body.getIsApproved() + ", \"" + body.getUser() + "\", \"" +
                body.getDate() + "\", \"" + body.getMessage() + "\", " + body.getIsSpam() + ", " + body.getIsHighlighted() + ", " + body.getThread() + ", \"" + body.getForum() + "\"," +
                "" + body.isDeleted + ", " + body.isEdited + ")";

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

        query = "select id, isApproved, user, date, message, isSpam, isHighlighted, thread, forum, isDeleted, isEdited from posts where id = (select max(id) from posts);";

        final int id;
        final boolean isApproved;
        final String user;
        final Timestamp date;
        final String message;
        final boolean isSpam;
        final boolean isHighlighted;
        final Integer thread;
        final String forum;
        final boolean isDeleted;
        final boolean isEdited;

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            id = rs.getInt(1);
            isApproved = rs.getBoolean(2);
            user = rs.getString(3);
            date = rs.getTimestamp(4);
            message = rs.getString(5);
            isSpam = rs.getBoolean(6);
            isHighlighted = rs.getBoolean(7);
            thread = rs.getInt(8);
            forum = rs.getString(9);
            isDeleted = rs.getBoolean(10);
            isEdited = rs.getBoolean(11);

        } catch (SQLException e) {
            e.printStackTrace();
            return status(HttpStatus.BAD_REQUEST).body("{" + e.getMessage() + "}");
        }finally {
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        }

        return ResponseEntity.ok("{ \"code\": 0, \"response\": { \"date\": \"" + date + "\", \"forum\": \"" +
                forum + "\", \"id\" : \"" + id + "\", \"isApproved\" : \"" + isApproved + "\" , \"isDeleted\" : \"" + isDeleted + "\"" +
                ", \"isEdited\" : \"" + isEdited + "\" , \"isHighlighted\" : \"" + isHighlighted + "\" , \"isSpam\" : \"" + isSpam + "\"" +
                ", \"message\" : \"" + message + "\" , \"parent\" : \"" + null + "\" , \"thread\" : \"" + thread + "\"" +
                ", \"user\" : \"" + user + "\"}}" );
    }

    private static final class CreatePost{

        private boolean isApproved;
        private String user;
        private String date;
        private String message;
        private boolean isSpam;
        private boolean isHighlighted;
        private Integer thread;
        private String forum;
        private boolean isDeleted;
        private boolean isEdited;



        private CreatePost(){
        }

        public CreatePost(boolean isApproved, String user, String date, String message, boolean isSpam, boolean isHighlighted, Integer thread, String forum, boolean isDeleted, boolean isEdited) throws ParseException {
            this.isApproved = isApproved;
            this.user = user;
            this.date = date;
            this.message = message;
            this.isSpam = isSpam;
            this.isHighlighted = isHighlighted;
            this.thread = thread;
            this.forum = forum;
            this.isDeleted = isDeleted;
            this.isEdited = isEdited;
        }


        public boolean getIsApproved() {
            return isApproved;
        }

        public String getUser() {
            return user;
        }

        public String getDate() {
            return date;
        }

        public String getMessage() {
            return message;
        }

        public boolean getIsSpam() {
            return isSpam;
        }

        public boolean getIsHighlighted() {
            return isHighlighted;
        }

        public Integer getThread() {
            return thread;
        }

        public String getForum() {
            return forum;
        }

        public boolean getIsDeleted() {
            return isDeleted;
        }

        public boolean getIsEdited() {
            return isEdited;
        }
    }
}
