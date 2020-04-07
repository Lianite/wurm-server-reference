// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.meshgen;

import javax.swing.SwingUtilities;
import com.wurmonline.server.utils.StringUtil;
import javax.swing.SwingWorker;
import javax.swing.JOptionPane;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.nio.FloatBuffer;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.text.DefaultCaret;
import java.awt.Insets;
import java.awt.Container;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.util.logging.Level;
import java.io.File;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import com.wurmonline.mesh.MeshIO;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.util.logging.Logger;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public final class MeshGenGui extends JFrame implements ActionListener
{
    private static final long serialVersionUID = -1462641916710560981L;
    private static final Logger logger;
    JLabel imageLabel;
    JButton generateGroundButton;
    JButton normaliseButton;
    JButton flowButton;
    JButton texturizeButton;
    JButton saveButton;
    JButton saveImageButton;
    JButton loadButton;
    JButton addIslandsButton;
    JToggleButton layerToggle;
    long seed;
    MeshGen meshGen;
    JPanel panel;
    MeshIO topLayerMeshIO;
    MeshIO rockLayerMeshIO;
    boolean loaded;
    final String baseDir;
    final String baseFile;
    private JProgressBar progressBar;
    private JTextArea taskOutput;
    private Task task;
    private JFrame frame;
    
    public MeshGenGui() {
        super("Wurm MeshGen GUI");
        this.seed = 0L;
        this.loaded = false;
        this.baseDir = "worldmachine" + File.separator + "NewEle2015";
        this.baseFile = File.separator + "output.r32";
        if (MeshGenGui.logger.isLoggable(Level.FINE)) {
            MeshGenGui.logger.fine("Starting Wurm MeshGen GUI");
        }
        (this.panel = new JPanel()).setLayout(new BorderLayout());
        this.imageLabel = new JLabel();
        (this.progressBar = new JProgressBar(0, 100)).setValue(0);
        this.progressBar.setStringPainted(true);
        (this.flowButton = new JButton("Load base map")).addActionListener(this);
        this.flowButton.setToolTipText("Load the output.r32 base map file");
        (this.texturizeButton = new JButton("Texturize")).addActionListener(this);
        (this.saveButton = new JButton("Save")).addActionListener(this);
        this.saveButton.setToolTipText("Save the top_layer.map and rock_layer.map");
        (this.loadButton = new JButton("Load")).addActionListener(this);
        this.loadButton.setToolTipText("Load the top_layer.map and rock_layer.map");
        (this.saveImageButton = new JButton("Save Image")).addActionListener(this);
        this.saveImageButton.setToolTipText("Save the coloured image of top_layer.map to map.png");
        (this.layerToggle = new JToggleButton("Layer", false)).addActionListener(this);
        this.layerToggle.setToolTipText("Selected shows the rock layer, unselected shows the surface layer");
        (this.addIslandsButton = new JButton("Add Islands")).addActionListener(this);
        this.addIslandsButton.setToolTipText("Add some islands to the top_layer.map and rock_layer.map");
        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.progressBar);
        buttonPanel.add(this.layerToggle);
        buttonPanel.add(this.flowButton);
        buttonPanel.add(this.texturizeButton);
        buttonPanel.add(this.addIslandsButton);
        buttonPanel.add(this.saveButton);
        buttonPanel.add(this.loadButton);
        buttonPanel.add(this.saveImageButton);
        this.panel.add(new JScrollPane(this.imageLabel), "Center");
        this.panel.add(buttonPanel, "South");
        this.setContentPane(this.panel);
        this.setSize(1200, 800);
        this.setDefaultCloseOperation(3);
        this.enableButtons(false);
        (this.frame = new JFrame("Please wait...")).setDefaultCloseOperation(3);
        this.frame.setLayout(new BorderLayout());
        (this.taskOutput = new JTextArea(20, 30)).setMargin(new Insets(5, 5, 5, 5));
        this.taskOutput.setEditable(false);
        final DefaultCaret caret = (DefaultCaret)this.taskOutput.getCaret();
        caret.setUpdatePolicy(2);
        final JScrollPane scroll = new JScrollPane(this.taskOutput, 22, 31);
        final JPanel mpanel = new JPanel();
        mpanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mpanel.add(scroll, "Center");
        this.frame.setContentPane(mpanel);
        this.frame.pack();
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        try {
            if (e.getSource() == this.flowButton) {
                if (!this.loaded) {
                    this.loaded = true;
                    try {
                        final JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(final File f) {
                                return f.isFile();
                            }
                            
                            @Override
                            public String getDescription() {
                                return "Files";
                            }
                        });
                        chooser.setCurrentDirectory(new File(this.baseDir));
                        final int returnVal = chooser.showOpenDialog(this.panel);
                        File selectedFile;
                        if (returnVal == 0) {
                            selectedFile = chooser.getSelectedFile();
                        }
                        else {
                            final StringBuilder sb;
                            selectedFile = new File(sb.append(this.baseDir).append(this.baseFile).toString());
                            sb = new StringBuilder();
                        }
                        final File worldMachineOutput = selectedFile;
                        final long baseFileSize = worldMachineOutput.length();
                        final double mapDimension = Math.sqrt(baseFileSize) / 2.0;
                        MeshGenGui.logger.info("Math.sqrt(fis.getChannel().size())/2 " + mapDimension);
                        if (MeshGenGui.logger.isLoggable(Level.FINE)) {
                            MeshGenGui.logger.fine("Opening " + worldMachineOutput.getName() + ", length: " + baseFileSize + " Bytes");
                        }
                        this.task = new Task("Loading file.") {
                            @Override
                            public void doWork() throws Exception {
                                try {
                                    MeshGenGui.this.imageLabel.setIcon(null);
                                    final FileInputStream fis = new FileInputStream(worldMachineOutput);
                                    DataInputStream dis = new DataInputStream(fis);
                                    final int size2 = (int)(Math.log(mapDimension) / Math.log(2.0));
                                    final int mapPixels = (int)(mapDimension * mapDimension);
                                    byte[] d = new byte[mapPixels * 4];
                                    dis.readFully(d);
                                    dis.close();
                                    ByteBuffer bb = ByteBuffer.wrap(d);
                                    bb.order(ByteOrder.LITTLE_ENDIAN);
                                    FloatBuffer fb = bb.asFloatBuffer();
                                    final float[] data = new float[mapPixels];
                                    fb.get(data);
                                    fb.position(0);
                                    fb.limit(data.length);
                                    fb = null;
                                    bb = null;
                                    d = null;
                                    dis = null;
                                    System.gc();
                                    (MeshGenGui.this.meshGen = new MeshGen(size2, this)).setData(data, this);
                                    MeshGenGui.this.imageLabel.setIcon(new ImageIcon(MeshGenGui.this.meshGen.getImage(this)));
                                }
                                catch (FileNotFoundException fnfe) {
                                    MeshGenGui.logger.log(Level.WARNING, "Problem loading Base Map", fnfe);
                                }
                                catch (IOException ioe) {
                                    MeshGenGui.logger.log(Level.WARNING, "Problem loading Base Map", ioe);
                                }
                            }
                        };
                        MeshGenGui.logger.log(Level.INFO, "Created task. Now setting prio and starting.");
                        this.task.execute();
                    }
                    catch (Exception ex) {
                        MeshGenGui.logger.log(Level.WARNING, "Problem loading Base Map", ex);
                    }
                }
                else {
                    (this.task = new Task("Showing Image.") {
                        @Override
                        public void doWork() throws Exception {
                            MeshGenGui.this.imageLabel.setIcon(new ImageIcon(MeshGenGui.this.meshGen.getImage(this)));
                        }
                    }).execute();
                }
            }
            else if (e.getSource() == this.texturizeButton) {
                if (this.meshGen != null) {
                    (this.task = new Task("Adding textures.") {
                        @Override
                        public void doWork() throws Exception {
                            MeshGenGui.this.imageLabel.setIcon(null);
                            final Random random = new Random();
                            MeshGenGui.this.meshGen.generateTextures(random, this);
                            MeshGenGui.this.imageLabel.setIcon(new ImageIcon(MeshGenGui.this.meshGen.getImage(this)));
                        }
                    }).execute();
                }
            }
            else if (e.getSource() == this.addIslandsButton) {
                if (this.meshGen != null) {
                    if (this.topLayerMeshIO != null) {
                        (this.task = new Task("Adding islands.") {
                            @Override
                            public void doWork() throws Exception {
                                MeshGenGui.this.imageLabel.setIcon(null);
                                final IslandAdder islandAdder = new IslandAdder(MeshGenGui.this.topLayerMeshIO, MeshGenGui.this.rockLayerMeshIO);
                                islandAdder.addIslands(MeshGenGui.this.meshGen.getWidth());
                                (MeshGenGui.this.meshGen = new MeshGen(islandAdder.getTopLayer().getSizeLevel(), this)).setData(islandAdder.getTopLayer(), islandAdder.getRockLayer(), this);
                                final BufferedImage image = MeshGenGui.this.meshGen.getImage(this);
                                MeshGenGui.this.imageLabel.setIcon(new ImageIcon(image));
                                MeshGenGui.this.topLayerMeshIO = islandAdder.getTopLayer();
                                MeshGenGui.this.rockLayerMeshIO = islandAdder.getRockLayer();
                            }
                        }).execute();
                    }
                    else {
                        MeshGenGui.logger.info("Failed to add Islands. Save map first.");
                    }
                }
            }
            else if (e.getSource() == this.loadButton) {
                try {
                    final String mapDirectory = this.selectMapDir();
                    MeshGenGui.logger.info("Opening Mesh " + mapDirectory + File.separatorChar + "top_layer.map");
                    final MeshIO meshIO = MeshIO.open(mapDirectory + File.separatorChar + "top_layer.map");
                    MeshGenGui.logger.info("Opening Mesh " + mapDirectory + File.separatorChar + "rock_layer.map");
                    final MeshIO meshIO2 = MeshIO.open(mapDirectory + File.separatorChar + "rock_layer.map");
                    if (meshIO.getSize() != meshIO2.getSize()) {
                        MeshGenGui.logger.warning("top layer and rock layer are not the same size");
                    }
                    (this.task = new Task("Loading maps.") {
                        @Override
                        public void doWork() throws Exception {
                            (MeshGenGui.this.meshGen = new MeshGen(meshIO.getSizeLevel(), this)).setData(meshIO, meshIO2, this);
                            final BufferedImage image = MeshGenGui.this.meshGen.getImage(this);
                            MeshGenGui.this.imageLabel.setIcon(new ImageIcon(image));
                            MeshGenGui.this.topLayerMeshIO = meshIO;
                            MeshGenGui.this.rockLayerMeshIO = meshIO2;
                        }
                    }).execute();
                }
                catch (IOException ioe) {
                    MeshGenGui.logger.log(Level.WARNING, "Problem loading Map", ioe);
                }
            }
            else if (e.getSource() == this.generateGroundButton) {
                if (this.meshGen != null) {
                    (this.task = new Task("Generating ground.") {
                        @Override
                        public void doWork() throws Exception {
                            MeshGenGui.this.imageLabel.setIcon(null);
                            MeshGenGui.this.meshGen.generateGround(new Random(), this);
                            final BufferedImage image = MeshGenGui.this.meshGen.getImage(this);
                            MeshGenGui.this.imageLabel.setIcon(new ImageIcon(image));
                        }
                    }).execute();
                }
            }
            else if (e.getSource() == this.saveButton) {
                (this.task = new Task("Saving maps.") {
                    @Override
                    public void doWork() throws Exception {
                        try {
                            MeshGenGui.this.imageLabel.setIcon(null);
                            MeshIO meshIO = MeshIO.createMap("top_layer_out.map", MeshGenGui.this.meshGen.getLevel(), MeshGenGui.this.meshGen.getData(this));
                            meshIO.close();
                            MeshGenGui.this.topLayerMeshIO = meshIO;
                            MeshGenGui.this.task.setNote(49, "Created top_layer_out.map");
                            MeshGenGui.logger.info("Created top_layer_out.map");
                            meshIO = MeshIO.createMap("rock_layer_out.map", MeshGenGui.this.meshGen.getLevel(), MeshGenGui.this.meshGen.getRockData(this));
                            meshIO.close();
                            MeshGenGui.this.rockLayerMeshIO = meshIO;
                            MeshGenGui.this.task.setNote(99, "Created rock_layer_out.map");
                            MeshGenGui.logger.info("Created rock_layer_out.map");
                        }
                        catch (IOException e1) {
                            MeshGenGui.logger.log(Level.WARNING, "problem saving Map", e1);
                        }
                    }
                }).execute();
            }
            else if (e.getSource() == this.saveImageButton) {
                if (this.meshGen != null) {
                    (this.task = new Task("Saving png.") {
                        @Override
                        public void doWork() throws Exception {
                            try {
                                MeshGenGui.this.imageLabel.setIcon(null);
                                final BufferedImage image = MeshGenGui.this.meshGen.getImage(this);
                                MeshGenGui.this.imageLabel.setIcon(new ImageIcon(image));
                                ImageIO.write(image, "png", new File("map.png"));
                                MeshGenGui.logger.info("Created map.png");
                            }
                            catch (IOException e1) {
                                MeshGenGui.logger.log(Level.WARNING, "problem saving image", e1);
                            }
                        }
                    }).execute();
                }
            }
            else if (e.getSource() == this.layerToggle && this.meshGen != null) {
                (this.task = new Task("Toggling layer.") {
                    @Override
                    public void doWork() throws Exception {
                        MeshGenGui.this.imageLabel.setIcon(null);
                        MeshGenGui.this.meshGen.setImageLayer(MeshGenGui.this.layerToggle.isSelected() ? 1 : 0);
                        final BufferedImage image = MeshGenGui.this.meshGen.getImage(this);
                        MeshGenGui.this.imageLabel.setIcon(new ImageIcon(image));
                    }
                }).execute();
            }
        }
        catch (RuntimeException re) {
            MeshGenGui.logger.log(Level.SEVERE, "Error while handling ActionClass ", re);
            throw re;
        }
    }
    
    private void enableButtons(final boolean running) {
        if (running) {
            this.progressBar.setVisible(true);
            this.layerToggle.setEnabled(false);
            this.flowButton.setEnabled(false);
            this.texturizeButton.setEnabled(false);
            this.addIslandsButton.setEnabled(false);
            this.saveButton.setEnabled(false);
            this.loadButton.setEnabled(false);
            this.saveImageButton.setEnabled(false);
        }
        else {
            final boolean sf = this.meshGen != null;
            this.progressBar.setVisible(false);
            this.layerToggle.setEnabled(sf);
            this.flowButton.setEnabled(true);
            this.texturizeButton.setEnabled(sf);
            this.addIslandsButton.setEnabled(sf);
            this.saveButton.setEnabled(sf);
            this.loadButton.setEnabled(true);
            this.saveImageButton.setEnabled(sf);
        }
    }
    
    private String selectMapDir() {
        while (true) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(final File f) {
                    return f.isDirectory();
                }
                
                @Override
                public String getDescription() {
                    return "Directories";
                }
            });
            chooser.setCurrentDirectory(new File("."));
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(final File f) {
                    return f.isFile();
                }
                
                @Override
                public String getDescription() {
                    return "Files";
                }
            });
            chooser.setCurrentDirectory(new File("."));
            chooser.setFileSelectionMode(1);
            chooser.setDialogTitle("Select the directory containing the map files");
            chooser.setApproveButtonText("Use this dir");
            chooser.setApproveButtonToolTipText("<html>The selected directory will be used by the Mesh Generator GUI<br> to load the top_layer.map and rock_layer.map files</html");
            final int returnVal = chooser.showOpenDialog(this.panel);
            if (returnVal != 0) {
                return null;
            }
            File file = chooser.getSelectedFile();
            if (file.isFile()) {
                if (MeshGenGui.logger.isLoggable(Level.FINE)) {
                    MeshGenGui.logger.fine("Using the directory containing the chosen file: " + file);
                }
                file = file.getParentFile();
            }
            if (!file.exists()) {
                file.mkdir();
            }
            if (file.listFiles().length != 0) {
                final int option = JOptionPane.showConfirmDialog(this.panel, "<html>Use \"" + file.toString() + "\"?", "Confirm directory", 0);
                if (option == 0) {
                    return file.toString();
                }
                continue;
            }
            else {
                final int option = JOptionPane.showConfirmDialog(this.panel, "<html>Use \"" + file.toString() + "\"?<br><br><b>Warning: The directory is empty.</b><br>This should contain the maps", "Confirm directory", 0);
                if (option == 0) {
                    return file.toString();
                }
                continue;
            }
        }
    }
    
    public static void main(final String[] args) {
        new MeshGenGui().setVisible(true);
    }
    
    static {
        logger = Logger.getLogger(MeshGenGui.class.getName());
    }
    
    abstract class Task extends SwingWorker<Void, Void>
    {
        int pmax;
        
        Task(final String title) {
            this.pmax = 100;
            new Thread("Wurm MeshGenGui") {
                @Override
                public void run() {
                    MeshGenGui.this.taskOutput.setText(title);
                    MeshGenGui.this.frame.setTitle(title + " Please wait...");
                    Task.this.setProgress(0);
                    MeshGenGui.this.progressBar.setValue(0);
                    MeshGenGui.this.frame.setVisible(true);
                    MeshGenGui.this.enableButtons(true);
                }
            }.start();
        }
        
        public void setMax(final int max) {
            this.pmax = max;
        }
        
        public void setNote(final String text) {
            System.out.println(text);
            if (MeshGenGui.this.taskOutput.getText().length() > 0) {
                MeshGenGui.this.taskOutput.append("\n");
            }
            MeshGenGui.this.taskOutput.append(text);
        }
        
        public void setNote(final int progress, final String text) {
            this.setNote(progress);
            this.setNote(text);
        }
        
        public void setNote(final int progress) {
            final int p = Math.max(Math.min(progress * 100 / this.pmax, 100), 0);
            MeshGenGui.this.progressBar.setValue(p);
        }
        
        public final Void doInBackground() {
            try {
                this.doWork();
            }
            catch (OutOfMemoryError e) {
                MeshGenGui.logger.log(Level.SEVERE, "Out of memory (Java heap space)", e);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        final String msg = StringUtil.format("Unexpected problem: %s", e.toString());
                        JOptionPane.showMessageDialog(MeshGenGui.this.panel, msg, "Unrecoverable error", 0);
                    }
                });
            }
            catch (Exception e2) {
                MeshGenGui.logger.log(Level.SEVERE, "Error while performing Task.doWork ", e2);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        final String msg = StringUtil.format("Unexpected problem: %s", e2.toString());
                        JOptionPane.showMessageDialog(MeshGenGui.this.panel, msg, "Unrecoverable error", 0);
                    }
                });
            }
            return null;
        }
        
        public abstract void doWork() throws Exception;
        
        public void done() {
            this.setProgress(100);
            MeshGenGui.this.progressBar.setValue(100);
            this.setNote("Finished!");
            MeshGenGui.this.frame.setVisible(false);
            MeshGenGui.this.enableButtons(false);
        }
    }
}
