/*
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-2018 BeDataDriven Groep B.V. and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, a copy is available at
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */
package com.acuo.algo;

import org.apache.commons.math.complex.Complex;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.renjin.eval.EvalException;
import org.renjin.eval.Session;
import org.renjin.eval.SessionBuilder;
import org.renjin.invoke.reflection.converters.Converters;
import org.renjin.parser.RParser;
import org.renjin.repackaged.guava.collect.Lists;
import org.renjin.repackaged.guava.primitives.UnsignedBytes;
import org.renjin.sexp.ComplexArrayVector;
import org.renjin.sexp.ComplexVector;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.ExpressionVector;
import org.renjin.sexp.IntArrayVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Logical;
import org.renjin.sexp.LogicalArrayVector;
import org.renjin.sexp.Null;
import org.renjin.sexp.RawVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;
import org.renjin.sexp.Symbol;
import org.renjin.sexp.Vector;

import java.util.List;

public abstract class EvalTestCase {

    public static final SEXP NULL = Null.INSTANCE;
    public static final SEXP CHARACTER_0 = new StringArrayVector();
    public static final SEXP DOUBLE_0 = new DoubleArrayVector();

    private Session session = new SessionBuilder().build();

    protected SEXP eval(String source) {
        if (!source.endsWith(";") && !source.endsWith("\n")) {
            source = source + "\n";
        }
        SEXP exp = RParser.parseSource(source);
        try {
            return session.getTopLevelContext().evaluate(exp);
        } catch (EvalException e) {
            e.printRStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }

    protected Complex complex(double real) {
        return new Complex(real, 0);
    }

    protected Complex complex(double real, double imaginary) {
        return new Complex(real, imaginary);
    }

    protected Vector c(Complex... values) {
        return new ComplexArrayVector(values);
    }

    protected Vector c(boolean... values) {
        return new LogicalArrayVector(values);
    }

    protected Vector c(Logical... values) {
        return new LogicalArrayVector(values);
    }

    protected Vector c(String... values) {
        return new StringArrayVector(values);
    }

    protected Vector c(double... values) {
        return new DoubleArrayVector(values);
    }

    protected Vector c_raw(int... values) {
        byte[] bytes = new byte[values.length];
        for (int i = 0; i != values.length; ++i) {
            bytes[i] = UnsignedBytes.checkedCast(values[i]);
        }
        return new RawVector(bytes);
    }

    protected Vector c_i(int... values) {
        return new IntArrayVector(values);
    }

    protected Vector list(Object... values) {
        ListVector.Builder builder = ListVector.newBuilder();
        for (Object obj : values) {
            builder.add(Converters.fromJava(obj));
        }
        return builder.build();
    }

    protected SEXP expression(Object... values) {
        List<SEXP> builder = Lists.newArrayList();
        for (Object obj : values) {
            builder.add(Converters.fromJava(obj));
        }
        return new ExpressionVector(builder);
    }

    protected SEXP symbol(String name) {
        return Symbol.get(name);
    }

    protected Matcher<SEXP> closeTo(final SEXP expectedSexp, final double epsilon) {
        final Vector expected = (Vector) expectedSexp;
        return new TypeSafeMatcher<SEXP>() {

            @Override
            public void describeTo(Description d) {
                d.appendText(expectedSexp.toString());
            }

            @Override
            public boolean matchesSafely(SEXP item) {
                if (!(item instanceof Vector)) {
                    return false;
                }
                Vector vector = (Vector) item;
                if (vector.length() != expected.length()) {
                    return false;
                }
                for (int i = 0; i != expected.length(); ++i) {
                    if (expected.isElementNA(i) != vector.isElementNA(i)) {
                        return false;
                    }
                    if (!expected.isElementNA(i)) {
                        double delta = expected.getElementAsDouble(i) - vector.getElementAsDouble(i);
                        if (Double.isNaN(delta) || Math.abs(delta) > epsilon) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };
    }

    protected Matcher<SEXP> closeTo(final Complex expectedValue, final double epsilon) {
        return new TypeSafeMatcher<SEXP>() {
            @Override
            protected boolean matchesSafely(SEXP sexp) {
                if (!(sexp instanceof ComplexVector)) {
                    return false;
                }
                if (sexp.length() != 1) {
                    return false;
                }
                ComplexVector vector = (ComplexVector) sexp;
                Complex value = vector.getElementAsComplex(0);
                double realDelta = Math.abs(value.getReal() - expectedValue.getReal());
                if (Double.isNaN(realDelta) || realDelta > epsilon) {
                    return false;
                }
                double imaginaryDelta = Math.abs(value.getImaginary() - expectedValue.getImaginary());
                if (Double.isNaN(imaginaryDelta) || imaginaryDelta > epsilon) {
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("complex value close to ").appendValue(expectedValue);
            }
        };
    }

    // otherwise this won't get resovled
    protected Matcher<Double> closeTo(double d, double epsilon) {
        return Matchers.closeTo(d, epsilon);
    }

    protected Matcher<Double> closeTo(int d, double epsilon) {
        return Matchers.closeTo((double) d, epsilon);
    }

    protected double[] row(double... d) {
        return d;
    }

    protected SEXP matrix(double[]... rows) {
        DoubleArrayVector.Builder matrix = new DoubleArrayVector.Builder();
        int nrows = rows.length;
        int ncols = rows[0].length;

        for (int j = 0; j != ncols; ++j) {
            for (int i = 0; i != nrows; ++i) {
                matrix.add(rows[i][j]);
            }
        }
        return matrix.build();
    }

    protected SEXP matrix(Complex[]... rows) {
        ComplexArrayVector.Builder matrix = new ComplexArrayVector.Builder();
        int nrows = rows.length;
        int ncols = rows[0].length;

        for (int j = 0; j != ncols; ++j) {
            for (int i = 0; i != nrows; ++i) {
                matrix.add(rows[i][j]);
            }
        }
        return matrix.build();
    }

    /**
     * Creates a matcher that matches if the examined object is identical to the expected value. The Vector's
     * attributes, if any, are ignored.
     */
    protected Matcher<SEXP> identicalTo(SEXP value) {
        return Matchers.equalTo(value);
    }


    /**
     * Creates a matcher that matches if the examined object is a double vector that has identical elements. The
     * Vector's attributes, if any, are ignored.
     */
    protected Matcher<SEXP> elementsIdenticalTo(double... elements) {
        return elementsIdenticalTo(new DoubleArrayVector(elements));
    }


    /**
     * Creates a matcher that matches if the examined object is a Vector and has identical elements. The Vector's
     * attributes, if any, are ignored.
     */
    protected Matcher<SEXP> elementsIdenticalTo(SEXP expected) {
        return elementsIdenticalTo((Vector) expected);
    }


    /**
     * Creates a matcher that matches if the examined object is a Vector and has identical elements. The Vector's
     * attributes, if any, are ignored.
     */
    protected Matcher<SEXP> elementsIdenticalTo(final Vector expected) {
        return new TypeSafeMatcher<SEXP>() {
            @Override
            public boolean matchesSafely(SEXP actual) {
                Vector v1 = (Vector) actual;
                Vector v2 = expected;
                if (v1.length() != v2.length()) {
                    return false;
                }
                if (!v1.getVectorType().equals(v2.getVectorType())) {
                    return false;
                }
                Vector.Type vectorType = v1.getVectorType();
                for (int i = 0; i != v1.length(); ++i) {
                    if (!vectorType.elementsIdentical(v1, i, v2, i)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(expected.toString());
            }
        };
    }

}