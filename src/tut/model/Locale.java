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
  Map<? extends Locale,Double> ins = null;
  public void setIns(Map<? extends Locale,Double> inl) { ins = inl; }
  double volume = Double.NaN;
  
  public Locale(int i, double start, double v) {
    super(i,start);
    volume = v;
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
    
    if (ins == null) return;  // null inputs = source
    
    for (Map.Entry<? extends Locale,Double> cme : ins.entrySet()) {
      Locale inComp = cme.getKey();
      double inRate = cme.getValue();
      for (Map.Entry<String,sim.util.MutableDouble> sme : particles.entrySet()) {
        sim.util.MutableDouble inSpecies = inComp.particles.get(sme.getKey());
        double inc = inSpecies.val*inRate;
        inSpecies.val -= inc; // decrement theirs
        particles.get(sme.getKey()).val += inc; // increment mine
      }
    }
  }
}
