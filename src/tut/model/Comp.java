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

public abstract class Comp implements sim.engine.Steppable {
  public int id = -Integer.MAX_VALUE;
  public double amount = Double.NaN;
  public boolean finished = false;
  
  public Comp(int i, double start) {
    id = i;
    amount = start;
    tut.ctrl.Batch.log("Comp("+id+", "+start+")");
  }
  
  public abstract double getConc();
  
  @Override
  public void step(sim.engine.SimState state) {
    if (!finished)
      state.schedule.scheduleOnce(this,tut.model.Model.SUB_ORDER);
  }
}
