/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counsil;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author Huvart
 */
public class RGBChooserPanel extends AbstractColorChooserPanel{

    private final ResourceBundle languageBundle;
    /**
   * This class handles the slider value changes for all three sliders.
   */
  class SliderHandler implements ChangeListener
  {
    /**
     * This method is called whenever any of the slider values change.
     *
     * @param e The ChangeEvent.
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
      if (updateChange)
	return;

      int color = R.getValue() << 16 | G.getValue() << 8 | B.getValue();

      sliderChange = true;
      getColorSelectionModel().setSelectedColor(new Color(color));
      sliderChange = false;
    }
  }

  /**
   * This class handles the Spinner values changing.
   */
  class SpinnerHandler implements ChangeListener
  {
    /**
     * This method is called whenever any of the JSpinners change values.
     *
     * @param e The ChangeEvent.
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
      if (updateChange)
	return;

      int red = ((Number) RSpinner.getValue()).intValue();
      int green = ((Number) GSpinner.getValue()).intValue();
      int blue = ((Number) BSpinner.getValue()).intValue();

      int color = red << 16 | green << 8 | blue;

      spinnerChange = true;
      getColorSelectionModel().setSelectedColor(new Color(color));
      spinnerChange = false;
    }
  }

  /** Whether the color change was initiated by the spinners. */
  private transient boolean spinnerChange = false;

  /** Whether the color change was initiated by the sliders. */
  private transient boolean sliderChange = false;

  /**
   * Whether the change was forced by the chooser (meaning the color has
   * already been changed).
   */
  private transient boolean updateChange = false;

  /** The ChangeListener for the sliders. */
  private transient ChangeListener colorChanger;

  /** The ChangeListener for the spinners. */
  private transient ChangeListener spinnerHandler;

  /** The slider that handles the red values. */
  private transient JSlider R;

  /** The slider that handles the green values. */
  private transient JSlider G;

  /** The slider that handles the blue values. */
  private transient JSlider B;

  /** The label for the red slider. */
  private transient JLabel RLabel;

  /** The label for the green slider. */
  private transient JLabel GLabel;

  /** The label for the blue slider. */
  private transient JLabel BLabel;

  /** The spinner that handles the red values. */
  private transient JSpinner RSpinner;

  /** The spinner that handles the green values. */
  private transient JSpinner GSpinner;

  /** The spinner that handles the blue values. */
  private transient JSpinner BSpinner;
  

  /**
   * Creates a new DefaultRGBChooserPanel object.
     * @param languageBundle
   */
  public RGBChooserPanel(ResourceBundle languageBundle)
  {    
    super();
    this.languageBundle = languageBundle;
  }

  /**
   * This method returns the name displayed in the JTabbedPane.
   *
   * @return The name displayed in the JTabbedPane.
   */
  @Override
  public String getDisplayName()
  {
    return "RGB";
  }

  /**
   * This method updates the chooser panel with the new color chosen in the
   * JColorChooser.
   */
  @Override
  public void updateChooser()
  {
    Color c = getColorFromModel();
    int rgb = c.getRGB();

    int red = rgb >> 16 & 0xff;
    int green = rgb >> 8 & 0xff;
    int blue = rgb & 0xff;

    updateChange = true;

    if (!sliderChange)
      {
	if (R != null)
	  R.setValue(red);
	if (G != null)
	  G.setValue(green);
	if (B != null)
	  B.setValue(blue);
      }
    if (!spinnerChange)
      {
	if (GSpinner != null)
	  GSpinner.setValue(green);
	if (RSpinner != null)
	  RSpinner.setValue(red);
	if (BSpinner != null)
	  BSpinner.setValue(blue);
      }

    updateChange = false;

    revalidate();
    repaint();
  }

  /**
   * This method builds the chooser panel.
   */
  @Override
  protected void buildChooser()
  {
    setLayout(new GridBagLayout());

    RLabel = new JLabel(languageBundle.getString("RED"));
    RLabel.setDisplayedMnemonic('d');
    GLabel = new JLabel(languageBundle.getString("GREEN"));
    GLabel.setDisplayedMnemonic('n');
    BLabel = new JLabel(languageBundle.getString("BLUE"));
    BLabel.setDisplayedMnemonic('B');

    R = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255);
    G = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255);
    B = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255);

    R.setPaintTicks(true);
    R.setSnapToTicks(false);
    G.setPaintTicks(true);
    G.setSnapToTicks(false);
    B.setPaintTicks(true);
    B.setSnapToTicks(false);

    R.setLabelTable(R.createStandardLabels(85));
    R.setPaintLabels(true);
    G.setLabelTable(G.createStandardLabels(85));
    G.setPaintLabels(true);
    B.setLabelTable(B.createStandardLabels(85));
    B.setPaintLabels(true);

    R.setMajorTickSpacing(85);
    G.setMajorTickSpacing(85);
    B.setMajorTickSpacing(85);

    R.setMinorTickSpacing(17);
    G.setMinorTickSpacing(17);
    B.setMinorTickSpacing(17);

    RSpinner = new JSpinner(new SpinnerNumberModel(R.getValue(),
                                                   R.getMinimum(),
                                                   R.getMaximum(), 1));
    GSpinner = new JSpinner(new SpinnerNumberModel(G.getValue(),
                                                   G.getMinimum(),
                                                   G.getMaximum(), 1));
    BSpinner = new JSpinner(new SpinnerNumberModel(B.getValue(),
                                                   B.getMinimum(),
                                                   B.getMaximum(), 1));

    RLabel.setLabelFor(R);
    GLabel.setLabelFor(G);
    BLabel.setLabelFor(B);

    GridBagConstraints bag = new GridBagConstraints();
    bag.fill = GridBagConstraints.VERTICAL;

    bag.gridx = 0;
    bag.gridy = 0;
    add(RLabel, bag);

    bag.gridx = 1;
    add(R, bag);

    bag.gridx = 2;
    add(RSpinner, bag);

    bag.gridx = 0;
    bag.gridy = 1;
    add(GLabel, bag);

    bag.gridx = 1;
    add(G, bag);

    bag.gridx = 2;
    add(GSpinner, bag);

    bag.gridx = 0;
    bag.gridy = 2;
    add(BLabel, bag);

    bag.gridx = 1;
    add(B, bag);

    bag.gridx = 2;
    add(BSpinner, bag);

    installListeners();
  }

  /**
   * This method uninstalls the chooser panel from the JColorChooser.
   *
   * @param chooser The JColorChooser to remove this chooser panel from.
   */
  @Override
  public void uninstallChooserPanel(JColorChooser chooser)
  {
    uninstallListeners();
    removeAll();

    R = null;
    G = null;
    B = null;

    RSpinner = null;
    GSpinner = null;
    BSpinner = null;

    super.uninstallChooserPanel(chooser);
  }

  /**
   * This method uninstalls any listeners that were added by the chooser
   * panel.
   */
  private void uninstallListeners()
  {
    R.removeChangeListener(colorChanger);
    G.removeChangeListener(colorChanger);
    B.removeChangeListener(colorChanger);

    colorChanger = null;

    RSpinner.removeChangeListener(spinnerHandler);
    GSpinner.removeChangeListener(spinnerHandler);
    BSpinner.removeChangeListener(spinnerHandler);

    spinnerHandler = null;
  }

  /**
   * This method installs any listeners that the chooser panel needs to
   * operate.
   */
  private void installListeners()
  {
    colorChanger = new SliderHandler();

    R.addChangeListener(colorChanger);
    G.addChangeListener(colorChanger);
    B.addChangeListener(colorChanger);

    spinnerHandler = new SpinnerHandler();

    RSpinner.addChangeListener(spinnerHandler);
    GSpinner.addChangeListener(spinnerHandler);
    BSpinner.addChangeListener(spinnerHandler);
  }

  /**
   * This method returns the small display icon.
   *
   * @return The small display icon.
   */
  @Override
  public Icon getSmallDisplayIcon()
  {
    return null;
  }

  /**
   * This method returns the large display icon.
   *
   * @return The large display icon.
   */
  @Override
  public Icon getLargeDisplayIcon()
  {
    return null;
  }

  /**
   * This method paints the default RGB chooser panel.
   *
   * @param g The Graphics object to paint with.
   */
  @Override
  public void paint(Graphics g)
  {
    super.paint(g);
  }
}

