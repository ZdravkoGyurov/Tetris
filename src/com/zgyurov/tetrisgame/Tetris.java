package com.zgyurov.tetrisgame;

import javax.swing.*;
import java.awt.*;

public class Tetris extends JFrame{

    private JLabel statusBar;

    public Tetris(){
        statusBar = new JLabel("0");
        statusBar.setOpaque(true);
        statusBar.setBackground(Color.white);
        add(statusBar, BorderLayout.NORTH);
        Board board = new Board(this);
        add(board);

        board.start();

        board.newPiece();
        board.repaint();

        setSize(400, 800);
        setTitle("TETRIS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JLabel GetStatusBar() {
        return statusBar;
    }

    public static void main(String[] args) {
        Tetris myTetris = new Tetris();
        myTetris.setLocationRelativeTo(null);
        myTetris.setVisible(true);
    }
}
