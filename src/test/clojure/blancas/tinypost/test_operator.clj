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

(deftest test-0460
  (fact "ifelse -- conditional execution"
	(run "true { 777 } { 787 } ifelse") => [777]
	(run "-1 99 100 ge { 747 } { 787 } ifelse") => [-1 787]
	(run "false { (foobar) } { true { (barbaz) } if } ifelse") => ["(barbaz)"]))

(deftest test-0480
  (fact "le -- less than or equal"
	(run "100 99 le 100 100 le 99 100 le") => [false true true]
	(run "0 0 1 add le") => [true]
	(run "0 0 0 add le") => [true]
	(run "0 0 1 sub le") => [false]))

(deftest test-0500
  (let [ps1 "[ 1 2 3 4 5 6 7 8 ] length"
	ps2 "/arr [ 2 4 6 8 ] def arr length"]
    (fact "length -- puts the length of an array on the TOS"
	  (run ps1) => [8]
	  (run ps2) => [4])))

(deftest test-0520
  (fact "ln"
	(run "45 ln") => [3.8066624897703196]
	(run "0.5 ln") => [-0.6931471805599453]))

(deftest test-0540
  (fact "log"
	(run "45 log") => [1.6532125137753437]
	(run "0.5 log") => [-0.3010299956639812]))

(deftest test-0560
  (fact "lt -- less than"
	(run "100 99 lt 100 100 lt 99 100 lt") => [false false true]
	(run "0 0 1 add lt") => [true]
	(run "0 0 0 add lt") => [false]
	(run "0 0 1 sub lt") => [false]))

;; 580 loop

(deftest test-0600
  (fact "mod -- computes the modulo of two integers"
	(run "10 2 mod") => [0]
	(run "4 3 mod") => [1]
	(run "3 4 mod") => [3]))

(deftest test-0620
  (fact "mul -- multiplies any two numbers"
	(run "3 4 mul") => [12]
	(run "3.0 4 mul") => [12.0]
	(run "3.5 4.5 mul") => [15.75]
	(run "5 10 15 20 mul mul mul") => [15000]))

(deftest test-0640
  (fact "ne -- tests for inequality"
	(run "(A380) (A380) ne") => [false]
	(run "737 757 ne") => [true]
	(run "3.14159 3.14159 ne") => [false]
	(run "true false ne") => [true]))

(deftest test-0660
  (fact "neg -- changes the sign of the TOS"
	(run "99 neg") => [-99]
	(run "-99 abs neg") => [-99]
        (run "-3.1416 neg") => [3.1416]))

(deftest test-0680
  (fact "not -- logical not"
	(run "100 99 lt not 100 100 lt not 99 100 lt not") => [true true false]
	(run "0 0 1 add lt not") => [false]
	(run "0 0 0 add lt not") => [true]
	(run "0 0 1 sub lt not") => [true]))

(deftest test-0700
  (let [ps "/x true def /y false def x x or x y or y x or y y or"]
    (fact "or -- logical OR"
	  (run ps) => [true true true false])))

(deftest test-0720
  (fact "pop -- removes the TOS"
	(run "1 2 (foobar) pop") => [1 2]
	(run "(foobar) pop") => []
	(run "1 2 (foobar) pop pop pop") => []))

(deftest test-0740
  (fact "put -- sets an array element"
	(run "/arr [0 0 0] def arr 1 747 put arr aload pop") => [0 747 0]))

;; 760 repeat

(deftest test-0780
  (fact "roll -- rotates n top elements in the stack to the right"
	(run "1 2 3 4 5 5 3 roll") => [4 5 1 2 3]))


;; 800 roll left

(deftest test-0820
  (fact "round -- rounds the TOS"
	(run "25.55 round") => [26]
	(run "-11.11 round") => [-11]))

(deftest test-0840
  (fact "sin"
	(run "45 sin") => [0.8509035245341184]
	(run "-1 sin") => [-0.8414709848078965]))

(deftest test-0860
  (fact "sqrt"
	(run "81 sqrt") => [9.0]
	(run "10000.0 sqrt") => [100.0]
	(run "40 sqrt") => [6.324555320336759]))

(deftest test-0880
  (fact "sub -- subtraction"
	(run "3 4 sub") => [-1]
	(run "4 3 sub") => [1]
	(run "5.58 4.5 sub") => [1.08]
	(run "20 15 10 5 sub sub sub") => [10]))

(deftest test-0900
  (fact "truncate"
	(run "81 truncate") => [81]
	(run "81.0 truncate") => [81]
	(run "10.37485 truncate") => [10]
	(run "-40.98755 truncate") => [-40]))

(deftest test-0920
  (let [ps "/x true def /y false def x x xor x y xor y x xor y y xor"]
    (fact "or -- logical OR"
	  (run ps) => [false true true false])))
