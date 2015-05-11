#! /usr/bin/Rscript
###!/bin/bash setR

###
## Read multiple *.csv files, average the 1st 1/3, 2/3, then all of them.
##
## Time-stamp: <2015-05-11 14:57:43 gepr>
##
##dev.off()

argv <- commandArgs(TRUE)

if (length(argv) < 2) {
    print("Usage: agg.r <CSV files>")
    print("  e.g. cmp-by-model.r case-23-??x/case-23-Loose.csv")
    print("  directories should contain files like: case-01-Loose.csv")
    print("Note that columns must match across all .csv files.")
    quit()
}

###
## mean the columns of the given list of data.frames
###
colAvg <- function(dfl) {
  means <- dfl[[1]][1]
  for (cnum in 2:ncol(dfl[[1]])) {
    column <- dfl[[1]][cnum]
    for (set in 2:length(dfl)) {
      column <- cbind(column, dfl[[set]][cnum])
    }
    means <- cbind(means, rowMeans(column))
  }
  colnames(means) <- colnames(dfl[[1]])
  return(means)
}


###
## read data & test for matching columns
###
dat <- vector("list")
fnum <- 1
for (file in argv) {
  dat[[fnum]] <- read.csv(file)
  columns <- colnames(dat[[fnum]])
  if (fnum > 1 && !all(columns == cols)) {
    print(paste("Columnames for file",fnum,"don't match."))
    quit()
  } else
    cols <- columns
  fnum <- fnum+1
}

###
## test for and create graphics subdirectory
###
if (!file.exists("graphics")) dir.create("graphics")

fileName <- paste("graphics/",length(dat),"-trial-mean.png",sep="")
png(fileName, width=1600, height=1236)

par(mar=c(5,6,4,2), cex.main=3, cex.axis=2, cex.lab=3)
par(mfrow=c(2,1)) # 2 rows, 1 column

cols[3] <- "Central"
cols[4] <- "Peripheral"

for (comp in seq(3,4)) {
  means <- colAvg(dat[1:length(dat)])
  plot(means[,1],means[,comp], type="l", log="y", xlab="Time (hr)", ylab="C (mg/L)",
       main=paste(cols[comp],"Compartment"))
  legend("topright", legend=paste("mean of",length(dat),"trials"), lty=1, cex=2)

  for (s in 1:3) {
    points(dat[[s]][,1], dat[[s]][,comp], pch=16, col="grey")
  }
}
