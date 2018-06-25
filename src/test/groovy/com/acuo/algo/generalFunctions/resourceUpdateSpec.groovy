package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class resourceUpdateSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "UpdateQtyOriInResourceDf updates the original quantity by minUnit quantity and residual quantity"() {
        when:
        eval('resource_df <- data.frame(id=c(\'EUR---ca1\',\'XXX---ca2\'), \n' +
                                    'qtyMin = c(5000,6000),\n'+
                                    'minUnit = c(1,1),\n' +
                                    'qtyOri = c(5000,6000),\n' +
                                    'qtyRes = c(0.1,0.2))')

        eval('newResource_df <- UpdateQtyOriInResourceDf(resource_df)')
        eval('print(newResource_df)')
        then:
        // check the updated original quantity
        that eval('newResource_df$qtyOri'), equalTo(c(5000.1,6000.2) as SEXP)
    }

    void "ResetQtyMinInResourceDf resets the minUnit quantity by original quantity"() {
        when:
        eval('resource_df <- data.frame(id=c(\'EUR---ca1\',\'XXX---ca2\'), \n' +
          'qtyMin = c(2000,3000),\n'+
          'minUnit = c(1,1),\n' +
          'qtyOri = c(5000.1,6000.2))')

        eval('newResource_df <- ResetQtyMinInResourceDf(resource_df)')
        eval('print(newResource_df)')
        then:
        // check the reset minUnit quantity
        that eval('newResource_df$qtyMin'), equalTo(c(5000,6000) as SEXP)
    }

    void "RemoveResourceNotInAvailAsset removes the resources that are not in availAsset_df from resource_df"() {
        when:
        eval('resource_df <- data.frame(id=c(\'EUR---ca1\',\'XXX---ca2\'), \n' +
          'qtyMin = c(5000,6000),\n'+
          'minUnit = c(1,1),\n' +
          'qtyOri = c(5000,6000),\n' +
          'qtyRes = c(0.1,0.2))')
        eval('availAsset_df <- data.frame(\n' +
          'callId = c(\'mc1\',\'mc1\'),\n' +
          'resource = c(\'USD---ca1\',\'EUR---ca1\'))')

        eval('resource_df_new <- RemoveResourceNotInAvailAsset(availAsset_df,resource_df)')
        eval('print(resource_df_new)')
        then:
        // check the new resources
        that eval('resource_df_new$id'), equalTo(c('EUR---ca1') as SEXP)
    }
}
