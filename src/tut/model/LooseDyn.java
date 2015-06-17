/*
 * Copyright 2015 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package tut.model;

import tut.ctrl.Parameters;

public class LooseDyn extends Loose {
  private final double RELIEF_BOTTOM = 0.30, RELIEF_TOP=0.70;
  
  public LooseDyn(tut.ctrl.Parameters p) {
    super(p);
  }
  
  @Override
  void instantiate() {
    LocaleDyn source = new LocaleDyn(this, 0, 0.0, 1.0);
    vc = params.loose.get("vc").doubleValue();
    LocaleDyn central = new LocaleDyn(this, 1, 0.0, vc);
    LocaleDyn periph = new LocaleDyn(this, 2, 0.0, params.loose.get("vp").doubleValue());
    double morbidity = params.looseDyn.get("morbidity").doubleValue();
    periph.setMorbidity(morbidity);
    LocaleDyn sink = new LocaleDyn(this, 3, 0.0, 1.0);
    sink.setController(true, RELIEF_BOTTOM*dose/vc, RELIEF_TOP*dose/vc);
    // store them in the ArrayList
    java.util.ArrayList<LocaleDyn> tmpComps = new java.util.ArrayList<>(4);
    tmpComps.add(source);
    tmpComps.add(central);
    tmpComps.add(periph);
    tmpComps.add(sink);
    comps = tmpComps;
  }
  
  public void init(sim.engine.SimState state, double tl, double cpt) {
    super.init(state, tl, cpt);
    
    // add rates for the new particle, morbidity product (mp)
    LocaleDyn source = (LocaleDyn)comps.get(0);
    LocaleDyn central = (LocaleDyn)comps.get(1);
    LocaleDyn periph = (LocaleDyn)comps.get(2);
    LocaleDyn sink = (LocaleDyn)comps.get(3);
    central.ins.get(source).put("MP", params.ldRates.get("MP")
            .get(new Parameters.Edge("source","central")));
    central.ins.get(periph).put("MP", params.ldRates.get("MP")
            .get(new Parameters.Edge("periph","central")));
    periph.ins.get(central).put("MP", params.ldRates.get("MP")
            .get(new Parameters.Edge("central","periph")));
    sink.ins.get(central).put("MP", params.ldRates.get("MP")
            .get(new Parameters.Edge("central","sink")));
  }    
}
