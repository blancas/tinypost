;; Copyright (c) 2013 Armando Blancas. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns blancas.tinypost.test-operator
  (:use [clojure.test]
	[blancas.tinypost main operator]
	[midje.sweet :exclude (expect one-of)]))

(deftest test-0000
  (fact "abs -- absolute value"
	(run "99 abs") => [99]
	(run "-99 abs") => [99]
        (run "-3.1416 abs") => [3.1416]
        (run "3.1416 abs") => [3.1416]))

(deftest test-0020
  (fact "add -- adds any two numbers"
	(run "3 4 add") => [7]
	(run "3.5 4.5 add") => [8.0]
	(run "5 10 15 20 add add add") => [50]))

(deftest test-0040
  (let [ps "/arr [ 3 4 ] def arr aload pop add"]
    (fact "aload -- splices elemets of an array on the TOS, before the array object"
	  (run ps) => [7])))

(deftest test-0060
  (let [ps "/x 1 1 eq def /y 1 0 eq def x x and x y and y x and y y and"]
    (fact "and -- logical AND"
	  (run ps) => [true false false false])))

(deftest test-0080
  (fact "atan"
	(run "45 atan") => [1.5485777614681775]
	(run "-1 atan") => [-0.7853981633974483]))

(deftest test-0100
  (fact "ceiling"
	(run "3.98 ceiling") => [4.0]
	(run "-3.98 ceiling") => [-3.0]))

(deftest test-0120
  (fact "clear"
	(run "1 2 3 4 5 6 clear") => []
	(run "5 5 add 5 5 5 add clear") => []))
