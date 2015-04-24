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
  tut.ctrl.Parameters params = null;
  public Observer(tut.ctrl.Parameters p) {
    params = p;
  }
  
  public void init(sim.engine.SimState state, tut.model.Model m) {
    if (m != null) subject = m;
    else throw new RuntimeException("Subject to Observe cannot be null.");
    System.out.print("Running:       ");
    tut.ctrl.Batch.log("Cycle, Comp0, Comp1");
  }
  @Override
  public void step(sim.engine.SimState state) {
    tut.ctrl.Batch.log(state.schedule.getSteps()*subject.cycle2time+", "
            +subject.central.amount+", "
            +subject.periph.amount);
    System.out.print(String.format("\b\b\b\b\b%3.0f", state.schedule.getTime()/(params.cycleLimit-1)*100)+"% ");
    state.schedule.scheduleOnce(this, VIEW_ORDER);
  }
}
