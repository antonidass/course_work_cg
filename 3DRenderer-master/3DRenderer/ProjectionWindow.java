import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class ProjectionWindow extends JFrame implements ActionListener, KeyListener {
   private JPanel primaryPanel, lowerPanel, modsPanel;
   private Engine canvas;

   private JLabel fpsCounter, modes;

   JButton goButton;

   private JRadioButton wireframe, realistic, models, lowSpeed, normSpeed, highSpeed, highSpeed2, lowSpeed2;
   private JCheckBox downLight, upLight, cornerLight, fromLight, toLight;
   private JButton upScene, downScene, initScene;

   private Camera cam;

   public ProjectionWindow() throws IOException {
      super("3D Projection");
      setPreferredSize(new Dimension(800, 700));
      setLocation(800, 400);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      addKeyListener(this);
      setFocusable(true);

      primaryPanel = new JPanel();
      primaryPanel.setPreferredSize(new Dimension(800, 540));
      lowerPanel = new JPanel();
      lowerPanel.setPreferredSize(new Dimension(800, 140));
      lowerPanel.setLayout(new GridLayout(1, 5));
      canvas = new Engine(this);
      canvas.setPreferredSize(new Dimension(800, 500));
      cam = new Camera(800, 500);

      cam.setLocation((new Vector(0, 500, 250)).scale(2));
      cam.setRight((new Vector(-1, 0, 0)));
      cam.setDown(cam.getLocation().normalize().scale(-1).cross(cam.getRight()));

      cam.setPerspectivePoint(1000);
      canvas.setCamera(cam);

      Light.addLightSource(new Vector(1000, 900, 0));
      Light.addLightSource(new Vector(0, 0, 0));
      Drawer.setCamera(cam);

      fpsCounter = new JLabel("<html>Количество<br> кадров<br>в секунду: 0</html>");
      fpsCounter.setMinimumSize(new Dimension(50, 20));
      fpsCounter.setPreferredSize(new Dimension(50, 20));
      fpsCounter.setMaximumSize(new Dimension(50, 20));

      modsPanel = new JPanel();
      modsPanel.setLayout(new BoxLayout(modsPanel, BoxLayout.Y_AXIS));

      modes = new JLabel("Режимы");

      wireframe = new JRadioButton("Каркас");
      wireframe.addItemListener(this::modeStateChanged);
      realistic = new JRadioButton("Реалистичный", true);
      realistic.addItemListener(this::modeStateChanged);
      models = new JRadioButton("Только модели");
      models.addItemListener(this::modeStateChanged);

      ButtonGroup buttonGroup = new ButtonGroup();

      buttonGroup.add(realistic);
      buttonGroup.add(wireframe);
      buttonGroup.add(models);

      JPanel radioPanel = new JPanel();
      radioPanel.setLayout(new GridLayout(3, 1));
      radioPanel.add(realistic);
      radioPanel.add(wireframe);
      radioPanel.add(models);

      modsPanel.add(modes);
      modsPanel.add(radioPanel);

      goButton = new JButton("Старт");
      goButton.setFont(new Font("Arial", Font.PLAIN, 20));
      goButton.addActionListener(this::systemStateChanged);
      goButton.setVisible(true);

      cornerLight = new JCheckBox();
      cornerLight.setSelected(true);
      cornerLight.addItemListener(this::lightStateChanged);
      upLight = new JCheckBox();
      upLight.addItemListener(this::lightStateChanged);
      downLight = new JCheckBox();
      downLight.addItemListener(this::lightStateChanged);
      fromLight = new JCheckBox();
      fromLight.addItemListener(this::lightStateChanged);
      toLight = new JCheckBox();
      toLight.addItemListener(this::lightStateChanged);

      JPanel lightBox = new JPanel();
      lightBox.setLayout(new GridLayout(1, 5));
      lightBox.add(cornerLight);
      lightBox.add(upLight);
      lightBox.add(downLight);
      lightBox.add(fromLight);
      lightBox.add(toLight);

      BufferedImage upPicture = ImageIO.read(new File("input/up.png"));
      BufferedImage downPic = ImageIO.read(new File("input/down.png"));
      BufferedImage cornerPic = ImageIO.read(new File("input/corner.png"));
      BufferedImage fromPic = ImageIO.read(new File("input/from.png"));
      BufferedImage toPic = ImageIO.read(new File("input/to.png"));

      JLabel upLabel = new JLabel(new ImageIcon(upPicture));
      JLabel downLabel = new JLabel(new ImageIcon(downPic));
      JLabel cornerLabel = new JLabel(new ImageIcon(cornerPic));
      JLabel toLabel = new JLabel(new ImageIcon(toPic));
      JLabel fromLabel = new JLabel(new ImageIcon(fromPic));

      JLabel lightLabel = new JLabel("   Источник света");

      JPanel lightPanel = new JPanel();
      lightPanel.setLayout(new FlowLayout());
      lightPanel.add(cornerLabel);
      lightPanel.add(downLabel);
      lightPanel.add(upLabel);
      lightPanel.add(fromLabel);
      lightPanel.add(toLabel);

      JPanel lightPanelPlus = new JPanel();
      lightPanelPlus.setLayout(new GridLayout(3, 1));
      lightPanelPlus.add(lightLabel);
      lightPanelPlus.add(lightPanel);
      lightPanelPlus.add(lightBox);

      JLabel turnLabel = new JLabel("Вид");
      JLabel passLabel = new JLabel("");

      upScene = new JButton("Сверху");
      upScene.addActionListener(this::turnSceneListener);
      downScene = new JButton("Снизу");
      downScene.addActionListener(this::turnSceneListener);
      initScene = new JButton("Спереди");
      initScene.addActionListener(this::turnSceneListener);

      JPanel turnScenePanel = new JPanel();
      turnScenePanel.setLayout(new GridLayout(4, 2));
      turnScenePanel.add(turnLabel);
      turnScenePanel.add(passLabel);
      turnScenePanel.add(upScene);
      turnScenePanel.add(downScene);
      turnScenePanel.add(initScene);

      JLabel speedLabel = new JLabel("Скорость системы");

      lowSpeed = new JRadioButton("0.5x");
      lowSpeed.addItemListener(this::speedChanged);
      lowSpeed2 = new JRadioButton("0.75x");
      lowSpeed2.addItemListener(this::speedChanged);
      normSpeed = new JRadioButton("1x", true);
      normSpeed.addItemListener(this::speedChanged);
      highSpeed = new JRadioButton("1.5x");
      highSpeed.addItemListener(this::speedChanged);
      highSpeed2 = new JRadioButton("4x");
      highSpeed2.addItemListener(this::speedChanged);

      ButtonGroup speedGroup = new ButtonGroup();

      speedGroup.add(lowSpeed);
      speedGroup.add(lowSpeed2);
      speedGroup.add(normSpeed);
      speedGroup.add(highSpeed);
      speedGroup.add(highSpeed2);

      JPanel speedPanel = new JPanel();
      speedPanel.setLayout(new GridLayout(5, 1));
      speedPanel.add(lowSpeed);
      speedPanel.add(lowSpeed2);
      speedPanel.add(normSpeed);
      speedPanel.add(highSpeed);
      speedPanel.add(highSpeed2);

      JPanel speedPanelPlus = new JPanel();
      speedPanelPlus.setLayout(new GridLayout(2, 1));
      speedPanelPlus.add(speedLabel);
      speedPanelPlus.add(speedPanel);

      lowerPanel.add(modsPanel);
      lowerPanel.add(goButton);

      lowerPanel.add(lightPanelPlus);
      lowerPanel.add(turnScenePanel);
      lowerPanel.add(speedPanelPlus);
      lowerPanel.add(fpsCounter);

      primaryPanel.add(canvas);
      primaryPanel.add(lowerPanel);
   
      getContentPane().add(primaryPanel);
      pack();
      this.requestFocusInWindow();
      setVisible(true);
      canvas.load();
   }


   public void actionPerformed(ActionEvent e) {
      if(e.getActionCommand().equals("fps timer")) {
         fpsCounter.setText("<html>Количество<br> кадров<br>в секунду: " +  canvas.getFps() + "</html>");
      }
   }
  
   public void modeStateChanged(ItemEvent e) {
      if (e.getSource() == wireframe) {
         Drawer.setWireframe(true);
         Drawer.setMode(Mode.WIREFRAME);
         canvas.setBackground(Color.WHITE);

      } else if (e.getSource() == realistic) {
         Drawer.setWireframe(false);
         Drawer.setMode(Mode.REALISTIC);
         canvas.setBackground(Color.BLACK);

      } else {
         Drawer.setWireframe(false);
         Drawer.setMode(Mode.MODELS);
         canvas.setBackground(Color.BLACK);
      }
      this.requestFocusInWindow();
   }

   public void speedChanged(ItemEvent e) {
      if (e.getSource() == lowSpeed2) {
         canvas.setSpeedSystem(45);
      } else if (e.getSource() == normSpeed) {
         canvas.setSpeedSystem(60);
      } else if (e.getSource() == lowSpeed) {
         canvas.setSpeedSystem(30);
      } else if (e.getSource() == highSpeed2) {
         canvas.setSpeedSystem(240);
      }
      else {
         canvas.setSpeedSystem(90);
      }
      this.requestFocusInWindow();
   }

   public void lightStateChanged(ItemEvent e) {
      if (e.getSource() == cornerLight) {
         if (cornerLight.isSelected()) {
            Light.addLightSource(new Vector(1000, 900, 0));
         } else {
            Light.removeLightSource(new Vector(1000, 900, 0));
         }
      } else if (e.getSource() == toLight) {
         if (toLight.isSelected()) {
            Light.addLightSource(new Vector(0, 500, 0));
         } else {
            Light.removeLightSource(new Vector(0, 500, 0));
         }
      } else if (e.getSource() == downLight) {
         if (downLight.isSelected()) {
            Light.addLightSource(new Vector(0, 500, -500));
         } else {
            Light.removeLightSource(new Vector(0, 500, -500));
         }
      } else if (e.getSource() == upLight) {
         if (upLight.isSelected()) {
            Light.addLightSource(new Vector(0, 500, 500));
         } else {
            Light.removeLightSource(new Vector(0, 500, 500));
         }
      } else if (e.getSource() == fromLight) {
         if (fromLight.isSelected()) {
            Light.addLightSource(new Vector(0, -200, 0));
         } else {
            Light.removeLightSource(new Vector(0, -200, 0));
         }
      }

      this.requestFocusInWindow();
   }

   private void systemStateChanged(ActionEvent event) {
      if (event.getSource() == goButton) {
         if (goButton.getLabel() == "Старт") {
            goButton.setLabel("Стоп");
            canvas.isMemento = false;
            canvas.setTimer(true);
         } else {
            goButton.setLabel("Старт");
            canvas.isMemento = true;
            canvas.setTimer(false);
         }
      }
   }

   private void turnSceneListener(ActionEvent event) {
      if (event.getSource() == upScene) {
            canvas.rotateScene(0, 200, 850);
      } else if (event.getSource() == downScene) {
            canvas.rotateScene(0, 200, -350);
      } else {
            canvas.rotateScene(0, 500, 250);
      }
   }

   
   public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
         System.exit(0);
      }
   }
   
   public void keyPressed(KeyEvent e) {}
   
   public void keyTyped(KeyEvent e) {}
 
   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         try {
            new ProjectionWindow();
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
   }
}
