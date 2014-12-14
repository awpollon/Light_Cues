import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class Editor {
	long editorTime;
	boolean isPlaying;
	JTextField timeText;
	JPanel p;
	JFrame f;

	public Editor() {
		editorTime = 0;


		f = new JFrame("Show Editor");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(50, 100, 300, 300);
		f.setVisible(true);

		p = new JPanel();
		p.setLayout(new BorderLayout());
		f.add(p);

		timeText = new JTextField("Time: " + editorTime);
		timeText.setEditable(false);
		p.add(timeText, BorderLayout.PAGE_START);
		
		JButton start = new JButton("Play");
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				handleButtonClick((JButton)e.getSource());
			}
		});
		p.add(start, BorderLayout.SOUTH);
		
		f.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e.getKeyChar()+ " pressed!");
			}

			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println(e.getKeyChar()+ " released!");

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}


		});

		f.validate();

	}

	protected void handleButtonClick(JButton b) {
		if(b.getText() == "Play") {
			startTimer();
			b.setText("Stop");
		}
		else if (b.getText() == "Stop") {
			stopTimer();
			b.setText("Play");
		}
	}

	private void stopTimer() {
		isPlaying = false;
	}

	void startTimer() {
        (new Thread(new Timer(this))).start();
	}


	void updateTime() {
		timeText.setText("Time: " + editorTime);		
	}

	public static void main(String[] args) {
		Editor e = new Editor();
	}
	public boolean writeFile(Song s){
		try {

			File file = new File(s.getTitle());
			if (!file.exists()) {
				if (file.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
			file = new File(s.getTitle() +"/"+s.getTitle()+".txt");

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			//Intro comment
			bw.append("/*\n");
			bw.append("This code has been generated by Song_Creater.java and is intended for an Arduino. \n Created by Aaron Pollon\n");
			bw.append("*/\n\n");
			//Setup method
			bw.append("void setup() {\n");
			Channel chs[] = s.getChannels();

			for(int i=0; i<chs.length; i++) {
				if(chs[i] != null){
					Channel c = chs[i];
					bw.append("pinMode(" + c.getArduinoPin() +", OUTPUT);\n");
				}
			}
			bw.append("}\n\n");

			//Loop
			bw.append("void loop() {\n");

			ArrayList<Cue> qs = s.getCues();

			for(int i=0; i<qs.size(); i++) {
				Cue c = qs.get(i);
				for(int j=0; j<c.getEvents().size(); j++) {
					LightEvent e = c.getEvents().get(j);
					bw.append("digitalWrite(" +e.channel.getChNum() +", ");
					if(e.on){
						bw.append("HIGH);");
					}
					else {
						bw.append("LOW);");
					}
					//Go to next line
					bw.append("\n");
				}
				//After each cue, place a delay equal to difference in timing
				//Check if there is another cue
				if(i<qs.size()-1) {
					double delayTime = qs.get(i+1).getRunTime() - c.getRunTime();
					bw.append("delay(" + delayTime +");\n");
				}
			}

			//End loop
			bw.append("}");


			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}