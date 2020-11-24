import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Obstacle {
	
	Random r = new Random();
	
	int x,gapsize,y;

	int width;

	int dx;
	boolean passed = false;
	Rectangle2D square1;
	Rectangle2D square2;
	Color c = new Color(255, 77, 77);
	
	public Obstacle() {
		width = 50;
		x = Test.width;
		gapsize = 80;		// 70 is possible
		y = r.nextInt(Test.height - 75*2 - gapsize) + 75 + gapsize;
		dx = 2;
		square1 = new Rectangle2D.Double(x + width/2, 0, width, y - gapsize);
		square2 = new Rectangle2D.Double(x + width/2, y, width, Test.height);
	}
	

	
	public void show(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(c);
		g2.fill(square2);
		g2.fill(square1);
	}
	
	public void update() {
		this.square1 = new Rectangle2D.Double(x + width/2, 0, width, y - gapsize);
		this.square2 = new Rectangle2D.Double(x + width/2, y, width, Test.height);
		x -= dx;
	}
	
	public void setPassed(boolean p) {
		this.passed = p;
	}
	
	public int getX() {
		return x;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setC(Color kk) {
		this.c = kk;
	}
	
}
