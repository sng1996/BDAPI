package com.example.main;

import com.example.main.requests.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
    public ResponseEntity createForum(@RequestBody CreatePost body){

        String query = "insert into posts VALUES (NULL, " + body.getIsApproved() + ", \"" + body.getUser() + "\", \"" +
                body.getDate() + "\", \"" + body.getMessage() + "\", " + body.getIsSpam() + ", " + body.getIsHighlighted() + ", " + body.getThread() + ", \"" + body.getForum() + "\"," +
                "" + body.getIsDeleted() + ", " + body.getIsEdited() + ", NULL, NULL, NULL, NULL)";

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

    @RequestMapping(path = "/db/api/post/details", method = RequestMethod.GET)
    public ResponseEntity detailsPost(@RequestParam("post") Integer id,
                                       @RequestParam(value = "related", required = false) ArrayList related) {

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
                    continue;
                }
                if (related.get(i).equals("forum")) {
                    joinForumTable = "join Forums on Forums.short_name = Posts.forum ";
                    isForum = true;
                    continue;
                }
                if (related.get(i).equals("thread")) {
                    joinThreadTable = "join Threads on Threads.forum = Posts.forum ";
                    isThread = true;
                    continue;
                }
            }
        }

        String query = "select * from Posts " + joinUserTable + joinForumTable + joinThreadTable + " where Posts.id = " + id + ";";

        CreatePost postObj = new CreatePost();
        CreateForum forumObj = new CreateForum();
        CreateThread threadObj = new CreateThread();
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

        response = response + "}"; //запятую поставить между постами
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/db/api/post/list", method = RequestMethod.GET)
    public ResponseEntity listPost(@RequestParam(value = "since", required = false) String since,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                         @RequestParam(value = "thread", required = false) Integer thread,
                                         @RequestParam(value = "forum", required = false) String forum) {


        String strSince = "";
        String strLimit = "";
        String query1 = "";

        if (since != null)
            strSince = "and Posts.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        if (thread != null){
            query1 = " Posts.thread = " + thread + " ";
        }
        else{
            query1 = " Posts.forum = \'" + forum + "\' ";
        }

        String query = "select * from Posts where " + query1 + strSince + " ORDER BY Posts.date " + order + strLimit + ";";

        CreatePost postObj = new CreatePost();
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

    @RequestMapping(path = "/db/api/post/remove", method = RequestMethod.POST)
    public ResponseEntity removePost(@RequestBody RemovePost post){

        int id = post.getPost();

        String query = "update posts set isDeleted = true where id = " + id + ";";
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
        return ResponseEntity.ok("{\"code\": 0,\"response\": { \"post\": " + id + "} }");
    }

    @RequestMapping(path = "/db/api/post/restore", method = RequestMethod.POST)
    public ResponseEntity restorePost(@RequestBody RemovePost post){

        int id = post.getPost();

        String query = "update posts set isDeleted = false where id = " + id + ";";
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
        return ResponseEntity.ok("{\"code\": 0,\"response\": { \"post\": " + id + "} }");
    }

    @RequestMapping(path = "/db/api/post/update", method = RequestMethod.POST)
    public ResponseEntity updatePost(@RequestBody UpdatePost post){

        int id = post.getPost();
        String message = post.getMessage();

        String query = "update posts set message = \"" + message + "\" where id = " + id + ";";
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

        CreatePost postObj = new CreatePost();
        String userResponse = "";
        String forumResponse = "";
        String threadResponse = "";
        String response = "{" +
                "\"code\": 0," +
                "\"response\": ";

        query = "select * from posts where id = " + id + ";";

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
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

        response = response + "{ \"date\": \"" + postObj.getDate() + "\", \"dislikes\": \"" +
                postObj.getDislikes() + "\", \"forum\": \"" +
                forumResponse + "\", \"id\" : \"" + postObj.getId() + "\", \"isApproved\" : \"" + postObj.getIsApproved() + "\" , \"isDeleted\" : \"" + postObj.getIsDeleted() + "\"" +
                ", \"isEdited\" : \"" + postObj.getIsEdited() + "\" , \"isHighlighted\" : \"" + postObj.getIsHighlighted() + "\" , \"isSpam\" : \"" + postObj.getIsSpam() + "\" , \"likes\" : \"" + postObj.getLikes() + "\"" +
                ", \"message\" : \"" + postObj.getMessage() + "\" , \"parent\" : \"" + postObj.getParent() + "\" , \"points\" : \"" + postObj.getPoitns() + "\" , \"thread\" : \"" + threadResponse + "\"" +
                ", \"user\" : \"" + userResponse + "\"} ";
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

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/db/api/post/vote", method = RequestMethod.POST)
    public ResponseEntity votePost(@RequestBody VotePost post){

        int id = post.getPost();
        int vote = post.getVote();

        String query = "";

        if (vote > 0)
            query = "update posts set likes = likes + 1, points = likes - dislikes where id = " + id + ";";
        else
            query = "update posts set dislikes = dislikes + 1, points = likes - dislikes where id = " + id + ";";


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

        CreatePost postObj = new CreatePost();
        String userResponse = "";
        String forumResponse = "";
        String threadResponse = "";
        String response = "{" +
                "\"code\": 0," +
                "\"response\": ";

        query = "select * from posts where id = " + id + ";";

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
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

            response = response + "{ \"date\": \"" + postObj.getDate() + "\", \"dislikes\": \"" +
                    postObj.getDislikes() + "\", \"forum\": \"" +
                    forumResponse + "\", \"id\" : \"" + postObj.getId() + "\", \"isApproved\" : \"" + postObj.getIsApproved() + "\" , \"isDeleted\" : \"" + postObj.getIsDeleted() + "\"" +
                    ", \"isEdited\" : \"" + postObj.getIsEdited() + "\" , \"isHighlighted\" : \"" + postObj.getIsHighlighted() + "\" , \"isSpam\" : \"" + postObj.getIsSpam() + "\" , \"likes\" : \"" + postObj.getLikes() + "\"" +
                    ", \"message\" : \"" + postObj.getMessage() + "\" , \"parent\" : \"" + postObj.getParent() + "\" , \"points\" : \"" + postObj.getPoitns() + "\" , \"thread\" : \"" + threadResponse + "\"" +
                    ", \"user\" : \"" + userResponse + "\"} ";
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

        return ResponseEntity.ok(response);////
    }
}
