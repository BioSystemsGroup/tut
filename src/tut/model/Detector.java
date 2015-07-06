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
  }

  /**
   * Sites
   */
  class Site {
    MPO occupant = null;
    public void occupy(MPO o) { occupant = o; } // can be null
    private boolean blocked = false;
    private  double blockDuration = Double.NaN;
    public double timer = Double.NaN;
    public Site(MPO o, double bd) {
      if (o != null) occupant = o;
      if (bd > 0.0) blockDuration = bd;
      else throw new RuntimeException("blockDuration <= 0");
    }
    public void block() {
      blocked = true;
      timer = blockDuration;
    }
    public void unBlock() {
      blocked = false;
      timer = Double.NaN;
    }
    public boolean isBlocked() { return blocked; }
    
  }
  
  java.util.ArrayList<MPO> mpolist = new java.util.ArrayList<>();
  final java.util.ArrayList<Site> sites = new java.util.ArrayList<>();
  double p_occupySite = Double.NaN, p_releaseMPO = Double.NaN;
  
  public Detector(int sn, double po, double pr, double bd) {
    if (sn > 0) for (int i=0 ; i<sn ; i++) sites.add(new Site(null, bd));
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
    // copy sites, loop over Sites with MPO occupants, check them for release, regardless of blocking
    java.util.ArrayList<Site> shuffledSites = shuffle(sites,s.random);
    shuffledSites.stream().filter(site -> (site.occupant != null))
            .forEach((oSite) -> {
              if (s.random.nextDouble() < p_releaseMPO) {
                oSite.occupant = null; // abandons MPO occupant
              }
            });
  }
  
  public void occupySites(SimState s) {
    // ∀ freeMPOs & ∀ non-blocked, unoccupied Site, draw for occupation
    for (Object o : shuffle(mpolist,s.random)) {
      MPO mpo = (MPO) o;
      java.util.Optional op = sites.stream().filter(site
              -> (site.occupant == null) && (!site.isBlocked())).findAny();
      if (!op.isPresent()) break;
      Site freeSite = (Site) op.get();
      if (s.random.nextDouble() < p_occupySite) {
        freeSite.occupant = mpo;
        mpolist.remove(mpo);  // remove it from the freeMPO list
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
