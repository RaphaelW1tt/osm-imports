library(tseries)
library(changepoint)
library(lubridate)
library(forecast)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

count <- read.delim("../files netherlands/netherlands/BAG/unique contributions/count.txt",sep =" ", header = FALSE, dec =".")
freq <- read.delim("../files netherlands/netherlands/BAG/unique contributions/unique freq.txt",sep =" ", header = FALSE, dec =".")

count_data <- count$V2
freq_data <- freq$V2

count_ts <- ts(count_data, start = decimal_date(as.Date("2014-02-01")), frequency = 365)
freq_ts <- ts(freq_data, start = decimal_date(as.Date("2014-02-01")), frequency = 365)

plot(count_ts)
plot(freq_ts)

chang <- cpt.mean((freq_ts),method = "AMOC",class = TRUE, param.estimates = TRUE)

par(mar = c(5, 5, 3, 5))
plot(count_ts,  ylab="Element count", xlab="Time (Days)", col="black")
par(new=TRUE)
plot(chang, xaxt = "n", yaxt = "n",
     ylab = "", xlab = "",lwd=2, col="black")
axis(side = 4)
mtext("Active contributors", side = 4, line = 2.5)
legend("topleft",inset = 0.01,cex =0.8 , c("Element count", "Active contributors", "Mean"),
       col = c("black", "black", "red"), lwd = c(1, 2.5, 1), box.lty = 0)
param.est(chang)
date_decimal(cpts.ts(chang))