library(tseries)
library(changepoint)
library(lubridate)
library(forecast)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

count <- read.delim("../files india/india/building/unique contributions/count.txt",sep =" ", header = FALSE, dec =".")
freq <- read.delim("../files india/india/building/unique contributions/unique freq.txt",sep =" ", header = FALSE, dec =".")

count_data <- count$V2
freq_data <- freq$V2

# # phase 1
# count_data <- count$V2[1:40]
# freq_data <- freq$V2[1:40]
# 
# # phase 2
# count_data <- count$V2[40:80]
# freq_data <- freq$V2[40:80]
# 
# # phase 3
# count_data <- count$V2[100:184]
# freq_data <- freq$V2[100:184]

count_ts <- ts(count_data, start = decimal_date(as.Date("2015-04-01")), frequency = 365/7)
freq_ts <- ts(freq_data, start = decimal_date(as.Date("2015-04-01")), frequency = 365/7)

# # phase 1
# count_ts <- ts(count_data, start = decimal_date(as.Date("2015-04-01")), frequency = 365/7)
# freq_ts <- ts(freq_data, start = decimal_date(as.Date("2015-04-01")), frequency = 365/7)
# 
# # phase 2
# count_ts <- ts(count_data, start = decimal_date(as.Date("2015-12-30")), frequency = 365/7)
# freq_ts <- ts(freq_data, start = decimal_date(as.Date("2015-12-30")), frequency = 365/7)
# 
# # phase 3
# count_ts <- ts(count_data, start = decimal_date(as.Date("2017-02-22")), frequency = 365/7)
# freq_ts <- ts(freq_data, start = decimal_date(as.Date("2017-02-22")), frequency = 365/7)

plot(count_ts)
plot(freq_ts)

chang <- cpt.mean((freq_ts),method = "AMOC",class = TRUE, param.estimates = TRUE)

par(mar = c(5, 5, 3, 5))
plot(count_ts,  ylab="Element count", xlab="Time (Weeks)", col="black")
par(new=TRUE)
plot(chang, xaxt = "n", yaxt = "n",
     ylab = "", xlab = "",lwd=2, col="black")
axis(side = 4)
mtext("Active contributors", side = 4, line = 2.5)
legend("topleft",inset = 0.01,cex =0.8 , c("Element count", "Active contributors", "Mean"),
       col = c("black", "black", "red"), lwd = c(1, 2.5, 1), box.lty = 0)
param.est(chang)
date_decimal(cpts.ts(chang))