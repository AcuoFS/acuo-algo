package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class modelFunctionsSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "DeriveMinMoveQty calculates the minimum movement quantity of each resource based on the minimin movement value"() {

      when: // when minMoveValue <= call amount
      eval('minMoveValue <- 1000')
      eval('quantity_vec <- c(5000,6000)')
      eval('minUnitValue_vec <- c(1,1)')
      eval('callAmount_vec <- c(1000,2000)')
      eval('haircut_vec <- c(0.08,0)')

      eval('minMoveQty_vec <- DeriveMinMoveQty(minMoveValue,quantity_vec,minUnitValue_vec,callAmount_vec,haircut_vec)')
      eval('print(minMoveQty_vec)')
      then:
      // check the the minimum movement quantity of each resource
      that eval('minMoveQty_vec'), equalTo(c(1000,1000) as SEXP)

      when: // when minMoveValue > call amount
      eval('minMoveValue <- 2000')
      eval('quantity_vec <- c(5000,6000)')
      eval('minUnitValue_vec <- c(1,1)')
      eval('callAmount_vec <- c(1000,2000)')
      eval('haircut_vec <- c(0.08,0)')

      eval('minMoveQty_vec <- DeriveMinMoveQty(minMoveValue,quantity_vec,minUnitValue_vec,callAmount_vec,haircut_vec)')
      eval('print(minMoveQty_vec)')
      then:
      // check the the minimum movement quantity of each resource
      that eval('minMoveQty_vec'), closeTo(c(1087,2000) as SEXP,0.0001)
    }

    void "DeriveLowerBound calculates the lower bound of each decision variable"() {

      when: // when minMoveValue <= call amount
      eval('minMoveValue <- 1000')
      eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
        '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
      eval('quantity_vec <- c(5000,6000)')
      eval('minUnitValue_vec <- c(1,1)')
      eval('callAmount_vec <- c(1000,2000)')
      eval('haircut_vec <- c(0.08,0)')

      eval('lowerBound_vec <- DeriveLowerBound(minMoveValue,varName_vec,quantity_vec,minUnitValue_vec,callAmount_vec,haircut_vec)')
      eval('print(lowerBound_vec)')
      then:
      // check the the minimum movement quantity of each resource
      that eval('lowerBound_vec'), equalTo(c(1000,1000,0,0) as SEXP)

      when: // when minMoveValue > call amount
      eval('minMoveValue <- 2000')
      eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
        '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
      eval('quantity_vec <- c(5000,6000)')
      eval('minUnitValue_vec <- c(1,1)')
      eval('callAmount_vec <- c(1000,2000)')
      eval('haircut_vec <- c(0.08,0)')

      eval('lowerBound_vec <- DeriveLowerBound(minMoveValue,varName_vec,quantity_vec,minUnitValue_vec,callAmount_vec,haircut_vec)')
      eval('print(lowerBound_vec)')
      then:
      // check the the minimum movement quantity of each resource
      that eval('lowerBound_vec'), closeTo(c(1087,2000,0,0) as SEXP,0.0001)
    }

    void "QtyConst constructs the quantity limit constraints"() {
        when:
        eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
                                '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
        eval('varNum <- 3')
        eval('resource_vec <- c(\'r1\',\'r2\')')
        eval('quantityTotal_vec <- c(5000,6000)')

        eval('con_list <- QtyConst(varName_vec,varNum,resource_vec,quantityTotal_vec)')
        eval('print(con_list)')
        then:
        // check the number of constraints and number of decision variables
        // add 0 to integer result in that has 'L' suffix R
        that eval('dim(con_list$coef_mat)[1] + 0'), equalTo(c(2) as SEXP)
        // check first constraint
        // x1 + x3 <= 5000
        that eval('con_list$coef_mat[1,]'), equalTo(c(1,0,1,0,0) as SEXP)
        // check constraint direction
        that eval('con_list$dir_vec'), equalTo(c('<=','<=') as SEXP)
        // check constraint right hand side
        that eval('con_list$rhs_vec'), equalTo(c(5000,6000) as SEXP)
    }

    void "MarginConst constructs the margin call requirement constraints"() {
      when:
      eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
        '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
      eval('varNum <- 3')
      eval('minUnitValue_vec <- c(1,1,1)')
      eval('haircut_vec <- c( 0.1,0.2,0.1)')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('callAmount_vec <- c(1000,2000)')

      eval('con_list <- MarginConst(varName_vec,varNum,minUnitValue_vec,haircut_vec,callId_vec,callAmount_vec)')
      eval('print(con_list)')
      then:
      // check the number of constraints and number of decision variables
      // add 0 to integer result in that has 'L' suffix R
      that eval('dim(con_list$coef_mat)[1] + 0'), equalTo(c(2) as SEXP)
      // check first constraint
      // 1 * (1-0.1) * x1 + 1 * (1-0.2) * x2 >= 1000
      that eval('con_list$coef_mat[1,]'), equalTo(c(0.9,0.8,0,0,0) as SEXP)
      // check constraint direction
      that eval('con_list$dir_vec'), equalTo(c('>=','>=') as SEXP)
      // check constraint right hand side
      that eval('con_list$rhs_vec'), equalTo(c(1000,2000) as SEXP)
    }

    void "DummyConst constructs the quantity and movement decision variables' relationships constraints"() {
      when:
      eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
        '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
      eval('varNum <- 3')
      eval('quantity_vec <- c( 5000,6000,5000)')

      eval('con_list <- DummyConst(varName_vec,varNum,quantity_vec)')
      eval('print(con_list)')
      then:
      // check the number of constraints and number of decision variables
      // add 0 to integer result in that has 'L' suffix R
      that eval('dim(con_list$coef_mat)[1] + 0'), equalTo(c(4) as SEXP)
      // check first constraint
      // x1 + x3 - (5000+1) * x4 <= 0
      that eval('con_list$coef_mat[1,]'), equalTo(c(1,0,1,-5001,0) as SEXP)
      // check third constraint
      // x1 + x3 - x4 >= -0.1
      that eval('con_list$coef_mat[3,]'), equalTo(c(1,0,1,-1,0) as SEXP)
      // check constraint direction
      that eval('con_list$dir_vec'), equalTo(c('<=','<=','>=','>=') as SEXP)
      // check constraint right hand side
      that eval('con_list$rhs_vec'), equalTo(c(0,0,-0.1,-0.1) as SEXP)
    }

    void "MovementConst constructs the margin call requirement constraints"() {
      when:
      eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
        '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
      eval('varNum <- 3')
      eval('operLimitMs <- 2')
      eval('fungible <- FALSE')

      eval('con_list <- MovementConst(varName_vec,varNum,operLimitMs,fungible)')
      eval('print(con_list)')
      then:
      // check the number of constraints and number of decision variables
      // add 0 to integer result in that has 'L' suffix R
      that eval('dim(con_list$coef_mat)[1] + 0'), equalTo(c(2) as SEXP)
      // check first constraint
      // x4 + x5 <= 2
      that eval('con_list$coef_mat[1,]'), equalTo(c(0,0,0,1,1) as SEXP)
      // check second constraint
      // x4 + x5 <= 2 (redundancy in case of 1 margin statement)
      that eval('con_list$coef_mat[2,]'), equalTo(c(0,0,0,1,1) as SEXP)
      // check constraint direction
      that eval('con_list$dir_vec'), equalTo(c('<=','<=') as SEXP)
      // check constraint right hand side
      that eval('con_list$rhs_vec'), equalTo(c(2,2) as SEXP)
    }

    void "DeriveOptimalAssetsV2 constructs the objectives parameters"() {
      when:
      eval('resource_vec <- c(\'USD---ca1\',\'EUR---ca2\')')
      eval('resourceQty_vec <- c(6000,5000)')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('callAmount_vec <- c(1000,2000)')
      eval('minUnitValue_vec <- c(1,1)')
      eval('eli_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
      eval('haircut_mat <- matrix(c(0.1,0.2,0.1,0.2),nrow = 2, byrow = T)')
      eval('costScore_mat <- matrix(c(1,1.2,1,1.2),nrow = 2, byrow = T)')
      eval('liquidityScore_mat <- matrix(c(1,0.8,1,0.8),nrow = 2, byrow = T)')
      eval('pref_vec <- c(5,5)')
      eval('rounding <- 2')

      eval('optimalAsset_vec <- DeriveOptimalAssetsV2(resource_vec,resourceQty_vec,callId_vec,callAmount_vec,minUnitValue_vec,eli_mat,haircut_mat,\n' +
        '      costScore_mat,liquidityScore_mat,pref_vec,rounding)')
      eval('print(optimalAsset_vec)')
      then:
      // check the optimal resource for each call
      that eval('optimalAsset_vec'), equalTo(c('USD---ca1','USD---ca1') as SEXP)
    }

    void "DeriveVarName constructs the decision variables info"() {
      when:
      eval('callInfo_df <- data.frame(id=c(\'mc1\',\'mc2\'),\n' +
                                      'marginStatement=c(\'ms1\',\'ms1\'))')
      eval('availAsset_df <- data.frame(\n' +
        'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
        'resource = c(\'USD---ca1\',\'EUR---ca2\',\'USD---ca1\'))')
      eval('varName_vec <- DeriveVarName(callInfo_df,availAsset_df)')
      eval('print(varName_vec)')
      then:
      // check decision variables' names
      that eval('varName_vec'), equalTo(c('ms1___mc1___USD---ca1', 'ms1___mc1___EUR---ca2', 'ms1___mc2___USD---ca1', 'ms1___dummy___USD---ca1', 'ms1___dummy___EUR---ca2') as SEXP)
    }

    void "GetQtyVarNum returns the number of quantity decision variables"() {
      when:
      eval('varName_vec <- c(\'ms1___mc1___USD---ca1\', \'ms1___mc1___EUR---ca2\', \'ms1___mc2___USD---ca1\', \'ms1___dummy___USD---ca1\', \'ms1___dummy___EUR---ca2\')')
      eval('qtyVarNum <- GetQtyVarNum(varName_vec)')
      eval('print(qtyVarNum)')
      then:
      // check number of quantity deicision variables
      that eval('qtyVarNum'), equalTo(c(3) as SEXP)
    }
}
