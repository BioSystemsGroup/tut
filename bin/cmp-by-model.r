#! /usr/bin/Rscript
###!/bin/bash setR

##
# Read multiple *.csv files and plot all columns vs the 1st.  Tight on
# the left, Loose on the right.
#
# Time-stamp: <2015-06-10 15:29:34 gepr>
#
#dev.off()

argv <- commandArgs(TRUE)

if (length(argv) < 2) {
    print("Usage: cmp-by-model.r <exp directories>")
    print("  e.g. cmp-by-model.r case-??x")
    print("  directories should contain files like: case-01-Loose-ObsCompound.csv")
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
plot.cols <- 3
plot.rows <- 4

#
# test for and create graphics subdirectory
#
if (!file.exists("graphics")) dir.create("graphics")

tight <- vector("list")
loose <- vector("list")
looseDyn <- vector("list")

titles <- vector("list")

expnum <- 1
for (exp in argv) {
  titles[[expnum]] <- exp
  multiName.base <- ifelse(exists("multiName.base"),paste(multiName.base,exp,sep="-"), exp)

  files <- list.files(path=paste(exp,"/",sep=""),pattern="[.]*-Tight-ObsCompound.csv")
  fileName <- paste(exp,"/",files,sep="")
  tight[[expnum]] <- read.csv(fileName)

  files <- list.files(path=paste(exp,"/",sep=""),pattern="[.]*-Loose-ObsCompound.csv")
  fileName <- paste(exp,"/",files,sep="")
  loose[[expnum]] <- read.csv(fileName)

  files <- list.files(path=paste(exp,"/",sep=""),pattern="[.]*-LooseDyn-ObsCompound.csv")
  fileName <- paste(exp,"/",files,sep="")
  looseDyn[[expnum]] <- read.csv(fileName)
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

plotPlot <- function(dat, cols, name) {
  if (length(grep("fract", colnames(dat[[i]]))) < 1)
    ylab <- "C (mg/L)"
  else
    ylab <- "Dose Fraction"

  if (!MULTIPLOT) {
    fileName <- paste("graphics/", titles[[i]] , sep="")
    if (PNG) png(paste(fileName, "-", name, ".png", sep=""), width=1600, height=1600)
    else svg(paste(fileName, "-", name, ".svg", sep=""), width=8, height=8)
    ##par(lwd=3)
    par(mar=c(5,6,4,2), cex.main=2, cex.axis=1, cex.lab=2)
  }
  plot(dat[[i]][,1],dat[[i]][,cols[1]],type="l",ylim=c(min.y,max.y),
       main=paste(argv[i],name),
       xlab="Time (hr)", ylab=ylab)
  lines(dat[[i]][,1],dat[[i]][,cols[2]],lty=2)
  legend("topright", legend=c("Central","Peripheral"),lty=1:2,cex=1)
}

for (i in 1:length(argv)) {

  max.y <- max(max(tight[[i]][,2:3]),max(loose[[i]][,3:4]))
  ##min.y <- min(min(tight[[i]][,2:3]),min(loose[[i]][,2:3]))
  tights <- tight[[i]][,2:3]
  min.y <- min(tights[tights > 0])
  looses <- loose[[i]][,3:4]
  min.y <- min(min.y, min(looses[looses > 0]))
  min.y <- roundDown(min.y)

  plotPlot(tight,c(2:3),"Tight")
  plotPlot(loose,c(3:4),"Loose")
  plotPlot(looseDyn,c(3:4),"LooseDyn")

}
#q()
