;; Copyright (c) 2013 Armando Blancas. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns blancas.tinypost.operator
  (:use blancas.tinypost.stack)
  (:import blancas.tinypost.ExitException))

(declare run-name)

(defn defer? [env] (peek (:ps env)))

(defn def?
  "Returns whether the value [obj] should be treated 
   as a definition in a user or system dictionary."
  [obj]
  (and (string? obj)
       (not (.startsWith obj "/"))
       (not (.startsWith obj "("))))

(defn exec
  "Executes a compiled procedure. Elements in the list
   are either literal objects or dictionary definitions."
  [f env]
  (if-let [e (first f)]
    (recur (rest f) (if (def? e) (run-name e env) (push e env)))
    env))
           
(defn run-name
  "In defer mode, pushes the name in the stack, otherwise
   looks up the name to execute its definition, which can be a 
   procedure represented as a set or a callable function."
  [name env]
  (if (defer? env)
    (push name env)
    (if-let [f (lookup (env :ds) name)]
      (if (list? f)
        (exec f env)
        (f env))
      (throw (Exception. (str "Undefined: " name))))))

(defn unlit
  "Removes the literal indicator in a name."
  [s]
  (if (.startsWith s "/")
    (.substring s 1)
    s))

;;
;; PostScript operators.
;;

(defn binary
  "Returns a function that implements an operator with the passed function."
  [f]
  (fn [env]
    (let [s (:os env)]
      (assoc env :os (conj (rdrop 2 s) (apply f (rtake 2 s)))))))

; add (n1 n2 --> n1+n2) Adds two numbers.
(def add (binary +))

(defn aload
  "aload (array --> e1..en array) Loads all elements of an array on the stack."
  [env]
  (let [s (:os env)
        a (peek s)]
    (assoc env :os (conj (vec (concat (pop s) @a)) a))))

(defn ps-and
  "and (op1 op1 --> (op1 and op2)) ANDs two numbers or booleans."
  [env]
  (let [s (:os env)
        [op1 op2] (rtake 2 s)
        f (if (number? op1) bit-and (fn [x y] (and x y)))]
    (assoc env :os (conj (rdrop 2 s) (f op1 op2)))))

(defn clear
  "clear (obj1.. objn --> ) Removes all stack contents."
  [env]
  (assoc env :os []))

(defn ps-count
  "count (obj1..objn --> obj1..objn n) Counts the elements on the stack."
  [env]
  (update-in env [:os] conj (count (:os env))))

(defn ps-def
  "Binds a name to a procedure object or a function that
   will push a data object on the operand stack. Both the
   name and value are removed from the stack."
  [env]
  (let [s (:os env)
        [k v] (rtake 2 s)
        xe (assoc env :os (rdrop 2 s))
        pushf (fn [obj] (fn [e] (push obj e)))]
    (if (list? v)
      (update-in xe [:ds 1] assoc (unlit k) v)
      (update-in xe [:ds 1] assoc (unlit k) (pushf v)))))

(defn div
  "div (n1 n2 --> n1/n2) Divides two numbers."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (rdrop 2 s) (double (apply / (rtake 2 s)))))))

(defn dup
  "dup (obj --> obj obj) Duplicates the top of the stack."
  [env]
  (update-in env [:os] conj (peek (:os env))))

; eq (v1 v2 --> v1==v2) Tests for equal.
(def eq (binary =))

(defn exch
  "exch (obj1 obj2 --> obj2 obj1) Reverses two top objects."
  [env]
  (let [s (:os env)]
    (assoc env :os (reduce conj (rdrop 2 s) (reverse (rtake 2 s))))))

(defn exit
  "exit ( --> ) Exits the current execution array."
  [env]
  (throw (ExitException. env)))

(defn ps-for
  "for (init inc limit proc -> ) Runs proc in a for loop."
  [env]
  (let [s (:os env)
        [ini inc lim pro] (rtake 4 s)
        done? (fn [v] (if (pos? inc) (> v lim) (< v lim)))]
    (loop [xe (assoc env :os (rdrop 4 s))
           val ini]
      (if (done? val)
        xe
        (recur (exec pro (update-in xe [:os] conj val)) (+ val inc))))))

(defn forall
  "forall (array proc --> ) Executes proc for each element in the array."
  [env]
  (let [s (:os env)
        p (peek s)]
    (try
      (loop [xe (assoc env :os (rdrop 2 s))
             a @(peek (pop s))]
        (if (empty? a)
          xe
          (recur (exec p (update-in xe [:os] conj (first a))) (rest a))))
      (catch ExitException ee 
        (.getXE ee)))))

(defn ps-false
  "false ( --> false) Returns false on the top of the stack."
  [env]
  (update-in env [:os] conj false))

; ge (v1 v2 --> v1>=v2) Tests for greater or equal.
(def ge (binary >=))

(defn ps-get
  "get (array k -->  objk) Gets the kth element of an array."
  [env]
  (let [s (:os env)
        [a k] (rtake 2 s)]
    (assoc env :os (conj (rdrop 2 s) (@a k)))))

; gt (v1 v2 --> v1>v2) Tests for greater than.
(def gt (binary >))

(defn idiv
  "(idiv (n1 n2 --> int(n1/n2)) Integer divide." 
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (rdrop 2 s) (apply quot (rtake 2 s))))))

(defn ps-if
  "if (b proc --> ) Executes proc if b is true."
  [env]
  (let [s (:os env)
        [b p] (rtake 2 s)
        xe (assoc env :os (rdrop 2 s))]
    (if b (exec p xe) xe)))

(defn ifelse
  "ifelse (b proc1 proc2 --> ) Executes proc1 if b is true and proc2 if false."
  [env]
  (let [s (:os env)
        [b p1 p2] (rtake 3 s)
        xe (assoc env :os (rdrop 3 s))]
    (if b (exec p1 xe) (exec p2 xe))))

(defn lbracket
  "[ (obj1 -- obj1 <[>) Marks the start of an array."
  [env]
  (push :array-mark env))

; le (v1 v2 --> v1<=v2) Tests for less or equal.
(def le (binary <=))

(defn length
  "length (array --> array n) Returns the number of elements in the array."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (rdrop 1 s) (count @(peek s))))))

; lt (v1 v2 --> v1<v2) Tests for less than.
(def lt (binary <))

(defn ps-loop
  "loop (proc --> ) Executes proc until the exit operator is called."
  [env]
  (let [s (:os env)]
    (try
      (loop [xe (assoc env :os (rdrop 2 s))]
        (recur (exec (peek s) xe)))
      (catch ExitException ee 
        (.getXE ee)))))

; mod (n1 n2 --> (n1 mod n2)) Modulus: division remainder.
(def modulo (binary mod))

; mul (n1 n2 --> n1*n2) Multiplies two numbers.
(def mul (binary *))

; ne (v1 v2 --> v1<>v2) Tests for not equal.
(def ne (binary not=))

(defn neg
  "neg (n --> -n) Reverses the sign of the number on the top of the stack."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (- (peek s))))))

(defn ps-not
  "not (n --> (not n)) Bitwise or logical NOT."
  [env]
  (let [s (:os env)
        v (peek s)
        f (if (number? v) bit-not not)]
    (assoc env :os (conj (pop s) (f v)))))

(defn ps-or
  "or (op1 op1 --> (op1 or op2)) ORs two numbers or booleans."
  [env]
  (let [s (:os env)
        [op1 op2] (rtake 2 s)
        f (if (number? op1) bit-or (fn [x y] (or x y)))]
    (assoc env :os (conj (rdrop 2 s) (f op1 op2)))))

(defn ps-pop
  "pop (obj1 obj2 --> obj1) Removes the top of the stack." 
  [env]
  (let [s (:os env)]
    (assoc env :os (pop s))))

(defn pstack
  "pstack (obj1 obj2 --> obj1 obj2) Prints the stack contents."
  [env]
  (doseq [v (reverse (:os env))] (println v))
  env)

(defn ptos
  "== (obj1 obj2 --> obj1) Prints the top of the stack."
  [env]
  (let [s (:os env)]
    (println (peek s))
    (assoc env :os (pop s))))

(defn ps-put
  "put (array k obj --> ) Puts obj as the kth element of an array."
  [env]
  (let [s (:os env)
        [a k v] (rtake 3 s)]
    (swap! a assoc k v)
    (assoc env :os (rdrop 3 s))))

(defn rbracket
  "] (obj1 <[> obj2 obj3 -- obj1 [obj2 obj3]) Creates an array from the [ mark."
  [env]
  (let [s (:os env)
        c (loop [n 0
                 v s]
            (if (not= (peek v) :array-mark)
              (recur (inc n) (pop v))
              n))]
    (assoc env :os (conj (rdrop (inc c ) s) (atom (rtake c s))))))

(defn ps-repeat
  "repeat (n proc --> ) Executes proc n times."
  [env]
  (let [s (:os env)]
    (try
      (loop [n (peek (pop s))
             xe (assoc env :os (rdrop 2 s))]
        (if (pos? n)
          (recur (dec n) (exec (peek s) xe))
          xe))
      (catch ExitException ee 
        (.getXE ee)))))

(defn roll
  "roll (a b c n j --> b c a) Rotates top n items j times <--(+) (-)-->."  
  [env]
  (let [s (:os env)
        [n j] (rtake 2 s)
        v (rdrop 2 s)]
    (assoc env :os (reduce conj (rdrop n v) (rotate (rtake n v) j)))))

; sub (n1 n2 --> n1-n2) Subtracts two numbers.
(def sub (binary -))

(defn ps-true
  "true ( --> true) Returns true on the top of the stack."
  [env]
  (update-in env [:os] conj true))

(defn ps-xor
  "xor (op1 op1 --> (op1 xor op2)) XORs two numbers or booleans."
  [env]
  (let [s (:os env)
        [op1 op2] (rtake 2 s)
        f (if (number? op1) 
            bit-xor 
            (fn [x y] (or (and x (not y)) (and (not x) y))))]
    (assoc env :os (conj (rdrop 2 s) (f op1 op2)))))

(defn abs
  "abs (n --> n) Computes the absolute value of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/abs s)))))

(defn ceiling
  "ceiling (n --> n) Computes the ceiling of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/ceil s)))))

(defn floor
  "floor (n --> n) Computes the floor of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/floor s)))))

(defn round
  "round (n --> n) Rounds the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/round s)))))

(defn truncate
  "truncate (n --> n) Truncates the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (long s)))))

(defn sqrt
  "sqrt (n --> n) Computes the square root of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/sqrt s)))))

(defn atan
  "atan (n --> n) Computes the atan of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/atan s)))))

(defn cos
  "cos (n --> n) Computes the cos of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/cos s)))))

(defn sin
  "sin (n --> n) Computes the sin of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/sin s)))))

(defn ln
  "ln (n --> n) Computes the natural logarithm of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/log s)))))

(defn log10
  "log (n --> n) Computes the logarithm base 10 of the number on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (Math/log10 s)))))

(defn ps-rand
  "rand ( --> n) Puts a random integer on the TOS."
  [env]
  (let [s (:os env)]
    (assoc env :os (conj (pop s) (long (* (rand) 1000000000))))))

; exp (n e --> n^e) Computes n^e.
(def exp (binary #(Math/pow %1 %2)))

;;
;; System Dictionary
;;

(def sys-dict (hash-map
                ; language
                "["        lbracket
                "]"        rbracket
                "aload"    aload
                "get"      ps-get
                "put"      ps-put
                "length"   length
                ; stack
                "="        ptos
                "=="       ptos
                "pstack"   pstack 
                "clear"    clear 
                "count"    ps-count
                "dup"      dup 
                "exch"     exch 
                "pop"      ps-pop 
                "roll"     roll
                ; control
                "eq"       eq
                "ne"       ne
                "ge"       ge
                "gt"       gt
                "le"       le
                "lt"       lt
                "and"      ps-and
                "not"      ps-not
                "or"       ps-or
                "xor"      ps-xor
                "true"     ps-true
                "false"    ps-false
                "if"       ps-if
                "ifelse"   ifelse
                "loop"     ps-loop
                "repeat"   ps-repeat
                "for"      ps-for
                "forall"   forall
                "exit"     exit
                ; math
                "add"      add 
                "+"        add
                "div"      div
                "/"        div 
                "idiv"     idiv 
                "mod"      modulo 
                "mul"      mul
                "*"        mul
                "neg"      neg
                "sub"      sub
                "-"        sub
		"abs"      abs
                "ceiling"  ceiling
                "floor"    floor
                "round"    round
                "truncate" truncate
                "sqrt"     sqrt
                "atan"     atan
                "cos"      cos
                "sin"      sin
                "exp"      exp
                "ln"       ln
                "log"      long
                "rand"     ps-rand
                ; dictionary
                "def"     ps-def ))
