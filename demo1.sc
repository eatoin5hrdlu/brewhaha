// Utilities
s.freeAll;
s.sendMsg("/dumpOSC",1);

( // Windows only Server.local.options.device ="ASIO4ALL";
s = Server.local;
s.boot;
s.initTree;
)

(
Pbind(\note, Pseq([[0,3,7], [2,5,8], [3,7,10], [5,8,12]],3),
	  \legato, 0.4,
	  \strum, 0.04,
	  \dur, 0.5).play;
)

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
d.startEventLoop
)

(
// define a noise pulse
SynthDef("mtish", { arg freq = 1200, rate = 2, amp = 5;
	var osc, trg, mrate;
	mrate = MouseX.kr(1,5);
	trg = Decay2.ar(Impulse.ar(mrate,0,0.3), 0.01, 0.3);
	osc = {WhiteNoise.ar(trg)}.dup;
	Out.ar(0, osc); // send output to audio bus zero.
}).load(s);

// Echo effect
SynthDef("echo", { arg delay = 0.1, decay = 3;
	var in, mdelay;
	in = In.ar(0,2);
	mdelay = MouseX.kr(0.01,1);
	// use ReplaceOut to overwrite the previous contents of the bus.
	ReplaceOut.ar(0, CombN.ar(in, 0.5, mdelay, decay, 1, in));
}).load(s);

//McCartney's Babbling Brook
SynthDef(\brook,{Out.ar(0,{
({RHPF.ar(OnePole.ar(BrownNoise.ar, 0.99), LPF.ar(BrownNoise.ar, 14)
* 400 + 500, 0.03, 0.003)}!2)
+ ({RHPF.ar(OnePole.ar(BrownNoise.ar, 0.99), LPF.ar(BrownNoise.ar, 20)
* 800 + 1000, 0.03, 0.005)}!2)
* 4
})}).load(s);

// Effect Wrapper
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

// Wah
~makeEffect.value(\wah, { arg in, env, rate = 0.7, ffreq = 1200, depth = 0.8, rq = 0.1;
	// in and env come from the wrapper. The rest are controls
 	var lfo;
	lfo = LFNoise1.kr(rate, depth * ffreq, ffreq);
	RLPF.ar(in, lfo, rq, 10).distort * 0.15; },
	[0.1, 0.1, 0.1, 0.1],  // lags for rate ffreq, depth and rq
	2	// numChannels
);

// A simple reverb
~makeEffect.value(\reverb, { arg in, env;
	// in and env come from the wrapper.
	var input;
	input = in;
	16.do({ input = AllpassC.ar(input, 0.04, Rand(0.001,0.04), 3)});
	input; },
	nil,  // no lags
	2	// numChannels
);

// Rain
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

