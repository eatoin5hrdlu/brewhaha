# brewhaha
A Musical Instrument for Yeast


 Biology           Electronics            Software              Music

 Yeast  →  Laser → Sensor → Raspberry Pi → SuperCollider → Music


Although something similar might be achieved by a guitarist having a few beers, a fully automated system has the advantage that when we project this idea into the future, we can't imagine that the same computer operating the Fermentophone would also be driving a car.

The main controller is a Raspberry Pi ($35) with a bluetooth dongle ($4.95).  On start-up it makes contact with the bluetooth controller ($3.00) on the Sensor which is monitoring temperature, turbidity, and gas production. 

The use of current open-source computer systems (Raspberry Pi, Arduino), plus the latest in open-source sharing (Git) means that anyone interested in this exhibit can download the source and see exactly how it is working.  With the Git program they can use the command:
“git clone https://github.com/eatoin5hrdlu/brewhaha.git“ 

to get a copy of the source code and this document.

In addition to the software and schematics the CAD model and 3D printer files for the non-contact sensor are also included here.


Time Compression vs. Instantaneous Measurement

As the fementation progresses, the gas production (rhythm) will increase and decrease slowly, as does the temperature.  Turbidity will probably only increase, although there are clarifying agents we can introduce to settle out particles (or it may happen spontaneously).   The time scale of these changes is many hours (days?) so the Fermentophone will have two modes of operation.

Real time:  Sounds will be produced to represent the current values of temperature, turbidity, and gas production.

Cumulative:  A time-compressed playback of the parameters measured over a longer period of time (15 minutes, 1 hour, four hours, etc.).  For example, a 30-second playback of the cumulative data could occur every few minutes with the real time data providing a background. 
