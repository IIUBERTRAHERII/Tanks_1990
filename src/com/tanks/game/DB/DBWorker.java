package com.tanks.game.DB;

import java.sql.*;

public class DBWorker {
    public static final String PATH_TO_DB_FILE = "mydb.db";
    public static final String URL = "jdbc:sqlite:" + PATH_TO_DB_FILE;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS player (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "score INTEGER," +
                "login TEXT," +
                "password TEXT" +
                ");";

        try (Connection connection = DriverManager.getConnection(URL);
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        }
    }

    public boolean checkCredentials(String login, String password) {
        String query = "SELECT * FROM player WHERE login = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUser(String name, String login, String password) {
        String query = "INSERT INTO player (name, score, login, password) VALUES (?, 0, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, login);
            statement.setString(3, password);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getUserId(String login, String password) {
        String query = "SELECT id FROM player WHERE login = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // В случае, если пользователя с таким логином и паролем не найдено
    }

    public void addScore(int userId, int scoreToAdd) {
        String query = "UPDATE player SET score = score + ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, scoreToAdd);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ResultSet getAllPlayers() throws SQLException {
        String query = "SELECT id, name, score FROM player";
        Connection connection = DriverManager.getConnection(URL);
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

}