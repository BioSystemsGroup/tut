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
  LooseDyn model = null;
  Morbidity morbidity = null;
  public double symptom = Double.NaN;
  public final double symptomMax = 10.0;
  double reliefBottom = Double.NaN, reliefTop = Double.NaN;
  Controller controller = null;
  double sigtop = Double.NaN;
  public void setController(double rb, double rt, double st) { 
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
  public void step(sim.engine.SimState state) {
    super.step(state);
    if (morbidity != null) morbidity.step(state);
    if (controller != null) controller.step(state);
  }

  /**
   * Morbidity represents a condition unique to this compartment
   */
  class Morbidity implements sim.engine.Steppable {
    double amount = Double.NaN;
    public Morbidity(double m) {
      amount = m;
    }
    @Override
    public void step(sim.engine.SimState s) {
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
  class Controller implements sim.engine.Steppable {
    @Override
    public void step(sim.engine.SimState s) {
      createSymptom();
      relieveSymptom();
    }
    private void createSymptom() {
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
  
    private void relieveSymptom() {
      double drug = particles.get("Drug").val;
      double dec = model.params.looseDyn.get("drug2symptom").doubleValue()*drug;
      //tut.ctrl.Batch.log("drug = "+drug+", ["+reliefBottom+","+reliefTop+"], symptom = "+symptom);
      if (reliefBottom <= drug && drug <= reliefTop) symptom -= dec;
      if (symptom < 0.0) symptom = 0.0;
    }
  }

}
