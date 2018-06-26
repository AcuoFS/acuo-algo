package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class resultAdjustmentSpec extends Specification implements RenjinEval {
  void setup() {
      eval("library(stats)")
      eval("library('com.acuo.collateral.acuo-algo')")
  }

  void "RoundUpQuantityVariable rounds up(or down) decision variables' "() {
    when:
    eval('solution_vec <- c(500.5,2000.01,100,1,1)')
    eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___EUR---ca2\',' +
      '\'ms1___dummy___EUR---ca2\',\'ms1___dummy___USD---ca1\')')

    eval('solution_vec_new <- RoundUpQuantityVariable(solution_vec,varName_vec)')
    then:
    that eval('solution_vec_new'), equalTo(c(501,2001,100,1,1) as SEXP)
  }

  void "UpdateDummyVariable updates dummy variables by quantity variables"() {
    when:
    eval('solution_vec <- c(5000,20000,100,0,1)')
    eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___EUR---ca2\',' +
      '\'ms1___dummy___EUR---ca2\',\'ms1___dummy___USD---ca1\')')

    eval('solution_vec <- UpdateDummyVariable(solution_vec,varName_vec)')
    then:
    that eval('solution_vec'), equalTo(c(5000,20000,100,1,1) as SEXP)
  }

  void "AdjustSolverResult checks resources quantity constraints"() {
    when:// result violates the constraints
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('resourceQty_vec <- c(3000,6000)')

    eval('x <- tryCatch(CheckQuantityConstraint(allocatedQty_mat,resourceQty_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(c('Quantity limit condition checking failed') as SEXP)

    when:// result satisfies the constraints
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('resourceQty_vec <- c(5000,6000)')

    eval('x <- tryCatch(CheckQuantityConstraint(allocatedQty_mat,resourceQty_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(eval('NULL') as SEXP)
  }

  void "AdjustNonNegativeViolation checks margin requirement constraints"() {
    when:
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,-1000),nrow=2,byrow=T,' +
                                    'dimnames=list(c(\'mc1\',\'mc2\'),c(\'EUR---ca2\',\'USD---ca1\')))')
    eval('result_mat <- AdjustNonNegativeViolation(allocatedQty_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    that eval('result_mat[1,]'), equalTo(c(4000,2000) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,0) as SEXP)
  }

  void "AdjustQuantityLimitViolation adjusts allocation result to satisfy quantity limit"() {
    when:
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T,' +
      'dimnames=list(c(\'mc1\',\'mc2\'),c(\'EUR---ca2\',\'USD---ca1\')))')
    eval('resourceQty_vec <- c(3500,6000)')
    eval('callAmount_vec <- c(3500,2500)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('eli_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')

    eval('result_mat <- AdjustQuantityLimitViolation(allocatedQty_mat,resourceQty_vec,callAmount_vec,haircut_mat,minUnitValue_mat,eli_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    that eval('result_mat[1,]'), equalTo(c(3500,2000) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,1000) as SEXP)
  }

  void "AdjustCallRequirementViolation adjusts allocation result to meet margin requirement"() {
    when:// result violates the constraints
    eval('allocatedQty_mat <- matrix(c(3000,2000,0,1000),nrow=2,byrow=T)')
    eval('resourceQty_vec <- c(3500,6000)')
    eval('callAmount_vec <- c(3500,2500)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('eli_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')

    eval('result_mat <- AdjustCallRequirementViolation(allocatedQty_mat,resourceQty_vec,minUnitValue_mat,haircut_mat,callAmount_vec,eli_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    that eval('result_mat[1,]'), equalTo(c(3000,2000) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,2778) as SEXP)
  }

  void "CalculateAllocatedCallAmount calculates call amount has been fulfilled"() {
    when:// result violates the constraints
    eval('allocatedQty_mat <- matrix(c(3000,2000,0,1000),nrow=2,byrow=T)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')
    eval('callIdx <- c(1,2)')

    eval('allocatedCallAmount <- CalculateAllocatedCallAmount(allocatedQty_mat,minUnitValue_mat,haircut_mat,callIdx)')
    then:
    that eval('allocatedCallAmount'), equalTo(c(4200,900) as SEXP)
  }

  void "DetermineNewQuantityToUseOfExcessResourceForCall calculates the quantity should be used to the calls"() {
    when:
    eval('quantityUsedForCall <- c(1500,2000)')
    eval('quantityTotal <- c(6000,6000)')
    eval('quantityUsed <- c(2000,2500)')

    eval('newQuantity <- DetermineNewQuantityToUseOfExcessResourceForCall(quantityUsedForCall,quantityTotal,quantityUsed)')
    then:
    that eval('newQuantity'), equalTo(c(5500,5500) as SEXP)
  }

  void "CalculateMissingCallAmount calculates the unfufilled amount of a call if use new quantity of a resource"() {
    when:
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('idxCall <- 1')
    eval('callAmount <- 3000')
    eval('idxOldResource <- 1')
    eval('oldResourceNewQuantity <- 1000')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')

    eval('missingAmount <- CalculateMissingCallAmount(allocatedQty_mat,idxCall,callAmount,idxOldResource,oldResourceNewQuantity,minUnitValue_mat,haircut_mat)')
    then:
    that eval('missingAmount'), equalTo(c(400) as SEXP)
  }

  void "FulfillMissingCallAmount "() {
    when:
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('idxCall <- 1')
    eval('missingAmount <- 400')
    eval('callAmount <- 3000')
    eval('thisAlloc_mat <- matrix(c(1,4000),nrow=1,byrow=T)')
    eval('resourceQty_vec <- c(3500,6000)')
    eval('quantityUsed_vec <- c(3500,2500)')
    eval('idxOldResource <- 1')
    eval('oldResourceNewQuantity <- 1000')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')
    eval('eli_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')

    eval('result_mat <- FulfillMissingCallAmount(allocatedQty_mat,idxCall,missingAmount,callAmount,thisAlloc_mat,resourceQty_vec,quantityUsed_vec,\n' +
      '                                     minUnitValue_mat,haircut_mat,eli_mat)')
    then:
    that eval('result_mat[1,]'), equalTo(c(4000,2445) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,1000) as SEXP)
  }
}
