package com.example.aggelos.tempcontrol.AzureConnection;

import com.example.aggelos.tempcontrol.Domain.User;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by aggelos on 12/14/2017.
 */

public class DatabaseConnection {

    private static final String SQL_SERVER = "agpdb.database.windows.net";
    private static final String USER_ID = "aggelos";
    private static final String PASSWORD = "d%@^pf:6eaE";
    private static final String DATABASE = "TempControl";

    private Connection _conn;

    public boolean connectionOpen() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        _conn = null;
        String conn_string = "jdbc:jtds:sqlserver://" + SQL_SERVER + ";databaseName=" + DATABASE;
        try {
            _conn = DriverManager.getConnection(conn_string, USER_ID, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void connectionClose(){
        try {
            _conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        if (!connectionOpen()) {
            return;
        }

        try {
            String sql_query = "Select Max(Priority) From dbo.Users;";

            PreparedStatement preparedStatement = _conn.prepareStatement(sql_query);
            ResultSet results = preparedStatement.executeQuery();
            while (results.next()) {
                user.setPriority(results.getInt(1) + 1);
            }

            sql_query = "Insert Into dbo.Users (Priority, [Name], OptimTemp) Values (?, ?, ?);";

            preparedStatement = _conn.prepareStatement(sql_query);
            preparedStatement.setInt(1, user.getPriority());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setInt(3, user.getOptimTemp());


            preparedStatement.executeUpdate();
            connectionClose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(User user) {
        if (!connectionOpen()) {
            return user;
        }
        try {
            if (user.getName() != null){
                String sql_query = "Select * From dbo.Users Where ([Name] = ?);";

                PreparedStatement preparedStatement = _conn.prepareStatement(sql_query);
                preparedStatement.setString(1, user.getName());

                ResultSet results = preparedStatement.executeQuery();
                while (results.next()) {
                    user.setPriority(results.getInt(1));
                    user.setOptimTemp(results.getInt(3));
                }

                connectionClose();
                return user;
            }
            String sql_query = "Select * From dbo.Users Where (Priority = ?);";

            PreparedStatement preparedStatement = _conn.prepareStatement(sql_query);
            preparedStatement.setInt(1, user.getPriority());

            ResultSet results = preparedStatement.executeQuery();
            while (results.next()) {
                user.setName(results.getString(2));
                user.setOptimTemp(results.getInt(3));
            }

            connectionClose();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return user;
        }
    }

    public void updateUser(User user) {
        if (!connectionOpen()) {
            return;
        }
        try {
            String sql_query = "Update dbo.Users Set [Name] = ?, OptimTemp = ? Where (Priority = ?);";

            PreparedStatement preparedStatement = _conn.prepareStatement(sql_query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setInt(2, user.getOptimTemp());
            preparedStatement.setInt(3, user.getPriority());

            preparedStatement.executeUpdate();
            connectionClose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user) {
        if (!connectionOpen()) {
            return;
        }
        try {
            String sql_query = "Delete From dbo.Users Where (Priority = ? And [Name] = ? And OptimTemp = ?);";

            PreparedStatement preparedStatement = _conn.prepareStatement(sql_query);
            preparedStatement.setInt(1, user.getPriority());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setInt(3, user.getOptimTemp());

            preparedStatement.executeUpdate();
            connectionClose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getUsersList() {
        try {
            if (!connectionOpen()) {
                return new ArrayList<>();
            }

            String sql_query = "Select [Name] From dbo.Users;";

            PreparedStatement preparedStatement = _conn.prepareStatement(sql_query);
            ResultSet results = preparedStatement.executeQuery();

            ArrayList<String> Users = new ArrayList<>();
            while (results.next()) {
                Users.add(results.getString(1));
            }

            connectionClose();
            return Users;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
}