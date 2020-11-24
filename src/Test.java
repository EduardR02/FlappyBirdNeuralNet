import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import basicneuralnetwork.NeuralNetwork;


public class Test extends JPanel implements ActionListener,KeyListener,ChangeListener{
	
	private static final long serialVersionUID = 1L;
	static int speed = 16;	//60fps
	static int width = 800, height = 600;
	int counter = 0;
	Timer ti = new Timer(speed,this);
	int population = 250*2;
	LinkedList<Obstacle> obs;
	LinkedList<Bird> savedBirds;
	LinkedList<Bird> birds = new LinkedList<Bird>();
	static boolean gameover = true;
	static int score = 0;
	int generation = 0;
	JSlider sli = new JSlider(JSlider.HORIZONTAL,1,1000,1);;
	Color col = new Color(0,250,154);
	Bird fittest = null;
	Bird secondFittest = null;
	Bird secAllTime = null;
	Bird allTime = null;
	String fileName = "TryingToGetLowestSize";
	boolean loadSavedGen = true; 	//false for 1 bird, true for full population
	
	
	public static void main(String[] args) {
		Test l = new Test();
		JFrame f = new JFrame();
		f.setTitle("FlappyBird");
		f.setIconImage(new ImageIcon("FlappyBird2.png").getImage());
		f.setContentPane(l);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setResizable(false);
		
	}
	
	public Test() {
		setPreferredSize(new Dimension(width,height));
		setFocusable(true);								//for KeyListener		
		requestFocus();	
		addKeyListener(this);
		sli.setPaintLabels(true);
		sli.setBackground(new Color(23,23,23,0));
		sli.setForeground(col);
		sli.setFocusable(false);
		sli.setBounds(10,10,200,20);
		add(sli);
		setLayout(null);
		
		
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g2);
		setBackground(new Color(23,23,23));
		g2.setColor(new Color(255,70,70));
		g2.setFont(new Font("Courier New", Font.BOLD, 24));
		
		if (!gameover) {
			for(Obstacle k : obs) {
				k.show(g2);
			}
			for(int i = 0; i < savedBirds.size(); i++) {
			savedBirds.get(i).show(g2);
			}
			g2.setColor(col);
			g2.drawString("Score:" + score, width - 225, 25);
			g2.drawString("Generation:" + generation, width - 225, 50);
		}
		else {
			if(birds.size() == 1) {
				gameover = false;
				loadBirds(false);
			}
			else {
			gameover = false;
			setupLevel();
			}
		}
		
	}
	
	public static boolean testIntersection(Shape shapeA, Shape shapeB) {
		   Area areaA = new Area(shapeA);
		   areaA.intersect(new Area(shapeB));
		   return !areaA.isEmpty();
		}
	
	public void setupLevel() {
		if(!gameover) {
			generation++;
			obs = new LinkedList<Obstacle>();
			savedBirds = new LinkedList<Bird>();
			nextGen();
			counter = 130;
			score = 0;
			ti.start();
		}
	}
	
	public void loadBirds(boolean x) {
		gameover = false;
		if(x) {
			for(int i = 0; i < population/2; i++) {
				birds.push(fittest);
				birds.push(secondFittest);
			}
			setupLevel();	
		}
		else {
		generation = 0;
		obs = new LinkedList<Obstacle>();
		savedBirds = new LinkedList<Bird>();
		birds = new LinkedList<Bird>();
		birds.push(allTime);
		counter = 130;
		score = 0;
		setupLevel();
		}
		
	}
	
	public void nextGen() {
		for(Bird p : birds ) {
			if(p.birdScore < 215) {
				p.fitness = 0;
			}
		}
		calculateFitness();
		getFittestBirds();
		if(birds.size() == 1) {
			savedBirds.push(fittest);
		}
		else {
			for(int i = 0; i < population/2; i++) {
				savedBirds.push(pick1());
				savedBirds.push(pick2());
			}
		}
		birds.clear();
	}
	
	public void calculateFitness() {
		double sum = 0;
		for(Bird p : birds) {
			sum += p.birdScore;
		}
		for(Bird p : birds) {
			p.fitness = (p.birdScore/sum);
		}
	}
	
	public Bird pick1() {
		if(birds.size() != 0) {

			Bird child = new Bird(null);
			double de = Math.random();
			
			if	(de >= 0 && de < 0.2) {
				child.brain = fittest.brain.merge(secondFittest.brain, 0.8);
				child.mutate();
				return child;
			}
			else if(de >= 0.2 && de < 0.4) {
				child.brain = fittest.brain;
				child.mutate();
				return child;
			}
			else if(de >= 0.4 && de < 0.6) {
				child.brain = allTime.brain.merge(secondFittest.brain);
				child.mutate();
				return child;
			}
			else if(de >= 0.6 && de < 0.8) {
				child.brain = allTime.brain.merge(fittest.brain);
				child.mutate();
				return child;
			}
			else {		//if(de >= 0.75 && de < 1) but needs return statement so just else
				child.brain = secondFittest.brain.merge(fittest.brain);
				child.mutate();
				return child;
			}
		}
		else {
			Bird p = new Bird(null);
			return p;
		}
	}
	
	public Bird pick2() {
		if(birds.size() != 0) {
		int index = 0;
		double r = Math.random();
		while(r > 0) {
			r = r - birds.get(index).fitness;
			index++;
		}
		index--;
		Bird temp = birds.get(index);
		Bird child = new Bird(temp.brain);
		child.mutate();
		return child;
		}
		else {
			Bird p = new Bird(null);
			return p;
		}
		
		
	}
	
	public void getFittestBirds() {
		if(birds.size() != 0 && birds.size() != 1) {
			
			if(allTime == null && secAllTime == null) {
				allTime = birds.get(0);
				secAllTime = birds.get(1);
			}
			
			fittest = birds.get(0);
			secondFittest = birds.get(1);
			for (int l = 1; l < birds.size(); l++) {
				if (birds.get(l).fitness > fittest.fitness) {
					fittest = birds.get(l);
				}
				else {
					if(secondFittest.fitness < birds.get(l).fitness) {
						secondFittest = birds.get(l);
					}
				}
			}
			if (allTime.fitness < fittest.fitness) {
				secAllTime = allTime;
				allTime = fittest;
			}
			if (secAllTime.fitness < secondFittest.fitness)
				secAllTime = secondFittest;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!gameover) {
			for(int n = 0; n < sli.getValue(); n++) {
			if(counter >= 130) {
				Obstacle o = new Obstacle();
				obs.push(o);
				counter = 0;
			}
			
			for(int i = obs.size() - 1; i >= 0; i--) {
				obs.get(i).update();
				if(savedBirds.size() != 0) {
					for (int j = savedBirds.size() - 1; j >= 0; j--) {
						
						if(testIntersection(savedBirds.get(j).d,obs.get(i).square1) || testIntersection(savedBirds.get(j).d,obs.get(i).square2) || savedBirds.get(j).y > height - savedBirds.get(j).img.getHeight(null)) {
							birds.push(savedBirds.get(j));
							savedBirds.remove(j);
							if(savedBirds.size() == 0) {
								gameover = true;	
							}
						}
					}
					
				}
				else {
					gameover = true;
				}
				if(obs.get(i).x < (0 - obs.get(i).width*1.5)) {
					obs.remove(i);
				}
			}
			for(int i = 0; i < savedBirds.size(); i++) {
				savedBirds.get(i).update();
				savedBirds.get(i).think(obs);
			}
			counter ++;
			score++;
			}
			repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent t) {
		int k = t.getKeyCode();
		
		if (k == KeyEvent.VK_S) {
			if(allTime != null) {
				if(savedBirds.size() == 1) {
					allTime = savedBirds.get(0);
					allTime.brain.writeToFile(fileName);
					secAllTime.brain.writeToFile(fileName + "2");
				}
				else {
					allTime.brain.writeToFile(fileName);
					secAllTime.brain.writeToFile(fileName + "2");
				}
				System.out.println("saved");
			}
			else
			System.out.println("failed to save, fittest doesn't exist");
			
		}
		if(k == KeyEvent.VK_L) {
			NeuralNetwork nn = NeuralNetwork.readFromFile(fileName + ".json");
			NeuralNetwork nn2 = NeuralNetwork.readFromFile(fileName + "2" + ".json");
			fittest = new Bird(nn);
			secondFittest = new Bird(nn2);
			allTime = new Bird(nn);
			secAllTime = new Bird(nn2);
			generation = 0;
			loadBirds(loadSavedGen);
			System.out.println("loaded");
		}
		if(k == KeyEvent.VK_R) {
			gameover = false;
			generation = 0;
			if(savedBirds.size() != 0) {
				for(Bird p : savedBirds)
					birds.push(p);
				}
			setupLevel();
		}
		if(k == KeyEvent.VK_A) {
			sli.setValue(1);
		}
		if(k == KeyEvent.VK_D) {
			sli.setValue(1000);
		}
		if(k == KeyEvent.VK_C) {
			if(loadSavedGen)
				loadSavedGen = false;
			else
				loadSavedGen = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
