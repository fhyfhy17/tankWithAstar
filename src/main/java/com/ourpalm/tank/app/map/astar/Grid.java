
package com.ourpalm.tank.app.map.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;

public class Grid implements Cloneable {

	private static final Logger logger = LogCore.runtime;

	private Map<Integer, List<Node>> nodeMap;

	private int cols;
	private int rows;
	private float deviationX; // X 轴坐标偏移量（网格信息只是整张地图的一部分可活动范围）
	private float deviationZ; // Z 轴坐标偏移量（二维世界中的 Y 轴）
	private int cellSize;     // 格子大小

	// public Grid(int cols, int rows) {
	// this.cols = cols;
	// this.rows = rows;
	//
	// this.nodeMap = new HashMap<>();
	//
	// for (int x = 0; x < cols; x++) {
	// List<Node> yNodes = new ArrayList<>();
	// for (int y = 0; y < rows; y++) {
	// Node node = new Node(x, y);
	// node.setIndex(y + x * rows);
	// yNodes.add(node);
	// }
	// nodeMap.put(x, yNodes);
	// }
	// }

	/**
	 * 通过格子索引坐标寻找格子对象
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Node getNode(int x, int y) {
		if (x < 0 || y < 0) {
			return null;
		}
		List<Node> yNodes = nodeMap.get(x);
		if (yNodes == null || y >= yNodes.size()) {
			return null;
		}
		return yNodes.get(y);
	}

	/**
	 * 根据世界坐标得到所在的节点
	 *
	 * @param xPos
	 *            世界 X 坐标
	 * @param yPos
	 *            世界 Z 坐标
	 * @return
	 */
	public Node getNodeByWoldPoint(float xPos, float zPos) {
		int x = worldToGrildPosX(xPos);
		int y = worldToGrildPosY(zPos);

		return this.getNode(x, y);
	}

	/**
	 * 获取附近的四个节点
	 *
	 * @param x
	 *            世界坐标
	 * @param y
	 *            世界坐标
	 * @return
	 */
	public List<Node> getNearByNodes(float xPos, float yPos) {
		int intX = worldToGrildPosX(xPos);
		int intY = worldToGrildPosY(yPos);

		return getNearByNodes(intX, intY);
	}

	/**
	 * 获取附近的四个节点
	 *
	 * @param x
	 *            网格坐标
	 * @param y
	 *            网格坐标
	 * @return
	 */
	public List<Node> getNearByNodes(int intX, int intY) {
		List<Node> result = new ArrayList<>();

		this.buildNodes(getNode(intX, intY), result);
		this.buildNodes(getNode(intX + 1, intY), result);
		this.buildNodes(getNode(intX, intY + 1), result);
		this.buildNodes(getNode(intX, intY - 1), result);
		this.buildNodes(getNode(intX - 1, intY), result);

		return result;
	}

	private List<Node> buildNodes(Node node, List<Node> returnList) {
		if (node == null) {
			return returnList;
		}
		returnList.add(node);
		return returnList;
	}

	/**
	 * 得到一个点下的所有节点
	 *
	 * @param xPos
	 *            点的横向位置
	 * @param yPos
	 *            点的纵向位置
	 * @param exception
	 *            例外格，若其值不为空，则在得到一个点下的所有节点后会排除这些例外格
	 * @return 共享此点的所有节点
	 */
	private List<Node> getNodesByPoint(float xPos, float yPos, List<Node> exceptList) {
		List<Node> result = new ArrayList<>();

		boolean xIsInt = xPos % 1 == 0;
		boolean yIsInt = yPos % 1 == 0;

		if (xIsInt && yIsInt) {
			// 点由四节点共享情况
			int intX = (int) xPos;
			int intY = (int) yPos;
			result.add(getNode(intX - 1, intY - 1));
			result.add(getNode(intX, intY - 1));
			result.add(getNode(intX - 1, intY));
			result.add(getNode(intX, intY));
		} else if (xIsInt && !yIsInt) {
			// 点由2节点共享情况
			// 点落在两节点左右临边上
			int intX = (int) xPos;
			int intY = (int) yPos;
			result.add(getNode(intX - 1, intY));
			result.add(getNode(intX, intY));
		} else if (!xIsInt && yIsInt) {
			// 点落在两节点上下临边上
			int intX = (int) xPos;
			int intY = (int) yPos;
			result.add(getNode(intX, intY - 1));
			result.add(getNode(intX, intY));
		} else {
			// 点由一节点独享情况
			result.add(getNode((int) xPos, (int) yPos));
		}
		// 在返回结果前检查结果中是否包含例外点，若包含则排除掉
		if (exceptList != null && exceptList.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				if (exceptList.contains(result.get(i))) {
					result.remove(i);
					i--;
				}
			}
		}
		return result;
	}

	private List<Node> getNodesUnderPoint(float x, float y) {
		return getNodesByPoint(x, y, null);
	}

	/**
	 * 世界坐标转换为格子坐标
	 */
	private int worldToGrildPosX(float worldposX) {
		worldposX -= deviationX;
		if (worldposX < 0)
			worldposX = 0;
		return (int) (worldposX / cellSize);
	}

	/**
	 * 世界坐标Y（三维Z）转换为格子坐标
	 */
	private int worldToGrildPosY(float worldposY) {
		worldposY -= deviationZ;
		if (worldposY < 0)
			worldposY = 0;
		return (int) (worldposY / cellSize);
	}

	/**
	 * 判断两点之间是否有障碍物
	 *
	 * @param starXPos
	 *            世界坐标
	 * @param starYPos
	 * @param endXPos
	 * @param endYPos
	 * @return true为有阻挡不可行走
	 */
	public boolean hadBarrier(float starXPos, float starYPos, float endXPos, float endYPos) {

		int startX = this.worldToGrildPosX(starXPos);
		int startY = this.worldToGrildPosY(starYPos);
		int endX = this.worldToGrildPosX(endXPos);
		int endY = this.worldToGrildPosY(endYPos);

		// 如果起点终点是同一个点那傻子都知道它们间是没有障碍物的
		if (startX == endX && startY == endY) {
			return false;
		}

		// 两节点中心位置
		Point point1 = new Point((float) (startX + 0.5), (float) (startY + 0.5));
		Point point2 = new Point((float) (endX + 0.5), (float) (endY + 0.5));

		// 起始点高度
		final Node startNode = getNode(startX, startY);
		final Node endNode = getNode(endX, endY);

		// float startHeight = getNode(startX, startY).getPy();
		// float endHeight = getNode(endX, endY).getPy();

		// 根据起点终点间横纵向距离的大小来判断遍历方向
		float distX = Math.abs(endX - startX);
		float distY = Math.abs(endY - startY);
		// 遍历方向，为true则为横向遍历，否则为纵向遍历
		boolean loopDirection = distX > distY;
		// 起始点与终点的连线方程
		LineFunction lineFunction;
		// 循环递增量
		float i;
		// 循环起始值
		float loopStart;
		// 循环终结值
		float loopEnd;
		// 起终点连线所经过的节点
		List<Node> passedNodeList;

		final float selfHeightOffset = 2f; // 自身高度偏移量
		final float blockTheshold = 0f; // 阻挡距离阈值，越高 AI 越容易在坡度地形击中玩家

		// 为了运算方便，以下运算全部假设格子尺寸为1，格子坐标就等于它们的行、列号
		if (loopDirection) {
			lineFunction = new LineFunction(point1, point2, 0);
			loopStart = Math.min(startX, endX);
			loopEnd = Math.max(startX, endX);

			// 开始横向遍历起点与终点间的节点看是否存在障碍(不可移动点)
			for (i = loopStart; i <= loopEnd; i++) {
				// 由于线段方程是根据终起点中心点连线算出的，所以对于起始点来说需要根据其中心点w
				// 位置来算，而对于其他点则根据左上角来算
				if (i == loopStart) {
					i += .5;
				}
				// 根据x得到直线上的y值
				float yPos = lineFunction.function(i);

				// 检查经过的节点是否有障碍物，若有则返回true
				passedNodeList = getNodesUnderPoint(i, yPos);

				// 两点式
				// y - y1 x - x1
				// ------ = ------
				// y2 - y1 x2 - x1
				//
				// |
				// v
				//
				// Ax + By + C = 0

				final float A = (float) ((endNode.getPy() - startNode.getPy()) * 1.0
						/ (endNode.getPx() - startNode.getPx()));
				final float B = -1;
				final float C = endNode.getPy() + selfHeightOffset - A * endNode.getPx();

				for (Node passedNode : passedNodeList) {
					if (passedNode.isWalkable() == false) {
						return true;
					}
					// (x0, y0) 表示地图实际高度图坐标
					float x0 = passedNode.getPx();
					float y0 = passedNode.getPy();
					// y1 表示弹道直线上的坐标
					float y1 = (-A * x0 - C) / B;
					// 点到直线距离
					float H = (float) Math.abs((A * x0 + B * y0 + C) / Math.sqrt(A * A + B * B));

					// y0 > y1 表示有阻挡，阈值越大 AI 在坡度地形越容易击中玩家
					// 阻挡距离大于阈值，判定阻挡
					if (y0 - y1 > blockTheshold && H > blockTheshold) {
						return true;
					}
				}
				// for (Node passedNode : passedNodeList) {
				// if (passedNode.isWalkable() == false) {
				// return true;
				// }
				// // 判断高度
				// if (!passedNode.isParallel(startHeight)) {
				// return true;
				// }
				// }
				if (i == loopStart + .5) {
					i -= .5;
				}
			}
		} else {
			lineFunction = new LineFunction(point1, point2, 1);
			loopStart = Math.min(startY, endY);
			loopEnd = Math.max(startY, endY);

			// 开始纵向遍历起点与终点间的节点看是否存在障碍(不可移动点)
			for (i = loopStart; i <= loopEnd; i++) {
				if (i == loopStart) {
					i += .5;
				}
				// 根据y得到直线上的x值
				float xPos = lineFunction.function(i);
				passedNodeList = getNodesUnderPoint(xPos, i);

				final float A = (float) ((endNode.getPy() - startNode.getPy()) * 1.0
						/ (endNode.getPx() - startNode.getPx()));
				final float B = -1;
				final float C = endNode.getPy() + selfHeightOffset - A * endNode.getPx();

				for (Node passedNode : passedNodeList) {
					if (passedNode.isWalkable() == false) {
						return true;
					}
					float x0 = passedNode.getPx();
					float y0 = passedNode.getPy();
					float y1 = (-A * x0 - C) / B;
					// 点到直线距离
					float H = (float) Math.abs((A * x0 + B * y0 + C) / Math.sqrt(A * A + B * B));

					// y0 > y1 表示有阻挡，阈值越大 AI 在坡度地形越容易击中玩家
					// 阻挡距离大于阈值，判定阻挡
					if (y0 - y1 > blockTheshold && H > blockTheshold) {
						return true;
					}
				}
				// for (Node passedNode : passedNodeList) {
				// if (passedNode.isWalkable() == false) {
				// return true;
				// }
				// // 判断高度
				// if (!passedNode.isParallel(startHeight)) {
				// return true;
				// }
				// }
				if (i == loopStart + .5) {
					i -= .5;
				}
			}
		}
		return false;

		// //如果起点终点是同一个点那傻子都知道它们间是没有障碍物的
		// if(startX == endX && startY == endY)
		// return false;
		//
		// //起始点与终点的连线
		// StraightLine line = new StraightLine(startX, startY, endX, endY);
		//
		// Point loopP = line.getLoopPoint();
		// float loopStart = loopP.getX();
		// float loopEnd = loopP.getY();
		// float startHeight = getNode(startX, startY).getPy();
		//
		// Node starNode = this.getNode(startX, startY);
		// Node endNode = this.getNode(endX, endY);
		// List<Node> exceptList = new ArrayList<>();
		// exceptList.add(starNode);
		// exceptList.add(endNode);
		//
		// //为了运算方便，以下运算全部假设格子尺寸为1，格子坐标就等于它们的行、列号
		// for(float i = loopStart; i <= loopEnd; i++) {
		// //由于线段方程是根据终起点中心点连线算出的，所以对于起始点来说需要根据其中心点位置来算，而对于其他点则根据左上角来算
		// if(i == loopStart)
		// i += .5;
		//
		// Point lineP = line.getLinePoint(i);
		//// List<Node> nodeList = getAllNodeByPoint(lineP.getX(),
		// lineP.getY());
		// List<Node> nodeList = getNodesByPoint(lineP.getX(), lineP.getY(),
		// exceptList);
		// for(Node node: nodeList) {
		// if (!node.isWalkable()) {
		// logger.debug("不可通行 {},{}", node.getX(), node.getY());
		// return true;
		// }
		// //判断高度
		// if (!node.isParallel(startHeight)) {
		// logger.debug("height barrier Pos: " + node.getX() + " y : " +
		// node.getY());
		// return true;
		// }
		// }
		//
		// if (i == loopStart + .5) {
		// i -= .5;
		// }
		// }
		//
		// return false;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setDeviationX(float deviationX) {
		this.deviationX = deviationX;
	}

	public void setDeviationZ(float deviationZ) {
		this.deviationZ = deviationZ;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	@Override
	public Object clone() {
		try {
			Grid grid = (Grid) super.clone();
			grid.setNodeMap(new HashMap<>(nodeMap));
			return grid;
		} catch (Exception e) {
			logger.error("深度复制对象发送异常...", e);
		}
		return null;
	}

	public void setNodeMap(Map<Integer, List<Node>> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public int getNumCols() {
		return this.cols;
	}

	public int getNumRows() {
		return this.rows;
	}

	/**
	 * 根据两坐标点，获取n1点到n2的方向
	 *
	 * @param n1
	 * @param n2
	 * @return
	 */
	public NodeDirection getDirection(Node start, Node end) {
		if (start == null || end == null)
			return NodeDirection.None;

		int x = end.getX() - start.getX(), y = end.getY() - start.getY();
		double angle = Math.atan2(y, x) * (-180) / Math.PI;
		if (angle < 0) {
			angle = angle + 360;
		}

		return NodeDirection.getDirection(angle);
	}

	public void showTankOccupyNodes(){
		int count = 0;
		String str = "";
		for(Iterator<Map.Entry<Integer,List<Node>>> initIt = this.nodeMap.entrySet().iterator(); initIt.hasNext();){
			Map.Entry<Integer,List<Node>> entry = initIt.next();
			for( Node node : entry.getValue() ){
				if( node.isTankOccupy() ){
					count ++;
					str += "["+node.getX()+","+node.getY()+"] ";
				}
			}
		}
		logger.debug("不可通行 count {}, {}", count, str);

	}

	public Node getNodeWithDirection(Node selfNode, float x, float y) {
		NodeDirection dir = NodeDirection.None;
		if (x == 0) {
			dir = y > 0 ? NodeDirection.Top : NodeDirection.Bottom;
		} else {
			double angle = Math.atan2(y, x) * (-180) / Math.PI;
			if (angle < 0) {
				angle = angle + 360;
			}
			dir = NodeDirection.getDirection(angle);
		}
		return dir.getNode(this, selfNode);
	}
}
