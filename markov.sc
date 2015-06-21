// Cadenas de Markov
// (C) 2007 Fernando Lopez-Lezcano

// usaremos el archivo midi que está incluido con la clase Markov de
// SuperCollider y lo procesaremos por el analizador Markov

// donde almacenar las notas
~notes = List.new;
s.freeAll;
(
s.quit;
)
s.boot;
// leamos las notas de la partitura
(
var midifile, path, div, score, tempo= 160, step=0;
// archivo midi
path ="C:/cygwin/home/peter/soundSpace/SuperCollider/MIDIFile/waechter.mid";

midifile=MIDIFile.new;
midifile.read(path);
score = midifile.scores.at(1); // score = midifile.scores.at(0); // para formato 0
score.postln;
div = midifile.division;

// imprimir sólo los mensajes de "note on" (con velocidad distinta de cero)
//
// cada evento es un arreglo que contiene:
//   offset 0: tiempo con respecto al evento anterior
//   offset 1: canal midi (comienza en 1)
//   offset 2: nota midi
//   offset 3: velocidad (0 -> note off)
//   offset 4: tiempo absoluto del evento

score.do({ arg note;
	// solo imprimir los note on
	if (note.at(3) != 0 , {
//		note.at(2).post; " ".post;
		~notes.add(note.at(2));
		~notes.add(((note.at(3)-20)*0.60)/128.0);
	})
});
~notes.postln;
"t-t-that's all folks...";
)

(
~notes.postln;
)
// entrenar una cadena de Markov de órden 2 usando nuestras notas
(
m = MarkovSetN.fill(~notes.size, Pseq.new(~notes).asStream, 2);
)
// y crear semillar para autoarrancar la cadena
m.makeSeeds;

// creamos un stream a partir de la cadena de Markov...
x = m.asStream;
200.do { x.next.post; " ".post; };

// imprimimos la cadena
(
var dict = m.dict;

dict.keys.do( {arg key;

	var size, targets;
	size = (dict.at(key)).size;
	key.post; " --> ".post;
	size.do( { arg i;
		postf("<%,%:%> ",
			// el ítem
			dict.at(key).at(i),
			// su peso
			dict.at(key).weights.at(i),
			// el número de veces que estaba presente
			dict.at(key).counts.at(i));
	} );
	"".postln;
} );

)

// un instrumento de prueba...
(
SynthDef(\highpipe,
	{arg midinote = 60, amp = 0.9, dur = 1, pan = 0, legato = 0.8;
		Out.ar(0,
			Pan2.ar(
				SinOsc.ar(Ramp.kr(midinote.midicps,0.4), mul: amp)
				*
				EnvGen.kr(Env.perc(0.4, dur*legato), doneAction: 2),
				pan
			)
		)
	}).store;
)
(
SynthDef("hotboy",
	{arg midinote = 60, amp = 0.9, dur = 1, pan = 0, legato = 0.8;
		Out.ar(0,
			Pan2.ar(
				Saw.ar(Ramp.kr(midinote.midicps,0.4), mul: amp)
				*
				EnvGen.kr(Env.perc(0.4, dur*legato), doneAction: 2),
				pan
			)
		)
	}).store;
)

SynthDescLib.global.read;

// el original sin información de ritmo
(
var note = 60, stream1, stream2;
stream1 = Pseq.new(~notes).asStream;
stream2 = Pseq.new(~notes).asStream;

r = Task({
	{Synth("highpipe",
		[
			\midinote, stream1.next,
			\dur, 0.3,
			\amp, stream1.next
		]);
		0.3.wait;
	}.loop;
}).play(SystemClock);
q = Task({
	{Synth("hotboy",
		[
			\midinote, stream2.next,
			\dur, 0.3,
			\amp, stream2.next
		]);
		0.3.wait;
	}.loop;
}).play(SystemClock)
)
(
r.play(SystemClock);
)
(
q.stop;
)
// definimos la frecuencia del instrumento basándonos en nuestra
// máquina de estados, probaremos con distintos ordenes...
(
var markov = MarkovSetN.fill(~notes.size, Pseq.new(~notes).asStream, 5),
    stream = markov.makeSeeds.asStream;

r = Task({
	{Synth("beep",
		[
			\midinote, stream.next,
			\dur, 0.2
		]);
		0.2.wait;
	}.loop;
}).play(SystemClock)
)

(
r.stop;
)