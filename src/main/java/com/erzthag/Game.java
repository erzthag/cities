package com.erzthag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Game implements ActionListener, KeyListener {

    JFrame frame;
    JTextField textfield;
    JTextField textField2;
    JTextField history;
    Font myFont = new Font("Times New Roman", Font.BOLD, 20);
    JButton enterButton;
    JButton newGameButton;
    static boolean isWin = true;
    private static String city;

    private static char lastChar;

    static List <String> cities = new ArrayList<>();
    private static int index = 1;
    private static int count = 0;
    DBWorker dbWorker = new DBWorker();
    Game() {
        frame = new JFrame("Cities");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(445, 600);
        frame.setLayout(null);
        textfield = new JTextField();
        textfield.setBounds(30, 25, 365, 50);
        textfield.setFont(myFont);
        textfield.setEditable(false);
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        enterButton = new JButton("Enter");
        enterButton.setBounds(30, 150, 365, 75);
        enterButton.addActionListener(this);
        enterButton.setFont(myFont);
        enterButton.setFocusable(false);

        newGameButton = new JButton("New Game");
        newGameButton.setBounds(30, 250, 365, 75);
        newGameButton.addActionListener(this);
        newGameButton.setFont(myFont);
        newGameButton.setFocusable(false);

        textField2 = new JTextField();
        textField2.setBounds(30, 90, 365, 50);
        textField2.setFont(myFont);
        textField2.setEditable(true);

        textField2.addKeyListener(this);

        history = new JTextField();
        history.setBounds(30, 350, 365, 150);
        history.setFont(myFont);
        history.setEditable(false);


        frame.add(enterButton);
        frame.setVisible(true);
        frame.add(textfield);
        frame.add(textField2);
        frame.add(history);
        frame.add(newGameButton);
    }
    private char checkLastChar(String city, int index) throws SQLException {

        char lastChar = city.charAt(city.length() - index);

        ResultSet rs;
        try (PreparedStatement preparedStatement = dbWorker.getConnection().prepareStatement("select Name from cities.city where Name like ?")) {
            preparedStatement.setString(1, lastChar + "%");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                return lastChar;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (index == city.length()) {
            return '(';
        }
        return checkLastChar(city, ++index);
    }
    public boolean findCity(String city) throws SQLException {

        try (PreparedStatement preparedStatement = dbWorker.getConnection().prepareStatement("select Name from cities.city where Name = ?")) {
            preparedStatement.setString(1, city);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println("Город " + city);
                if (cities.contains(city)) {
                    return false;
                } else {
                    cities.add(rs.getString(1));
                    textfield.setText("");
                    textfield.setText("Город " + rs.getString(1));
                    history.setText(history.getText().concat(" ".concat(rs.getString(1))));
                    return true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void newGameButtonPressed() {
        cities.clear();
        textField2.setEditable(true);
        textField2.setText("");
        history.setText("");
        isWin=true;
        textfield.setText("New Game");
        index = 1;
        count = 0;

    }
    public void enterButtonPressed() {
        try {

            if (textField2.getText().length() == 0) {
                return;
            } else {
                while (isWin) {
                    city = textField2.getText();
                    textField2.setText("");
                    count++;
                    try {
                        if (count == 1) {
                            if (findCity(city)) {
                                try {
                                    lastChar = checkLastChar(city, index);
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }

                                if (lastChar == '(') {
                                    textfield.setText("Game over!");
                                    isWin = false;
                                    textField2.setEditable(false);
                                    return;
                                }
                                return;
                            }
                            else {
                                textfield.setText("Game over!");
                                isWin=false;
                                textField2.setEditable(false);
                                return;
                            }
                        }
                        else if ((!city.startsWith(String.valueOf(Character.toUpperCase(lastChar)))) && (!city.startsWith(String.valueOf(Character.toLowerCase(lastChar))))){
                            char c = city.charAt(0);
                            System.out.println(c);
                            System.out.println(Character.toUpperCase(lastChar));
                            count--;
                            continue;
                        }

                        else if (count > 1) {
                            try {
                                if (!findCity(city)) {
                                    textfield.setText("Game over!");
                                    isWin=false;
                                    textField2.setEditable(false);
                                    return;
                                }
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            try {
                                lastChar = checkLastChar(city, index);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            if (lastChar == '(') {
                                textfield.setText("Game over!");
                                isWin=false;
                                textField2.setEditable(false);
                                return;
                            }

                            return;
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }textField2.setText("");
                }
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);
        }
        finally {
            return;
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource()==newGameButton){
                newGameButtonPressed();
            }
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        if (textField2.getText().length() == 0)
            return;
        if (e.getSource() == enterButton) {
            enterButtonPressed();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            enterButtonPressed();
        }
    }
}