/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und gehört zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.snake.parts.exceptions;

/**
 * Diese Exception sollte nur besonderen Fällen geworfen werden, weshalb diese
 * nicht deklariert werden müssen soll.
 * Deshalb erbt diese von der RuntimeException
 * @author Jean-Pierre Hotz
 */
public class SnakeFalseExecutionException extends RuntimeException {

    public SnakeFalseExecutionException(){}

    public SnakeFalseExecutionException(String msg) {
        super(msg);
    }

    public SnakeFalseExecutionException(Throwable cause) {
        super(cause);
    }

    public SnakeFalseExecutionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SnakeFalseExecutionException(String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(msg, cause, enableSuppression, writableStackTrace);
    }

}
