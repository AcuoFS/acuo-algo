package com.acuo.algo

import org.renjin.sexp.DoubleVector
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class RenjinSpec extends Specification implements RenjinEval {

    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }

    void "sanity check"() {
        // Sanity check
        when:
        DoubleVector result = (DoubleVector) eval("1+1")

        then:
        that result.length(), equalTo(1)
        that result.getElementAsDouble(0), equalTo(2.0d)

        // Produce some output
        eval("print(rnorm(10))")
    }

    void "data frames to list"() {
        when:
        eval('DfListFun <- function(df1,df2){ \n' +
             'df1$col1 <- 1 \n' +
             'df2$col2 <- 2 \n' +
             'return(list(df1=df1,df2=df2)) \n' +
             '}\n')

        eval('df1 <- data.frame(col1=rep(3,5),col2=rep(3,5))')
        eval('df2 <- data.frame(col1=rep(3,5),col2=rep(3,5))')

        eval('x <- DfListFun(df1, df2)')
        eval('print(x$df1)')

        then:
        that eval('x$df1$col1'), RenjinMatchers.closeTo(c_i(1, 1, 1, 1, 1), 0.00001)
        that eval('x$df1$col2'), RenjinMatchers.closeTo(c_i(3, 3, 3, 3, 3), 0.00001)
    }

    void "PasteFun1 concatenation of two sting"() {
        expect:
        eval("x <- PasteFun1('a', 'b')")
        that eval("x"), equalTo(c("a_b") as SEXP)
    }
}
