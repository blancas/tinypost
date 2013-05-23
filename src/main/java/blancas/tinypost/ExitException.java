// Copyright (c) 2013 Armando Blancas. All rights reserved.
// The use and distribution terms for this software are covered by the
// Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
// which can be found in the file epl-v10.html at the root of this distribution.
// By using this software in any fashion, you are agreeing to be bound by
// the terms of this license.
// You must not remove this notice, or any other, from this software.

package blancas.tinypost;

/**
 * This exception is thrown to exit a running procedure.
 * The cached xe will be the procedure's return value.
 */
public class ExitException extends Exception {

    /**
     * The interpreter's execution environment.
     */
    Object xe;

    public ExitException(Object xe) {
        this.xe = xe;
    }

    public Object getXE() {
        return xe;
    }

}
