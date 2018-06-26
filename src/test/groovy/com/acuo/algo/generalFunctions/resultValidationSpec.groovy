package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.Null
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.comparesEqualTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class resultValidationSpec extends Specification implements RenjinEval {
  void setup() {
      eval("library(stats)")
      eval("library('com.acuo.collateral.acuo-algo')")
  }

  void "CheckLowerBound checks decision variables' lower bound"() {
    when:// result violates the constraints
    eval('solution_vec <- c(500,2000)')
    eval('lpLowerBound_vec <- c(1000,1000)')

    eval('x <- tryCatch(CheckLowerBound(solution_vec,lpLowerBound_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(c('Lower bound condition checking failed') as SEXP)

    when:// result satisfies the constraints
    eval('solution_vec <- c(0,2000)')
    eval('lpLowerBound_vec <- c(1000,1000)')

    eval('x <- tryCatch(CheckLowerBound(solution_vec,lpLowerBound_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(eval('NULL') as SEXP)
  }

  void "CheckUpperBound checks decision variables' upper bound"() {
    when:// result violates the constraints
    eval('solution_vec <- c(4000,2000)')
    eval('lpUpperBound_vec <- c(3000,4000)')

    eval('x <- tryCatch(CheckUpperBound(solution_vec,lpUpperBound_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(c('Upper bound condition checking failed') as SEXP)

    when:// result satisfies the constraints
    eval('solution_vec <- c(500,2000)')
    eval('lpUpperBound_vec <- c(1000,4000)')

    eval('x <- tryCatch(CheckUpperBound(solution_vec,lpUpperBound_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(eval('NULL') as SEXP)
  }

  void "CheckQuantityConstraint checks resources quantity constraints"() {
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

  void "CheckMarginConstraint checks margin requirement constraints"() {
    when:// result violates the constraints
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')
    eval('callAmount_vec <- c(3000,6000)')

    eval('x <- tryCatch(CheckMarginConstraint(allocatedQty_mat,minUnitValue_mat,haircut_mat,callAmount_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(c('Margin completion condition checking failed') as SEXP)

    when:// result satisfies the constraints
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('minUnitValue_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')
    eval('callAmount_vec <- c(3000,2000)')

    eval('x <- tryCatch(CheckMarginConstraint(allocatedQty_mat,minUnitValue_mat,haircut_mat,callAmount_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(eval('NULL') as SEXP)
  }

  void "CheckDummyConstraint checks variables relationships constraints"() {
    when:// result violates the constraints
    eval('solution_vec <- c(500,1000,100,1,0)')
    eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___EUR---ca2\',' +
      '\'ms1___dummy___EUR---ca2\',\'ms1___dummy___USD---ca1\')')

    eval('x <- tryCatch(CheckDummyConstraint(solution_vec,varName_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(c('Dummy relationship condition checking failed') as SEXP)

    when:// result satisfies the constraints
    eval('solution_vec <- c(500,0,100,1,0)')
    eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___EUR---ca2\',' +
      '\'ms1___dummy___EUR---ca2\',\'ms1___dummy___USD---ca1\')')

    eval('x <- tryCatch(CheckDummyConstraint(solution_vec,varName_vec),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(eval('NULL') as SEXP)
  }

  void "CheckMovementConstraint checks movement limit constraints"() {
    when:// result violates the constraints
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('operLimitMs <- 1')
    eval('fungible <- FALSE')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),\n' +
      'marginStatement=c(\'ms1\',\'ms1\'),\n' +
      'marginType=c(\'Initial\',\'Variation\'))')

    eval('x <- tryCatch(CheckMovementConstraint(allocatedQty_mat,operLimitMs,fungible,callInfo_df),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(c('Movement limit per statement condition failed') as SEXP)

    when:// result satisfies the constraints
    eval('allocatedQty_mat <- matrix(c(4000,2000,0,1000),nrow=2,byrow=T)')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),\n' +
      'marginStatement=c(\'ms1\',\'ms1\'),\n' +
      'marginType=c(\'Initial\',\'Variation\'))')

    eval('x <- tryCatch(CheckMovementConstraint(allocatedQty_mat,operLimitMs,fungible,callInfo_df),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(eval('NULL') as SEXP)
  }

  void "CheckSolverResult validates allocation result by certain constraints"() {
    when:
    eval('solution_vec <- c(1500,2000,4000,1,1)')
    eval('result_mat <- matrix(c(1500,2000,4000,0),nrow=2,byrow=T)')
    eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___EUR---ca2\',' +
      '\'ms1___dummy___EUR---ca2\',\'ms1___dummy___USD---ca1\')')
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\'),\n' +
      'marginStatement=c(\'ms1\',\'ms1\'),\n' +
      'marginType=c(\'Initial\',\'Variation\'))')
    eval('resourceQty_vec <- c(6000,6000)')
    eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('haircut_mat <- matrix(c(0.2,0.1,0.2,0.1),nrow=2,byrow=T)')
    eval('lpLowerBound_vec <- c(1000,1000,1000,0,0)')
    eval('lpUpperBound_vec <- c(6000,4000,6000,1,1)')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')

    eval('x <- tryCatch(CheckSolverResult(solution_vec,result_mat,varName_vec,callInfo_df,resourceQty_vec,minUnitValue_mat,haircut_mat,\n' +
      '                              lpLowerBound_vec,lpUpperBound_vec,operLimitMs,fungible),' +
      'error = function(e){' +
      'print(e$message)' +
      '})')
    then:
    that eval('x'), equalTo(eval('NULL') as SEXP)
  }
}
