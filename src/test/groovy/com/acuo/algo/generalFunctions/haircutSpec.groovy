package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class haircutSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "HaircutCVec2Mat constructs collateral haircut matrix"() {
        when:
        eval('haircutC_vec <- c(0.1,0.2,0.1)')
        eval('availAsset_df <- data.frame(callId=c(\'mc1\',\'mc2\',\'mc2\'), \n' +
                          'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'))')
        eval('callId_vec <- c(\'mc1\',\'mc2\')')
        eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
        eval('haircutC_mat <- HaircutCVec2Mat(haircutC_vec,availAsset_df,callId_vec,resource_vec)')
        eval('rownames(haircutC_mat) <- NULL')
        eval('colnames(haircutC_mat) <- NULL')
        then:
        // check eligible matrix
        that eval('haircutC_mat[1,]'), equalTo(c(0.1,0) as SEXP)
        that eval('haircutC_mat[2,]'), equalTo(c(0.1,0.2) as SEXP)
    }

  void "HaircutFXVec2Mat constructs eligibility matrix"() {
    when:
    eval('haircutFX_vec <- c(0.08,0,0.08)')
    eval('availAsset_df <- data.frame(callId=c(\'mc1\',\'mc2\',\'mc2\'), \n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'))')
    eval('callId_vec <- c(\'mc1\',\'mc2\')')
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('haircutFX_mat <- HaircutFXVec2Mat(haircutFX_vec,availAsset_df,callId_vec,resource_vec)')
    eval('rownames(haircutFX_mat) <- NULL')
    eval('colnames(haircutFX_mat) <- NULL')
    then:
    // check eligible matrix
    that eval('haircutFX_mat[1,]'), equalTo(c(0.08,0) as SEXP)
    that eval('haircutFX_mat[2,]'), equalTo(c(0.08,0) as SEXP)
  }

  void "HaircutMat constructs eligibility matrix"() {
    when:
    eval('CRMap_df <- data.frame(callId=c(\'mc1\',\'mc2\'), \n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\'))')
    eval('availAsset_df <- data.frame(callId=c(\'mc1\',\'mc2\',\'mc2\'), \n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'),' +
      'haircut = c(0.1,0.2,0.13),' +
      'FXHaircut = c(0.08,0,0.08))')
    eval('callId_vec <- c(\'mc1\',\'mc2\')')
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('haircut_mat <- HaircutMat(availAsset_df,callId_vec,resource_vec)')
    eval('rownames(haircut_mat) <- NULL')
    eval('colnames(haircut_mat) <- NULL')
    then:
    // check haircut matrix
    that eval('haircut_mat[1,]'), equalTo(c(0.18,0) as SEXP)
    that eval('haircut_mat[2,]'), closeTo(c(0.21,0.2) as SEXP,0.0001)
  }
}
