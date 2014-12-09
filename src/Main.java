import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.win32.User32;

import javax.swing.*;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

class ML extends Window
{
	boolean showGradient = true;
	int loc;

	ML(int a) throws Exception
	{
		super(null);
		loc = a;
		setBackground(Main.color);
		AWTUtilities.setWindowOpaque(this, false);
		if (loc % 2 == 0)
			setSize(Main.size, 7);
		else
			setSize(7, Main.size);
		setVisible(true);
		setAlwaysOnTop(true);
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		Color n1 = Main.color;
		Color n2 = Main.color;
		Color b1 = Main.color;
		Color b2 = Main.color;
		Paint p;
		switch (loc)
		{
			case 0:
				n1 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 0);
				n2 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 255);
				b1 = new Color(0, 0, 0, 0);
				b2 = new Color(0, 0, 0, 255);
				break;
			case 1:
				n1 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 255);
				n2 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 0);
				b1 = new Color(0, 0, 0, 255);
				b2 = new Color(0, 0, 0, 0);
				break;
			case 2:
				n1 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 255);
				n2 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 0);
				b1 = new Color(0, 0, 0, 255);
				b2 = new Color(0, 0, 0, 0);
				break;
			case 3:
				n1 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 0);
				n2 = new Color(Main.color.getRed(), Main.color.getGreen(), Main.color.getBlue(), 255);
				b1 = new Color(0, 0, 0, 0);
				b2 = new Color(0, 0, 0, 255);
				break;
		}
		p = new GradientPaint(0.0f, 0.0f, n1, getWidth(), getHeight(), n2, true);
		g2d.setPaint(showGradient ? p : Main.color);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		p = new GradientPaint(0.0f, 0.0f, b1, getWidth(), getHeight(), b2, true);
		g2d.setPaint(showGradient ? p : Color.black);
		g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
	}

	public void setLength()
	{
		if (loc % 2 == 0)
			setSize(Main.size, 7);
		else
			setSize(7, Main.size);
	}

	public void setLocation()
	{
		Point p = MouseInfo.getPointerInfo().getLocation();
		switch (loc)
		{
			case 1:
				p.y += 30;
				p.x -= getWidth() / 2;
				break;
			case 2:
				p.y -= getHeight() / 2;
				p.x += 30;
				break;
			case 0:
				p.y -= getHeight() / 2;
				p.x -= getWidth() + 30;
				break;
			case 3:
				p.y -= getHeight() + 30;
				p.x -= getWidth() / 2;
				break;
		}
		setLocation(p);
	}
}

class TA
{
	PopupMenu tm;

	TA(MenuItem...a)
	{
		tm = new PopupMenu();
		for (MenuItem i : a)
			tm.add(i);
		TrayIcon ico = new TrayIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("mouse.png"))).getImage(), "", tm);
		try
		{
			ico.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					if (e.getButton() == MouseEvent.BUTTON1) Main.toggleThread();
				}
			});
			SystemTray.getSystemTray().add(ico);
		} catch (Exception e) {}
	}
}

class Settings extends JFrame
{
	JLabel colorl = new JLabel("Color: ");
	JButton color = new JButton();

	JLabel sizel = new JLabel("Size: ");
	JTextField size = new JTextField();

	JLabel smoothnessl = new JLabel("Smoothness: ");
	JTextField smoothness = new JTextField();

	boolean wasVisible = false;

	public Settings()
	{
		super("Settings");

		color.addActionListener(e ->
		{
			Color temp = Main.color;
			setVisible(false);
			final JColorChooser jcc = new JColorChooser();
			jcc.getSelectionModel().addChangeListener(e1 -> Main.setColor(((DefaultColorSelectionModel) e1.getSource()).getSelectedColor()));
			JColorChooser.createDialog(null, null, true, jcc, e1 -> color.setBackground(jcc.getColor()), e1 -> color.setBackground(temp)).setVisible(true);
			setVisible(true);
			save();
		});
		size.addActionListener(e -> save());
		smoothness.addActionListener(e -> save());

		Container c = getContentPane();
		SpringLayout sl = new SpringLayout();
		sl.putConstraint(SpringLayout.NORTH, colorl, 10, SpringLayout.NORTH, c);
		sl.putConstraint(SpringLayout.WEST, colorl, 10, SpringLayout.WEST, c);
		sl.putConstraint(SpringLayout.NORTH, sizel, 10, SpringLayout.SOUTH, colorl);
		sl.putConstraint(SpringLayout.WEST, sizel, 10, SpringLayout.WEST, c);
		sl.putConstraint(SpringLayout.NORTH, smoothnessl, 10, SpringLayout.SOUTH, sizel);
		sl.putConstraint(SpringLayout.WEST, smoothnessl, 10, SpringLayout.WEST, c);

		sl.putConstraint(SpringLayout.NORTH, color, 0, SpringLayout.NORTH, colorl);
		sl.putConstraint(SpringLayout.SOUTH, color, 0, SpringLayout.SOUTH, colorl);
		sl.putConstraint(SpringLayout.WEST, color, 10, SpringLayout.EAST, smoothnessl);
		sl.putConstraint(SpringLayout.EAST, color, -10, SpringLayout.EAST, c);
		sl.putConstraint(SpringLayout.NORTH, size, 0, SpringLayout.NORTH, sizel);
		sl.putConstraint(SpringLayout.WEST, size, 10, SpringLayout.EAST, smoothnessl);
		sl.putConstraint(SpringLayout.EAST, size, -10, SpringLayout.EAST, c);
		sl.putConstraint(SpringLayout.NORTH, smoothness, 0, SpringLayout.NORTH, smoothnessl);
		sl.putConstraint(SpringLayout.WEST, smoothness, 10, SpringLayout.EAST, smoothnessl);
		sl.putConstraint(SpringLayout.EAST, smoothness, -10, SpringLayout.EAST, c);
		c.setLayout(sl);

		c.add(colorl);
		c.add(color);
		c.add(sizel);
		c.add(size);
		c.add(smoothnessl);
		c.add(smoothness);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				hidePanel();
			}
		});

		addWindowFocusListener(new WindowFocusListener()
		{
			public void windowGainedFocus(WindowEvent e){}
			public void windowLostFocus(WindowEvent e)
			{
				hidePanel();
			}
		});

		setSize(new Dimension(300, 150));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
	}

	public void showPanel()
	{
		color.setBackground(Main.color);
		size.setText(Main.size + "");
		smoothness.setText((Main.smoothness / 1000000.0) + "");
		wasVisible = Main.running();
		Main.toggleThread(true);
		setVisible(true);
	}

	public void hidePanel()
	{
		save();
		setVisible(false);
		if (!wasVisible)
			Main.toggleThread(false);
	}

	public void save()
	{
		try {Main.setColor(color.getBackground());} catch (Exception e){}
		try {Main.setSize(Integer.parseInt(size.getText()));} catch (Exception e){}
		try {Main.smoothness = (long)Math.ceil(Double.parseDouble(smoothness.getText()) * 1000000.0);} catch (Exception e){}
	}
}

public class Main
{
	public static Color color = Color.WHITE;
	static void setColor(Color c)
	{
		Main.color = c;
		if (running())
			Main.toggleThread(true);
	}
	public static int size = 200;
	static void setSize(int len)
	{
		Main.size = len;
		for (ML ml : lines)
			ml.setLength();
		if (running())
			Main.toggleThread(true);
	}
	public static long smoothness = 100000;
	public static Settings settings = new Settings();
	static boolean shouldStop = false;
	static boolean done = true;
	static boolean monitorMouse = false;
	static long lastMousePress = 0;
	static Runnable mover = new Runnable()
	{
		public void run()
		{
			shouldStop = false;
			done = false;
			while (!shouldStop)
			{
				if (monitorMouse)
				{
					if (User32.INSTANCE.GetAsyncKeyState(0x01) < 0 || User32.INSTANCE.GetAsyncKeyState(0x02) < 0)
					{
						if (System.currentTimeMillis() - lastMousePress > 50) moveLines(false);
					}
					else
					{
						moveLines(true);
						lastMousePress = System.currentTimeMillis();
					}
				}
				else moveLines(true);

				LockSupport.parkNanos(smoothness);
			}

			visible(false);
			done = true;
		}

		private void moveLines(boolean v)
		{
			for (ML ml : lines)
				ml.setLocation();
			visible(v);
		}
	};
	static ArrayList<ML> lines;

	public static void main(String[] args) throws Exception
	{
		lines = new ArrayList<>();
		for (int i = 0; i < 4; ++i)
			lines.add(new ML(i));

		MenuItem ex = new MenuItem("Exit");
		MenuItem showSettings = new MenuItem("Settings");
		final MenuItem wowHotfix = new MenuItem("Enable mouse button hook (WoW hotfix)");
		ex.addActionListener(e -> System.exit(0));
		wowHotfix.addActionListener(e ->
		{
			wowHotfix.setLabel((monitorMouse ? "En" : "Dis") + "able mouse button hook (WoW hotfix)");
			monitorMouse = !monitorMouse;
		});
		showSettings.addActionListener(e -> settings.showPanel());

		if (new File("jna.jar").exists() && new File("platform.jar").exists()) new TA(showSettings, wowHotfix, ex);
		else new TA(showSettings, ex);
		new Thread(mover).start();
	}

	static synchronized void visible(boolean v)
	{
		for (ML ml : lines)
			ml.setVisible(v);
	}

	static void toggleThread()
	{
		toggleThread(!running());
	}

	static void toggleThread(boolean b)
	{
		if (running())
		{
			shouldStop = true;
			while (!done) try {Thread.sleep(10);} catch (Exception e) {}
		}

		if (b)
			new Thread(mover).start();
	}

	static boolean running()
	{
		return !done;
	}
}
