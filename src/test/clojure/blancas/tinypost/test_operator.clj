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


(deftest test-0140
  (fact "count"
	(run "1 2 3 4 5 count") => [1 2 3 4 5 5]
	(run "5 5 add 5 5 5 add add add count") => [25 1]
	(run "1 2 3 pop pop pop count") => [0]))

(deftest test-0160
  (fact "cos"
	(run "45 cos") => [0.5253219888177297]
	(run "-1 cos") => [0.5403023058681398]))

(deftest test-0180
  (let [ps "/x 99 def /y -1 def /z (foobar) def x y z count"]
    (fact "def -- define a value"
	  (run ps) => [99 -1 "(foobar)" 3])))

(deftest test-0200
  (fact "div -- divides any two numbers"
	(run "10 2 div") => [5.0]
	(run "12.9 3.0 div") => [4.3]
	(run "200 10 2 div div") => [40.0]
	(run "3 4 div") => [0.75]))

(deftest test-0220
  (fact "dup -- duplicates the TOS"
	(run "747 dup") => [747 747]
	(run "(foo) dup") => ["(foo)" "(foo)"]
	(run "5 dup 10 mul mul") => [250]))

(deftest test-0240
  (fact "eq -- tests for equality"
	(run "(A380) (A380) eq") => [true]
	(run "737 757 eq") => [false]
	(run "3.14159 3.14159 eq") => [true]))

(deftest test-0260
  (fact "exch -- switches the TOS and the next element"
	(run "10 2 exch div") => [0.2]
	(run "12.9 3.0 exch sub") => [-9.9]
	(run "200 2 10 exch div div") => [40.0]
	(run "3 4 exch sub") => [1]))

;; 0280 exit

(deftest test-0300
  (fact "exp"
	(run "2 3 exp") => [8.0]
	(run "2 8 exp") => [256.0]
	(run "-2 3 exp") => [-8.0]
	(run "2 -3 exp") => [0.125]))

(deftest test-0320
  (fact "floor"
	(run "3.98 floor") => [3.0]
	(run "-3.98 floor") => [-4.0]))

;; 0340 for

;; 0360 forall

(deftest test-0380
  (fact "true, false"
	(run "true false") => [true false]
	(run "true false and") => [false]
	(run "true true and") => [true]
	(run "false true or") => [true]
	(run "false false or") => [false]))

(deftest test-0400
  (fact "ge -- greater than or equal"
	(run "100 99 ge 100 100 ge 99 100 ge") => [true true false]
	(run "0 0 1 add ge") => [false]
	(run "0 0 0 add ge") => [true]
	(run "0 0 1 sub ge") => [true]))

(deftest test-0420
  (fact "idiv -- divides two integers"
	(run "10 2 idiv") => [5]
	(run "4 3 idiv") => [1]
	(run " 3 4 idiv") => [0]))

(deftest test-0440
  (fact "if -- conditional execution"
	(run "true { 777 } if") => [777]
	(run "-1 99 100 ge { 747 } if") => [-1]))

