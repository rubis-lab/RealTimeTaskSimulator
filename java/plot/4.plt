##########
#Task Information
#Number of tasks = $d
#task id  (p    , e    , d    ) + phase
#task   1 (12000000, 7139589, 9587408) + 2412591
#task   2 (12000000, 1796618, 2412591) + 0
#task   3 (600000, 131521, 108871) + 104894
#task   4 (300000, 96643, 80000) + 24894
#task   5 (300000, 24894, 24894) + 0
##########
set term post enhanced color eps;
set output 'plot/data4_true.eps';
set xlabel 'time';
set ylabel 'density';
set xrange [0:60000];
set yrange [0:];
set size 1.5,\
 1;
set key outside right reverse;
plot 'plot/data4_true' u 1:2 w l not,\
'' u 1:($3+$4+$5+$6+$7) w filledcurves t '  0 + (24894,\
 24894,\
 300000)',\
'' u 1:($3+$4+$5+$6) w filledcurves t '24894 + (96643,\
 80000,\
 300000)',\
'' u 1:($3+$4+$5) w filledcurves t '104894 + (131521,\
 108871,\
 600000)',\
'' u 1:($3+$4) w filledcurves t '  0 + (1796618,\
 2412591,\
 12000000)',\
'' u 1:($3) w filledcurves t '2412591 + (7139589,\
 9587408,\
 12000000)',\
'' u 1:($3+$4+$5+$6+$7) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5+$6) w l not lw 2 lc 'black',\
'' u 1:($3+$4+$5) w l not lw 2 lc 'black',\
'' u 1:($3+$4) w l not lw 2 lc 'black',\
'' u 1:($3) w l not lw 2 lc 'black'