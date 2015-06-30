/*
 * Copyright 2015 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package tut.model;

import sim.engine.Steppable;
import sim.engine.SimState;

public class SlaveAgent implements Steppable {
  java.util.ArrayList<Steppable> actions = new java.util.ArrayList<>();

  @Override
  public void step(SimState s) {
    shuffle(actions,s.random).stream().forEach((a) -> {
      ((Steppable)a).step(s);
    });
  }
  
  /** Shuffles (randomizes the order of) the ArrayList
      -- stolen from MASON 17 implementation for sim.util.Bag
   * @param al arraylist to shuffle
   * @param rng pRNG to use for the shuffling
   * @return pRNG shuffled arraylist
   */
  public static java.util.ArrayList shuffle(java.util.ArrayList al, ec.util.MersenneTwisterFast rng) {
      Object[] objs = al.toArray();
      int numObjs = objs.length;
      Object obj;
      int rand;

      for (int x = numObjs - 1; x >= 1; x--) {
          rand = rng.nextInt(x + 1);
          obj = objs[x];
          objs[x] = objs[rand];
          objs[rand] = obj;
      }
      java.util.ArrayList objs_al = new java.util.ArrayList<>(objs.length);
      for (int x = 0; x < objs.length; x++) {
          objs_al.add(objs[x]);
      }
      return objs_al;
  }
}
