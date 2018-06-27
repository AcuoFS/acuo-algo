package com.acuo.algo.allocationApproaches

import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class AllocateUnderInsufficientOptimalAssetsSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "AllocateUnderInsufficientOptimalAssets" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('costScore_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('liquidityScore_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('pref_vec <- c(5,5)')
    eval('callInfo_df <- data.frame(\n' +
                      'id=c(\'mc1\',\'mc2\'),' +
                      'marginStatement=c(\'ms1\',\'ms1\'),' +
                      'callAmount=c(1000,2000))')
    eval('resource_df <- data.frame(\n' +
                      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
                      'qtyMin = c(5000,6000),\n' +
                      'minUnitValue = c(1,1))')
    eval('availAsset_df <- data.frame(\n' +
                      'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
                      'resource = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
                      'haircut = c(0,0,0),\n' +
                      'FXHaircut = c(0.08,0.08,0))')
    eval('minMoveValue <- 1000')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')
    eval('timeLimit <- 13')
    eval('ifNewAlloc <- TRUE')
    eval('result_mat <- AllocateUnderInsufficientOptimalAssets(configurations,costScore_mat,liquidityScore_mat,pref_vec,\n' +
      '                                                   callInfo_df,resource_df,availAsset_df,\n' +
      '                                                   minMoveValue,operLimitMs,fungible,timeLimit,\n' +
      '                                                   ifNewAlloc,list())')
    eval('print(result_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    // check allocation result
    that eval('result_mat[1,]'), equalTo(c(1087,0) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,2000) as SEXP)
  }
}
