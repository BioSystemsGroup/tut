/**
 * LinearGradient - Maps one interval, linearly to another.
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
 *                       (RefX-RefY)
 * ref = RefY + value * ------------- 
 *                       (ValX-ValY)
 * 
 * @author gepr
 */
public class LinearGradient extends Gradient {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger( LinearGradient.class );

  public LinearGradient(double[] p) {
    super(p);
  }
  public static double eval(double refX, double refY, double valX, double valY, double x) {
    double retVal = refX + x * (refY-refX)/(valY-valX);
    //log.debug(retVal+" = "+refX+" + "+x+" * ("+refY+"-"+refX+")/("+valY+"-"+valX+")");
    return retVal;
  }
}
