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

import java.util.ArrayList;

public class Model implements sim.engine.Steppable {
  public static int SUB_ORDER = tut.ctrl.Batch.MODEL_ORDER+1;
  public ec.util.MersenneTwisterFast pRNG = null;
  public boolean finished = false;
  tut.ctrl.Parameters params = null;
  public double timeLimit = Double.NaN;
  public double cyclePerTime = Double.NaN;
  
  /**
   * comps allows us to decouple the Observer from the specific Model
   */
  public ArrayList<Comp> comps = new ArrayList<>();
  
  public Model(tut.ctrl.Parameters p) {
    if (p != null) params = p;
  }
  public void init(sim.engine.SimState state, double tl, double cpt) {
    pRNG = state.random;
    if (tl > 0.0) timeLimit = tl;
    else throw new RuntimeException(getClass().getName()+".timeLimit <= 0.0.");
    if (cpt > 0.0) cyclePerTime = cpt;
    else throw new RuntimeException(getClass().getName()+".cyclePerTime <= 0.0.");
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    if (state.schedule.getTime() < timeLimit*cyclePerTime )
      state.schedule.scheduleOnce(this, tut.ctrl.Batch.MODEL_ORDER);
    else {
      finished = true;
      for (Comp c : comps) c.finished = true;
    }
  }
}
