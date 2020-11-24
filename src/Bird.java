import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import basicneuralnetwork.NeuralNetwork;

public class Bird {

	int y,x,birdScore;
	double dy, fitness;
	Rectangle2D d;
	Image img = Toolkit.getDefaultToolkit().getImage("FlappyBird2.png");
	NeuralNetwork brain;
	
	public Bird(NeuralNetwork smart) {
		y = Test.height/2;
		x = Test.width/2;
		dy = -10;
		birdScore = 0;
		fitness = 0;
		d = new Rectangle2D.Double(x, y, img.getWidth(null), img.getHeight(null));
		if(smart != null) {
			brain = smart.copy();
		}
		else {
		brain = new NeuralNetwork(7,8,2);
		}
		
	}
	
	public void show(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(244,22,44));
		g2.drawImage(img, x, y, null);
	}
	
	public void update() {
		dy += 0.4;
		y += dy;
		birdScore += 1;
		if(dy < -12)
			dy = -12;
		if(dy > 12)
			dy = 12;
		if(y <= 0)
			y = 0;
		this.d = new Rectangle2D.Double(x, y, img.getWidth(null), img.getHeight(null));
	}
	
	public void up() {
		dy = -10;
	}
	
	public void mutate() {
		brain.mutate(0.1);
	}
	
	public void think(LinkedList<Obstacle> li) {
		
		//closest Pipe
		Obstacle closest = null;
		int closestD = 100000;
		int closestD2 = 100000;
		for(int i = 0; i < li.size(); i++) {
			int d = (int) (li.get(i).x + li.get(i).width*1.5 - this.x);
			if(d < closestD && d > 0) {
				closest = li.get(i);
				closestD = d;
				closestD2 = closestD - li.get(i).width - img.getWidth(null);
				if(closestD2 <= 0) {
					closestD2 = 0;
				}
			}
		}
		
		double[] inputs = new double[7];
		inputs[0] = (double) y / Test.height;
		inputs[1] = (double) (closest.y - closest.gapsize) / Test.height;
		inputs[2] = (double) (closest.y - img.getHeight(null)) / Test.height;
		inputs[3] = (double) closestD / Test.width;
		inputs[4] = (double) closestD2 / Test.width;
		inputs[5] = 0;
		inputs[6] = 0;
		
		if(dy > 0) {
			inputs[5] = (double) dy / 12;	//max Jump 12
			inputs[6] = 0;
		}
		else if(dy <= 0) {
			inputs[6] = (double) -dy / 12;	//max Jump 12
			inputs[5] = 0;
		}
		double[] output = brain.guess(inputs);
		if(output[0] > output[1]) {
			up();
		}
	}
	
	public int getX() {
		return x;
	}
}