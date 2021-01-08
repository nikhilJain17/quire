(ns quire.core
  (:gen-class))

; (use 'quire.core :reload)

; @TODO
; - support custom tempo
; - add more instruments

(require '[clojure.string :as str])
; (do (use 'overtone.live) (use 'overtone.inst.piano))
; (use 'overtone.inst.piano)

(defrecord Note [pitch len])
(defrecord NoteEvent [timing note])

(defn schedule-note-stream [note-stream]
  "Schedules the note-stream to be played using Overtone."
  (loop [note-stream note-stream
      curr-time 0]

    (if (= 0 (count note-stream))
      (println "done")
      (do
        (let [curr-note (first note-stream)]
          (do
            ; (println note-stream curr-time curr-note)
            (if (= (get curr-note :pitch) 0)
                (Thread/sleep (get curr-note :len)) ; if its a rest, do nothing
                (at (+ (now) curr-time) (piano :note (note (get curr-note :pitch))) (get curr-note :len))
            )
            (recur (rest note-stream) (+ curr-time (get curr-note :len)))
          )  
        )
      )
    )
  )
)

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

    ; (println note base-pitch octave-modifier)
    (if (= 0 base-pitch)
      0 ; if it's a rest, don't apply any octave modifiers
      (+ base-pitch octave-modifier)
    )
  )
)

(defn len-to-ms
  "Given a note len and a tempo, returns how many ms the note should last."
  [len]
  (def len-converter {
    1 1000
    2 500
    4 250
    8 125
    16 62
    32 31
    64 15
    128 8
  })
  (len-converter len)
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
              (len-to-ms 4) ; default to quarter note
              (len-to-ms (read-string (note-info 2)))
            )
        )
      )
    )
  )
  ; (re-find #"\([ ]*([A-G-a-g][#_]?)[ ]*([ ]+[\d]+)?[ ]*([ ]+[+-]+)?[ ]*\)" noteString)
)

(defn mary-had-a-little-lamb
  []
  (def e (parse-note "(E)"))
  (def f (parse-note "(F#)"))
  (def f2 (parse-note "(F# 2)"))
  (def g (parse-note "(G)"))
  (def g2 (parse-note "(G 2)"))
  (def b (parse-note "(B +)"))
  (def b2 (parse-note "(B 2 +)"))
  (def r (parse-note "(R 8)"))
  (def e2 (parse-note "(E 2)"))
  (schedule-note-stream (list g f e f g g g2 f f f2 g b b2 g f e f g g g g f f g f e2))
)

(defn parse-note-stream [file-contents]
  "Takes a String called fileContents and parses and tokenizes each note 
  individually until no more notes are available, throwing error if necessary.
  Returns seq of NoteEvent"
  
  (map parse-note (str/split file-contents #","))
)

(defn -main
  []
  (loop []
    (println "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    (println "| 🎵 Enter .quire file to play 🎵 |")
    (println "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    (schedule-note-stream (parse-note-stream (slurp (read-line))))
    (recur)
  )
)

