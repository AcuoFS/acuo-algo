package com.acuo.algo.allocationByGroup

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class OtherFunctionsSpec extends Specification implements RenjinEval {

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
  }

  void "ExcludeInsufficientResourceFromAllocation" (){
    when:
    eval('resource_df <- data.frame(\n' +
      'id = c(\'EUR---ca2\',\'USD---ca1\'), \n' +
      'qtyMin = c(400,2),\n' +
      'minUnit = c(1,1))')
    eval('availAsset_df <- data.frame(\n' +
      'callId = c(\'mc1\',\'mc1\',\'mc2\'),\n' +
      'resource = c(\'EUR---ca2\',\'USD---ca1\',\'EUR---ca2\'))')
    eval('callNum <- 3')
    eval('list <- ExcludeInsufficientResourceFromAllocation(resource_df,availAsset_df,callNum)')
    eval('print(list)')
    then:
    // check new callInfo_df
    that eval('list$resource_df$id'), equalTo(c('EUR---ca2') as SEXP)
    that eval('list$availAsset_df$resource'), equalTo(c('EUR---ca2','EUR---ca2') as SEXP)
  }

}
