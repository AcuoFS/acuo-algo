package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class analysisFunctionsSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "LiquidFun calculates the Reserved Liquidity Ratio for available assets pool"() {
        when:
        eval('quantityLeft_vec <- c(100,200)')
        eval('quantityTotal_vec <- c(200,200)')
        eval('liquidity_vec <- c(0.8,0.5)')
        eval('minUnitValue_vec <- c(1,1)')

        eval('RLRatio <- LiquidFun(quantityLeft_vec,quantityTotal_vec,liquidity_vec,minUnitValue_vec)')
        eval('print(RLRatio)')
        then:
        that eval('RLRatio'), closeTo(c(0.6923d) as SEXP,0.0001)
    }

    void "CostDefinition defines the cost factor for assets"() {
        when:
        eval('availAsset_df <- data.frame(\n' +
                'callId = c(\'c1\',\'c1\'),\n' +
                'assetCustacId = c(\'USD---CA1\',\'NONCASH---CA2\'),\n' +
                'opptCost = c(-0.0001,-0.0001),\n' +
                'internalCost = c(0.0002,0.0001),\n' +
                'externalCost = c(0.0001,0.0001),\n' +
                'interestRate = c(-0.0001,0))\n')

        eval('resource_df <- data.frame(\n' +
                'id = c(\'USD---CA1\',\'NONCASH---CA2\'),\n' +
                'assetId = c(\'USD\',\'NONCASH\'),\n' +
                'currency = c(\'USD\',\'EUR\'))\n')

        eval('costFactor <- CostDefinition(availAsset_df,resource_df)')
        eval('print(costFactor)')
        then:
        that eval('costFactor'), closeTo(c(0.0003d,0.0002d) as SEXP,0.00001)
        // The following won't work, why?
        // that eval('cost'), equalTo(c(0.0003d,0.0002d) as SEXP)
    }

    void "CostFun calculates the cost for assets"() {
        when:
        eval('amount_vec <- c(10000,10000)')
        eval('cost_vec <- c(0.0003,0.0002)')

        eval('cost <- CostFun(amount_vec,cost_vec)')
        eval('print(cost)')
        then:
        that eval('cost'), equalTo(c(5d) as SEXP)
    }

    void "OperationFun calculates the movements for allocation"() {
        when:
        eval('result <- rbind(c(10000,0),c(50,10000))')
        eval('callInfo_df <- data.frame(\n' +
                'id = c(\'c1\',\'c2\'),\n' +
                'marginStatement = c(\'ms1\',\'ms1\')) \n')
        eval('method <- \'matrix\'')

        eval('movement <- OperationFun(result,callInfo_df,method)')
        eval('print(movement)')
        then:
        that eval('movement'), equalTo(c(2d) as SEXP)
    }
}
