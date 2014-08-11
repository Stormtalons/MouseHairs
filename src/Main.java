import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.win32.User32;

import javax.swing.*;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

class ML extends Window
{
	Color currentColor;
	boolean showGradient = true;
	int loc;

	ML(int a) throws Exception
	{
		super(null);
		loc = a;
		currentColor = new Color(255, 0, 0);
		setBackground(currentColor, false);
		if (loc % 2 == 0)
			setSize(Main.hairLen, 7);
		else
			setSize(7, Main.hairLen);
		setVisible(true);
		setAlwaysOnTop(true);
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		Color c = getBackground();
		Color n1 = c;
		Color n2 = c;
		Color b1 = c;
		Color b2 = c;
		Paint p;
		switch (loc)
		{
			case 0:
				n1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
				n2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
				b1 = new Color(0, 0, 0, 0);
				b2 = new Color(0, 0, 0, 255);
				break;
			case 1:
				n1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
				n2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
				b1 = new Color(0, 0, 0, 255);
				b2 = new Color(0, 0, 0, 0);
				break;
			case 2:
				n1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
				n2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
				b1 = new Color(0, 0, 0, 255);
				b2 = new Color(0, 0, 0, 0);
				break;
			case 3:
				n1 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
				n2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
				b1 = new Color(0, 0, 0, 0);
				b2 = new Color(0, 0, 0, 255);
				break;
		}
		p = new GradientPaint(0.0f, 0.0f, n1, getWidth(), getHeight(), n2, true);
		g2d.setPaint(showGradient ? p : c);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		p = new GradientPaint(0.0f, 0.0f, b1, getWidth(), getHeight(), b2, true);
		g2d.setPaint(showGradient ? p : Color.black);
		g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
	}

	public void setBackground(Color c, boolean b)
	{
		setBackground(c);
		AWTUtilities.setWindowOpaque(this, b);
	}

	public void setLength()
	{
		if (loc % 2 == 0)
			setSize(Main.hairLen, 7);
		else
			setSize(7, Main.hairLen);
		setBackground(currentColor, false);
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

	public void toggleGradient()
	{
		showGradient = !showGradient;
		repaint();
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

public class Main
{
	public static int hairLen = 130;
	static boolean shouldStop = false;
	static boolean monitorMouse = false;
	static long lastMousePress = 0;
	static Runnable mover = new Runnable()
	{
		public void run()
		{
			shouldStop = false;
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

				LockSupport.parkNanos(100000);
			}

			visible(false);
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
		MenuItem tglGrd = new MenuItem("Toggle Gradient");
		MenuItem clr = new MenuItem("Color");
		MenuItem size = new MenuItem("Size");
		final MenuItem wowHotfix = new MenuItem("Enable mouse button hook (WoW hotfix)");
		ex.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		tglGrd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for (ML ml : lines) ml.toggleGradient();
			}
		});
		clr.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JColorChooser jcc = new JColorChooser();
				jcc.getSelectionModel().addChangeListener(new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						setColor(((DefaultColorSelectionModel) e.getSource()).getSelectedColor());
					}
				});
				JColorChooser.createDialog(null, null, true, jcc, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						setColor(jcc.getColor());
					}
				}, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						setColor(lines.get(0).currentColor);
					}
				}).setVisible(true);
			}
		});
		size.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					hairLen = Integer.parseInt(JOptionPane.showInputDialog(null, "New crosshair length in pixels:"));
					for (ML ml : lines)
						ml.setLength();
				} catch (Exception ignored) {}
			}
		});
		wowHotfix.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				wowHotfix.setLabel((monitorMouse ? "En" : "Dis") + "able mouse button hook (WoW hotfix)");
				monitorMouse = !monitorMouse;
			}
		});

		MenuItem[] items;

		if (new File("jna.jar").exists() && new File("platform.jar").exists()) items = new MenuItem[] {wowHotfix, clr, size, ex};
		else
		{
			JOptionPane.showMessageDialog(null, "jna.jar or platform.jar missing from source directory.\nWoW hotfix will not be available.");
			items = new MenuItem[] {clr, size, ex};
		}

		new TA(items);
		new Thread(mover).start();
	}

	static synchronized void visible(boolean v)
	{
		for (ML ml : lines)
			ml.setVisible(v);
	}

	static void toggleThread()
	{
		if (shouldStop)
			new Thread(mover).start();
		else
			shouldStop = true;
	}

	static void setColor(Color c)
	{
		for (ML ml : lines)
			ml.setBackground(c, false);
	}
}
