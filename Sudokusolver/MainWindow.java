import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class MainWindow extends javax.swing.JFrame implements MouseListener, ActionListener {
	private static final long serialVersionUID = -7018142967304183953L;
	
	private static JPopupMenu popupMenu = null;
	private JLabel clickedLabel = null;
	private class Cell extends HashSet<Integer>{
		private static final long serialVersionUID = 6834502541443111674L;
		public Cell() {
			add(1);
			add(2);
			add(3);
			add(4);
			add(5);
			add(6);
			add(7);
			add(8);
			add(9);
		}
		private int GetFirst() {
			return iterator().next();
		}
	}; 
	private Cell grid[][] = new Cell[9][9];
	private JPanel gridPanel = new JPanel();
	private JLabel labels[][] = new JLabel[9][9];
	
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Build popup menu from "1" to "9"
		popupMenu = new JPopupMenu();
		JPopupMenu removeMenu = new JPopupMenu("Remove");
		popupMenu.add(removeMenu);
		for (int i=1; i<=9; ++i) {
			JMenuItem menuItem = new JMenuItem(Integer.toString(i));
			JMenuItem removeItem = new JMenuItem(Integer.toString(i));
			popupMenu.add(menuItem);
			removeMenu.add(removeItem);
			menuItem.addActionListener(this);
		}
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{69, 219, 0};
		gridBagLayout.rowHeights = new int[]{245, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new GridLayout(0, 1, 0, 0));
		GridBagConstraints gbc_actionPanel = new GridBagConstraints();
		gbc_actionPanel.anchor = GridBagConstraints.NORTH;
		gbc_actionPanel.insets = new Insets(0, 0, 0, 5);
		gbc_actionPanel.gridx = 0;
		gbc_actionPanel.gridy = 0;
		getContentPane().add(actionPanel, gbc_actionPanel);
		
		JButton btnNewButton_1 = new JButton("Reset");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InitUI();
				PrintGrid();
			}
		});
		actionPanel.add(btnNewButton_1);
		
		gridPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_gridPanel = new GridBagConstraints();
		gbc_gridPanel.fill = GridBagConstraints.BOTH;
		gbc_gridPanel.gridx = 1;
		gbc_gridPanel.gridy = 0;
		getContentPane().add(gridPanel, gbc_gridPanel);
		gridPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		// Build UI grid
		InitUI();
		setSize(308, 339);
		setTitle("Sudoku solver");
	}
	
	private void InitUI() {
		gridPanel.removeAll();
		for (int block=0; block<9; ++block) {
			JPanel p = new JPanel(new GridLayout(3, 3, 0, 0));
			p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			gridPanel.add(p);
			for (int cell=0; cell<9; ++cell) {
				
				JLabel l = new JLabel();
				int x = block%3*3 + cell%3;
				int y = block/3*3 + cell/3;
				labels[x][y] = l;
				l.setName(Integer.toString(block) + ":" + Integer.toString(cell) +
						"=" + x + "," + y );
				l.setBorder(BorderFactory.createLineBorder(Color.black, 1));
				l.setHorizontalAlignment(SwingConstants.CENTER);
				l.setFont(new Font("Arial", Font.PLAIN, 8));
				l.addMouseListener(this);
				p.add(l);
				l.add(popupMenu);
			}
		}
		
		// Build data structures
		for (int x=0; x<9; ++x) {
			for (int y=0; y<9; ++y) {
				grid[x][y] = new Cell();
			}
		}
		PrintGrid();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainWindow main = new MainWindow();
		main.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
		JLabel l = (JLabel) e.getComponent();
		
		// Adaptive pop up menu
		int x = Integer.parseInt(l.getName().substring(4, 5));
		int y = Integer.parseInt(l.getName().substring(6, 7));
		for (int i=0; i<popupMenu.getComponentCount(); ++i) {
			if (popupMenu.getComponent(i) instanceof JPopupMenu) {
				continue;
			}
			JMenuItem mi = (JMenuItem) popupMenu.getComponent(i);
			if (grid[x][y].contains(Integer.parseInt(mi.getText()))) {
				mi.setVisible(true);
			}
			else {
				mi.setVisible(false);
			}
		}
		popupMenu.show(l, e.getX(), e.getY());
		clickedLabel = l;
	}

	public void actionPerformed(ActionEvent e) {
		clickedLabel.setText(e.getActionCommand());
		int x = Integer.parseInt(clickedLabel.getName().substring(4, 5));
		int y = Integer.parseInt(clickedLabel.getName().substring(6, 7));
		AddNumber(Integer.parseInt(e.getActionCommand()), x, y, false);
		for (int i=0; i<9; ++i)
			CountNumbers(i, i);
		for (int i=0; i<9; ++i)
			CheckPairs(i, i);
		PrintGrid();
	}
	
	private void AddNumber(int number, int x, int y, boolean automatic) {
		grid[x][y].clear();
		grid[x][y].add(number);
		if (automatic)
			labels[x][y].setForeground(Color.BLUE);
		
		// Remove same numbers from lines
		for (int i=0; i<9; ++i) {
			// col
			if (i != y)
				RemoveNumber(number, x, i);
			// row
			if (i != x)
				RemoveNumber(number, i, y);
		}
	
		// Remove same numbers from block
		for (int dx=x/3*3; dx<x/3*3+3; ++dx) {
			for (int dy=y/3*3; dy<y/3*3+3; ++dy) {
				if ( (dx==x) && (dy==y) ) continue;
				RemoveNumber(number, dx, dy);
			}
		}
		PrintGrid();
	}
	
	private void RemoveNumber(int number, int x, int y) {
		if (grid[x][y].contains(number) && (grid[x][y].size() > 1) ) {
			grid[x][y].remove(number);
			CountNumbers(x, y);
			if (grid[x][y].size() == 1)
				AddNumber(grid[x][y].GetFirst(), x, y, true);
		}		
	}
	
	private void CheckPairs(int x, int y) {

		// row
		for (int first=0; first<8; ++first) {
			if (grid[first][y].size() == 2) {
				for (int second=first+1; second<9; ++second) {
					if (grid[second][y].equals(grid[first][y])) {
						for (int rm=0; rm<9; ++rm) {
							if ( (rm != first) && (rm != second) &&	(grid[rm][y].size() > 1) ) {
								grid[rm][y].removeAll(grid[first][y]);
								if (grid[rm][y].size() == 1)
									AddNumber(grid[rm][y].GetFirst(), rm, y, true);
							}
						}
					}
				}
			}
		}
		
		// col
		for (int first=0; first<8; ++first) {
			if (grid[x][first].size() == 2) {
				for (int second=first+1; second<9; ++second) {
					if (grid[x][second].equals(grid[x][first])) {
						for (int rm=0; rm<9; ++rm) {
							if ( (rm != first) && (rm != second) && (grid[x][rm].size() > 1) ) {
								grid[x][rm].removeAll(grid[x][first]);
								if (grid[x][rm].size() == 1)
									AddNumber(grid[x][rm].GetFirst(), x, rm, true);
							}
						}
					}
				}
			}
		}
		
		// block
		Cell first = null;
		int firstx = -1;
		int firsty = -1;
		for (int dx=x/3*3; dx<x/3*3+3; ++dx) {
			for (int dy=y/3*3; dy<y/3*3+3; ++dy) {
				if (grid[dx][dy].size() == 2) {
					if (first == null)  
						first = grid[dx][dy];
					else if (grid[dx][dy].equals(first)) {
						for (int rx=x/3*3; rx<x/3*3+3; ++rx) {
							for (int ry=y/3*3; ry<y/3*3+3; ++ry) {
								if ( (rx !=  dx) && (ry != dy) && 
										(rx != firstx) && (ry != firsty) ) {
									grid[rx][ry].removeAll(first);
									if (grid[rx][ry].size() == 1)
										AddNumber(grid[rx][ry].GetFirst(), rx, ry, true);
								}
								return;
							}
						}
					}						
				}
			}
		}
	}

	private void CountNumbers(int x, int y) {
		for (int number = 1; number<=9; ++number) {
			int rowCounter = 0;
			int rowPlace = -1;
			int colCounter = 0;
			int colPlace = -1;
			
			for (int i=0; i<9; ++i) {
				if (grid[i][y].contains(number)) {
					++rowCounter;
					rowPlace = i;
				}
				if (grid[x][i].contains(number)) {
					++colCounter;
					colPlace = i;
				}
			}
			if ( (rowCounter == 1) && (grid[rowPlace][y].size() > 1) )
				AddNumber(number, rowPlace, y, true);
			if ( (colCounter == 1) && (grid[x][colPlace].size() > 1) ) 
				AddNumber(number, x, colPlace, true);
		
			// block
			int blockCounter = 0;
			int blockx = -1;
			int blocky = -1;
			for (int dx=x/3*3; dx<x/3*3+3; ++dx) {
				for (int dy=y/3*3; dy<y/3*3+3; ++dy) {
					if (grid[dx][dy].contains(number)) {
						++blockCounter;
						blockx = dx;
						blocky = dy;
					}
				}
			}
			if ( (blockCounter == 1) && (grid[blockx][blocky].size() > 1) )
				AddNumber(number, blockx, blocky, true);
		}
	}
	
	private void PrintGrid() {
		for (int y=0; y<9; ++y) {
			for (int x=0; x<9; ++x) {
				labels[x][y].setText("<html>" + grid[x][y].toString().replace("[", "").replace("]", "").replace(",", ""));
				if (grid[x][y].size() == 1) {
					labels[x][y].setFont(new Font("Arial", Font.BOLD, 12));
				}
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
