/**
 * Gradient - for now provides the explicit gradient used in the ISL
 * Later will provide a super class for various gradients with a function
 * pointer to the math.
 *
 * Copyright 2003-2014 - Regents of the University of California, San
 * Francisco.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package isl.util;

public abstract class Gradient {
  double[] params = null;
  public Gradient(double[] p) {
    params = p;
  }
  public double eval(double x) {
    double retVal = eval(params[0],params[1],params[2],params[3], x);
    return retVal;
  }
  public static double eval(double refX, double refY, double valX, double valY, double x) {
      return Double.NaN;
  }
}
