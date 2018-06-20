package com.acuo.algo.coreAlgo

import org.renjin.sexp.SEXP
import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that
class CoreAlgoSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "CoreAlgoV2 no solver" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('callInfo_df <- data.frame(\n' +
                      'id=c(\'mc1\',\'mc2\'),' +
                      'marginStatement=c(\'ms1\',\'ms1\'),' +
                      'marginType=c(\'Initial\',\'Variation\'),\n' +
                      'currency=c(\'EUR\',\'JPY\'),\n' +
                      'callAmount=c(1000,2000),\n' +
                      'FXRate=c(1.2,100),\n' +
                      'from=c(\'EUR\',\'USD\'),\n' +
                      'to=c(\'USD\',\'JPY\'),\n' +
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
                      'assetCustacId = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
                      'haircut = c(0,0,0),\n' +
                      'FXHaircut = c(0,0.08,0),\n' +
                      'externalCost = c(0.0001,0.0001,0.0001),\n' +
                      'interestRate = c(0.0001,-0.0001,0.0001),\n' +
                      'internalCost = c(0.0002,0.0001,0.0002),\n' +
                      'opptCost = c(0.0001,-0.0001,0.0001),\n' +
                      'stringsAsFactors = F)')
    eval('availInfo_list <- AssetByCallInfo(callInfo_df$id,resource_df$id,availAsset_df,resource_df)')
    eval('timeLimit <- 13')
    eval('pref_vec <- c(5,5)')
    eval('operLimit <- 4')
    eval('operLimitMs_vec <- c(2,2)')
    eval('fungible <- FALSE')
    eval('minMoveValue <- 1000')
    eval('ifNewAlloc <- TRUE')
    eval('result <- CoreAlgoV2(configurations,callInfo_df, resource_df, availInfo_list,\n' +
      '                       timeLimit,pref_vec,operLimit,operLimitMs_vec,fungible,\n' +
      '                       minMoveValue,ifNewAlloc)')
    eval('print(result)')
    then:
    // check allocation result for mc1
    that eval('result$callOutput_list$mc1$Asset'), equalTo(c('EUR') as SEXP)
    that eval('round(result$callOutput_list$mc1$NetAmount,4)'), equalTo(c(830.7600) as SEXP)
    that eval('round(result$callOutput_list$mc1$`NetAmount(USD)`,4)'), equalTo(c(1000.9157) as SEXP)
    that eval('round(result$callOutput_list$mc1$Amount,4)'), equalTo(c(903) as SEXP)
    that eval('result$callOutput_list$mc1$Quantity'), equalTo(c(903) as SEXP)
    that eval('result$callOutput_list$mc1$FXRatePerUSD'), equalTo(c(0.83) as SEXP)
    that eval('result$callOutput_list$mc1$FXRate'), equalTo(c(1.2) as SEXP)
    that eval('result$callOutput_list$mc1$marginType'), equalTo(c('Initial') as SEXP)
    that eval('round(result$callOutput_list$mc1$Cost,4)'), equalTo(c(0.2176) as SEXP)

    // check allocation result for mc2
    that eval('result$callOutput_list$mc2$Asset'), equalTo(c('USD') as SEXP)
    that eval('result$callOutput_list$mc2$NetAmount'), equalTo(c(2000) as SEXP)
    that eval('result$callOutput_list$mc2$CustodianAccount'), equalTo(c('ca1') as SEXP)
    that eval('round(result$callOutput_list$mc2$CostFactor,5)'), equalTo(c(0.0003) as SEXP)
    that eval('round(result$callOutput_list$mc2$Cost,4)'), equalTo(c(0.6) as SEXP)
  }

  void "CoreAlgoV2 with solver" (){
    when:
    eval('configurations <- list(debugMode=FALSE)')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),' +
      'marginStatement=c(\'ms1\',\'ms1\'),' +
      'marginType=c(\'Initial\',\'Variation\'),\n' +
      'currency=c(\'EUR\',\'JPY\'),\n' +
      'callAmount=c(5000,3000),\n' +
      'FXRate=c(1.2,100),\n' +
      'from=c(\'EUR\',\'USD\'),\n' +
      'to=c(\'USD\',\'JPY\'),\n' +
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
      'assetCustacId = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'),\n' +
      'haircut = c(0,0,0),\n' +
      'FXHaircut = c(0.08,0,0.08),\n' +
      'externalCost = c(0.0001,0.0002,0.0001),\n' +
      'interestRate = c(0.0001,-0.0001,0.0001),\n' +
      'internalCost = c(0.0001,0.0001,0.0002),\n' +
      'opptCost = c(0.0001,-0.0001,0.0001),\n' +
      'stringsAsFactors = F)')
    eval('availInfo_list <- AssetByCallInfo(callInfo_df$id,resource_df$id,availAsset_df,resource_df)')
    eval('print(availInfo_list)')
    eval('timeLimit <- 13')
    eval('pref_vec <- c(10,0)')
    eval('operLimit <- 4')
    eval('operLimitMs_vec <- c(2,2)')
    eval('fungible <- FALSE')
    eval('minMoveValue <- 1000')
    eval('ifNewAlloc <- TRUE')
    eval('result <- CoreAlgoV2(configurations,callInfo_df, resource_df, availInfo_list,\n' +
      '                       timeLimit,pref_vec,operLimit,operLimitMs_vec,fungible,\n' +
      '                       minMoveValue,ifNewAlloc)')
    eval('print(result)')
    then:
    // check solver status
    that eval('result$solverStatus + 0'), equalTo(c(0) as SEXP)
    // check allocation result for mc1
    that eval('result$callOutput_list$mc1$Asset'), equalTo(c('EUR','USD') as SEXP)
    that eval('round(result$callOutput_list$mc1$NetAmount,4)'), equalTo(c(2109.56,2459) as SEXP)
    that eval('round(result$callOutput_list$mc1$`NetAmount(USD)`,4)'), equalTo(c(2541.6386,2459) as SEXP)
    that eval('round(result$callOutput_list$mc1$Amount,4)'), equalTo(c(2293,2459) as SEXP)
    that eval('result$callOutput_list$mc1$Quantity'), equalTo(c(2293,2459) as SEXP)
    that eval('result$callOutput_list$mc1$FXRatePerUSD'), equalTo(c(0.83,1) as SEXP)
    that eval('result$callOutput_list$mc1$FXRate'), equalTo(c(1.2,1) as SEXP)
    that eval('result$callOutput_list$mc1$marginType'), equalTo(c('Initial','Initial') as SEXP)
    that eval('round(result$callOutput_list$mc1$Cost,4)'), equalTo(c(0.5525,0.7377) as SEXP)

    // check allocation result for mc2
    that eval('result$callOutput_list$mc2$Asset'), equalTo(c('EUR') as SEXP)
    that eval('result$callOutput_list$mc2$NetAmount'), equalTo(c(2490.44) as SEXP)
    that eval('result$callOutput_list$mc2$CustodianAccount'), equalTo(c('ca2') as SEXP)
    that eval('round(result$callOutput_list$mc2$CostFactor,5)'), equalTo(c(0.0003) as SEXP)
    that eval('round(result$callOutput_list$mc2$Cost,4)'), equalTo(c(0.9784) as SEXP)
  }
}
