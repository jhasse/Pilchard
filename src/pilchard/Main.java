package pilchard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;




public class Main extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode();
	private final Sardine sardine = SardineFactory.begin();
	private final JTree serverTree;
	private String url;


	public static String connect(String path1, String path2) {
		if (path1.endsWith("/")) {
			return path1 + path2;
		}
		return path1 + "/" + path2;
	}

	private void scanPath(DefaultMutableTreeNode node, String path) throws IOException {
		System.out.println(path);
		List<DavResource> resources = sardine.list(path);
		boolean first = true;
		for (DavResource res : resources) {
			if (first) {
				first = false;
				continue;
			}
			DefaultMutableTreeNode newNode = new FileNode(res.getName(), res.isDirectory(), path, sardine);
			node.add(newNode);
			if (res.isDirectory()) {
				scanPath(newNode, connect(path, res.getName()));
			}
		}
	}

	public Main() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		setTitle("Pilchard WebDAV Client");
		setSize(630, 480);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		// add MouseListener to tree
		MouseAdapter ma = new MouseAdapter() {
			private void myPopupEvent(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				JTree tree = (JTree) e.getSource();
				TreePath path = tree.getPathForLocation(x, y);
				if (path == null)
					return;

				tree.setSelectionPath(path);

				final FileNode node = (FileNode) path.getLastPathComponent();

				JPopupMenu popup = new JPopupMenu();

				JMenuItem item = new JMenuItem("New folder");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						node.createDirectory();
					}
				});
				popup.add(item);

				item = new JMenuItem("Delete");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						node.delete();
					}
				});
				popup.add(item);
				popup.show(tree, x, y);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					myPopupEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					myPopupEvent(e);
			}
		};

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"), },
				new RowSpec[] {
						RowSpec.decode("40px"),
						RowSpec.decode("default:grow"), }));

		JButton btnReload = new JButton("Reload");
		btnReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					reload();
				} catch (Exception e) {
					Main.print(e.toString());
				}
			}
		});
		panel_1.add(btnReload, "1, 1, center, default");

		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "1, 2, fill, fill");


		serverTree = new JTree(serverNode);
		scrollPane.setViewportView(serverTree);
		serverTree.addMouseListener(ma);
		serverTree.setDragEnabled(true);
		serverTree.setDropMode(DropMode.ON);
		serverTree.setTransferHandler(new FileTransferHandler());


		new Login();
	}

	public void remove(FileNode node) {
		node.removeFromParent();
		serverTree.updateUI();
	}

	public void reload() throws IOException {
		serverNode.removeAllChildren();
		scanPath(serverNode, url);
		serverTree.expandRow(0);
		serverTree.updateUI();
	}

	public void connect(String url, String name, String pw) throws IOException {
		sardine.setCredentials(name, pw);
		this.url = url;
		reload();
		setVisible(true);
	}


	public static Main main;


	public static void main(String[] args) {
		main = new Main();
	}


	public static void print(String s) {
		StringBuilder sb = new StringBuilder();
		String[] lines = s.split("\n");
		for (String line : lines) {
			String[] words = line.split(" ");
			int i = 0;
			int rowLength = 0;
			while (i < words.length)
			{
				if (rowLength > 50)
				{
					sb.append("\n");
					rowLength = 0;
				}
				rowLength = rowLength + words[i].length() + 1;
				sb.append(words[i]).append(" ");
				i++;
			}
			sb.append("\n");
		}
		JOptionPane.showMessageDialog(Main.main, sb.toString());
	}
}
