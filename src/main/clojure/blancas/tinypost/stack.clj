(ns blancas.tinypost.stack)

;;
;; Operand Stack functions.
;;

(defn push
  "Pushes an object into the operand stack."
  [obj env]
  (update-in env [:os] conj obj))

(defn rtake
  "A take-last for vectors with stack semantics."
  [n v]
  (let [c (count v)]
    (if (>= c n)
      (subvec v (- c n))
      (throw (IllegalStateException. "Stack underflow")))))

(defn rdrop
  "A drop-last for vectors with stack semantics."
  [n v]
  (let [c (count v)]
    (if (>= c n)
      (subvec v 0 (- c n))
      (throw (IllegalStateException. "Stack underflow")))))

(defn rotate-left
  "Rotates the vector [v] to the left [n] times."
  [v n]
  (if (zero? n)
    v 
    (recur (conj (vec (rest v)) (first v)) (dec n))))

(defn rotate-right
  "Rotates the vector [v] to the right [n] times."
  [v n]
  (if (zero? n)
    (vec v)
    (recur (cons (peek v) (rdrop 1 v)) (dec n))))

(defn rotate
  "Rotates the vector [v] [n] times; to the left if
   [n] is positive, to the right otherwise."
  [v n]
  (if (pos? n)
    (rotate-left v n)
    (rotate-right v (- n))))

;;
;; Dictionary Stack functions
;;

(defn lookup
  "Searches the name [n] in the dictionary stack [s], from the TOS to
   the system dictionary. Returns the value or nil if not found."
  [s n]
  (if-let [dict (peek s)]
    (or (dict n) (recur (pop s) n))))
