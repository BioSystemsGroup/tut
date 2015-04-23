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

public class Model implements sim.engine.Steppable {
  public static int MODEL_ORDER = 10;
  public static int SUB_ORDER = 11;
  public ec.util.MersenneTwisterFast pRNG = null;
  Comp central = null, periph = null;
  public double k_a = Double.NaN, k_10 = Double.NaN, k_12 = Double.NaN, k_21 = Double.NaN;
  tut.ctrl.Parameters params = null;
  public Model(tut.ctrl.Parameters p) {
    if (p != null) params = p;
  }
  public void init(sim.engine.SimState state) {
    pRNG = state.random;
    central = new Comp(0, 0.0, params.k_a, params.k_10);
    state.schedule.scheduleOnce(central, SUB_ORDER);
    periph = new Comp(1, 0.0, params.k_21, params.k_12);
    state.schedule.scheduleOnce(periph, SUB_ORDER);
  }

  @Override
  public void step(sim.engine.SimState state) {
    tut.ctrl.Batch.log("Model.step() - cycle = "+state.schedule.getSteps());
    state.schedule.scheduleOnce(this, MODEL_ORDER);
  }
}
