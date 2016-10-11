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
public class generalController {

    private static final String url = "jdbc:mysql://localhost:3306/forum?autoReconnect=true&useSSL=false";
    private static final String username = "root";
    private static final String password = "";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private static Statement smt;
    private static ResultSet tmpRs;

    @RequestMapping(path = "/db/api/clear", method = RequestMethod.POST)
    public ResponseEntity clear(){

        String query = "SELECT CONCAT('truncate table ',table_name,';')\n" +
                "FROM INFORMATION_SCHEMA.TABLES\n" +
                "WHERE TABLE_SCHEMA = 'forum'\n" +
                "AND TABLE_TYPE = 'BASE TABLE';";

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()){
                smt = con.createStatement();
                smt.executeUpdate(rs.getString(1));
            }
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

        return ResponseEntity.ok("{\" code : 0, \"response\": \"OK\"}" );
    }

    @RequestMapping(path = "/db/api/status", method = RequestMethod.GET)
    public ResponseEntity status(){

        Integer[] arr = new Integer[4];
        int i = 0;

        String query = "SELECT CONCAT('select count(*) from ',table_name,';')\n" +
                "FROM INFORMATION_SCHEMA.TABLES\n" +
                "WHERE TABLE_SCHEMA = 'forum'\n" +
                "AND TABLE_TYPE = 'BASE TABLE';";

        try {
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()){
                smt = con.createStatement();
                tmpRs = smt.executeQuery(rs.getString(1));
                tmpRs.next();
                arr[i] = tmpRs.getInt(1);
                i++;
            }
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

        return ResponseEntity.ok("{\" code\" : 0, \"response\": {\"user\": " + arr[3] +
                "\", \"thread\": " + arr[2] +
                "\",\"forum\": " + arr[0] +
                "\",\"post\": " + arr[1] +
                "}" );
    }

}
