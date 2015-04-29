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

import java.util.function.Function;

public class Tight extends Model {
  public Tight(tut.ctrl.Parameters p) {
    super(p);
  }
  @Override
  public void init(sim.engine.SimState state) {
    super.init(state);
    
    Function func = concCent();
    FunctionCall comp = new FunctionCall(0, func, 0.0);
    state.schedule.scheduleOnce(comp, SUB_ORDER);
    comps.add(comp);
    
    func = concPeriph();
    comp = new FunctionCall(1, func, 0.0);
    state.schedule.scheduleOnce(comp, SUB_ORDER);
    comps.add(comp);
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
  }
  
  Function<Double, Double> concCent() {
    return (Double c) -> {
      double t = c*cycle2time;
      return A*Math.exp(-α*t) + B*Math.exp(-β*t) - (A+B)*Math.exp(-params.k_a*t);
    };
  }
  Function<Double, Double> concPeriph() {
    return (Double c) -> {
      double t = c*cycle2time;
      return (A*params.k_21)/(params.k_21-α) * Math.exp(-α*t)
              + (B*params.k_21)/(params.k_21-β) * Math.exp(-β*t)
              + ((B-A)*params.k_21)/(params.k_21-params.k_a) * Math.exp(-params.k_a*t);
    };
  }
}
