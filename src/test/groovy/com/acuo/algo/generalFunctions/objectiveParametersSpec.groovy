package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class objectiveParametersSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "GenerateStandardizedCostMat derives the standardized cost matrix"() {
      when:
      eval('cost_mat <- matrix(c(0.0001,0.0002,0.0001,0.0002),nrow = 2, byrow = T)')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('resource_vec <- c(\'USD---ca1\',\'EUR---ca2\')')

      eval('cost_mat <- GenerateStandardizedCostMat(cost_mat,callId_vec,resource_vec)')
      eval('print(cost_mat)')
      then:
      // check the standardized cost
      that eval('cost_mat[1,]'), closeTo(c(0.7071,2.1213) as SEXP,0.0001)
      that eval('cost_mat[2,]'), closeTo(c(0.7071,2.1213) as SEXP,0.0001)
    }

    void "CostVec2Mat converts cost vector to matrix format"() {
      when:
      eval('cost_vec <- c(0.0001,0.0002,0.0001)')
      eval('availAsset_df <- data.frame(\n' +
                    'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
                    'resource = c(\'USD---ca1\',\'EUR---ca2\',\'USD---ca1\'))')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('resource_vec <- c(\'USD---ca1\',\'EUR---ca2\')')

      eval('cost_mat <- CostVec2Mat(cost_vec,availAsset_df,callId_vec,resource_vec)')
      eval('print(cost_mat)')
      then:
      // check the cost matrix
      that eval('cost_mat[1,]'), closeTo(c(0.0001, 0.0002) as SEXP,0.0001)
      that eval('cost_mat[2,]'), closeTo(c(0.0001, 0) as SEXP,0.0001)
    }

    void "GenerateStandardizedLiquidityMat derives the standardized liquidity matrix"() {
      when:
      eval('resourceLiquidity_vec <- c(0.9,0.8)')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('resource_vec <- c(\'USD---ca1\',\'EUR---ca2\')')

      eval('liquidity_mat <- GenerateStandardizedLiquidityMat(resourceLiquidity_vec,callId_vec,resource_vec)')
      eval('print(liquidity_mat)')
      then:
      // check the the minimum movement quantity of each resource
      that eval('liquidity_mat[1,]'), closeTo(c(2.1213,0.7071) as SEXP,0.0001)
    }

    void "CalculateObjParams constructs the quantity limit constraints"() {
        when: // unit = 'quantity"
        eval('cost_vec <- c(1,1.2)')
        eval('liquidity_vec <- c(0.9,0.8)')
        eval('pref_vec <- c(5,5)')
        eval('unit <- \'quantity\'')
        eval('minUnitValue_vec <- c(2,1)')

        eval('weightedScore_vec <- CalculateObjParams(cost_vec,liquidity_vec,pref_vec,unit,minUnitValue_vec)')
        eval('print(weightedScore_vec)')
        then:
        that eval('weightedScore_vec'), equalTo(c(1.9,1) as SEXP)

        when: // unit = 'amount"
        eval('cost_vec <- c(1,1.2)')
        eval('liquidity_vec <- c(0.9,0.8)')
        eval('pref_vec <- c(5,5)')
        eval('unit <- \'amount\'')

        eval('weightedScore_vec <- CalculateObjParams(cost_vec,liquidity_vec,pref_vec,unit)')
        eval('print(weightedScore_vec)')
        then:
        that eval('weightedScore_vec'), equalTo(c(0.95,1) as SEXP)
    }
}
