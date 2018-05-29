package com.acuo.algo;

import org.junit.Test;
import org.renjin.sexp.DoubleVector;

import static org.assertj.core.api.Assertions.assertThat;

public class RenjinTest extends EvalTestCase {

    @Test
    public void test() {
        // Sanity check
        DoubleVector result = (DoubleVector) eval("1+1");
        assertThat(result.length()).isEqualTo(1);
        assertThat(result.getElementAsDouble(0)).isEqualTo(2.0);

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
        eval("print(x[1])");
    }

}