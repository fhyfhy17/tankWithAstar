package com.ourpalm.tank.app.map.ai.node;

public enum Operator {

	EQUAL {
		@Override
		public boolean eval(int a, int b) {
			return a == b;
		}
	},

	GREATER {
		@Override
		public boolean eval(int a, int b) {
			return a > b;
		}
	},

	GREATER_EQUAL {
		@Override
		public boolean eval(int a, int b) {
			return a >= b;
		}
	},

	LESS {
		@Override
		public boolean eval(int a, int b) {
			return a < b;
		}
	},

	LESS_EQUAL {
		@Override
		public boolean eval(int a, int b) {
			return a <= b;
		}
	};

	public abstract boolean eval(int a, int b);
}
