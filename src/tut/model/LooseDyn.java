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

public class LooseDyn extends Model {
  private double dose = Double.NaN, vc = Double.NaN;
  public double acc = Double.NaN; // accumulator for "pain"
  double acc_factor = 1.0;
  public double MAX_ACC = 10.0;
  private final double RELIEF_BOTTOM = 0.30, RELIEF_TOP=0.70;
  
  public LooseDyn(tut.ctrl.Parameters p) {
    super(p);
  }
  
  @Override
  public void init(sim.engine.SimState state, double tl, double cpt) {
    super.init(state, tl, cpt);

    acc = params.loose.get("initialAcc").intValue();
    double morb_delay_hr = params.loose.get("morbidityDelay").doubleValue();
    // create and schedule the Compartments
    dose_time = params.loose.get("doseTime").doubleValue();
    dose = params.loose.get("dose").doubleValue();
    LocaleDyn source = new LocaleDyn(this, 0, 0.0, 1.0);
    state.schedule.scheduleOnce(source, SUB_ORDER);
    vc = params.loose.get("vc").doubleValue();
    LocaleDyn central = new LocaleDyn(this, 1, 0.0, vc);
    central.setMorbidity(true, RELIEF_BOTTOM*dose/vc, RELIEF_TOP*dose/vc, cpt*morb_delay_hr);
    state.schedule.scheduleOnce(central, SUB_ORDER);
    LocaleDyn periph = new LocaleDyn(this, 2, 0.0, params.loose.get("vp").doubleValue());
    state.schedule.scheduleOnce(periph, SUB_ORDER);
    LocaleDyn sink = new LocaleDyn(this, 3, 0.0, 1.0);
    state.schedule.scheduleOnce(sink, SUB_ORDER);
    
    // wire them up
    HashMap<LocaleDyn,Double> tmp = new HashMap<>(2);
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
  
  public void relieveMorbidity(double intensity) {
    acc -= intensity;
    if (acc < 0.0) acc = 0.0;
  }
  public void registerMorbidity(double intensity) {
    //acc += intensity*acc_factor;
    acc = intensity;
    if (acc > 10.0) acc = 10.0;
  }
  @Override
  protected void dose() { super.dose(); comps.get(0).amount = dose; }
  
  @Override
  public double getConc(Comp c) {
    LocaleDyn l = (LocaleDyn) c;
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
