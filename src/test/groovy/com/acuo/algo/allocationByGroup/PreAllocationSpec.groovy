package com.acuo.algo.allocationByGroup

import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class PreAllocationSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "PreAllocation no solver" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),' +
      'marginStatement=c(\'ms1\',\'ms1\'),' +
      'marginType=c(\'Initial\',\'Variation\'),\n' +
      'currency=c(\'EUR\',\'USD\'),\n' +
      'callAmount=c(1000,2000))')
    eval('resource_df <- data.frame(\n' +
      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
      'qtyMin = c(5000,6000),\n' +
      'minUnitValue = c(1,1))')
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
      'resource = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0.08,0.08,0),\n' +
      'externalCost = c(0.0001,0.0003,0.0001),\n' +
      'interestRate = c(0.0001,-0.0001,0.0001),\n' +
      'internalCost = c(0.0002,0.0001,0.0002),\n' +
      'opptCost = c(0.0001,-0.0001,0.0001),\n' +
      'stringsAsFactors = F)')
    eval('pref_vec <- c(5,5)')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')
    eval('algoVersion <- 2')
    eval('ifNewAlloc <- TRUE')
    eval('minMoveValue <- 1000')
    eval('timeLimit <- 13')
    eval('list <- PreAllocation(configurations,callInfo_df,availAsset_df,resource_df,\n' +
      '                          pref_vec,operLimitMs,fungible,\n' +
      '                          algoVersion,ifNewAlloc,list(),\n' +
      '                          minMoveValue,timeLimit)')
    eval('print(list)')
    eval('result_mat <- list$result_mat')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    // check allocation result
    that eval('result_mat[1,]'), equalTo(c(1087,0) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,2000) as SEXP)
  }

  void "PreAllocation with solver" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),' +
      'marginStatement=c(\'ms1\',\'ms1\'),' +
      'marginType=c(\'Initial\',\'Variation\'),\n' +
      'currency=c(\'EUR\',\'USD\'),\n' +
      'callAmount=c(4000,3000))')
    eval('resource_df <- data.frame(\n' +
      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
      'qtyMin = c(5000,6000),\n' +
      'minUnitValue = c(1,1))')
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
      'resource = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0.08,0.08,0),\n' +
      'externalCost = c(0.0001,0.0001,1),\n' +
      'interestRate = c(0.0001,-0.0001,0.0001),\n' +
      'internalCost = c(0.0002,0.0001,0.0002),\n' +
      'opptCost = c(0.0001,-0.0001,0.0001),\n' +
      'stringsAsFactors = F)')
    eval('pref_vec <- c(10,0)')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')
    eval('algoVersion <- 2')
    eval('ifNewAlloc <- TRUE')
    eval('minMoveValue <- 300')
    eval('timeLimit <- 13')
    eval('list <- PreAllocation(configurations,callInfo_df,availAsset_df,resource_df,\n' +
      '                          pref_vec,operLimitMs,fungible,\n' +
      '                          algoVersion,ifNewAlloc,list(),\n' +
      '                          minMoveValue,timeLimit)')
    eval('print(list)')
    eval('result_mat <- list$result_mat')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    // check allocation result
    that eval('result_mat[1,]'), equalTo(c(4348,0) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(652,2401) as SEXP)
    that eval('list$objValue'), closeTo(c(147778.2462) as SEXP,0.0001)
  }
}
