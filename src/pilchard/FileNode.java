package pilchard;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import com.googlecode.sardine.Sardine;




public class FileNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	private final Sardine sardine;
	private final String name;
	private final String path;
	private final boolean isDirectory;


	public FileNode(String name, boolean isDirectory, String path, Sardine sardine) {
		super(name, isDirectory);
		this.name = name;
		this.isDirectory = isDirectory;
		if (isDirectory) {
			path = Main.connect(path, name);
		}
		this.path = path;
		this.sardine = sardine;
	}

	public void uploadFiles(List<File> files) {
		for (File file : files) {
			try {
				InputStream fis = new FileInputStream(file);
				sardine.put(Main.connect(path, file.getName()), fis);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public void delete() {
		try {
			if (isDirectory) {
				sardine.delete(path);
			} else {
				sardine.delete(Main.connect(path, name));
			}
			Main.main.remove(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createDirectory() {
		try {
			String str = JOptionPane.showInputDialog(Main.main, "Directory name: ", "Create Directory", 1);
			sardine.createDirectory(Main.connect(path, str));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
