package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class infoConstructionSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }

    void "AddMinUnitValueInBaseCcyToAssetInfo calculates minUnit value based on USD and adds a mew column in assetInfo_df"() {
      when:
      eval('assetInfo_df <- data.frame(\n' +
        'id = c(\'EUR\',\'USD\'), \n' +
        'name = c(\'Euro\',\'US Dollar\'), \n' +
        'currency = c(\'EUR\',\'USD\'), \n' +
        'unitValue = c(1,1),\n'+
        'minUnit = c(1,1),\n' +
        'yield = c(0.0001,0.0001),\n' +
        'FXRate = c(1.2,1),\n' +
        'from = c(\'EUR\',\'USD\'),\n' +
        'to = c(\'USD\',\'USD\'),\n' +
        'oriFXRate = c(1.2,1))')
      eval('assetInfo_df_new <- AddMinUnitValueInBaseCcyToAssetInfo(assetInfo_df)')
      eval('print(assetInfo_df_new)')
      then:
      // check the new field minUnitValue
      that eval('assetInfo_df_new$minUnitValue'), closeTo(c(0.8333,1) as SEXP,0.0001)
    }

    void "UnifyFxBaseUsdInAssetInfo changes the column FXRate to store the fx rate based on USD and adds the column oriFXRate to store the original fxRate in assetInfo_df"() {
        when:
        eval('assetInfo_df <- data.frame(\n' +
          'id = c(\'EUR\',\'USD\'), \n' +
          'name = c(\'Euro\',\'US Dollar\'), \n' +
          'currency = c(\'EUR\',\'USD\'), \n' +
          'unitValue = c(1,1),\n'+
          'minUnit = c(1,1),\n' +
          'yield = c(0.0001,0.0001),\n' +
          'FXRate = c(1.2,1),\n' +
          'from = c(\'EUR\',\'USD\'),\n' +
          'to = c(\'USD\',\'USD\'))')
        eval('assetInfo_df_new <- UnifyFxBaseUsdInAssetInfo(assetInfo_df)')
        eval('print(assetInfo_df_new)')
        then:
        // check the new field FXRate
        that eval('assetInfo_df_new$FXRate'), closeTo(c(0.8333,1) as SEXP,0.0001)
        that eval('assetInfo_df_new$oriFXRate'), closeTo(c(1.2,1) as SEXP,0.0001)
      }

    void "UnifyFxBaseUsdInCallInfo changes the column FXRate to store the fx rate based on USD and adds the column oriFXRate to store the original fxRate in callInfo_df"() {
      when:
      eval('callInfo_df <- data.frame(\n' +
        'id=c(\'c1\',\'c2\'),\n' +
        'currency=c(\'EUR\',\'JPY\'),\n' +
        'callAmount=c(1000,20000),\n' +
        'FXRate=c(1.2,100),\n' +
        'from=c(\'EUR\',\'USD\'),\n' +
        'to=c(\'USD\',\'JPY\'))\n')
      eval('callInfo_df_new <- UnifyFxBaseUsdInCallInfo(callInfo_df)')
      eval('print(callInfo_df_new)')
      then:
      that eval('callInfo_df_new$FXRate'), closeTo(c(0.8333,100) as SEXP, 0.0001)
      that eval('callInfo_df_new$oriFXRate'), closeTo(c(1.2,100) as SEXP, 0.0001)
    }

    void "ConvertCallAmountToBaseCcyInCallInfo changes the column callAmount to base on USD and adds column oriCallAmount to store the original call amount in callInfo_df"() {
      when:
      eval('callInfo_df <- data.frame(\n' +
        'id=c(\'c1\',\'c2\'),\n' +
        'currency=c(\'EUR\',\'JPY\'),\n' +
        'callAmount=c(1000,20000),\n' +
        'FXRate=c(0.833333333,100),\n' +
        'from=c(\'EUR\',\'USD\'),\n' +
        'to=c(\'USD\',\'JPY\'),\n' +
        'oriFXRate = c(1.2,100))')
      eval('callInfo_df_new <- ConvertCallAmountToBaseCcyInCallInfo(callInfo_df)')
      eval('print(callInfo_df_new)')
      then:
      that eval('callInfo_df_new$callAmount'), closeTo(c(1200,200) as SEXP, 0.0001)
      that eval('callInfo_df_new$oriCallAmount'), closeTo(c(1000,20000) as SEXP, 0.0001)
    }

    void "ResourceInfoAndAvailAsset constructs resource_df and updates availAsset_df"() {
      when:
      eval('assetInfo_df <- data.frame(\n' +
                      'id = c(\'EUR\',\'USD\'), \n' +
                      'name = c(\'Euro\',\'US Dollar\'), \n' +
                      'currency = c(\'EUR\',\'USD\'), \n' +
                      'unitValue = c(1,1),\n'+
                      'minUnit = c(1,1),\n' +
                      'minUnitValue = c(1,1),\n' +
                      'yield = c(0.0001,0.0001),\n' +
                      'FXRate = c(0.83,1),\n' +
                      'from = c(\'EUR\',\'USD\'),\n' +
                      'to = c(\'USD\',\'USD\'),\n' +
                      'oriFXRate = c(1.2,1))')
      eval('availAsset_df <- data.frame(\n' +
                      'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
                      'assetId = c(\'USD\',\'EUR\',\'USD\'),\n' +
                      'haircut = c(0,0,0),\n' +
                      'FXHaircut = c(0,0.08,0),\n' +
                      'externalCost = c(0.0001,0.0001,0.0001),\n' +
                      'interestRate = c(0.0001,-0.0001,0.0001),\n' +
                      'quantity = c(5000,6000,5000),\n' +
                      'internalCost = c(0.0002,0.0001,0.0002),\n' +
                      'opptCost = c(0.0001,-0.0001,0.0001),\n' +
                      'venue = c(\'SG\',\'SG\',\'SG\'),\n' +
                      'CustodianAccount = c(\'ca1\',\'ca2\',\'ca1\'))')

      eval('list <- ResourceInfoAndAvailAsset(assetInfo_df, availAsset_df)')
      eval('print(list)')
      then:
      // check the new created resource_df
      that eval('dim(list$resource_df) + 0'), equalTo(c(2,15) as SEXP)
      that eval('names(list$resource_df)'), equalTo(c('id','assetId','assetName','qtyOri','qtyMin','qtyRes','unitValue','minUnit','minUnitValue','currency','yield','FXRate','oriFXRate','custodianAccount','venue') as SEXP)
      that eval('list$resource_df$id'), equalTo(c('EUR---ca2','USD---ca1') as SEXP)
      that eval('list$resource_df$qtyMin'), equalTo(c(6000,5000) as SEXP)
      that eval('list$resource_df$minUnitValue'), equalTo(c(1,1) as SEXP)
      that eval('list$resource_df$FXRate'), equalTo(c(0.83,1) as SEXP)
      // check the updated availAsset_df
      that eval('dim(list$availAsset_df) + 0'), equalTo(c(3,9) as SEXP)
      that eval('names(list$availAsset_df)'), equalTo(c( 'callId','resource','internalCost','opptCost','haircut','FXHaircut','externalCost','interestRate','yield') as SEXP)
      that eval('list$availAsset_df$callId'), equalTo(c('mc1','mc1','mc2') as SEXP)
      that eval('list$availAsset_df$resource'), equalTo(c('EUR---ca2','USD---ca1','USD---ca1') as SEXP)
    }
}
