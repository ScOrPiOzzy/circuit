package com.cas.circuit;

import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

import com.cas.circuit.util.CircuitUtil;

class MemristorElm extends CircuitElm {
	double r_on, r_off, dopeWidth, totalWidth, mobility, resistance;

	Point ps3, ps4;

	public MemristorElm(int xx, int yy) {
		super(xx, yy);
		r_on = 100;
		r_off = 160 * r_on;
		dopeWidth = 0;
		totalWidth = 10e-9; // meters
		mobility = 1e-10; // m^2/sV
		resistance = 100;
	}

	public MemristorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		r_on = new Double(st.nextToken()).doubleValue();
		r_off = new Double(st.nextToken()).doubleValue();
		dopeWidth = new Double(st.nextToken()).doubleValue();
		totalWidth = new Double(st.nextToken()).doubleValue();
		mobility = new Double(st.nextToken()).doubleValue();
		resistance = 100;
	}

	@Override
	void calculateCurrent() {
		current = (volts[0] - volts[1]) / resistance;
	}

	@Override
	void doStep() {
		sim.stampResistor(nodes[0], nodes[1], resistance);
	}

	@Override
	void draw(Graphics g) {
		int segments = 6;
		int i;
		int ox = 0;
		double v1 = volts[0];
		double v2 = volts[1];
		int hs = 2 + (int) (8 * (1 - dopeWidth / totalWidth));
		setBbox(point1, point2, hs);
		draw2Leads(g);
		setPowerColor(g, true);
		double segf = 1. / segments;

		// draw zigzag
		for (i = 0; i <= segments; i++) {
			int nx = (i & 1) == 0 ? 1 : -1;
			if (i == segments) {
				nx = 0;
			}
			double v = v1 + (v2 - v1) * i / segments;
			setVoltageColor(g, v);
			CircuitUtil.interpPoint(lead1, lead2, ps1, i * segf, hs * ox);
			CircuitUtil.interpPoint(lead1, lead2, ps2, i * segf, hs * nx);
			CircuitUtil.drawThickLine(g, ps1, ps2);
			if (i == segments) {
				break;
			}
			CircuitUtil.interpPoint(lead1, lead2, ps1, (i + 1) * segf, hs * nx);
			CircuitUtil.drawThickLine(g, ps1, ps2);
			ox = nx;
		}

		doDots(g);
		drawPosts(g);
	}

	@Override
	String dump() {
		return super.dump() + " " + r_on + " " + r_off + " " + dopeWidth + " " + totalWidth + " " + mobility;
	}

	@Override
	int getDumpType() {
		return 'm';
	}

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Max Resistance (ohms)", r_on, 0, 0);
		if (n == 1)
			return new EditInfo("Min Resistance (ohms)", r_off, 0, 0);
		if (n == 2)
			return new EditInfo("Width of Doped Region (nm)", dopeWidth * 1e9, 0, 0);
		if (n == 3)
			return new EditInfo("Total Width (nm)", totalWidth * 1e9, 0, 0);
		if (n == 4)
			return new EditInfo("Mobility (um^2/(s*V))", mobility * 1e12, 0, 0);
		return null;
	}

	@Override
	void getInfo(String arr[]) {
		arr[0] = "memristor";
		getBasicInfo(arr);
		arr[3] = "R = " + CircuitUtil.getUnitText(resistance, CirSim.ohmString);
		arr[4] = "P = " + CircuitUtil.getUnitText(getPower(), "W");
	}

	@Override
	String getScopeUnits(int x) {
		return (x == 2) ? CirSim.ohmString : (x == 1) ? "W" : "V";
	}

	@Override
	double getScopeValue(int x) {
		return (x == 2) ? resistance : (x == 1) ? getPower() : getVoltageDiff();
	}

	@Override
	boolean nonLinear() {
		return true;
	}

	@Override
	void reset() {
		dopeWidth = 0;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			r_on = ei.value;
		if (n == 1)
			r_off = ei.value;
		if (n == 2)
			dopeWidth = ei.value * 1e-9;
		if (n == 3)
			totalWidth = ei.value * 1e-9;
		if (n == 4)
			mobility = ei.value * 1e-12;
	}

	@Override
	void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps3 = new Point();
		ps4 = new Point();
	}

	@Override
	void stamp() {
		sim.stampNonLinear(nodes[0]);
		sim.stampNonLinear(nodes[1]);
	}

	@Override
	public void startIteration() {
		double wd = dopeWidth / totalWidth;
		dopeWidth += sim.timeStep * mobility * r_on * current / totalWidth;
		if (dopeWidth < 0)
			dopeWidth = 0;
		if (dopeWidth > totalWidth)
			dopeWidth = totalWidth;
		resistance = r_on * wd + r_off * (1 - wd);
	}
}
