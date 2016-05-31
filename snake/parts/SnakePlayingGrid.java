/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und gehört zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.snake.parts;

import java.awt.Graphics;

/**
 * Diese Klasse repräsentiert ein Spielfeld für ein Snake-Spiel.<br>
 * Dieses ist mit einer gewissen Spielfeldgröße (x Felder * y Felder) ausgestattet,
 * wobei ein Feld quadratisch ist und in einer ebenfalls definierten Größe gezeichnet wird.<br>
 * Des weiteren kann gegeben werden, ob das Spielfeld gezeichnet werden soll.<br>
 * Falls dies nicht der Fall ist:<br>
 *      1) benötigt man trotzdem ein Spielfeld, damit die Schlange in ihrer Bewegungsfreiheit
 *         auf ein bestimmtes Feld eingeschränkt ist<br>
 *      2) werden die Schlange und das Essen dieser Schlange trotzdem gezeichnet
 * @author Jean-Pierre Hotz
 */
public class SnakePlayingGrid {
    /**
     * Diese Variablen geben die Breite und Höhe des Spielfeld in width Felder * h Felder an
     */
    private int width, height;
    /**
     * Diese Variable gibt die Breite eines Felds in px an
     */
    private int size;
    /**
     * Diese Variable gibt an, ob das Spielfeld gezeichnet werden soll
     */
    private boolean drawingGrid;
    
    /**
     * Dieser Konstruktor erzeugt ein Spielfeld mit gegebener Breite, Höhe, Zeichengröße
     * und der gegebenen Eigenschaft, ob das Spielfeld gezeichnet werden soll
     * @param w     Die Breite des Spielfelds in Feldern
     * @param h     Die Höhe des Spielfelds in Feldern
     * @param s     Die Zeichengröße eines Felds in px
     * @param dG    Die Eigenschaft, ob das Spielfeld gezeichnet werden soll
     */
    public SnakePlayingGrid(int w, int h, int s, boolean dG){
        this.width = w;
        this.height = h;
        this.size = s;
        this.drawingGrid = dG;
    }
    
    /**
     * Diese Methode setzt die Größe des Spielfelds und die Zeichengröße auf die gegebenen Werte.
     * Diese Methode wird aufgerufen, sobald das Spiel mit gespeicherten Optionen wieder
     * aufgenommen werden soll.
     * @param w     Die neue Breite des Spielfelds in Feldern
     * @param h     Die neue Höhe des Spielfelds in Feldern
     * @param s     Die neue Zeichengröße in px
     */
    public void setSize(int w, int h, int s){
        this.width = w;
        this.height = h;
        this.size = s;
    }
    
    /**
     * Diese Methode gibt ihnen die Breite des Spielfelds in Feldern
     * @return  Die Breite des Spielfelds
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Diese Methode gibt ihnen die Höhe des Spielfelds in Feldern
     * @return  Die Höhe des Spielfelds in Feldern
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Diese Methode gibt ihnen die Zeichengröße des Spielfelds in px
     * @return  Die Zeichengröße des Spielfelds in px
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Diese Methode setzt die Eigenschaft, ob das Spielfeld gezeichnet werden soll
     * @param dG    Die Eigenschaft, ob das Spielfeld gezeichnet werden soll
     */
    public void setDrawingGrid(boolean dG){
        this.drawingGrid = dG;
    }
    
    /**
     * Diese Methode zeichnet das Spielfeld
     * @param x     das Offset in x-Richtung
     * @param y     das Offset in y-Richtung
     * @param g     das GRaphics-Objekt, auf dem gezeichnet wird
     */
    public void drawPlayingGrid(int x, int y, Graphics g){
//      Wir malen auf jeden Fall den Aussenrand des Spielfelds
        g.drawRect(x, y, width * size, height * size);
//      und falls das Spielfeld gezeichnet werden soll
        if(drawingGrid){
//          Zeichnen wir zuerst alle vertikalen Linien,
            for(int i = 0; i <= width; i++)
//              indem wir von einer von i abhängigen Beite,
//              und dem Offset in y-Richtung zu der gleichen Breite an der tiefsten Stelle,
//              also der Höhe * der Zeichengröße plus das Offset in y-Richtung
                g.drawLine(x + i * size, y, x + i * size, y + height * size);
//          Und dann zeichnen wir alle horizontalen Linien,
            for(int i = 0; i <= height; i++)
//              indem wir von einer von i abhängigen Höhe,
//              und dem Offset in x-Richtung zu der gleichen Höhe and der rechtesten Stelle,
//              also der Breite * der Zeichengröße plus das Offset in x-Richtung
                g.drawLine(x, y + i * size, x + width * size, y + i * size);
        }
    }
}