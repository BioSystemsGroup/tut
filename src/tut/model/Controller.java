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
  
  public Controller(LocaleDyn c, int sn, double dpot, double mp2o, double po, double pr) {
    if (c == null) throw new RuntimeException("Containing Compartment can't be null");
    else comp = c;
    if (sn <= 0) throw new RuntimeException("DetectorSites <= 0");
    else detectorSites = sn;
    if (dpot < 0.0) throw new RuntimeException("drugPotency < 0.0");
    else drugPotency = dpot;
    if (mp2o < 0.0) throw new RuntimeException("mp2mpo < 0.0");
    else mp2mpo = mp2o;
    
    detector = new Detector(detectorSites, po, pr);
    
    actions.add(new Steppable() {
      @Override
      public void step(SimState s) { adjustMPOs(); adjustSymptom(); }
    });
  }
  @Override
  public void step(SimState s) {
    super.step(s);
    detector.step(s);
  }
  private void adjustSymptom() {
    double drugAmount = comp.particles.get("Drug").val;
    //double symptomFraction = detector.sites.size() - drugPotency*drugAmount;
    double symptomFraction = 1.0 - (drugPotency*drugAmount/detector.sites.size());
    if (symptomFraction < 0.0) symptomFraction = 0.0;
    comp.symptom = symptomFraction * detector.sites.stream().filter((o) -> (o != null)).count();
  }
  
  private void adjustMPOs() {
    int current = detector.mpolist.size();
    double desired = Math.floor(mp2mpo * comp.particles.get("MP").val);
    if (current > desired) detector.downMPO((int)desired);
    else detector.upMPO((int)(desired-current));
  }
    
}
