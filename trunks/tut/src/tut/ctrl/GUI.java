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

public class GUI extends sim.display.GUIState {
  Batch batch = null;
  sim.display.Console console = new sim.display.Console(this);
  
  public GUI(Batch b) {
    super(b.state);
    batch = b;
    Thread.currentThread().setPriority(1);
    console.setVisible(true);
  }
  @Override
  public void start() {
    super.start();
    console.setWhenShouldPause((long)batch.getMaxCycle());
    batch.load();
  }
  public void load() {
    batch.load();
  }
  @Override
  public void finish() {
    super.finish();
    batch.finish();
  }
  
  public void go() {
    while (console.isShowing()) ; // wait till the frame disappears.
    
  }
}
