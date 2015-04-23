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

public class Comp implements sim.engine.Steppable {
  int id = -Integer.MAX_VALUE;
  double amount = Double.NaN;
  double k_in = Double.NaN;
  double k_out = Double.NaN;
  public Comp(int i, double start, double in, double out) {
    id = i;
    amount = start;
    k_in = in;
    k_out = out;
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    tut.ctrl.Batch.log("Comp:"+id+".step() - cycle = "+state.schedule.getSteps()+" "+describe());
    state.schedule.scheduleOnce(this,tut.model.Model.SUB_ORDER);
  }
  public String describe() {
    com.google.gson.Gson gson = new com.google.gson.Gson();
    return gson.toJson(this);
  }
}
