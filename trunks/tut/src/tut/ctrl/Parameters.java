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

import java.util.Map;
import java.util.HashMap;

public class Parameters {
  String version = "";
  public Map<String,Number> batch = new HashMap<>(2);
  public Map<String,Number> tight = new HashMap<>(6);
  public Map<String,Number> loose = new HashMap<>();
  
  public Parameters() {
    batch.put("seed",-Long.MAX_VALUE);
    
    tight.put("timeLimit",-Long.MAX_VALUE);
    tight.put("cyclePerTime",Double.NaN);
    tight.put("dose",Double.NaN);
    tight.put("vc",Double.NaN);
    tight.put("k_a",Double.NaN);
    tight.put("k_10",Double.NaN);
    tight.put("k_12",Double.NaN);
    tight.put("k_21",Double.NaN);
    
    loose.put("timeLimit",-Long.MAX_VALUE);
    loose.put("cyclePerTime",Double.NaN);
    loose.put("dose",Double.NaN);
    loose.put("vc",Double.NaN);
    loose.put("vp",Double.NaN);
    loose.put("src2cent",Double.NaN);
    loose.put("cent2peri",Double.NaN);
    loose.put("peri2cent",Double.NaN);
    loose.put("cent2sink",Double.NaN);
  }

  public static Parameters readOneOfYou(java.io.InputStream is) {
    Parameters p = null;
    com.google.gson.Gson gson = new com.google.gson.Gson();
    String json = null;
    json = new java.util.Scanner(is).useDelimiter("\\A").next();
    if (json != null) p = gson.fromJson (json, Parameters.class); 
    return p;
  }
  
  public String describe() {
    com.google.gson.Gson gson = new com.google.gson.Gson();
    return gson.toJson(this);
  }
}
