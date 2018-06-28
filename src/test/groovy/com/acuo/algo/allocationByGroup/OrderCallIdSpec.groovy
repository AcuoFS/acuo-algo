package com.acuo.algo.allocationByGroup

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class OrderCallIdSpec extends Specification implements RenjinEval {

  void setup() {
    eval("library(stats)")
    eval("library('com.acuo.collateral.acuo-algo')")
  }

  void "OrderCallId sorts the call ids based on method 0" (){
    when:
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\',\'mc3\'),' +
      'marginStatement=c(\'ms1\',\'ms1\',\'ms2\'),' +
      'marginType=c(\'Initial\',\'Variation\',\'Variation\'),\n' +
      'currency=c(\'USD\',\'USD\',\'USD\'),\n' +
      'callAmount=c(1000,2000,2500))')
    eval('callOrderMethod <- 0')
    eval('newCallInfo_df <- OrderCallId(callOrderMethod,callInfo_df)')
    eval('print(newCallInfo_df)')
    then:
    // check new callInfo_df
    that eval('newCallInfo_df$id'), equalTo(c('mc1','mc2','mc3') as SEXP)
  }

  void "OrderCallId sorts the call ids based on method 1" (){
    when:
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\',\'mc3\'),' +
      'marginStatement=c(\'ms1\',\'ms1\',\'ms2\'),' +
      'marginType=c(\'Initial\',\'Variation\',\'Variation\'),\n' +
      'currency=c(\'USD\',\'USD\',\'USD\'),\n' +
      'callAmount=c(1000,2000,2500))')
    eval('callOrderMethod <- 1')
    eval('newCallInfo_df <- OrderCallId(callOrderMethod,callInfo_df)')
    eval('print(newCallInfo_df)')
    then:
    // check new callInfo_df
    that eval('newCallInfo_df$id'), equalTo(c('mc3','mc2','mc1') as SEXP)
  }

  void "OrderCallId sorts the call ids based on method 2" (){
    when:
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\',\'mc3\'),' +
      'marginStatement=c(\'ms1\',\'ms1\',\'ms2\'),' +
      'marginType=c(\'Initial\',\'Variation\',\'Variation\'),\n' +
      'currency=c(\'USD\',\'USD\',\'USD\'),\n' +
      'callAmount=c(1000,2000,2500))')
    eval('callOrderMethod <- 2')
    eval('newCallInfo_df <- OrderCallId(callOrderMethod,callInfo_df)')
    eval('print(newCallInfo_df)')
    then:
    // check new callInfo_df
    that eval('newCallInfo_df$id'), equalTo(c('mc3','mc2','mc1') as SEXP)
  }

  void "OrderCallId sorts the call ids based on method 3" (){
    when:
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\',\'mc3\'),' +
      'marginStatement=c(\'ms1\',\'ms1\',\'ms2\'),' +
      'marginType=c(\'Initial\',\'Variation\',\'Variation\'),\n' +
      'currency=c(\'USD\',\'USD\',\'USD\'),\n' +
      'callAmount=c(1000,2000,2500))')
    eval('callOrderMethod <- 3')
    eval('newCallInfo_df <- OrderCallId(callOrderMethod,callInfo_df)')
    eval('print(newCallInfo_df)')
    then:
    // check new callInfo_df
    that eval('newCallInfo_df$id'), equalTo(c('mc1','mc2','mc3') as SEXP)
  }

  void "OrderCallId sorts the call ids based on method 4" (){
    when:
    eval('callInfo_df <- data.frame(\n' +
      'id=c(\'mc1\',\'mc2\',\'mc3\'),' +
      'marginStatement=c(\'ms1\',\'ms1\',\'ms2\'),' +
      'marginType=c(\'Initial\',\'Variation\',\'Variation\'),\n' +
      'currency=c(\'USD\',\'USD\',\'USD\'),\n' +
      'callAmount=c(1000,2000,2500))')
    eval('callOrderMethod <- 4')
    eval('newCallInfo_df <- OrderCallId(callOrderMethod,callInfo_df)')
    eval('print(newCallInfo_df)')
    then:
    // check new callInfo_df
    that eval('newCallInfo_df$id'), equalTo(c('mc3','mc1','mc2') as SEXP)
  }
}
