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

public class Parameters {
  public long seed = -Long.MAX_VALUE;
  public long cycleLimit = -Long.MAX_VALUE;
  public double k_a = Double.NaN, k_10 = Double.NaN, k_12 = Double.NaN, k_21 = Double.NaN;
  
  public Parameters() {
  }
  
  public void writeYourself() {
    com.google.gson.Gson gson = new com.google.gson.Gson();
    try {
      java.io.FileWriter fw = new java.io.FileWriter(new java.io.File(tut.Main.VERSION+"-parameters-"+System.currentTimeMillis()+".json"));
      fw.write(gson.toJson(this));
      fw.close();
    } catch (java.io.IOException ioe) {
      System.exit(-1);
    }
  }
  public static Parameters readOneOfYou(java.io.InputStream is) {
    Parameters p = null;
    com.google.gson.Gson gson = new com.google.gson.Gson();
    String json = null;
    json = new java.util.Scanner(is).useDelimiter("\\A").next();
    if (json != null) p = gson.fromJson (json, Parameters.class); 
    return p;
  }
}
