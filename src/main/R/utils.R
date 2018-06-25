
CalculateIntegralUnit <- function(amount,valuePerUnit,discount){
  # All args can be a single element or a vector
  intUnit <- ceiling(amount/(valuePerUnit*discount))
  return(intUnit)
}

UsedQtyFromResultList <- function(result_list,resource_vec,callId_vec){
  #### minUnitQuantity of resources used for allocation
  quantityUsed_vec <- rep(0,length(resource_vec))
  callNum <- length(callId_vec)
  resourceNum <- length(resource_vec)
  print('resource_vec'); print(resource_vec)
  for(i in 1:callNum){
    callId <- callId_vec[i]
    alloc_df <- result_list[[callId]]
    print('alloc_df'); print(alloc_df)
    resourceTemp_vec <- PasteResource(alloc_df$Asset,alloc_df$CustodianAccount)
    print('resourceTemp_vec'); print(resourceTemp_vec)
    idxInRes_vec <- na.omit(match(resourceTemp_vec,resource_vec))
    print('idxInRes_vec'); print(idxInRes_vec)
    if(length(idxInRes_vec)!=0){
      idxInAlloc <- match(resource_vec[idxInRes_vec],resourceTemp_vec)
      print('idxInAlloc'); print(idxInAlloc)

      quantityUsed_vec[idxInRes_vec] <- quantityUsed_vec[idxInRes_vec]+alloc_df$Quantity[idxInAlloc]
    }
  }
  return(quantityUsed_vec)
}
