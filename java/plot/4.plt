##########
#Task Information
#Number of tasks = $d
#task id  (p    , e    , d    ) + phase
#task   1 (20000, 10976, 15987) + 4012
#task   2 (20000,  2755,  4012) + 0
#task   3 (  800,   886,   484) + 314
#task   4 (  800,   395,   215) + 99
#task   5 (  800,   182,    99) + 0
##########
set term post enhanced color eps;
set output 'plot/data4_true.eps';
set xlabel 'time';
set ylabel 'density';
set xrange [0:20000];
set yrange [0:];
set size 1.5,\
 1;
set key outside right reverse;
plot 'plot/data4_true' u 1:2 w l not,\
'' u 1:($3+$4+$5+$6+$7) w filledcurves t '  0 + (182,\
  99,\
 800)',\
'' u 1:($3+$4+$5+$6) w filledcurves t ' 99 + (395,\
 215,\
 800)',\
'' u 1:($3+$4+$5) w filledcurves t '314 + (886,\
 484,\
 800)',\
'' u 1:($3+$4) w filledcurves t '  0 + (2755,\
 4012,\
 20000)',\
'' u 1:($3) w filledcurves t '4012 + (10976,\
 15987,\
 20000)',\
'' u 1:($3+$4+$5+$6+$7) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5+$6) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5) w l not lw 2 lc 'black',\
'' u 1:($3+$4) w l not lw 2 lc 'black',\
'' u 1:($3) w l not lw 2 lc 'black'