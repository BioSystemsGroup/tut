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
  double reliefBottom = Double.NaN, reliefTop = Double.NaN;
  boolean controller = false;
  public void setController(boolean c) { 
    controller = c; 
    pain = 0.0;
  }
  
  public LocaleDyn(LooseDyn b, int i, double start, double v) {
    super(i,start,v);
    particles.put("Marker", new sim.util.MutableDouble(0.0));
    if (b != null) body = b;
    else throw new RuntimeException("body can't be null.");
  }
  
  public void setMorbidity(double m, double rb, double rt) {
    if (m < 0.0 && rt <= 0.0) throw new RuntimeException("reliefTop <= 0.0, set morbidity = 0 disable it");
    morbidity = m;
    if (rt > rb) {
      reliefBottom = rb;
      reliefTop = rt;
    } else throw new RuntimeException("reliefTop <= reliefBottom");
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
    
    if (morbidity > 0.0) handleMorbidity();
    if (controller) eliminateMarker();
  }
  
  private void eliminateMarker() {
    
  }
  
  private void handleMorbidity() {
    
  }
}
