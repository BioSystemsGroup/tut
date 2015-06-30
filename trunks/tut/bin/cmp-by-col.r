#! /usr/bin/Rscript

##
# Read multiple *.csv files and plot each column vs the 1st.
#
# Time-stamp: <2015-06-24 16:26:11 gepr>
#
#dev.off()

argv <- commandArgs(TRUE)

if (length(argv) < 2) {
    print("Usage: cmp-by-col.r <exp directories>")
    print("  e.g. cmp-by-col.r case-??x")
    print("Note that columns must match across all output.csv files.")
    quit()
}


# determine # of plots
nplots <- length(argv)
plot.cols <- round(sqrt(nplots))
# add a new row if we rounded up
plot.rows <- ifelse(plot.cols >= sqrt(nplots), plot.cols, plot.cols+1)

#
# test for and create graphics subdirectory
#
if (!file.exists("graphics")) dir.create("graphics")

data <- vector("list")
titles <- vector("list")

filenum <- 1
for (exp in argv) {
  titles[[filenum]] <- exp
  fileName.base <- ifelse(exists("fileName.base"),paste(fileName.base,exp,sep="-"), exp)
  files <- list.files(path=paste(exp,"/",sep=""),pattern="[.]*-Loose.csv")
  fileName <- paste(exp,"/",files,sep="")
  data[[filenum]] <- read.csv(fileName)

  filenum <- filenum+1
}


# assume all Time vectors are the same
columns <- colnames(data[[1]])
column.1 <- columns[1]
max.x <- max(data[[1]][column.1])
for (column in columns[2:length(columns)]) {
   fileName <- paste("graphics/", fileName.base, "-", column, ".png", sep="")
   png(fileName, width=1600, height=1600)
   # set margins and title, axis, and label font sizes
   par(mar=c(5,6,4,2), cex.main=2, cex.axis=2, cex.lab=2)
   par(mfrow=c(plot.rows,plot.cols))

   print(paste("Working on ",column, sep=""))

   ## get min/max of this column over all data sets
   min.y <- Inf
   max.y <- -Inf
   for (df in data) {
     min.y <- min(min.y,min(df[column]))
     max.y <- max(max.y, max(df[column]))
   }
   if (min.y <= 0) min.y <- 0.00001
   # plot this column from all data sets
   ndx <- 1
   for (df in data) {
       attach(df)
       dat <- cbind(get(column.1), get(column))
       detach(df)
       colnames(dat) <- c(column.1, column)
       plot(dat, main=titles[[ndx]], xlim=c(0,max.x), ylim=c(min.y,max.y),log="y",type="l")
       grid()
       ndx <- ndx+1
   }
}

q()
