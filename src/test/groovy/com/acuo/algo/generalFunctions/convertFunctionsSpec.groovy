package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class convertFunctionsSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }

    void "ConstructAllocDf constructs allocation result data frame for one call"() {
      when:
      eval('resourceInfo_df <- data.frame(\n' +
                      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
                      'assetId = c(\'EUR\',\'USD\'), \n' +
                      'assetName = c(\'Euro\',\'US Dollar\'), \n' +
                      'unitValue = c(1,1),\n'+
                      'minUnit = c(1,1),\n' +
                      'currency = c(\'EUR\',\'USD\'), \n' +
                      'custodianAccount = c(\'ca2\',\'ca1\'), \n' +
                      'venue = c(\'SG\',\'SG\'), \n' +
                      'FXRate = c(0.83,1),\n' +
                      'from = c(\'EUR\',\'USD\'),\n' +
                      'to = c(\'USD\',\'USD\'),\n' +
                      'oriFXRate = c(1.2,1))')
      eval('callInfo_df <- data.frame(\n' +
                      'id=c(\'mc1\'),\n' +
                      'marginStatement=c(\'ms1\'),\n' +
                      'marginType=c(\'Initial\'))')
      eval('haircutC_vec <- c(0,0)')
      eval('haircutFX_vec <- c(0.08,0)')
      eval('minUnitQuantity_vec <- c(5000,6000)')
      eval('cost_vec <- c(0.0003,0.0002)')

      eval('alloc_df <- ConstructAllocDf(resourceInfo_df,callInfo_df,haircutC_vec,haircutFX_vec,minUnitQuantity_vec,cost_vec)')
      eval('print(alloc_df)')
      then:
      // check the allocation data frame
      that eval('alloc_df$Asset'), equalTo(c('EUR','USD') as SEXP)
      that eval('alloc_df$NetAmount'), closeTo(c(4600,6000) as SEXP,0.0001)
      that eval('alloc_df$`NetAmount(USD)`'), closeTo(c(5542.169,6000) as SEXP,0.001)
      that eval('alloc_df$FXRate'), equalTo(c(1.2,1) as SEXP)
      that eval('alloc_df$FXRatePerUSD'), equalTo(c(0.83,1) as SEXP)
      that eval('alloc_df$Haircut'), equalTo(c(0.08,0) as SEXP)
      that eval('alloc_df$Quantity'), equalTo(c(5000,6000) as SEXP)
    }

    void "ResultMat2List converts result matrix to list format"() {
        when:
        eval('result_mat <- matrix(c(1000,2001,-1,4000), nrow = 2,byrow = T)')
        eval('callId_vec <- c(\'mc1\',\'mc2\')')
        eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
        eval('callInfo_df <- data.frame(\n' +
                            'id=c(\'mc1\'),\n' +
                            'marginStatement=c(\'ms1\'),\n' +
                            'marginType=c(\'Initial\'))')

        eval('haircutC_mat <- matrix(c(0,0,0,0),nrow = 2,byrow = T)')
        eval('haircutFX_mat <- matrix(c(0.08,0,0,0),nrow = 2,byrow = T)')
        eval('cost_mat <- matrix(c(0.0001,0.0001,0.0001,0.0001),nrow = 2,byrow = T)')
        eval('resourceInfo_df <- data.frame(\n' +
                            'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
                            'assetId = c(\'EUR\',\'USD\'), \n' +
                            'assetName = c(\'Euro\',\'US Dollar\'), \n' +
                            'unitValue = c(1,1),\n'+
                            'minUnit = c(1,1),\n' +
                            'currency = c(\'EUR\',\'USD\'), \n' +
                            'custodianAccount = c(\'ca2\',\'ca1\'), \n' +
                            'venue = c(\'SG\',\'SG\'), \n' +
                            'FXRate = c(0.83,1),\n' +
                            'from = c(\'EUR\',\'USD\'),\n' +
                            'to = c(\'USD\',\'USD\'),\n' +
                            'oriFXRate = c(1.2,1))')

        eval('callSelect_list <- list()')
        eval('msSelect_list <- list()')

        eval('list <- ResultMat2List(result_mat,callId_vec,resource_vec,callInfo_df, haircutC_mat,haircutFX_mat,cost_mat,resourceInfo_df,\n' +
          'callSelect_list,msSelect_list)')
        eval('print(list)')
        then:
        // check the result list
        that eval('list$callSelect_list$mc1$Asset'), equalTo(c('EUR','USD') as SEXP)
      }

    void "ResultVec2Mat converts result vector to matrix format"() {
      when:
      eval('solution_vec <- c(1000,2000,4000,1,0)')
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
      eval('idxEli_vec <- c(1,3,4)')
      eval('varNum <- 3')

      eval('result_mat <- ResultVec2Mat(solution_vec,callId_vec,resource_vec,idxEli_vec,varNum)')
      eval('print(result_mat)')
      eval('rownames(result_mat) <- NULL')
      eval('colnames(result_mat) <- NULL')
      eval('print(result_mat)')
      then:
      // check the result matrix
      that eval('result_mat[1,]'), equalTo(c(1000,0) as SEXP)
      that eval('result_mat[2,]'), equalTo(c(2000,4000) as SEXP)
    }

    void "ResultList2Vec converts result list to vector format"() {
        when:
        eval('allocated_list <- list(' +
                            'mc1 = data.frame(' +
                                    'Asset= c(\'EUR\'),' +
                                    'CustodianAccount = c(\'ca2\'),' +
                                    'marginStatement = c(\'ms1\'),' +
                                    'marginCall = c(\'mc1\'),' +
                                    'Quantity = c(1000)),' +
                              'mc2 = data.frame(' +
                                    'Asset= c(\'USD\'),' +
                                    'CustodianAccount = c(\'ca1\'),' +
                                    'marginStatement = c(\'ms1\'),' +
                                    'marginCall = c(\'mc2\'),' +
                                    'Quantity = c(3000)))' )
        eval('callId_vec <- c(\'mc1\',\'mc2\')')
        eval('minUnit_vec <- c(1,1,1)')
        eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___USD---ca1\',\n' +
          '\'ms1___dummy___EUR---ca2\',\'ms1___dummy___USD---ca1\')')
        eval('varNum <- 3')
        eval('pos_vec <- c(1,2,2)')

        eval('result_vec <- ResultList2Vec(allocated_list,callId_vec,minUnit_vec,varName_vec,varNum,pos_vec)')
        eval('print(result_vec)')
        then:
        that eval('result_vec'), equalTo(c(1000, 0, 3000, 1, 1) as SEXP)

    }

    void "ResultList2AmountVec extracts allocation amount of resources from result list"() {
      when:
      eval('mc1_df <- data.frame(c(\'EUR\'), c(\'ca2\'), c(\'ms1\'), c(\'mc1\'), c(1200))')
      eval('colnames(mc1_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Amount(USD)\')')
      eval('mc2_df = data.frame( c(\'USD\'), c(\'ca1\'), c(\'ms1\'), c(\'mc2\'), c(1200))')
      eval('colnames(mc2_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Amount(USD)\')')
      eval('callOutput_list <- list(mc1 = mc1_df, mc2 = mc2_df)' )
      eval('callId_vec <- c(\'mc1\',\'mc2\')')
      eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___USD---ca1\')')

      eval('amount_vec <- ResultList2AmountVec(callOutput_list,callId_vec,varName_vec)')
      eval('print(amount_vec)')
      then:
      that eval('amount_vec'), equalTo(c(1200,0,1200) as SEXP)

    }

  void "VarVec2mat converts allocation result from vector to matrix format"() {
    when:
    eval('var_vec <- c(1200,0,1200)' )
    eval('varName_vec <- c(\'ms1___mc1___EUR---ca2\',\'ms1___mc1___USD---ca1\',\'ms1___mc2___USD---ca1\')')
    eval('callId_vec <- c(\'mc1\',\'mc2\')')
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')

    eval('result_mat <- VarVec2mat(var_vec,varName_vec,callId_vec,resource_vec)')
    eval('print(result_mat)')
    then:
    that eval('result_mat[1,1]'), equalTo(c(1200) as SEXP)
    that eval('result_mat[1,2]'), equalTo(c(0) as SEXP)
    that eval('result_mat[2,1]'), equalTo(c(0) as SEXP)
    that eval('result_mat[2,2]'), equalTo(c(1200) as SEXP)
  }

  void "ResultList2Df converts result list data frame format"() {
    when:
    eval('mc1_df <- data.frame(c(\'EUR\'), c(\'ca2\'), c(\'ms1\'), c(\'mc1\'), c(1200))')
    eval('colnames(mc1_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Amount(USD)\')')
    eval('mc2_df = data.frame( c(\'USD\'), c(\'ca1\'), c(\'ms1\'), c(\'mc2\'), c(1200))')
    eval('colnames(mc2_df) <- c(\'Asset\',\'CustodianAccount\',\'marginStatement\',\'marginCall\',\'Amount(USD)\')')
    eval('callOutput_list <- list(mc1 = mc1_df, mc2 = mc2_df)' )
    eval('callId_vec <- c(\'mc1\',\'mc2\')')

    eval('result_df <- ResultList2Df(callOutput_list,callId_vec)')
    eval('print(result_df)')
    then:
    that eval('result_df$Asset'), equalTo(c('EUR','USD') as SEXP)
    that eval('result_df$`Amount(USD)`'), equalTo(c(1200,1200) as SEXP)
  }

}
