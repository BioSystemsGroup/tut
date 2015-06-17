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

import java.util.Map;
import java.util.HashMap;
import tut.ctrl.Parameters;

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
    
    // build the inputs to central
    HashMap<Locale,Map<String,Double>> inputs = new HashMap<>(2);
    HashMap<String,Double> rates = new HashMap<>(1);
    rates.put("Drug",params.lRates.get("Drug")
            .get(new Parameters.Edge("source","central")));
    inputs.put(source,rates);
    rates = new HashMap<>(1);
    rates.put("Drug",params.lRates.get("Drug")
            .get(new Parameters.Edge("periph","central")));
    inputs.put(periph,rates);
    central.setIns(inputs);
    
    // build the inputs to periph
    inputs = new HashMap<>(1);
    rates = new HashMap<>(1);
    rates.put("Drug",params.lRates.get("Drug")
            .get(new Parameters.Edge("central","periph")));
    inputs.put(central,rates);
    periph.setIns(inputs);
    
    // inputs to sink
    inputs = new HashMap<>(1);
    rates = new HashMap<>(1);
    rates.put("Drug",params.lRates.get("Drug")
            .get(new Parameters.Edge("central","sink")));
    inputs.put(central,rates);
    sink.setIns(inputs);
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
