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

public class Tight extends Model {
  double k_a = Double.NaN, k_12 = Double.NaN, k_21 = Double.NaN;
  double k_10 = Double.NaN, dose = Double.NaN, vc = Double.NaN;
  public Tight(tut.ctrl.Parameters p) {
    super(p);
  }
  @Override
  public void init(sim.engine.SimState state, double tl, double cpt) {
    super.init(state, tl, cpt);
    calcConstants();
    
    Function func = concCent();
    FunctionCall comp = new FunctionCall(0, func, 0.0);
    state.schedule.scheduleOnce(comp, SUB_ORDER);
    comps.add(comp);
    
    func = concPeriph();
    comp = new FunctionCall(1, func, 0.0);
    state.schedule.scheduleOnce(comp, SUB_ORDER);
    comps.add(comp);
  }

  @Override
  public void step(sim.engine.SimState state) {
    super.step(state);
  }

  double A = Double.NaN, B = Double.NaN;
  double α = Double.NaN, β = Double.NaN;
  void calcConstants() {
    k_a = params.tight.get("k_a").doubleValue();
    k_10 = params.tight.get("k_10").doubleValue();
    k_12 = params.tight.get("k_12").doubleValue();
    k_21 = params.tight.get("k_21").doubleValue();
    dose = params.tight.get("dose").doubleValue();
    vc = params.tight.get("vc").doubleValue();
    tut.ctrl.Batch.log("k_a="+k_a+", k_10="+k_10+", k_12="+k_12+", k_21="+k_21+", vc="+vc+", dose="+dose);
    double αβ_1 = k_12 + k_21 + k_10;
    double αβ_2 = Math.sqrt(Math.pow(αβ_1, 2.0) - 4.0*k_21*k_10);
    α = 0.5 * (αβ_1 + αβ_2);
    β = 0.5 * (αβ_1 - αβ_2);
    A = (k_a * dose)/vc * (k_21 - α)/((β-α)*(k_a-α));
    B = (k_a * dose)/vc * (k_21 - β)/((α-β)*(k_a-β));
    tut.ctrl.Batch.log("α="+α+", β="+β+", A="+A+", B="+B);
  }

  Function<Double, Double> concCent() {
    return (Double c) -> {
      double t = c/cyclePerTime;
      return A*Math.exp(-α*t) + B*Math.exp(-β*t) - (A+B)*Math.exp(-k_a*t);
    };
  }
  Function<Double, Double> concPeriph() {
    return (Double c) -> {
      double t = c/cyclePerTime;
      return (A*k_21)/(k_21-α) * Math.exp(-α*t)
              + (B*k_21)/(k_21-β) * Math.exp(-β*t)
              + ((B-A)*k_21)/(k_21-k_a) * Math.exp(-k_a*t);
    };
  }
}
