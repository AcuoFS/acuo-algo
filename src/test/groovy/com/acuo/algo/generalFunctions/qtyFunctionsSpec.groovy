package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class qtyFunctionsSpec extends Specification implements RenjinEval {
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

    void "UsedQtyFromResultList calculates the used quantity from allocation result list"() {
        when:
        eval('result_list <- list(mc1=data.frame(Asset=\'EUR\',CustodianAccount=\'ca1\',Quantity=1000),\n' +
                                'mc2=data.frame(Asset=\'XXX\',CustodianAccount=\'ca2\',Quantity=2000))')
        eval('resource_vec =c(\'EUR---ca1\',\'XXX---ca2\')')
        eval('callId_vec <- c(\'mc1\',\'mc2\')')

        eval('quantityUsed_vec <- UsedQtyFromResultList(result_list,resource_vec,callId_vec)')
        eval('print(quantityUsed_vec)')
        then:
        that eval('quantityUsed_vec'), equalTo(c(1000,2000) as SEXP)
    }

    void "UpdateResourceInfoAndAvailAsset removes the resources with few units left from resource_df and availAsset_df"() {
        when:
        eval('resource_df <- data.frame(id=c(\'EUR---ca1\',\'XXX---ca2\'), \n' +
                            'qtyMin = c(1,3000),\n'+
                            'minUnit = c(1,1))')
        eval('availAsset_df <- data.frame(callId = c(\'c1\',\'c1\',\'c2\'),\n' +
                            'assetCustacId = c(\'EUR---ca1\',\'XXX---ca2\',\'XXX---ca2\'))')
        eval('callNum <- 2')

        eval('updateList <- UpdateResourceInfoAndAvailAsset(resource_df,availAsset_df,callNum)')
        eval('print(updateList)')
        then:
        // check the resource left in resource_df
        that eval('updateList$resource_df$id'), equalTo(c('XXX---ca2') as SEXP)
        // check the resource left in availAsset_df
        that eval('unique(updateList$availAsset_df$assetCustacId)'), equalTo(c('XXX---ca2') as SEXP)
    }
}
