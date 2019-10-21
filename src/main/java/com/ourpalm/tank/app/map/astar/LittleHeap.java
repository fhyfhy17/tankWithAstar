package  com.ourpalm.tank.app.map.astar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 2016/6/7.
 */
public class LittleHeap {

    private List<Node>  heap = new ArrayList<>();


    //构造过程
    private void adjust(int pIndex, int len) {
        Node tmp = null;
        try{
            tmp = heap.get(pIndex);
        }catch (Exception e) {
            e.printStackTrace();
        }
        int leftChild = 2 * pIndex + 1;

        while(leftChild < len) {
            if(leftChild + 1 < len && heap.get(leftChild).getF() > heap.get(leftChild + 1).getF())
                leftChild++;

            if(heap.get(pIndex).getF() > heap.get(leftChild).getF()) {
                heap.set(pIndex, heap.get(leftChild));
                pIndex = leftChild;
                leftChild = 2 * pIndex + 1;
            } else {
                break;
            }
            heap.set(pIndex, tmp);
        }
    }

    //构造小顶堆
    private void swap() {
        List<Node> _heap = heap;
        int len = _heap.size();
        if(len <= 0)
            return;

        for (int i = (len - 1) / 2; i >= 0; --i) {
            adjust(i, len);
        }
    }


    public void put(Node node) {
        heap.add(node);
    }

    /**
     * 取出堆顶F值最小元素
     * @return
     */
    public Node poll() {
        swap();
        return heap.remove(0);
    }


    public int size() {
        return heap.size();
    }

    public boolean contains(Node node) {
        return heap.contains(node);
    }


}
