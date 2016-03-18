/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und gehört zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.snake.parts.exceptions;

/**
 * Diese Exception wird geworfen, falls ein Snake-Spiel mit Regeln gespielt wird, und die
 * Snake in sich selbst hineinfährt, und sich somit "frisst"
 * @author Jean-Pierre Hotz
 */
public class SnakeAteItselfException extends Exception {

    public SnakeAteItselfException(){}

    public SnakeAteItselfException(String msg){
        super(msg);
    }

    public SnakeAteItselfException(Throwable cause){
        super(cause);
    }

    public SnakeAteItselfException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SnakeAteItselfException(String msg, Throwable cause, boolean suppression, boolean stackTraceWritable){
        super(msg, cause, suppression, stackTraceWritable);
    }

}
