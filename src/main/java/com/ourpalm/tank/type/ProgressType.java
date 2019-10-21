package com.ourpalm.tank.type;

public enum ProgressType {
	add {//累加 
		@Override
		public int getProgress(int oldValue, int value) {
			return oldValue + value;
		}
	},	
	replace {//替换
		@Override
		public int getProgress(int oldValue, int value) {
			return value;
		}
	},
	max {//保留最大值
		@Override
		public int getProgress(int oldValue, int value) {
			return Math.max(oldValue, value);
		}
	};
	
	public int getProgress(int oldValue, int value){
		return oldValue + value;
	}
}
