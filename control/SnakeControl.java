/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und gehört zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.control;

import java.awt.Color;
import java.awt.Graphics;

import de.jeanpierrehotz.snake.parts.Food;
import de.jeanpierrehotz.snake.parts.Snake;
import de.jeanpierrehotz.snake.parts.SnakePlayingGrid;
import de.jeanpierrehotz.snake.parts.Snake.Direction;

import de.jeanpierrehotz.snake.parts.exceptions.SnakeAteItselfException;
import de.jeanpierrehotz.snake.parts.exceptions.SnakeHitBordersException;

import de.jeanpierrehotz.ui.SnakeUI;

import static de.jeanpierrehotz.snake.parts.Snake.Direction.up;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.down;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.right;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.left;

import static de.jeanpierrehotz.control.SnakeControl.CauseOfPause.*;

/**
 * Diese Klasse repräsentiert eine Steuerung eines Snake-Spiels. Dieses Spiel
 * wird von einem {@link SnakeUI}-Objekt angezeigt.<br>
 * Diese Steuerung beinhaltet einen Thread, der für den Spielablauf zuständig ist,
 * und ein {@link Snake}-, ein {@link Food}- und ein {@link SnakePlayingGrid}-Objekt,
 * die insgesamt das Spiel an sich bilden.<br>
 * Die Klasse bietet der UI die Möglichkeit ein neues Spiel zu starten, das vorherige
 * Spiel (falls es eins gibt!) wieder aufzunehmen, und evtl. vorher die Einstellungen
 * erneut zu speichern.<br>
 * Außerdem wird von der UI eine Nachricht geschickt, sobald der User eingibt, dass sich
 * die Richtung, in die sich die Schlange bewegen soll ändern soll.
 * @author Jean-Pierre Hotz
 * @see de.jeanpierrehotz.ui.SnakeUI
 * @see de.jeanpierrehotz.snake.Snake
 * @see de.jeanpierrehotz.snake.SnakePlayingGrid
 * @see de.jeanpierrehotz.snake.Food
 */
public class SnakeControl{
    /**
     * Dieses Objekt ist repräsentiert die Schlange,
     * die sich auf der Spielfläche bewegen soll
     */
    private Snake playingSnake;
    /**
     * Dieses Objekt repräsentiert das Essen der Schlange,
     * das sich derzeit auf dem Spielfeld befindet
     */
    private Food currentFood;
    /**
     * Dieses Objekt repräsentiert das Spielfeld, auf dem derzeitig gespielt wird
     */
    private SnakePlayingGrid playGrid;
    
    /**
     * Diese Variable zeigt ihnen an, ob die Steuerung
     * sich derzeitig in einem Spiel befindet
     */
    private boolean inGame;
    /**
     * Diese Variable zeigt ihnen an, ob der User in dem derzeitigen Spiel "cheatet"
     */
    private boolean cheating;
    /**
     * Diese Variable zeigt ihnen an, ob das Spielfeld unendlich sein soll
     */
    private boolean infinite;
    /**
     * Diese Variable zeigt ihnen an, ob das Spiel mit den üblichen Regeln abläuft.
     */
    private boolean withRules;
    
    /**
     * Dieser Thread ist für das Steuern des zeitlichen Ablaufs eines Spiels zuständig
     */
    private Thread playThread;
    
    /**
     * Dieses Objekt ist dafür zuständig, das Spiel, und die Optionen / das Startmenü anzuzeigen
     */
    private SnakeUI gui;

    /**
     * Sobald ein neues Steuerungs-Objekt erzeugt wird, wird mit diesem auch ein neues
     * UI-Objekt mit einer Referenz auf das neue SnakeControl-Objekt erzeugt.<br>
     * Dieses UI-Objekt wird in seinem Konstruktor sichtbar gemacht.
     */
    public SnakeControl(){
        gui = new SnakeUI(this);
    }
    
    /**
     * Diese Methode beginnt (initialisiert) ein Snake-Spiel.<br>
     * Es liest alle benötigten Werte aus dem UI-Objekt ein.
     */
    public void init(){
//      Wir lesen alle Werte ein
        int sp = gui.getColumns();
        int ze = gui.getRows();
        
        int vsp = (gui.getWidth() - 40) / sp;    //  Diese Werte geben an, wie breit / hoch ein Feld wäre,
        int vze = (gui.getHeight() - 40) / ze;   //  wenn diese  nicht quadratisch wären
        
        boolean dG = gui.shouldDrawGrid();
        infinite = gui.shouldBeInfinite();
        withRules = gui.shouldBeWithRules();
        
        cheating = gui.shouldBeCheating();
        
//      
//      Hier eine kleine Erläuterung der Berechnung der Offsets
//        
//                               Breite eines Feldteils
//                              |  |
//                           _   __
//      Höhe eines Feldteils _  |__| <- Ein Feldteil
//
//      |Breite des Fensters                                                          |
//      |-----------------------------------------------------------------------------|
//      |                                                                             |
//      |x-Offset   |sp mal Breite eines Feldteils                        |x-Offset   |
//      |-----------|-----------------------------------------------------|-----------|
//       _____________________________________________________________________________
//      | Snake - CT-Projekt 2016 Jean-Pierre Hotz                   | _ | [] |   x   |
//      |____________________________________________________________|___|____|_______|  ___________________
//      |                                                                             |   | y-Offset      | Höhe
//      |                                                                             |   |               | des
//      |            __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __            |  _|_              | Fensters
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   | ze mal        |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   | Höhe          |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   | eines         |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   | Feldteils     |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   |               |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   |               |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   |               |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   |               |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |  _|_              |
//      |                                                                             |   | y-Offset      |
//      |                                                                             |   |               |
//      |_____________________________________________________________________________|  _|_______________|_
//      
//      Aus vorhergehender Verbildlichung kann gesehen werden, dass man für die Offsets
//      zuerst die Breite / die Höhe des Spielfelds von der Breite / der Höhe des Fensters
//      abziehen muss, und diesen Wert durch zwei teilen muss, da sich dieser Freiraum
//      auf beide Seiten gleich verteilen soll.
//      Die auf diese Weise berechnete Offsets werden dann dem UI_Objekt mitgeteilt
//
//      Wir nehmen den kleineren der Werte für die theoretische Größe eines Felds,
//      damit alle der Felder sichtbar sein können
//      
        if(vsp > vze){
//          Die Berechung beider Offsets mit dem kleineren 
//          Wert für die Breite eines Feldteils eingesetzt
            gui.setOffSets((gui.getWidth() - sp * vze) / 2, (gui.getHeight() - ze * vze) / 2 + 10);
            
//          Dann wird ein Spiel mit den gegebenen und berechneten Informationen vorbereitet
            init(sp, ze, vze, Direction.getRandomDirection(), withRules, infinite, dG);
        }else{
//          Die Berechung beider Offsets mit dem kleineren 
//          Wert für die Breite eines Feldteils eingesetzt
            gui.setOffSets((gui.getWidth() - sp * vsp) / 2, (gui.getHeight() - ze * vsp) / 2 + 10);
            
//          Dann wird ein Spiel mit den gegebenen und berechneten Informationen vorbereitet
            init(sp, ze, vsp, Direction.getRandomDirection(), withRules, infinite, dG);
        }
        
//      Außerdem zeigen wir an, dass wir uns jetzt in einem Spiel befinden
        inGame = true;
        
//      Lassen die UI das Spiel anzeigen
        gui.showGame();
        
//      Und starten den zeitsteuernden Thread für das Spiel
        playThread = new Thread(new Timer());
        playThread.start();
    }
    
    /**
     * Diese Methode initialisiert alle am Spiel beteiligten Objekte mit den gegebenen Werten
     * @param w     Die Breite des Spielfelds in Feldern
     * @param h     Die Höhe des Spielfelds in Feldern
     * @param s     Die Größe eines Feld in px
     * @param dir   Die Richtung, in die die Schlange anfangs gehen soll
     * @param wR    Ob die Schlange sich an Regeln halten soll
     * @param inf   Ob das Spielfeld unendlich sein soll
     * @param dG    Ob das Spielfeld gezeichnet werden soll
     */
    private void init(int w, int h, int s, Direction dir, boolean wR, boolean inf, boolean dG){
//      Zuerst müssen wir ein Spielfeld erzeugen, auf dem sich die Schlange befinden kann
        playGrid = new SnakePlayingGrid(w, h, s, dG);
//      Dann benötigen wir eine Schlange, damit wir wissen, 
//      wo wir das Food-Objekt generieren können
        playingSnake = new Snake(w, h, dir, wR, inf);
//      Schlussendlich generieren wir ein neues Food-Objekt
        generateFood();
    }
    
    /**
     * Diese Enumeration wird benutzt um zu unterscheiden, aus welchem Grund das Spiel 
     * pausiert wurde, um zu entscheiden, ob es wieder aufnehmbar ist, oder nicht.
     * @author Jean-Pierre Hotz
     */
    public static enum CauseOfPause{
        /*
         * Falls die Schlange unerlaubterweise die Grenzen überschritten hat
         */
        BorderHit,
        /*
         * Falls die Schlange sich selbst gefressen hat
         */
        EatenItself,
        /*
         * Falls der Pausierbefehl von der UI kam (z.B. Escape gedrückt)
         */
        UICause;
    }
    
    /**
     * Diese Methode pausiert das Spiel, und lässt die UI das Menü mit der gegebenen Nachricht
     * und dem Score anzeigen. Außerdem wird anhand des gegebenen Wertes für cause angegeben,
     * ob das Spiel wieder aufnehmbar ist.
     * @param message   Die Nachricht, die von der UI angezeigt werden soll
     * @param cause     Die Ursache für die Pausierung
     * @see CauseOfPause
     */
    public void pauseGame(String message, CauseOfPause cause){
//      Wir brechen den Thread ab, indem wir ihm sagen, dass wir nicht mehr in einem Spiel sind
        inGame = false;
        
//      Falls das Spiel durch den "Tod" der Schlange verursacht wird
        if(cause == BorderHit || cause == EatenItself){
//          wird der UI gesagt, dass das Spiel nicht wieder aufnehmbar ist
            gui.setResumable(false);
        }else{
//          Und ansonsten ist es wieder aufnehmbar
            gui.setResumable(true);
        }
        
//      Der letzte Frame des Spiels wird gelöscht, indem noch ein letztes Mal gemalt wird,
//      obwohl inGame bereits auf false gesetzt wurde (-> es wird nichts ausgegeben;
//      siehe dazu SnakeUI#paint(Graphics))
        gui.repaint();
        
//      Und die UI soll wieder ihre Komponenten ausgeben
        gui.showUI(message, playingSnake.getScore());
    }
    
    /**
     * Diese Methode lässt den User das Spiel wieder aufnehmen.
     * Dies ist NUR möglich, wenn ein Spiel davor bereits gespielt wurde!
     */
    public void resumeGame(){
//      Zuerst lassen wir von der UI wieder das Spiel anzeigen
        gui.showGame();
        
//      Dann lassen wir angeben, dass wir uns wieder in einem Spiel befinden
        inGame = true;
        
//      Und starten den zeitsteuernden Thread wieder.
//      Die am Spiel beteiligten Objekte sind noch in ihrem vorherigen Zustand, und können
//      daher einfach dort weitermachen, wo sie aufgehört haben.
        playThread = new Thread(new Timer());
        playThread.start();
    }
    
    /**
     * Diese Methode speichert die neuen Eingaben und nimmt dann das Spiel wieder auf
     */
    public void saveOptionsAndResumeGame(){
//      Zuerst werden alle Eingaben eingelesen
        int sp = gui.getColumns();
        int ze = gui.getRows();
        
        int vsp = (gui.getWidth() - 40) / sp;
        int vze = (gui.getHeight() - 40) / ze;
        
        boolean dG = gui.shouldDrawGrid();
        boolean inf = gui.shouldBeInfinite();
        boolean wR = gui.shouldBeWithRules();
        
        cheating = gui.shouldBeCheating();
        
//      Dann die Offsets berechnet, und gespeichert
//      Für weitere Informationen dazu siehe SnakeControl#init()
        if(vsp > vze){
            gui.setOffSets((gui.getWidth() - sp * vze) / 2, (gui.getHeight() - ze * vze)/ 2 + 10);
            
//          Dann wird die Größe des Spielfelds gespeichert
            playGrid.setSize(sp, ze, vze);
            
//          Und falls gecheatet werden soll
            if(cheating){
//              Wird auf jeden Fall ohne Regeln und mit unendlichem Spielfeld gespielt
                playingSnake.setWithRules(false);
                playingSnake.setInfinite(true);
            }else{
//              und falls nicht werden die eingelesenen Werte gespeichert
                playingSnake.setWithRules(wR);
                playingSnake.setInfinite(inf);
            }
        }else{
            gui.setOffSets((gui.getWidth() - sp * vsp) / 2, (gui.getHeight() - ze * vsp) / 2 + 10);
            
//          Dann wird die Größe des Spielfelds gespeichert
            playGrid.setSize(sp, ze, vsp);

//          Und falls gecheatet werden soll
            if(cheating){
//              Wird auf jeden Fall ohne Regeln und mit unendlichem Spielfeld gespielt
                playingSnake.setWithRules(false);
                playingSnake.setInfinite(true);
            }else{
//              und falls nicht werden die eingelesenen Werte gespeichert
                playingSnake.setWithRules(wR);
                playingSnake.setInfinite(inf);
            }
        }
//      Letzten Endes werden alle übrigen Werte gespeichert
        playGrid.setDrawingGrid(dG);
        playingSnake.notifySizeChanged(sp, ze);
        currentFood.notifySizeChanged(sp, ze);
        
//      Und das Spiel wieder aufgenommen
        resumeGame();
    }
    
    /**
     * Diese Methode zeichnet das Spielfeld, da die UI keinen
     * Zugriff auf die spielbeteiligten Objekte hat
     * @param x     das x-Offset des Spielfelds
     * @param y     das y-Offset des Spielfelds
     * @param g     das Graphics-Objekt auf dem gezeichnet werden soll
     */
    public void paintEverything(int x, int y, Graphics g){
//      zuerst wird sicher gestellt, dass in Schwarz gezeichnet wird
        g.setColor(Color.BLACK);
        
//      Dann malen wir das Spielfeld
        playGrid.drawPlayingGrid(x, y, g);
//      die Schlange
        playingSnake.drawSnake(x, y, playGrid.getSize(), g);
//      Und das Food-Objekt
        currentFood.drawFood(x, y, playGrid.getSize(), g);
    }
    
    /**
     * Diese Methode soll aufgerufen werden, sobald eine Nachricht des Users anzeigt, dass
     * er möchte, dass die Schlange ihre Richtung ändert.<br>
     * Diese greift auf das Snake-Objekt zu, und aktualisiert dessen Attribut.
     * @param directionChangingTo   die Richtung, in die sich die Schlange bewegen soll
     */
    public void onDirectionChanging(Direction directionChangingTo){
        playingSnake.changeDirectionTo(directionChangingTo);
    }
    
    /**
     * Diese Methode generiert ein neues Food-Objekt, das so gut es geht nicht innerhalb der
     * Schlange liegt.
     */
    private void generateFood(){
//      Wir benötigen eine x- und eine y-Koordinate für das Food-Objekt,
//      und außerdem noch einen Zähler
        int x = 0, y = 0, ctr = 0;
        
//      Wir wollen mindestens einmal einen zufälligen Wert für die Koordinaten zuweisen
        do{
            x = (int) (Math.random() * playGrid.getWidth());    //  dieser Wert liegt im Intervall [0 .. Breite]
            y = (int) (Math.random() * playGrid.getHeight());   //  dieser Wert liegt im Intervall [0 .. Höhe]
//      dies tun wir so lange, wie die Schlange das Feld mit den generierten Koordinaten nicht mehr
//      überdeckt, allerdings höchstens 4000 Iterationen, damit sichergestellt wird, dass das Spiel
//      sich nicht aufhängt, sobald die Schlange das gesamte Feld bedeckt
        }while(playingSnake.contains(x, y) && ctr++ < 4000);
        
//      dann erzeugen wir ein neues Food-Objekt mit den generierten Koordinaten
        currentFood = new Food(x, y);
    }
    
    /**
     * Diese Methode zeigt ihnen, ob sich die Steuerung derzeit in einem Spiel befindet
     * @return
     */
    public boolean isInGame(){
        return inGame;
    }

    /**
     * Diese Methode wird von der Kommandozeile aufgerufen
     * @param args  Die von der Kommandozeile übergebenen Argumente
     */
    public static void main(String[] args){
//      Wir erzeugen einfach ein neues SnakeControl-Objekt.
//      Dieses lässt dann eine UI anzeigen, mit der man dann Snake spielen kann
        new SnakeControl();
    }
    
    /**
     * Diese Methode lässt die Schlange sich selbstständig (abhängig von den Einstellungen) bewegen.
     * Sie versucht sich an die Regeln so gut es geht zu halten, und das bestmögliche
     * computer-gesteuerte Ergebnis zu erzielen.
     * @param ctr   der Zählstand, der der Bewegung der Schlange bei Cheats hilft
     * @return      der aktualisierte Zählstand
     */
    private int moveIfCheating(int ctr) {
        int x = playingSnake.getFirstX();
        int y = playingSnake.getFirstY();

        if(withRules) {
            if(infinite) {
                
                /*
                 * Bei Regeln mit unendlicher Welt fahren wir die ganze Höhe ab (nach oben oder unten),
                 * fahren eins nach rechts und fahren dann wieder hoch.
                 * Diesen Prozess wiederholen wir, bis das Spiel beendet wurde.
                 */
                
                if(ctr == 0) {
                    playingSnake.changeDirectionTo(right);
                }else if(ctr == 1) {
                    playingSnake.changeDirectionTo(down);
                }else if(ctr == playGrid.getHeight()) {
                    playingSnake.changeDirectionTo(right);
                }else if(ctr == playGrid.getHeight() + 1) {
                    playingSnake.changeDirectionTo(up);
                }

                ctr = (ctr + 1) % (2 * playGrid.getHeight());
            }else {

                /*
                 * Bei Regeln und endlicher Welt müssen wir zwischen drei Fällen unterscheiden:
                 *  - Höhe ist gerade, Breite ist egal
                 *  - Höhe ist ungerade, Breite ist gerade
                 *  - Höhe ist ungerade, Breite ist ungerade
                 */
                
                if(playGrid.getHeight() % 2 == 0) {
                    
                    /*
                     * Wenn die Höhe gerade ist (Breite ist egal)
                     */

                    /*
                     * Beim ersten Durchlauf (bei allen anderen Durchläufen ist ctr = 1)
                     * wird die Snake in die (für ihre Position) richtige Richtung gelenkt,
                     * um zu verhindern, dass sie auf keinen markanten Punkt trifft, und
                     * einfach in die Wand fährt
                     */
                    
                    if(ctr == 0) {
                        ctr++;
                        if(x == 0) {
                            playingSnake.changeDirectionTo(up);
                        }else if(y % 2 == 0) {
                            playingSnake.changeDirectionTo(right);
                        }else {
                            playingSnake.changeDirectionTo(left);
                        }
                    }

                    /*
                     * Dann wird sie nach folgendem Muster bewegt (Pfeile zeigen einen besonderen
                     * Punkt, an dem die Richtung in die von dem Pfeil angezeigte Richtung gewechselt):
                     * 
                     *   __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __
                     *  |->|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     *  |__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     *  |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     *  |__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     *  |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     *  |__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     *  |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     *  |^^|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     *  
                     */

                    if(x == 0 && y == 0) {
                        playingSnake.changeDirectionTo(right);
                    }else if(y == playGrid.getHeight() - 1) {
                        if(x == 0) {
                            playingSnake.changeDirectionTo(up);
                        }else if(x == playGrid.getWidth() - 1){
                            playingSnake.changeDirectionTo(left);
                        }
                    }else if(x == playGrid.getWidth() - 1 && y % 2 == 0) {
                        playingSnake.changeDirectionTo(down);
                    }else if(x == playGrid.getWidth() - 1 && y % 2 == 1) {
                        playingSnake.changeDirectionTo(left);
                    }else if(x == 1 && y % 2 == 1) {
                        playingSnake.changeDirectionTo(down);
                    }else if(x == 1 && y % 2 == 0) {
                        playingSnake.changeDirectionTo(right);
                    }
                }else if(playGrid.getWidth() % 2 == 0) {

                    /*
                     * Wenn die Höhe ungerade, und die Breite gerade ist
                     */
                    
                    /*
                     * Beim ersten Durchlauf (bei allen anderen Durchläufen ist ctr = 1)
                     * wird die Snake in die (für ihre Position) richtige Richtung gelenkt,
                     * um zu verhindern, dass sie auf keinen markanten Punkt trifft, und
                     * einfach in die Wand fährt
                     */
                    
                    if(ctr == 0) {
                        if(x == 0) {
                            playingSnake.changeDirectionTo(up);
                        }else if(y == 0 || y % 2 == 1) {
                            playingSnake.changeDirectionTo(right);
                        }else if(y % 2 == 0) {
                            playingSnake.changeDirectionTo(left);
                        }
                        ctr++;
                    }

                    /*
                     * Dann wird sie nach folgendem Muster bewegt (Pfeile zeigen einen besonderen
                     * Punkt, an dem die Richtung in die von dem Pfeil angezeigte Richtung gewechselt):
                     * 
                     *  __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __
                     * |->|__|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |__|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|
                     * |__|__|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|
                     * |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     * |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     * |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |^^|__|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     */


                    if(x == 0) {
                        if(y == 0) {
                            playingSnake.changeDirectionTo(right);
                        }else if(y == playGrid.getHeight() - 1) {
                            playingSnake.changeDirectionTo(Direction.up);
                        }
                    }else if(y == 0) {
                        if(x == playGrid.getWidth() - 1) {
                            playingSnake.changeDirectionTo(down);
                        }
                    }else if(y == playGrid.getHeight() - 1) {
                        if(x == playGrid.getWidth() - 1) {
                            playingSnake.changeDirectionTo(Direction.left);
                        }
                    }else if(y == 1 || y == 2) {
                        if(x == 1) {
                            playingSnake.changeDirectionTo(down);
                        }else if(x % 2 == 1) {
                            if(y == 1) {
                                playingSnake.changeDirectionTo(down);
                            }else {
                                playingSnake.changeDirectionTo(Direction.left);
                            }
                        }else {
                            if(y == 1) {
                                playingSnake.changeDirectionTo(Direction.left);
                            }else {
                                playingSnake.changeDirectionTo(Direction.up);
                            }
                        }
                    }else if(y % 2 == 1) {
                        if(x == 1) {
                            playingSnake.changeDirectionTo(right);
                        }else if(x == playGrid.getWidth() - 1) {
                            playingSnake.changeDirectionTo(down);
                        }
                    }else if(y % 2 == 0) {
                        if(x == 1) {
                            playingSnake.changeDirectionTo(down);
                        }else if(x == playGrid.getWidth() - 1) {
                            playingSnake.changeDirectionTo(Direction.left);
                        }
                    }
                }else {
                    
                    /*
                     * Wenn die Höhe und die Breite ungerade sind wird in 2-Durchlauf Zyklen gearbeitet.
                     * Dabei wird nach dem Schema gearbeitet, als wäre die Höhe ungerade und die Breite gerade.
                     * Um dieses Schema allerdings ausführen zu können muss man eine Spalte auslassen.
                     * Dadurch wird mit jedem Durchlauf eines Zykluses zwischen der ersten Spalte und der
                     * letzten Spalte auslassen gewechselt.
                     */
                    
                    /*
                     * Am Punkt P(2|0) wird zwischen den auszulassenden Spalten gewechselt
                     */
                    
                    if(x == 2 && y == 0) {
                        ctr += (ctr == 1)? 1: -1;
                    }
                    
                    /*
                     * Beim ersten Durchlauf (bei allen anderen Durchläufen ist ctr =/= 0)
                     * wird die Snake in die (für ihre Position) richtige Richtung gelenkt,
                     * um zu verhindern, dass sie auf keinen markanten Punkt trifft, und
                     * einfach in die Wand fährt
                     */
                    
                    if(ctr == 0) {
                        if(x == 0) {
                            playingSnake.changeDirectionTo(Direction.up);
                        }else if(y == 0 || y % 2 == 1) {
                            playingSnake.changeDirectionTo(right);
                        }else if(y % 2 == 0) {
                            playingSnake.changeDirectionTo(Direction.left);
                        }
                        ctr++;
                    }
                    
                    /*
                     * Im zweiten Durchlauf wird die (imaginäre) Position der Snake verschoben, damit
                     */
                    
                    else if(ctr == 2) {
                        x--;
                    }
                    
                    /*
                     * Dann wird sie nach folgendem Muster bewegt (Pfeile zeigen einen besonderen
                     * Punkt, an dem die Richtung in die von dem Pfeil angezeigte Richtung gewechselt):
                     * 
                     * Durchlauf 1 des Zyklus (ctr = 1):
                     *  __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __
                     * |->|__|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|__|
                     * |__|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|__|
                     * |__|__|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|__|
                     * |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|__|
                     * |__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|__|
                     * |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|__|
                     * |__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|__|
                     * |__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|__|
                     * |^^|__|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|__|
                     * 
                     * Durchlauf 2 des Zyklus (ctr = 2):
                     *  __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __
                     * |->|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |__|__|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|<-|vv|
                     * |__|__|__|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|^^|<-|
                     * |__|__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |__|__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     * |__|__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |__|__|vv|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     * |__|__|->|__|__|__|__|__|__|__|__|__|__|__|__|__|vv|
                     * |^^|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|<-|
                     * 
                     * Somit ist gewährleistet, dass innerhalb kurzer Zeit (eines Zykluses) das gesamte Spielfeld
                     * abgefahren wird, und die Schlange (so gut wie möglich) nicht in sich selbst hineinfährt.
                     * 
                     */
                    
                    if((ctr == 1 && x == 0) || (ctr == 2 && x == -1)) {
                        if(y == 0) {
                            playingSnake.changeDirectionTo(right);
                        }else if(y == playGrid.getHeight() - 1) {
                            playingSnake.changeDirectionTo(Direction.up);
                        }
                    }else if(y == 0) {
                        if(x == playGrid.getWidth() - 2) {
                            playingSnake.changeDirectionTo(down);
                        }
                    }else if(y == playGrid.getHeight() - 1) {
                        if(x == playGrid.getWidth() - 2) {
                            playingSnake.changeDirectionTo(Direction.left);
                        }
                    }else if(y == 1 || y == 2) {
                        if(x == 1) {
                            playingSnake.changeDirectionTo(down);
                        }else if(x % 2 == 1) {
                            if(y == 1) {
                                playingSnake.changeDirectionTo(down);
                            }else {
                                playingSnake.changeDirectionTo(Direction.left);
                            }
                        }else {
                            if(y == 1) {
                                playingSnake.changeDirectionTo(Direction.left);
                            }else {
                                playingSnake.changeDirectionTo(Direction.up);
                            }
                        }
                    }else if(y % 2 == 1) {
                        if(x == 1) {
                            playingSnake.changeDirectionTo(right);
                        }else if(x == playGrid.getWidth() - 2) {
                            playingSnake.changeDirectionTo(down);
                        }
                    }else if(y % 2 == 0) {
                        if(x == 1) {
                            playingSnake.changeDirectionTo(down);
                        }else if(x == playGrid.getWidth() - 2) {
                            playingSnake.changeDirectionTo(Direction.left);
                        }
                    }

                    
                }
            }
        }else {
//          falls der Zählstand auf 0 steht
            if(ctr == 0)
//              wird die Schlange nach unten gesteuert
                playingSnake.changeDirectionTo(down);
//          falls der Zählstand auf 1 steht
            else if(ctr == 1)
//              wird die Schlange nach rechts gesteuert
                playingSnake.changeDirectionTo(right);
//              Danach wird der Zählstand erhöht, und so geteilt, 
//                  dass er mindestens eine Zeile durchläuft, falls das 
//                  Spielbrett breiter ist als die Schlange lang,
//                  oder die Schlangenlänge durchläuft, falls diese länger
//                  ist als die Spielfeldbreite
            ctr = (ctr + 1) % 
                    ((playGrid.getWidth() > playingSnake.getScore())? 
                            playGrid.getWidth(): 
                            playingSnake.getScore() + 1);
        }

        return ctr;
    }

    /**
     * Diese Methode führt einen Tick des Spiels aus.<br>
     * Dieser besteht daraus das Snake-Objekt um ein Feld zu bewegen, falls das Food-Objekt
     * gefressen wurde ein neues zu generieren, falls die Schlange stirbt das Spiel
     * abzubrechen, und die UI das aktualisierte Bild ausgeben zu lassen.<br>
     * Außerdem wird, falls gecheatet wird die Schlange selbstständig bewegt.
     * @param ctr   der Zählstand, der der Bewegung der Schlange bei Cheats hilft
     * @return      der aktualisierte Zählstand
     * @see SnakeControl#moveIfCheating(int)
     */
    private int onTick(int ctr){
//      Falls gecheatet wird
        if(cheating){
//          Wird die Schlange selbstständig bewegt
            ctr = moveIfCheating(ctr);
        }
        
        try{
//          Nun bewegen wir die Schlange um ein Feld, und falls diese uns sagt, dass
//          sie das Food-Objekt gefressen hat
            if(playingSnake.move(currentFood, playGrid.getWidth(), playGrid.getHeight())){
//              generieren wir ein neues
                generateFood();
            }
//      falls dabei allerdings eine Exception geworfen wird
        }catch(SnakeAteItselfException | SnakeHitBordersException e){
//          brechen wir das Spiel ab
            pauseGame(e.getMessage(), (e instanceof SnakeAteItselfException)? EatenItself: BorderHit);
        }
        
//      Dann lassen wir die UI das neue Bild ausgeben
        gui.repaint();
        
//      und geben schließlich den evtl. aktualisierten Zählstand zurück
        return ctr;
    }

    /**
     * Diese Klasse entspricht Zeitsteuerung für den Spielablauf.<br>
     * Dieser gibt, sobald ausgeführt, (sofern nich gecheatet wird) in einem
     * von der Teilfeldgröße abhängigen zeitlichen Abstand eine Nachricht an das
     * SnakeControl-Objekt.<br>
     * Falls gecheatet wird wird dieser Thread so schnell es geht durchgeführt
     * (so gut es geht ohne zeitliche Verzögerung)
     * @author Jean-Pierre Hotz
     */
    private class Timer implements Runnable{
        @Override
        public void run(){
//          der Zählstand, der nur für das Cheaten relevant ist
            int ctr = 0;
            
//          Eine Referenz auf den derzeitigen Thread, da ein geschleifter
//          Thread von seiner Referenz abhängig gemacht werden soll
            Thread currentThread = Thread.currentThread();
//          Abbruchbedingung ist, dass entweder das SnakeControl-Objekt sich nicht mehr
//          in einem Spiel befindet, oder die Referenz auf den Thread sich geändert hat
            while(currentThread == playThread && inGame){
//              Es wird ein Tick ausgeführt und der aktualisierte Zählstand gespeichert
                ctr = onTick(ctr);
                
                try{
//                  Dann wird der Thread (falls nicht gecheatet wird) abhängig
//                  von der Größe eines Teilfeldes pausiert
                    Thread.sleep((cheating)? (withRules)? 5: 0: (int) (((5 * playGrid.getSize()) / 3f) + (100f / 3f)));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Diese Methode lässt den zeitsteuernden Thread auf jeden Fall abbrechen, indem wir im eine
     * null-Referenz zuweisen
     * @see Timer
     */
    public void disposeThread(){
        playThread = null;
    }
}