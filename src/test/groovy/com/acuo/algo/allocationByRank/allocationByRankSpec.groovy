package com.acuo.algo.allocationByRank

import com.acuo.algo.NativeUtils
import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class allocationByRankSpec extends Specification implements RenjinEval {
  static {
    NativeUtils.load("lpsolve55", "lpsolve55j")
  }

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
    eval('import(lpsolve.LpSolve)')
  }

  void "AllocateByRank" (){
    when:
    eval('callInfo_df <- data.frame(\n' +
                      'id = c(\'mc1\',\'mc2\'),\n' +
                      'marginStatement = c(\'ms1\',\'ms1\'),\n' +
                      'callAmount = c(1000,2000))')
    eval('resource_df <- data.frame(\n' +
                      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
                      'qtyMin = c(5000,6000),\n' +
                      'minUnitValue = c(1,1))')
    eval('availAsset_df <- data.frame(\n' +
                      'callId = c(\'mc1\',\'mc2\',\'mc2\'),\n' +
                      'resource = c(\'EUR---ca2\',\'EUR---ca2\',\'USD---ca1\'),\n' +
                      'haircut = c(0,0,0),\n' +
                      'FXHaircut = c(0.08,0.08,0))')
    eval('costScore_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('liquidityScore_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')
    eval('pref_vec <- c(5,5)')
    eval('operLimitMs <- 2')
    eval('fungible <- FALSE')
    eval('result_mat <- AllocateByRank(costScore_mat,liquidityScore_mat,pref_vec,callInfo_df,resource_df,availAsset_df,\n' +
      '                           operLimitMs,fungible)')
    eval('print(result_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    // check allocation result
    that eval('result_mat[1,]'), equalTo(c(1087,0) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,2000) as SEXP)
  }

  void "AllocateFirstCall " (){
    when:
    eval('result_mat <- matrix(c(0,0,0,0),nrow=2,byrow=T,' +
      '                   dimnames=list(c(\'mc1\',\'mc2\'),c(\'EUR---ca2\',\'USD---ca1\')))')
    eval('score_vec <- c(1,2)')
    eval('movementLimit <- 2')
    eval('idxCall <- 1')
    eval('callAmount <- 3000')
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('resourceQty_vec <- c(5000,6000)')
    eval('minUnitValue_vec <- c(1,1)')
    eval('haircut_vec <- c(0.1,0.2)')
    eval('eli_vec <- c(1,1)')

    eval('result_mat <- AllocateFirstCall(result_mat,score_vec,movementLimit,idxCall,callAmount,resource_vec,resourceQty_vec,minUnitValue_vec,\n' +
      '                              haircut_vec,eli_vec)')
    eval('print(result_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    // check result
    that eval('result_mat[1,]'), equalTo(c(3334,0) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,0) as SEXP)
  }

  void "AllocateAnotherCall" (){
    when:
    eval('result_mat <- matrix(c(2000,0,0,0),nrow=2,byrow=T,' +
      '                   dimnames=list(c(\'mc1\',\'mc2\'),c(\'EUR---ca2\',\'USD---ca1\')))')
    eval('score_vec <- c(2,1)')
    eval('movementLimit <- 0')
    eval('idxCall <- 2')
    eval('idxCall0 <- 1')
    eval('callAmount <- 3000')
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('resourceQty_vec <- c(5000,6000)')
    eval('minUnitValue_vec <- c(1,1)')
    eval('haircut_vec <- c(0.1,0.2)')
    eval('eli_vec <- c(1,1)')

    eval('result_mat <- AllocateAnotherCall(result_mat,score_vec,movementLimit,idxCall,idxCall0,callAmount,resource_vec,resourceQty_vec,minUnitValue_vec,\n' +
      '                                haircut_vec,eli_vec)')
    eval('print(result_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    // check result
    that eval('result_mat[1,]'), equalTo(c(2000,0) as SEXP)
    that eval('result_mat[2,]'), closeTo(c(3334,0) as SEXP,0.0001)
  }

  void "AllocateLargerCallFirst" (){
    when:
    eval('idxCallLarger <- 1')
    eval('idxCallSmaller <- 2')
    eval('callAmountLarger <- 3000')
    eval('callAmountSmaller <- 1000')
    eval('result_mat <- matrix(c(0,0,0,0),nrow=2,byrow=T,' +
      '                   dimnames=list(c(\'mc1\',\'mc2\'),c(\'EUR---ca2\',\'USD---ca1\')))')
    eval('score_mat <- matrix(c(2000,1000,0,3000),nrow=2,byrow=T)')
    eval('operLimitMs <- 2')
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('score_vec <- c(1,2)')
    eval('resourceQty_vec <- c(5000,6000)')
    eval('minUnitValue_vec <- c(1,1)')
    eval('haircut_mat <- matrix(c(0.1,0.2,0.1,0.2),nrow=2,byrow=T)')
    eval('eli_mat <- matrix(c(1,1,1,1),nrow=2,byrow=T)')

    eval('result_mat <- AllocateLargerCallFirst(idxCallLarger,idxCallSmaller,callAmountLarger,callAmountSmaller,\n' +
      '                                    result_mat,score_mat,operLimitMs,resource_vec,resourceQty_vec,minUnitValue_vec,haircut_mat,eli_mat)')
    eval('print(result_mat)')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')
    then:
    // check result
    that eval('result_mat[1,]'), equalTo(c(0,3750) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(1112,0) as SEXP)
  }

  void "AllocateWithinMovements" (){
    when:
    eval('result_mat <- matrix(c(0,0,0,0),nrow=2,byrow=T,' +
      '                   dimnames=list(c(\'mc1\',\'mc2\'),c(\'EUR---ca2\',\'USD---ca1\')))')
    eval('idxCall <- 1')
    eval('leftCallAmount <- 1000')
    eval('movementLeft <- 1')
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('score_vec <- c(1,2)')
    eval('resourceQty_vec <- c(5000,6000)')
    eval('haircut_vec <- c(0.1,0.2)')
    eval('minUnitValue_vec <- c(1,1)')
    eval('idx_vec <- c(1,2)')
    eval('idx0Move_vec <- c()')

    eval('list <- AllocateWithinMovements(result_mat,idxCall,leftCallAmount,movementLeft,resource_vec,score_vec,resourceQty_vec,\n' +
      '                                    haircut_vec,minUnitValue_vec,idx_vec,idx0Move_vec)')
    eval('print(list)')
    eval('result_mat <- list$result_mat')
    eval('rownames(result_mat) <- NULL')
    eval('colnames(result_mat) <- NULL')

    then:
    // check result
    that eval('result_mat[1,]'), equalTo(c(1112,0) as SEXP)
    that eval('result_mat[2,]'), equalTo(c(0,0) as SEXP)
    that eval('list$leftCallAmount'), equalTo(c(0) as SEXP)
  }

  void "DetermineOptimalResourceByRank decides the optimal resource based on resource's ranking" (){
    when:
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('score_vec <- c(1,2)')
    eval('resourceQty_vec <- c(5000,6000)')
    eval('haircut_vec <- c(0.1,0.2)')
    eval('minUnitValue_vec <- c(1,1)')
    eval('dominator <- \'amount\'')
    eval('resourceSelected <- DetermineOptimalResourceByRank(resource_vec,score_vec,resourceQty_vec,haircut_vec,minUnitValue_vec,\n' +
      '                                           dominator)')
    then:
    // check solver status
    that eval('resourceSelected'), equalTo(c('USD---ca1') as SEXP)

  }

  void "SelectResourceAmountFirst decides the resource ranking dominated by resource's amount" (){
    when:
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('score_vec <- c(1,2)')
    eval('resourceQty_vec <- c(5000,6000)')
    eval('haircut_vec <- c(0.1,0.2)')
    eval('minUnitValue_vec <- c(1,1)')
    eval('resourceSelected <- SelectResourceAmountFirst(resource_vec,score_vec,resourceQty_vec,haircut_vec,minUnitValue_vec)')
    then:
    // check the resource selected
    that eval('resourceSelected'), equalTo(c('USD---ca1') as SEXP)
  }

  void "SelectResourceScoreFirst decides the resource ranking dominated by resource's score" (){
    when:
    eval('resource_vec <- c(\'EUR---ca2\',\'USD---ca1\')')
    eval('score_vec <- c(1,2)')
    eval('resourceQty_vec <- c(5000,6000)')
    eval('haircut_vec <- c(0.1,0.2)')
    eval('minUnitValue_vec <- c(1,1)')
    eval('resourceSelected <- SelectResourceScoreFirst(resource_vec,score_vec,resourceQty_vec,haircut_vec,minUnitValue_vec)')
    then:
    // check the resource selected
    that eval('resourceSelected'), equalTo(c('EUR---ca2') as SEXP)
  }
}
