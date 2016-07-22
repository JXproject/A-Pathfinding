import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class List extends ArrayList {
	public List() {
		super();
	}

	public void buildWall(Block block) {
		if (search(block) == -1) {
			boolean pass = false;
			if (Game.Start != null) {
				if (block.getCoor().equals(Game.Start.getCoor())) {
					pass = true;
				}
			}

			if (Game.wall.size() > 0) {
				if (Game.wall.search(block) != -1) {
					pass = true;
				}
			}
			if (Game.End != null) {
				if (block.getCoor().equals(Game.End.getCoor())) {
					pass = true;
				}
			}
			if (!pass) {
				this.add(block);
				System.out.println("Created!");
			}
		}
	}

	public void buildPath(Block block) {
		if (search(block) == -1) {
			boolean pass = false;

			if (Game.wall.size() > 0) {
				if (Game.wall.search(block) != -1) {
					pass = true;
				}
			}
			if (Game.ClosedList.size() > 0) {
				if (Game.ClosedList.search(block) != -1) {
					pass = true;
				}
			}
			if (Game.Start != null&&this.size()!=0) {
				if (block.getCoor().equals(Game.Start.getCoor())) {
					pass = true;
				}
			}

			if (!pass) {
				this.add(block);
				System.out.println("Created!");
			}
		}else{
			int i=search(block);
			Block previous=(Block) this.get(i);
			if(previous.getC()>block.getC()){
				this.remove(i);
				this.add(block);
//				((Block) this.get(i)).setParent(block.Parent);
			}
		}
	}

	public void remove(Block block) {
		int n = search(block);
		if (n != -1) {
//			System.out.println("Before!\n" +this.toString());
			this.remove(n);
//			System.out.println("Removed!\n" +this.toString());
		} else {
			System.out.println("No block!");
		}

	}

	public int search(Block block) {
		int n = -1;
		for (int i = 0; i < this.size(); i++) {
			if (((Block) this.get(i)).getCoor().equals(block.getCoor())) {
				n = i;
				break;
			}
		}
		return n;
	}

	public String toString() {
		String N = "List:\n";
		for (int i = 0; i < this.size(); i++) {
			N += i+"-"+((Block) this.get(i)).getCoor().toString() + "\n";
		}
		return N;
	}

	public void sortF() {
		Block[] temp = new Block[this.size()];
		for (int i = 0; i < this.size(); i++) {
			temp[i] = (Block) this.get(i);
		}
		mergeSort(temp, 0, temp.length - 1);
		this.clear();
		for (int i = 0; i < temp.length; i++) {
			this.add(temp[i]);
		}
	}

	public static void mergeSort(Block data[], int begin, int end) {
		int low = begin;
		int high = end;
		if (low >= high) {
			return;
		}
		int middle = (low + high) / 2;
		mergeSort(data, low, middle);
		mergeSort(data, middle + 1, high);
		int end_left = middle;
		int start_right = middle + 1;
		while ((begin <= end_left) && (start_right <= high)) {
			if (data[low].getF() < data[start_right].getF()) {
				low++;
			} else {
				Block Temp = data[start_right];
				for (int k = start_right - 1; k >= low; k--) {
					data[k + 1] = data[k];
				}
				data[low] = Temp;
				low++;
				end_left++;
				start_right++;
			}
		}
	}

}
