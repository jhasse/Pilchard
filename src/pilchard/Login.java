package pilchard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;




public class Login extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPane;
	private final JTextField url;
	private final JTextField password;
	private final JTextField username;
	private final Preferences prefs;
	private final JButton btnConnect;


	public Login() {
		setTitle("Login");
		prefs = Preferences.userRoot().node(this.getClass().getName());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), },
				new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("default:grow"), }));

		JLabel lblUrl = new JLabel("URL:");
		contentPane.add(lblUrl, "2, 2, left, default");

		url = new JTextField();
		contentPane.add(url, "4, 2, fill, default");
		url.setColumns(10);
		url.setText(prefs.get("url", ""));
		url.addActionListener(this);

		JLabel lblUsername = new JLabel("Username:");
		contentPane.add(lblUsername, "2, 4, left, default");

		username = new JTextField();
		contentPane.add(username, "4, 4, fill, default");
		username.setColumns(10);
		username.setText(prefs.get("username", ""));
		username.addActionListener(this);

		JLabel lblPassword = new JLabel("Password:");
		contentPane.add(lblPassword, "2, 6, left, default");

		password = new JPasswordField();
		contentPane.add(password, "4, 6, fill, default");
		password.setColumns(10);
		password.addActionListener(this);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		contentPane.add(btnConnect, "2, 8, 3, 1, default, bottom");

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					btnConnect.setEnabled(false);
					prefs.put("url", url.getText());
					prefs.put("username", username.getText());
					Main.main.connect(url.getText(), username.getText(), password.getText());
					setVisible(false);
				} catch (Exception e) {
					Main.print(e.toString());
				} finally {
					btnConnect.setEnabled(true);
				}
			}
		};
		thread.start();
	}
}
