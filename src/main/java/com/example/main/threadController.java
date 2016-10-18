package com.example.main;

import com.example.main.requests.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;

import static org.springframework.http.ResponseEntity.status;

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
                ", " + body.isDeleted() + ", NULL, NULL, NULL, NULL)";

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

    @RequestMapping(path = "/db/api/thread/close", method = RequestMethod.POST)
    public ResponseEntity closeThread(@RequestBody CloseThread thread){

        int id = thread.getId();

        String query = "update threads set isClosed = true where id = " + id + ";";
        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
        }
        return ResponseEntity.ok("{\"code\": 0,\"response\": { \"thread\": " + id + "} }");
    }

    @RequestMapping(path = "/db/api/thread/details", method = RequestMethod.GET)
    public ResponseEntity detailsThread(@RequestParam("thread") Integer id,
                                      @RequestParam(value = "related", required = false) ArrayList related) {

        String joinUserTable = "";
        String joinForumTable = "";
        boolean isUser = false;
        boolean isForum = false;

        if (related != null) {
            for (int i = 0; i < related.size(); i++) {
                if (related.get(i).equals("user")) {
                    joinUserTable = "join Users on Threads.user = Users.email ";
                    isUser = true;
                    continue;
                }
                if (related.get(i).equals("forum")) {
                    joinForumTable = "join Forums on Forums.short_name = Threads.forum ";
                    isForum = true;
                    continue;
                }
            }
        }

        String query = "select * from Threads " + joinUserTable + joinForumTable + " where threads.id = " + id + ";";

        CreateThread threadObj = new CreateThread();
        CreateForum forumObj = new CreateForum();
        CreateUser userObj = new CreateUser();
        String userResponse = "";
        String forumResponse = "";
        String threadResponse = "";
        String response = "{" +
                "\"code\": 0," +
                "\"response\": ";
        int count = 0;

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            threadObj.setId(rs.getInt(1));
            threadObj.setForum(rs.getString(2));
            threadObj.setTitle(rs.getString(3));
            threadObj.setClosed(rs.getBoolean(4));
            threadObj.setUser(rs.getString(5));
            threadObj.setTmpDate(rs.getTimestamp(6));
            threadObj.setMessage(rs.getString(7));
            threadObj.setSlug(rs.getString(8));
            threadObj.setDeleted(rs.getBoolean(9));
            threadObj.setDislikes(rs.getInt(10));
            threadObj.setLikes(rs.getInt(11));
            threadObj.setPoints(rs.getInt(12));
            threadObj.setPosts(rs.getInt(13));
            userResponse = threadObj.getUser();
            forumResponse = threadObj.getForum();
            if (isUser) {
                userObj.setId(rs.getInt(14));
                userObj.setUsername(rs.getString(15));
                userObj.setAbout(rs.getString(16));
                userObj.setAnonymous(rs.getBoolean(17));
                userObj.setName(rs.getString(18));
                userObj.setEmail(rs.getString(19));
                userResponse = "{\"about\": \"" + userObj.getAbout() + "\", \"email\": \"" + userObj.getEmail() + "\", \"followers\" : [], \"following\" : [], \"id\" : " + userObj.getId() + ", \"isAnonymous\" : " + userObj.getIsAnonymous() + ", \"name\" : \"" + userObj.getName() + "\"" +
                        ", \"subscriptions\" : [], \"username\" : \"" + userObj.getUsername() + "\"}";
                count = 6;
            }
            if (isForum) {
                forumObj.setId(rs.getInt(14 + count));
                forumObj.setName(rs.getString(15 + count));
                forumObj.setShort_name(rs.getString(16 + count));
                forumObj.setUser((rs.getString(17 + count)));
                forumResponse = "{ \"id\": " + forumObj.getId() + ", \"name\": \"" +
                        forumObj.getName() + "\", \"short_name\" : \"" + forumObj.getShort_name() + "\", \"user\" : \"" + forumObj.getName() + "\"}";
            }
            threadResponse = "{ \"date\": \"" + threadObj.getDate() + "\", \"dislikes\": \"" +
                    threadObj.getDislikes() + "\", \"forum\": \"" +
                    forumResponse + "\", \"id\" : \"" + threadObj.getId() + "\", \"isClosed\" : " + threadObj.isClosed() + ", \"isDeleted\" : " + threadObj.isDeleted() + ", \"likes\": \"" +
                    threadObj.getLikes() + "\", " +
                    " \"message\" : \"" + threadObj.getMessage() + "\", \"points\": " +
                    threadObj.getPoints() + ", \"posts\": " +
                    threadObj.getPosts() + ", \"slug\" : \"" + threadObj.getSlug() + "\", \"title\" : \"" + threadObj.getTitle() + "\", \"user\" : \"" + userResponse + "\"}";

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
        }

        response = response + threadResponse;
        response = response + "}"; //запятую поставить между постами

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/db/api/thread/list", method = RequestMethod.GET)
    public ResponseEntity listThread(@RequestParam(value = "since", required = false) String since,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                   @RequestParam(value = "user", required = false) String user,
                                   @RequestParam(value = "forum", required = false) String forum) {


        String strSince = "";
        String strLimit = "";
        String query1 = "";

        if (since != null)
            strSince = "and Threads.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        if (user != null){
            query1 = " Threads.user = " + user + " ";
        }
        else{
            query1 = " Threads.forum = \'" + forum + "\' ";
        }

        String query = "select * from Threads where " + query1 + strSince + " ORDER BY Threads.date " + order + strLimit + ";";

        CreateThread threadObj = new CreateThread();
        String userResponse = "";
        String forumResponse = "";
        String threadResponse = "";
        String response = "{" +
                "\"code\": 0," +
                "\"response\": [ ";

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                threadObj.setId(rs.getInt(1));
                threadObj.setForum(rs.getString(2));
                threadObj.setTitle(rs.getString(3));
                threadObj.setClosed(rs.getBoolean(4));
                threadObj.setUser(rs.getString(5));
                threadObj.setTmpDate(rs.getTimestamp(6));
                threadObj.setMessage(rs.getString(7));
                threadObj.setSlug(rs.getString(8));
                threadObj.setDeleted(rs.getBoolean(9));
                threadObj.setDislikes(rs.getInt(10));
                threadObj.setLikes(rs.getInt(11));
                threadObj.setPoints(rs.getInt(12));
                threadObj.setPosts(rs.getInt(13));
                userResponse = threadObj.getUser();
                forumResponse = threadObj.getForum();

                response = response + "{ \"date\": \"" + threadObj.getDate() + "\", \"dislikes\": \"" +
                        threadObj.getDislikes() + "\", \"forum\": \"" +
                        forumResponse + "\", \"id\" : \"" + threadObj.getId() + "\", \"isClosed\" : " + threadObj.isClosed() + ", \"isDeleted\" : " + threadObj.isDeleted() + ", \"likes\": \"" +
                        threadObj.getLikes() + "\", " +
                        " \"message\" : \"" + threadObj.getMessage() + "\", \"points\": " +
                        threadObj.getPoints() + ", \"posts\": " +
                        threadObj.getPosts() + ", \"slug\" : \"" + threadObj.getSlug() + "\", \"title\" : \"" + threadObj.getTitle() + "\", \"user\" : \"" + userResponse + "\"}";

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
        }

        response = response + "] }"; //запятую поставить между постами
        return ResponseEntity.ok(response);
    }
}
