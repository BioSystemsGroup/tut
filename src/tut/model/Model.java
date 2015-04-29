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

import java.util.ArrayList;

public class Model implements sim.engine.Steppable {
  public static int SUB_ORDER = tut.ctrl.Batch.MODEL_ORDER+1;
  public ec.util.MersenneTwisterFast pRNG = null;
  tut.ctrl.Parameters params = null;
  public double cycle2time = 0.25;
  
  /**
   * comps allows us to decouple the Observer from the specific Model
   */
  public ArrayList<Comp> comps = new ArrayList<>();
  
  public Model(tut.ctrl.Parameters p) {
    if (p != null) params = p;
  }
  public void init(sim.engine.SimState state) {
    pRNG = state.random;
    calcConstants();
  }
  
  double A = Double.NaN, B = Double.NaN;
  double α = Double.NaN, β = Double.NaN;
  void calcConstants() {
    double αβ_1 = params.k_12 + params.k_21 + params.k_10;
    double αβ_2 = Math.sqrt(Math.pow(αβ_1, 2.0) - 4.0*params.k_21*params.k_10);
    α = 0.5 * (αβ_1 + αβ_2);
    β = 0.5 * (αβ_1 - αβ_2);
    A = (params.k_a * params.dose)/params.vc * (params.k_21 - α)/((β-α)*(params.k_a-α));
    B = (params.k_a * params.dose)/params.vc * (params.k_21 - β)/((α-β)*(params.k_a-β));
  }

  @Override
  public void step(sim.engine.SimState state) {
    state.schedule.scheduleOnce(this, tut.ctrl.Batch.MODEL_ORDER);
  }
}
