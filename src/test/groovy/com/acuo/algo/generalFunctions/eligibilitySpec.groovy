package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class eligibilitySpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "EliMat constructs eligibility matrix"() {
        when:
        eval('CRMap_df <- data.frame(callId=c(\'mc1\',\'mc2\'), \n' +
                          'resource = c(\'EUR---ca2\',\'USD---ca1\'))')
        eval('callId_vec <- c(\'mc1\',\'mc2\')')
        eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
        eval('eli_mat <- EliMat(CRMap_df,callId_vec,resource_vec)')
        eval('rownames(eli_mat) <- NULL')
        eval('colnames(eli_mat) <- NULL')
        then:
        // check eligible matrix
        that eval('eli_mat[1,]'), equalTo(c(1,0) as SEXP)
        that eval('eli_mat[2,]'), equalTo(c(0,1) as SEXP)
    }
}
