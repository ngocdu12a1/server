package util.troopUtils;

/**
 * Created by CPU60126_LOCAL on 2020-08-04.
 */
public class Node {
    public int x;
    public int y;
    int id;
    //for walls
    int weight = 1;
    int f = 0;
    int g = 999999999;
    int h = 0;
    Node parent = null;
    boolean visited = false;
    boolean closed = false;

    public Node(int x, int y, int weight) {
        this.x = x;
        this.y = y;
        this.id = weight;
    }

    public void setValues(int g, int h) {
        this.h = h;
        this.g = g;
        this.f = h + g;
    }

    public boolean cantMoveTo() {
        return this.id != -1;
    }
}
