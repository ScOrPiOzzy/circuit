package com.cas.circuit;

import static java.lang.Math.PI;

import java.util.StringTokenizer;

class AntennaElm extends RailElm {
	double fmphase;

	public AntennaElm(int xx, int yy) {
		super(xx, yy, WF_DC);
	}

	public AntennaElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		waveform = WF_DC;
	}

	@Override
	void doStep() {
		sim.updateVoltageSource(0, nodes[0], voltSource, getVoltage());
	}

	@Override
	int getDumpType() {
		return 'A';
	}

	@Override
	int getShortcut() {
		return 0;
	}

	@Override
	double getVoltage() {
		fmphase += 2 * PI * (2200 + Math.sin(2 * PI * sim.t * 13) * 100) * sim.timeStep;
		double fm = 3 * Math.sin(fmphase);
		return Math.sin(2 * PI * sim.t * 3000) * (1.3 + Math.sin(2 * PI * sim.t * 12)) * 3 + Math.sin(2 * PI * sim.t * 2710) * (1.3 + Math.sin(2 * PI * sim.t * 13)) * 3 + Math.sin(2 * PI * sim.t * 2433) * (1.3 + Math.sin(2 * PI * sim.t * 14)) * 3 + fm;
	}

	@Override
	void stamp() {
		sim.stampVoltageSource(0, nodes[0], voltSource);
	}
}
