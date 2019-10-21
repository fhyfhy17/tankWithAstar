package com.ourpalm.tank.app.map.astar;

/**
 * Created by g on 2016/6/7.
 */
public enum Heuristic {

	// 曼哈顿启发函数
	MANHATTAN {
		@Override
		public float function(Node node, final Node endNode, final float straightCost) {
			return Math.abs(node.getX() - endNode.getX()) * straightCost
					+ Math.abs(node.getY() + endNode.getY()) * straightCost;
		}
	},

	// 欧几里得几何启发函数
	EUCLIDIAN {
		@Override
		public float function(Node node, final Node endNode, final float straightCost) {
			float dx = node.getX() - endNode.getX();
			float dy = node.getY() - endNode.getY();
			return (float) (Math.sqrt(dx * dx + dy * dy) * straightCost);
		}
	},

	// 对角启发函数
	DIAGONAL {
		@Override
		public float function(Node node, final Node endNode, final float straightCost) {
			float dx = Math.abs(node.getX() - endNode.getX());
			float dy = Math.abs(node.getY() - endNode.getY());
			float diag = Math.min(dx, dy);
			float straight = dx + dy;
			return (float) (Math.sqrt(2) * diag + straightCost * (straight - 2 * diag));
		}
	};

	public abstract float function(Node node, final Node endNode, final float straightCost);
}
