#! /bin/bash setR
###! /usr/bin/Rscript

##
# Read multiple *.csv files and plot given columns vs the 1st.
#
# Time-stamp: <2015-07-14 15:32:07 gepr>
#
#dev.off()

#argv <- commandArgs(TRUE)

if (length(argv) < 2) {
    print("Usage: mexplot.r <ind axis> <exp dirs>")
    print("  e.g. mexplot.r morbidity 2015-07-09-*")
    quit()
}

library(rjson)

ind.var <- argv[1]
exp.dirs <- argv[2:length(argv)]
ind.vals <- vector()
dep.dfs <- list()
ndx <- 1
for (dir in exp.dirs) {
  ## get independent variable value from JSON file
  parm.file <- list.files(dir,pattern="parameters-*",full.names=TRUE)
  parm.json <- fromJSON(file=parm.file)
  ind.val <- as.numeric(parm.json$looseDyn[ind.var])

  symptom.file <- list.files(dir,pattern="*-LooseDyn-ObsSymptom.csv",full.names=TRUE)
  exp.df <- read.csv(symptom.file)

  ## get the index that contains this independent value, if any
  ind.ndx <- which(ind.vals == ind.val)

  if (length(ind.ndx) == 0) {  # if this ind.val isn't there, add new one
    ind.vals[ndx] <- ind.val
    tmp <- list()
    tmp[[1]] <- exp.df
    dep.dfs[[ndx]] <- tmp
    ndx <- ndx+1
  } else { # if this ind.val is there, add this DF to the others for ind.val
    prev.length <- length(dep.dfs[[ind.ndx]])
    dep.dfs[[ind.ndx]][[prev.length+1]] <- exp.df
  }

}

## average over dep.dfs[][1..n]
dep.dfs.avg <- list()
for (mNdx in 1:length(dep.dfs)) { # pretend for now
  for (dfNdx in 1:length(dep.dfs[[mNdx]])) {
    if (exists("deps"))
      deps <- cbind(deps, dep.dfs[[mNdx]][[dfNdx]][[3]])  # column 3 = comp1
    else
      deps <- dep.dfs[[mNdx]][[dfNdx]][[3]]
  }
  dep.dfs.avg[[mNdx]] <- rowMeans(deps)
  rm(deps)
}

time.min = 98.0
time.max = 100.0
attach(dep.dfs[[1]][[1]])
time.min.ndx <- which(Time == time.min)
time.max.ndx <- which(Time == time.max)-1
detach(dep.dfs[[1]][[1]])

dep.sliced.avg <- vector()
dep.sliced.max <- vector()
dep.sliced.min <- vector()
for (ndx in 1:length(dep.dfs.avg)) {
  dep.sliced.avg[ndx] <- mean(dep.dfs.avg[[ndx]][time.min.ndx:time.max.ndx])
  trialMax <- -Inf
  trialMin <- Inf
  for (trial in length(dep.dfs[[ndx]])) {
    trialMax <- max(trialMax, max(dep.dfs[[ndx]][[trial]][time.min.ndx:time.max.ndx,3]))
    trialMin <- min(trialMin, min(dep.dfs[[ndx]][[trial]][time.min.ndx:time.max.ndx,3]))
  }
  dep.sliced.max[ndx] <- trialMax
  dep.sliced.min[ndx] <- trialMin
}

fileName <- paste("graphics/symptom-vs-",ind.var,"[",time.min,",",time.max,")",sep="")
svg(paste(fileName, ".svg", sep=""), width=8, height=8)
par(mar=c(5,6,4,2), cex.main=2, cex.axis=1, cex.lab=2)

plot(ind.vals,dep.sliced.avg,xlab=ind.var, ylab=paste("μ(Symptom(t)) t∈[",time.min,",",time.max,")"), type="p", pch=".",ylim=c(0,20))
##points(ind.vals, dep.sliced.max, pch="-")
##points(ind.vals, dep.sliced.min, pch="-")

q()
