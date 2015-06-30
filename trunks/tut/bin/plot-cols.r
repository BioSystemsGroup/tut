#! /bin/bash setR
###! /usr/bin/Rscript

##
# Read multiple *.csv files and plot given columns vs the 1st.
#
# Time-stamp: <2015-06-24 16:32:32 gepr>
#
#dev.off()

#argv <- commandArgs(TRUE)

if (length(argv) < 2) {
    print("Usage: plot-cols.r 'col1, col2, ...' *.csv")
    print("  e.g. plot-cols.r case-??x/case-??-LooseDyn-ObsDrug.csv")
    print("Note that columns must match across all .csv files.")
    quit()
}

columns <- as.numeric(unlist(strsplit(argv[1],',')))
files <- argv[2:length(argv)]
### determine # of plots
##nplots <- length(files)*length(columns)
##plot.cols <- round(sqrt(nplots))
### add a new row if we rounded up
##plot.rows <- ifelse(plot.cols >= sqrt(nplots), plot.cols, plot.cols+1)

plot.cols <- 2
plot.rows <- 3

#
# test for and create graphics subdirectory
#
if (!file.exists("graphics")) dir.create("graphics")

data <- vector("list")
titles <- vector("list")

ft1 <- regexpr("-Obs",files[1])
ft2 <- regexpr(".csv",files[1])
fileType <- substr(files[1],ft1+4,ft2-1)

filenum <- 1
for (file in files) {
  first <- regexpr("case-??",file) # get 1st instance of case-??
  exp <- substr(file,first,first+6)
  titles[[filenum]] <- paste(exp,fileType,sep="-")
  fileName.base <- ifelse(exists("fileName.base"),paste(fileName.base, exp ,sep="-"), exp)
  data[[filenum]] <- read.csv(file)

  filenum <- filenum+1
}
fileName.base <- paste(fileName.base,"-",fileType,sep="")

### assume all Time vectors are the same
column.1 <- colnames(data[[1]])[1]
max.x <- max(data[[1]][column.1])

for (column in columns) {
  colName <- colnames(data[[1]])[column]
  fileName <- paste("graphics/", fileName.base, "-", colName, "-%02d.png", sep="")
  png(fileName, width=1600, height=1600)
  ##fileName <- paste("graphics/", fileName.base, "-", colName, ".svg", sep="")
  ##svg(fileName, width=8, height=11)
  ##fileName <- paste("graphics/", fileName.base, "-", colName, ".pdf", sep="")
  ##pdf(fileName, width=8, height=11)
  # set margins and title, axis, and label font sizes
  par(mar=c(5,6,4,2), cex.main=3, cex.axis=3, cex.lab=3)
  par(mfrow=c(plot.rows,plot.cols))

  print(paste("Working on ",colName, sep=""))

  ## get min/max of this column over all data sets
  min.y <- Inf
  max.y <- -Inf
  for (df in data) {
    min.y <- min(min.y,min(df[colName]))
    max.y <- max(max.y, max(df[colName]))
  }
  if (min.y <= 0) min.y <- 0.00001
  # plot this column from all data sets
  ndx <- 1
  for (df in data) {
    attach(df)
    dat <- cbind(get(column.1), get(colName))
    detach(df)
    colnames(dat) <- c(column.1, colName)
    plot(dat, main=titles[[ndx]], xlim=c(0,max.x), ylim=c(min.y,max.y), #log="y"
        , type="l")
    grid()
    ndx <- ndx+1
  }
}
##stop()
q()
