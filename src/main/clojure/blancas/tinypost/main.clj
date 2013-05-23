(ns blancas.tinypost.main
  (:gen-class)
  (:use [blancas.tinypost scan interpret]))

(defn -main
"\nRuns a PostScript program.

Usage: ps <file>

       file  A PostScript source file."
  [& args]
  (try
    (if-let [file (first args)]
      (let [env (make-env)]
        (reduce postscript env (token-seq (scanner file))))
      (println (:doc (meta (var -main)))))
    (catch Throwable t
      (.println *err* (.getMessage t)))))
