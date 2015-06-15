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
  public double pain = Double.NaN;
  public final double painMax = 10.0;
  double reliefBottom = Double.NaN, reliefTop = Double.NaN;
  boolean controller = false;
  public void setController(boolean c, double rb, double rt) { 
    controller = c; 
    pain = 0.0;
    if (c && rt <= 0.0) throw new RuntimeException("reliefTop <= 0.0, set morbidity = 0 disable it");
    if (rt > rb) {
      reliefBottom = rb;
      reliefTop = rt;
    } else throw new RuntimeException("reliefTop <= reliefBottom");
  }
  
  public LocaleDyn(LooseDyn b, int i, double start, double v) {
    super(i,start,v);
    particles.put("Marker", new sim.util.MutableDouble(0.0));
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
      eliminateMarker();
      relievePain();
    }
  }
  
  private void handleMorbidity() {
    particles.get("Marker").val += morbidity
            * body.params.looseDyn.get("morb2mark").doubleValue();
  }
  
  private void eliminateMarker() {
    sim.util.MutableDouble marker = particles.get("Marker");
    if (pain < painMax && marker.val > 0.0) {
      double inc = body.params.looseDyn.get("mark2pain").doubleValue()*marker.val;
      pain += inc;
      if (pain > painMax) pain = painMax;
      marker.val -= inc;
      if (marker.val < 0.0) marker.val = 0.0;
    }
  }
  
  private void relievePain() {
    double drug = particles.get("Drug").val;
    double dec = body.params.looseDyn.get("drug2pain").doubleValue()*drug;
    tut.ctrl.Batch.log("drug = "+drug+", ["+reliefBottom+","+reliefTop+"], pain = "+pain);
    if (reliefBottom <= drug && drug <= reliefTop) pain -= dec;
    if (pain < 0.0) pain = 0.0;
  }
}
