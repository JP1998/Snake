/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und geh�rt zu einem
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

import static de.jeanpierrehotz.snake.parts.Snake.Direction.down;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.right;

import static de.jeanpierrehotz.control.SnakeControl.CauseOfPause.*;

/**
 * Diese Klasse repr�sentiert eine Steuerung eines Snake-Spiels. Dieses Spiel
 * wird von einem {@link SnakeUI}-Objekt angezeigt.<br>
 * Diese Steuerung beinhaltet einen Thread, der f�r den Spielablauf zust�ndig ist,
 * und ein {@link Snake}-, ein {@link Food}- und ein {@link SnakePlayingGrid}-Objekt,
 * die insgesamt das Spiel an sich bilden.<br>
 * Die Klasse bietet der UI die M�glichkeit ein neues Spiel zu starten, das vorherige
 * Spiel (falls es eins gibt!) wieder aufzunehmen, und evtl. vorher die Einstellungen
 * erneut zu speichern.<br>
 * Au�erdem wird von der UI eine Nachricht geschickt, sobald der User eingibt, dass sich
 * die Richtung, in die sich die Schlange bewegen soll �ndern soll.
 * @author Jean-Pierre Hotz
 * @see de.jeanpierrehotz.ui.SnakeUI
 * @see de.jeanpierrehotz.snake.Snake
 * @see de.jeanpierrehotz.snake.SnakePlayingGrid
 * @see de.jeanpierrehotz.snake.Food
 */
public class SnakeControl{
    /**
     * Dieses Objekt ist repr�sentiert die Schlange,
     * die sich auf der Spielfl�che bewegen soll
     */
    private Snake playingSnake;
    /**
     * Dieses Objekt repr�sentiert das Essen der Schlange,
     * das sich derzeit auf dem Spielfeld befindet
     */
    private Food currentFood;
    /**
     * Dieses Objekt repr�sentiert das Spielfeld, auf dem derzeitig gespielt wird
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
     * Dieser Thread ist f�r das Steuern des zeitlichen Ablaufs eines Spiels zust�ndig
     */
    private Thread playThread;
    
    /**
     * Dieses Objekt ist daf�r zust�ndig, das Spiel, und die Optionen / das Startmen� anzuzeigen
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
     * Es liest alle ben�tigten Werte aus dem UI-Objekt ein.
     */
    public void init(){
//      Wir lesen alle Werte ein
        int sp = gui.getColumns();
        int ze = gui.getRows();
        
        int vsp = (gui.getWidth() - 40) / sp;    //  Diese Werte geben an, wie breit / hoch ein Feld w�re,
        int vze = (gui.getHeight() - 40) / ze;   //  wenn diese  nicht quadratisch w�ren
        
        boolean dG = gui.shouldDrawGrid();
        boolean inf = gui.shouldBeInfinite();
        boolean wR = gui.shouldBeWithRules();
        
        cheating = gui.shuldBeCheating();
        
//      
//      Hier eine kleine Erl�uterung der Berechnung der Offsets
//        
//                               Breite eines Feldteils
//                              |  |
//                           _   __
//      H�he eines Feldteils _  |__| <- Ein Feldteil
//
//      |Breite des Fensters                                                          |
//      |-----------------------------------------------------------------------------|
//      |                                                                             |
//      |x-Offset   |sp mal Breite eines Feldteils                        |x-Offset   |
//      |-----------|-----------------------------------------------------|-----------|
//       _____________________________________________________________________________
//      | Snake - CT-Projekt 2016 Jean-Pierre Hotz                   | _ | [] |   x   |
//      |____________________________________________________________|___|____|_______|  ___________________
//      |                                                                             |   | y-Offset      | H�he
//      |                                                                             |   |               | des
//      |            __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __            |  _|_              | Fensters
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   | ze mal        |
//      |           |__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|__|           |   | H�he          |
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
//      Aus vorhergehender Verbildlichung kann gesehen werden, dass man f�r die Offsets
//      zuerst die Breite / die H�he des Spielfelds von der Breite / der H�he des Fensters
//      abziehen muss, und diesen Wert durch zwei teilen muss, da sich dieser Freiraum
//      auf beide Seiten gleich verteilen soll.
//      Die auf diese Weise berechnete Offsets werden dann dem UI_Objekt mitgeteilt
//
//      Wir nehmen den kleineren der Werte f�r die theoretische Gr��e eines Felds,
//      damit alle der Felder sichtbar sein k�nnen
//      
        if(vsp > vze){
//          Die Berechung beider Offsets mit dem kleineren 
//          Wert f�r die Breite eines Feldteils eingesetzt
            gui.setOffSets((gui.getWidth() - sp * vze) / 2, (gui.getHeight() - ze * vze)/ 2 + 10);
            
//          Falls der User cheaten m�chte
            if(cheating){
//              Wird auf jeden Fall ohne Regeln und mit unendlichem Spielfeld gespielt
//              (Letzteres ist irrelevant, da, wenn ohne Regeln gespielt 
//              wird das Spielfeld automatisch unendlich ist)
                init(sp, ze, vze, Direction.getRandomDirection(), false, true, dG);
            }else{
//              Und falls nicht werden seine gegebenen und die berechneten Werte benutzt um
//              das Spiel nun endg�ltig vorzubereiten
                init(sp, ze, vze, Direction.getRandomDirection(), wR, inf, dG);
            }
        }else{
//          Die Berechung beider Offsets mit dem kleineren 
//          Wert f�r die Breite eines Feldteils eingesetzt
            gui.setOffSets((gui.getWidth() - sp * vsp) / 2, (gui.getHeight() - ze * vsp) / 2 + 10);
            
//          Falls der User cheaten m�chte
            if(cheating){
//              Wird auf jeden Fall ohne Regeln und mit unendlichem Spielfeld gespielt
//              (Letzteres ist irrelevant, da, wenn ohne Regeln gespielt 
//              wird das Spielfeld automatisch unendlich ist)
                init(sp, ze, vsp, Direction.getRandomDirection(), false, true, dG);
            }else{
//              Und falls nicht werden seine gegebenen und die berechneten Werte benutzt um
//              das Spiel nun endg�ltig vorzubereiten
                init(sp, ze, vsp, Direction.getRandomDirection(), wR, inf, dG);
            }
        }
        
//      Au�erdem zeigen wir an, dass wir uns jetzt in einem Spiel befinden
        inGame = true;
        
//      Lassen die UI das Spiel anzeigen
        gui.showGame();
        
//      Und starten den zeitsteuernden Thread f�r das Spiel
        playThread = new Thread(new Timer());
        playThread.start();
    }
    
    /**
     * Diese Methode initialisiert alle am Spiel beteiligten Objekte mit den gegebenen Werten
     * @param w     Die Breite des Spielfelds in Feldern
     * @param h     Die H�he des Spielfelds in Feldern
     * @param s     Die Gr��e eines Feld in px
     * @param dir   Die Richtung, in die die Schlange anfangs gehen soll
     * @param wR    Ob die Schlange sich an Regeln halten soll
     * @param inf   Ob das Spielfeld unendlich sein soll
     * @param dG    Ob das Spielfeld gezeichnet werden soll
     */
    private void init(int w, int h, int s, Direction dir, boolean wR, boolean inf, boolean dG){
//      Zuerst m�ssen wir ein Spielfeld erzeugen, auf dem sich die Schlange befinden kann
        playGrid = new SnakePlayingGrid(w, h, s, dG);
//      Dann ben�tigen wir eine Schlange, damit wir wissen, 
//      wo wir das Food-Objekt generieren k�nnen
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
         * Falls die Schlange unerlaubterweise die Grenzen �berschritten hat
         */
        BorderHit,
        /*
         * Falls die Schlange sich selbst gefressen hat
         */
        EatenItself,
        /*
         * Falls der Pausierbefehl von der UI kam (z.B. Escape gedr�ckt)
         */
        UICause;
    }
    
    /**
     * Diese Methode pausiert das Spiel, und l�sst die UI das Men� mit der gegebenen Nachricht
     * und dem Score anzeigen. Au�erdem wird anhand des gegebenen Wertes f�r cause angegeben,
     * ob das Spiel wieder aufnehmbar ist.
     * @param message   Die Nachricht, die von der UI angezeigt werden soll
     * @param cause     Die Ursache f�r die Pausierung
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
        
//      Der letzte Frame des Spiels wird gel�scht, indem noch ein letztes Mal gemalt wird,
//      obwohl inGame bereits auf false gesetzt wurde (-> es wird nichts ausgegeben;
//      siehe dazu SnakeUI#paint(Graphics))
        gui.repaint();
        
//      Und die UI soll wieder ihre Komponenten ausgeben
        gui.showUI(message, playingSnake.getScore());
    }
    
    /**
     * Diese Methode l�sst den User das Spiel wieder aufnehmen.
     * Dies ist NUR m�glich, wenn ein Spiel davor bereits gespielt wurde!
     */
    public void resumeGame(){
//      Zuerst lassen wir von der UI wieder das Spiel anzeigen
        gui.showGame();
        
//      Dann lassen wir angeben, dass wir uns wieder in einem Spiel befinden
        inGame = true;
        
//      Und starten den zeitsteuernden Thread wieder.
//      Die am Spiel beteiligten Objekte sind noch in ihrem vorherigen Zustand, und k�nnen
//      daher einfach dort weitermachen, wo sie aufgeh�rt haben.
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
        
        cheating = gui.shuldBeCheating();
        
//      Dann die Offsets berechnet, und gespeichert
//      F�r weitere Informationen dazu siehe SnakeControl#init()
        if(vsp > vze){
            gui.setOffSets((gui.getWidth() - sp * vze) / 2, (gui.getHeight() - ze * vze)/ 2 + 10);
            
//          Dann wird die Gr��e des Spielfelds gespeichert
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
            
//          Dann wird die Gr��e des Spielfelds gespeichert
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
//      Letzten Endes werden alle �brigen Werte gespeichert
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
     * er m�chte, dass die Schlange ihre Richtung �ndert.<br>
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
    public void generateFood(){
//      Wir ben�tigen eine x- und eine y-Koordinate f�r das Food-Objekt,
//      und au�erdem noch einen Z�hler
        int x = 0, y = 0, ctr = 0;
        
//      Wir wollen mindestens einmal einen zuf�lligen Wert f�r die Koordinaten zuweisen
        do{
            x = (int) (Math.random() * playGrid.getWidth());    //  dieser Wert liegt im Intervall [0 .. Breite]
            y = (int) (Math.random() * playGrid.getHeight());   //  dieser Wert liegt im Intervall [0 .. H�he]
//      dies tun wir so lange, wie die Schlange das Feld mit den generierten Koordinaten nicht mehr
//      �berdeckt, allerdings h�chstens 4000 Iterationen, damit sichergestellt wird, dass das Spiel
//      sich nicht aufh�ngt, sobald die Schlange das gesamte Feld bedeckt
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
     * @param args  Die von der Kommandozeile �bergebenen Argumente
     */
    public static void main(String[] args){
//      Wir erzeugen einfach ein neues SnakeControl-Objekt.
//      Dieses l�sst dann eine UI anzeigen, mit der man dann Snake spielen kann
        new SnakeControl();
    }

    /**
     * Diese Methode f�hrt einen Tick des Spiels aus.<br>
     * Dieser besteht daraus das Snake-Objekt um ein Feld zu bewegen, falls das Food-Objekt
     * gefressen wurde ein neues zu generieren, falls die Schlange stirbt das Spiel
     * abzubrechen, und die UI das aktualisierte Bild ausgeben zu lassen.<br>
     * Au�erdem wird, falls gecheatet wird je nach gegebenem Z�hlerstand evtl.
     * unten oder nach rechts gesteuert.<br>
     * Dies wird so gesteuert, dass die Schlange mindestens die gesamte Zeile durchl�uft
     * (sofern der User nicht die Schlange vorher umsteuert), allerdings niemals mehr
     * als zwei Zeilen komplett und nie mehr als drei Zeilen teilweise besetzt.
     * @param ctr   der Z�hlstand, wie oft die Schlange bisher bewegt wirde
     * @return      der aktualisierte Z�hlstand
     */
    public int onTick(int ctr){
//      Falls gecheatet wird
        if(cheating){
//          und der Z�hlstand auf 0 steht
            if(ctr == 0)
//              wird die Schlange nach unten gesteuert
                playingSnake.changeDirectionTo(down);
//          und der Z�hlstand auf 1 steht
            else if(ctr == 1)
//              wird die Schlange nach rechts gesteuert
                playingSnake.changeDirectionTo(right);
//          Danach wird der Z�hlstand erh�ht, und so geteilt, 
//              dass er mindestens eine Zeile durchl�uft, falls das 
//              Spielbrett breiter ist als die Schlange lang,
//              oder die Schlangenl�nge durchl�uft, falls diese l�nger
//              ist als die Spielfeldbreite
            ctr = (ctr + 1) % 
                    ((playGrid.getWidth() > playingSnake.getScore())? 
                            playGrid.getWidth(): 
                            playingSnake.getScore() + 1);
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
        
//      und geben schlie�lich den evtl. aktualisierten Z�hlstand zur�ck
        return ctr;
    }

    /**
     * Diese Klasse entspricht einer Zeitsteuerung f�r den Spielablauf.<br>
     * Dieser gibt, sobald ausgef�hrt, (sofern nich gecheatet wird) in einem
     * von der Teilfeldgr��e abh�ngigen zeitlichen Abstand eine Nachricht an das
     * SnakeControl-Objekt.<br>
     * Falls gecheatet wird wird dieser Thread so schnell es geht durchgef�hrt
     * (so gut es geht ohne zeitliche Verz�gerung)
     * @author Jean-Pierre Hotz
     */
    public class Timer implements Runnable{
        @Override
        public void run(){
//          der Z�hlstand, der nur f�r das Cheaten relevant ist
            int ctr = 0;
            
//          Eine Referenz auf den derzeitigen Thread, da ein geschleifter
//          Thread von seiner Referenz abh�ngig gemacht werden soll
            Thread currentThread = Thread.currentThread();
//          Abbruchbedingung ist, dass entweder das SnakeControl-Objekt sich nicht mehr
//          in einem Spiel befindet, oder die Referenz auf den Thread sich ge�ndert hat
            while(currentThread == playThread && inGame){
//              Es wird ein Tick ausgef�hrt und der aktualisierte Z�hlstand gespeichert
                ctr = onTick(ctr);
                
                try{
//                  Dann wird der Thread (falls nicht gecheatet wird) abh�ngig
//                  von der Gr��e eines Teilfeldes pausiert
                    Thread.sleep((cheating)? 0: (int) (((5 * playGrid.getSize()) / 3f) + (100f / 3f)));
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Diese Methode l�sst den zeitsteuernden Thread auf jeden Fall abbrechen, indem wir im eine
     * null-Referenz zuweisen
     * @see Timer
     */
    public void disposeThread(){
        playThread = null;
    }
}