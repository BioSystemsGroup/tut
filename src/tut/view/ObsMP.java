/*
 * Copyright 2015 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package tut.view;

public class ObsMP extends Obs {
  public ObsMP(String en, tut.ctrl.Parameters p) {
    super(en,p);
  }
  
  @Override
  public java.util.ArrayList<Double> measure() {
    java.util.ArrayList<Double> retVal = ((tut.model.LooseDyn)subject).countMP();
    return retVal;
  }
}
