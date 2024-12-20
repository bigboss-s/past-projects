import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JButton;
import java.util.LinkedHashSet;
import java.util.Set;

public class GameOfLife {
    public static final int ROWS = 100;
    public static final int COLS = 100;
    static List<Integer> aliveNeighbours = new LinkedList<Integer>();
    static List<Integer> deadNeighbours = new LinkedList<Integer>();
    public static JFrame frame;
    private static Timer timer;
    private static TimerTask task;
    private static int currentDelay = 500;
    public static boolean isRunning = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Game Of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new FlowLayout());
        frame.getContentPane().setBackground(Color.BLACK);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel cellWrapPanel = new JPanel();
        cellWrapPanel.setLayout(new FlowLayout());
        cellWrapPanel.setBackground(Color.BLACK);
        CellButtonGridPanel cellButtonGridPanel = new CellButtonGridPanel();
        cellButtonGridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        cellWrapPanel.add(cellButtonGridPanel);
        mainPanel.add(cellWrapPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(10, 0, 10, 10));
        leftPanel.setBackground(Color.BLACK);

        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));
        rulesPanel.setBackground(Color.BLACK);

        JLabel rulesLabel = new JLabel("RULES:");

        rulesLabel.setBackground(Color.BLACK);
        rulesLabel.setForeground(Color.WHITE);
        rulesLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        rulesLabel.setOpaque(true);
        rulesPanel.add(rulesLabel);

        JTextField aliveField = createFormattedTextField("23", Color.GREEN);
        rulesPanel.add(aliveField);

        JLabel divider = new JLabel("/");
        divider.setForeground(Color.WHITE);
        rulesPanel.add(divider);

        JTextField deadField = createFormattedTextField("3", Color.RED);
        rulesPanel.add(deadField);

        JButton startButton = createFormattedButton("START");
        JButton pauseButton = createFormattedButton("PAUSE");
        JButton stopButton = createFormattedButton("STOP");
        JButton clearButton = createFormattedButton("CLEAR");

        startButton.addActionListener(e -> {
            updateNeighbourRules(aliveNeighbours, aliveField);
            updateNeighbourRules(deadNeighbours, deadField);
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    cellButtonGridPanel.nextStep();
                    Toolkit.getDefaultToolkit().sync();
                }
            };
            startButton.setEnabled(false);
            cellButtonGridPanel.createCopy();
            timer.scheduleAtFixedRate(task, 0, currentDelay);
            stopButton.setEnabled(true);
            isRunning = true;
        });

        pauseButton.addActionListener(e -> {
            if (timer != null) {
                timer.cancel();
                task.cancel();
            }
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            isRunning = false;
        });

        clearButton.addActionListener(e -> {
            if (timer != null) {
                timer.cancel();
                task.cancel();
            }
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            cellButtonGridPanel.clearCells();
            isRunning = false;
        });

        stopButton.addActionListener(e -> {
            timer.cancel();
            task.cancel();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            cellButtonGridPanel.restoreCopy();
            cellButtonGridPanel.revalidate();
            cellButtonGridPanel.repaint();
            isRunning = false;
        });
        stopButton.setEnabled(false);

        JPanel delayPanel = new JPanel();
        delayPanel.setBackground(Color.BLACK);
        delayPanel.setLayout(new GridLayout(0, 3));

        JLabel delayLabel = new JLabel("<html>STEP<br>DELAY</html>");
        delayLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        delayLabel.setForeground(Color.WHITE);

        JButton delayUpButton = createFormattedButton(" + ");
        JButton delayDownButton = createFormattedButton(" - ");

        JLabel currentDelayLabel = new JLabel("CURRENT: " + currentDelay);
        currentDelayLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        currentDelayLabel.setForeground(Color.WHITE);
        currentDelayLabel.setVerticalAlignment(SwingConstants.TOP);
        currentDelayLabel.setHorizontalAlignment(SwingConstants.LEFT);

        delayUpButton.addActionListener(e -> {
            if (currentDelay == 1000) return;
            currentDelay += 100;
            currentDelayLabel.setText("CURRENT: " + currentDelay);
            if (isRunning) restartTimer(currentDelayLabel, cellButtonGridPanel);
        });

        delayDownButton.addActionListener(e -> {
            if (currentDelay == 100) return;
            currentDelay -= 100;
            currentDelayLabel.setText("CURRENT: " + currentDelay);
            if (isRunning) restartTimer(currentDelayLabel, cellButtonGridPanel);
        });

        delayPanel.add(delayLabel);
        delayPanel.add(delayUpButton);
        delayPanel.add(delayDownButton);

        leftPanel.add(rulesPanel);
        leftPanel.add(startButton);
        leftPanel.add(pauseButton);
        leftPanel.add(clearButton);
        leftPanel.add(stopButton);
        leftPanel.add(delayPanel);
        leftPanel.add(currentDelayLabel);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        frame.add(mainPanel);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JTextField createFormattedTextField(String text, Color textColor) {
        JTextField textField = new JTextField(text, 5);
        textField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        textField.setBackground(Color.BLACK);
        textField.setForeground(textColor);
        textField.setCaretColor(Color.WHITE);
        return textField;
    }

    private static JButton createFormattedButton(String text) {
        JButton jButton = new JButton(text);
        jButton.setBackground(Color.BLACK);
        jButton.setForeground(Color.WHITE);
        jButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1), BorderFactory.createEmptyBorder()));
        jButton.setFocusPainted(false);
        jButton.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        jButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (jButton.isEnabled()) {
                    jButton.setBackground(Color.DARK_GRAY);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jButton.setBackground(Color.BLACK);
            }

        });
        return jButton;
    }

    private static void updateNeighbourRules(List<Integer> neighbourList, JTextField sourceField) {
        neighbourList.clear();
        sourceField.setBackground(Color.BLACK);
        for (int i = 0; i < sourceField.getText().length(); i++) {
            try {
                neighbourList.add(Integer.parseInt(String.valueOf(sourceField.getText().charAt(i))));
            } catch (NumberFormatException numberFormatException) {
                sourceField.setBackground(Color.RED);
            }
        }
    }

    private static void restartTimer(JLabel speedLabel, CellButtonGridPanel cellPanel) {
        if (timer != null) {
            timer.cancel();
            task.cancel();
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    cellPanel.nextStep();
                }
            };
            timer.scheduleAtFixedRate(task, 0, currentDelay);
        }
    }
}
class CellButtonGridPanel extends JPanel {
    private static Cell[][] cells;
    private static boolean[][] savedStatus;
    private boolean mousePressed = false;
    private boolean clickedCellState = false;
    MouseAdapter mouseAdapter;

    public CellButtonGridPanel() {
        mouseAdapter  = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (GameOfLife.isRunning) return;
                mousePressed = true;
                Cell cell = (Cell) e.getSource();
                cell.toggleSelectedState();
                clickedCellState = !cell.isSelected();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (GameOfLife.isRunning) return;
                mousePressed = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (GameOfLife.isRunning) return;
                Cell cell = (Cell) e.getSource();
                if (mousePressed && cell.isSelected() == clickedCellState) {
                    cell.toggleSelectedState();
                } else {
                    cell.setHoverState();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (GameOfLife.isRunning) return;
                Cell cell = (Cell) e.getSource();
                cell.setDefaultState();
            }
        };
        setLayout(new GridLayout(GameOfLife.ROWS, GameOfLife.COLS));
        cells = new Cell[GameOfLife.ROWS][GameOfLife.COLS];
        savedStatus = new boolean[GameOfLife.ROWS][GameOfLife.COLS];
        initializeButtons();
        setButtonBorders();
    }

    private void setButtonBorders() {
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border gridLineBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
        Border compoundBorder = BorderFactory.createCompoundBorder(gridLineBorder, paddingBorder);

        for (int row = 0; row < GameOfLife.ROWS; row++) {
            for (int col = 0; col < GameOfLife.COLS; col++) {
                cells[row][col].setBorder(compoundBorder);
            }
        }
    }

    private void initializeButtons() {
        for (int row = 0; row < GameOfLife.ROWS; row++) {
            for (int col = 0; col < GameOfLife.COLS; col++) {
                Cell cell = new Cell();
                cells[row][col] = cell;
                cell.addMouseListener(mouseAdapter);
//                Helpful when checking neighbours, breaks the game
/*                cell.addActionListener(e -> {
                    for (Cell neigh : cell.getNeighbouringCells()){
                        neigh.toggleSelectedState();
                    }
                });*/
                add(cell);
            }
        }
        for (int row = 0; row < GameOfLife.ROWS; row++) {
            int[] rowNeighbours = getNeighbouringIndexes(row, GameOfLife.ROWS);
            int up = rowNeighbours[0], down = rowNeighbours[1];
            for (int col = 0; col < GameOfLife.COLS; col++) {
                int[] colNeighbours = getNeighbouringIndexes(col, GameOfLife.COLS);
                int left = colNeighbours[0], right = colNeighbours[1];
                cells[row][col].addNeighbour(cells[up][left]);
                cells[row][col].addNeighbour(cells[up][col]);
                cells[row][col].addNeighbour(cells[up][right]);
                cells[row][col].addNeighbour(cells[row][left]);
                cells[row][col].addNeighbour(cells[row][right]);
                cells[row][col].addNeighbour(cells[down][left]);
                cells[row][col].addNeighbour(cells[down][col]);
                cells[row][col].addNeighbour(cells[down][right]);
            }
        }
    }

    private int[] getNeighbouringIndexes(int currentValue, int maxValue) {
        int[] neighbours = new int[2];
        if (currentValue == 0) {
            neighbours[0] = maxValue - 1;
            neighbours[1] = currentValue + 1;
        } else if (currentValue == maxValue - 1) {
            neighbours[0] = currentValue - 1;
            neighbours[1] = 0;
        } else {
            neighbours[0] = currentValue - 1;
            neighbours[1] = currentValue + 1;
        }
        return neighbours;
    }

    public void clearCells() {
        for (int row = 0; row < GameOfLife.ROWS; row++) {
            for (int col = 0; col < GameOfLife.COLS; col++) {
                cells[row][col].setState(false);
            }
        }
        revalidate();
        repaint();
    }

    public void nextStep() {
        for (int row = 0; row < GameOfLife.ROWS; row++) {
            for (int col = 0; col < GameOfLife.COLS; col++) {
                boolean isAlive = cells[row][col].isSelected();
                int aliveNeighbours = cells[row][col].getAliveNeighbourCount();
                if (isAlive && !GameOfLife.aliveNeighbours.contains(aliveNeighbours)){
                    cells[row][col].setNextState(false);
                } else if(!isAlive && GameOfLife.deadNeighbours.contains(aliveNeighbours)) {
                    cells[row][col].setNextState(true);
                } else cells[row][col].setNextState(isAlive);
            }
        }
        for (int row = 0; row < GameOfLife.ROWS; row++) {
            for (int col = 0; col < GameOfLife.COLS; col++) {
                cells[row][col].nextState();
            }
        }
        repaint();
    }
    public void createCopy() {
        for (int row = 0; row < GameOfLife.ROWS; row++) {
            for (int col = 0; col < GameOfLife.COLS; col++) {
                savedStatus[row][col] = cells[row][col].isSelected();
            }
        }
    }

    public void restoreCopy() {
        for (int row = 0; row < GameOfLife.ROWS; row++) {
            for (int col = 0; col < GameOfLife.COLS; col++) {
                cells[row][col].setState(savedStatus[row][col]);
            }
        }
    }
}
class Cell extends JButton {

    private final Color defaultColor = Color.BLACK;
    private final Color hoverColor = Color.GRAY;
    private final Color selectedColor = Color.WHITE;
    private final Set<Cell> neighbouringCells = new LinkedHashSet<Cell>();
    private boolean nextState;

    public Cell() {
        super();
        setFocusPainted(false);
        int screenHeightOptimal = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9);
        setPreferredSize(new Dimension(screenHeightOptimal/GameOfLife.ROWS, screenHeightOptimal/GameOfLife.COLS));
        setBackground(defaultColor);
    }

    public void setHoverState() {
        if (!isSelected()) {
            setBackground(hoverColor);
        }
    }

    public void setDefaultState() {
        if (!isSelected()) {
            setBackground(defaultColor);
        }
    }

    public void toggleSelectedState() {
        setSelected(!isSelected());
        setBackground(isSelected() ? selectedColor : defaultColor);
    }
    public void setState(boolean state){
        setSelected(state);
        setBackground(state ? selectedColor : defaultColor);
    }

    public void addNeighbour(Cell cell){
        neighbouringCells.add(cell);
    }
    public Set<Cell> getNeighbouringCells() {
        return neighbouringCells;
    }
    public int getAliveNeighbourCount(){
        int alive = 0;
        for (Cell neighbour : neighbouringCells){
            if (neighbour.isSelected()) {
                alive++;
            }
        }
        return alive;
    }

    public void setNextState(boolean nextState) {
        this.nextState = nextState;
    }
    public void nextState(){
        if(nextState!=isSelected()) {
            toggleSelectedState();
            revalidate();
        }
    }
}