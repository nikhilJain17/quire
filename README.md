# quire

## Description
A simple music language / piano written in Clojure.

## Disclaimer
I wrote this as my first Clojure project to learn the language, so please forgive my lack of music knowledge.

## Language
Pitch is the note you want to play, where "#" is a sharp and "\_" is a flat. "R" and "r" correspond to a rest.
```
pitch := "a_" | "A_" | "a" | "A" | "a#" | "A#" | "b_" | "B_" | "b" | "B" | "c" | "C" | "c#" | "C#" | "d_" | "D_" | "d" | "D" | "d#" | "D#" | "e_" | "E_" | "e" | "E" | "f" | "F" | "f#" | "F#" | "g_" | "G_" | "g" | "G" | "r" | "R"
```

Length is the type of note -- i.e. quarter note, eighth note, and so on. If omitted, it is a quarter note.
```
length := ε | "1" | "2" | "4" | "8" | "16" | "32" | "64" | "128"
```

Octaves are series of strings composed of "+" and "-" to shift the octave up and down from middle C. If omitted, it is the 4th octave.
```
octave := ε | "+" | "-" | "+" | "+" octave | "-" octave 
```

```
note := ε | '(' pitch length octave ')'
```

A song is a comma separated list of notes.
```
song := ε | note | note "," song
```

## Features
Write your song in a .quire file and play it from the program. Alternatively, you can load up the Clojure REPL and play songs from there.

## Future Work
There are plenty of features to add. Keep in mind that my knowledge of music is limited, so this is not an exhaustive list.
- specify tempo
- specify key
- more ergonomic language?  
  • make a song space separated instead of comma separated

