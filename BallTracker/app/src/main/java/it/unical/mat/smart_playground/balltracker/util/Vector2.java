package it.unical.mat.smart_playground.balltracker.util;

/**
 * Created by utente on 06/10/2020.
 */
public class Vector2<T>
{
    private T x, y;

    public Vector2( final T x, final T y )
    {
        this.x = x;
        this.y = y;
    }

    public T getX()
    {
        return x;
    }

    public void setX(T x)
    {
        this.x = x;
    }

    public T getY()
    {
        return y;
    }

    public void setY(T y)
    {
        this.y = y;
    }

    public void set( final Vector2<T> other )
    {
        this.x = other.x;
        this.y = other.y;
    }
}
