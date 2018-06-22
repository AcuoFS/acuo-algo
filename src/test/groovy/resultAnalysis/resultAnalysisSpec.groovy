package resultAnalysis

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class resultAnalysisSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
  void "DeriveResultAnalytics derives the analytics result" (){
    when:
    eval('availAsset_df <- data.frame(\n' +
                    'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
                    'resource = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
                    'haircut = c(0,0,0),\n' +
                    'FXHaircut = c(0.08,0.08,0),\n' +
                    'externalCost = c(0.0001,0.0001,0.0001),\n' +
                    'interestRate = c(0.0001,-0.0001,0.0001),\n' +
                    'internalCost = c(0.0002,0.0001,0.0002),\n' +
                    'opptCost = c(0.0001,-0.0001,0.0001),\n' +
                    'stringsAsFactors = F)')

    eval('resource_df <- data.frame(\n' +
                    'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
                    'assetId = c(\'EUR\',\'USD\'), \n' +
                    'assetName = c(\'Euro\',\'US Dollar\'),' +
                    'qtyOri = c(5000,6000),\n' +
                    'qtyMin = c(5000,6000),\n' +
                    'qtyRes = c(0,0),\n' +
                    'unitValue = c(1,1),\n'+
                    'minUnit = c(1,1),\n' +
                    'minUnitValue = c(1,1),\n' +
                    'currency = c(\'EUR\',\'USD\'), \n' +
                    'custodianAccount = c(\'ca2\',\'ca1\'), \n' +
                    'venue = c(\'SG\',\'SG\'), \n' +
                    'FXRate = c(0.83,1),\n' +
                    'from = c(\'EUR\',\'USD\'),\n' +
                    'to = c(\'USD\',\'USD\'),\n' +
                    'oriFXRate = c(1.2,1),\n' +
                    'stringsAsFactors = F)')

    eval('callInfo_df <- data.frame(\n' +
                    'id=c(\'mc1\',\'mc2\'),' +
                    'marginStatement=c(\'ms1\',\'ms1\'),' +
                    'marginType=c(\'Initial\',\'Variation\'),\n' +
                    'currency=c(\'EUR\',\'USD\'),\n' +
                    'callAmount=c(1000,2000),\n' +
                    'FXRate=c(0.83,1),\n' +
                    'from=c(\'EUR\',\'USD\'),\n' +
                    'to=c(\'USD\',\'USD\'),\n' +
                    'oriFXRate=c(1.2,1),\n' +
                    'stringsAsFactors = F)\n')
    eval('mc1_df <- data.frame(c(\'EUR\'), c(\'ca2\'), c(\'ms1\'), c(\'mc1\'),c(830), c(830))')
    eval('colnames(mc1_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Quantity\',\'Amount(USD)\')')
    eval('mc2_df = data.frame( c(\'USD\'), c(\'ca1\'), c(\'ms1\'), c(\'mc2\'),c(3000), c(3000))')
    eval('colnames(mc2_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Quantity\',\'Amount(USD)\')')
    eval('callOutput_list <- list(mc1 = mc1_df, mc2 = mc2_df)' )

    eval('resultAnalysis <- DeriveResultAnalytics(availAsset_df,resource_df,callInfo_df,callOutput_list)')
    eval('print(resultAnalysis)')
    then:
    that eval('round(resultAnalysis$dailyCost,4)'), equalTo(c(1.149) as SEXP)
    that eval('resultAnalysis$movement'), equalTo(c(2) as SEXP)
    that eval('round(resultAnalysis$reservedLiquidityRatio,4)'), equalTo(c(0.6534) as SEXP)
  }

  void "CalculateLiquidity calculates the Reserved Liquidity Ratio for available assets pool"() {
        when:
        eval('quantityLeft_vec <- c(100,200)')
        eval('quantityTotal_vec <- c(200,200)')
        eval('liquidity_vec <- c(0.8,0.5)')
        eval('minUnitValue_vec <- c(1,1)')

        eval('RLRatio <- CalculateLiquidity(quantityLeft_vec,quantityTotal_vec,liquidity_vec,minUnitValue_vec)')
        eval('print(RLRatio)')
        then:
        that eval('RLRatio'), closeTo(c(0.6923d) as SEXP,0.0001)
    }

    void "DefineCost defines the cost factor for assets"() {
        when:
        eval('availAsset_df <- data.frame(\n' +
                'callId = c(\'c1\',\'c1\'),\n' +
                'resource = c(\'USD---CA1\',\'NONCASH---CA2\'),\n' +
                'opptCost = c(-0.0001,-0.0001),\n' +
                'internalCost = c(0.0002,0.0001),\n' +
                'externalCost = c(0.0001,0.0001),\n' +
                'interestRate = c(-0.0001,0))\n')

        eval('resource_df <- data.frame(\n' +
                'id = c(\'USD---CA1\',\'NONCASH---CA2\'),\n' +
                'assetId = c(\'USD\',\'NONCASH\'),\n' +
                'currency = c(\'USD\',\'EUR\'))\n')

        eval('costFactor <- DefineCost(availAsset_df,resource_df)')
        eval('print(costFactor)')
        then:
        that eval('costFactor'), closeTo(c(0.0003d,0.0002d) as SEXP,0.00001)
        // The following won't work, why?
        // that eval('cost'), equalTo(c(0.0003d,0.0002d) as SEXP)
    }

    void "DefineLiquidity defines the cost factor for assets"() {
      when:
      eval('availAsset_df <- data.frame(\n' +
        'callId = c(\'c1\',\'c1\'),\n' +
        'resource = c(\'USD---CA1\',\'NONCASH---CA2\'),\n' +
        'haircut = c(0,0.1),\n' +
        'FXHaircut = c(0.05,0.05),\n' +
        'opptCost = c(-0.0001,-0.0001),\n' +
        'internalCost = c(0.0002,0.0001),\n' +
        'externalCost = c(0.0001,0.0001),\n' +
        'interestRate = c(-0.0001,0))\n')

      eval('resource_df <- data.frame(\n' +
        'id = c(\'USD---CA1\',\'NONCASH---CA2\'),\n' +
        'assetId = c(\'USD\',\'NONCASH\'),\n' +
        'currency = c(\'USD\',\'EUR\'))\n')

      eval('liquidity <- DefineLiquidity(availAsset_df,resource_df)')
      eval('print(liquidity)')
      then:
      that eval('liquidity'), closeTo(c(0.9025,0.7225) as SEXP,0.0001)
    }

    void "CalculateCost calculates the daily or monthly cost for assets"() {
        when:
        eval('amount_vec <- c(10000,10000)')
        eval('cost_vec <- c(0.0003,0.0002)')
        eval('termDaily <- \'daily\'')
        eval('termMonthly <- \'monthly\'')
        eval('costDaily <- CalculateCost(amount_vec,cost_vec,termDaily)')
        eval('costMonthly <- CalculateCost(amount_vec,cost_vec,termMonthly)')
        then:
        that eval('costDaily'), equalTo(c(5) as SEXP)
        that eval('costMonthly'), equalTo(c(150) as SEXP)
    }

    void "CalculateMovement calculates the movements for allocation"() {
        when:
        eval('result <- rbind(c(10000,0),c(50,10000))')
        eval('callInfo_df <- data.frame(\n' +
                'id = c(\'c1\',\'c2\'),\n' +
                'marginStatement = c(\'ms1\',\'ms1\')) \n')
        eval('method <- \'matrix\'')

        eval('movement <- CalculateMovement(result,callInfo_df,method)')
        eval('print(movement)')
        then:
        that eval('movement'), equalTo(c(2d) as SEXP)
    }
}
