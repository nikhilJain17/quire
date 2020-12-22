(ns quire.core
  (:gen-class))

(use 'overtone.live)
(use 'overtone.inst.piano)

(defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (println "Hello, World!")
    (piano (note "C4"))
)
