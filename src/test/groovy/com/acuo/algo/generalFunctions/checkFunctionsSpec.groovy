package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.comparesEqualTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class checkFunctionsSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "AdjustResultVec adjusts the solver solution by rounding"() {
        when:
        eval('solution_vec <- c(1000.8,2000.1,4000,1,0)')
        eval('varName_vec <- c(\'x1\',\'x2\',\'x3\',\'x4\',\'x5\')')
        eval('varNum <- 3')
        eval('callAmount_vec <- c(3000,4000,3000)')
        eval('minUnitQuantity_vec <- c(5000,6000,5000)')
        eval('minUnitValue_vec <- c(1,1,1)')
        eval('fCon4_mat <- matrix(c(1,0,1,-5001,0,' +
                                      '0,1,0,0,-6001,' +
                                      '1,0,1,-1,0,' +
                                      '0,1,0,0,-1), nrow = 4,byrow = T)')

        eval('adjustedSolution_vec <- AdjustResultVec(solution_vec,varNum,varName_vec,fCon4_mat,\n' +
                'callAmount_vec,minUnitQuantity_vec,minUnitValue_vec)')
        eval('print(adjustedSolution_vec)')
        then:
        that eval('adjustedSolution_vec'), equalTo(c(1001,2001,4000,1,1) as SEXP)
    }

  void "CheckResultVec checks the allocation result and adjusts it for negative quantity used"() {
    when:
    eval('result_mat <- matrix(c(1000,2001,-1,4000), nrow = 2,byrow = T)')
    eval('quantityTotal_vec <- c(5000,6000)')
    eval('callId_vec <- c(\'mc1\',\'mc2\')')
    eval('callAmount_vec <- c(3000,4000)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
    eval('haircut_mat <- matrix(c(0,0,0,0),nrow = 2, byrow = T)')
    eval('eli_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')

    eval('checkResult <- CheckResultVec(result_mat,quantityTotal_vec,callId_vec,callAmount_vec,minUnitValue_mat,haircut_mat,eli_mat)')
    eval('print(checkResult)')

    then:
    that eval('result_mat[1,]'), equalTo(c(1000,2001) as SEXP)
    that eval('checkResult$result_mat[1,]'), equalTo(c(1000,2000) as SEXP)
    that eval('checkResult$result_mat[2,]'), equalTo(c(0,4000) as SEXP)
  }

  void "CheckResultVec checks the allocation result and adjusts it for over used resource quantity"() {
    when:
    eval('result_mat <- matrix(c(1000,2001,0,4000), nrow = 2,byrow = T)')
    eval('quantityTotal_vec <- c(5000,6000)')
    eval('callId_vec <- c(\'mc1\',\'mc2\')')
    eval('callAmount_vec <- c(3000,4000)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
    eval('haircut_mat <- matrix(c(0,0,0,0),nrow = 2, byrow = T)')
    eval('eli_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')

    eval('checkResult <- CheckResultVec(result_mat,quantityTotal_vec,callId_vec,callAmount_vec,minUnitValue_mat,haircut_mat,eli_mat)')
    eval('print(checkResult)')
    then:
    that eval('checkResult$result_mat[1,]'), equalTo(c(1000,2000) as SEXP)
    that eval('checkResult$result_mat[2,]'), equalTo(c(0,4000) as SEXP)
  }

  void "CheckResultVec checks the allocation result and adjusts it for not fully fulfilled call requirement"() {
    when:
    eval('result_mat <- matrix(c(1000,1001,0,4000), nrow = 2,byrow = T)')
    eval('quantityTotal_vec <- c(5000,6000)')
    eval('callId_vec <- c(\'mc1\',\'mc2\')')
    eval('callAmount_vec <- c(3000,4000)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
    eval('haircut_mat <- matrix(c(0,0,0,0),nrow = 2, byrow = T)')
    eval('eli_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')

    eval('checkResult <- CheckResultVec(result_mat,quantityTotal_vec,callId_vec,callAmount_vec,minUnitValue_mat,haircut_mat,eli_mat)')
    eval('print(checkResult)')
    then:
    that eval('checkResult$result_mat[1,]'), equalTo(c(1999,1001) as SEXP)
    that eval('checkResult$result_mat[2,]'), equalTo(c(0,4000) as SEXP)
  }

}
