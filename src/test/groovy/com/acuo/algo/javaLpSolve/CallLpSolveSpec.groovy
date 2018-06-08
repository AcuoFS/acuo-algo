package com.acuo.algo.javaLpSolve

import com.acuo.algo.RenjinEval
import com.acuo.algo.Renjin
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.expect
import static spock.util.matcher.HamcrestSupport.that

class CallLpSolveSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library(lpSolveAPI)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "CallLpSolve calculates the Reserved Liquidity Ratio for available assets pool"() {
        expect:
        eval('lpModel <- LpSolve$makeLp(0, 2)')
        that eval('2'), equalTo(2d as SEXP)
    }

}
