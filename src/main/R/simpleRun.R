simpleRun <- function(callIds,
                      pref,
                      clientId,
                      callInfoByCallId,
                      availAssetByCallIdAndClientId,
                      assetInfoByAssetId,
                      operLimitMs,
                      fungible,
                      debugMode) {
  #### parameters checking ####
  if(length(unlist(callIds))==0){
    stop('Empty callIds input!')
  }

  if(length(unlist(pref))==0){
    stop('Empty pref input!')
  }

  if(length(unlist(callInfoByCallId))==0){
    stop('Empty callInfoByCallId input!')
  }

  if(length(unlist(availAssetByCallIdAndClientId))==0){
    stop('Empty availAssetByCallIdAndClientId input!')
  }

  if(length(unlist(assetInfoByAssetId))==0){
    stop('Empty assetInfoByAssetId input!')
  }

  #### rename the parameters ####
  callId_vec <- callIds
  pref_vec <- pref
  operLimitMs <- 2
  fungible <- FALSE

  #### callInfo_df ####
  callInfo_df <- callInfoByCallId
  callInfo_df <- UnifyFxBaseUsdInCallInfo(callInfo_df)
  callInfo_df <- ConvertCallAmountToBaseCcyInCallInfo(callInfo_df)

  #### availAsset_df ####
  oriAvailAsset_df <- availAssetByCallIdAndClientId

  #### assetInfo ####
  assetInfo_df <- assetInfoByAssetId
  assetInfo_df <- assetInfo_df[order(assetInfo_df$id),]
  assetInfo_df <- UnifyFxBaseUsdInAssetInfo(assetInfo_df)
  assetInfo_df <- AddMinUnitValueInBaseCcyToAssetInfo(assetInfo_df)


  #### remove unexpected data ####
  # remove assets that have 0 unitValue from assetInfo_df and oriAvailAsset_df
  rmIdxAsset <- which(assetInfo_df$unitValue==0)
  if(length(rmIdxAsset)>0){
    rmIdxAvail <- which(oriAvailAsset_df$assetId %in% assetInfo_df$id[rmIdxAsset])
    oriAvailAsset_df <- oriAvailAsset_df[-rmIdxAvail,]
    assetInfo_df <- assetInfo_df[-rmIdxAsset,]
  }
  # remove assets that have 0 or less quantity from assetInfo_df and oriAvailAsset_df
  rmIdxAvail <- which(oriAvailAsset_df$quantity<=0)
  if(length(rmIdxAvail)>0){
    rmIdxAsset <- which(assetInfo_df$id %in% oriAvailAsset_df$assetId[rmIdxAvail])
    oriAvailAsset_df <- oriAvailAsset_df[-rmIdxAvail,]
    assetInfo_df <- assetInfo_df[-rmIdxAsset,]
  }

  #### resource_df and availAsset_df ####
  info_list <- ResourceInfoAndAvailAsset(assetInfo_df,oriAvailAsset_df)
  resource_df <- info_list$resource_df
  availAsset_df <- info_list$availAsset_df

  #### configurations #####
  configurations <- list(debugMode=debugMode)

  #### CALL THE ALLOCATION FUNCTION ###########
  algoVersion <- 2
  preAllocateEnable <- F
  compareEnable <- F
  startPoints <- 1
  controls <- list(preAllocateEnable=preAllocateEnable,compareEnable=compareEnable, startPoints=startPoints)

  # scenario 1: Algo Suggestion
  result_mat <- AllocationScenario1(configurations,callInfo_df,availAsset_df,resource_df,pref_vec,operLimitMs,fungible,
                                    algoVersion,controls,ifNewAlloc=T)
  callOutput_list <- ResultMat2CallList(result_mat,callInfo_df,availAsset_df,resource_df)
  msOutput_list <- CallList2MsList(callOutput_list,callInfo_df)
  resultAnalysis1 <- DeriveResultAnalytics(availAsset_df,resource_df,callInfo_df,callOutput_list)
  resultS1 <- list(callOutput=callOutput_list,msOutput=msOutput_list,resultAnalysis=resultAnalysis1)

  ResultList2Df(callOutput_list,callInfo_df$id)


  # scenario 2: Post Settlement Currency
  # output2 <- AllocationScenario2(configurations,callInfo_df,availAsset_df,resource_df,pref_vec,operLimitMs,fungible,
  #                                algoVersion,ifNewAlloc=T)
  # resultAnalysis2 <- DeriveResultAnalytics(availAsset_df,resource_df,callInfo_df,output2$callOutput)
  # resultS2 <- list(callOutput=output2$callOutput_list,msOutput=output2$msOutput_list,resultAnalysis=resultAnalysis2)


  # scenario 3: post least liquid assets
  # output3 <- AllocationScenario3(configurations,callInfo_df,availAsset_df,resource_df,pref_vec,operLimitMs,fungible,
  #                                algoVersion,ifNewAlloc=T)
  # resultAnalysis3 <- DeriveResultAnalytics(availAsset_df,resource_df,callInfo_df,output3$callOutput)
  # resultS3 <- list(callOutput=output3$callOutput_list,msOutput=output3$msOutput_list,resultAnalysis=resultAnalysis3)

  scenarios <- list()
  scenarios[['Algo']] <- resultS1
  #scenarios[['SettleCCY']] <- result2
  #scenarios[['LeastLiquid']] <- result3
  #### Scenario Analysis Output END #######################

  #### Noted:
  #### Things to Consider for Settlement Currency Scenario Start ################
  # limit the available assets to settlement currency
  # Whether to use the real asset data(available quantity) in the inventory
  # or use the hypothetic asset to get the result first?
  #
  # Using the real asset data may have some issues when
  # 1. the settlement currency is not in the inventory?
  # it's very rare that the client doesn't have the settlement currency
  # but if that is the case we will need to create that asset and fetch
  # the info (costs) from client(cannot be implemented in the allocation step)
  # and external service
  # 2. the settlement currency is not sufficient?
  # this we can use red line to represent the insufficient amount
  #
  # However, to get started with most scenarios, we assume that the client has
  # sufficient amount of settlment currencies.
  #### Things to Consider for Settlement Currency Scenario END ###################

  result <- resultS1
  return(result)
}

.onLoad <- function(libname, pkgname) {
  options(stringsAsFactors = FALSE)
}
