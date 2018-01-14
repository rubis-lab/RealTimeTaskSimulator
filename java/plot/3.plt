##########
#Task Information
#Number of tasks = $d
#task id  (p    , e    , d    ) + phase
#task   1 (20000000, 7139589, 10000000) + 0
#task   2 (12000000, 1796618, 7000000) + 0
#task   3 (800000, 131521, 120000) + 0
#task   4 (400000, 96643, 80000) + 0
#task   5 (300000, 24894, 50000) + 0
##########
set term post enhanced color eps;
set output 'plot/data3_true.eps';
set xlabel 'time';
set ylabel 'density';
set xrange [0:60000];
set yrange [0:];
set size 1.5,\
 1;
set key outside right reverse;
plot 'plot/data3_true' u 1:2 w l not,\
'' u 1:($3+$4+$5+$6+$7) w filledcurves t '  0 + (24894,\
 50000,\
 300000)',\
'' u 1:($3+$4+$5+$6) w filledcurves t '  0 + (96643,\
 80000,\
 400000)',\
'' u 1:($3+$4+$5) w filledcurves t '  0 + (131521,\
 120000,\
 800000)',\
'' u 1:($3+$4) w filledcurves t '  0 + (1796618,\
 7000000,\
 12000000)',\
'' u 1:($3) w filledcurves t '  0 + (7139589,\
 10000000,\
 20000000)',\
'' u 1:($3+$4+$5+$6+$7) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5+$6) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5) w l not lw 2 lc 'black',\
'' u 1:($3+$4) w l not lw 2 lc 'black',\
'' u 1:($3) w l not lw 2 lc 'black'