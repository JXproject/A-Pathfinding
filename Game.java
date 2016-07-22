import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.ImageGraphicAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
/*
# A-Pathfinding
A* pahfinding

************************************
Author: JXstudio / JXinBOX / Jack Xu
************************************
*
Block Color Indicators:
Red   : Starting Point
Blue  : Ending Point
Black : Walls (Obstacles)
Orange: Final Shortest Path
Green : Closed List
Blue  : Open List

Controller:
1. [Left Click/Drag] : Create Walls (black obstacles)
2. [Right Click/Drag] : Destroy Walls 
3. [E]+[One Left Click] : Create Ending Point
4. [S]+[One Left Click] : Create Start Point
P.S.: Starting point can only be created after the existence of Ending point.

5. [A] : Showing The Path Finding Process with Animation Automatically
6. [Space] : Showing The Path Finding Process with Animation Manually, One click and one step.
7. [F] : Showing The Shortest Path and results, and time duration of calculation.

8. [E] : Empty all the path and starting points. (All blocks except walls and the ending block)
9. [L] : Clear all

10. [G] : Show/Hide Grids
11. [Scroll D/U] : Zoom In / Out
12. [Shift]+[Mouse Drag]: Move around the Canvas
*/
public class Game extends Canvas implements Runnable {
	public static int horCells = 20, verCells = 20, AvailableX = 0, AvailableY = 0, MaxScale = 50, MinScale = 2;
	public static int width = 1000;
	public static int MainWidth = 800;
	public static int height = MainWidth / 4 * 4;
	public static int scale = 1;
	public static int Quan_frames = 0;
	public static int Quan_updates = 0;
	public static double ticks = 0;
	public static final String TITLE = "Game";

	private Thread thread;// handle game
	private JFrame frame;
	private Screen screen;
	private boolean running = false;
	public static InputHandler input;

	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	public static int cellW, cellH;

	public static Block Start, End;
	public static List wall, OpenList = new List(), ClosedList = new List(), ShortPath = new List();
	public static int CostS = 10, CostD = 14;

	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		screen = new Screen(width, height, MainWidth);
		frame = new JFrame();
		input = new InputHandler();
		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		addMouseWheelListener(input);

		cellW = MainWidth / horCells;
		cellH = height / verCells;
		wall = new List();
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "A*");
		// handle this game, attached to the Game
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.setTitle(TITLE + " | " + Quan_updates + " UPS | " + Quan_frames + " FPS");
		game.frame.add(game);// add canvas component to the frame window
		game.frame.pack();// size up the frame to this game component
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		game.start();
	}

	public static long timerf = 0;
	boolean FastS=false;

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		long timertick = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;

		Quan_frames = 0;
		Quan_updates = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (Fastmode) {
				timerf = System.currentTimeMillis();
				FastS=true;
			}
			while (delta >= 1) {
				update();// hold logic
				Quan_updates++;
				ticks++;
				delta--;
			}
			if(FastS){
				timerf = System.currentTimeMillis() - timer;
				FastS=false;
			}
			
			render();// render

			Quan_frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(TITLE + " | " + Quan_updates + " UPS | " + Quan_frames + " FPS");
				Quan_updates = 0;
				Quan_frames = 0;
			}
		}
		stop();
	}

	public static int iteration = 0;
	static boolean finishPathFinding = false;
	static boolean reach = false, Fastmode = false;

	public void update() {
		if (Fastmode) {
			while (!reach) {
				OpenList.sortF();
				if (OpenList.size() == 0) {
					System.out.println("Impossible END");
					reach = true;
				} else {
					Expand((Block) OpenList.get(0));
				}
			}
			Fastmode = false;
		} else if ((input.run || input.AutoRun) && Game.Start != null && Game.End != null && !reach) {
			if (ticks % 1 == 0) {
				OpenList.sortF();
				if (OpenList.size() == 0) {
					System.out.println("Impossible END");
				} else {
					Expand((Block) OpenList.get(0));
				}
				// System.out.println(ClosedList.toString());
				iteration++;
				input.run = false;
			}
		}

		if (reach && !finishPathFinding) {
			BackTracking();
			finishPathFinding = true;
			System.out.println(ShortPath.toString());
		}
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);// triple buffering
			return;
		}
		screen.clear();
		screen.render();

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, (int) (getWidth() * 1), (int) (getHeight() * 1), null);
		// Every time drawing, so we have to clean up the graphics
		g.setFont(new Font("TimesRoman", Font.BOLD, FontHeight));

		// for (int i = 0; i < wall.size(); i++) {
		// g.drawString("wall", ((Block) wall.get(i)).getX()*cellH, ((Block)
		// wall.get(i)).getY()*cellH);
		// }
		if (cellW >= 40) {
			Block temp;
			for (int i = 0; i < OpenList.size(); i++) {
				temp = ((Block) OpenList.get(i));
				if (!((temp.getX() - AvailableX) >= horCells || (temp.getY() - AvailableY) > verCells
						|| (temp.getX() - AvailableX) < 0 || (temp.getY() - AvailableY) < 0)) {
					g.drawString(temp.toString(2), (temp.getX() - AvailableX) * cellH + 3,
							(temp.getY() - AvailableY) * cellH + cellH - 2);
					g.drawString(temp.toString(1), (temp.getX() - AvailableX) * cellH + 3,
							(temp.getY() - AvailableY) * cellH + cellH - 10);
					g.drawString(temp.toString(0), (temp.getX() - AvailableX) * cellH + 3,
							(temp.getY() - AvailableY) * cellH + cellH - 30);
				}
			}
			for (int i = 0; i < ClosedList.size(); i++) {
				temp = ((Block) ClosedList.get(i));
				if (!((temp.getX() - AvailableX) >= horCells || (temp.getY() - AvailableY) > verCells
						|| (temp.getX() - AvailableX) < 0 || (temp.getY() - AvailableY) < 0)) {
					g.drawString(temp.toString(2), (temp.getX() - AvailableX) * cellH + 3,
							(temp.getY() - AvailableY) * cellH + cellH - 2);
					g.drawString(temp.toString(1), (temp.getX() - AvailableX) * cellH + 3,
							(temp.getY() - AvailableY) * cellH + cellH - 10);
					g.drawString(temp.toString(0), (temp.getX() - AvailableX) * cellH + 3,
							(temp.getY() - AvailableY) * cellH + cellH - 30);
				}
			}
		}
		g.setFont(new Font("TimesRoman", Font.BOLD, 14));
		g.drawString("Iteration: " + iteration, MainWidth + 30, 150);

		g.drawString("Screen Coor: [" + AvailableX + "," + AvailableY + "]", MainWidth + 30, 200);

		g.drawString("Checked box #: " + (ClosedList.size() + OpenList.size()), MainWidth + 30, 250);
		g.drawString("Steps Required#: " + (ShortPath.size() - 1), MainWidth + 30, 300);

		g.drawString("Algorithm Timer#: " + timerf/1000+"."+timerf%1000, MainWidth + 30, 350);

		g.dispose();
		bs.show();
	}

	public void Expand(Block current) {
		if (current.getD() == 0) {
			reach = true;
			ClosedList.add(current);
		} else {
			Block temp = null;
			ClosedList.add(current);
			for (int i = -1; i <= 1; i++) {
				for (int i1 = -1; i1 <= 1; i1++) {
					int c = CostD;
					if (i * i1 == 0) {
						c = CostS;
					}

					temp = new Block(new Point(current.getCoor().x - i, current.getCoor().y - i1), c + current.getC());
					temp.setParent(current);
					OpenList.buildPath(temp);
				}
			}
			OpenList.remove(current);
		}
	}

	private void BackTracking() {
		Block current = (Block) ClosedList.get(ClosedList.search(End));
		ShortPath.add(current);
		do {
			current = current.getParent();
			ShortPath.add(current);
		} while (!current.getCoor().equals(Start.getCoor()));
	}

	static int FontHeight = 10;

	public static void Scale(double scale) {
		System.out.println(scale);

		cellW = (int) (scale);
		cellH = (int) (scale);
		horCells = MainWidth / cellW;
		verCells = height / cellH;
		System.out.println(horCells + "|" + cellW);

	}

}
