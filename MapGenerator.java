import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.Random;

public class MapGenerator {
    Color myColor = Color.decode("#01002d");
    Color[] colors;
    public int map[][];
    public int brickWidth;
    public int brickHeight;
    
    public MapGenerator(int row, int col) { //constructer
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1;
            }
        }
        brickWidth = 540 / col;
        brickHeight = 150 / row;
        colors = new Color[map.length];
        for (int i = 0; i < map.length; i++) {
            Random random = new Random();
            final float hue = random.nextFloat();
            final float saturation = (random.nextInt(2000 + 1000) / 10000f);
            final float luminance = 0.9f;
            final Color color = Color.getHSBColor(hue, saturation, luminance);
            colors[i] = color;
        }
    }
    
    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(colors[i]); //brick colors
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight); //setting size of bricks
                    g.setStroke(new BasicStroke(10)); //adding border padding to bricks
                    g.setColor(myColor); //color of padding
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight); //size of padding
                }
            }
        }
    }
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value; //0 = broken brick, 1 = normal brick
    }
}

