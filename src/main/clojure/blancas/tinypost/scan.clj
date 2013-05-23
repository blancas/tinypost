(ns blancas.tinypost.scan
  (:import [org.antlr.runtime ANTLRFileStream CharStream Token]
           blancas.tinypost.Scanner))

(defn scanner
  "Returns a PostScript Scanner for the file."
  [file]
  (let [input (ANTLRFileStream. file)]
    (Scanner. input)))

(defn token-seq
  "Returns a lazy sequence of tokens from the given
   PostScript [scanner]. Ignores whitespace."
  [scanner]
  (let [token (.nextToken scanner)]
    (when-not (= token Token/EOF_TOKEN) 
      (if (= (.getType token) Scanner/WS)
        (recur scanner)
        (cons token (lazy-seq (token-seq scanner)))))))

;;
;; Predicates for token types.
;;

(defmacro defpred [p v]
  `(defn ~p [t#] (= (.getType t#) ~(symbol (str "Scanner/" v)))))

(defpred int? INTEGER)

(defpred radix? RADIX_INTEGER)

(defpred real? REAL)

(defpred str? STRING)

(defpred name? NAME)

(defpred literal? LITERAL_NAME)

(defpred comment? COMMENT)

(defpred begin? BEGIN)

(defpred end? END)

;;
;; Conversion functions
;;

(defn parse-int
  "Returns the integer parsed from the string [s] with
   a given radix [r] or as a decimal by default."
  ([s] (parse-int s 10))
  ([s r]
   (try
     (Integer/parseInt s r)
     (catch NumberFormatException e
       (try 
         (Long/parseLong s r)
         (catch NumberFormatException e
           (BigInteger. s r)))))))

(defn descape
  "Remove delimiters and resolve ESC sequences."
  [s] (.substring s 1 (dec (count s))))
