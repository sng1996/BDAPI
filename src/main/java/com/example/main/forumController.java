package com.example.main;

import com.example.main.requests.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.eclipse.jetty.server.Request;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;


import javax.servlet.ServletRequest;

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
    public ResponseEntity createForum(@RequestBody CreateForum body) {
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
        } finally {
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
        }

        query = "SELECT id, name, short_name, user FROM forums WHERE id = (SELECT max(id) FROM forums);";

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
        } finally {
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
        }

        return ResponseEntity.ok("{ \"code\": 0, \"response\": { \"id\": \"" + id + "\", \"name\": \"" +
                dbname + "\", \"short_name\" : \"" + dbshort_name + "\", \"user\" : \"" + dbuser + "\"}}");
    }


    @RequestMapping(path = "/db/api/forum/details", method = RequestMethod.GET)
    public ResponseEntity detailsForum(@RequestParam("forum") String forum,
                                       @RequestParam(value = "related", required = false) ArrayList related) {

        String joinUserTable = "";
        boolean isUser = false;
        if (related != null) {
            for (int i = 0; i < related.size(); i++) {
                if (related.get(i).equals("user")) {
                    joinUserTable = "join Users on Forums.user = Users.email";
                    isUser = true;
                }
            }
        }

        String query = "select * from Forums " + joinUserTable + " where short_name = \'" + forum + "\';";

        int forumId = 0;
        String dbForumName = null;
        String dbShort_name = null;
        int userId = 0;
        String dbUsername;
        String dbAbout = null;
        boolean dbIsAnonymous = false;
        String dbUserName = null;
        String dbEmail = null;

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            forumId = rs.getInt(1);
            dbForumName = rs.getString(2);
            dbShort_name = rs.getString(3);
            dbEmail = rs.getString(4);
            if (isUser) {
                userId = rs.getInt(5);
                dbUsername = rs.getString(6);
                dbAbout = rs.getString(7);
                dbIsAnonymous = rs.getBoolean(8);
                dbUserName = rs.getString(9);
                dbEmail = rs.getString(10);
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

        String responseUser = "\"" + dbEmail + "\"";

        if (isUser) {
            responseUser = "{\"about\": \"" + dbAbout + "\", \"email\": \"" +
                    dbEmail + "\", \"followers\" : [], \"following\" : [], \"id\" : \"" + userId + "\", \"isAnonymous\" : \"" + dbIsAnonymous + "\", \"name\" : \"" + dbUserName + "\"" +
                    ", \"subscriptions\" : [], \"username\" : \"" + dbUserName + "\"}";
        }

        final String response = "{ \"code\": 0, \"response\": { \"id\": \"" + forumId + "\", \"name\": \"" +
                dbForumName + "\", \"short_name\" : \"" + dbShort_name + "\", \"user\" : " + responseUser + "}}";

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/db/api/forum/listPosts", method = RequestMethod.GET)
    public ResponseEntity listPostsForum(@RequestParam(value = "since", required = false) String since,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                         @RequestParam(value = "related", required = false) ArrayList related,
                                         @RequestParam("forum") String forum) {

        String joinUserTable = "";
        String joinForumTable = "";
        String joinThreadTable = "";
        boolean isUser = false;
        boolean isForum = false;
        boolean isThread = false;

        if (related != null) {
            for (int i = 0; i < related.size(); i++) {
                if (related.get(i).equals("user")) {
                    joinUserTable = "join Users on Posts.user = Users.email ";
                    isUser = true;
                    break;
                }
                if (related.get(i).equals("forum")) {
                    joinForumTable = "join Forums on Forums.short_name = Posts.forum ";
                    isForum = true;
                    break;
                }
                if (related.get(i).equals("thread")) {
                    joinThreadTable = "join Threads on Threads.forum = Posts.forum ";
                    isThread = true;
                    break;
                }
            }
        }

        String strSince = "";
        String strLimit = "";

        if (since != null)
            strSince = "and Posts.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        String query = "select * from Posts " + joinUserTable + joinForumTable + joinThreadTable + " where Posts.forum = \'" + forum + "\' " + strSince + " ORDER BY Posts.date " + order + strLimit + ";";

        CreatePost postObj = new CreatePost();
        CreateForum forumObj = new CreateForum();
        CreateThread threadObj = new CreateThread();
        CreateUser userObj = new CreateUser();
        String userResponse = "";
        String forumResponse = "";
        String threadResponse = "";
        String response = "{" +
                "\"code\": 0," +
                "\"response\": [ ";
        int count = 0;

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                postObj.setId(rs.getInt(1));
                postObj.setApproved(rs.getBoolean(2));
                postObj.setUser(rs.getString(3));
                postObj.setTmpDate(rs.getTimestamp(4));
                postObj.setMessage(rs.getString(5));
                postObj.setSpam(rs.getBoolean(6));
                postObj.setHighlighted(rs.getBoolean(7));
                postObj.setThread(rs.getInt(8));
                postObj.setForum(rs.getString(9));
                postObj.setDeleted(rs.getBoolean(10));
                postObj.setEdited(rs.getBoolean(11));
                postObj.setDislikes(rs.getInt(12));
                postObj.setLikes(rs.getInt(13));
                postObj.setParent(rs.getInt(14));
                postObj.setPoitns(rs.getInt(15));
                userResponse = postObj.getUser();
                forumResponse = postObj.getForum();
                threadResponse = Integer.toString(postObj.getThread());
                count = 0;

                if (isUser) {
                    userObj.setId(rs.getInt(16));
                    userObj.setUsername(rs.getString(17));
                    userObj.setAbout(rs.getString(18));
                    userObj.setAnonymous(rs.getBoolean(19));
                    userObj.setName(rs.getString(20));
                    userObj.setEmail(rs.getString(21));
                    userResponse = "{\"about\": \"" + userObj.getAbout() + "\", \"email\": \"" + userObj.getEmail() + "\", \"followers\" : [], \"following\" : [], \"id\" : " + userObj.getId() + ", \"isAnonymous\" : " + userObj.getIsAnonymous() + ", \"name\" : \"" + userObj.getName() + "\"" +
                            ", \"subscriptions\" : [], \"username\" : \"" + userObj.getUsername() + "\"}";
                    count = 6;
                }
                if (isForum) {
                    forumObj.setId(rs.getInt(16 + count));
                    forumObj.setName(rs.getString(17 + count));
                    forumObj.setShort_name(rs.getString(18 + count));
                    forumObj.setUser((rs.getString(19 + count)));
                    forumResponse = "{ \"id\": " + forumObj.getId() + ", \"name\": \"" +
                            forumObj.getName() + "\", \"short_name\" : \"" + forumObj.getShort_name() + "\", \"user\" : \"" + forumObj.getName() + "\"}";
                    count += 4;
                }
                if (isThread) {
                    threadObj.setId(rs.getInt(16 + count));
                    threadObj.setForum(rs.getString(17 + count));
                    threadObj.setTitle(rs.getString(18 + count));
                    threadObj.setClosed(rs.getBoolean(19 + count));
                    threadObj.setUser(rs.getString(20 + count));
                    threadObj.setTmpDate(rs.getTimestamp(21 + count));
                    threadObj.setMessage(rs.getString(22 + count));
                    threadObj.setSlug(rs.getString(23 + count));
                    threadObj.setDeleted(rs.getBoolean(24 + count));
                    threadObj.setDislikes(rs.getInt(25 + count));
                    threadObj.setLikes(rs.getInt(26 + count));
                    threadObj.setPoints(rs.getInt(27 + count));
                    threadObj.setPosts(rs.getInt(28 + count));
                    threadResponse = "{ \"date\": \"" + threadObj.getDate() + "\", \"dislikes\": \"" +
                            threadObj.getDislikes() + "\", \"forum\": \"" +
                            threadObj.getForum() + "\", \"id\" : \"" + threadObj.getId() + "\", \"isClosed\" : " + threadObj.isClosed() + ", \"isDeleted\" : " + threadObj.isDeleted() + ", \"likes\": \"" +
                            threadObj.getLikes() + "\", " +
                            " \"message\" : \"" + threadObj.getMessage() + "\", \"points\": " +
                            threadObj.getPoints() + ", \"posts\": " +
                            threadObj.getPosts() + ", \"slug\" : \"" + threadObj.getSlug() + "\", \"title\" : \"" + threadObj.getTitle() + "\", \"user\" : \"" + threadObj.getUser() + "\"}";
                }

                response = response + "{ \"date\": \"" + postObj.getDate() + "\", \"dislikes\": \"" +
                        postObj.getDislikes() + "\", \"forum\": \"" +
                        forumResponse + "\", \"id\" : \"" + postObj.getId() + "\", \"isApproved\" : \"" + postObj.getIsApproved() + "\" , \"isDeleted\" : \"" + postObj.getIsDeleted() + "\"" +
                        ", \"isEdited\" : \"" + postObj.getIsEdited() + "\" , \"isHighlighted\" : \"" + postObj.getIsHighlighted() + "\" , \"isSpam\" : \"" + postObj.getIsSpam() + "\" , \"likes\" : \"" + postObj.getLikes() + "\"" +
                        ", \"message\" : \"" + postObj.getMessage() + "\" , \"parent\" : \"" + postObj.getParent() + "\" , \"points\" : \"" + postObj.getPoitns() + "\" , \"thread\" : \"" + threadResponse + "\"" +
                        ", \"user\" : \"" + userResponse + "\"} ";
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


    @RequestMapping(path = "/db/api/forum/listThreads", method = RequestMethod.GET)
    public ResponseEntity listThreadsForum(@RequestParam(value = "since", required = false) String since,
                                           @RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                           @RequestParam(value = "related", required = false) ArrayList related,
                                           @RequestParam("forum") String forum) {

        String joinUserTable = "";
        String joinForumTable = "";
        boolean isUser = false;
        boolean isForum = false;

        if (related != null) {
            for (int i = 0; i < related.size(); i++) {
                if (related.get(i).equals("user")) {
                    joinUserTable = "join Users on Threads.user = Users.email ";
                    isUser = true;
                    break;
                }
                if (related.get(i).equals("forum")) {
                    joinForumTable = "join Forums on Forums.short_name = Threads.forum ";
                    isForum = true;
                    break;
                }
            }
        }

        String strSince = "";
        String strLimit = "";

        if (since != null)
            strSince = "and Threads.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        String query = "select * from threads " + joinUserTable + joinForumTable + " where Threads.forum = \'" + forum + "\' " + strSince + " ORDER BY Threads.date " + order + strLimit + ";";

        CreateForum forumObj = new CreateForum();
        CreateThread threadObj = new CreateThread();
        CreateUser userObj = new CreateUser();
        String userResponse = "";
        String forumResponse = "";

        String response = "{" +
                "\"code\": 0," +
                "\"response\": [ ";
        int count = 0;

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
                count = 0;
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
                response = response + "{ \"date\": \"" + threadObj.getDate() + "\", \"dislikes\": \"" +
                        threadObj.getDislikes() + "\", \"forum\": \"" +
                        forumResponse + "\", \"id\" : \"" + threadObj.getId() + "\", \"isClosed\" : \"" + threadObj.isClosed() + "\", \"isDeleted\" : \"" + threadObj.isDeleted() + "\", \"likes\": \"" +
                        threadObj.getLikes() + "\", " +
                        "\"message\" : \"" + threadObj.getMessage() + "\", \"points\": " +
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

    @RequestMapping(path = "/db/api/forum/listUsers", method = RequestMethod.GET)
    public ResponseEntity listUsersForum(@RequestParam(value = "since_id", required = false) Integer since_id,
                                         @RequestParam(value = "max_id", required = false) Integer max_id,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                         @RequestParam("forum") String forum) {

        String strLimit = "";
        String strSince = "";

        if (since_id != null && max_id != null){
            strSince = " and Users.id >= " + since_id + " and Users.id <= " + max_id;
        }

        if (limit != null)
            strLimit = " LIMIT " + limit;

        String query = "Select Users.id, username, about, Users.isAnonymous, Users.name, email from Forums join Posts on Forums.short_name = Posts.forum join Users on Posts.user = Users.email where forums.short_name = \"" + forum + "\" " + strSince + " order by Users.name " + strLimit + ";";

        CreateUser userObj = new CreateUser();

        String response = "{" +
                "\"code\": 0," +
                "\"response\": [ ";
        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                userObj.setId(rs.getInt(1));
                userObj.setUsername(rs.getString(2));
                userObj.setAbout(rs.getString(3));
                userObj.setAnonymous(rs.getBoolean(4));
                userObj.setName(rs.getString(5));
                userObj.setEmail(rs.getString(6));
                response = response + "{\"about\": \"" + userObj.getAbout() + "\", \"email\": \"" + userObj.getEmail() + "\", \"followers\" : [], \"following\" : [], \"id\" : " + userObj.getId() + ", \"isAnonymous\" : " + userObj.getIsAnonymous() + ", \"name\" : \"" + userObj.getName() + "\"" +
                        ", \"subscriptions\" : [], \"username\" : \"" + userObj.getUsername() + "\"}";
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



