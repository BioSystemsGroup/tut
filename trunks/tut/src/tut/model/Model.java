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

import java.util.function.Function;

public class Model implements sim.engine.Steppable {
  public static int MODEL_ORDER = 10;
  public static int SUB_ORDER = 11;
  public ec.util.MersenneTwisterFast pRNG = null;
  public Comp central = null, periph = null;
  tut.ctrl.Parameters params = null;
  public double cycle2time = 0.25;
  public Model(tut.ctrl.Parameters p) {
    if (p != null) params = p;
  }
  public void init(sim.engine.SimState state) {
    pRNG = state.random;
    calcConstants();
    Function func = concCent();
    central = new Comp(0, func, 0.0);
    state.schedule.scheduleOnce(central, SUB_ORDER);
    func = concPeriph();
    periph = new Comp(1, func, 0.0);
    state.schedule.scheduleOnce(periph, SUB_ORDER);
  }

  @Override
  public void step(sim.engine.SimState state) {
    state.schedule.scheduleOnce(this, MODEL_ORDER);
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
    //tut.ctrl.Batch.log("calcConstants(): "+params.describe()+", αβ_1="+αβ_1+", αβ_2="+αβ_2+", α="+α+", β="+β+", A="+A+", B="+B);
  }
  Function<Double, Double> concCent() {
    return (Double c) -> {
      double t = c*cycle2time;
      return A*Math.exp(-α*t) + B*Math.exp(-β*t) - (A+B)*Math.exp(-params.k_a*t);
    };
  }
  Function<Double, Double> concPeriph() {
    return (Double c) -> {
      double t = c*cycle2time;
      return (A*params.k_21)/(params.k_21-α) * Math.exp(-α*t)
              + (B*params.k_21)/(params.k_21-β) * Math.exp(-β*t)
              + ((B-A)*params.k_21)/(params.k_21-params.k_a) * Math.exp(-params.k_a*t);
    };
  }
}
