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
  private double dose = Double.NaN, vc = Double.NaN;
  
  public Loose(tut.ctrl.Parameters p) {
    super(p);
  }
  
  @Override
  public void init(sim.engine.SimState state, double tl, double cpt) {
    super.init(state, tl, cpt);
    
    // create and schedule the Compartments
    dose = params.loose.get("dose").doubleValue();
    Locale source = new Locale(0, dose, 1.0);
    state.schedule.scheduleOnce(source, SUB_ORDER);
    vc = params.loose.get("vc").doubleValue();
    Locale central = new Locale(1, 0.0, vc);
    state.schedule.scheduleOnce(central, SUB_ORDER);
    Locale periph = new Locale(2, 0.0, params.loose.get("vp").doubleValue());
    state.schedule.scheduleOnce(periph, SUB_ORDER);
    Locale sink = new Locale(3, 0.0, 1.0);
    state.schedule.scheduleOnce(sink, SUB_ORDER);
    
    // wire them up
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
    
    // store them in the ArrayList
    comps.add(source);
    comps.add(central);
    comps.add(periph);
    comps.add(sink);
  }
  
  @Override
  public double getConc(Comp c) {
    Locale l = (Locale) c;
    return l.amount/l.volume;
  }
  @Override
  public double getFraction(Comp c) {
    return getConc(c) * vc/dose;
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
  }
}
