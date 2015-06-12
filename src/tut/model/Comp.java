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
  public int id = -Integer.MAX_VALUE;
  public java.util.LinkedHashMap<String, sim.util.MutableDouble> particles = new java.util.LinkedHashMap<>(1);
  public boolean finished = false;
  
  public Comp(int i, double start) {
    id = i;
    particles.put("Drug", new sim.util.MutableDouble(start));
    tut.ctrl.Batch.log("Comp("+id+", "+start+")");
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    if (!finished)
      state.schedule.scheduleOnce(this,tut.model.Model.SUB_ORDER);
  }
}
