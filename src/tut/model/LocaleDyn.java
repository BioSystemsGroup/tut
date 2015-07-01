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

import java.util.Collections;
import sim.engine.Steppable;
import sim.engine.SimState;

public class LocaleDyn extends Locale {
  LooseDyn model = null;
  Morbidity morbidity = null;
  public double symptom = Double.NaN;
  public final double symptomMax = 10.0;
  double reliefBottom = Double.NaN, reliefTop = Double.NaN;
  Controller controller = null;
  boolean useMPO = true;
  Detector detector = null;
  double sigtop = Double.NaN;
  public void setController(double rb, double rt, double st) { 
    if (useMPO) detector = new Detector(model.params.looseDyn.get("detectorSites").intValue());
    controller = new Controller();
    symptom = 0.0;
    if (rt <= 0.0) throw new RuntimeException("reliefTop <= 0.0, set morbidity = 0 disable it");
    if (rt > rb) {
      reliefBottom = rb;
      reliefTop = rt;
    } else throw new RuntimeException("reliefTop <= reliefBottom");
    sigtop = st;
  }
  
  public LocaleDyn(LooseDyn m, int i, double start, double v) {
    super(i,start,v);
    particles.put("MP", new sim.util.MutableDouble(0.0));
    if (m != null) model = m;
    else throw new RuntimeException("Parent model can't be null.");
  }
  
  public void setMorbidity(double m) {
    morbidity = new Morbidity(m);
  }

  @Override
  public void step(SimState state) {
    super.step(state);
    if (morbidity != null) morbidity.step(state);
    if (controller != null) controller.step(state);
  }

  /**
   * Morbidity represents a condition unique to this compartment
   */
  class Morbidity implements Steppable {
    double amount = Double.NaN;
    public Morbidity(double m) {
      amount = m;
    }
    @Override
    public void step(SimState s) {
      particles.get("MP").val += amount
              * model.params.looseDyn.get("morb2mp").doubleValue();
      amount += amount
              * model.params.looseDyn.get("morbFactor").doubleValue();
    }
  }

  /**
   * Controller represents the agents that respond to morbidity product with
   * whatever symptoms result.  It also controls whether/how Symptom responds
   * to Drug.
   */
  class Controller extends SlaveAgent {
    public Controller() {
      actions.add(new Steppable() {
        @Override
        public void step(SimState s) { if (useMPO) adjustMPOs(); adjustSymptom(); }
      });
      if (!useMPO)
        actions.add(new Steppable() {
          @Override
          public void step(SimState s) { relieveSymptom(); }
        });
    }
    @Override
    public void step(SimState s) {
      super.step(s);
      if (useMPO) detector.step(s);
    }
    private void adjustSymptom() {
      if (useMPO) symptomFromDetectedMPO();
      else symptomFromMP();
    }
    private void symptomFromDetectedMPO() {
      double drugPotency = model.params.looseDyn.get("drugPotency").doubleValue();
      double drugAmount = particles.get("Drug").val;
      //double symptomFraction = detector.sites.size() - drugPotency*drugAmount;
      double symptomFraction = 1.0 - (drugPotency*drugAmount/detector.sites.size());
      if (symptomFraction < 0.0) symptomFraction = 0.0;
      symptom = symptomFraction * detector.sites.stream().filter((o) -> (o != null)).count();
      System.out.println("Comp"+id+".detector.sites.size() = "+detector.sites.size()+", symptom = "+symptom);
    }
    /**
     * MP are destroyed
     */
    private void symptomFromMP() {
      sim.util.MutableDouble mp = particles.get("MP");
      if (symptom < symptomMax && mp.val > 0.0) {
        double mp2symptom = model.params.looseDyn.get("mp2symptom").doubleValue();
        double mpSum = model.sumMP();
        double mp_inc = isl.util.SigmoidGradient.eval(0.0, 10.0, 0.0, sigtop, model.sumMP());
        tut.ctrl.Batch.log("sigtop = "+sigtop+", symptom: "+symptom+" += "+mp_inc+" :SG("+mpSum+")");
        symptom += mp_inc*mp2symptom;
        if (symptom > symptomMax) symptom = symptomMax;
        mp.val -= mp_inc;
        if (mp.val < 0.0) mp.val = 0.0;
      }
    }
    /**
     * No MP are destroyed
     */
    private void adjustMPOs() {
      int current = detector.mpolist.size();
      double desired = Math.floor(model.params.looseDyn.get("mp2mpo").doubleValue() * particles.get("MP").val);
      System.out.println("Comp"+id+","+desired+" = "+model.params.looseDyn.get("mp2mpo").doubleValue()+" * "+particles.get("MP").val);
      if (current > desired) detector.shrink((int)desired);
      else for (int i=current ; i<desired ; i++) detector.mpolist.add(new MPO());
    }
    
    private void relieveSymptom() {
      double drug = particles.get("Drug").val;
      double dec = model.params.looseDyn.get("drug2symptom").doubleValue()*drug;
      //tut.ctrl.Batch.log("drug = "+drug+", ["+reliefBottom+","+reliefTop+"], symptom = "+symptom);
      if (reliefBottom <= drug && drug <= reliefTop) symptom -= dec;
      if (symptom < 0.0) symptom = 0.0;
    }
  }

  /**
   * Morbidity Product Object - composition of Morbidity Products
   */
  class MPO {
    public MPO() {}
  }
  class Detector extends SlaveAgent {
    java.util.ArrayList<MPO> mpolist = new java.util.ArrayList<>();
    final java.util.ArrayList<MPO> sites = new java.util.ArrayList<>();
    public Detector(int sn) {
      if (sn > 0) for (int i=0 ; i<sn ; i++) sites.add(null);
      else throw new RuntimeException("Invalid site number: "+sn);
      
      actions.add(new Steppable() {
        @Override
        public void step(SimState s) { releaseSites(); }
      });
      actions.add(new Steppable() {
        @Override
        public void step(SimState s) { occupySites(); }
      });
    }
    
    public void releaseSites() {
      // copy sites, loop over non-null values, check them for release
      shuffle(sites,model.pRNG).stream().filter(o -> (o != null)).forEach((o) -> {
        MPO mpo = (MPO) o;
        if (model.pRNG.nextDouble() < model.params.looseDyn.get("P_releaseMPO").doubleValue()) {
          sites.set(sites.indexOf(mpo), null); // abandon the MPO to GC
        }
      });
    }
    public void occupySites() {
      for (Object o : shuffle(mpolist,model.pRNG)) {
        MPO mpo = (MPO) o;
        int site = sites.indexOf(null);
        if (site == -1) break;
        if (model.pRNG.nextDouble() < model.params.looseDyn.get("P_occupySite").doubleValue()) {
          sites.set(site,mpo);
          mpolist.remove(mpo);  // remove it from the original list
        }
      }
    }
    
    public void shrink(int size) {
      java.util.ArrayList<MPO> newlist = new java.util.ArrayList<>(mpolist.subList(0,size));
      mpolist.clear();  // unhook them for GC
      mpolist = newlist;
    }
  } // end Detector class
  
}
