package Network;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;

import View.MainFrame;

public class Receiver{

	private MainFrame mainFrame;
	private ServerSocket s;
	
	private Socket client;
	private DataInputStream in;
	private BufferedWriter out;
	
	private volatile boolean running = false;
	
	private LinkedList<CanvasFrame> canvas = new LinkedList<>();
	
	public Receiver(int port, MainFrame mainFrame) {
		
		this.mainFrame = mainFrame;
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean connect(){
		System.out.println("Awaiting connection");
		try {
			client = s.accept();
			in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		} catch (IOException e1) {
			return false;
		}
		System.out.println("Connection established");
		return true;
	}
	
	
	public void run() {
//		Scanner sc = new Scanner(System.in);
//		while(client.isConnected()){
//			String command = sc.nextLine();
//			try {
//				out.write(command);
//				out.newLine();
//				out.flush();
//			} catch (IOException e) {
//				break;
//			}
//			if((command.equals("w0") || command.equals("w1") || command.equals("d")) && !running)
//				new Thread(new Runnable() {
//
//					public void run() {
//						startFrameTransfer();
//					}
//				
//				}).start();
//			
//			else if(command.equals("s0") || command.equals("s1")){
//				CanvasFrame frame = new CanvasFrame("Webcam Photo");
//				canvas.add(frame);
//				frame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
//				try {
//					receiveFrame(frame);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			else if(command.equals("stop")){
//				stopFrameTransfer();
//			}
//			else if(command.equals("exit")){
//				for(CanvasFrame frame : canvas)
//					frame.dispose();
//				stopFrameTransfer();
//				sc.close();
//				break;
//			}
//		}
	}
	
	public void sendCommand(String command){
		try {
			out.write(command);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exit(){
		sendCommand("exit");
		for(CanvasFrame frame : canvas)
			frame.dispose();
		stopFrameTransfer();
	}
	
	public void singleFrameTransfer(){
		CanvasFrame frame = new CanvasFrame("Webcam Photo");
		canvas.add(frame);
		frame.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);
		try {
			receiveFrame(frame);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startFrameTransfer(){
		running = true;
		CanvasFrame frame = new CanvasFrame("Webcam Capture");
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				sendCommand("stop");
				stopFrameTransfer();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		frame.getCanvas().addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				sendCommand("KEY:" + String.valueOf(e.getExtendedKeyCode()));
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
		frame.getCanvas().addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				Point p = e.getPoint();
				try {
					out.write(p.x + "|" + p.y + "|" + frame.getContentPane().getWidth() + "|" +frame.getContentPane().getHeight());
					out.newLine();
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}
		});
		
		while(running){
			try {
				receiveFrame(frame);
			} catch (IOException e) {
				e.printStackTrace();
				running = false;
			}
		}
		frame.dispose();
	}
	
	public void stopFrameTransfer(){
		running = false;
	}
	
	public boolean isFrameTransferActive(){
		return running;
	}
	
	public void receiveFrame(CanvasFrame frame) throws IOException{
		
		int size = in.readInt();
		if(size == -1){
			running = false;
			return;
		}
		
		byte[] img = new byte[size];
		in.readFully(img, 0, size);
		
		BufferedImage im = ImageIO.read(new ByteArrayInputStream(img));
				
		if(im != null && frame.isVisible())
			frame.showImage(im);
		
		img = null;
		im = null;
	}
}
