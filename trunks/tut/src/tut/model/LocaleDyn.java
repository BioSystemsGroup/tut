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

public class LocaleDyn extends Locale {
  LooseDyn body = null;
  boolean morbidity = false;
  double reliefBottom = Double.NaN, reliefTop = Double.NaN, 
          morb_delay = Double.NaN;
  public void setMorbidity(boolean m, double rb, double rt, double d) {
    if (m && rt <= 0.0) throw new RuntimeException("reliefTop <= 0.0, set morbidity false to disable morbidity");
    morbidity = m;
    if (rt > rb) {
      reliefBottom = rb;
      reliefTop = rt;
    } else throw new RuntimeException("reliefTop <= reliefBottom");
    if (d < 0.0) throw new RuntimeException("morbidity delay must be > 0");
    morb_delay = d;
  }
  
  public LocaleDyn(LooseDyn b, int i, double start, double v) {
    super(i,start,v);
    if (b != null) body = b;
    else throw new RuntimeException("body can't be null.");
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
    if (morbidity) handleMorbidity(state);
  }
  
  private int cyclesWORelief = 0;
  private void handleMorbidity(sim.engine.SimState state) {
    if (reliefBottom < amount && amount < reliefTop) {
      cyclesWORelief = 0;
      // schedule relief for next cycle
      state.schedule.scheduleOnce(
              (sim.engine.SimState s) -> { body.relieveMorbidity(0.5); }, 
              tut.model.Model.SUB_ORDER);
    } else {
      cyclesWORelief++;
      // schedule registration for the future
      double now = state.schedule.getTime();
      state.schedule.scheduleOnce(now+morb_delay, tut.model.Model.SUB_ORDER, 
              (sim.engine.SimState s) -> { 
                double intensity = isl.util.SigmoidGradient.eval(0.0, body.MAX_ACC, 0.0, morb_delay, (double)cyclesWORelief/body.cyclePerTime);
                body.registerMorbidity(intensity);
              });
    }
  }
}
