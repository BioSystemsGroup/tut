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

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    boolean useGUI = keyExists("-gui",args);
    tut.ctrl.GUI gui = null;
    tut.ctrl.Parameters p = tut.ctrl.Parameters.readOneOfYou();
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
