package com.acuo.algo.javaLpSolve

import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class CallLpSolveSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "CallLpSolve calculates the Reserved Liquidity Ratio for available assets pool"() {
    given:
    eval('lpModel <- LpSolve$makeLp(0L, 4L)')
    eval('lpModel$strAddConstraint("3 2 2 1", 1L, 4)')
    eval('lpModel$strAddConstraint("0 4 3 1", 2L, 3)')
    eval('lpModel$strSetObjFn("2 3 -2 3")')
    eval('lpModel$solve()')

    when:
    SEXP obj = (SEXP) eval('obj <- lpModel$getObjective()')
    SEXP res = (SEXP) eval('res <- lpModel$getPtrVariables()')

    then:
    that obj, equalTo(c(-4.0d) as SEXP)
    that res, equalTo(c(0,0,2,0) as SEXP)

    cleanup:
    eval('lpModel$deleteLp()')
  }

}
