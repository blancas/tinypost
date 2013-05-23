;; Copyright (c) 2013 Armando Blancas. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

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
