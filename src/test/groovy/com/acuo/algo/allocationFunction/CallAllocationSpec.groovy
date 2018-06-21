package com.acuo.algo.allocationFunction

import org.renjin.sexp.SEXP
import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that
class CallAllocationSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }
  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "CallAllocation no solver" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('algoVersion <- 2')
    eval('scenario <- 1')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),' +
      'marginStatement=c(\'ms1\',\'ms1\'),' +
      'marginType=c(\'Initial\',\'Variation\'),\n' +
      'currency=c(\'EUR\',\'USD\'),\n' +
      'callAmount=c(1000,2000),\n' +
      'FXRate=c(1.2,100),\n' +
      'from=c(\'EUR\',\'USD\'),\n' +
      'to=c(\'USD\',\'USD\'),\n' +
      'stringsAsFactors = F)\n')
    eval('resource_df <- data.frame(\n' +
      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
      'assetId = c(\'EUR\',\'USD\'), \n' +
      'assetName = c(\'Euro\',\'US Dollar\'),' +
      'qtyOri = c(5000,6000),\n' +
      'qtyMin = c(5000,6000),\n' +
      'qtyRes = c(0,0),\n' +
      'unitValue = c(1,1),\n'+
      'minUnit = c(1,1),\n' +
      'minUnitValue = c(1,1),\n' +
      'currency = c(\'EUR\',\'USD\'), \n' +
      'custodianAccount = c(\'ca2\',\'ca1\'), \n' +
      'venue = c(\'SG\',\'SG\'), \n' +
      'FXRate = c(0.83,1),\n' +
      'from = c(\'EUR\',\'USD\'),\n' +
      'to = c(\'USD\',\'USD\'),\n' +
      'oriFXRate = c(1.2,1),\n' +
      'stringsAsFactors = F)')
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
      'assetCustacId = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0,0.08,0),\n' +
      'externalCost = c(0.0001,0.0001,0.0001),\n' +
      'interestRate = c(0.0001,-0.0001,0.0001),\n' +
      'internalCost = c(0.0002,0.0001,0.0002),\n' +
      'opptCost = c(0.0001,-0.0001,0.0001),\n' +
      'stringsAsFactors = F)')
    eval('timeLimit <- 13')
    eval('pref_vec <- c(5,5)')
    eval('operLimit <- 4')
    eval('operLimitMs_vec <- c(2,2)')
    eval('fungible <- FALSE')
    eval('minMoveValue <- 1000')
    eval('inputLimit_vec <- c(7,7,7,4)')
    eval('callOrderMethod <- 3')
    eval('ifNewAlloc <- TRUE')
    eval('result <- CallAllocation(configurations,algoVersion,scenario,callInfo_df,availAsset_df,resource_df,\n' +
      'pref_vec,operLimit,operLimitMs_vec,fungible,ifNewAlloc,allocated_list=list(),inputLimit_vec,timeLimit,callOrderMethod,minMoveValue)')
    //eval('print(result)')
    // convert to data frame format to easily read the result
    eval('result_df <- ResultList2Df(result$callOutput,callInfo_df$id)')
    eval('print(result_df)')
    then:
    // check allocation result for mc1
    that eval('result$callOutput$mc1$Asset'), equalTo(c('EUR') as SEXP)
    that eval('round(result$callOutput$mc1$NetAmount,4)'), equalTo(c(830) as SEXP)
    that eval('round(result$callOutput$mc1$`NetAmount(USD)`,4)'), equalTo(c(1000) as SEXP)
    that eval('round(result$callOutput$mc1$Amount,4)'), equalTo(c(830) as SEXP)
    that eval('result$callOutput$mc1$Quantity'), equalTo(c(830) as SEXP)
    that eval('result$callOutput$mc1$FXRatePerUSD'), equalTo(c(0.83) as SEXP)
    that eval('result$callOutput$mc1$FXRate'), equalTo(c(1.2) as SEXP)
    that eval('result$callOutput$mc1$marginType'), equalTo(c('Initial') as SEXP)
    that eval('round(result$callOutput$mc1$Cost,4)'), equalTo(c(0.3) as SEXP)

    // check allocation result for mc2
    that eval('result$callOutput$mc2$Asset'), equalTo(c('EUR') as SEXP)
    that eval('round(result$callOutput$mc2$NetAmount,4)'), equalTo(c(1660.6) as SEXP)
    that eval('result$callOutput$mc2$CustodianAccount'), equalTo(c('ca2') as SEXP)
    that eval('round(result$callOutput$mc2$CostFactor,5)'), equalTo(c(0.0002) as SEXP)
    that eval('round(result$callOutput$mc2$Cost,4)'), equalTo(c(0.4349) as SEXP)
  }

  void "CallAllocation with solver" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('algoVersion <- 2')
    eval('scenario <- 1')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),' +
      'marginStatement=c(\'ms1\',\'ms1\'),' +
      'marginType=c(\'Initial\',\'Variation\'),\n' +
      'currency=c(\'EUR\',\'USD\'),\n' +
      'callAmount=c(3000,5000),\n' +
      'FXRate=c(1.2,100),\n' +
      'from=c(\'EUR\',\'USD\'),\n' +
      'to=c(\'USD\',\'USD\'),\n' +
      'stringsAsFactors = F)\n')
    eval('resource_df <- data.frame(\n' +
      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
      'assetId = c(\'EUR\',\'USD\'), \n' +
      'assetName = c(\'Euro\',\'US Dollar\'),' +
      'qtyOri = c(5000,6000),\n' +
      'qtyMin = c(5000,6000),\n' +
      'qtyRes = c(0,0),\n' +
      'unitValue = c(1,1),\n'+
      'minUnit = c(1,1),\n' +
      'minUnitValue = c(1,1),\n' +
      'currency = c(\'EUR\',\'USD\'), \n' +
      'custodianAccount = c(\'ca2\',\'ca1\'), \n' +
      'venue = c(\'SG\',\'SG\'), \n' +
      'FXRate = c(0.83,1),\n' +
      'from = c(\'EUR\',\'USD\'),\n' +
      'to = c(\'USD\',\'USD\'),\n' +
      'oriFXRate = c(1.2,1),\n' +
      'stringsAsFactors = F)')
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
      'assetCustacId = c(\'EUR---ca2\',\'USD---ca1\',\'USD---ca1\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0.08,0,0),\n' +
      'externalCost = c(0.0001,0.0001,0.0001),\n' +
      'interestRate = c(0.0001,-0.0001,-0.0001),\n' +
      'internalCost = c(0.0002,0.0001,0.0001),\n' +
      'opptCost = c(0.0001,-0.0001,-0.0001),\n' +
      'stringsAsFactors = F)')
    eval('timeLimit <- 13')
    eval('pref_vec <- c(10,0)')
    eval('operLimit <- 4')
    eval('operLimitMs_vec <- c(2,2)')
    eval('fungible <- FALSE')
    eval('minMoveValue <- 1000')
    eval('inputLimit_vec <- c(7,7,7,4)')
    eval('callOrderMethod <- 3')
    eval('ifNewAlloc <- TRUE')
    eval('result <- CallAllocation(configurations,algoVersion,scenario,callInfo_df,availAsset_df,resource_df,\n' +
      'pref_vec,operLimit,operLimitMs_vec,fungible,ifNewAlloc,allocated_list=list(),inputLimit_vec,timeLimit,callOrderMethod,minMoveValue)')
    eval('print(result)')
    // convert to data frame format to easily read the result
    eval('result_df <- ResultList2Df(result$callOutput,callInfo_df$id)')
    eval('print(result_df)')
    then:
    // check allocation result for mc1
    that eval('result$callOutput$mc1$Asset'), equalTo(c('EUR','USD') as SEXP)
    that eval('round(result$callOutput$mc1$NetAmount,4)'), equalTo(c(1660.6,1000) as SEXP)
    that eval('round(result$callOutput$mc1$`NetAmount(USD)`,4)'), equalTo(c(2000.7229,1000) as SEXP)
    that eval('round(result$callOutput$mc1$Amount,4)'), equalTo(c(1805,1000) as SEXP)
    that eval('result$callOutput$mc1$Quantity'), equalTo(c(1805,1000) as SEXP)
    that eval('result$callOutput$mc1$FXRatePerUSD'), equalTo(c(0.83,1) as SEXP)
    that eval('result$callOutput$mc1$FXRate'), equalTo(c(1.2,1) as SEXP)
    that eval('result$callOutput$mc1$marginType'), equalTo(c('Initial','Initial') as SEXP)
    that eval('round(result$callOutput$mc1$Cost,4)'), equalTo(c(0.6524,0.2) as SEXP)

    // check allocation result for mc2
    that eval('result$callOutput$mc2$Asset'), equalTo(c('USD') as SEXP)
    that eval('round(result$callOutput$mc2$NetAmount,4)'), equalTo(c(5000) as SEXP)
    that eval('result$callOutput$mc2$CustodianAccount'), equalTo(c('ca1') as SEXP)
    that eval('round(result$callOutput$mc2$CostFactor,5)'), equalTo(c(0.0002) as SEXP)
    that eval('round(result$callOutput$mc2$Cost,4)'), equalTo(c(1) as SEXP)
  }
}
