(ns blancas.tinypost.interpret
  (:use blancas.tinypost.scan 
        blancas.tinypost.stack 
        blancas.tinypost.operator))

;;
;; Execution Environment
;;
;; There are four stacks: dictionary, operand, execution, procedure flag.
;;

(defstruct exec-env :ds :os :xs :ps)

(defn make-env
  "Makes an execution environment."
  [] (struct exec-env [sys-dict {}] [] [] [false]))

(def proc-mark :proc-mark)

;;
;; PostScript Interpreter
;;

(defn run-literal
  "Pushes a literal name on the stack."
  [name env] (push name env))

(defn run-integer
  "Parses an integer token and pushes it on the stack."
  [s env] (push (parse-int s) env))

(defn run-radix
  "Parses an integer token and pushes it on the stack."
  [s env]
  (let [[radix number] (vec (.split s "#"))]
    (push (parse-int number (parse-int radix)) env)))

(defn run-real
  "Parses a real token and pushes it on the stack."
  [s env] (push (Double/parseDouble s) env))

(defn run-string
  "Parses a string token and pushes it on the stack."
  [s env] (push s env))

(defn run-comment
  "Comment metadata is ignored for now."
  [token env] env)

(defn run-begin
  "Marks the start of a procedure."
  [env] (update-in (push proc-mark env) [:ps] conj true))

(defn run-end
  "Creates a procedure as a list objects on the stack
   starting with the last proc-mark. The objects are
   removed and replaced by the list on the stack."
  [env]
  (let [s (:os env)
        c (loop [n 0
                 v s]
            (if (not= (peek v) proc-mark)
              (recur (inc n) (pop v))
              n))]
    (assoc 
      (update-in env [:ps] pop)
      :os (conj (rdrop (inc c ) s) (apply list (rtake c s))))))

(defn postscript
  "Process a token coming from the input stream
   against the current execution environment."
  [env token]
  (let [text (.getText token)]
    (cond (name?    token) (run-name    text env)
          (literal? token) (run-literal text env)
          (int?     token) (run-integer text env)
          (radix?   token) (run-radix   text env)
          (real?    token) (run-real    text env)
          (str?     token) (run-string  text env)
          (comment? token) (run-comment text env)
          (begin?   token) (run-begin   env)
          (end?     token) (run-end     env)
          :else
            (throw (Exception. (str "Unexpected: " token))))))
