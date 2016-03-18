/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und gehört zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.snake.parts.exceptions;

/**
 * Diese Exception soll geworfen werden, sobald die Snake über die Spielfeldgrenzen
 * geschritten ist, und somit gestorben ist.
 * Das Werfen dieser Exception wird verhindert, sobald eingestellt ist, dass die
 * Schlange wieder am anderen Ende des Spielfelds auftauchen soll.
 * @author Jean-Pierre Hotz
 */
public class SnakeHitBordersException extends Exception {

    public SnakeHitBordersException(){}

    public SnakeHitBordersException(String msg) {
        super(msg);
    }

    public SnakeHitBordersException(Throwable cause) {
        super(cause);
    }

    public SnakeHitBordersException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SnakeHitBordersException(String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(msg, cause, enableSuppression, writableStackTrace);
    }

}
