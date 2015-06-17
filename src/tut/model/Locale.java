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
  Map<? extends Locale,Map<String,Double>> ins = null;
  public void setIns(Map<? extends Locale,Map<String,Double>> inl) { ins = inl; }
  double volume = Double.NaN;
  
  public Locale(int i, double start, double v) {
    super(i,start);
    volume = v;
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
    
    if (ins == null) return;  // null inputs = source
    
    for (Map.Entry<? extends Locale,Map<String,Double>> cme : ins.entrySet()) {
      Locale inComp = cme.getKey();
      Map<String,Double> rateMap = cme.getValue();
      for (Map.Entry<String,sim.util.MutableDouble> sme : particles.entrySet()) {
        String inParticleName = sme.getKey();
        sim.util.MutableDouble inParticleAmount = inComp.particles.get(inParticleName);
        double inRate = rateMap.get(inParticleName);
        double inc = inParticleAmount.val*inRate;
        inParticleAmount.val -= inc; // decrement theirs
        particles.get(inParticleName).val += inc; // increment mine
      }
    }
  }
}
