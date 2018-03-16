package com.zgyurov.tetrisgame;

import java.util.Random;

public class Shape {

    enum Tetrominoes {
        NoShape(new int[][] {{0, 0}, {0, 0}, {0, 0}, {0, 0}}),
        IShape(new int[][] {{0, -1}, {0, 0}, {0, 1}, {0, 2}}),
        OShape(new int[][] {{0, 0}, {1, 0}, {1, 1}, {0, 1}}),
        TShape(new int[][] {{0, -1}, {0, 0}, {-1, 0}, {1, 0}}),
        JShape(new int[][] {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}),
        LShape(new int[][] {{1, -1}, {0, -1}, {0, 0}, {0, 1}}),
        SShape(new int[][] {{0, -1}, {0, 0}, {1, 0}, {1, 1}}),
        ZShape(new int[][] {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}});

        public int[][] coordinates;

        private Tetrominoes(int[]... _coordinates) {
            this.coordinates = _coordinates;
        }
    }

    private Tetrominoes shapeType;
    private int[][] shapeCoordinates;

    public Shape() {
        shapeCoordinates = new int[4][2];
        setShape(Tetrominoes.NoShape);
    }

    public void setShape(Tetrominoes _shape){
        shapeType = _shape;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 2; j++){
                shapeCoordinates[i][j] = _shape.coordinates[i][j];
            }
        }
    }

    public Tetrominoes getShapeType() {
        return shapeType;
    }

    public void setX(int index, int x){
        shapeCoordinates[index][0] = x;
    }

    public void setY(int index, int y){
        shapeCoordinates[index][1] = y;
    }

    public int getX(int index){
        return shapeCoordinates[index][0];
    }

    public int getY(int index){
        return shapeCoordinates[index][1];
    }

    public void setRandomShape(){
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
    }

    public int minX(){
        int m = shapeCoordinates[0][0];

        for(int i = 0; i < 4; i++){
            m = Math.min(m, shapeCoordinates[i][0]);
        }

        return m;
    }

    public int minY(){
        int m = shapeCoordinates[0][0];

        for(int i = 0; i < 4; i++){
            m = Math.min(m, shapeCoordinates[i][1]);
        }

        return m;
    }

    public Shape rotateLeft(){
        if(shapeType == Tetrominoes.OShape)
            return this;

        Shape result = new Shape();
        result.shapeType = shapeType;

        for(int i = 0; i < 4; i++){
            result.setX(i, getY(i));
            result.setY(i, -getX(i));
        }

        return result;
    }

    public Shape rotateRight(){
        if(shapeType == Tetrominoes.OShape)
            return this;

        Shape result = new Shape();
        result.shapeType = shapeType;

        for(int i = 0; i < 4; i++){
            result.setX(i, -getY(i));
            result.setY(i, getX(i));
        }

        return result;
    }
}
