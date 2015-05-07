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

public class Observer implements sim.engine.Steppable {
  public static int VIEW_ORDER = 20;
  tut.model.Model subject = null;
  String expName = null;
  java.io.PrintWriter outFile = null;
  tut.ctrl.Parameters params = null;
  public Observer(String en, tut.ctrl.Parameters p) {
    params = p;
    if (en != null && !en.equals("")) expName = en;
    else throw new RuntimeException("Experiment name cannot be null or empty.");
  }
  
  public void init(java.io.File dir, tut.model.Model m) {
    if (m != null) subject = m;
    else throw new RuntimeException("Subject to Observe cannot be null.");
    
    // setup the output file
    if (dir != null && dir.exists()) {
      try {
        String fileName = dir.getCanonicalPath() + java.io.File.separator
                + tut.ctrl.Batch.expName + "-"
                + m.getClass().getSimpleName()+".csv";
        outFile = new java.io.PrintWriter(new java.io.File(fileName));
      } catch (java.io.IOException ioe) { throw new RuntimeException(ioe); }
    }

    // write the output file header
    StringBuilder sb = new StringBuilder("Time, ");
    java.util.ListIterator<tut.model.Comp> cIt = m.comps.listIterator();
    while (true) {
      tut.model.Comp c = cIt.next();
      sb.append("Comp").append(c.id);
      if (cIt.hasNext()) sb.append(", ");
      else break;
    }
    outFile.println(sb.toString());
    
    System.out.print("Running:       ");
  }
  
  @Override
  public void step(sim.engine.SimState state) {
    if (!subject.finished) {
      StringBuilder sb = new StringBuilder(state.schedule.getTime()/subject.cyclePerTime+",");
      java.util.ListIterator<tut.model.Comp> cIt = subject.comps.listIterator();
      while (true) {
        sb.append(cIt.next().getConc());
        if (cIt.hasNext()) sb.append(", ");
        else break;
      }
      outFile.println(sb.toString()); outFile.flush();
    
      state.schedule.scheduleOnce(this, VIEW_ORDER);
    }
  }
}
