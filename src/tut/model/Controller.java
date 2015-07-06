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

import sim.engine.Steppable;
import sim.engine.SimState;

/**
 * Controller represents the agents that respond to morbidity product with
 * whatever symptoms result.  It also controls whether/how Symptom responds
 * to Drug.
 */
class Controller extends SlaveAgent {
  LocaleDyn comp = null;
  Detector detector = null;
  int detectorSites = -Integer.MAX_VALUE;
  double drugPotency = Double.NaN, mp2mpo = Double.NaN;
  
  public Controller(LocaleDyn c, int sn, double dpot, double mp2o, double po, double pr, double bd) {
    if (c == null) throw new RuntimeException("Containing Compartment can't be null");
    else comp = c;
    if (sn <= 0) throw new RuntimeException("DetectorSites <= 0");
    else detectorSites = sn;
    if (dpot < 0.0) throw new RuntimeException("drugPotency < 0.0");
    else drugPotency = dpot;
    if (mp2o < 0.0) throw new RuntimeException("mp2mpo < 0.0");
    else mp2mpo = mp2o;
    
    detector = new Detector(detectorSites, po, pr, bd);
    
    actions.add(new Steppable() {
      @Override
      public void step(SimState s) { adjustMPOs(); }
    });
    actions.add(new Steppable() {
      @Override
      public void step(SimState s) { adjustBlocks(s); }
    });
  }
  @Override
  public void step(SimState s) {
    super.step(s);
    detector.step(s);
  }
  private void adjustBlocks(SimState s) {
    double drugAmount = comp.particles.get("Drug").val;
    double blockFraction = drugPotency*drugAmount;
    if (blockFraction < 0.0) blockFraction = 0.0;
    if (blockFraction > 1.0) blockFraction = 1.0;
    final double p_block = blockFraction;
    System.out.println("drugAmount = "+drugAmount+", drugPotency = "+drugPotency+", p_block = "+p_block);
    java.util.stream.Stream<Detector.Site> openSites = detector.sites.stream().filter(site -> !site.isBlocked()); 
    java.util.stream.Stream<Detector.Site> blockedSites = detector.sites.stream().filter(site -> site.isBlocked());
    openSites.forEach((openSite) -> {
      if (s.random.nextDouble() < p_block) openSite.block();
    });
    blockedSites.forEach((blockedSite -> {
      if (blockedSite.timer <= 0.0 && s.random.nextDouble() < 1.0-p_block)
        blockedSite.unBlock();
      else blockedSite.timer -= 1.0/comp.model.cyclePerTime;
    }));
  }
  
  private void adjustMPOs() {
    int current = detector.mpolist.size();
    double desired = Math.floor(mp2mpo * comp.particles.get("MP").val);
    if (current > desired) detector.downMPO((int)desired);
    else detector.upMPO((int)(desired-current));
  }
    
}
