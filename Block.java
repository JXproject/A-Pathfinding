import java.awt.Point;

public class Block {
	private int ID, x, y, F, C, D = -1;
	public Block Parent = null;

	public Block(int x, int y, int C, int D) {
		ID = -1;
		this.x = x;
		this.y = y;
		this.C = C;
		this.D = D;
		F = C + D * 10;
	}

	public Block(int x, int y, int C) {
		ID = -1;
		this.x = x;
		this.y = y;
		this.C = C;
		getDistance();
		F = C + D * 10;
	}

	public Block(int x, int y) {
		ID = -1;
		this.x = x;
		this.y = y;
		this.F = -1;
		this.C = -1;
		this.D = -1;
	}

	public Block(Point coor, int C) {
		Parent = null;
		ID = -1;
		this.x = coor.x;
		this.y = coor.y;
		this.C = C;
		getDistance();
		F = C + D * 10;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getC() {
		return C;
	}

	public int getD() {
		return D;
	}

	public int getF() {
		return F;
	}

	public int getID() {
		return ID;
	}

	public Point getCoor() {
		return new Point(x, y);
	}

	public void setCD(int C, int D) {
		this.C = C;
		this.D = D;
		F = C + D * 10;
	}

	public String toString(int i) {
		if (i == 1) {
			return "C:" + C;
		} else if (i == 2) {
			return "D:" + D;
		} else if (i == 0) {
			return "F:" + F;
		} else {
			return "[" + x + "," + y + "]->" + "F:" + F + "|D:" + D + "|C:" + C;
		}
	}

	public int getDistance() {

		if (D == -1 && Game.End != null && Game.Start != null) {
			D = Math.abs(x - Game.End.getX()) + Math.abs(y - Game.End.getY());
		}

		// Testing
//		 if (D == -1 && Game.End != null && Game.Start != null) {
//		 int xyDif=Math.abs(Math.abs(x - Game.End.getX()) - Math.abs(y -
//		 Game.End.getY()));
//		 int xy=(Math.abs(x - Game.End.getX()) + Math.abs(y -
//		 Game.End.getY())-xyDif)/2;
//		 xyDif=Math.abs(x - Game.End.getX()) + Math.abs(y -
//		 Game.End.getY())-xy*2;
//		 D = (int) Math.sqrt(Math.pow(xy, 2.0)*2)+xyDif;
//		 System.out.println(xyDif+"|"+xy);
//		 }

		return D;
	}

	public Block getParent() {
		return Parent;

	}

	public void setParent(Block Parent) {
		this.Parent = Parent;
	}

}
