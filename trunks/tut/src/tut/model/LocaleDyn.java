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

import sim.engine.Steppable;
import sim.engine.SimState;

public class LocaleDyn extends Locale {
  LooseDyn model = null;
  Morbidity morbidity = null;
  double morb2mp = Double.NaN, morbFactor = Double.NaN;
  public double symptom = Double.NaN;

  Controller controller = null;
  public void setController(int sn, double dpot, double mp2o, double po, double pr) { 
    controller = new Controller(this,sn,dpot,mp2o,po,pr);
    symptom = 0.0;
  }
  
  public LocaleDyn(LooseDyn m, int ident, double start, double v) {
    super(ident,start,v);
    particles.put("MP", new sim.util.MutableDouble(0.0));
    if (m != null) model = m;
    else throw new RuntimeException("Parent model can't be null.");
  }
  
  public void setMorbidity(double m, double m2mp, double mf) {
    morbidity = new Morbidity(m);
    morb2mp = m2mp;
    morbFactor = mf;
  }

  @Override
  public void step(SimState state) {
    super.step(state);
    if (morbidity != null) morbidity.step(state);
    if (controller != null) controller.step(state);
  }

  /**
   * Morbidity represents a condition unique to this compartment
   */
  class Morbidity implements Steppable {
    double amount = Double.NaN;
    public Morbidity(double m) {
      amount = m;
    }
    @Override
    public void step(SimState s) {
      particles.get("MP").val += amount * morb2mp;
      amount += amount * morbFactor;
    }
  }
  
}
