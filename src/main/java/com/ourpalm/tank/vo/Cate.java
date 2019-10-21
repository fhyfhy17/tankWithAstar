package com.ourpalm.tank.vo;

public enum Cate {

	LT(1) {
		@Override
		public Restraint eval(Cate cate) {
			return cate == Cate.TD
					? Restraint.SUPERIOR
					: cate == Cate.MT
						? Restraint.INFERIOR
						: Restraint.EQUIPOLLENCE;
		}
	},
	
	MT(2) {
		@Override
		public Restraint eval(Cate cate) {
			return cate == Cate.LT
					? Restraint.SUPERIOR
					: cate == Cate.HT
						? Restraint.INFERIOR
						: Restraint.EQUIPOLLENCE;
		}
	},
	
	HT(3) {
		@Override
		public Restraint eval(Cate cate) {
			return cate == Cate.MT
					? Restraint.SUPERIOR
					: cate == Cate.TD
						? Restraint.INFERIOR
						: Restraint.EQUIPOLLENCE;
		}
	},
	
	TD(4) {
		@Override
		public Restraint eval(Cate cate) {
			return cate == Cate.HT
					? Restraint.SUPERIOR
					: cate == Cate.LT
						? Restraint.INFERIOR
						: Restraint.EQUIPOLLENCE;
		}
	};
	
	int type;
	
	Cate(int type) {
		this.type = type;
	}
	
	public abstract Restraint eval(Cate cate);
	
	public boolean isSuperior(Cate cate) {
		return eval(cate) == Restraint.SUPERIOR;
	}
	
	public boolean isInferior(Cate cate) {
		return eval(cate) == Restraint.INFERIOR;
	}
	
	public boolean isEquipollence(Cate cate) {
		return eval(cate) == Restraint.EQUIPOLLENCE;
	}
	
	public static Cate valueOf(int type) {
		for (Cate cate : Cate.values()) {
			if (cate.type == type) {
				return cate;
			}
		}
		
		return null;
	}
}
