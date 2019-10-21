package  com.ourpalm.tank.app.map.astar;

/**
 * Created by g on 2016/6/7.
 */
public class Node {
    private final int index;          //结点索引，先列后行
    private final int x, y;     //结点坐标
    private float f;    		// g + h 的合值，通过合值判断最小路径
    private float g;    		// 出发点到终点的距离
    private float h;    		// 当前结点到终点的距离--估值，3种计算估值算法
    
    private float px;				//中心位置世界坐标X坐标
    private float py;				//中心位置世界坐标高度
    private float pz;				//中心位置世界坐标Z坐标（二维坐标中的Y轴）

    private Node parent;                	//中间结点为相临8个结点的父亲
    private boolean walkable = true;    	//是否为障碍物
    private boolean tankOccupy = false;		//是否有坦克占据
    private float costMultiplier = 1.0f;    //代价因子（代表g的代价因子）

    public Node(int index, int x, int y) {
    	this.index = index;
        this.x = x;
        this.y = y;
    }
    public int getIndex() {
        return index;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public float getCostMultiplier() {
        return costMultiplier;
    }

    /** 判断是否平行 平行返回true */
    public boolean isParallel(float height) {
    	//自己的高度高于节点高度是允许的
    	if(height >= this.py){
    		return true;
    	}
        return Math.abs(this.py - height) < AStar.ParallelRange;
    }

    public void setCostMultiplier(float costMultiplier) {
        this.costMultiplier = costMultiplier;
    }
    
	public float getPx() {
		return px;
	}
	public void setPx(float px) {
		this.px = px;
	}
	public float getPy() {
		return py;
	}
	public void setPy(float py) {
		this.py = py;
	}
	public float getPz() {
		return pz;
	}
	public void setPz(float pz) {
		this.pz = pz;
	}
	public boolean isTankOccupy() {
		return tankOccupy;
	}
	public void setTankOccupy(boolean tankOccupy) {
		this.tankOccupy = tankOccupy;
	}
}
