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
  tut.ctrl.Parameters params = null;
  public double cycle2time = 0.25;
  
  /**
   * comps allows us to decouple the Observer from the specific Model
   */
  public ArrayList<Comp> comps = new ArrayList<>();
  
  public Model(tut.ctrl.Parameters p) {
    if (p != null) params = p;
  }
  public void init(sim.engine.SimState state) {
    pRNG = state.random;
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    state.schedule.scheduleOnce(this, tut.ctrl.Batch.MODEL_ORDER);
  }
}
