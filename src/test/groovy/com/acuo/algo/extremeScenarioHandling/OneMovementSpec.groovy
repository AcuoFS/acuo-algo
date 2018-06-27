package com.acuo.algo.extremeScenarioHandling

import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class OneMovementSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "HandleStatementMovementLimitIsOne" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
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

    eval('availAsset_df <- HandleStatementMovementLimitIsOne(availAsset_df,callInfo_df,resource_df)')
    eval('print(availAsset_df)')
    then:
    // check new availAsset_df
    that eval('availAsset_df$resource'), equalTo(c('EUR---ca2','EUR---ca2') as SEXP)

  }

  void "RemoveResourcesNotSufficientForBothCallsFromAvailAsset " (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),' +
      'marginStatement=c(\'ms1\',\'ms1\'),' +
      'callAmount=c(1000,3000))')
    eval('resource_df <- data.frame(\n' +
      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
      'qtyMin = c(5000,6000),\n' +
      'minUnitValue = c(1,1))')
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0.08,0,0.08))')
    eval('twoCallIds <- c(\'mc1\',\'mc2\')')
    eval('msId <- c(\'ms1\')')
    eval('availAsset_df <- RemoveResourcesNotSufficientForBothCallsFromAvailAsset(availAsset_df,callInfo_df,resource_df,twoCallIds,msId)')
    eval('print(availAsset_df)')
    then:
    // check new availAsset_df
    that eval('availAsset_df$resource'), equalTo(c('EUR---ca2','EUR---ca2') as SEXP)
  }

  void "RemoveResourcesNotEligibleForBothCallsFromAvailAsset " (){
    when:
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0.08,0,0.08))')
    eval('twoCallIds <- c(\'mc1\',\'mc2\')')
    eval('msId <- c(\'ms1\')')
    eval('availAsset_df <- RemoveResourcesNotEligibleForBothCallsFromAvailAsset(availAsset_df,twoCallIds,msId)')
    eval('print(availAsset_df)')
    then:
    // check new availAsset_df
    that eval('availAsset_df$resource'), equalTo(c('EUR---ca2','EUR---ca2') as SEXP)
  }

  void "FindSufficientResourcesForBothCalls returns resource ids" (){
    when:
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0.08,0,0.08))')
    eval('resource_df <- data.frame(\n' +
      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
      'qtyMin = c(5000,6000),\n' +
      'minUnitValue = c(1,1))')
    eval('twoCallIds <- c(\'mc1\',\'mc2\')')
    eval('twoCallAmounts <- c(1000,2000)')
    eval('suffResource_vec <- FindSufficientResourcesForBothCalls(availAsset_df,resource_df,twoCallIds,twoCallAmounts)')
    eval('print(suffResource_vec)')
    then:
    // check solver status
    that eval('suffResource_vec'), equalTo(c('EUR---ca2') as SEXP)
  }

  void "RemoveRowsInAvailAsset removes rows in availAsset_df" (){
    when:
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'))')
    eval('rmIdx_vec <- c(1,2)')
    eval('availAsset_df <- RemoveRowsInAvailAsset(availAsset_df,rmIdx_vec)')
    eval('print(availAsset_df)')
    then:
    // check new availAsset_df
    that eval('dim(availAsset_df)[1]+0'), equalTo(c(1) as SEXP)
  }
}
