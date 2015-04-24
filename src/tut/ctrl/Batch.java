/*
 * Copyright 2015 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package tut.ctrl;

public class Batch {
  long cycleLimit = -Integer.MAX_VALUE;
  long seed = -Integer.MAX_VALUE;
  Parameters params = null;
  public sim.engine.SimState state = null;
  public Batch(tut.ctrl.Parameters p) {
    if (p != null) params = p;
    if (params.cycleLimit > 0) cycleLimit = params.cycleLimit;
    if (params.seed > 0) seed = params.seed;
    state = new sim.engine.SimState(seed);
  }
  public final void load() {
    // setup output target
    setupOutput(params);

    cycleLimit = params.cycleLimit;
    tut.model.Model m = new tut.model.Model(params);
    m.init(state);
    state.schedule.scheduleOnce(m, tut.model.Model.MODEL_ORDER);
    
    tut.view.Observer obs = new tut.view.Observer(params);
    obs.init(state, m);
    state.schedule.scheduleOnce(obs, tut.view.Observer.VIEW_ORDER);
  }
  
  public void go() {
    while (state.schedule.getSteps() < cycleLimit) {
      state.schedule.step(state);
    }
    //log("Batch.go() - Done!");
  }
  public void finish() {
    log.close();
    System.out.println("Finished.  Wait for buffered output.");
  }

  private static java.io.PrintWriter log = null;
  public static void log(String entry) { log.println(entry); log.flush(); }
  private static final String logName="output.csv";
  static void setupOutput(Parameters p) {
    String out_dir = null;
    final String DATE_FORMAT = "yyyy-MM-dd-HHmmss";
    final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
    StringBuffer date_s = new StringBuffer("");
    sdf.format(new java.util.Date(System.currentTimeMillis()), date_s,
            new java.text.FieldPosition(0));
    out_dir = date_s.toString();

    // create a directory using the current date and time
    java.io.File dir = null;
    dir = new java.io.File(out_dir);
    if (!dir.exists()) {
      dir.mkdir();
    }
    String dirPath = null;
    try {
      dirPath = dir.getCanonicalPath() + java.io.File.separator;
    } catch (java.io.IOException ioe) {
      throw new RuntimeException("Couldn't " + dir + ".getCanonicalPath().");
    }

    // write parameters
    try {
      java.io.FileWriter fw = new java.io.FileWriter(new java.io.File(out_dir 
              + java.io.File.separator 
              + "parameters-" + tut.Main.MAJOR_VERSION + "-" + System.currentTimeMillis()+".json"));
      p.version = tut.Main.MAJOR_VERSION+" Subversion"+tut.Main.MINOR_VERSION;
      fw.write(p.describe());
      fw.close();
    } catch (java.io.IOException ioe) {
      System.exit(-1);
    }

    
    // initialize the output for run-time measurements
    try {
      log = new java.io.PrintWriter(new java.io.File(out_dir
              + java.io.File.separator + logName));
    } catch (java.io.FileNotFoundException fnfe) {
      throw new RuntimeException("Couldn't open " + out_dir
              + java.io.File.separator + logName, fnfe);
    }

  }

}
