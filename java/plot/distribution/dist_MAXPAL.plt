set term post eps solid enhanced monochrome font 'Times-roman, 20' size 3,3;
set xlabel 'Time bound ratio';
set ylabel 'Density bound ratio';
set xrange [0:4];
set yrange [0:4];
set xtics 0,1,1
set ytics 0,1,1
set arrow from 1,0 to 1,1 nohead
set arrow from 0,1 to 1,1 nohead
set output 'plot/distribution/dist_MAXPAL.eps'
plot 'plot/distribution/dist_MAXPAL.txt' u 3:4 w p not

