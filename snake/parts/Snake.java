/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und gehört zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.snake.parts;

import java.awt.Color;
import java.awt.Graphics;

import java.util.ArrayList;

import de.jeanpierrehotz.snake.parts.exceptions.SnakeAteItselfException;
import de.jeanpierrehotz.snake.parts.exceptions.SnakeFalseExecutionException;
import de.jeanpierrehotz.snake.parts.exceptions.SnakeHitBordersException;

import static de.jeanpierrehotz.snake.parts.Snake.Direction.up;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.down;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.left;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.right;

/**
 * Diese Klasse repräsentiert eine Schlange mit einer bestimmten Sammlung an Schlangenteilen,
 * einer Richtung, in die dieses gehen möchte, bzw. soll, und den Spielregeln,
 * ob überhaupt mit Regeln gespielt werden soll, und falls ja, ob die Schlange wieder am anderen
 * Ende des Spielfelds auftauchen soll
 * @author Jean-Pierre Hotz
 */
public class Snake{
    /**
     * Diese ArrayList aus SnakePart-Objekten repräsentiert die Abfolge (des Körpers) der Schlange
     */
    private ArrayList<SnakePart> snakeParts;
    
    /**
     * Diese Methode gibt ihnen die x-Position des "Kopfes" der Schlange
     * @return  x-Position des Kopfes der Schlange
     */
    public int getFirstX(){
       return snakeParts.get(0).getxInGrid(); 
    }
    /**
     * Diese Methode gibt ihnen die y-Position des "Kopfes" der Schlange
     * @return  y-Position des Kopfes der Schlange
     */
    public int getFirstY(){
        return snakeParts.get(0).getyInGrid(); 
     }
    
    /**
     * Diese Variable gibt an, in welche Richtung das Objekt gehen soll
     */
    private Direction dir;
    
    /**
     * Diese Variable gibt an, wie viele Essensobjekte bisher gegessen wurden
     */
    private int score;
    
    /**
     * Diese Variablen repräsentieren die Eigenschaften, ob mit Regeln gespielt werden soll,
     * und ob die Schlange wieder am anderen Ende des Spielfelds auftauchen soll, und somit
     * das Spielfeld unendlich machen soll.
     */
    private boolean withRules, infinitePlayGrid;
    
    /**
     * Diese Enumeration repräsentiert einen Datentyp für die Richtung, in die eine Schlange
     * gehen soll.
     * Sie bietet die Möglichkeit eine zufällige Richtung zu ermitteln.
     * @author Jean-Pierre Hotz
     */
    public static enum Direction{
        /**
         * Die einzelnen Literale dieses Datentyps
         */
        up, down, left, right;
        
        /**
         * Eine Methode, die auf der Enumeration (statisch -> static) ausgeführt wird.
         * Diese gibt ihnen eine zufällige Richtung zurück.
         * @return  Eine zufällig ermittelte Richtung
         */
        public static Direction getRandomDirection(){
//          Dazu erzeugen wir eine Zufallszahl im Intervall [0 .. 3]
            switch((int) (Math.random() * 4)){
//              Bei 0 geben wir das Literal für nach oben zurück
                case 0:
                    return up;
//              Bei 1 geben wir das Literal für nach unten zurück
                case 1:
                    return down;
//              Bei 2 geben wir das Literal für nach links zurück
                case 2:
                    return left;
//              Bei 3 geben wir das Literal für nach rechts zurück
                case 3:
                    return right;
//              Und bei jedem anderen Fall geben wir das Literal für nach oben zurück
//              Dieser Zweig wird nur für die fehlerfreie Kompilierbarkeit aufgeführt
                default:
                    return up;
            }
        }
    }
    
    /**
     * Dieser Konstruktor erzeugt eine neue Schlange an einer zufälligen Stelle in dem Spielfeld,
     * dessen Breite und Höhe wir benötigen, und den gegebenen Werten
     * @param w     Die Breite des Spielfelds
     * @param h     Die Höhe des Spielfelds
     * @param dir   Die Richtung, in die die Schlange anfängluch gehen soll
     * @param wR    ob die Schlange Regeln zu beachten hat
     * @param inf   ob die Schlange über das Spielfeld hinaus gehen darf
     */
    public Snake(int w, int h, Direction dir, boolean wR, boolean inf){
//      Zuerst initialisieren wir die Variablen mit den gegebenen oder mit festen Werten
        this.score = 0;
        this.dir = dir;
        this.withRules = wR;
        this.infinitePlayGrid = inf;
        
//      Dann erzeugen wir die ArrayList neu
        this.snakeParts = new ArrayList<SnakePart>();
        
//      Und geben ihr ein Schlangenteil an zufälliger Koordinate
//      Der try-catch-Block sollte nie ausgelöst werden, weshalb in diesem
//      (außer den StackTrace auszugeben) nichts gemacht wird
        try {
            snakeParts.add(
                    new SnakePart(
                            w, h,
                            getRandomHorizontalPoint(w), 
                            getRandomVerticalPoint(h)
                    )
            );
        }catch (SnakeHitBordersException e){
            e.printStackTrace();
        }
    }

    /**
     * Diese Methode gibt ihnen einen zufälligen x-Punkt in dem Spielfeld mit gegebener Breite.
     * Dabei wird auch die Richtung, in die die Schlange geht berücksichtigt, damit man
     * nicht am Anfang direkt aus dem Spielfeld läuft und verliert.
     * @param w     Die Breite des Spielfelds
     * @return      Einen zufälligen x-Punkt im Spielfeld
     */
    private int getRandomHorizontalPoint(int w){
//      Falls die Schlange nach links geht ist das Minimum ein Fünftel der Breite; ansonsten 0
        int min = (dir == left)? w / 5: 0;
//      Falls die Schlange nach rechts geht ist das Maximum bei VierFünftel der Breite;
//      ansonsten bei der Breite - 1
        int max = (dir == right)? (w * 4) / 5: w - 1;
//      Dann geben wir eine Zufallszahl in dem Intervall [min .. max] zurück
//      Damit ein geschlossenes Intervall gegeben ist muss man zum Umfang eins dazuzählen 
//      (da Math.random() eine Zufallszahl im halboffenen Intervall [0 .. 1[ erzeugt)
        return (int) (Math.random() * (max - min + 1)) + min;
    }
    
    /**
     * Diese Methode gibt ihnen einen zufälligen y-Punkt in dem Spielfeld mit gegebener Höhe.
     * Dabei wird auch die Richtung, in die die Schlange geht berücksichtigt, damit man
     * nicht am Anfang direkt aus dem Spielfeld läuft und verliert.
     * @param h     Die Höhe des Spielfelds
     * @return      Einen zufälligen y-Punkt im Spielfeld
     */
    private int getRandomVerticalPoint(int h){
//      Falls die Schlange nach oben geht ist das Minimum ein Fünftel der Höhe; ansonsten 0
        int min = (dir == up)? h / 5: 0;
//      Falls die Schlange nach rechts geht ist das Maximum bei VierFünftel der Höhe;
//      ansonsten bei der Höhe - 1
        int max = (dir == down)? (h * 4) / 5: h - 1;
//      Dann geben wir eine Zufallszahl in dem Intervall [min .. max] zurück
//      Damit ein geschlossenes Intervall gegeben ist muss man zum Umfang eins dazuzählen 
//      (da Math.random() eine Zufallszahl im halboffenen Intervall [0 .. 1[ erzeugt)
        return (int) (Math.random() * (max - min + 1)) + min;
    }
    
    /**
     * Diese Methode bewegt die Schlange um einen Schritt, und schaut, ob etwas von ihr gefressen wurde
     * @param f     Das Food-Objekt, das derzeit auf dem Spielfeld ist
     * @param w     Die Breite des Spielfelds
     * @param h     Die Höhe des Spielfelds
     * @return      falls das Food-Objekt gefressen wurde {@code true}; ansonsten {@code false}
     * @throws SnakeAteItselfException      Falls die Schlange sich selbst gefressen hat
     * @throws SnakeHitBordersException     Falls die Schlange die Spielfeldgrenzen überschritten hat
     */
    public boolean move(Food f, int w, int h) throws SnakeAteItselfException, SnakeHitBordersException{
//      Wir speichern das letzte Teil zwischen, damit wir es, falls das Food-Objekt gefressen
//      wurde, wieder an die Schlange anhängen können
        SnakePart lastPart = snakeParts.get(snakeParts.size() - 1);
        
//      Dann wird jedes Objekt in der ArrayList um 1 nach hinten geshiftet
        for(int i = snakeParts.size() - 1; i > 0; i--){
            snakeParts.set(i, snakeParts.get(i - 1));
        }
        
//      Und dem ersten Objekt wird das bewegte Teil zugewiesen
        snakeParts.set(0, getMovedElement(w, h));
        
//      Falls die Schlange sich an Regeln zu halten hat
        if(withRules)
//          wird für jedes außer dem ersten Teil geschaut
            for(int i = 1; i < snakeParts.size(); i++)
//              ob das erste Teil dem Teil an Index i gleicht
                if(snakeParts.get(0).equals(snakeParts.get(i)))
//                  Falls dies so ist, so wird eine SnakeAteItselfException geworfen
                    throw new SnakeAteItselfException("Your Snake ate itself!");
        
//      Ansonsten falls das erste Teil auf dem Food-Objekt ist
        if(snakeParts.get(0).getxInGrid() == f.getxInGrid() && snakeParts.get(0).getyInGrid() == f.getyInGrid()){
//          Wird das letzte Objekt wieder angehängt
            snakeParts.add(lastPart);
//          Der Score wird erhöht
            score++;
//          Und true wird zurückgegeben, was anzeigt, dass ein neues Food-Objekt erzeugt werden muss
            return true;
        }
//      Ansonsten wird false zurückgegeben
        return false;
    }
    
    /**
     * Diese Methode gibt das erste Element um einen Schritt weitergerückt zurück
     * @param w     Die Breite des Spielfelds
     * @param h     Die Höhe des Spielfelds
     * @return      Das erste Teil um einen Schritt weitergerückt
     * @throws SnakeHitBordersException     falls die Spielfeldgrenzen unerlaubterweise überschritten
     *                                      wurde
     */
    private SnakePart getMovedElement(int w, int h) throws SnakeHitBordersException{
//      Zuerst benötigen wir eine Referenz auf das erste Teil
        SnakePart front = snakeParts.get(0);
        
//      Und entscheiden nach Richtung, in die wir gehen wollen
        switch(dir){
//          Falls wir nach oben gehen
            case up:
//              Verändern wir die x-Position nicht, und geben dem neuen Teil entweder die Höhe
//              des ersten - 1 oder die Spielfeldhöhe - 1
                return new SnakePart(
                        w, h,
                        front.getxInGrid(), 
                        (front.getyInGrid() != 0 || (!infinitePlayGrid && withRules))?
                                front.getyInGrid() - 1:
                                h - 1
                );
//          Falls wir nach unten gehen
            case down:
//              Verändern wir die x-Position nicht, und geben dem neuen Teil entweder die Höhe
//              des ersten + 1 oder 0
                return new SnakePart(
                        w, h,
                        front.getxInGrid(), 
                        (front.getyInGrid() + 1 != h || (!infinitePlayGrid && withRules))?
                                front.getyInGrid() + 1:
                                0
                );
//          Falls wir nach links gehen
            case left:
//              Verändern wir die y-Position nicht, und geben dem neuen Teil entweder die Breitenposition
//              des ersten - 1 oder die Spielfeldbreite - 1
                return new SnakePart(
                        w, h,
                        (front.getxInGrid() != 0 || (!infinitePlayGrid && withRules))?
                                front.getxInGrid() - 1:
                                w - 1, 
                        front.getyInGrid()
                );
//          Falls wir nach rechts gehen
            case right:
//              Verändern wir die y-Position nicht, und geben dem neuen Teil entweder die Breitenposition
//              des ersten + 1 oder 0
                return new SnakePart(
                        w, h,
                        (front.getxInGrid() != w - 1 || (!infinitePlayGrid && withRules))?
                                front.getxInGrid() + 1:
                                0, 
                        front.getyInGrid()
                );
//          Bei einem anderen Wert (nicht möglich, allerdings zur fehlerfreien Kompilierbarkeit benötigt)
//          werfen wir eine Exception, die zeigt, dass das Programm falsch ausgeführt wurde
//          Grund dafür kann nur ein Aufruf der Snake#move(int, int)-Methode bevor das Attribut 
//          Snake#dir initialisiert wurde sein. Dies wird allerdings im einzigen Konstruktor der Klasse
//          Snake verhindert
            default:
                throw new SnakeFalseExecutionException();
        }
    }
    
    /**
     * Diese Methode gibt ihnen den derzeitigen Score dieses Snake-Objekts
     * @return  den Score, und damit die Anzahl an gefressenen Food-Objekten
     */
    public int getScore(){
        return score;
    }
    
    /**
     * Diese Methode lässt das Snake-Objekt in die gegebene Richtung laufen
     * @param dir   Die Richtung, in die das Objekt gehen soll
     */
    public void changeDirectionTo(Direction dir){
        this.dir = dir;
    }
    
    /**
     * Diese Methode setzt die Eigenschaft, ob die Schlange sich an Regeln zu halten hat
     * @param wR    ob die Schlange sich an Regeln zu halten hat
     */
    public void setWithRules(boolean wR){
        this.withRules = wR;
    }
    
    /**
     * Diese Methode setzt die Eigenschaft, ob die Schlange an der anderen Seite des Spielfelds
     * wieder auftauchen, und somit das Spielfeld unendlich machen soll
     * @param inf   ob das Spielfeld unendlich sein soll
     */
    public void setInfinite(boolean inf){
        this.infinitePlayGrid = inf;
    }
    
    /**
     * Diese Methode zeigt dem Snake-Objekt, dass die Größe des Spielfelds geändert wurde,
     * und zeigt dies allen SnakePart-Objekten, aus dem dieses Objekt besteht
     * @param w     Die neue Breite des Spielfelds
     * @param h     Die neue Höhe des Spielfelds
     */
    public void notifySizeChanged(int w, int h){
//      Wir gehen durch alle SnakePart-Objekte in snakeParts
        for(SnakePart s : snakeParts)
//          und zeigen jedem an, dass sich die Spielfeldgröße auf w * h geändert hat
            s.notifySizeChanged(w, h);
    }
    
    /**
     * Diese Methode zeichnet das Snake-Objekt mit den gegebenen Offsets.
     * Dabei wird der Körper der Schlange in Schwarz, und ihr Kopf (das erste Element)
     * in Rot gezeichnet
     * @param x     Das Offset in x-Richtung
     * @param y     Das Offset in y-Richtung
     * @param s     Die Größe eines Felds
     * @param g     Das Graphics-Objekt, auf dem wir zeichnen
     */
    public void drawSnake(int x, int y, int s, Graphics g){
//      Zuerst gehen wir durch alle SnakePart-Objekte außer dem ersten
        for(int i = 1; i < snakeParts.size(); i++){
//          Und zeichnen es in Schwarz
            g.fillRect(
                    x + snakeParts.get(i).getxInGrid() * s, 
                    y + snakeParts.get(i).getyInGrid() * s, 
                    s, 
                    s
            );
        }
        
//      Dann malen wir das erste Element in Rot
        g.setColor(Color.RED);
        
        g.fillRect(
                x + snakeParts.get(0).getxInGrid() * s, 
                y + snakeParts.get(0).getyInGrid() * s,
                s,
                s
        );
        
//      Und stellen die vorherige Farbe wieder her
        g.setColor(Color.BLACK);
    }
    
    /**
     * Diese Methode überprüft, ob die Schlange die gegebenen Koordinaten überdeckt.<br>
     * Falls diese Koordinaten außerhalb des Spielfelds liegen* wird immer {@code false} zurückgegeben<br>
     * * das bedeutet: (x < 0 || y < 0 || x >= Spielfeldbreite || y >= Spielfeldhöhe)
     * @param x     Die x-Koordinate, die überprüft werden soll
     * @param y     Die y-Koordinate, die überprüft werden soll
     * @return      Ob die Schlange diese Koordinate überdeckt
     */
    public boolean contains(int x, int y){
//      Wir gehen durch alle SnakePart-Objekte
        for(SnakePart s : snakeParts){
//          Und falls bei einem davon die Koordinaten übereinstimmen
            if(s.getxInGrid() == x && s.getyInGrid() == y)
//              Geben wir true zurück
                return true;
        }
//      Falls wir durch die gesamte ArrayList gehen konnten, ohne true zurückgeben zu müssen
//      So geben wir false zurück
        return false;
    }
}