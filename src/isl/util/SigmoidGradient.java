/**
 * SigmoidGradient - Maps one interval to another according to a sigmoid.
 *
 * Copyright 2013-2014 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package isl.util;

/**
 *
 * @author gepr
 */
public class SigmoidGradient extends LinearGradient {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger( LinearGradient.class );

  public SigmoidGradient(double[] p) {
    super(p);
  }
  /**
   * 
   * @param refX = startProb
   * @param refY = finishProb
   * @param valX = start
   * @param valY = finish
   * @param x = position
   * @return 
   */
  public static double eval(double refX, double refY, double valX, double valY, double x) {
    double val = LinearGradient.eval(10.0, 0.0, valX, valY, x);
    //log.debug("SigmoidGradient: "+val+" = 0.0 + "+x+" * (10.0-0.0)/("+valY+"-"+valX+")");
    double retVal = refX + (refY-refX)/(1.0+StrictMath.exp(val-5.0));
    //log.debug("SigmoidGradient: "+retVal+" = "+refX+" + ("+refY+"-"+refX+")/(1.0+e^("+val+"-5.0))");
    return retVal;
  }
  public static void main(String[] args) {
    log.debug("min");
    for (double i=1.0; i<=100.0 ; i++) {
        log.debug(i+" => "+ SigmoidGradient.eval(0.0, 10.0, 0.0, 100.0, i));
    }
  }
  

}
