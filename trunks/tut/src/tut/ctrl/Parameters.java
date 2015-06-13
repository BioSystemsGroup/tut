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
  public Map<String,Number> loose = new HashMap<>(11);
  public Map<String,Number> looseDyn = new HashMap<>(1);
  
  public Parameters() {
    batch.put("seed",-Long.MAX_VALUE);
    
    tight.put("timeLimit",-Long.MAX_VALUE);
    tight.put("cyclePerTime",Double.NaN);
    tight.put("doseTime", Double.NaN);
    tight.put("dose",Double.NaN);
    tight.put("vc",Double.NaN);
    tight.put("k_a",Double.NaN);
    tight.put("k_10",Double.NaN);
    tight.put("k_12",Double.NaN);
    tight.put("k_21",Double.NaN);
    
    loose.put("timeLimit",-Long.MAX_VALUE);
    loose.put("cyclePerTime",Double.NaN);
    loose.put("doseTime", Double.NaN);
    loose.put("dose",Double.NaN);
    loose.put("vc",Double.NaN);
    loose.put("vp",Double.NaN);
    loose.put("src2cent",Double.NaN);
    loose.put("cent2peri",Double.NaN);
    loose.put("peri2cent",Double.NaN);
    loose.put("cent2sink",Double.NaN);
    
    looseDyn.put("morbidity",Double.NaN);
    looseDyn.put("morb2mark", Double.NaN);
    looseDyn.put("mark2pain", Double.NaN);
    looseDyn.put("drug2pain", Double.NaN);
  }

  private boolean test(String name, Map<String,Number> ref, Map<String,Number> map) {
    java.util.Optional<Map.Entry<String, Number>> broken = ref.entrySet().stream().filter(me -> map.get(me.getKey()) == null).findAny();
    if (broken.isPresent())
      tut.ctrl.Batch.log(name+"."+broken.get().getKey()+" not found in parameter file.");
    return broken.isPresent();
  }
  private boolean test() {
    boolean retVal = true;
    Parameters testP = new Parameters();
    if (test("batch", testP.batch, batch)) retVal = false;
    if (test("tight", testP.tight, tight)) retVal = false;
    if (test("loose", testP.loose, loose)) retVal = false;
    if (test("looseDyn", testP.looseDyn, looseDyn)) retVal = false;
    return retVal;
  }

  public static Parameters readOneOfYou(String json) {
    Parameters p = null;
    com.google.gson.Gson gson = new com.google.gson.Gson();
    if (json != null) p = gson.fromJson (json, Parameters.class); 
    if (p != null) p.test();
    else throw new RuntimeException("Problem loading parameters. Gson returned null.");
    return p;
  }
  public static Parameters readOneOfYou(java.io.InputStream is) {
    String json = new java.util.Scanner(is).useDelimiter("\\A").next();
    Parameters p = readOneOfYou(json);
    return p;
  }
  
  public String describe() {
    com.google.gson.Gson gson = new com.google.gson.Gson();
    return gson.toJson(this);
  }
}
