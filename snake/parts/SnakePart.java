/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und geh�rt zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.snake.parts;

import de.jeanpierrehotz.snake.parts.exceptions.SnakeHitBordersException;

/**
 * Diese Klasse repr�sentiert ein Teil einer Schlange.
 * Dieses kann nur innerhalb eines bestimmten Feldes erzeugen
 * @author Jean-Pierre Hotz
 */
public class SnakePart{
    /**
     * Diese Variablen rer�sentieren die Position des Schlangenteils als Koordinaten
     */
    private int xInGrid, yInGrid;

    /**
     * Dieser Konstruktor �berpr�ft die gegebenen Koordinaten darauf, ob diese in diesem
     * Feld liegen, und falls dies der Fall ist erzeugt er dieses Objekt.
     * Ansonsten wird eine {@link SnakeHitBordersException} geworfen.
     * @param w     Die Breite des Spielfelds
     * @param h     Die H�he des Spielfelds
     * @param x     Die x-Koordinate, die das Objekt haben soll
     * @param y     Die y-Koordinate, die das Objekt haben soll
     * @throws SnakeHitBordersException     falls die gegebenen Kooridinaten au�erhalb des Spielfelds liegen
     */
    public SnakePart(int w, int h, int x, int y) throws SnakeHitBordersException{
//      Falls der x-Wert unter 0 oder gr��er oder gleich* der Breite ist
//      oder der y-Wert unter 0 oder gr��er oder gleich* der H�he ist
        if(x < 0 || x >= w || y < 0 || y >= h)
//          zeigen wir, dass die Schlange aus dem Spielfeld gegangen ist
            throw new SnakeHitBordersException("Your Snake hit the borders!");
        
//      * das gleich ist, da die Breite / H�he die Anzahl an Feldern in die entspr. Richtung
//        angibt. Da allerdings hier (wie bei Arrays) bei 0 angefangen wird zu z�hlen
//        liegt der Wert der Breite / H�he selbst au�erhalb des Spielfelds
        
//      Falls dies nicht der Fall ist, so werden die Koordinaten kopiert
        xInGrid = x;
        yInGrid = y;
    }
    
    /**
     * Diese Methode gibt ihnen die x-Koordinate des Teils
     * @return  die x-Kooridinate des Teils
     */
    public int getxInGrid(){
        return xInGrid;
    }

    /**
     * Diese Methode gibt ihnen die y-Koordinate des Teils
     * @return  die y-Koordinate des Teils
     */
    public int getyInGrid(){
        return yInGrid;
    }
    
    /**
     * Diese Methode l�sst das Objekt wissen, dass sich die Gr��e des Spielfelds
     * ge�ndert hat, und l�sst dieses darauf reagieren.
     * Es wird allerdings nur verhindert, dass das Teil au�erhalb des Spielfelds liegt
     * @param w     Die neue Breite des Spielfelds
     * @param h     Die neue H�he des Spielfelds
     */
    public void notifySizeChanged(int w, int h){
        xInGrid = xInGrid % w;
        yInGrid = yInGrid % h;
    }

    /**
     * Diese Methode zeigt, ob dieses Teil einem anderen Objekt gleich ist
     */
    @Override
    public boolean equals(Object obj){
//      Dieses Objekt kann nur einem anderen gleichen, falls dieses eine Instanz
//      der Klasse SnakePart ist
        if(obj instanceof SnakePart){
//          Dann m�ssen wir das gegebene Objekt typecasten, damit wir dessen Attribute
//          �ber die entsprechenden Getter erhalten k�nnen
//          Dies geht auf jeden Fall, da wir in dem vorherigen Schritt sicher gestellt
//          haben, dass dieses Objekt aus der Klasse SnakePart stammt
            SnakePart objCopy = (SnakePart) obj;
            
//          Und k�nnen dessen Attribute dann mit denen dieses Objekts vergleichen
            return (objCopy.getxInGrid() == xInGrid && objCopy.getyInGrid() == yInGrid);
        }else{
//          Falls das gegebene Objekt keine Instanz der Klasse SnakePart ist, so kann es
//          diesem Teil (wie bereits erkl�rt) nicht gleichen
            return false;
        }
    }
}