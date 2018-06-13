install.packages("devtools")
devtools::install_github("twitter/BreakoutDetection")
library(BreakoutDetection)
data(Scribe)
res = breakout(Scribe, min.size=24, method='multi', beta=.001, degree=1, plot=TRUE)
res$plot