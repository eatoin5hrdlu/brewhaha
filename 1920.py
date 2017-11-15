{
  # define a chord progression that serves as basis for the canon (change this!)
    'chords' : "C9 F7 Am7 Dm G C7",
  # scale in which to interpret these chords
  'scale' : "C",
  # realize the chords using the given number of voices (e.g. 4)
    'voices' : 5,
  # realize the chords in octave 4 (e.g. 4)
    'octave' : 4,
  # realize the chords using half notes (e.g. 1 for a whole note)
    'quarterLength' : 2,
  # number of times to spice-up the streams (e.g. 2)
    'spice_depth' : 1,
  # how many instances of the same chords to stack (e.g. 2)
    'stacking' : 1,
  # define extra transpositions for different voices (e.g. +12, -24, ...)
  # note that the currently implemented method only gives good results with multiples of 12
    'voice_transpositions' : { 'VOICE1' : 0, 'VOICE2' : 0, 'VOICE3' : -12, 'VOICE4': -24, 'VOICE5': -12 },
  # Amount of detuning for edginess
    'microtone' : 50,
}
