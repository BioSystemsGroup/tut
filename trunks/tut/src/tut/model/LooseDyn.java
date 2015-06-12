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
    periph.setMorbidity(morbidity, RELIEF_BOTTOM*dose/vc, RELIEF_TOP*dose/vc);
    LocaleDyn sink = new LocaleDyn(this, 3, 0.0, 1.0);
    sink.setController(true);
    // store them in the ArrayList
    java.util.ArrayList<LocaleDyn> tmpComps = new java.util.ArrayList<>(4);
    tmpComps.add(source);
    tmpComps.add(central);
    tmpComps.add(periph);
    tmpComps.add(sink);
    comps = tmpComps;
  }
  
}
