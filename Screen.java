import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;

//Class display pixels
public class Screen {
	public boolean ShowIndicator = true;
	private int width, height, MainWidth;
	private int Edge = 50;
	public int[] pixels;
	public int[] p;
	public int timer = 0;

	public Screen(int width, int height, int MainWidth) {
		this.height = height;
		this.width = width;
		this.MainWidth = MainWidth;
		pixels = new int[width * height];
		p = new int[(MainWidth + 2 * Edge) * (height + 2 * Edge)];
	}

	int lineColor = 0;

	public void render() {
		Arrays.fill(p, 0xf5f5f5);
		// System.out.println("R---!!!");
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[x + y * width] = 0xf5f5f5;
			}
		}
		if (Game.End != null) {
			Point coors = (Game.End).getCoor();
			if (coors.getX() >= (Game.AvailableX - Edge / Game.cellW)
					&& coors.getX() < (Game.horCells + Game.AvailableX + Edge / Game.cellW)
					&& coors.getY() >= (Game.AvailableY - Edge / Game.cellW)
					&& coors.getY() < (Game.verCells + Game.AvailableY + Edge / Game.cellW)) {

				CellAlive(Game.End.getX() - Game.AvailableX, Game.End.getY() - Game.AvailableY,
						(Color.BLUE).hashCode());

			}
		}

		// Wall
		for (int i = 0; i < Game.wall.size(); i++) {
			Point coor = ((Block) Game.wall.get(i)).getCoor();
			if (coor.getX() >= (Game.AvailableX - Edge / Game.cellW)
					&& coor.getX() < (Game.horCells + Game.AvailableX + Edge / Game.cellW)
					&& coor.getY() >= (Game.AvailableY - Edge / Game.cellW)
					&& coor.getY() < (Game.verCells + Game.AvailableY + Edge / Game.cellW)) {
				CellAlive(coor.x - Game.AvailableX, coor.y - Game.AvailableY, 0);
			}
		}

		// Path Open
		for (int i = 0; i < Game.OpenList.size(); i++) {
			Point coor = ((Block) Game.OpenList.get(i)).getCoor();

			if (coor.getX() >= (Game.AvailableX - Edge / Game.cellW)
					&& coor.getX() < (Game.horCells + Game.AvailableX + Edge / Game.cellW)
					&& coor.getY() >= (Game.AvailableY - Edge / Game.cellW)
					&& coor.getY() < (Game.verCells + Game.AvailableY + Edge / Game.cellW)) {
				int c = Color.CYAN.hashCode();

				if (coor.equals(Game.Start.getCoor())) {
					c = Color.RED.hashCode();
				} else if (coor.equals(Game.End.getCoor())) {
					c = Color.BLUE.hashCode();
				}

				CellAlive(coor.x - Game.AvailableX, coor.y - Game.AvailableY, c);

				if (ShowIndicator) {
					///// =========////
					Point coorP = ((Block) Game.OpenList.get(i)).getParent().getCoor();
					int x = -coor.x + coorP.x;
					int y = -coor.y + coorP.y;
					ParentCursour(coor.x - Game.AvailableX, coor.y - Game.AvailableY, Color.YELLOW.hashCode(), x, y);
				}
			}
		}

		// Path Closed
		for (int i = 0; i < Game.ClosedList.size(); i++) {
			Point coor = ((Block) Game.ClosedList.get(i)).getCoor();

			if (coor.getX() >= (Game.AvailableX - Edge / Game.cellW)
					&& coor.getX() < (Game.horCells + Game.AvailableX + Edge / Game.cellW)
					&& coor.getY() >= (Game.AvailableY - Edge / Game.cellW)
					&& coor.getY() < (Game.verCells + Game.AvailableY + Edge / Game.cellW)) {

				int c = Color.GREEN.hashCode();

				if (coor.equals(Game.Start.getCoor())) {
					c = Color.RED.hashCode();
				} else if (coor.equals(Game.End.getCoor())) {
					c = Color.BLUE.hashCode();
				}

				CellAlive(coor.x - Game.AvailableX, coor.y - Game.AvailableY, c);

				if (ShowIndicator) {
					///// =========////
					Point coorP = ((Block) Game.ClosedList.get(i)).getParent().getCoor();
					int x = -coor.x + coorP.x;
					int y = -coor.y + coorP.y;
					ParentCursour(coor.x - Game.AvailableX, coor.y - Game.AvailableY, Color.YELLOW.hashCode(), x, y);
				}
			}
		}

		// ShortPath
		for (int i = 0; i < Game.ShortPath.size(); i++) {
			Point coor = ((Block) Game.ShortPath.get(i)).getCoor();

			if (coor.getX() >= (Game.AvailableX - Edge / Game.cellW)
					&& coor.getX() < (Game.horCells + Game.AvailableX + Edge / Game.cellW)
					&& coor.getY() >= (Game.AvailableY - Edge / Game.cellW)
					&& coor.getY() < (Game.verCells + Game.AvailableY + Edge / Game.cellW)) {

				int c = Color.ORANGE.hashCode();

				if (coor.equals(Game.Start.getCoor())) {
					c = Color.RED.hashCode();
				} else if (coor.equals(Game.End.getCoor())) {
					c = Color.BLUE.hashCode();
				}

				CellAlive(coor.x - Game.AvailableX, coor.y - Game.AvailableY, c);

				if (ShowIndicator) {
					///// =========////
					Point coorP = ((Block) Game.ShortPath.get(i)).getParent().getCoor();
					int x = -coor.x + coorP.x;
					int y = -coor.y + coorP.y;
					ParentCursour(coor.x - Game.AvailableX, coor.y - Game.AvailableY, Color.YELLOW.hashCode(), x, y);
				}
			}
		}

		// Transfer into Screen pixel
		for (int i = 0; i < MainWidth; i++) {
			for (int ii = 0; ii < height; ii++) {
				pixels[i + ii * width] = p[Edge + i + (ii + Edge) * (MainWidth + 2 * Edge)];
			}
		}

		// Grids
		if (Game.input.Grid) {
			for (int nw = 0; nw < MainWidth / Game.cellW + 1; nw++) {
				for (int x = 1; x < height; x++) {
					pixels[nw * Game.cellW + x * width] = lineColor;
				}
				for (int y = 1; y < MainWidth; y++) {
					if (y + nw * Game.cellH * width < pixels.length)
						pixels[y + nw * Game.cellH * width] = lineColor;
				}
			}
		}

		// INDICATOR
		int color = 0;
		if (Game.input.ControlScreenDrag) {
			color = (Color.GREEN).hashCode();
		} else if (Game.input.run && !Game.input.ShiftToggle) {
			color = (Color.RED).hashCode();
		} else if (Game.input.ShiftToggle) {
			color = (Color.BLUE).hashCode();
		}
		for (int x = 0; x < MainWidth; x++) {
			pixels[x] = color;
			pixels[x + (height - 1) * width] = color;
			pixels[x * width + MainWidth] = color;
			pixels[x * width] = color;
		}

	}

	public void CellAlive(int x, int y, int color) {
		for (int i = 0; i < Game.cellW; i++) {
			for (int ih = 0; ih < Game.cellH; ih++) {
				p[x * Game.cellW + Edge + i + (y * Game.cellH + ih + Edge) * (MainWidth + 2 * Edge)] = color;
			}
		}
	}

	public void ParentCursour(int x, int y, int color, int xc, int yc) {
		if (xc < 0) {
			xc = -1;
		}
		if (xc > 0) {
			xc = 1;
		}
		if (yc < 0) {
			yc = -1;
		}
		if (yc > 0) {
			yc = 1;
		}
		for (int i = 17; i < Game.cellW - 17; i++) {
			for (int ih = 17; ih < Game.cellH - 17; ih++) {
				p[x * Game.cellW + Edge + xc * 10 + i
						+ (y * Game.cellH + ih + Edge + yc * 10) * (MainWidth + 2 * Edge)] = color;
			}
		}
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;// clear to black
		}
	}

	public static Color ColorLighter(int ampp, int shiftCC) {
		int Frequency = 0;
		int center = 255 - ampp;
		int Y1 = (int) (Math.sin(Frequency + shiftCC) * ampp + center);
		// sin max=1 min=-1;begin at 0;neutral color: 128;
		// int v = (int) (Math.cos (frequency * a) * amp + center)
		// cos max=1;min=-1;begin at 1;start from 255;
		int Y2 = (int) (Math.sin(Frequency + 2 + shiftCC) * ampp + center);
		int Y3 = (int) (Math.sin(Frequency + 4 + shiftCC) * ampp + center);
		Color vvv1 = new Color(Y1, Y2, Y3);
		return (vvv1);
	}
}
