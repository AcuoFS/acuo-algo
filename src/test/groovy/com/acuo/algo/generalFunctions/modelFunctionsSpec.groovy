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
    void "QtyConst constructs the quantity limit constraints"() {
        when:
        eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
                                '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
        eval('varNum <- 3')
        eval('resource_vec <- c(\'r1\',\'r2\')')
        eval('quantityTotal_vec <- c(5000,6000)')

        eval('fCon2_list <- QtyConst(varName_vec,varNum,resource_vec,quantityTotal_vec)')
        eval('print(fCon2_list)')
        then:
        // check the number of constraints and number of decision variables
        // add 0 to integer result in that has 'L' suffix R
        that eval('dim(fCon2_list$fCon2_mat)[1] + 0'), equalTo(c(2) as SEXP)
        // check first constraint
        // x1 + x3 <= 5000
        that eval('fCon2_list$fCon2_mat[1,]'), equalTo(c(1,0,1,0,0) as SEXP)
        // check constraint direction
        that eval('fCon2_list$fDir2_vec'), equalTo(c('<=','<=') as SEXP)
        // check constraint right hand side
        that eval('fCon2_list$fRhs2_vec'), equalTo(c(5000,6000) as SEXP)
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

      eval('fCon3_list <- MarginConst(varName_vec,varNum,minUnitValue_vec,haircut_vec,callId_vec,callAmount_vec)')
      eval('print(fCon3_list)')
      then:
      // check the number of constraints and number of decision variables
      // add 0 to integer result in that has 'L' suffix R
      that eval('dim(fCon3_list$fCon3_mat)[1] + 0'), equalTo(c(2) as SEXP)
      // check first constraint
      // 1 * (1-0.1) * x1 + 1 * (1-0.2) * x2 >= 1000
      that eval('fCon3_list$fCon3_mat[1,]'), equalTo(c(0.9,0.8,0,0,0) as SEXP)
      // check constraint direction
      that eval('fCon3_list$fDir3_vec'), equalTo(c('>=','>=') as SEXP)
      // check constraint right hand side
      that eval('fCon3_list$fRhs3_vec'), equalTo(c(1000,2000) as SEXP)
    }

    void "DummyConst constructs the quantity and movement decision variables' relationships constraints"() {
      when:
      eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
        '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
      eval('varNum <- 3')
      eval('quantity_vec <- c( 5000,6000,5000)')
      eval('minUnitValue_vec <- c(1,1,1)')
      eval('callAmount_vec <- c(1000,2000)')

      eval('fCon4_list <- DummyConst(varName_vec,varNum,quantity_vec,minUnitValue_vec,callAmount_vec)')
      eval('print(fCon4_list)')
      then:
      // check the number of constraints and number of decision variables
      // add 0 to integer result in that has 'L' suffix R
      that eval('dim(fCon4_list$fCon4_mat)[1] + 0'), equalTo(c(4) as SEXP)
      // check first constraint
      // x1 + x3 - (5000+1) * x4 <= 0
      that eval('fCon4_list$fCon4_mat[1,]'), equalTo(c(1,0,1,-5001,0) as SEXP)
      // check third constraint
      // x1 + x3 - x4 >= -0.1
      that eval('fCon4_list$fCon4_mat[3,]'), equalTo(c(1,0,1,-1,0) as SEXP)
      // check constraint direction
      that eval('fCon4_list$fDir4_vec'), equalTo(c('<=','<=','>=','>=') as SEXP)
      // check constraint right hand side
      that eval('fCon4_list$fRhs4_vec'), equalTo(c(0,0,-0.1,-0.1) as SEXP)
    }

    void "MoveConst constructs the margin call requirement constraints"() {
      when:
      eval('varName_vec <- c(\'ms1___mc1___r1\',\'ms1___mc1___r2\',\'ms1___mc2___r1\',\n' +
        '\'ms1___dummy___r1\',\'ms1___dummy___r2\')')
      eval('varNum <- 3')
      eval('operLimit <- 2')
      eval('operLimitMs <- 2')
      eval('fungible <- FALSE')

      eval('fCon5_list <- MoveConst(varName_vec,varNum,operLimit,operLimitMs,fungible)')
      eval('print(fCon5_list)')
      then:
      // check the number of constraints and number of decision variables
      // add 0 to integer result in that has 'L' suffix R
      that eval('dim(fCon5_list$fCon5_mat)[1] + 0'), equalTo(c(2) as SEXP)
      // check first constraint
      // x4 + x5 <= 2
      that eval('fCon5_list$fCon5_mat[1,]'), equalTo(c(0,0,0,1,1) as SEXP)
      // check second constraint
      // x4 + x5 <= 2 (redundancy in case of 1 margin statement)
      that eval('fCon5_list$fCon5_mat[2,]'), equalTo(c(0,0,0,1,1) as SEXP)
      // check constraint direction
      that eval('fCon5_list$fDir5_vec'), equalTo(c('<=','<=') as SEXP)
      // check constraint right hand side
      that eval('fCon5_list$fRhs5_vec'), equalTo(c(2,2) as SEXP)
    }

    void "ConstructModelObj constructs the objectives parameters"() {
      when:
      eval('callAmount_mat <- matrix(c(1000,1000,2000,2000),nrow = 2,byrow = T)')
      eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
      eval('haircut_mat <- matrix(c(0.1,0.2,0.1,0.2),nrow = 2, byrow = T)')
      eval('costBasis_mat <- matrix(c(0.001,0.002,0.001,0.002),nrow = 2, byrow = T)')
      eval('eli_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
      eval('callInfo_df <- data.frame(id=c(\'mc1\',\'mc2\'),\n' +
                                        'currency=c(\'EUR\',\'JPY\'))')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('resource_vec <- c(\'EUR---ca1\',\'XXX---ca2\')')

      eval('objParams_list <- ConstructModelObj(callAmount_mat,minUnitValue_mat,haircut_mat,costBasis_mat,eli_mat,\n' +
                                    'callInfo_df,callId_vec,resource_vec)')
      eval('print(objParams_list)')
      then:
      // check the cost factor for the first call
      that eval('objParams_list$cost_mat[1,]'), closeTo(c(0.7071,2.1213) as SEXP,0.0001)

      // check the liquidity factor for the first call
      that eval('objParams_list$liquidity_mat[1,]'), closeTo(c(2.1213,0.7071) as SEXP,0.0001)
    }

    void "DeriveOptimalAssetsV2 constructs the objectives parameters"() {
      when:
      eval('minUnitQuantity_mat <- matrix(c(1000,1000,2000,2000),nrow = 2,byrow = T)')
      eval('eli_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
      eval('callAmount_mat <- matrix(c(1000,1000,2000,2000),nrow = 2,byrow = T)')
      eval('haircut_mat <- matrix(c(0.1,0.2,0.1,0.2),nrow = 2, byrow = T)')
      eval('minUnitValue_mat <- matrix(c(1,1,1,1),nrow = 2, byrow = T)')
      eval('pref_vec <- c(1,1)')
      eval('objParams_list <- list(cost_mat = matrix(c(0.7071,2.1213,0.7071,2.1213),nrow = 2,byrow = T),\n' +
                                  'liquidity_mat = matrix(c(2.1213,0.7071,2.1213,0.7071),nrow = 2,byrow = T))')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('resource_vec <- c(\'EUR---ca1\',\'XXX---ca2\')')

      eval('optimalAsset_mat <- DeriveOptimalAssetsV2(minUnitQuantity_mat,eli_mat,callAmount_mat,haircut_mat,minUnitValue_mat,\n' +
                                          'pref_vec,objParams_list,callId_vec,resource_vec)')
      eval('print(optimalAsset_mat)')
      then:
      // check the optimal resource for the first call
      that eval('optimalAsset_mat[1,1]'), equalTo(c('mc1') as SEXP)
      that eval('optimalAsset_mat[1,2]'), equalTo(c('EUR---ca1') as SEXP)

      // check the optimal resource for the second call
      that eval('optimalAsset_mat[2,1]'), equalTo(c('mc2') as SEXP)
      that eval('optimalAsset_mat[2,2]'), equalTo(c('XXX---ca2') as SEXP)
    }

    void "VarInfo constructs the decision variables info"() {
      when:
      eval('eli_vec <- c(1,1,1,1)')
      eval('callInfo_df <- data.frame(id=c(\'mc1\',\'mc2\'),\n' +
                                      'marginStatement=c(\'ms1\',\'ms1\'))')
      eval('resource_vec <- c(\'EUR---ca1\',\'XXX---ca2\')')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')

      eval('var_list <- VarInfo(eli_vec,callInfo_df,resource_vec,callId_vec)')
      eval('print(var_list)')
      then:
      // check the number of quantity decision variables
      that eval('var_list$varNum + 0'), equalTo(c(4) as SEXP)
      // check the number of total decision variables
      that eval('var_list$varNum2 + 0'), equalTo(c(6) as SEXP)
      // check the positions of decision variables
      that eval('var_list$pos_vec + 0'), equalTo(c(1,2,1,2) as SEXP)
      // check the first decision variable name
      that eval('var_list$varName_vec[1]'), equalTo(c('ms1___mc1___EUR---ca1') as SEXP)
      // check the first decision variable name
      that eval('var_list$varName_vec[6]'), equalTo(c('ms1___dummy___XXX---ca2') as SEXP)
    }
}
