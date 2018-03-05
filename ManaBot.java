package nmimura_mkn1;
import robocode.*;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * ManaBot - a robot by Michael Nguyen and Nana Mimura
 */
public class ManaBot extends AdvancedRobot
{
	private static final int WALL_MARGIN = 50, MAX_VELOCITY = 8, MAX_DIMENSION = 5000;
	private int tooCloseToWall = 0, moveDirection = 1, movementDirection = 1; //when -1, turns other way, tooClosetoWall is initialization
	double previousEnergy = 100; //keep track of enemy energy
	
	public void run() {
		setColors(Color.blue, Color.blue, Color.green); //body,gun,radar
		setScanColor(Color.white); //scan arc
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		addCustomEvent(new Condition("too_close_to_wall")
		{
			public boolean test()
			{
				return (getX() <= WALL_MARGIN || getX() >= getBattleFieldWidth() - WALL_MARGIN ||
						getY() <= WALL_MARGIN || getY() >= getBattleFieldHeight() - WALL_MARGIN);
			}
		});
		
		while(true) {
			turnRadarRight(90);
			doMove();
			execute();
		}
	}
		
	public void onScannedRobot(ScannedRobotEvent e) {
	// 5 lines of code (dodging) were provided by https://www.ibm.com/developerworks/library/j-dodge/index.html
		setTurnRight(e.getBearing() + 90 - 30 * movementDirection);
		double energyChange = previousEnergy - e.getEnergy();
		setTurnGunRight(normalizeBearing(getHeading() - getGunHeading() + e.getBearing()));
		setTurnRadarRight(normalizeBearing(getHeading() - getRadarHeading() + e.getBearing()));
		if (energyChange > 0 && energyChange <= 3) {
        	movementDirection = -movementDirection;
        	setAhead((e.getDistance()/4+25)*movementDirection);
		}
		setFire(Math.min(400 / e.getDistance(), 3));
		if (e.getEnergy() < 20)
			setFire(.5);
		previousEnergy = e.getEnergy();
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
			setFire(Math.min(600 / e.getDistance(), 3));
	}
	
	//normalizes bearings to be between -180 and +180
	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	
	public void onHitByBullet(HitByBulletEvent e) {
		setBack(10);
	}
	
	public void onHitByRobot(HitRobotEvent e) {
		scan();
	}
	
	public void onCustomEvent(CustomEvent e)
	{
		if (e.getCondition().getName().equals("too_close_to_wall"))
		{
			if (tooCloseToWall <= 0)
			{
				tooCloseToWall += WALL_MARGIN;
				setMaxVelocity(0);
			}
		}
	}
	
	public void doMove()
	{
		if (tooCloseToWall > 0)	
			tooCloseToWall--;
		if (getVelocity() == 0)
		{
			setMaxVelocity(MAX_VELOCITY);
			moveDirection *= -1;
			setAhead(MAX_DIMENSION * moveDirection);
		}
	}
	
}
