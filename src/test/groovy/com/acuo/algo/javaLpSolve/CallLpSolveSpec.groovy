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

  void "Try lpSolve"() {
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

  void "Try CallLpSolve in 5 decision variables scenario"() {
    given:
    // model: 5 decision variables
    // Objective: Min(0.01 x1 + 0.01 x2 + 0.02 x3)
    // Constraints
    // x1 + x2 <= 5000
    // x3 <= 4000
    // x1 + x3 >= 1000
    // x2 >= 2000
    // x1 + x2 - 5000x4 <= 0
    // x1 + x2 - x4 >= 0
    // x3 - 6000x5 <= 0
    // x3 - x5 >= 0

    eval('configurations <- list(debugMode=FALSE)')
    eval('lpObj_vec <- c(0.01,0.01,0.02,0,0)')
    eval('lpCon_mat <- matrix(c(1,1,0,0,0,' +
      '                         0,0,1,0,0,' +
      '                         1,0,1,0,0,' +
      '                         0,2,0,0,0,' +
      '                         1,1,0,-5000,0,' +
      '                         1,1,0,-1,0,' +
      '                         0,0,1,0,-6000,' +
      '                         0,0,1,0,-1),' +
      'nrow = 8, byrow = T)')
    eval('lpDir_vec <- c(\'<=\',\'<=\',\'>=\',\'>=\',\'<=\',\'>=\',\'<=\',\'>=\')')
    eval('lpRhs_vec <- c(5000,4000,1000,2000,0,0,0,0)')
    eval('lpType_vec <- rep(\'integer\',5)')
    eval('lpKind_vec <- rep(\'semi-continuous\',5)')
    eval('lpLowerBound_vec <- rep(0,5)')
    eval('lpUpperBound_vec <- c(5000,5000,6000,1,1)')
    eval('lpBranchMode_vec <- rep(\'auto\',5)')
    eval('lpGuessBasis_vec <- rep(0,5)')
    eval('presolve <- c(\'none\')')
    eval('timeout <- c(13)')

    when:
    eval('result <- CallLpSolve(configurations,lpObj_vec,lpCon_mat,lpDir_vec,lpRhs_vec,\n' +
      '                        lpType_vec,lpKind_vec,lpLowerBound_vec,lpUpperBound_vec,lpBranchMode_vec,\n' +
      '                        lpGuessBasis_vec,\n' +
      '                        presolve,timeout)')
    eval('print(result)')
    then:
    that eval('result$solverSolution_vec'), equalTo(c(1000,1000,0,1,0) as SEXP)
    that eval('result$resultStatus + 0'), equalTo(c(0) as SEXP)
    // eval('result$solverObjValue'): 19.999999999999996
    that eval('round(result$solverObjValue,4)'), equalTo(c(20) as SEXP)
  }

}
