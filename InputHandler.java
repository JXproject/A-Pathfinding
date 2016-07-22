import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputHandler
		implements KeyListener, FocusListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static boolean Grid = true;
	public boolean run = false;
	public static int MouseX, MouseY;

	// public static boolean[] key = new boolean[68836];

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.isMetaDown()) {
			ShiftToggle = true;
		} else
			ShiftToggle = false;
		cellControl(e);
		sceneMap(e);

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.isMetaDown()) {
			ShiftToggle = true;
		} else
			ShiftToggle = false;
		cellControl(e);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		ShiftToggle = false;
		Preset = true;

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	boolean ShiftToggle = false, ControlScreenDrag = false, Preset = true, AutoRun = false;
	boolean startset = false, endset = false;

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_A) {
			AutoRun = !AutoRun;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			run = !run;
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			ControlScreenDrag = true;
			ShiftToggle = false;
			// run = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			startset = true;
		} else if (e.getKeyCode() == KeyEvent.VK_E) {
			endset = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			Game.timerf=0;
			Game.ClosedList.clear();
			Game.OpenList.clear();
			Game.ShortPath.clear();
			Game.finishPathFinding = false;
			Game.reach = false;
			AutoRun = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_L) {
			Game.timerf=0;
			Game.wall.clear();
			Game.End = null;
			Game.ClosedList.clear();
			Game.OpenList.clear();
			Game.ShortPath.clear();
			Game.finishPathFinding = false;
			Game.reach = false;
			AutoRun = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_F) {
			Game.Fastmode=true;
		}
		if (e.getKeyCode() == KeyEvent.VK_G) {
			Grid=!Grid;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			ControlScreenDrag = false;
		}
	}

	// Sub Methods
	public int initialX = 0, initialY = 0, initialAvailableX = 0, initialAvailableY = 0;

	public void sceneMap(MouseEvent e) {
		if (ControlScreenDrag && Preset) {
			initialX = e.getX();
			initialY = e.getY();
			initialAvailableX = Game.AvailableX;
			initialAvailableY = Game.AvailableY;
			Preset = false;
		}
		if (ControlScreenDrag) {
			Game.AvailableX = initialAvailableX + (int) ((initialX - e.getX()) / Game.cellW);
			Game.AvailableY = initialAvailableY + (int) ((initialY - e.getY()) / Game.cellH);
		}
	}

	public void cellControl(MouseEvent e) {
		Block wall = null;
		if (!run && !ShiftToggle && !ControlScreenDrag) {

			int x = (int) (e.getX() / Game.cellW) + Game.AvailableX;
			int y = (int) (e.getY() / Game.cellH) + Game.AvailableY;

			// System.out.println("Create: [" + x + "," + y + "]");
			if (x >= Game.AvailableX && x <= (Game.horCells + Game.AvailableX) && y >= Game.AvailableY
					&& y <= (Game.verCells + Game.AvailableY)) {
				wall = new Block(x, y);
				if (startset) {
					if (Game.End != null) {
						Game.Start = wall;
						Game.OpenList.clear();
						Game.Start.setParent(Game.Start);
						Game.OpenList.buildPath(Game.Start);
						if (Game.End != null) {
							Game.Start.setCD(0, wall.getDistance());
						}
					}
					startset = false;
				} else if (endset) {
					Game.End = wall;
					endset = false;
				} else {
					Game.wall.buildWall(wall);
				}
				Game.iteration = 0;
			}
			// Game.life.printOut();
		}
		if (ShiftToggle && !ControlScreenDrag) {
			int x = (int) (e.getX() / Game.cellW) + Game.AvailableX;
			int y = (int) (e.getY() / Game.cellH) + Game.AvailableY;

			// System.out.println("Delete: [" + x + "," + y + "]");
			if (x >= Game.AvailableX && x <= (Game.horCells + Game.AvailableX) && y >= Game.AvailableY
					&& y <= (Game.verCells + Game.AvailableY)) {
				wall = new Block(x, y);
				Game.wall.remove(wall);
				Game.iteration = 0;
			}
		}
		wall = null;
	}

	int rate = 10;

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		int x = initialAvailableX + (int) ((e.getX()) / Game.cellW);
		int y = initialAvailableY + (int) ((e.getY()) / Game.cellH);
		System.out.println(x + "==" + y);

		int wheel = -e.getWheelRotation();
		rate += wheel;

		System.out.println(rate);

		if (rate <= Game.MinScale) {
			rate = Game.MinScale;
		} else if (rate >= Game.MaxScale) {
			rate = Game.MaxScale;
		}
		Game.Scale(rate);

		int xf = initialAvailableX + (int) ((e.getX()) / Game.cellW);
		int yf = initialAvailableY + (int) ((e.getY()) / Game.cellH);

		Game.AvailableX += (x - xf);
		Game.AvailableY += (y - yf);

	}

}
