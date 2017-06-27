##########
#Task Information
#Number of tasks = $d
#task id  (p    , e    , d    ) + phase
#task   1 (30000, 10976, 16000) + 0
#task   2 (20000,  2755,  6000) + 0
#task   3 (  800,   174,   600) + 0
#task   4 ( 1000,   229,   600) + 0
#task   5 ( 1000,   886,   600) + 0
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
'' u 1:($3+$4+$5+$6+$7) w filledcurves t '  0 + (886,\
 600,\
 1000)',\
'' u 1:($3+$4+$5+$6) w filledcurves t '  0 + (229,\
 600,\
 1000)',\
'' u 1:($3+$4+$5) w filledcurves t '  0 + (174,\
 600,\
 800)',\
'' u 1:($3+$4) w filledcurves t '  0 + (2755,\
 6000,\
 20000)',\
'' u 1:($3) w filledcurves t '  0 + (10976,\
 16000,\
 30000)',\
'' u 1:($3+$4+$5+$6+$7) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5+$6) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5) w l not lw 2 lc 'black',\
'' u 1:($3+$4) w l not lw 2 lc 'black',\
'' u 1:($3) w l not lw 2 lc 'black'