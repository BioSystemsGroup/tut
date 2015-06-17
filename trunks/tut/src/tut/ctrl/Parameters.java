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
import java.util.Objects;

public class Parameters {
  String version = "";
  public Map<String,Number> batch = new HashMap<>(2);
  public Map<String,Number> tight = new HashMap<>(6);
  public Map<String,Number> loose = new HashMap<>(11);
  public Map<String,Map<Edge,Double>> lRates = new HashMap<>();
  public Map<String,Number> looseDyn = new HashMap<>(1);
  public Map<String,Map<Edge,Double>> ldRates = new HashMap<>();
  
  public Parameters() {
    batch.put("seed",-Long.MAX_VALUE);
    
    tight.put("timeLimit",-Long.MAX_VALUE);
    tight.put("cyclePerTime",Double.MIN_NORMAL);
    tight.put("doseTime", Double.MIN_NORMAL);
    tight.put("dose",Double.MIN_NORMAL);
    tight.put("vc",Double.MIN_NORMAL);
    tight.put("k_a",Double.MIN_NORMAL);
    tight.put("k_10",Double.MIN_NORMAL);
    tight.put("k_12",Double.MIN_NORMAL);
    tight.put("k_21",Double.MIN_NORMAL);
    
    loose.put("timeLimit",-Long.MAX_VALUE);
    loose.put("cyclePerTime",Double.MIN_NORMAL);
    loose.put("doseTime", Double.MIN_NORMAL);
    loose.put("dose",Double.MIN_NORMAL);
    loose.put("vc",Double.MIN_NORMAL);
    loose.put("vp",Double.MIN_NORMAL);
    //loose.put("src2cent",Double.MIN_NORMAL);
    //loose.put("cent2peri",Double.MIN_NORMAL);
    //loose.put("peri2cent",Double.MIN_NORMAL);
    //loose.put("cent2sink",Double.MIN_NORMAL);
    lRates.put("Drug", specNet());

    looseDyn.put("morbidity",Double.MIN_NORMAL);
    looseDyn.put("morb2mark", Double.MIN_NORMAL);
    looseDyn.put("mark2pain", Double.MIN_NORMAL);
    looseDyn.put("drug2pain", Double.MIN_NORMAL);
    //looseDyn.put("src2centM",Double.MIN_NORMAL);
    //looseDyn.put("cent2periM",Double.MIN_NORMAL);
    //looseDyn.put("peri2centM",Double.MIN_NORMAL);
    //looseDyn.put("cent2sinkM",Double.MIN_NORMAL);
    ldRates.put("Marker", specNet());
  }
  
  public static class Edge {
    String from = "s";
    String to = "t";
    public Edge() {}
    public Edge(String s, String t) {
      from = s; to = t;
    }

    @Override
    public int hashCode() {
      return from.hashCode()+to.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Edge other = (Edge) obj;
      if (!Objects.equals(this.from, other.from)) {
        return false;
      }
      if (!Objects.equals(this.to, other.to)) {
        return false;
      }
      return true;
    }
    
  }
  
  private Map<Edge,Double> specNet() {
    Map<Edge,Double> m = new HashMap<>(4);
    m.put(new Edge("source","central"), Double.MIN_NORMAL);
    m.put(new Edge("central","periph"), Double.MIN_NORMAL);
    m.put(new Edge("periph","central") , Double.MIN_NORMAL);
    m.put(new Edge("central","sink"), Double.MIN_NORMAL);
    return m;
  }
  
  public String describe() {
    com.owlike.genson.Genson g = new com.owlike.genson.Genson();
    String json = g.serialize(this);
    return json;
  }
  public static Parameters readOneOfYou(String json) {
    com.owlike.genson.Genson g = new com.owlike.genson.Genson();
    Parameters p = g.deserialize(json, Parameters.class);
    return p;
  }
  
  public static void main(String[] args) throws java.io.FileNotFoundException {
    java.io.File f = new java.io.File(args[0]);
    String json = new java.util.Scanner(f).useDelimiter("\\A").next();
    Parameters p = readOneOfYou(json);
    System.out.println(p.describe());
  }
  
}
