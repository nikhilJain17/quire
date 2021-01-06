(ns quire.core
  (:gen-class))

; (use 'quire.core :reload)

; @TODO
; - support custom tempo
; - add more instruments

(require '[clojure.string :as str])
; (use 'overtone.live)
; (use 'overtone.inst.piano)

(defrecord Note [pitch len])
(defrecord NoteEvent [timing note])

(defn schedule-note-stream [note-stream]
  "Schedules the noteStream to be played using Overtone."
  (loop [note-stream note-stream
      curr-time 0]

      (if (= 0 (count note-stream))
        (println "done")
        (do
          (println note-stream curr-time)
          (at (+ (now) curr-time) (piano :note (note "C4")) 1000)
          (recur (rest note-stream) (+ curr-time 1000))
        )
      )

  )
)
; (at (+ 5000 (now)) (piano :note (note "C4")) 1000)

; curr_time = 0
; for note in notestream:
;   at curr-time note
;   curr_time += note.type * tempo

(defn looper-test [x]
  (loop [sum 0
        i 0]
    (println i sum)
    (if (= i x)
      [sum]
      (recur (+ sum i) (+ i 1))
    )
  )
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
    "r"   0
  })
  (let [[note _ octave] note-info
      base-pitch (note-pitches (str/lower-case note))
      octave-up (count (filter (fn [chr] (= \+ chr)) octave))
      octave-down (count (filter (fn [chr] (= \- chr)) octave))
      octave-modifier (* 12 (- octave-up octave-down))]

    (println note base-pitch octave-modifier)
    (if (= 0 base-pitch)
      0 ; if it's a rest, don't apply any octave modifiers
      (+ base-pitch octave-modifier)
    )
  )
  ; TODO implement
)

(defn parse-note
  "Takes a String object called noteString and validates it. 
  Either it returns Note :: {:pitch <int> :len <int>}, or an Error."
  [note-string]
  (let
    [note-info (re-find #"\([ ]*([A-GR-a-gr][#_]?)[ ]*([ ]+[\d]+)?[ ]*([ ]+[+-]+)?[ ]*\)" note-string)] ;@TODO is this regex robust?
    (if (= note-info nil) 
      (println "[Parsing Error]: Malformed note:" note-string)
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

(defn parse-note-stream [file-contents]
  "Takes a String called fileContents and parses and tokenizes each note 
  individually until no more notes are available, throwing error if necessary.
  Returns seq of NoteEvent"
  
  ; @TODO
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