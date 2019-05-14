import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MusicPlayer implements ActionListener, StdAudio.AudioEventListener {

    // instance variables
    private Song song;
    private boolean playing; // whether a song is currently playing
    private JFrame frame;
    private JFileChooser fileChooser;
    private JTextField tempoText;
    private JSlider currentTimeSlider;
    private JPanel panel;
    private ArrayList<Component> components;
    private JButton saveCoord, loadCoord;
    private boolean isMoving = false;

    //these are the two labels that indicate time
    // to the right of the slider
    private JLabel currentTimeLabel;
    private JLabel totalTimeLabel;

    //this the label that says 'welcome to the music player'
    private JLabel statusLabel;
    private JButton play, stop, pause, reverse, up, down, change, load;

    /*
     * Creates the music player GUI window and graphical components.
     */
    public MusicPlayer() {
        song = null;
        createComponents();
        doLayout();
        StdAudio.addAudioEventListener(this);
        frame.setVisible(true);

        //update();
    }

    /*
     * Called when the user interacts with graphical components, such as
     * clicking on a button.
     */
    public void actionPerformed(ActionEvent event) {

        String cmd = event.getActionCommand();
        if (isMoving == false) {
            if (cmd.equals("Play")) {
                panel.setBackground(Color.CYAN);
                currentTimeSlider.setBackground(Color.CYAN);
                (components.get(0)).setEnabled(false);
                components.get(1).setEnabled(true);
                components.get(7).setEnabled(true);
                components.get(3).setEnabled(true);
                components.get(4).setEnabled(true);
                components.get(5).setEnabled(true);
                components.get(6).setEnabled(true);
                components.get(2).setEnabled(false);
                playSong();

                console("Playing");
                //fill this
            } else if (cmd.equals("Pause/Resume")) {
                StdAudio.setPaused(!StdAudio.isPaused());
                console("Pausing");
            } else if (cmd == "Stop") {
                StdAudio.setMute(true);
                StdAudio.setPaused(false);
                components.get(1).setEnabled(false);
                components.get(0).setEnabled(true);
                components.get(6).setEnabled(true);
                components.get(2).setEnabled(true);
                console("Stopping");
            } else if (cmd == "Load") {
                try {
                    loadFile();
                    console("Loading");
                    for (int i = 0; i < components.size(); i++) {
                        if (components.get(i) instanceof JButton) {
                            JButton button = ((JButton) components.get(i));
                            if (button.getName().equals("Pause/Resume")) {
                                button.setEnabled(false);
                            } else {
                                button.setEnabled(true);
                            }
                        }

                    }
                } catch (IOException ioe) {
                    System.out.println("not able to load from the file");
                    console("Loading Failed");
                }

            } else if (song != null) {
                if (cmd == "Up") {
                    song.octaveUp();
                    console("Octave Up");
                    //TODO - fill this
                } else if (cmd == "Down") {
                    song.octaveDown();
                    console("Octave Down");
                    //TODO - fill this
                } else if (cmd == "Reverse") {
                    song.reverse();
                    console("Reversing");
                    //TODO - fill this
                } else if (cmd == "Change Tempo") {

                    console("TEMPO IS " + Double.parseDouble(tempoText.getText()));
                    song.changeTempo(Double.parseDouble(tempoText.getText()));

                    console("Changing Tempo");
                    //TODO - fill this
                }
            } else if (cmd == "Save Coord") {

                try {
                    saveScreenCoord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (cmd == "Load Coord") {
                loadScreenCoord();
            }
        }
    }

    public void saveScreenCoord() throws Exception {
        // The name of the file to open.
        String fileName = "componentCoordinates.txt";

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.


            for (int i = 0; i < components.size(); i++) {
                Component c = components.get(i);
                bufferedWriter.write(c.getX() + "," + c.getY() + "," + c.getWidth() + "," + c.getHeight());
                bufferedWriter.newLine();

            }

            // Always close files.
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }

    public void readFile(String fileName) throws Exception {
        Scanner kb = new Scanner(new File(fileName));
        while (kb.hasNextLine()) {
            console(kb.nextLine());
        }
    }


    public void loadScreenCoord() {
        // The name of the file to open.
        String fileName = "componentCoordinates.txt";

        try {
            int count = 0;
            Scanner kb = new Scanner(new File(fileName));
            while (kb.hasNext()) {
                String line = kb.nextLine();
                String[] coord = line.split(",");
                //console(line);
                //console(coord[0] + " " + coord[1] + " " + coord[2] + " " + coord[3]);

                int x = Integer.parseInt(coord[0]);
                int y = Integer.parseInt(coord[1]);
                int width = Integer.parseInt(coord[2]);
                int height = Integer.parseInt(coord[3]);
                //console(height+"");
                components.get(count).setBounds(x, y, width, height);
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void console(String msg) {
        System.out.println(msg);
    }

    /*
     * Called when audio events occur in the StdAudio library. We use this to
     * set the displayed current time in the slider.
     */
    public void onAudioEvent(StdAudio.AudioEvent event) {
        // update current time
        if (event.getType() == StdAudio.AudioEvent.Type.PLAY
                || event.getType() == StdAudio.AudioEvent.Type.STOP) {
            setCurrentTime(getCurrentTime() + event.getDuration());
        }
    }

    /*
     * Sets up the graphical components in the window and event listeners.
     */
    private void createComponents() {
        //TODO - create all your components here.
        // note that you should have already defined your components as instance variables.
        frame = new JFrame();
        frame.setTitle("Music Player");
        //frame.setSize(1000, 400);
        //frame.setLocation(100, 200);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }

            public void windowClosed(WindowEvent e) {

            }

            public void windowIconified(WindowEvent e) {

            }

            public void windowDeiconified(WindowEvent e) {

            }

            public void windowActivated(WindowEvent e) {

            }

            public void windowDeactivated(WindowEvent e) {

            }


        });

    }


    /*
     * Performs layout of the components within the graphical window.
     * Also make the window a certain size and put it in the center of the screen.
     */
    private void doLayout() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        frame.setSize(screenWidth / 2, screenHeight / 2);
        frame.setLocation(screenWidth / 4, screenHeight / 4);
        //TODO - figure out how to layout the components
        components = new ArrayList<Component>();
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        final Container contentPane = frame.getContentPane();
        panel = new JPanel();
        panel.setLayout(null);
        contentPane.add(panel);

        //start adding buttons
        play = new JButton("Play");
        play.setName("Play");
        play.setBounds(100, 100, 100, 50);
        panel.add(play);
        play.addActionListener(this);
        components.add(play);

        stop = new JButton("Stop");
        stop.setName("Stop");
        stop.setBounds(220, 100, 100, 50);
        panel.add(stop);
        stop.addActionListener(this);
        components.add(stop);

        load = new JButton("Load");
        load.setName("Load");
        load.setBounds(330, 100, 100, 50);
        panel.add(load);
        load.addActionListener(this);
        components.add(load);

        reverse = new JButton("Reverse");
        reverse.setName("Reverse");
        reverse.setBounds(100, 200, 100, 50);
        panel.add(reverse);
        reverse.addActionListener(this);
        components.add(reverse);

        up = new JButton("Up");
        up.setName("Up");
        up.setBounds(220, 200, 100, 50);
        panel.add(up);
        up.addActionListener(this);
        components.add(up);

        down = new JButton("Down");
        down.setName("Down");
        down.setBounds(330, 200, 100, 50);
        panel.add(down);
        down.addActionListener(this);
        components.add(down);

        change = new JButton("Change Tempo");
        change.setName("Change Tempo");
        change.setBounds(100, 300, 150, 50);
        panel.add(change);
        change.addActionListener(this);
        components.add(change);

        pause = new JButton("Pause/Resume");
        pause.setName("Pause/Resume");
        pause.setBounds(250, 300, 100, 50);
        panel.add(pause);
        pause.addActionListener(this);
        components.add(pause);

        saveCoord = new JButton("Save Coord");
        saveCoord.setName("Save Coord");
        saveCoord.setBounds(250, 350, 100, 50);
        panel.add(saveCoord);
        saveCoord.addActionListener(this);
        components.add(saveCoord);

        loadCoord = new JButton("Load Coord");
        loadCoord.setName("Load Coord");
        loadCoord.setBounds(250, 200, 100, 50);
        panel.add(loadCoord);
        loadCoord.addActionListener(this);
        components.add(loadCoord);

        //setup slider
        currentTimeSlider = new JSlider();
        currentTimeSlider.setName("Slider");
        //currentTimeSlider.setName("Slider");
        //currentTimeSlider.createStandardLabels(5, 0);
        currentTimeSlider.setMinorTickSpacing(1);
        currentTimeSlider.setMajorTickSpacing(5);
        currentTimeSlider.setPaintTicks(true);
        currentTimeSlider.setSnapToTicks(true);
        //currentTimeSlider.setPaintLabels(true);
        currentTimeSlider.setPaintTrack(true);
        //currentTimeSlider.setMinimum(0);
        //currentTimeSlider.setMaximum(60);
        //currentTimeSlider.setValue(0);
        currentTimeSlider.setLocation(200, 400);
        panel.add(currentTimeSlider, BorderLayout.SOUTH);
        currentTimeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!currentTimeSlider.getValueIsAdjusting()) {
                    console(currentTimeSlider.getValue() + "");
                }

            }
        });
        currentTimeSlider.setBounds(100, 30, 500, 25);

        //make status label
        statusLabel = new JLabel("Welcome to the Music Player!");
        statusLabel.setBounds(40, 55, 200, 25);
        statusLabel.setName("Status Label");
        panel.add(statusLabel);
        components.add(statusLabel);

        fileChooser = new JFileChooser();

        tempoText = new JTextField();
        tempoText.setText("Tempo");
        tempoText.setBounds(450, 250, 100, 50);
        tempoText.setName("Tempo");
        panel.add(tempoText);
        components.add(tempoText);

        currentTimeLabel = new JLabel();
        currentTimeLabel.setText("00.00 /");

        currentTimeLabel.setName("Current Time");
        currentTimeLabel.setBounds(450, 300, 100, 100);
        panel.add(currentTimeLabel);
        components.add(currentTimeLabel);

        totalTimeLabel = new JLabel();
        totalTimeLabel.setText("00.00");
        totalTimeLabel.setName("Total Time");
        totalTimeLabel.setBounds(500, 300, 100, 100);
        panel.add(totalTimeLabel);
        components.add(totalTimeLabel);

        for (int i = 0; i < components.size(); i++) {
            components.get(i).addMouseListener(myMouseAdapter);
            components.get(i).addMouseMotionListener(myMouseAdapter);
            if (components.get(i) instanceof JButton) {
                JButton button = ((JButton) components.get(i));
                button.setFocusPainted(false);
                if (i < 6) {
                    if (button.getName().equals("Load")) {
                        button.setEnabled(true);
                    } else {
                        button.setEnabled(false);
                    }
                } else if (button.getName().equals("Pause/Resume")) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }

        }

        loadScreenCoord();

    }

    /*
     * Returns the estimated current time within the overall song, in seconds.
     */
    private double getCurrentTime() {
        String timeStr = currentTimeLabel.getText();
        timeStr = timeStr.replace(" /", "");
        try {
            return Double.parseDouble(timeStr);
        } catch (NumberFormatException nfe) {
            return 0.0;
        }
    }

    /*
     * Sets the current time display slider/label to show the given time in
     * seconds. Bounded to the song's total duration as reported by the song.
     */
    private void setCurrentTime(double time) {
        double total = song.getTotalDuration();
        time = Math.max(0, Math.min(total, time));
        currentTimeLabel.setText(String.format("%08.2f /", time));
        totalTimeLabel.setText(total + "");
        currentTimeSlider.setValue((int) (100 * time / total));
    }

    /*
     * Pops up a file-choosing window for the user to select a song file to be
     * loaded. If the user chooses a file, a Song object is created and used
     * to represent that song.
     */
    private void loadFile() throws IOException {
        if (fileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selected = fileChooser.getSelectedFile();
        if (selected == null) {
            return;
        }
        statusLabel.setText("Current song: " + selected.getName());
        String filename = selected.getAbsolutePath();
        System.out.println("Loading song from " + selected.getName() + " ...");

        //TODO - create a song from the file
        song = new Song(filename);
        tempoText.setText("1.0");
        setCurrentTime(0.0);
        //updateTotalTime();
        System.out.println("Loading complete.");
        System.out.println("Song: " + song);

        //currentTimeSlider.setMaximum((int)song.getTotalDuration()*100);
        //currentTimeSlider.setMajorTickSpacing(currentTimeSlider.getMaximum()/10);
        //currentTimeSlider.setMinorTickSpacing(5);

    }

    /*
     * Initiates the playing of the current song in a separate thread (so
     * that it does not lock up the GUI).
     * You do not need to change this method.
     * It will not compile until you make your Song class.
     */
    private void playSong() {
        if (song != null) {
            setCurrentTime(0.0);
            Thread playThread = new Thread(new Runnable() {
                public void run() {
                    StdAudio.setMute(false);
                    playing = true;

                    String title = song.getTitle();
                    String artist = song.getArtist();
                    double duration = song.getTotalDuration();
                    System.out.println("Playing \"" + title + "\", by "
                            + artist + " (" + duration + " sec)");
                    song.play();
                    System.out.println("Playing complete.");
                    components.get(7).setEnabled(false);
                    components.get(0).setEnabled(true);
                    components.get(1).setEnabled(false);
                    for (int i = 0; i < components.size(); i++) {
                        if (components.get(i) instanceof JButton) {
                            JButton button = ((JButton) components.get(i));

                            if (button.getName().equals("Load")) {
                                button.setEnabled(true);
                            } else if (button.getName().equals("Load Coord")) {
                                button.setEnabled(true);
                            } else if (button.getName().equals("Save Coord")) {
                                button.setEnabled(true);
                            } else {
                                button.setEnabled(false);
                            }

                        }

                    }
                    components.get(6).setEnabled(true);
                    components.get(0).setEnabled(true);
                    components.get(2).setEnabled(true);
                    playing = false;

                }
            });
            playThread.start();
        }
    }

    class MyMouseAdapter extends MouseAdapter {

        private Point initialLoc;
        private Point initialLocOnScreen;

        @Override
        public void mousePressed(MouseEvent e) {

            Component comp = (Component) e.getSource();
            initialLoc = comp.getLocation();
            initialLocOnScreen = e.getLocationOnScreen();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isMoving = false;
            Component comp = (Component) e.getSource();
            Point locOnScreen = e.getLocationOnScreen();

            int x = locOnScreen.x - initialLocOnScreen.x + initialLoc.x;
            int y = locOnScreen.y - initialLocOnScreen.y + initialLoc.y;
            comp.setLocation(x, y);
            String cmd = comp.getName();
            console(cmd);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            isMoving = true;
            Component comp = (Component) e.getSource();
            Point locOnScreen = e.getLocationOnScreen();

            int x = locOnScreen.x - initialLocOnScreen.x + initialLoc.x;
            int y = locOnScreen.y - initialLocOnScreen.y + initialLoc.y;
            comp.setLocation(x, y);
        }
    }

}