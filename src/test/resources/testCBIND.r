callIds <- c('mcp1','mcp5','mcp7')
assetIds <- c("AUD", "CAD", "EUR", "GBP", "HKD", "JPY" ,"SGD","USD")
call.num <- length(callIds)
asset <- length(assetIds)

base.mat <- matrix(0,nrow=call.num,ncol=asset.num, dimnames = list(callIds,assetIds))
eli.mat <- base.mat

group.callIds <- c('mcp1','mcp1','mcp1','mcp5','mcp5','mcp5','mcp5''mcp7')
group.assetIds <- c("AUD", "CAD", "EUR", "GBP", "HKD", "JPY" ,"SGD","USD")

eli.mat[cbind(group.callIds ,group.assetIds)] <- 1
print(eli.mat)