#! /usr/bin/Rscript
###!/bin/bash setR

##
# Read multiple *.csv files and plot all columns vs the 1st.  Tight on
# the left, Loose on the right.
#
# Time-stamp: <2015-06-08 14:29:10 gepr>
#
#dev.off()

argv <- commandArgs(TRUE)

if (length(argv) < 2) {
    print("Usage: cmp-by-model.r <exp directories>")
    print("  e.g. cmp-by-model.r case-??x")
    print("  directories should contain files like: case-01-Loose.csv")
    print("Note that columns must match across all .csv files.")
    quit()
}

MULTIPLOT <- TRUE
PNG <- TRUE

roundDown <- function(x) {
  return (10^floor(log10(x)))
}

# determine # of plots
nplots <- length(argv)
plot.cols <- 2
plot.rows <- 3

#
# test for and create graphics subdirectory
#
if (!file.exists("graphics")) dir.create("graphics")

tight <- vector("list")
loose <- vector("list")

titles <- vector("list")

expnum <- 1
for (exp in argv) {
  titles[[expnum]] <- exp
  multiName.base <- ifelse(exists("multiName.base"),paste(multiName.base,exp,sep="-"), exp)

  files <- list.files(path=paste(exp,"/",sep=""),pattern="[.]*-Tight.csv")
  fileName <- paste(exp,"/",files,sep="")
  tight[[expnum]] <- read.csv(fileName)

  files <- list.files(path=paste(exp,"/",sep=""),pattern="[.]*-Loose.csv")
  fileName <- paste(exp,"/",files,sep="")
  loose[[expnum]] <- read.csv(fileName)

  expnum <- expnum+1
}

if (MULTIPLOT) {
  fileName <- paste("graphics/",multiName.base,sep="")
  if (PNG) png(paste(fileName,"-%02d.png",sep=""), width=1600, height=1600)
  else svg(paste(fileName,"-%02d.svg",sep=""), width=8, height=8)
  par(mfrow=c(plot.rows,plot.cols))
  ##par(lwd=3)
  par(mar=c(5,6,4,2), cex.main=2, cex.axis=1, cex.lab=2)
}

for (i in 1:length(argv)) {

  max.y <- max(max(tight[[i]][,2:3]),max(loose[[i]][,3:4]))
  ##min.y <- min(min(tight[[i]][,2:3]),min(loose[[i]][,2:3]))
  tights <- tight[[i]][,2:3]
  min.y <- min(tights[tights > 0])
  looses <- loose[[i]][,3:4]
  min.y <- min(min.y, min(looses[looses > 0]))
  min.y <- roundDown(min.y)

  ### get the ylabel for the first plot
  if (length(grep("fract", colnames(tight[[i]]))) < 1)
    ylab <- "C (mg/L)"
  else
    ylab <- "Dose Fraction"

  if (!MULTIPLOT) {
    fileName <- paste("graphics/", titles[[i]], sep="")
    if (PNG) png(paste(fileName, "-Tight.png", sep=""), width=1600, height=1600)
    else svg(paste(fileName, "-Tight.svg", sep=""), width=8, height=8)
    ##par(lwd=3)
    par(mar=c(5,6,4,2), cex.main=2, cex.axis=1, cex.lab=2)
  }
  plot(tight[[i]][,1],tight[[i]][,2],type="l",ylim=c(min.y,max.y),
       main=paste(argv[i],"Tight"),
       xlab="Time (hr)", ylab=ylab)
  lines(tight[[i]][,1],tight[[i]][,3],lty=2)
  legend("topright", legend=c("Central","Peripheral"),lty=1:2,cex=1)

  ### get the ylabel for the next plot
  if (length(grep("fract", colnames(loose[[i]]))) < 1)
    ylab <- "C (mg/L)"
  else
    ylab <- "Dose Fraction"

  if (!MULTIPLOT) {
    fileName <- paste("graphics/", titles[[i]] , sep="")
    if (PNG) png(paste(fileName, "-Loose.png", sep=""), width=1600, height=1600)
    else svg(paste(fileName, "-Loose.svg", sep=""), width=8, height=8)
    ##par(lwd=3)
    par(mar=c(5,6,4,2), cex.main=2, cex.axis=1, cex.lab=2)
  }
  plot(loose[[i]][,1],loose[[i]][,3],type="l",ylim=c(min.y,max.y),
       main=paste(argv[i],"Loose"),
       xlab="Time (hr)", ylab=ylab)
  lines(loose[[i]][,1],loose[[i]][,4],lty=2)
  legend("topright", legend=c("Central","Peripheral"),lty=1:2,cex=1)

}
#q()
