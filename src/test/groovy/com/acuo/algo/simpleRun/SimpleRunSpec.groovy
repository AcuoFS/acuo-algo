package com.acuo.algo.simpleRun

import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class SimpleRunSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }
  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "SimpleRun no solver" (){
    when:
    eval('callIds <- c(\'mc1\',\'mc2\') ')
    eval('pref <- c(5,5)')
    eval('clientId <- \'999\'')
    eval('algoVersion <- 2')
    eval('scenario <- 1')
    eval('callInfoByCallId <- data.frame(\n' +
            'id=c(\'mc1\',\'mc2\'),' +
            'marginStatement=c(\'ms1\',\'ms1\'),' +
            'marginType=c(\'Initial\',\'Variation\'),\n' +
            'currency=c(\'EUR\',\'USD\'),\n' +
            'callAmount=c(1000,2000),\n' +
            'FXRate=c(1.2,1),\n' +
            'from=c(\'EUR\',\'USD\'),\n' +
            'to=c(\'USD\',\'USD\'),\n' +
            'stringsAsFactors = F)\n')
    eval('assetInfoByAssetId <- data.frame(\n' +
                'id = c(\'EUR\',\'USD\'), \n' +
                'name = c(\'Euro\',\'US Dollar\'),' +
                'currency = c(\'EUR\',\'USD\'), \n' +
                'unitValue = c(1,1),\n'+
                'minUnit = c(1,1),\n' +
                'yield = c(0,0),\n' +
                'FXRate = c(1.2,1),\n' +
                'from = c(\'EUR\',\'USD\'),\n' +
                'to = c(\'USD\',\'USD\'),\n' +
                'stringsAsFactors = F)')
    eval('availAssetByCallIdAndClientId <- data.frame(\n' +
                    'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
                    'assetId = c(\'EUR\',\'EUR\',\'USD\'),\n' +
                    'haircut = c(0,0,0),\n' +
                    'FXHaircut = c(0.08,0.08,0),\n' +
                    'externalCost = c(0.0001,0.0001,0.0001),\n' +
                    'interestRate = c(0.0001,-0.0001,0.0001),\n' +
                    'quantity = c(5000,5000,6000),\n' +
                    'internalCost = c(0.0002,0.0001,0.0002),\n' +
                    'opptCost = c(0.0001,-0.0001,0.0001),\n' +
                    'venue = c(\'SG\',\'SG\',\'SG\'), \n' +
                    'CustodianAccount = c(\'ca2\',\'ca2\',\'ca1\'), \n' +
                    'stringsAsFactors = F)')
    eval('debugMode <- F')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')
    eval('result <- simpleRun(callIds,\n' +
      '                      pref,\n' +
      '                      clientId,\n' +
      '                      callInfoByCallId,\n' +
      '                      availAssetByCallIdAndClientId,\n' +
      '                      assetInfoByAssetId,\n' +
      '                      operLimitMs,\n' +
      '                      fungible,\n' +
      '                      debugMode)')
    //eval('print(result)')
    // convert to data frame format to easily read the result
    eval('result_df <- ResultList2Df(result$callOutput,callInfoByCallId$id)')
    eval('print(result_df)')
    then:
    // check allocation result for mc1
    that eval('result$callOutput$mc1$Asset'), equalTo(c('EUR') as SEXP)
    that eval('round(result$callOutput$mc1$NetAmount,4)'), equalTo(c(1000.04) as SEXP)
    that eval('round(result$callOutput$mc1$`NetAmount(USD)`,4)'), equalTo(c(1200.048) as SEXP)
    that eval('round(result$callOutput$mc1$Amount,4)'), equalTo(c(1087) as SEXP)
    that eval('result$callOutput$mc1$Quantity'), equalTo(c(1087) as SEXP)
    that eval('round(result$callOutput$mc1$FXRatePerUSD,4)'), equalTo(c(0.8333) as SEXP)
    that eval('result$callOutput$mc1$FXRate'), equalTo(c(1.2) as SEXP)
    that eval('result$callOutput$mc1$marginType'), equalTo(c('Initial') as SEXP)
    that eval('round(result$callOutput$mc1$Cost,4)'), equalTo(c(0.3913) as SEXP)

    // check allocation result for mc2
    that eval('result$callOutput$mc2$Asset'), equalTo(c('EUR') as SEXP)
    that eval('round(result$callOutput$mc2$NetAmount,4)'), equalTo(c(1667.04) as SEXP)
    that eval('result$callOutput$mc2$CustodianAccount'), equalTo(c('ca2') as SEXP)
    that eval('round(result$callOutput$mc2$CostFactor,5)'), equalTo(c(0.0002) as SEXP)
    that eval('round(result$callOutput$mc2$Cost,4)'), equalTo(c(0.4349) as SEXP)
  }

  void "SimpleRun with solver" (){
    when:
    eval('callIds <- c(\'mc1\',\'mc2\') ')
    eval('pref <- c(5,5)')
    eval('clientId <- \'999\'')
    eval('algoVersion <- 2')
    eval('scenario <- 1')
    eval('callInfoByCallId <- data.frame(\n' +
                    'id=c(\'mc1\',\'mc2\'),' +
                    'marginStatement=c(\'ms1\',\'ms1\'),' +
                    'marginType=c(\'Initial\',\'Variation\'),\n' +
                    'currency=c(\'EUR\',\'USD\'),\n' +
                    'callAmount=c(3000,5000),\n' +
                    'FXRate=c(1.2,1),\n' +
                    'from=c(\'EUR\',\'USD\'),\n' +
                    'to=c(\'USD\',\'USD\'),\n' +
                    'stringsAsFactors = F)\n')
    eval('assetInfoByAssetId <- data.frame(\n' +
                    'id = c(\'EUR\',\'USD\'), \n' +
                    'name = c(\'Euro\',\'US Dollar\'),' +
                    'currency = c(\'EUR\',\'USD\'), \n' +
                    'unitValue = c(1,1),\n'+
                    'minUnit = c(1,1),\n' +
                    'yield = c(0,0),\n' +
                    'FXRate = c(1.2,1),\n' +
                    'from = c(\'EUR\',\'USD\'),\n' +
                    'to = c(\'USD\',\'USD\'),\n' +
                    'stringsAsFactors = F)')
    eval('availAssetByCallIdAndClientId <- data.frame(\n' +
                  'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
                  'assetId = c(\'EUR\',\'EUR\',\'USD\'),\n' +
                  'haircut = c(0,0,0),\n' +
                  'FXHaircut = c(0.08,0.08,0),\n' +
                  'externalCost = c(0.0001,0.0001,0.0001),\n' +
                  'interestRate = c(0.0001,-0.0001,0.0001),\n' +
                  'quantity = c(5000,5000,6000),\n' +
                  'internalCost = c(0.0002,0.0001,0.0002),\n' +
                  'opptCost = c(0.0001,-0.0001,0.0001),\n' +
                  'venue = c(\'SG\',\'SG\',\'SG\'), \n' +
                  'CustodianAccount = c(\'ca2\',\'ca2\',\'ca1\'), \n' +
                  'stringsAsFactors = F)')
    eval('debugMode <- F')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')
    eval('result <- simpleRun(callIds,\n' +
      '                      pref,\n' +
      '                      clientId,\n' +
      '                      callInfoByCallId,\n' +
      '                      availAssetByCallIdAndClientId,\n' +
      '                      assetInfoByAssetId,\n' +
      '                      operLimitMs,\n' +
      '                      fungible,\n' +
      '                      debugMode)')
    //eval('print(result)')
    // convert to data frame format to easily read the result
    eval('result_df <- ResultList2Df(result$callOutput,callInfoByCallId$id)')
    eval('print(result_df)')
    then:
    // check allocation result for mc1
    that eval('result$callOutput$mc1$Asset'), equalTo(c('EUR') as SEXP)
    that eval('round(result$callOutput$mc1$NetAmount,4)'), equalTo(c(3000.12) as SEXP)
    that eval('round(result$callOutput$mc1$`NetAmount(USD)`,4)'), equalTo(c(3600.144) as SEXP)
    that eval('round(result$callOutput$mc1$Amount,4)'), equalTo(c(3261) as SEXP)
    that eval('result$callOutput$mc1$Quantity'), equalTo(c(3261) as SEXP)
    that eval('round(result$callOutput$mc1$FXRatePerUSD,4)'), equalTo(c(0.8333) as SEXP)
    that eval('result$callOutput$mc1$FXRate'), equalTo(c(1.2) as SEXP)
    that eval('result$callOutput$mc1$marginType'), equalTo(c('Initial') as SEXP)
    that eval('round(result$callOutput$mc1$Cost,4)'), equalTo(c(1.174) as SEXP)

    // check allocation result for mc2
    that eval('result$callOutput$mc2$Asset'), equalTo(c('EUR','USD') as SEXP)
    that eval('round(result$callOutput$mc2$NetAmount,4)'), equalTo(c(1599.88,3081) as SEXP)
    that eval('result$callOutput$mc2$CustodianAccount'), equalTo(c('ca2','ca1') as SEXP)
    that eval('round(result$callOutput$mc2$CostFactor,5)'), equalTo(c(0.0002,0.0003) as SEXP)
    that eval('round(result$callOutput$mc2$Cost,4)'), equalTo(c(0.4174,0.9243) as SEXP)
  }
}
