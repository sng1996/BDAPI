package com.example.main;

import com.example.main.requests.CreateUser;
import com.example.main.requests.FollowUser;
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
public class userController {

    private static final String url = "jdbc:mysql://localhost:3306/forum?autoReconnect=true&useSSL=false";
    private static final String username = "root";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private static ResultSet rsFollower;
    private static ResultSet rsFollowee;

    @RequestMapping(path = "/db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody CreateUser body) {

        String query = "insert into users VALUES (NULL, \"" + body.getUsername() + "\", \"" + body.getAbout() + "\", " +
                body.getIsAnonymous() + ", \"" + body.getName() + "\", \"" + body.getEmail() + "\")";

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

        query = "SELECT id, username, about, isAnonymous, name, email FROM users WHERE id = (SELECT max(id) FROM users);";

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
        } finally {
            try {
                con.close();
            } catch (SQLException se) { /*can't do anything */ }
            try {
                stmt.close();
            } catch (SQLException se) { /*can't do anything */ }
        }

        return ResponseEntity.ok("{ \"code\": 0, \"response\": { \"about\": \"" + dbAbout + "\", \"email\": \"" +
                dbEmail + "\", \"id\" : \"" + id + "\", \"isAnonymous\" : \"" + dbIsAnonymous + "\", \"name\" : \"" + dbName + "\"" +
                ", \"username\" : \"" + dbUsername + "\"}}");
    }

    @RequestMapping(path = "/db/api/user/details", method = RequestMethod.GET)
    public ResponseEntity detailsForum(@RequestParam("user") String user) {

        String query = "select * from Users where email = " + user + ";";

        CreateUser userObj = new CreateUser();

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            userObj.setId(rs.getInt(1));
            userObj.setUsername(rs.getString(2));
            userObj.setAbout(rs.getString(3));
            userObj.setAnonymous(rs.getBoolean(4));
            userObj.setName(rs.getString(5));
            userObj.setEmail(rs.getString(6));
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

        return ResponseEntity.ok("{}");
    }

    @RequestMapping(path = "/db/api/user/follow", method = RequestMethod.POST)
    public ResponseEntity followUser(@RequestBody FollowUser body) {

        String query = "insert into Follows VALUES (NULL, \"" + body.getFollower() + "\", \"" + body.getFollowee() + "\")";
        String query1 = "select count(*) from Follows where followee = \"" + body.getFollower() + "\";";
        String query2 = "select follower from Follows where followee = \"" + body.getFollower() + "\";";
        String query3 = "select followee from Follows where follower = \"" + body.getFollower() + "\";";
        String query4 = "select * from Users where email = \"" + body.getFollower() + "\";";

        int subscriptions = 0;
        ArrayList followers = new ArrayList();
        ArrayList followees = new ArrayList();
        CreateUser userObj = new CreateUser();

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query1);
            rs.next();
            subscriptions = rs.getInt(1);
            rs = con.createStatement().executeQuery(query2);
            while (rs.next()) {
                followers.add(rs.getString(1));
            }
            rs = con.createStatement().executeQuery(query3);
            while (rs.next()) {
                followees.add(rs.getString(1));
            }
            rs = con.createStatement().executeQuery(query4);
            rs.next();
            userObj.setId(rs.getInt(1));
            userObj.setUsername(rs.getString(2));
            userObj.setAbout(rs.getString(3));
            userObj.setAnonymous(rs.getBoolean(4));
            userObj.setName(rs.getString(5));
            userObj.setEmail(rs.getString(6));
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

        String follower = "";
        String followee = "";

        for (Object i : followers) {
            follower = follower + "\"" + i + "\", ";
        }
        for (Object i : followees) {
            followee = followee + "\"" + i + "\", ";
        }

        String response = "{\"about\": \"" + userObj.getAbout() + "\", \"email\": \"" + userObj.getEmail() + "\", \"followers\" : [ " + follower + " ], \"following\" : [ " + followee + " ], \"id\" : " + userObj.getId() + ", \"isAnonymous\" : " + userObj.getIsAnonymous() + ", \"name\" : \"" + userObj.getName() + "\"" +
                ", \"subscriptions\" : " + subscriptions + ", \"username\" : \"" + userObj.getUsername() + "\"}";

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/db/api/user/listFollowers", method = RequestMethod.GET)
    public ResponseEntity listFollowersUser(@RequestParam(value = "since_id", required = false) Integer since_id,
                                         @RequestParam(value = "max_id", required = false) Integer max_id,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                         @RequestParam("user") String user) {

        String strLimit = "";
        String strSince = "";

        if (since_id != null && max_id != null){
            strSince = " and Users.id >= " + since_id + " and Users.id <= " + max_id;
        }

        if (limit != null)
            strLimit = " LIMIT " + limit;

        String query = "SELECT * FROM Follows JOIN Users ON follower = email WHERE followee = \"" + user + "\"" + strSince + " order by name " + order + " " + strLimit + ";";

        CreateUser userObj = new CreateUser();
        ArrayList followers = new ArrayList();
        ArrayList followees = new ArrayList();

        String response = "{" +
                "\"code\": 0," +
                "\"response\": [ ";
        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                userObj.setId(rs.getInt(4));
                userObj.setUsername(rs.getString(5));
                userObj.setAbout(rs.getString(6));
                userObj.setAnonymous(rs.getBoolean(7));
                userObj.setName(rs.getString(8));
                userObj.setEmail(rs.getString(9));
                String queryFollower = "select follower from Follows where followee = \"" + userObj.getEmail() + "\"";
                rsFollower = con.createStatement().executeQuery(queryFollower);
                while(rsFollower.next()){
                    followers.add(rsFollower.getString(1));
                }
                String queryFollowee = "select followee from Follows where follower = \"" + userObj.getEmail() + "\"";
                rsFollowee = con.createStatement().executeQuery(queryFollowee);
                while(rsFollowee.next()){
                    followees.add(rsFollowee.getString(1));
                }
                response = response + "{\"about\": \"" + userObj.getAbout() + "\", \"email\": \"" + userObj.getEmail() + "\", \"followers\" : [ " + followers + " ], \"following\" : [ " + followees + " ], \"id\" : " + userObj.getId() + ", \"isAnonymous\" : " + userObj.getIsAnonymous() + ", \"name\" : \"" + userObj.getName() + "\"" +
                        ", \"subscriptions\" : [], \"username\" : \"" + userObj.getUsername() + "\"}";
                followers.clear();
                followees.clear();
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

    @RequestMapping(path = "/db/api/user/listFollowing", method = RequestMethod.GET)
    public ResponseEntity listFollowingUser(@RequestParam(value = "since_id", required = false) Integer since_id,
                                            @RequestParam(value = "max_id", required = false) Integer max_id,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                            @RequestParam("user") String user) {

        String strLimit = "";
        String strSince = "";

        if (since_id != null && max_id != null){
            strSince = " and Users.id >= " + since_id + " and Users.id <= " + max_id;
        }

        if (limit != null)
            strLimit = " LIMIT " + limit;

        String query = "SELECT * FROM Follows JOIN Users ON followee = email WHERE follower = \"" + user + "\"" + strSince + " order by name " + order + " " + strLimit + ";";

        CreateUser userObj = new CreateUser();
        ArrayList followers = new ArrayList();
        ArrayList followees = new ArrayList();

        String response = "{" +
                "\"code\": 0," +
                "\"response\": [ ";
        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                userObj.setId(rs.getInt(4));
                userObj.setUsername(rs.getString(5));
                userObj.setAbout(rs.getString(6));
                userObj.setAnonymous(rs.getBoolean(7));
                userObj.setName(rs.getString(8));
                userObj.setEmail(rs.getString(9));
                String queryFollower = "select follower from Follows where followee = \"" + userObj.getEmail() + "\"";
                rsFollower = con.createStatement().executeQuery(queryFollower);
                while(rsFollower.next()){
                    followers.add(rsFollower.getString(1));
                }
                String queryFollowee = "select followee from Follows where follower = \"" + userObj.getEmail() + "\"";
                rsFollowee = con.createStatement().executeQuery(queryFollowee);
                while(rsFollowee.next()){
                    followees.add(rsFollowee.getString(1));
                }
                response = response + "{\"about\": \"" + userObj.getAbout() + "\", \"email\": \"" + userObj.getEmail() + "\", \"followers\" : [ " + followers + " ], \"following\" : [ " + followees + " ], \"id\" : " + userObj.getId() + ", \"isAnonymous\" : " + userObj.getIsAnonymous() + ", \"name\" : \"" + userObj.getName() + "\"" +
                        ", \"subscriptions\" : [], \"username\" : \"" + userObj.getUsername() + "\"}";
                followers.clear();
                followees.clear();
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
