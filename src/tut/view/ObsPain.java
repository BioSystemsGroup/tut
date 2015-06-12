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

import tut.model.LooseDyn;

public class ObsPain extends Obs {
  public ObsPain(String en, tut.ctrl.Parameters p) {
    super(en,p);
  }

  @Override
  public void init(java.io.File dir, tut.model.Model m) {
    super.init(dir,m);
    if (!(m instanceof LooseDyn)) throw new RuntimeException("Pain can only be measured from LooseDyn");
  }
  @Override
  public void writeHeader() {
    StringBuilder sb = new StringBuilder("Time");
    subject.comps.stream().forEach((c) -> {
      sb.append(", Comp").append(c.id);
    });
    outFile.println(sb.toString());
  }
  
  @Override
  public java.util.ArrayList<Double> measure() {
    java.util.ArrayList<Double> retVal = new java.util.ArrayList<>();
    subject.comps.stream().forEach((c) -> {
      retVal.add(((tut.model.LocaleDyn)c).pain);
    });
    return retVal;
  }
}
