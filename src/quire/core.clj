(ns quire.core
  (:gen-class))

; (use 'quire.core :reload)

(require '[clojure.string :as str])
; (use 'overtone.live)
; (use 'overtone.inst.piano)

(defrecord Note [pitch len])
(defrecord NoteEvent [timing note])

(defn schedule-note-stream [noteStream]
  "Schedules the noteStream to be played using Overtone."
)

(defn parse-note-stream [fileContents]
  "Takes a String called fileContents and parses and tokenizes each note 
  individually until no more notes are available, throwing error if necessary.
  Returns List[NoteEvent]"
)

(defn parse-note
  "Takes a String object called noteString and validates it. 
  Either it returns Note :: {:pitch <int> :len <int>}, or an Error."
  [noteString]
  (let
    [note-info (re-find #"\([ ]*([A-G-a-g][#_]?)[ ]*([ ]+[\d]+)?[ ]*([ ]+[+-]+)?[ ]*\)" noteString)]
    (println "Note-Info: " (subvec note-info 1))
    (if (= note-info nil) 
      (println "[Parsing Error]: Couldn't find note in program")
      (if (= (note-info 1) nil)
        (println "[Syntax Error]: Note is nil")
        (hash-map 
          :pitch (note-to-pitch (subvec note-info 1))
          :len 
            (if (= (note-info 2) nil) 
              4                           ; default to quarter note
              (read-string (note-info 2))
            )
        )
      )
    )
  )

  ; (re-find #"\([ ]*([A-G-a-g][#_]?)[ ]*([ ]+[\d]+)?[ ]*([ ]+[+-]+)?[ ]*\)" noteString)
)

(defn note-to-pitch
  "Takes a note with optional sharps or flats, along with the octave,
  and returns what pitch (i.e. MIDI number) the note is."
  [note-info]
  ; These are the MIDI pitches as they correspond to middle C and friends.
  ; The octave arg can send the notes up or down as desired.
  (def note-pitches {
    "a"   57
    "a#"  58
    "b_"  58
    "b"   59
    "c"   60
    "c#"  61
    "d_"  61
    "d"   62
    "d#"  63
    "e_"  63
    "e"   64
    "f"   65
    "f#"  66
    "g_"  66
    "g"   67
    "g#"  68
    "a_"  68
  })
  (let [[note _ octave] note-info
      base-pitch (note-pitches (str/lower-case note))
      octave-up (count (filter (fn [chr] (= \+ chr)) octave))
      octave-down (count (filter (fn [chr] (= \- chr)) octave))
      octave-modifier (* 12 (- octave-up octave-down))]
    (println note base-pitch octave-modifier)
    (+ base-pitch octave-modifier)
  )
  ; TODO implement
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (def c (Note. 100 8))
  (def plunk (NoteEvent. 2 c))
  ; (println c)
  ; (println plunk)
  (println (parse-note "(A#  ++)"))
)