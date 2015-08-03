#!/bin/bash setR
###! /usr/bin/Rscript


###
## Read multiple *.csv files, average the 1st 1/3, 2/3, then all of them.
##
## Time-stamp: <2015-07-29 11:38:11 gepr>
##
##dev.off()

##argv <- commandArgs(TRUE)

if (length(argv) < 2) {
    print("Usage: agg.r <CSV files>")
    print("  e.g. cmp-by-model.r case-23-??x/case-23-Loose.csv")
    print("  directories should contain files like: case-01-Loose.csv")
    print("Note that columns must match across all .csv files.")
    quit()
}

SHOWRAW <- FALSE
PNG <- FALSE

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

roundDown <- function(x) {
  return (10^floor(log10(x)))
}

###
## get the title from the 1st argv
###
first <- regexpr("case-??",argv[1])[1]
last <- regexpr("-*x",argv[1])[1]
##exp.title <- substr(argv[1],first,first+6)
exp.title <- substr(argv[1],first,last)

###
## read data & test for matching columns
###
dat <- vector("list")
max.y <- -1e100
min.y <- 1e100
fnum <- 1
for (file in argv) {
  dat[[fnum]] <- read.csv(file)
  columns <- colnames(dat[[fnum]])
  if (fnum > 1 && !all(columns == cols)) {
    print(paste("Columnames for file",fnum,"don't match."))
    quit()
  } else
    cols <- columns

  max.y <- max(max.y, max(dat[[fnum]][,3:4]))
  col3 <- dat[[fnum]][,3]
  min.y <- min(min.y, min(col3[col3 != 0]))
  col4 <- dat[[fnum]][4]
  min.y <- min(min.y, min(col4[col4 != 0]))

  fnum <- fnum+1
}
min.y <- roundDown(min.y)

###
## set y axis units depending on column names and filename
###
if (length(grep("fract", cols)) < 1) {
  if (length(grep("Symptom",argv[1])) < 1)
    ylab <- "C (mg/L)"
  else ylab <- "Symptom"
} else
  ylab <- "Dose Fraction"

###
## test for and create graphics subdirectory
###
if (!file.exists("graphics")) dir.create("graphics")

fileName <- paste("graphics/",exp.title,"-μ-", length(dat), "-trials-%02d", sep="")
plot.title <- bquote(.(paste(exp.title,"μ"))[.(length(dat))])
if (PNG) {
  png(paste(fileName, ".png", sep=""), width=1600, height=1236)
} else svg(paste(fileName, ".svg", sep=""), width=8, height=8)
par(mar=c(5,6,4,2), cex.main=2, cex.axis=1, cex.lab=2)

cols[3] <- "Loose.Central"
cols[4] <- "Loose.Peripheral"

means <- colAvg(dat[1:length(dat)])

if (SHOWRAW) {
  ## plot 1st raw data set
  plot(dat[[1]][,1], dat[[1]][,3], type="p", log="y", ylim=c(min.y,max.y),
       xlab="Time (hr)", ylab=ylab, main=plot.title)
  ## plot remainder of data sets for compartment 2
  for (s in 2:length(dat)) {
    points(dat[[s]][,1], dat[[s]][,3], pch=16, col="grey")
  }
  ## plot all data sets for compartment 3
  for (s in 1:length(dat)) {
    points(dat[[s]][,1], dat[[s]][,4], pch=17, col="grey")
  }
} else {
  ## plot the means for compartment 2
  plot(means[,1],means[,3], type="l", log="y",
       ylim=c(min.y,max.y),
       xlab="Time (hr)", ylab=ylab, main=plot.title)
}
###
## plot column 4, compartment 3 on the same plot
###
lines(means[,1],means[,4], lty=2)


if (SHOWRAW) {
  legend("topright", legend=c(cols[3], cols[4]), lty=1:2, cex=1)
} else {
  leg.elements <- c(cols[3:4])
  leg.types <- 1:2
  legend("topright", legend=leg.elements, lty=leg.types, cex=1)
}

###
## For use when you want to interactively plot Tight Drug curves
## along with the ones you already included.  Load tight with
## something like:
## tight <- read.csv("case-04-00x/case-04-Tight-ObsDrug.csv")
###
extratight <- function() {
  lines(tight[,1],tight[,2],lty=3)
  lines(tight[,1],tight[,3],lty=4)
}
