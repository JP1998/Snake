/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und geh�rt zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.snake.parts;

import java.awt.Graphics;

/**
 * Diese Klasse repr�sentiert ein Feld, das von der Schlange gefressen werden kann
 * @author Jean-Pierre Hotz
 */
public class Food {
    /**
     * Die Variablen, die die Koordinaten des Food-Objekts repr�setieren
     */
    private int xInGrid, yInGrid;
    
    /**
     * Der Konstruktor, der ein Food-Objekt an gegebenen Koordinaten erzeugt
     * @param x     Die x-Koordinate des Food-Objekts
     * @param y     Die y-Koordinate des Food-Objekts
     */
    public Food(int x, int y){
        xInGrid = x;
        yInGrid = y;
    }
    
    /**
     * Diese Methode gibt ihnen die x-Position dieses Objekts
     * @return  die x-Koordinate des Food-Objektes
     */
    public int getxInGrid() {
        return xInGrid;
    }

    /**
     * Diese Methode gibt ihnen die y-Position dieses Objekts
     * @return  die y-Koordinate des Food-Objekts
     */
    public int getyInGrid() {
        return yInGrid;
    }
    
    /**
     * Diese Methode zeigt dem Objekt, dass die Spielfeldgr��e ge�ndert wurde.
     * Dadurch soll sichergestellt werden, dass dieses Objekt nicht au�erhalb des
     * Spielfelds liegt
     * @param w     Die neue Breite des Spielfelds
     * @param h     Die neue H�he des Spielfelds
     */
    public void notifySizeChanged(int w, int h){
        xInGrid = xInGrid % w;
        yInGrid = yInGrid % h;
    }

    /**
     * Diese Methode zeichnet das Food-Objekt mit gegebenen Offsets und der gegebenen
     * Gr��e eines Felds an seiner Stelle
     * @param x     Das x-Offset des Spielfelds
     * @param y     Das y-Offset des Spielfelds
     * @param s     Die Gr��e eines Teilfeldes des Spielfelds
     * @param g     Das Graphics-Objekt, auf dem gezeichnet werden soll
     */
    public void drawFood(int x, int y, int s, Graphics g){
        g.fillOval(x + xInGrid * s, y + yInGrid * s, s, s);
    }
}
