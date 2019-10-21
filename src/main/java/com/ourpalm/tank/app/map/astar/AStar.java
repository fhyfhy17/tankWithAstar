package com.ourpalm.tank.app.map.astar;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by g on 2016/6/7.
 */
public class AStar {

	public static final float ParallelRange = 2.0f;

	private BinaryHeap<Node> open;
	private Stack<Node> closed;
	private Stack<Node> path;
	private Grid grid;
	private Node endNode;
	private Node startNode;
	private float straightCost = 1.0f;
	private float diagCost = (float) (Math.sqrt(2));
	private boolean retractable = false;
	private Heuristic heuristic;

	private Comparator<Node> comparator;

	private Lock lock = new ReentrantLock();

	public AStar(Grid grid, boolean retractable) {
		this(Heuristic.MANHATTAN, true);
		this.grid = grid;
	}

	/**
	 * 构造方法
	 *
	 * @param retractable
	 *            以死角点为寻路终点时，是否启用以死角点最近的可行点为路径终点
	 * @param heuristicName
	 *            启发函数，为Heuristic类的枚举常量
	 */
	public AStar(Heuristic heuristic, boolean retractable) {
		this.heuristic = heuristic;
		this.retractable = retractable;
		// node1小于、等于或大于node2，分别返回负整数、零或正整数
		comparator = new Comparator<Node>() {
			@Override
			public int compare(Node node1, Node node2) {
				if (node1.getF() < node2.getF()) {
					return 1;
				} else if (node1.getF() > node2.getF()) {
					return -1;
				}
				return 0;
			}
		};
	}

	/**
	 * 构造方法
	 *
	 * @param heuristicName
	 *            路径代价分析函数
	 */
	public AStar(Heuristic heuristic) {
		this(heuristic, true);
	}

	/**
	 * 构造方法 默认为对角线启发函数
	 */
	public AStar() {
		this(Heuristic.DIAGONAL);
	}

	/**
	 * 启发函数计算
	 *
	 * @param node
	 * @return
	 */
	private float heuristic(Node node) {
		return heuristic.function(node, endNode, straightCost);
	}

	/**
	 * 找寻路径
	 *
	 * @param grid
	 *            网格对象
	 * @return true为找到了路径，调用getPath获取路径节点列表
	 */
	public boolean findPath(Grid grid) {
		try {
			lock.lock();
			this.grid = grid;
			this.open = new BinaryHeap(comparator);
			this.closed = new Stack<>();
			this.startNode.setG(0);
			this.startNode.setH(heuristic(startNode));
			this.startNode.setF(startNode.getG() + startNode.getH());
			boolean result = search();
			return result;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 寻路算法
	 *
	 * @return true寻路成功
	 */
	private boolean search() {
		Node node = startNode;
		while (node != endNode) {
			int startX = Math.max(0, node.getX() - 1);
			int endX = Math.min(grid.getNumCols() - 1, node.getX() + 1);
			int startY = Math.max(0, node.getY() - 1);
			int endY = Math.min(grid.getNumRows() - 1, node.getY() + 1);

			// 围绕node的待测试节点
			for (int i = startX; i <= endX; i++) {
				for (int j = startY; j <= endY; j++) {
					Node test = grid.getNode(i, j);
					if (test == node) {
						continue;
					}
					Node vnode = grid.getNode(node.getX(), test.getY());
					Node hnode = grid.getNode(test.getX(), node.getY());
					if (retractable) {
						// if (test.isWalkable() == false || test.isTankOccupy() || !isDiagonalWalkable(node, test)) {
						if (test.isWalkable() == false) { // TODO
							// 设其代价为超级大的一个值，比大便还大哦~
							test.setCostMultiplier(1000);
						} else {
							test.setCostMultiplier(1);
						}
					} else {
						if (test.isWalkable() == false || test.isTankOccupy() || vnode.isWalkable() == false
								|| vnode.isTankOccupy() || hnode.isWalkable() == false || hnode.isTankOccupy()) {
							continue;
						}
					}

					// 直线成本（三角形，设边为1，其值为边长）
					float cost = straightCost;
					if (!((node.getX() == test.getX()) || (node.getY() == test.getY()))) {
						// 斜线成本（三角形，设边为1，斜边三角函数的值约为2的平方根）
						cost = diagCost;
					}
					float g = node.getG() + cost * test.getCostMultiplier();
					float h = heuristic(test);
					float f = g + h;
					if (isOpen(test) || isClosed(test)) {
						if (test.getF() > f) {
							test.setF(f);
							test.setG(g);
							test.setH(h);
							test.setParent(node);
						}
					} else {
						test.setF(f);
						test.setG(g);
						test.setH(h);
						test.setParent(node);
						open.push(test);
					}
				}
			}
			closed.push(node);
			if (open.getSize() == 0) {
				return false;
			}
			node = open.shift();
		}
		buildPath();
		return true;
	}

	/**
	 * 判断两个节点的对角线路线是否可走
	 *
	 * @return true可走
	 */
	private boolean isDiagonalWalkable(Node node1, Node node2) {
		Node nearByNode1 = grid.getNode(node1.getX(), node2.getY());
		Node nearByNode2 = grid.getNode(node2.getX(), node1.getY());
		return nearByNode1.isWalkable() && !nearByNode1.isTankOccupy() && nearByNode2.isWalkable()
				&& !nearByNode2.isTankOccupy();
	}

	/**
	 * 移除list中从startIndex处开始的len个元素
	 *
	 * @param list
	 *            列表对象
	 * @param startIndex
	 *            开始删除位置
	 * @param len
	 *            删除的个数
	 */
	private void removeListElement(List<Node> list, int startIndex, int len) {
		int index = startIndex;
		for (int j = 0; j < len; j++) {
			list.remove(index++);
		}
	}

	/**
	 * 移除stack中从startIndex处和其后的所有元素
	 *
	 * @param stack
	 *            列表对象
	 * @param startIndex
	 *            移除元素的起始位置
	 */
	private void removeStackElement(Stack<Node> stack, int startIndex) {
		int endIndex = stack.size();
		while (endIndex != startIndex) {
			//stack.pop(); 不能用pop 会开始点的那些点去掉了，而保留了结束点
			stack.remove(0);
			endIndex--;
		}
	}

	/**
	 * 构建路径
	 */
	private void buildPath() {
		path = new Stack<>();
		Node node = endNode;
		path.push(node);
		// 不包含起始节点
		while (node != startNode) {
			node = node.getParent();
			path.push(node);
		}
		if (retractable) {
			// 排除无法移动点
			int len = path.size();
			for (int i = 0; i < len; i++) {
				//System.out.println("1 " + i + " node " + path.get(i).getX() + " " + path.get(i).getY() );
				//System.out.println("path.get(i).isWalkable() " + path.get(i).isWalkable() );
//				if( ignoreNode != null && ignoreNode.containsKey(path.get(i).getX()) && ignoreNode.get(path.get(i).getX()).contains(path.get(i).getY())){
//					continue;
//				}
				if (path.get(i).isWalkable() == false ) {
					// removeListElement(path, i, len - i);
					removeStackElement(path, i);
					break;
				} else if (len == 1 && !isDiagonalWalkable(startNode, endNode)) {
					// 由于之前排除了起始点，所以当路径中只有一个元素时候判断该元素与起始点是否是不可穿越关系，若是，则连最后这个元素也给他弹出来~
					path.remove(0);
//				} else if (i < len - 1 && !isDiagonalWalkable(path.get(i), path.get(i + 1))) {
//					// 判断后续节点间是否存在不可穿越点，若有，则把此点之后的元素全部拿下
//					// removeListElement(path, i + 1, len - i - 1);
//					removeStackElement(path, i + 1);
//					break;
				}
			}
		}
	}

	/**
	 * 判断节点是否在开放列表中
	 *
	 * @param node
	 *            带判断的节点
	 * @return true在开放列表中
	 */
	private boolean isOpen(Node node) {
		return open.toStack().contains(node);
	}

	/**
	 * 判断节点是否在关闭列表中
	 *
	 * @param node
	 *            待判断的节点
	 * @return true在关闭列表中
	 */
	private boolean isClosed(Node node) {
		return closed.contains(node);
	}

	/**
	 * 获取路径
	 *
	 * @return 寻到的路径
	 */
	public Stack<Node> getPath() {
		// path.add(grid.getStart());
		return path;
	}

	/**
	 * 获取已访问过得节点列表
	 *
	 * @return 已访问的节点列表
	 */
	public Stack<Node> getVisited() {
		Stack<Node> stack = new Stack<>();
		stack.addAll(closed);
		stack.addAll(open.toStack());
		return stack;
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}

	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}
}
