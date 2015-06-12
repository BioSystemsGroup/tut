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

import java.util.HashMap;

public class Loose extends Model {
  double dose = Double.NaN, vc = Double.NaN;
  
  public Loose(tut.ctrl.Parameters p) {
    super(p);
  }

  @Override
  void instantiate() {
    Locale source = new Locale(0, 0.0, 1.0);
    vc = params.loose.get("vc").doubleValue();
    Locale central = new Locale(1, 0.0, vc);
    Locale periph = new Locale(2, 0.0, params.loose.get("vp").doubleValue());
    Locale sink = new Locale(3, 0.0, 1.0);
    // store them in the ArrayList
    java.util.ArrayList<Locale> tmpComps = new java.util.ArrayList<>(4);
    tmpComps.add(source);
    tmpComps.add(central);
    tmpComps.add(periph);
    tmpComps.add(sink);
    comps = tmpComps;
  }
  
  @Override
  public void init(sim.engine.SimState state, double tl, double cpt) {
    super.init(state, tl, cpt);
    
    // create and schedule the Compartments
    dose_time = params.loose.get("doseTime").doubleValue();
    dose = params.loose.get("dose").doubleValue();
    
    instantiate();
    
    // schedule
    state.schedule.scheduleOnce(comps.get(0), SUB_ORDER);
    state.schedule.scheduleOnce(comps.get(1), SUB_ORDER);
    state.schedule.scheduleOnce(comps.get(2), SUB_ORDER);
    state.schedule.scheduleOnce(comps.get(3), SUB_ORDER);
    
    // wire them up
    Locale source = (Locale)comps.get(0);
    Locale central = (Locale)comps.get(1);
    Locale periph = (Locale)comps.get(2);
    Locale sink = (Locale)comps.get(3);
    
    HashMap<Locale,Double> tmp = new HashMap<>(2);
    tmp.put(source,params.loose.get("src2cent").doubleValue());
    tmp.put(periph,params.loose.get("peri2cent").doubleValue());
    central.setIns(tmp);
    tmp = new HashMap<>(1);
    tmp.put(central,params.loose.get("cent2peri").doubleValue());
    periph.setIns(tmp);
    tmp = new HashMap<>(1);
    tmp.put(central,params.loose.get("cent2sink").doubleValue());
    sink.setIns(tmp);
  }
  
  @Override
  public double getConc(Comp c) {
    Locale l = (Locale) c;
    return l.particles.get("Drug").val/l.volume;
  }
  @Override
  public double getFraction(Comp c) {
    return getConc(c) * vc/dose;
  }
  @Override
  protected void dose() { super.dose(); comps.get(0).particles.get("Drug").val = dose; }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
  }
}
