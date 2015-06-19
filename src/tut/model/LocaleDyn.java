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
  double morbidity = 0.0;
  public double symptom = Double.NaN;
  public final double symptomMax = 10.0;
  double reliefBottom = Double.NaN, reliefTop = Double.NaN;
  boolean controller = false;
  double sigtop = Double.NaN;
  public void setController(boolean c, double rb, double rt, double st) { 
    controller = c; 
    symptom = 0.0;
    if (c && rt <= 0.0) throw new RuntimeException("reliefTop <= 0.0, set morbidity = 0 disable it");
    if (rt > rb) {
      reliefBottom = rb;
      reliefTop = rt;
    } else throw new RuntimeException("reliefTop <= reliefBottom");
    sigtop = st;
  }
  
  public LocaleDyn(LooseDyn b, int i, double start, double v) {
    super(i,start,v);
    particles.put("MP", new sim.util.MutableDouble(0.0));
    if (b != null) body = b;
    else throw new RuntimeException("body can't be null.");
  }
  
  public void setMorbidity(double m) {
    morbidity = m;
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
    if (morbidity > 0.0) handleMorbidity();
    if (controller) {
      createSymptom();
      relieveSymptom();
    }
  }
  
  private void handleMorbidity() {
    particles.get("MP").val += morbidity
            * body.params.looseDyn.get("morb2mp").doubleValue();
    morbidity += morbidity
            * body.params.looseDyn.get("morbFactor").doubleValue();
  }
  
  private void createSymptom() {
    sim.util.MutableDouble mp = particles.get("MP");
    if (symptom < symptomMax && mp.val > 0.0) {
      double mp2symptom = body.params.looseDyn.get("mp2symptom").doubleValue();
      double mpSum = body.sumMP();
      double mp_inc = isl.util.SigmoidGradient.eval(0.0, 10.0, 0.0, sigtop, body.sumMP());
      tut.ctrl.Batch.log("sigtop = "+sigtop+", symptom: "+symptom+" += "+mp_inc+" :SG("+mpSum+")");
      symptom += mp_inc*mp2symptom;
      if (symptom > symptomMax) symptom = symptomMax;
      mp.val -= mp_inc;
      if (mp.val < 0.0) mp.val = 0.0;
    }
  }
  
  private void relieveSymptom() {
    double drug = particles.get("Drug").val;
    double dec = body.params.looseDyn.get("drug2symptom").doubleValue()*drug;
    //tut.ctrl.Batch.log("drug = "+drug+", ["+reliefBottom+","+reliefTop+"], symptom = "+symptom);
    if (reliefBottom <= drug && drug <= reliefTop) symptom -= dec;
    if (symptom < 0.0) symptom = 0.0;
  }
}
