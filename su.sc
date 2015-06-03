s.freeAll;

( // Windows only Server.local.options.device ="ASIO4ALL";
s = Server.local;
s.boot;
s.initTree;
)

(
c = Buffer.read(s, "/home/pi/src/brewhaha/Cowbell.wav");
d = Buffer.read(s, "/home/pi/src/brewhaha/bleep.wav");
e = Buffer.read(s, "/home/pi/src/brewhaha/takeoff.wav");
)
e.postln;
c.postln;

(
x = SynthDef(\cowbell, { arg out = 0, bufnum = 2;
		 Out.ar( out,

			 PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum),loop:0)
           	   )
                       }
           ).load(s);
)

(
t = SynthDef("takeoff", { arg out = 0, bufnum = 0;
		 Out.ar( out,

			 PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum),loop:1.0)
           	   )
                       }
           ).load(s);
)

(
p = Synth(\cowbell);
)
s.plotTree;
u.run;

Pbind(\degree, Pseries(0,1,30),\dur, 1.0).play;


Pbind(\instrument, \cowbell, \bufnum, 2, \dur, Pseries(2,-0.3,32)).play;

(
Pbind(\note, Pseq([[0,3,7], [2,5,8], [3,7,10], [5,8,12]],3),
	  \legato, 0.4,
	  \strum, 0.04,
	  \dur, 0.5).play;
)


s.sendMsg(\n_set,1001, \gate, 1);

s.sendMsg(\play,1000);

(
s.z.play(s);
)






// now play it
(
SynthDef("tutorial-PlayBuf",{ arg out = 0, bufnum;
	Out.ar( out,
		PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum))
	)
}).play(s,[\bufnum, b.bufnum ]);
)


(
x.free; b.free;
)

s.queryAllNodes;

(
play{
	BPF.ar(
		Mix(
			Pulse.ar(587.3*[1,1.5074]))*EnvGen.ar(Env([0,1,0.1,0],[0.0005, 0.015,0.283]),
				Impulse.ar(2)),2640,0.9)!2}

)

(
s.sendMsg("/n_free", 1001);
s.freeAll;
)

//		[evtType.asHexString(4), evtCode.asHexString(4), evtValue].postln;


(
d = LID("/dev/input/event1");
t = Synth.new("mtish");
~e = Synth.new("echo");

d.slot(0x01,0x120).action = { arg slot ;
	var ndly = (slot.value/2.0)+0.01;
	ndly.postln;
	s.sendMsg(\n_set,1001,\delay, ndly);
}
)


(
(
d.startEventLoop
)

s.freeAll;
s.sendMsg("/dumpOSC",1);
(
x = {
    MdaPiano.ar(
        LFNoise0.kr(1).range(20, 60).round.midicps, // random note
        stereo: 0.5,
        gate: LFPulse.kr(1),
        vel: LFPar.kr(0.1).range(10, 100), // varying velocity
        mul: 0.2
    )
}.play
)
(
SynthDef(\bleep, { Out.ar(0, {
            SinOsc.ar(LFNoise0.kr(5).range(20,100).round.midicps, mul: 0.1 )
                             }
                         )
                 }
        ).load(s);
)
(
// define a noise pulse
SynthDef("mtish", { arg freq = 1200, rate = 2, amp = 5;
	var osc, trg, mrate;
	mrate = MouseX.kr(1,5);
	trg = Decay2.ar(Impulse.ar(rate,0,0.3), 0.01, 0.3);
	osc = {WhiteNoise.ar(trg)}.dup;
	Out.ar(0, osc); // send output to audio bus zero.
}).load(s);
)


(
// define an echo effect
SynthDef("echo", { arg delay = 0.1, decay = 3;
	var in, mdelay;
	in = In.ar(0,2);
	//	mdelay = MouseX.kr(0.01,1);
	// use ReplaceOut to overwrite the previous contents of the bus.
	ReplaceOut.ar(0, CombN.ar(in, 0.5, delay, decay, 1, in));
}).load(s);
)
)

//McCartney's Babbling Brook
(
SynthDef(\brook,{Out.ar(0,{
({RHPF.ar(OnePole.ar(BrownNoise.ar, 0.99), LPF.ar(BrownNoise.ar, 14)
* 400 + 500, 0.03, 0.003)}!2)
+ ({RHPF.ar(OnePole.ar(BrownNoise.ar, 0.99), LPF.ar(BrownNoise.ar, 20)
* 800 + 1000, 0.03, 0.005)}!2)
* 4
})}).load(s);
)

(
// EFFECT WRAPPER
~makeEffect = { arg name, func, lags, numChannels = 2;

	SynthDef(name, { arg i_bus = 0, gate = 1, wet = 1;
	 	var in, out, env, lfo;
	 	in = In.ar(i_bus, numChannels);
		env = Linen.kr(gate, 2, 1, 2, 2); // fade in the effect

		// call the wrapped function. The in and env arguments are passed to the function
		// as the first two arguments (prependArgs).
		// Any other arguments of the wrapped function will be Controls.
		out = SynthDef.wrap(func, lags, [in, env]);

		XOut.ar(i_bus, wet * env, out);
	}, [0, 0, 0.1] ).load(s);

};
)

(
// now make a wah

~makeEffect.value(\wah, { arg in, env, rate = 0.7, ffreq = 1200, depth = 0.8, rq = 0.1;
	// in and env come from the wrapper. The rest are controls
 	var lfo;
	lfo = LFNoise1.kr(rate, depth * ffreq, ffreq);
	RLPF.ar(in, lfo, rq, 10).distort * 0.15; },
	[0.1, 0.1, 0.1, 0.1],  // lags for rate ffreq, depth and rq
	2	// numChannels
);
)

(
// now make a simple reverb
~makeEffect.value(\reverb, { arg in, env;
	// in and env come from the wrapper.
	var input;
	input = in;
	16.do({ input = AllpassC.ar(input, 0.04, Rand(0.001,0.04), 3)});
	input; },
	nil,  // no lags
	2	// numChannels
);
)

(
 SynthDef(\rain, {arg out=0,rate=3;
		var x,y;
		x = MouseX.kr(0.01,1);
		y = MouseY.kr(0.01,1);
		Out.ar(out,{
		{ //RHPF.ar(
		   OnePole.ar(
			  Decay2.ar(Dust2.ar(rate), mul: PinkNoise.ar(0.2)),
						 x)
		 //   y* 400+300 + 500, x*0.8, 0.3)
		} ! 2})}).load(s);
)

