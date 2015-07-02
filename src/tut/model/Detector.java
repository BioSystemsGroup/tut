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

class Detector extends SlaveAgent {
  
  /**
   * Morbidity Product Object - composition of Morbidity Products
   */
  class MPO {
    public MPO() {}
  }

  java.util.ArrayList<MPO> mpolist = new java.util.ArrayList<>();
  final java.util.ArrayList<MPO> sites = new java.util.ArrayList<>();
  double p_occupySite = Double.NaN, p_releaseMPO = Double.NaN;
  
  public Detector(int sn, double po, double pr) {
    if (sn > 0) for (int i=0 ; i<sn ; i++) sites.add(null);
    else throw new RuntimeException("Invalid site number: "+sn);
    p_occupySite = po;
    p_releaseMPO = pr;
    
    actions.add(new Steppable() {
      @Override
      public void step(SimState s) { releaseSites(s); }
    });
    actions.add(new Steppable() {
      @Override
      public void step(SimState s) { occupySites(s); }
    });
  }
    
  public void releaseSites(SimState s) {
    // copy sites, loop over non-null values, check them for release
    shuffle(sites,s.random).stream().filter(o -> (o != null)).forEach((o) -> {
      MPO mpo = (MPO) o;
      if (s.random.nextDouble() < p_releaseMPO) {
        sites.set(sites.indexOf(mpo), null); // abandon the MPO to GC
      }
    });
  }
  public void occupySites(SimState s) {
    for (Object o : shuffle(mpolist,s.random)) {
      MPO mpo = (MPO) o;
      int site = sites.indexOf(null);
      if (site == -1) break;
      if (s.random.nextDouble() < p_occupySite) {
        sites.set(site,mpo);
        mpolist.remove(mpo);  // remove it from the original list
      }
    }
  }
  
  public void upMPO(int delta) {
    for (int i=0 ; i<delta ; i++) mpolist.add(new MPO());
  }
  
  public void downMPO(int size) {
    java.util.ArrayList<MPO> newlist = new java.util.ArrayList<>(mpolist.subList(0,size));
    mpolist.clear();  // unhook them for GC
    mpolist = newlist;
  }
} // end Detector class
