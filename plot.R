
x <- read.table("plot.x")[[1]]
x <- x - x[1]

y <- read.table("plot.y")[[1]]

svg("plot.svg", width= 16, height=8)
plot(x=x, y=y, type='l', xlab = "Time (s)", ylab = "Number of processes")
dev.off()