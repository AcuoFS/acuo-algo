package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class assetSufficiencySpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "EstimateAssetSufficiency estimates the asset inventory sufficiency"() {
        when:
        eval('availAsset_df <- data.frame(callId=c(\'mc1\',\'mc2\'), \n' +
                          'resource = c(\'EUR---ca2\',\'USD---ca1\'),' +
                          'haircut = c(0,0),' +
                          'FXHaircut = c(0.08,0))')
        eval('callInfo_df <- data.frame(id=c(\'mc1\',\'mc2\'), \n' +
                           'callAmount = c(1000,3000))')
        eval('resource_df <- data.frame(id=c(\'EUR---ca2\',\'USD---ca1\'), \n' +
                           'qtyMin = c(5000,6000),\n'+
                           'minUnitValue = c(1,1))')
        eval('isSufficient <- EstimateAssetSufficiency(availAsset_df,callInfo_df,resource_df)')
        then:
        // check sufficiency
        that eval('isSufficient'), equalTo(c(true) as SEXP)
    }

    void "CheckOptimalAssetSufficiency checks optimal assets sufficiency"() {
        when:
        eval('optimalResource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
        eval('callInfo_df <- data.frame(id=c(\'mc1\',\'mc2\'), \n' +
          'callAmount = c(1000,3000))')
        eval('availAsset_df <- data.frame(callId=c(\'mc1\',\'mc2\'), \n' +
          'resource = c(\'EUR---ca2\',\'USD---ca1\'),' +
          'haircut = c(0,0),' +
          'FXHaircut = c(0.08,0))')
        eval('resource_df <- data.frame(id=c(\'EUR---ca2\',\'USD---ca1\'), \n' +
          'qtyMin = c(5000,6000),\n'+
          'minUnitValue = c(1,1))')

        eval('isSufficient <- CheckOptimalAssetSufficiency(optimalResource_vec,callInfo_df,availAsset_df,resource_df)')
        then:
        // check optimal resources sufficiency
        that eval('isSufficient'), equalTo(c(true) as SEXP)
    }
}
