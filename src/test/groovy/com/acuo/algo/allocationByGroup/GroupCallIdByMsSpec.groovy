package com.acuo.algo.allocationByGroup

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class GroupCallIdByMsSpec extends Specification implements RenjinEval {

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
  }

  void "GroupCallIdByMs splits call ids into several groups" (){
    when:
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\',\'mc3\'),' +
      'marginStatement=c(\'ms1\',\'ms1\',\'ms2\'),' +
      'marginType=c(\'Initial\',\'Variation\',\'Variation\'),\n' +
      'currency=c(\'USD\',\'USD\',\'USD\'),\n' +
      'callAmount=c(1000,2000,2500))')
    eval('callLimit <- 2')
    eval('msLimit <- 1')
    eval('callOrderMethod <- 3')
    eval('groupCallId_list <- GroupCallIdByMs(callLimit,msLimit,callInfo_df,callOrderMethod)')
    eval('print(groupCallId_list)')
    then:
    // check groupCallId_list
    that eval('groupCallId_list[[1]]'), equalTo(c('mc1','mc2') as SEXP)
    that eval('groupCallId_list[[2]]'), equalTo(c('mc3') as SEXP)
  }
}
