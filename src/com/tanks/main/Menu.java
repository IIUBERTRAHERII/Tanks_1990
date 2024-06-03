package com.tanks.main;

import com.tanks.game.DB.DBWorker;
import com.tanks.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu extends Frame implements ActionListener {

    Button startButton;
    Button exitButton;
    Button startButton1;
    Button regButton;
    DBWorker dbWorker;

    public int ID = 0;

    public Menu() {
        setTitle("Tanks 1990");
        setSize(800, 800);
        setLayout(null); // Устанавливаем null layout, чтобы позиционировать элементы вручную

        dbWorker = new DBWorker();

        startButton = new Button("Start Game normal");
        startButton.setBackground(Color.GRAY);
        startButton1 = new Button("Start Game global");
        startButton1.setBackground(Color.GRAY);
        exitButton = new Button("Exit");
        exitButton.setBackground(Color.GRAY);
        regButton = new Button("Login");
        regButton.setBackground(Color.GRAY);

        startButton.addActionListener(this);
        startButton1.addActionListener(this);
        exitButton.addActionListener(this);
        regButton.addActionListener(this);

        // Задаем размеры и позиции кнопок
        startButton.setBounds(300, 300, 200, 50);
        startButton1.setBounds(300, 400, 200, 50);
        regButton.setBounds(300, 500, 200, 50);
        exitButton.setBounds(300, 600, 200, 50);

        // Добавляем кнопки на экран
        add(startButton);
        add(startButton1);
        add(exitButton);
        add(regButton);

        setLocationRelativeTo(null);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    // Метод для рисования фона и текста
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight()); // Рисуем черный фон

        // Рисуем название игры вверху окна
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        String title = "Tanks 1990";
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        int y = fm.getAscent() + 50;
        g.drawString(title, x, y);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            if(ID!=0) {
                Game game = new Game(624, 624, ID);
                game.start();
                System.out.println("Starting game...");
                setVisible(false);
            }
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        } else if (e.getSource() == startButton1) {
            if(ID!=0) {
                Game game = new Game(1608, 1230, ID);
                game.start();
                System.out.println("Starting game...");
                setVisible(false);
            }
        } else if (e.getSource() == regButton) {
            handleLogin();
        }
    }

    private void handleLogin() {
        String[] options = {"Login", "Register", "Show Scoreboard"};
        int choice = JOptionPane.showOptionDialog(this, "Do you want to login, register, or view the scoreboard?", "Login/Register",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            login();
        } else if (choice == 1) {
            register();
        } else if (choice == 2) {
            JFrame frame = new JFrame("Scoreboard");
            ScoreboardDialog scoreboardDialog = new ScoreboardDialog(frame);
            scoreboardDialog.setVisible(true);
        }
    }

    private void login() {
        JTextField loginField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
                "Login:", loginField,
                "Password:", passwordField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());
            if (dbWorker.checkCredentials(login, password)) {
                JOptionPane.showMessageDialog(this, "Login successful");
                ID = dbWorker.getUserId(login, password);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login or password");
            }
        }
    }

    private void register() {
        JTextField nameField = new JTextField();
        JTextField loginField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
                "Name:", nameField,
                "Login:", loginField,
                "Password:", passwordField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());
            if (dbWorker.createUser(name, login, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed");
            }
        }
    }
}