package pilchard;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;




public class FileTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;


	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		try {
			JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
			TreePath path = dropLocation.getPath();
			FileNode node = (FileNode) path.getLastPathComponent();
			node.uploadFiles((List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
