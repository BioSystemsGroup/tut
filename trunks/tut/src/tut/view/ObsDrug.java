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

public class ObsDrug extends Obs {
  private final boolean fraction = true;
  
  public ObsDrug(String en, tut.ctrl.Parameters p) {
    super(en,p);
  }
  
  @Override
  public void writeHeader() {
    // write the output file header
    StringBuilder sb = new StringBuilder("Time");
    subject.comps.stream().forEach((c) -> {
      sb.append(", Comp").append(c.id).append((fraction ? ".fract" : ".conc"));
    });
    outFile.println(sb.toString());
  }
  
  @Override
  public java.util.ArrayList<Double> measure() {
    java.util.ArrayList<Double> retVal = new java.util.ArrayList<>();
    subject.comps.stream().forEach((c) -> {
      double result = (fraction ? subject.getFraction(c) : subject.getConc(c));
      retVal.add(result);
    });
    return retVal;
  }
  
}
