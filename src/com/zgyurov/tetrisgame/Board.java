package com.zgyurov.tetrisgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener{

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 22;
    private static final Color[] COLORS = {new Color(0, 0, 0),
                                            new Color(204, 102, 102),
                                            new Color(102, 204, 102),
                                            new Color(102, 102, 204),
                                            new Color(204, 204, 102),
                                            new Color(204, 102, 204),
                                            new Color(102, 204, 204),
                                            new Color(218, 170, 0)};
    private Timer timer;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private boolean isFalling = true;
    private int numRemovedLines = 0, curX = 0, curY = 0;
    private JLabel statusBar;
    private Shape currentPiece;
    private Shape.Tetrominoes[] board;

    public Board(Tetris parent){
        setFocusable(true);
        currentPiece = new Shape();
        timer = new Timer(350, this);
        statusBar = parent.GetStatusBar();
        board = new Shape.Tetrominoes[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        addKeyListener(new MyTetrisAdapter());
    }

    public int squareWidth(){
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    public int squareHeight(){
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    public Shape.Tetrominoes shapeAt(int x, int y){
        return board[y * BOARD_WIDTH + x];
    }

    public void clearBoard(){
        for(int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++){
            board[i] = Shape.Tetrominoes.NoShape;
        }
    }

    private void pieceDropped(){
        for(int i = 0; i < 4; i++){
            int x = curX + currentPiece.getX(i);
            int y = curY - currentPiece.getY(i);
            board[y * BOARD_WIDTH + x] = currentPiece.getShapeType();
        }

        removeFullLines();

        if(isFalling){
            newPiece();
        }
    }

    public void newPiece(){
        currentPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + currentPiece.minY();

        if(!tryMove(currentPiece, curX, curY - 1)){
            currentPiece.setShape(Shape.Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusBar.setText("GAME OVER - Press R to Restart");
        }
    }

    private void oneLineLower(){
        if(!tryMove(currentPiece, curX, curY - 1))
            pieceDropped();
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        if(!isFalling){
            isFalling = true;
            newPiece();
        }
        else{
            oneLineLower();
        }
    }

    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoes shape){
        Color color = COLORS[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        Dimension size = getSize();

        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for(int i = 0; i < BOARD_HEIGHT; i++){
            for(int j = 0; j < BOARD_WIDTH; j++){
                Shape.Tetrominoes shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if(shape != Shape.Tetrominoes.NoShape){
                    drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if(currentPiece.getShapeType() != Shape.Tetrominoes.NoShape){
            for(int i = 0; i < 4; ++i){
                int x = curX + currentPiece.getX(i);
                int y = curY - currentPiece.getY(i);
                drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), currentPiece.getShapeType());
            }
        }
    }

    public void start(){
        if(isPaused)
            return;

        isStarted = true;
        isFalling = true;
        numRemovedLines = 0;
        clearBoard();
        newPiece();
        timer.start();
    }

    public void pause(){
        if(!isStarted)
            return;

        isPaused = !isPaused;

        if(isPaused){
            timer.stop();
            statusBar.setText("GAME PAUSED");
        }
        else{
            timer.start();
            statusBar.setText(String.valueOf(numRemovedLines));
        }

        repaint();
    }

    private boolean tryMove(Shape newPiece, int newX, int newY){
        for(int i = 0; i < 4; ++i){
            int x = newX + newPiece.getX(i);
            int y = newY - newPiece.getY(i);

            if(x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
                return false;

            if(shapeAt(x, y) != Shape.Tetrominoes.NoShape)
                return false;
        }

        currentPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();

        return true;
    }

    private void removeFullLines(){
        int numFullLines = 0;

        for(int i = BOARD_HEIGHT - 1; i >= 0; --i){
            boolean lineIsFull = true;

            for(int j = 0; j < BOARD_WIDTH; ++j){
                if(shapeAt(j, i) == Shape.Tetrominoes.NoShape){
                    lineIsFull = false;
                    break;
                }
            }

            if(lineIsFull){
                ++numFullLines;

                for(int k = i; k < BOARD_HEIGHT - 1; ++k){
                    for(int j = 0; j < BOARD_WIDTH; ++j){
                        board[k * BOARD_WIDTH + j] = shapeAt(j, k + 1);
                    }
                }
            }

            if(numFullLines > 0){
                numRemovedLines += numFullLines;
                statusBar.setText(String.valueOf(numRemovedLines));
                isFalling = false;
                currentPiece.setShape(Shape.Tetrominoes.NoShape);
                repaint();
                numFullLines = 0;
            }
        }
    }

    private void dropDown(){
        int newY = curY;

        while(newY > 0){
            if(!tryMove(currentPiece, curX, newY -  1))
                break;

            --newY;
        }

        pieceDropped();
    }

    class MyTetrisAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent ke) {
            if(!isStarted || currentPiece.getShapeType() == Shape.Tetrominoes.NoShape){
                return;
            }

            int keyCode = ke.getKeyCode();

            if(keyCode == 'p' || keyCode == 'P')
                    pause();

            if(isPaused)
                return;

            switch (keyCode){
                case KeyEvent.VK_LEFT:
                    tryMove(currentPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(currentPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(currentPiece.rotateRight(), curX, curY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(currentPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case 'd':
                    oneLineLower();
                    break;
                case 'D':
                    oneLineLower();
                    break;
            }
        }
    }


}
