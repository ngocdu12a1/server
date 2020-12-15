package util.troopUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by CPU60126_LOCAL on 2020-08-04.
 */
public class Heap {
    ArrayList<Node> heap = new ArrayList<Node>();

    private Node getHeap(int i) {
        if (i >= 0 && i < heap.size()) {
            return heap.get(i);
        } else {
            return null;
        }
    }

    public Node getMin() {
        return getHeap(0);
    }

    public void insert(Node node) {
        heap.add(node);
        upHeap();
    }

    public void upHeap() {
        int heapLength = heap.size();
        if (heapLength > 0) {
            //travel from bottom to parent
            int i = heapLength - 1;
            while(i>0) {
                int parentIndex;
                if (i % 2 == 1) {
                    parentIndex = i/2;
                }
                else {
                    parentIndex = i/2 - 1;
                }
                if (getHeap(parentIndex).f < getHeap(i).f)
                    break;
                swap(i, parentIndex);
                i = parentIndex;
            }
        }
    }

    public Node removeMin() {
        Node result = getMin();

        int heapLength = heap.size();
        if (heapLength > 1) {
            heap.set(0, getHeap(heapLength - 1));
            heap.remove(heapLength - 1);
            downHeap(0);
        }
        else if (heapLength == 1) {
            heap.remove(0);
        }
        else return null;

        return result;
    }

    public void downHeap(int n) {
        int i = n;
        int leftChild = i * 2 + 1;
        int rightChild = i * 2 + 2;

        while (getHeap(leftChild) != null &&
                getHeap(rightChild) != null &&
                (getHeap(i).f > getHeap(leftChild).f ||
                        getHeap(i).f > getHeap(rightChild).f)) {
            if (getHeap(leftChild).f < getHeap(rightChild).f) {
                swap(i, leftChild);
                i = leftChild;
            }
            else {
                swap(i, rightChild);
                i = rightChild;
            }

            leftChild = i * 2 + 1;
            rightChild = i * 2 + 2;
        }

        if (getHeap(rightChild) == null &&
                getHeap(leftChild) != null &&
                getHeap(leftChild).f < getHeap(i).f) {
            swap(i, leftChild);
        }
    }

    public void swap(int index1, int index2) {
        Collections.swap(heap, index1, index2);
    }

    public void clear() {
        heap.clear();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void reScore(Node node) {
        for (int i = 0; i < heap.size(); i++) {
            if (node.x == getHeap(i).x && node.y == getHeap(i).y) {
                heap.set(i, node);
                downHeap(i);
                break;
            }
        }
    }
}
