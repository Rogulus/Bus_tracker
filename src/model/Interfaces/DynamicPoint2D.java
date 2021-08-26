package model.Interfaces;

public interface DynamicPoint2D extends Point2D{
    public void changeX(int x);
    public void changeY(int y);
    public void move(int x, int y);
}