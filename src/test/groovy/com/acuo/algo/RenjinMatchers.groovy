package com.acuo.algo

import org.apache.commons.math.complex.Complex
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.renjin.sexp.ComplexVector
import org.renjin.sexp.DoubleArrayVector
import org.renjin.sexp.SEXP
import org.renjin.sexp.Vector

class RenjinMatchers {

    static Matcher<SEXP> closeTo(final SEXP expectedSexp, final double epsilon) {
        final Vector expected = (Vector) expectedSexp
        return new TypeSafeMatcher<SEXP>() {

            @Override
            void describeTo(Description d) {
                d.appendText(expectedSexp.toString())
            }

            @Override
            boolean matchesSafely(SEXP item) {
                if (!(item instanceof Vector)) {
                    return false
                }
                Vector vector = (Vector) item
                if (vector.length() != expected.length()) {
                    return false
                }
                for (int i = 0; i != expected.length(); ++i) {
                    if (expected.isElementNA(i) != vector.isElementNA(i)) {
                        return false
                    }
                    if (!expected.isElementNA(i)) {
                        double delta = expected.getElementAsDouble(i) - vector.getElementAsDouble(i)
                        if (Double.isNaN(delta) || Math.abs(delta) > epsilon) {
                            return false
                        }
                    }
                }
                return true
            }
        }
    }

    static Matcher<SEXP> closeTo(final Complex expectedValue, final double epsilon) {
        return new TypeSafeMatcher<SEXP>() {
            @Override
            boolean matchesSafely(SEXP sexp) {
                if (!(sexp instanceof ComplexVector)) {
                    return false
                }
                if (sexp.length() != 1) {
                    return false
                }
                ComplexVector vector = (ComplexVector) sexp
                Complex value = vector.getElementAsComplex(0)
                double realDelta = Math.abs(value.getReal() - expectedValue.getReal())
                if (Double.isNaN(realDelta) || realDelta > epsilon) {
                    return false
                }
                double imaginaryDelta = Math.abs(value.getImaginary() - expectedValue.getImaginary())
                if (Double.isNaN(imaginaryDelta) || imaginaryDelta > epsilon) {
                    return false
                }
                return true
            }

            @Override
            void describeTo(Description description) {
                description.appendText("complex value close to ").appendValue(expectedValue)
            }
        }
    }

    // otherwise this won't get resovled
    static Matcher<Double> closeTo(double d, double epsilon) {
        return Matchers.closeTo(d, epsilon)
    }

    static Matcher<Double> closeTo(int d, double epsilon) {
        return Matchers.closeTo((double) d, epsilon)
    }

    /**
     * Creates a matcher that matches if the examined object is identical to the expected value. The Vector's
     * attributes, if any, are ignored.
     */
    static Matcher<SEXP> identicalTo(SEXP value) {
        return Matchers.equalTo(value)
    }


    /**
     * Creates a matcher that matches if the examined object is a double vector that has identical elements. The
     * Vector's attributes, if any, are ignored.
     */
    static Matcher<SEXP> elementsIdenticalTo(double... elements) {
        return elementsIdenticalTo(new DoubleArrayVector(elements))
    }


    /**
     * Creates a matcher that matches if the examined object is a Vector and has identical elements. The Vector's
     * attributes, if any, are ignored.
     */
    static Matcher<SEXP> elementsIdenticalTo(SEXP expected) {
        return elementsIdenticalTo((Vector) expected)
    }


    /**
     * Creates a matcher that matches if the examined object is a Vector and has identical elements. The Vector's
     * attributes, if any, are ignored.
     */
    static Matcher<SEXP> elementsIdenticalTo(final Vector expected) {
        return new TypeSafeMatcher<SEXP>() {
            @Override
            boolean matchesSafely(SEXP actual) {
                Vector v1 = (Vector) actual
                Vector v2 = expected
                if (v1.length() != v2.length()) {
                    return false
                }
                if (!v1.getVectorType().equals(v2.getVectorType())) {
                    return false
                }
                Vector.Type vectorType = v1.getVectorType()
                for (int i = 0; i != v1.length(); ++i) {
                    if (!vectorType.elementsIdentical(v1, i, v2, i)) {
                        return false
                    }
                }
                return true
            }

            @Override
            void describeTo(Description description) {
                description.appendValue(expected.toString())
            }
        }
    }
}