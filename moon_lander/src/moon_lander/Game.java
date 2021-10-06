package moon_lander;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 * Actual game.
 * 
 * @author www.gametutorial.net
 */

public class Game {

    /**
     * The space rocket with which player will have to land.
     */
    private PlayerRocket[] playerRocket = new PlayerRocket[2];
    
    /**
     * Landing area on which rocket will have to land.
     */
    private LandingArea landingArea;
    
    /**
     * Game background image.
     */
    private BufferedImage backgroundImg;
    
    /**
     * Red border of the frame. It is used when player crash the rocket.
     */
    private BufferedImage redBorderImg;
    
    public static int rocketNum = 0;

    public Game()
    {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();
                
                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    private void Initialize()
    {
    	switch(Framework.playerCnt) {
    		case 1:
    			playerRocket[0] = new PlayerRocket();
    		break;
    		case 2:
    			playerRocket[0] = new PlayerRocket();
    			playerRocket[1] = new PlayerRocket();
    		break;
    	}
        landingArea  = new LandingArea();
    }
    
    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent()
    {
        try
        {
            URL backgroundImgUrl = this.getClass().getResource("/resources/images/background.jpg");
            backgroundImg = ImageIO.read(backgroundImgUrl);
            
            URL redBorderImgUrl = this.getClass().getResource("/resources/images/red_border.png");
            redBorderImg = ImageIO.read(redBorderImgUrl);
        }
        catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Restart game - reset some variables.
     */
    public void RestartGame()
    {
    	switch(Framework.playerCnt) {
			case 1:
				playerRocket[0].ResetPlayer();
			break;
			case 2:
				playerRocket[0].ResetPlayer();
				playerRocket[1].ResetPlayer();
			break;
		}
    }
    
    
    /**
     * Update game logic.
     * 
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
    	switch(Framework.playerCnt) {
    		case 1:
    			// Move the rocket
    	        playerRocket[0].Update();
    	        
    	        // Checks where the player rocket is. Is it still in the space or is it landed or crashed?
    	        // First we check bottom y coordinate of the rocket if is it near the landing area.
    	        if(playerRocket[0].y + playerRocket[0].rocketImgHeight - 10 > landingArea.y)
    	        {
    	            // Here we check if the rocket is over landing area.
    	            if((playerRocket[0].x > landingArea.x) && (playerRocket[0].x < landingArea.x + landingArea.landingAreaImgWidth - playerRocket[0].rocketImgWidth))
    	            {
    	                // Here we check if the rocket speed isn't too high.
    	                if(playerRocket[0].speedY <= playerRocket[0].topLandingSpeed)
    	                    playerRocket[0].landed = true;
    	                else
    	                    playerRocket[0].crashed = true;
    	            }
    	            else
    	                playerRocket[0].crashed = true;
    	                
    	            Framework.gameState = Framework.GameState.GAMEOVER;
    	        }
    	    break;
    		case 2:
    			for(int i=0; i<2; i++) {
    				rocketNum = i;
    				// Move the rocket
    		        playerRocket[i].Update();
    		        
    		        // Checks where the player rocket is. Is it still in the space or is it landed or crashed?
    		        // First we check bottom y coordinate of the rocket if is it near the landing area.
    		        if(playerRocket[i].y + playerRocket[i].rocketImgHeight - 10 > landingArea.y)
    		        {
    		            // Here we check if the rocket is over landing area.
    		            if((playerRocket[i].x > landingArea.x) && (playerRocket[i].x < landingArea.x + landingArea.landingAreaImgWidth - playerRocket[i].rocketImgWidth))
    		            {
    		                // Here we check if the rocket speed isn't too high.
    		                if(playerRocket[i].speedY <= playerRocket[i].topLandingSpeed)
    		                    playerRocket[i].landed = true;
    		                else
    		                    playerRocket[i].crashed = true;
    		            }
    		            else
    		                playerRocket[i].crashed = true;
    		        }
    		        if(playerRocket[0].crashed && playerRocket[1].crashed) {
    		        	Framework.gameState = Framework.GameState.GAMEOVER;
    		        } else if(playerRocket[0].crashed && playerRocket[1].landed) {
    		        	Framework.gameState = Framework.GameState.GAMEOVER;
    		        } else if(playerRocket[0].landed && playerRocket[1].crashed) {
    		        	Framework.gameState = Framework.GameState.GAMEOVER;
    		        } else if(playerRocket[0].landed && playerRocket[1].landed) {
    		        	Framework.gameState = Framework.GameState.GAMEOVER;
    		        }
    			}
    	}
    }
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        
        landingArea.Draw(g2d);
        
        switch(Framework.playerCnt) {
			case 1:
				rocketNum = 0;
				playerRocket[0].Draw(g2d);
			break;
			case 2:
				rocketNum = 0;
				playerRocket[0].Draw(g2d);
				rocketNum = 1;
				playerRocket[1].Draw(g2d);
			break;
		}
    }
    
    
    /**
     * Draw the game over screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition Current mouse position.
     * @param gameTime Game time in nanoseconds.
     */
    public void DrawGameOver(Graphics2D g2d, Point mousePosition, long gameTime)
    {
        Draw(g2d, mousePosition);
        
        g2d.drawString("Press space or enter to restart.", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3 + 70);
        
        switch(Framework.playerCnt) {
			case 1:
				if(playerRocket[0].landed)
		        {
		            g2d.drawString("You have successfully landed!", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3);
		            g2d.drawString("You have landed in " + gameTime / Framework.secInNanosec + " seconds.", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3 + 20);
		        }
		        else
		        {
		            g2d.setColor(Color.red);
		            g2d.drawString("You have crashed the rocket!", Framework.frameWidth / 2 - 95, Framework.frameHeight / 3);
		            g2d.drawImage(redBorderImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
		        }
			break;
			case 2:
				if(playerRocket[0].landed && playerRocket[1].landed)
		        {
		            g2d.drawString("You have successfully landed!", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3);
		            g2d.drawString("You have landed in " + gameTime / Framework.secInNanosec + " seconds.", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3 + 20);
		        }
		        else if(playerRocket[0].crashed && playerRocket[1].crashed)
		        {
		            g2d.setColor(Color.red);
		            g2d.drawString("You have crashed the rocket!", Framework.frameWidth / 2 - 95, Framework.frameHeight / 3);
		            g2d.drawImage(redBorderImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
		        }
		        else
		        {
		        	g2d.drawString("Game over", Framework.frameWidth / 2 - 50, Framework.frameHeight / 3);
		        }
			break;
		}
    }
}
