package launcher;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Random;
 
@SuppressWarnings("serial")
public class Splash extends JFrame {

	private static String downloadUrl = "http://world1.projectdream.co.uk/pd-launcher/ProjectDream.jar";
	private static String fileName = "ProjectDream.jar";
	private static String serverName = "Project Dream";
	private static String backgroundImageUrl = "3.png";
	private static String saveDirectory = System.getProperty("user.home")+"/";
	 
	public static URL url;
    private JLabel imglabel;
    private ImageIcon img;
    private static JProgressBar pbar;
    private static JButton pbutton;
    Thread t = null;
 
    public Splash() {
        super("Splash");
        
        File file = new File(saveDirectory + fileName);
        
		try {
			url = new URL(downloadUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		setSize(1140, 424);
		 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        
        try {
        	
        	Random rand = new Random();
        	int  n = rand.nextInt(4) + 1;
        	
			BufferedImage img = ImageIO.read(new URL
					("http://world1.projectdream.co.uk/player-portal/images/slider/"+ n +".png"));
			ImageIcon icon = new ImageIcon(img);
			JLabel label = new JLabel(icon);
			add(label, BorderLayout.CENTER);
			label.setBounds(0, 0, 1140, 424);
        } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
       
		
        setLayout(null);
        pbar = new JProgressBar();
        
        pbar.setMinimum(0);
        pbar.setMaximum(100);
        pbar.setStringPainted(true);
        pbar.setForeground(Color.LIGHT_GRAY);
        
        add(pbar);
        pbar.setPreferredSize(new Dimension(310, 30));
        pbar.setBounds(420, 370, 300, 30);


        try {
            if (file.exists()) {
            	URLConnection connection = url.openConnection();
            	connection.connect();
    			long time = connection.getLastModified();
    			if (time > file.lastModified()) {
                    if (!startDialogue()) {
                    	startApplication();
                        return;
                    }
    			} else {
                    setVisible(true);
                    Thread.sleep(1000);
                    startApplication();
                    return;
                }
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Thread t = new Thread() {
 
            public void run() {
            	OutputStream dest = null;
            	URLConnection download;
            	InputStream readFileToDownload = null;
            	try {
            		dest = new BufferedOutputStream(new FileOutputStream(saveDirectory + fileName)); 
            		download = url.openConnection();
            		readFileToDownload = download.getInputStream();
            		byte[] data = new byte[1024];
            		int numRead;
            		long numWritten = 0;
            		int length = download.getContentLength();
            		while ((numRead = readFileToDownload.read(data)) != -1) {
            			dest.write(data, 0, numRead);
            			numWritten += numRead;
            			int percent = (int)(((double)numWritten / (double)length) * 100D);
            			pbar.setValue(percent);
            			pbar.setString(""+(percent < 99 ? "Downloading "+serverName+" - "+percent+"%" : "Launching & Exiting..")+"");
            		}
            	} catch (Exception exception) {
            		exception.printStackTrace();
            	} finally {
            		try {
            			if (readFileToDownload != null)
            				readFileToDownload.close();
            			if (dest != null)
            				dest.close();
            			Thread.sleep(200L);
            			startApplication();
            		} catch (IOException | InterruptedException ioe) {
            				
            		}
            	}
            }
        };
        t.start();
    }
    
    public boolean startDialogue() {
        setVisible(true);
        int selection = JOptionPane.showConfirmDialog(null, "Project Dream has updated. Do you wish to download?", "Update Available", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        return selection == JOptionPane.OK_OPTION;
    }


    /**
     * Launches the downloaded Jar file and closes the progress bar
     */
    public static void startApplication() {
    	try {
			Runtime.getRuntime().exec("java -jar "+(saveDirectory + fileName)+"");
			Thread.sleep(200L);
			System.exit(0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
    }


}