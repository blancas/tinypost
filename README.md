## Tinypost

A tiny PostScript interpreter.

It uses an ANTLR3 lexer and a Java Exception class as a form of longjmp to get out of (possibly nested) loops. There is support for most of the core language but not for graphics.

## Sample Usage

Sample file:

```
$ cat src/main/resources/fact.ps 
/factorial {
  dup 0 eq {
    pop 1
  } {
    dup 1 sub
    factorial
    mul
  } ifelse
} def

6 factorial =  %% Prints this result
10 factorial   %% returns this result
```

Load the `main` namespace:

```clojure
(use 'blancas.tinypost.main)
```

Use `runf` to run a ps file and get as result the operand stack.

```Clojure
(runf "src/main/resources/fact.ps")
;; 720
;; [3628800]
```

## To Do

* Make host program data available to ps code.

## License

Copyright © 2013 Armando Blancas.

Licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html).
