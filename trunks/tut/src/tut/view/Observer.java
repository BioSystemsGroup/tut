/*
 * Copyright 2015 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package tut.view;

public class Observer implements sim.engine.Steppable {
  public static int VIEW_ORDER = 20;
  tut.model.Model subject = null;
  public Observer() {
    
  }
  public void init(sim.engine.SimState state, tut.model.Model m) {
    if (m != null) subject = m;
    else throw new RuntimeException("Subject to Observe cannot be null.");
  }
  @Override
  public void step(sim.engine.SimState state) {
    System.out.println("Observer.step() - cycle = "+state.schedule.getSteps()+": output goes here");
    state.schedule.scheduleOnce(this, VIEW_ORDER);
  }
}
