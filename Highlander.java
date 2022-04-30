package capacitacao;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Highlander - a robot by (your name here)
 */
public class Highlander extends Robot
{
	// Cria uma enumeração para indicar a orientação do robô
	private enum DirectionEnum {
		TO_TOP("TO_TOP", 0),
		TO_RIGHT("TO_RIGHT", 90),
		TO_BOTTON("TO_BOTTON", 180),
		TO_LEFT("TO_LEFT", 270);

		public final String direction;		
		public final int angle;
		public double distance;
		
		DirectionEnum(String direction, int angle) {
			this.direction = direction;
			this.angle = angle;
		}
	}
	
	// Variáveis que definirão as bordas da arena
	private double topLine;
	private double bottonLine;
	private double leftLine;
	private double rightLine;
	// Variável que armazena a orientação do robô e sua distância para a borda à frente
	private DirectionEnum botDirection = null;
	
	// Altera a orientação do robô
	public void changeDirection() {
		// Vetor que contará as disâncias para cada borda da arena
		DirectionEnum[] directions = {DirectionEnum.TO_TOP, DirectionEnum.TO_BOTTON, DirectionEnum.TO_LEFT, DirectionEnum.TO_RIGHT};
		directions[0].distance = this.topLine - getY();
		directions[1].distance = getY() - this.bottonLine;
		directions[2].distance = getX() - this.leftLine;
		directions[3].distance = this.rightLine - getX();
		
		// Ordena o vetor com base nas distâncias
		for(int index = 0;index < directions.length - 1;index++) {
			if(directions[index].distance > directions[index + 1].distance) {
				DirectionEnum temp = directions[index];
				directions[index] = directions[index + 1];
				directions[index + 1] = temp;
				index = -1;
			}
		}
		// Define a próxima orientação do robô
		for(int index = 0;index < directions.length;index++) {
			// Verifica se a distância para esta orientação oé maior que zero
			if(directions[index].distance > 0.0d) {
				if(this.botDirection == null){
					this.botDirection = directions[index];
					break;
				} else if ((this.botDirection.direction == "TO_TOP" || this.botDirection.direction == "TO_BOTTON") && (directions[index].direction == "TO_RIGHT" || directions[index].direction == "TO_LEFT")) {
					this.botDirection = directions[index];
					break;
				} else if ((this.botDirection.direction == "TO_RIGHT" || this.botDirection.direction == "TO_LEFT") && (directions[index].direction == "TO_TOP" || directions[index].direction == "TO_BOTTON")){
					this.botDirection = directions[index];
					break;
				}
			}
		}
		// Vira o robô para a orientação selecionada
		turnRight(normalRelativeAngleDegrees(this.botDirection.angle - getHeading()));
		
	}
	
	// Executa a movimentação do robô
	public void move() {
		// Se a distância para a borda à frente for maior que zero...
		if(this.botDirection.distance > 0.0d) {
			// Caso a distância for maior que 100 define que será percorrida 100 senão o valor que fala para o fim da arena
			double distance = (this.botDirection.distance > 100) ? 100 : this.botDirection.distance;
			ahead(distance);
			// Subtrai o valor percorrido da distância
			this.botDirection.distance -= distance;
		} else {
			// Caso a distância para a borda à frente for zero muda a direção
			this.changeDirection();
		}
	}
	
	// Movimenta o canhão
	public void turnGun() {
		if((getX() == this.rightLine && this.botDirection.direction == "TO_TOP") || (getX() == this.leftLine && this.botDirection.direction == "TO_BOTTON") || (getY() == this.topLine && this.botDirection.direction == "TO_LEFT") || (getY() == this.bottonLine && this.botDirection.direction == "TO_RIGHT")){
			turnGunRight(-180);
			turnGunRight(180);
		} else {
			turnGunRight(180);
			turnGunRight(-180);
		}
	}
	
	/**
	 * run: Prototipo's default behavior
	 */
	public void run() {
		this.topLine = getBattleFieldHeight() - (getHeight() + 2);
		this.bottonLine = getHeight() + 2;
		this.rightLine = getBattleFieldWidth() - (getWidth() + 2);
		this.leftLine = getWidth() + 2;
	
		this.changeDirection();

		while(true) {
			move();
			turnGunRight(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		fire(1);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		this.changeDirection();
	}
}
