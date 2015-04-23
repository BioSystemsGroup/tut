/*
 * Copyright 2015 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package tut;

public class Main {
  public static final String MAJOR_VERSION = "TUT-v0.1";
  public static final String MINOR_VERSION = "$Id$";
  public static void main(String[] args) {
    // run the GUI?
    boolean useGUI = keyExists("-gui",args);
    tut.ctrl.GUI gui = null;

    // run with custom parameters?
    java.io.InputStream pf = null;
    String fileName = "/parameters.json";
    if (keyExists("-pf",args)) {
      fileName = argumentForKey("-pf", args, 0);
      try {
        pf = new java.io.FileInputStream(new java.io.File(fileName));
      } catch (java.io.FileNotFoundException fnfe) { 
        throw new RuntimeException(fnfe);
      }
    } else {
      pf = Main.class.getResourceAsStream(fileName);
      if (pf == null) throw new RuntimeException("Could not find default parameters file in CLASSPATH.");
    }
    tut.ctrl.Parameters p = tut.ctrl.Parameters.readOneOfYou(pf);
    if (keyExists("-wpf",args)) p.writeYourself();
    
    tut.ctrl.Batch b = new tut.ctrl.Batch(p);
    if (useGUI) {
      gui = new tut.ctrl.GUI(b);
      gui.go();
    } else {
      b.load();
      b.go();
    }
  }
  
  public static boolean keyExists(String key, String[] args) {
    for (String arg : args) if (arg.equalsIgnoreCase(key)) return true;
    return false;
  }

  public static String argumentForKey(String key, String[] args, int startingAt) {
    for (int x = 0; x < args.length - 1; x++) // key can't be the last string
      if (args[x].equalsIgnoreCase(key)) return args[x + 1];
    return null;
  }

}
