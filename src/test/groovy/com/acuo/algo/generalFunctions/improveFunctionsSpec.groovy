package com.acuo.algo.generalFunctions
import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that
class improveFunctionsSpec extends Specification implements RenjinEval{
  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
  }
  void "OrderCallId order margin calls when order method is 3"() {
    when:
    eval('callOrderMethod <- 3')
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
    eval('newCallInfo_df <- OrderCallId(callOrderMethod,callInfo_df)')
    eval('print(newCallInfo_df)')
    then:
    // check the order result
    that eval('newCallInfo_df$id'), equalTo(c('mc1','mc2') as SEXP)
  }

  void "OrderCallId order margin calls when order method is 2"() {
    when:
    eval('callOrderMethod <- 2')
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
    eval('newCallInfo_df <- OrderCallId(callOrderMethod,callInfo_df)')
    eval('print(newCallInfo_df)')
    then:
    // check the order result
    that eval('newCallInfo_df$id'), equalTo(c('mc2','mc1') as SEXP)
  }

  void "GroupCallIdByMs groups the margin calls by statements"() {
    when:
    eval('callLimit <- 7')
    eval('msLimit <- 4')
    eval('callId_vec <- c(\'mc1\',\'mc2\')')
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
    eval('groupCallId_list <- GroupCallIdByMs(callLimit,msLimit,callInfo_df,callId_vec)')
    eval('print(groupCallId_list)')
    then:
    // check the order result
    that eval('groupCallId_list'), equalTo(list(c('mc1','mc2')) as SEXP)
  }

  void "ResultSelect selects the better result between two allocation results"() {
    when:
    eval('mc1_df <- data.frame(c(\'USD\'), c(\'ca1\'), c(\'ms1\'), c(\'mc1\'),c(1000), c(1000))')
    eval('colnames(mc1_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Quantity\',\'Amount(USD)\')')
    eval('mc2_df = data.frame( c(\'USD\'), c(\'ca1\'), c(\'ms1\'), c(\'mc2\'),c(3000), c(3000))')
    eval('colnames(mc2_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Quantity\',\'Amount(USD)\')')

    eval('result1 <- list(callOutput = list(mc1 = mc1_df, mc2 = mc2_df))' )

    eval('mc1_df <- data.frame(c(\'EUR\'), c(\'ca2\'), c(\'ms1\'), c(\'mc1\'),c(830), c(830))')
    eval('colnames(mc1_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Quantity\',\'Amount(USD)\')')
    eval('mc2_df = data.frame( c(\'USD\'), c(\'ca1\'), c(\'ms1\'), c(\'mc2\'),c(3000), c(3000))')
    eval('colnames(mc2_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Quantity\',\'Amount(USD)\')')
    eval('result2 <- list(callOutput = list(mc1 = mc1_df, mc2 = mc2_df))' )

    eval('availAssetOri_df <- data.frame(\n' +
                'callId = c(\'mc1\',\'mc1\',\'mc2\'),' +
                'assetCustacId = c(\'EUR---ca2\',\'USD---ca1\',\'USD---ca1\'),\n' +
                'haircut = c(0,0,0),\n' +
                'FXHaircut = c(0.08,0,0),\n' +
                'externalCost = c(0.0001,0.0003,0.0001),\n' +
                'interestRate = c(0.0001,-0.0001,0.0001),\n' +
                'internalCost = c(0.0002,0.0001,0.0002),\n' +
                'opptCost = c(0.0001,-0.0001,0.0001),\n' +
                'stringsAsFactors = F)')
    eval('availAsset_df <- availAssetOri_df')
    eval('resourceOri_df <- data.frame(\n' +
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
    eval('resource_df <- resourceOri_df')
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
    eval('pref_vec <- c(5,5)')

    eval('betterResult <- ResultSelect(result1, result2,availAssetOri_df,availAsset_df,resourceOri_df,resource_df,callInfo_df,pref_vec)')
    eval('print(betterResult)')
    then:
    // check the order result
    that eval('betterResult$callOutput$mc1$Asset'), equalTo(c('EUR') as SEXP)
  }

  void "AnalysisFunction derives the analytics result" (){
    when:
    eval('availAssetOri_df <- data.frame(\n' +
              'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
              'assetCustacId = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
              'haircut = c(0,0,0),\n' +
              'FXHaircut = c(0.08,0.08,0),\n' +
              'externalCost = c(0.0001,0.0001,0.0001),\n' +
              'interestRate = c(0.0001,-0.0001,0.0001),\n' +
              'internalCost = c(0.0002,0.0001,0.0002),\n' +
              'opptCost = c(0.0001,-0.0001,0.0001),\n' +
              'stringsAsFactors = F)')
    eval('availAsset_df <- availAssetOri_df')

    eval('resourceOri_df <- data.frame(\n' +
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
    eval('resource_df <- resourceOri_df')
    eval('resource_df$qtyMin <- c(4000,3000)')

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
    eval('mc1_df <- data.frame(c(\'EUR\'), c(\'ca2\'), c(\'ms1\'), c(\'mc1\'), c(830))')
    eval('colnames(mc1_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Amount(USD)\')')
    eval('mc2_df = data.frame( c(\'USD\'), c(\'ca1\'), c(\'ms1\'), c(\'mc2\'), c(3000))')
    eval('colnames(mc2_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Amount(USD)\')')
    eval('callOutput_list <- list(mc1 = mc1_df, mc2 = mc2_df)' )

    eval('resultAnalysis <- ResultAnalysis(availAssetOri_df,availAsset_df,resourceOri_df,resource_df,callInfo_df,callOutput_list)')
    eval('print(resultAnalysis)')
    then:
    that eval('round(resultAnalysis$dailyCost,4)'), equalTo(c(1.149) as SEXP)
    that eval('resultAnalysis$movement'), equalTo(c(2) as SEXP)
    that eval('round(resultAnalysis$reservedLiquidityRatio,4)'), equalTo(c(0.6378) as SEXP)
  }

}
