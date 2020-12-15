package util.troopUtils;

import util.mapUtils.MapUtils;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by CPU60126_LOCAL on 2020-08-04.
 */
public class Astar {
    Heap openList;
    Node[][] nodeMap;

    int mapGridW;
    int mapGridH;

    public Astar() {
        this.openList = new Heap();
    }

    public ArrayList<Node> search(int[][] mapGrid, int troopX, int troopY, int destinationX, int destinationY, int id) {
        mapGridW = mapGrid.length;
        mapGridH = mapGrid[0].length;

        nodeMap = new Node[mapGridW][mapGridH];
        for (int i=0; i<mapGridW; i++)
            for (int j=0; j<mapGridH; j++) {
                Node node = new Node(i, j, mapGrid[i][j]);
                nodeMap[i][j] = node;
            }

        openList.clear();

        Node start = nodeMap[troopX][troopY];
        Node end = nodeMap[destinationX][destinationY];

        int startH = heuristic(start, end, id);

        start.setValues(0, startH);
        openList.insert(start);

        while (!openList.isEmpty()) {
            Node currentNode = openList.removeMin();

            if (currentNode == end) {
                return tracePath(currentNode);
            }

            currentNode.closed = true;

            ArrayList<Node> neighbors = getNeighbors(currentNode);

            for (int i=0; i<neighbors.size(); i++) {
                Node neighbor = neighbors.get(i);

                if (neighbor.closed)
                    continue;

                if (neighbor.cantMoveTo()) {
                    if (neighbor.id != end.id)
                        continue;
                }

                int gScore = currentNode.g + neighbor.weight;
                boolean beenVisited = neighbor.visited;

                if (!beenVisited || gScore < neighbor.g) {
                    neighbor.visited = true;
                    neighbor.parent = currentNode;
                    int neighborH = neighbor.h != 0 ? neighbor.h : heuristic(neighbor, end, id);
                    neighbor.setValues(gScore, neighborH);
                }

                if (!beenVisited) {
                    openList.insert(neighbor);
                }
                else {
                    openList.reScore(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }

    public int heuristic(Node node0, Node node, int id) {
        int numberOfHeuristic = 3;
        if (id % numberOfHeuristic == 0) {
            //manhattan
            if (node != null) {
                Point p1 = new Point(node.x, node.y);
                Point p2 = new Point(node0.x, node0.y);
                return MapUtils.getInstance().getManhattanDistance(p1, p2);
            }
            else return 0;
        }
        else if (id % numberOfHeuristic == 1) {
            //euclidean
            if (node != null) {
                int D = 1;
                Point p1 = new Point(node0.x, node0.y);
                Point p2 = new Point(node.x, node.y);
                double res = D * MapUtils.getInstance().getEuclideanDistance(p1, p2);
                return (int) Math.floor(res);
            }
            else return 0;
        }
        else {
            //diagonal
            if (node != null) {
                Point p1 = new Point(node0.x, node0.y);
                Point p2 = new Point(node.x, node.y);
                double res = MapUtils.getInstance().getDiagonalDistance(p1, p2);
                return (int) Math.floor(res);
            }
            else return 0;
        }
    }

    public ArrayList<Node> tracePath(Node node) {
        Node currentNode = node;
        ArrayList<Node> result = new ArrayList<>();
        while (currentNode.parent != null) {
            result.add(0, currentNode);
            currentNode = currentNode.parent;
        }
        return  result;
    }

    boolean checkBoundary(int x, int y) {
        if (x<0 || x>=mapGridW || y<0 || y>=mapGridH)
            return false;
        return true;
    }

    public ArrayList<Node> getNeighbors(Node node) {
        ArrayList<Node> result = new ArrayList<>();

        int[] deltaX = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
        int[] deltaY = new int[] {1, 1, 0, -1, -1, -1, 0, 1};

        for (int i=0; i<deltaX.length; i++) {
            int nextX = node.x + deltaX[i];
            int nextY = node.y + deltaY[i];

            if (checkBoundary(nextX, nextY)) {
                result.add(nodeMap[nextX][nextY]);
            }
        }

        return result;
    }
}
