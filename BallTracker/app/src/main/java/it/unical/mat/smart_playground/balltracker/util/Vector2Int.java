package it.unical.mat.smart_playground.balltracker.util;

/**
 * Created by utente on 28/10/2020.
 */
public class Vector2Int extends Vector2<Integer>
{
    public static final Vector2Int X = new Vector2Int(1, 0);
    public static final Vector2Int Y = new Vector2Int(0, 1);

    public Vector2Int( final int x, final int y )
    {
        super(x, y);
    }

    public Vector2Int not()
    {
        return new Vector2Int(-x, -y);
    }

    public Vector2Int sum( final Vector2Int other )
    {
        return new Vector2Int(this.x + other.x, this.y + other.y);
    }

    public boolean isZero()
    {
        return (x == 0 && y == 0);
    }

    public static Vector2Int fromPoints( final Vector2Int p1, final Vector2Int p2 )
    {
        return p1.not().sum(p2);
    }

    public static Vector2Int fromDegrees( final short degrees, final double scalar )
    {
        return Vector2Int.fromRadians( Math.toRadians(degrees), scalar );
    }

    public static Vector2Int fromRadians( final double radians, final double scalar )
    {
        return new Vector2Int( (int)(Math.cos(radians) * scalar), (int)(Math.sin(radians) * scalar) );
    }

    public static Vector2Int avg( final Vector2Int v1, final Vector2Int v2 )
    {
        return new Vector2Int((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);
    }
}
