package com.acuo.algo

import org.apache.commons.math.complex.Complex
import org.renjin.eval.EvalException
import org.renjin.eval.Session
import org.renjin.eval.SessionBuilder
import org.renjin.invoke.reflection.converters.Converters
import org.renjin.parser.RParser
import org.renjin.repackaged.guava.collect.Lists
import org.renjin.repackaged.guava.primitives.UnsignedBytes
import org.renjin.sexp.*

trait RenjinEval {

    public static final SEXP NULL = Null.INSTANCE
    public static final SEXP CHARACTER_0 = new StringArrayVector()
    public static final SEXP DOUBLE_0 = new DoubleArrayVector()

    private Session session = new SessionBuilder().build()

    SEXP eval(String source) {
        if (!source.endsWith(";") && !source.endsWith("\n")) {
            source = source + "\n"
        }
        SEXP exp = RParser.parseSource(source)
        try {
            return session.getTopLevelContext().evaluate(exp)
        } catch (EvalException e) {
            e.printRStackTrace(System.out)
            throw new RuntimeException(e)
        }
    }

    Complex complex(double real) {
        return new Complex(real, 0)
    }

    Complex complex(double real, double imaginary) {
        return new Complex(real, imaginary)
    }

    Vector c(Complex... values) {
        return new ComplexArrayVector(values)
    }

    Vector c(boolean ... values) {
        return new LogicalArrayVector(values)
    }

    Vector c(Logical... values) {
        return new LogicalArrayVector(values)
    }

    Vector c(String... values) {
        return new StringArrayVector(values)
    }

    Vector c(double ... values) {
        return new DoubleArrayVector(values)
    }

    Vector c_raw(int ... values) {
        byte[] bytes = new byte[values.length]
        for (int i = 0; i != values.length; ++i) {
            bytes[i] = UnsignedBytes.checkedCast(values[i])
        }
        return new RawVector(bytes)
    }

    Vector c_i(int ... values) {
        return new IntArrayVector(values)
    }

    Vector list(Object... values) {
        ListVector.Builder builder = ListVector.newBuilder()
        for (Object obj : values) {
            builder.add(Converters.fromJava(obj))
        }
        return builder.build()
    }

    SEXP expression(Object... values) {
        List<SEXP> builder = Lists.newArrayList()
        for (Object obj : values) {
            builder.add(Converters.fromJava(obj))
        }
        return new ExpressionVector(builder)
    }

    SEXP symbol(String name) {
        return Symbol.get(name)
    }

    double[] row(double ... d) {
        return d
    }

    SEXP matrix(double[] ... rows) {
        DoubleArrayVector.Builder matrix = new DoubleArrayVector.Builder()
        int nrows = rows.length
        int ncols = rows[0].length

        for (int j = 0; j != ncols; ++j) {
            for (int i = 0; i != nrows; ++i) {
                matrix.add(rows[i][j])
            }
        }
        return matrix.build()
    }

    SEXP matrix(Complex[] ... rows) {
        ComplexArrayVector.Builder matrix = new ComplexArrayVector.Builder()
        int nrows = rows.length
        int ncols = rows[0].length

        for (int j = 0; j != ncols; ++j) {
            for (int i = 0; i != nrows; ++i) {
                matrix.add(rows[i][j])
            }
        }
        return matrix.build()
    }
}