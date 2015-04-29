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

import java.util.Map;

public class Locale extends Comp {
  Map<Locale,Double> ins = null;
  public void setIns(Map<Locale,Double> inl) { ins = inl; }
  double ratio = 1.0; // factor by which to compare to a reference compartment
  
  public Locale(int i, double start) {
    super(i,start);
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
    
    if (ins == null) return;  // null inputs = source
    
    for (Map.Entry<Locale,Double> me : ins.entrySet()) {
      Locale in = me.getKey();
      double inc = in.amount*me.getValue();
      in.amount -= inc;
      amount += inc;
    }
  }
}
