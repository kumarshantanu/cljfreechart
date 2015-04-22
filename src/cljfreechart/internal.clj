(ns cljfreechart.internal)


(defn illegal-arg
  [msg & more]
  (->> (interpose \space more)
    ^String (apply str msg \space)
    (IllegalArgumentException.)
    throw))
