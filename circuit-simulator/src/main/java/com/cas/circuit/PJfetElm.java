package com.cas.circuit;

class PJfetElm extends JfetElm {
	public PJfetElm(int xx, int yy) {
		super(xx, yy, true);
	}

	@Override
	protected Class getDumpClass() {
		return JfetElm.class;
	}
}
