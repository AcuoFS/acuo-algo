package com.acuo.algo.generalFunctions

import com.acuo.algo.RenjinEval
import org.renjin.sexp.SEXP
import spock.lang.Specification

import static com.acuo.algo.RenjinMatchers.closeTo
import static org.hamcrest.Matchers.equalTo
import static spock.util.matcher.HamcrestSupport.that

class staticFunctionsSpec extends Specification implements RenjinEval {
    void setup() {
        eval("library(stats)")
        eval("library('com.acuo.collateral.acuo-algo')")
    }
    void "PasteResource pastes asset id and custodian account id by ---"() {
        when:
        eval('assetId_vec <- c(\'EUR\',\'USD\')')
        eval('custodianAccount_vec <- c(\'ca1\',\'ca2\')')

        eval('resource_vec <- PasteResource(assetId_vec,custodianAccount_vec)')
        eval('print(resource_vec)')
        then:
        // check resource id
        that eval('resource_vec[1]'), equalTo(c('EUR---ca1') as SEXP)
        that eval('resource_vec[2]'), equalTo(c('USD---ca2') as SEXP)
    }

    void "PasteVarName pastes statement id, call id and resource id by ___"() {
        when:
        eval('msId_vec <- c(\'ms1\',\'ms1\')')
        eval('callId_vec <- c(\'mc1\',\'mc2\')')
        eval('resource_vec <- c(\'EUR---ca1\',\'USD---ca2\')')

        eval('varName_vec <- PasteVarName(msId_vec,callId_vec,resource_vec)')
        eval('print(varName_vec)')
        then:
        // check variable names
        that eval('varName_vec[1]'), equalTo(c('ms1___mc1___EUR---ca1') as SEXP)
        that eval('varName_vec[2]'), equalTo(c('ms1___mc2___USD---ca2') as SEXP)
    }

    void "SplitResource splits resource id by ---"() {
      when:
      eval('resource_vec <- c(\'EUR---ca1\',\'USD---ca2\')')
      eval('target1 <- \'asset\'')
      eval('target2 <- \'custodianAccount\'')
      eval('target12 <- \'all\'')

      eval('assetId_vec <- SplitResource(resource_vec,target = target1)')
      eval('custodianAccount_vec <- SplitResource(resource_vec,target = target2)')
      eval('all_mat <- SplitResource(resource_vec,target = target12)')
      eval('print(all_mat)')
      then:
      // check split asset id and custodian account id
      that eval('assetId_vec[1]'), equalTo(c('EUR') as SEXP)
      that eval('custodianAccount_vec[2]'), equalTo(c('ca2') as SEXP)
      that eval('all_mat[1,]'), equalTo(c('EUR','USD') as SEXP)
    }

    void "SplitVarName split variable names by ___"() {
      when:
      eval('varName_vec <- c(\'ms1___mc1___EUR---ca1\',\'ms1___mc2___USD---ca2\')')
      eval('target1 <- \'call\'')
      eval('target2 <- \'ms\'')
      eval('target3 <- \'resource\'')
      eval('target123 <- \'all\'')

      eval('call_vec <- SplitVarName(varName_vec,target = target1)')
      eval('ms_vec <- SplitVarName(varName_vec,target = target2)')
      eval('resource_vec <- SplitVarName(varName_vec,target = target3)')
      eval('all_mat <- SplitVarName(varName_vec,target = target123)')
      eval('print(all_mat)')
      then:
      // check the split call id, statement id, resource id
      that eval('call_vec'), equalTo(c('mc1','mc2') as SEXP)
      that eval('ms_vec'), equalTo(c('ms1','ms1') as SEXP)
      that eval('resource_vec'), equalTo(c('EUR---ca1','USD---ca2') as SEXP)
    }
}
