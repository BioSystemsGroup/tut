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
  public Loose(tut.ctrl.Parameters p) {
    super(p);
  }
  
  @Override
  public void init(sim.engine.SimState state) {
    super.init(state);

    // create and schedule the Compartments
    Locale source = new Locale(0,params.dose/params.vc);
    state.schedule.scheduleOnce(source, SUB_ORDER);
    Locale central = new Locale(1, 0.0);
    state.schedule.scheduleOnce(central, SUB_ORDER);
    Locale periph = new Locale(2, 0.0);
    state.schedule.scheduleOnce(periph, SUB_ORDER);
    Locale sink = new Locale(3, 0.0);
    state.schedule.scheduleOnce(sink, SUB_ORDER);
    
    // wire them up
    HashMap<Locale,Double> tmp = new HashMap<>(2);
    tmp.put(source,params.k_a/params.vc);
    tmp.put(periph,params.k_21/params.vc);
    central.setIns(tmp);
    tmp = new HashMap<>(1);
    tmp.put(central,params.k_12/params.vc);
    periph.setIns(tmp);
    tmp = new HashMap<>(1);
    tmp.put(central,params.k_10/params.vc);
    sink.setIns(tmp);
    
    // store them in the ArrayList
    comps.add(source);
    comps.add(central);
    comps.add(periph);
    comps.add(sink);
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
  }
}
