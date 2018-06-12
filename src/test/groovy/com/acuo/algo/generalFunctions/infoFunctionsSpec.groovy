package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class infoFunctionsSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }

    void "ResourceInfoAndAvailAsset constructs resource_df and updates availAsset_df"() {
      when:
      eval('assetInfo_df <- data.frame(\n' +
                      'id = c(\'EUR\',\'USD\'), \n' +
                      'name = c(\'Euro\',\'US Dollar\'), \n' +
                      'currency = c(\'EUR\',\'USD\'), \n' +
                      'unitValue = c(1,1),\n'+
                      'minUnit = c(1,1),\n' +
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
      that eval('names(list$availAsset_df)'), equalTo(c( 'callId','assetCustacId','internalCost','opptCost','haircut','FXHaircut','externalCost','interestRate','yield') as SEXP)
      that eval('list$availAsset_df$callId'), equalTo(c('mc1','mc1','mc2') as SEXP)
      that eval('list$availAsset_df$assetCustacId'), equalTo(c('EUR---ca2','USD---ca1','USD---ca1') as SEXP)
    }

    void "AssetByCallInfo derives eligibility, haircut and cost information"() {
        when:
        eval('callId_vec <- c(\'mc1\',\'mc2\')')
        eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
        eval('availAsset_df <- data.frame(\n' +
                        'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
                        'assetCustacId = c(\'USD---ca1\',\'EUR---ca2\',\'USD---ca1\'),\n' +
                        'haircut = c(0,0,0),\n' +
                        'FXHaircut = c(0,0.08,0),\n' +
                        'externalCost = c(0.0001,0.0001,0.0001),\n' +
                        'interestRate = c(0.0001,-0.0001,0.0001),\n' +
                        'internalCost = c(0.0002,0.0001,0.0002),\n' +
                        'opptCost = c(0.0001,-0.0001,0.0001))')
        eval('resource_df <- data.frame(\n' +
                        'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
                        'assetId = c(\'EUR\',\'USD\'), \n' +
                        'currency = c(\'EUR\',\'USD\'))')

        eval('list <- AssetByCallInfo(callId_vec,resource_vec,availAsset_df,resource_df)')
        eval('print(list)')
        then:
        // check the new created resource_df
        that eval('list$eli_mat[1,1]'), equalTo(c(1) as SEXP)
        that eval('list$eli_mat[2,1]'), equalTo(c(0) as SEXP)
        that eval('list$haircut_mat[1,1]'), equalTo(c(0.08) as SEXP)
        that eval('list$haircut_mat[1,2]'), equalTo(c(0) as SEXP)
        that eval('list$haircutFX_mat[2,1]'), equalTo(c(0) as SEXP)
        that eval('list$haircutFX_mat[2,2]'), equalTo(c(0) as SEXP)
        that eval('list$cost_mat[1,2]'), closeTo(c(0.0003) as SEXP,0.0001)
        that eval('list$cost_mat[2,1]'), equalTo(c(0) as SEXP)
      }

    void "AssetInfoFxConversion convert fxRate to base on USD and update assetInfo_df"() {
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
      eval('assetInfo_df_new <- AssetInfoFxConversion(assetInfo_df)')
      eval('print(assetInfo_df_new)')
      then:
      // check the converted fx rate and original fx rate
      that eval('assetInfo_df_new$FXRate[1]'), closeTo(c(0.8333) as SEXP, 0.0001)
      that eval('assetInfo_df_new$oriFXRate[1]'), closeTo(c(1.2) as SEXP, 0.0001)
    }

    void "CallInfoFxConversion convert fxRate to base on USD and update callInfo_df"() {
        expect:
        eval('callInfo_df <- data.frame(\n' +
                'id=c(\'c1\',\'c2\'),\n' +
                'currency=c(\'EUR\',\'JPY\'),\n' +
                'callAmount=c(1000,20000),\n' +
                'FXRate=c(1.2,100),\n' +
                'from=c(\'EUR\',\'USD\'),\n' +
                'to=c(\'USD\',\'JPY\'))\n')
        eval('callInfo_df_new <- CallInfoFxConversion(callInfo_df)')
        eval('fxRate_EUR <- callInfo_df_new$FXRate[1]')
        eval('print(fxRate_EUR)')
        that eval('fxRate_EUR'), closeTo(c(0.8333d) as SEXP, 0.0001)
        // Alternatively, use the rounding function, not recommendated
        // that eval('round(fxRate_EUR,4)'), equalTo(c(0.8333d) as SEXP)

        // NOTE that the value(s) must be enclosed by "c()" and followed by "as SEXP"
        // So the following expressions do not work
        // that eval('round(fxRate_EUR,4)'), equalTo(c(0.8333d))
        // that eval('round(fxRate_EUR,4)'), equalTo(0.8333d as SEXP)
        // that eval('round(fxRate_EUR,4)'), closeTo(0.8333d,0.0001)
    }
}
