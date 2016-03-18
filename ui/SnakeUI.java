/*
 * Diese Klasse ist von Jean-Pierre Hotz geschrieben worden, und gehört zu einem
 * CT-Projekt, in dem einige Eigenschaften von Enumerations und Exceptions 
 * herausgearbeitet werden sollen.
 */
package de.jeanpierrehotz.ui;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import de.jeanpierrehotz.control.SnakeControl;

import static de.jeanpierrehotz.snake.parts.Snake.Direction.up;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.down;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.left;
import static de.jeanpierrehotz.snake.parts.Snake.Direction.right;

import static de.jeanpierrehotz.control.SnakeControl.CauseOfPause.UICause;

/**
 * Diese Klasse repräsentiert die GUI eines Snake-Spiels.<br>
 * Diese gibt die Möglichkeit über Choices die Spielfeldgröße einzustellen,
 * einzustellen, ob das Spielfeld gezeichnet werden soll, ob die Schlange sich 
 * an Regeln halten soll, und ob die Snake wieder am anderen Ende des Spielfelds
 * auftauchen soll.<br>
 * Außerdem bietet sie eine Möglichkeit das Spiel zu starten, und (falls möglich)
 * das vorherige Spiel wieder aufzunehmen.<br>
 * Falls das vorherige Spiel mit den neu eingegebenen Optionen aufgenommen werden soll,
 * so erscheint zuerst eine Warnung, die wieder verschwindet, sobald eine andere Komponente
 * fokusiert wird.
 * In der unteren linken Ecke ist außerdem noch eine unbeschriftete Checkbox, die den
 * User in dem Spiel "cheaten" lässt, indem das Spiel von alleine "spielt"
 * @author Jean-Pierre Hotz
 */
public class SnakeUI extends Frame{
    /**
     * Dieses Label repräsentiert die Überschrift des Spiels
     */
    private Label captionLabel;
    /**
     * Dieses Label ist die Beschriftung für die Choice für die Spalten
     */
    private Label columnsLabel;
    /**
     * Dieses Label ist die Beschriftung für die Choice für die Zeilen
     */
    private Label rowsLabel;
    
    /**
     * Dieses Label zeigt eine Nachricht für den User an
     */
    private Label messageLabel;
    
    /**
     * Dieses Label zeigt den Score des letzten Spiels für den User an
     */
    private Label lastScoreLabel;
    
    /**
     * Dieses Choice lässt den User die Anzahl an Spalten im Spielfeld einstellen
     */
    private Choice columnsChoice;
    
    /**
     * Diese Methode gibt ihnen die derzeitig eingestellte Anzahl an Spalten
     * @return  Die derzeitig eingestellte Anzahl an Spalten
     */
    public int getColumns(){
        return Integer.parseInt(columnsChoice.getSelectedItem());
    }
    
    /**
     * Dieses Choice lässt den User die Anzahl an Zeilen im Spielfeld einstellen
     */
    private Choice rowsChoice;
    
    /**
     * Diese Methode gibt ihnen die derzeitig eingestellte Anzahl an Zeilen
     * @return  Die derzeitig eingestellte Anzahl an Zeilen
     */
    public int getRows(){
        return Integer.parseInt(rowsChoice.getSelectedItem());
    }
    
    /**
     * Diese Checkbox lässt den User einstellen, ob die Schlange am anderen
     * Ende des Spielfelds wieder auftauchen soll
     */
    private Checkbox infiniteCheckbox;
    
    /**
     * Diese Methode gibt ihnen an, ob die Schlange wieder am anderen Ende des Spielfeld
     * auftauchen soll, oder nicht
     * @return  ob das Spielfeld unendlich sein soll, oder nicht
     */
    public boolean shouldBeInfinite(){
        return infiniteCheckbox.getState();
    }
    
    /**
     * Diese Checkbox lässt den User einstellen, ob die Schlange sich an Regeln halten soll
     */
    private Checkbox withRulesCheckbox;
    
    /**
     * Diese Methode gibt ihnen, ob die Schlange sich an Regeln halten sollte
     * @return  ob die Schlange sich an Regeln halten soll
     */
    public boolean shouldBeWithRules(){
        return withRulesCheckbox.getState();
    }
    
    /**
     * Diese Checkbox gibt dem User die Möglichkeit einzustellen, ob das Spielfeld
     * gezeichnet werden soll.
     */
    private Checkbox drawGridCheckbox;
    
    /**
     * Diese Methode gibt ihnen, ob das Spielfeld gezeichnet werden soll
     * @return  ob das Spielfeld gezeichnet werde soll
     */
    public boolean shouldDrawGrid(){
        return drawGridCheckbox.getState();
    }
    
    /**
     * Diese Checkbox gibt dem User die Möglichkeit einzustellen, ob er "cheaten" möchte
     */
    private Checkbox cheatingCheckbox;
    
    /**
     * Diese Methode zeigt ihnen, ob der User cheaten möchte
     * @return  ob gecheatet werden soll
     */
    public boolean shuldBeCheating(){
        return cheatingCheckbox.getState();
    }
    
    /**
     * Dieser Button lässt den User das Spiel starten
     */
    private Button playBtn;
    
    /**
     * Dieser ActionListener beginnt das Spiel, sobald der User den entspr. Button drückt
     */
    private ActionListener playListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent ev){
            control.init();
        }
    };
    
    /**
     * Dieser Button lässt den User das vorherige Spiel wieder aufnehmen
     */
    private Button resumeBtn;
    
    /**
     * Dieser ActionListener nimmt das Spiel wieder auf, sobald der entspr. Button gedrückt wurde
     */
    private ActionListener resumeActionListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent ev){
            control.resumeGame();
        }
    };
    
    /**
     * Diese Methode stellt ein, ob das vorherige Spiel wiederaufnehmbar ist.<br>
     * Dies ist nur der Fall, wenn das Spiel seitens der UI pausiert wird
     * @param resumable ob das Spiel wiederaufnehmbar ist
     */
    public void setResumable(boolean resumable){
        resumeBtn.setEnabled(resumable);
        saveAndResumeBtn.setEnabled(resumable);
    }
    
    /**
     * Dieser Button lässt den User die Optionen speichern, und das vorherige
     * Spiel wieder aufnehmen
     */
    private Button saveAndResumeBtn;
    
    /**
     * Diese Label sind dazu da, dem User eine Warnung anzuzeigen, sobald er
     * versucht die Optionen zu speichern und das Spiel wieder auf zu nehmen
     */
    private Label warningLabel_1, warningLabel_2, warningLabel_3;
    
    /**
     * Diese Stringkonstanten sind die Warnung, die angezeigt wird, sobald der User
     * versucht die Optionen zu speichern und das vorherige Spiel wieder auf zu nehmen.<br>
     * Diese ist auf drei Strings aufgeteilt, da die Klasse {@link Label} keinen
     * mehrzeiligen Text anzeigen kann. (Dieser wird einfach in einer Zeile angezeigt)
     */
    private static final String WARNING_1 = "Your Snake could immediately die if you save the options!",
                                WARNING_2 = "Are you sure you want to proceed?",
                                WARNING_3 = "If so just click \"Save and resume\" again.";
    
    /**
     * Diese Variable zeigt an, ob der Button zum Speichern der Optionen und
     * Wiederaufnehmen des vorherigen Spiels bereits geklickt wurde, und seinen
     * Fokus nicht verloren hat.<br>
     * Diese wird benutzt, damit wir vor dem eigentlichen Speichern und Wiederaufnehmen
     * die Warnung ausgeben können
     */
    private boolean saveAndResumeAlreadyClicked;
    
    /**
     * Dieser FocusListener löscht die Warnung, sobald der Button zum Speichern der Optionen und
     * Wiederaufnehmen des vorherigen Spiels seinen Fokus verloren hat, und setzt die Variable
     * {@link SnakeUI#saveAndResumeAlreadyClicked} auf {@code false}.<br>
     * Damit wird die Warnung auch immer gelöscht, sobald man ein Spiel startet.
     */
    FocusListener resumeAndSaveFocusListener = new FocusListener(){
        @Override
        public void focusGained(FocusEvent ev){}
        @Override
        public void focusLost(FocusEvent ev){
            saveAndResumeAlreadyClicked = false;
            warningLabel_1.setText("");
            warningLabel_2.setText("");
            warningLabel_3.setText("");
        }
    };
    
    /**
     * Dieser ActionListener schaut, ob der Button zum Speichern der Optionen und
     * Wiederaufnehmen des vorherigen Spiels bereits geklickt wurde.<br>
     * Falls dies der Fall ist, so wird die Variable {@link SnakeUI#saveAndResumeAlreadyClicked}
     * auf {@code false} gesetzt, und die Optionen werden gespeichert und das vorherige
     * Spiel wieder aufgenommen<br>
     * Falls nicht wird {@link SnakeUI#saveAndResumeAlreadyClicked} auf {@code true} gesetzt,
     * und die Warnung angezeigt
     */
    ActionListener saveAndResumeActionListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent ev) {
            if(saveAndResumeAlreadyClicked){
                saveAndResumeAlreadyClicked = false;
                control.saveOptionsAndResumeGame();
            }else{
                saveAndResumeAlreadyClicked = true;
                warningLabel_1.setText(WARNING_1);
                warningLabel_2.setText(WARNING_2);
                warningLabel_3.setText(WARNING_3);
            }
        }
    };
    
    /**
     * Dieses SnakeControl-Objekt wird benutzt, um Nachrichten zwischen der UI und
     * der Steuerung der Steuerung auszutauschen
     */
    SnakeControl control;
    
    /**
     * Dieser WindowListener lässt, falls noch in einem Spiel ist, das Spiel unterbrechen,
     * oder, falls wir nicht in einem Spiel sind, das Programm beenden.
     */
    private WindowListener windowClosingListener = new WindowAdapter(){
        @Override
        public void windowClosing(WindowEvent ev){
//          Falls wir in einem Spiel sind
            if(control.isInGame()){
//              Lassen wir das Spiel pausieren mit der Nachricht "Are you sure you want to exit the game?"
                control.pauseGame("Are you sure you want to exit the game?", UICause);
//          Ansonsten, falls wir nicht mehr in einem Spiel sind
            }else{
//              Beenden wir alle Looper, was alle Listener und alle Threads betrifft
                playBtn.removeActionListener(playListener);
                
                resumeBtn.removeActionListener(resumeActionListener);
                
                saveAndResumeBtn.removeFocusListener(resumeAndSaveFocusListener);
                saveAndResumeBtn.removeActionListener(saveAndResumeActionListener);
                
                removeWindowListener(this);
                
                removeKeyListener(steuerungsListener);
                
                control.disposeThread();
                
//              Und beenden dann das Programm
                System.exit(0);
            }
        }
    };
    
    /**
     * Dieser KeyListener lässt den User das Spiel steuern
     */
    private KeyListener steuerungsListener = new KeyAdapter(){
        @Override
        public void keyPressed(KeyEvent ev){
//          Falls wir derzeitig in einem Spiel sind
            if(control.isInGame()){
//              Entscheiden wir nach dem KeyCode des KeyEvents
                switch(ev.getKeyCode()){
//                  Falls "W" oder "Pfeiltaste nach oben" gedrückt wurde
                    case KeyEvent.VK_W: case KeyEvent.VK_UP:
//                      Lassen wir die Schlange nach oben gehen
                        control.onDirectionChanging(up);
                        return;
//                  Falls "S" oder "Pfeiltaste nach unten" gedrückt wurde
                    case KeyEvent.VK_S: case KeyEvent.VK_DOWN:
//                      Lassen wir die Schlange nach unten gehen
                        control.onDirectionChanging(down);
                        return;
//                  Falls "A" oder "Pfeiltaste nach links" gedrückt wurde
                    case KeyEvent.VK_A: case KeyEvent.VK_LEFT:
//                      Lassen wir die Schlange nach inks gehen
                        control.onDirectionChanging(left);
                        return;
//                  Falls "D" oder "Pfeiltaste nach rechts" gedrückt wurde
                    case KeyEvent.VK_D: case KeyEvent.VK_RIGHT:
//                      Lassen wir die Schlange nach rechts gehen
                        control.onDirectionChanging(right);
                        return;
//                  Falls "Escape" gedrückt wurde
                    case KeyEvent.VK_ESCAPE:
//                      Pausieren wir das Spiel
                        control.pauseGame("You hit escape!", UICause);
                        return;
                }
            }
        }
    };

    /**
     * Dieser Konstruktor erstellt ein SnakeUI-Objekt mit einem gegebenen SnakeControl-Objekt,
     * mit dem ein Snake-Spiel gesteuert wird.<br>
     * Dieses SnakeUI wird auf die Größe 1600 * 900 px gesetzt und angezeigt
     * @param control
     */
    public SnakeUI(SnakeControl control){
        super("Snake - CT-Projekt 2016 Jean-Pierre Hotz");
        
//      Die Referenz auf das SnakeControl-Objekt wird kopiert
        this.control = control;
        
//      Wir setzen den Layoutmanager auf null, damit die Component#setBounds([...])-Aufrufe
//      ihre Gültigkeit erhalten
        setLayout(null);
        
//      fügen die Listener hinzu
        addWindowListener(windowClosingListener);
        addKeyListener(steuerungsListener);
        
//      
//      Und initialisieren dann das Layout:
//      (Positionierung wird erst später gemacht)
//      
        captionLabel = new Label("Snake");
        captionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
        captionLabel.setAlignment(Label.CENTER);
        add(captionLabel);

        messageLabel = new Label();
        messageLabel.setAlignment(Label.CENTER);
        messageLabel.setText("Spielbeginn bei Klick auf \"Spielen\"!");
        add(messageLabel);

        columnsChoice = new Choice();
        for(int i = 16; i < 1570; i++){
            columnsChoice.add("" + i);
        }
        add(columnsChoice);

        columnsLabel = new Label("Spalten: ");
        add(columnsLabel);
        
        rowsLabel = new Label("Zeilen:");
        add(rowsLabel);
        
        rowsChoice = new Choice();
        for(int i = 9; i < 870; i++){
            rowsChoice.add("" + i);
        }
        add(rowsChoice);
        
        infiniteCheckbox = new Checkbox("Snake soll wieder am anderen Ende auftauchen?");
        infiniteCheckbox.setState(false);
        add(infiniteCheckbox);
        
        withRulesCheckbox = new Checkbox("Regeln sind angeschalten?");
        withRulesCheckbox.setState(true);
        add(withRulesCheckbox);
        
        drawGridCheckbox = new Checkbox("Soll das Gitternetz des Spielfelds gezeichnet werden?");
        drawGridCheckbox.setState(true);
        add(drawGridCheckbox);
        
        playBtn = new Button("Spielen");
        playBtn.addActionListener(playListener);
        add(playBtn);
        
        lastScoreLabel = new Label("");
        lastScoreLabel.setAlignment(Label.CENTER);
        add(lastScoreLabel);

        cheatingCheckbox = new Checkbox("");
        add(cheatingCheckbox);
        
        resumeBtn = new Button("Resume");
        resumeBtn.addActionListener(resumeActionListener);
        resumeBtn.setEnabled(false);
        add(resumeBtn);
        
        saveAndResumeBtn = new Button("Save and resume");
        saveAndResumeBtn.setEnabled(false);
        saveAndResumeBtn.addFocusListener(resumeAndSaveFocusListener);
        saveAndResumeBtn.addActionListener(saveAndResumeActionListener);
        add(saveAndResumeBtn);
        
        warningLabel_1 = new Label();
        warningLabel_1.setAlignment(Label.CENTER);
        add(warningLabel_1);
        
        warningLabel_2 = new Label();
        warningLabel_2.setAlignment(Label.CENTER);
        add(warningLabel_2);

        warningLabel_3 = new Label();
        warningLabel_3.setAlignment(Label.CENTER);
        add(warningLabel_3);

//      Dann setzen wir die Größe dieses Frames auf 1600 * 900 px
        setBounds(0, 40, 1600, 900);
//      Lassen den User den Frame nicht vergrößern oder verkleinern
        setResizable(false);
//      Und zeigen den Frame an
        setVisible(true);
        
//        
//      Schlussendlich setzen wir die Grenzen der einzelnen Komponenten so, dass sie
//      in dem Frame zentriert sind.
//      Dies kann erst hier geschehen, da die tatsächliche Breite / Höhe des Fensters
//      erst von #getWidth() oder #getHeight() zurückgegeben wird, sobald der Frame sichtbar ist
//        
        captionLabel.setBounds(20, 50, getWidth() - 40, 40);
        messageLabel.setBounds(20, 100, getWidth() - 40, 20);        
        columnsChoice.setBounds(getWidth() / 2 - 150, 130, 130, 20);
        columnsLabel.setBounds(getWidth() / 2 - 220, 130, 70, 20);
        rowsLabel.setBounds(getWidth() / 2 + 20, 130, 70, 20);
        rowsChoice.setBounds(getWidth() / 2 + 90, 130, 130, 20);
        infiniteCheckbox.setBounds(getWidth() / 2 - 150, 170, 800, 20);
        withRulesCheckbox.setBounds(getWidth() / 2 - 150, 200, 800, 20);
        drawGridCheckbox.setBounds(getWidth() / 2 - 150, 230, 800, 20);
        playBtn.setBounds(getWidth() / 2 - 100, 300, 200, 40);
        lastScoreLabel.setBounds(getWidth() / 2 - 100, 350, 200, 20);
        cheatingCheckbox.setBounds(20, getHeight() - 40, 40, 40);
        resumeBtn.setBounds(getWidth() / 2 - 60, 390, 120, 30);
        saveAndResumeBtn.setBounds(getWidth() / 2 - 100, 430, 200, 40);
        warningLabel_1.setBounds(20, 480, getWidth() - 40, 20);
        warningLabel_2.setBounds(20, 500, getWidth() - 40, 20);
        warningLabel_3.setBounds(20, 520, getWidth() - 40, 20);
    }

    /**
     * Diese Methode lässt die UI das Spiel anzeigen
     */
    public void showGame(){
//      Dazu muss das Fenster den Fokus haben, damit der KeyListener Events erhält
        requestFocus();
        
//      Und die Komponenten auf der UI werden nicht mehr angezeigt
        captionLabel.setVisible(false);
        columnsLabel.setVisible(false);
        rowsLabel.setVisible(false);
        messageLabel.setVisible(false);
        lastScoreLabel.setVisible(false);
        columnsChoice.setVisible(false);
        rowsChoice.setVisible(false);
        infiniteCheckbox.setVisible(false);
        withRulesCheckbox.setVisible(false);
        drawGridCheckbox.setVisible(false);
        cheatingCheckbox.setVisible(false);
        playBtn.setVisible(false);
        resumeBtn.setVisible(false);
        saveAndResumeBtn.setVisible(false);
        warningLabel_1.setVisible(false);
        warningLabel_2.setVisible(false);
        warningLabel_3.setVisible(false);
    }
    
    /**
     * Diese Methode zeigt nach einem Spiel wieder die UI mit einer 
     * gegebenen Nachricht und einem gegebenen Score an
     * @param abortMessage  Die Nachricht, die angezeigt werden soll
     * @param score         Der Score des letzten Spiels, der ebenfalls angezeigt wird
     */
    public void showUI(String abortMessage, int score){
//      Wir zeigen alle Komponenten der UI wieder an
        captionLabel.setVisible(true);
        columnsLabel.setVisible(true);
        rowsLabel.setVisible(true);
        messageLabel.setVisible(true);
        lastScoreLabel.setVisible(true);
        columnsChoice.setVisible(true);
        rowsChoice.setVisible(true);
        infiniteCheckbox.setVisible(true);
        withRulesCheckbox.setVisible(true);
        drawGridCheckbox.setVisible(true);
        cheatingCheckbox.setVisible(true);
        playBtn.setVisible(true);
        resumeBtn.setVisible(true);
        saveAndResumeBtn.setVisible(true);
        warningLabel_1.setVisible(true);
        warningLabel_2.setVisible(true);
        warningLabel_3.setVisible(true);
        
//      Zeigen die Nachricht
        messageLabel.setText(abortMessage);
//      und den Score an
        lastScoreLabel.setText("Last Score: " + score);
    }
    
    /**
     * Diese Variablen repräsentieren die Offsets in x- und y-Richtung des Spielfelds,
     * damit dieses zentriert ist
     */
    private int xOff, yOff;
    
    /**
     * Diese Methode stellt ein, wie groß die Offsets sein müssen, damit das Spielfeld
     * zentriert ist
     * @param x     Das x-Offset
     * @param y     Das y-Offset
     */
    public void setOffSets(int x, int y){
        xOff = x;
        yOff = y;
    }
    
    /**
     * Dieses Image-Objekt wird für das Double-Buffern benutzt.<br>
     * Das bedeutet, dass man zuerst auf dieses Bild zeichnen lässt, ohne es dem
     * User anzuzeigen, und gibt dann dieses Image-Objekt auf einmal aus.<br>
     * Dadurch verhindert man Flackern, das durch die einzeln (zeitlich versetzt)
     * ausgeführten Befehle verursacht wird, bei schnellen Aktualisierungsraten.
     */
    private Image dbImage;
    /**
     * Mit diesem Graphics-Objekt zeichnen wir auf das Image-Objekt,
     * das dem Double-Buffern dient.<br>
     * Dadurch dient dieses Objekt ebenfalls ausschließlich dem Double-Buffern
     */
    private Graphics dbg;

    /**
     * Diese Methode dient der Ausgabe des Spiels
     */
    @Override
    public void paint(Graphics g){
//      Falls wir in einem Spiel sind
        if(control.isInGame())
//          Sagen wir dem Steuerungsobjekt, dass es mit g und den gegebenen Offsets
//          das Spiel ausgeben soll
            control.paintEverything(xOff, yOff, g);
    }

    /**
     * Diese Methode dient dem Double-Buffern der Ausgabe
     */
    @Override
    public void update(Graphics g){
//      Falls das Image-Objekt zum Double-Buffern noch nicht erzeugt wurde
        if(dbImage == null){
//          Erzeugen wir eins mit den Abmessungen des Frames
            dbImage = createImage(getWidth(), getHeight());
//          Und weisen dem Graphics-Objekt die Graphics des Image-Objekts zu
            dbg = dbImage.getGraphics();
        }
//      Dann löschen wir das Image in der Hintergrundfarbe
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, getWidth(), getHeight());
        
//      Und malen dann (auf dem Image-Objekt) in der Vordergrundfarbe
        dbg.setColor(getForeground());
        paint(dbg);
        
//      Schlussendlich geben wir das Image-Objekt auf den Koordinaten (0|0) aus
        g.drawImage(dbImage, 0, 0, this);
    }
}
