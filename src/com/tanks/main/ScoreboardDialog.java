package com.tanks.main;
import com.tanks.game.DB.DBWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoreboardDialog extends JDialog {

    private JTable table;

    public ScoreboardDialog(JFrame parent) {
        super(parent, "Scoreboard", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        // Создание таблицы
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Заполнение таблицы данными из базы данных
        updateScoreboard();
    }

    private void updateScoreboard() {
        DBWorker dbWorker = new DBWorker();
        try {
            ResultSet resultSet = dbWorker.getAllPlayers();
            // Создание модели таблицы и заполнение её данными из ResultSet
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Name");
            model.addColumn("Score");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int score = resultSet.getInt("score");
                model.addRow(new Object[]{id, name, score});
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}