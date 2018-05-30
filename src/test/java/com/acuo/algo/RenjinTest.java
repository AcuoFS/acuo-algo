package com.acuo.algo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.renjin.sexp.DoubleVector;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@Ignore
public class RenjinTest extends EvalTestCase {

    @Before
    public void setUp() {
        eval("library(stats)");
        eval("library(\"com.acuo.collateral.acuo-algo\")");
    }

    @Test
    public void test() {
        // Sanity check
        DoubleVector result = (DoubleVector) eval("1+1");
        assertThat(result.length(), equalTo(1));
        assertThat(result.getElementAsDouble(0), equalTo(2.0));

        // Produce some output
        eval("print(rnorm(10))");
    }

    @Test
    public void testDataFramesToList() {
        eval("DfListFun <- function(df1,df2){ \n" +
                "df1$col1 <- 1 \n" +
                "df2$col2 <- 2 \n" +
                "return(list(df1=df1,df2=df2)) \n" +
                "}\n");

        eval("df1 <- data.frame(col1=rep(3,5),col2=rep(3,5))");
        eval("df2 <- data.frame(col1=rep(3,5),col2=rep(3,5))");

        eval("x <- DfListFun(df1, df2)");
        eval("print(x$df1)");

        assertThat(eval("x$df1$col1"), closeTo(c_i(1, 1, 1, 1, 1), 0.00001));
        assertThat(eval("x$df1$col2"), closeTo(c_i(3, 3, 3, 3, 3), 0.00001));
    }

    @Test
    public void testPasteFun1() {
        eval("x <- PasteFun1('a', 'b')");
        assertThat(eval("x"), equalTo(c("a_b")));
    }

}