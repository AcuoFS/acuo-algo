library(hamcrest)
library(stats)
library("com.acuo.collateral.acuo-algo")

test.df <- function() {
    df <- data.frame(x = seq(10), y = runif(10))
    x <- c(1,2)
    assertThat(df, instanceOf("data.frame"))
    assertThat(dim(df), equalTo(c(10,2)))
}