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

public class FunctionCall extends Comp {
  Function<Double,Double> func = null;
  public FunctionCall(int i, Function<Double,Double> f, double start) {
    super(i,start);
    func = f;
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
    particles.get("Drug").val = func.apply(state.schedule.getTime());
  }
}
