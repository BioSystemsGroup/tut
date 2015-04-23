/*
 * Copyright 2015 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package tut.ctrl;

public class Batch {
  long cycleLimit = -Integer.MAX_VALUE;
  long seed = -Integer.MAX_VALUE;
  Parameters params = null;
  ec.util.MersenneTwisterFast pRNG = null;
  public sim.engine.SimState state = null;
  public Batch(tut.ctrl.Parameters p) {
    if (p != null) params = p;
    if (params.cycleLimit > 0) cycleLimit = params.cycleLimit;
    if (params.seed > 0) seed = params.seed;
    state = new sim.engine.SimState(seed);
    pRNG = state.random;
  }
  public final void load() {
    cycleLimit = params.cycleLimit;
    tut.model.Model m = new tut.model.Model(params);
    m.init(state);
    state.schedule.scheduleOnce(m, tut.model.Model.MODEL_ORDER);
    
    tut.view.Observer obs = new tut.view.Observer();
    obs.init(state, m);
    state.schedule.scheduleOnce(obs, tut.view.Observer.VIEW_ORDER);
  }
  
  public void go() {
    while (state.schedule.getSteps() < cycleLimit) {
      state.schedule.step(state);
    }
    System.out.println("Batch.go() - Done!");
  }

}
