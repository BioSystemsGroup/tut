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

public class Comp implements sim.engine.Steppable {
  public int id = -Integer.MAX_VALUE;
  public double amount = Double.NaN;
  Function<Double,Double> func = null;
  public Comp(int i, Function<Double,Double> f, double start) {
    id = i;
    func = f;
    amount = start;
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    amount = func.apply(state.schedule.getTime());
    state.schedule.scheduleOnce(this,tut.model.Model.SUB_ORDER);
  }
  public String describe() {
    com.google.gson.Gson gson = new com.google.gson.Gson();
    return gson.toJson(this);
  }
}
