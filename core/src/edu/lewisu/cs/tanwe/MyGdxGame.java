package edu.lewisu.cs.tanwe;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import edu.lewisu.cs.cpsc41000.common.Boundary;
import edu.lewisu.cs.cpsc41000.common.EdgeHandler;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObjectDrawer;
import edu.lewisu.cs.cpsc41000.common.PlatformCharacter;
import edu.lewisu.cs.cpsc41000.common.labels.ActionLabel;



public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	PlayerCharacter pc;
	Alien alien;
	ImageBasedScreenObjectDrawer artist;

	int playerHealth = 3;

	ImageBasedScreenObject key;
	ArrayList<ImageBasedScreenObject> walls;
	ImageBasedScreenObject goal;
	ArrayList<ImageBasedScreenObject> bouncyPlatforms;
	ArrayList<ImageBasedScreenObject> gooPlatforms; // players cannot jump on these

	ArrayList<Boundary> boundaries;
	EdgeHandler edgy;

	OrthographicCamera cam;
	OrthographicCamera menuCam;

	float WIDTH, HEIGHT;

	Label healthLabel;
	ActionLabel title;
	ActionLabel winMessage;
	ActionLabel deathMessage;
	ActionLabel startGameLabel;
	ArrayList<ActionLabel> tutorials;
	int scene;
	Texture background;

	int WORLDWIDTH, WORLDHEIGHT;

	ArrayList<Music> songs;
	Music ashesToAshes;
	Music lifeOnMars;

	public void restart() {
		pc.hasKey = false;
		playerHealth = 3;
		pc.setXPos(50);
		pc.setYPos(0);
		key.setXPos(1450);
		scene = 1;
	}

	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		batch = new SpriteBatch();

		Texture img = new Texture("spaceman.png");
		background = new Texture("background.png");
		WORLDWIDTH = background.getWidth();
		WORLDHEIGHT = background.getHeight();

		// key 
		Texture keyTex = new Texture("key.png");
		key = new ImageBasedScreenObject(keyTex,1450,550,true);

		// alien
		alien = new Alien(img,400,0,false);
		alien.setMaxSpeed(200);
		alien.setAcceleration(400);
		alien.setDeceleration(800);

		// player character settings
		pc = new PlayerCharacter(img,50,0,false);
		pc.setMaxSpeed(400);
		pc.setAcceleration(800);
		pc.setDeceleration(1600);
		// movement tutorial
		tutorials = new ArrayList<ActionLabel>();
		tutorials.add(new ActionLabel("W - Jump\nA - Move left \nD - Move right", 100, -100, "fonts/arial_small_font.fnt"));

		// Building a test level with platforms
		walls = new ArrayList<ImageBasedScreenObject>();
		Texture wallTex = new Texture("wall.png");
		walls.add(new ImageBasedScreenObject(wallTex,200,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,500,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,500,wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,500+wallTex.getWidth(),0,true));
		walls.add(new ImageBasedScreenObject(wallTex,900,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000,200,true));
		walls.add(new ImageBasedScreenObject(wallTex,1500,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1200,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight()*2,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000,500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth(),500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*2,500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*3,500,true));
		

		//player continues to bounce on these surfaces and pressing jump on landing will launch player
		tutorials.add(new ActionLabel("Press jump while landing on \npink platforms to bounce high", 600, -40, "fonts/arial_small_font.fnt"));
		Texture bouncyTex = new Texture("bouncy.png");
		bouncyPlatforms = new ArrayList<ImageBasedScreenObject>();
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,800,100,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,600,400,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,1600,150,true));

		// slow the player down and prevents jumping
		tutorials.add(new ActionLabel("Green platforms slow you down and\nprevent you from jumping", 1500, -40, "fonts/arial_small_font.fnt"));
		Texture gooTex = new Texture("goo.png");
		gooPlatforms = new ArrayList<ImageBasedScreenObject>();
		gooPlatforms.add(new ImageBasedScreenObject(gooTex,1800,0,true));
		gooPlatforms.add(new ImageBasedScreenObject(gooTex,1800+gooTex.getWidth(),0,true));
		
		tutorials.add(new ActionLabel("Touch the gold platforms to end \nthe level",2000,-40, "fonts/arial_small_font.fnt"));
		Texture goalTex = new Texture("goal.png");
		goal = new ImageBasedScreenObject(goalTex,2700,0,true);
		winMessage = new ActionLabel("LEVEL \nCOMPLETE \nESC to restart",100,100,"fonts/arial_large_font.fnt");
		deathMessage = new ActionLabel("YOU DIED \nESC to restart", 100,100,"fonts/arial_large_font.fnt");


		pc.setPlatforms(walls);
		alien.setPlatforms(walls);
		artist = new ImageBasedScreenObjectDrawer(batch);
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH/2,HEIGHT/2);
		cam.update();
		menuCam = new OrthographicCamera(WIDTH,HEIGHT);
		menuCam.translate(WIDTH/2,HEIGHT/2);
		menuCam.update();
		menuCam = new OrthographicCamera(WIDTH,HEIGHT);
		menuCam.translate(WIDTH/2,HEIGHT/2);
		menuCam.update();
		batch.setProjectionMatrix(cam.combined);
		edgy = new EdgeHandler(pc,cam,batch,0,WORLDWIDTH,0,WORLDHEIGHT,100,
			EdgeHandler.EdgeConstants.PAN, EdgeHandler.EdgeConstants.PAN);
		
		scene = 0;
		title = new ActionLabel("SPACE ODDITY",50,400,"fonts/arial_large_font.fnt");
		startGameLabel = new ActionLabel("Press ESC to begin",150,100,"fonts/arial.fnt");


		//Music
		songs = new ArrayList<Music>();
	
		// win screen music
		songs.add(Gdx.audio.newMusic(Gdx.files.internal("music/ashestoashes.mp3"))); // 0
		songs.get(0).setLooping(true);
		songs.get(0).setVolume(0.1f);
		
		// level 1 music
		songs.add(Gdx.audio.newMusic(Gdx.files.internal("music/lifeonmars.mp3"))); // 1
		songs.get(1).setLooping(true);
		songs.get(1).setVolume(0.1f);

		// title screen music
		songs.add(Gdx.audio.newMusic(Gdx.files.internal("music/starman.mp3"))); // 2
		songs.get(2).setLooping(true);
		songs.get(2).setVolume(0.1f);
	}

	public void renderMain() {
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float dt = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			
		}
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 0;
			return;
		}
		if (Gdx.input.isKeyPressed(Keys.W) && pc.onSolid()) {
			pc.jump();
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			pc.accelerateAtAngle(180);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			pc.accelerateAtAngle(270);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			pc.accelerateAtAngle(0);
		}

		pc.applyPhysics(dt);
		Vector2 bounce;

		

		if (pc.onSolid()) {
			pc.setMaxSpeed(400); // resets player speed 
		}

		for (ImageBasedScreenObject wall : walls) {
			if (pc.overlaps(wall)) {
				System.out.println("collision");
				bounce = pc.preventOverlap(wall);
				if (bounce != null) {
					pc.rebound(bounce.angle(),0.01f);
				}
			}
		}

		for (ImageBasedScreenObject bouncy : bouncyPlatforms) {
			if (pc.overlaps(bouncy)) {
				System.out.println("boing");
				bounce = pc.preventOverlap(bouncy);
				if (bounce != null) {
					pc.rebound(bounce.angle(),1f);
					if (Gdx.input.isKeyPressed(Keys.W)) {
						pc.setMaxSpeed(700); // bounce higher
					}
				}
			}
		}

		for (ImageBasedScreenObject goo : gooPlatforms) {
			if (pc.overlaps(goo)) {
				bounce = pc.preventOverlap(goo);
				if (bounce != null) {
					pc.setMaxSpeed(100); // slows player down
				}
			}
		}

		if (pc.overlaps(alien)) {
			bounce = pc.preventOverlap(alien);
			playerHealth -= 1;
			System.out.println("OUCH");
			System.out.println("Lives left: " + playerHealth);
			
			if (bounce != null) {
				pc.rebound(bounce.angle(),0.2f);
			}
		}

		if (pc.overlaps(key)) {
			pc.gotKey();
			key.setXPos(-1000);
			System.out.println("Got key!");
		}

		if (pc.overlaps(goal) && pc.hasKey) {
			scene = 2; // win screen
		}


		edgy.enforceEdges();
		batch.begin();
		batch.draw(background, 0,0);
		for (ActionLabel tutorial : tutorials) {
			tutorial.draw(batch,1f);
		}
		artist.draw(pc);
		artist.draw(alien);
		for (ImageBasedScreenObject wall : walls) {
			artist.draw(wall);
		}

		for (ImageBasedScreenObject bouncy : bouncyPlatforms) {
			artist.draw(bouncy);
		}

		for (ImageBasedScreenObject goo : gooPlatforms) {
			artist.draw(goo);
		}

		artist.draw(key);
		artist.draw(goal);

		batch.end();
	}	

	public void renderTitleScreen() {
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 1;
		} else {
			batch.setProjectionMatrix(menuCam.combined);
			batch.begin();
			batch.draw(background,0,0);
			title.draw(batch,1f);
			startGameLabel.draw(batch,1f);
			batch.end();
		}
	}

	public void renderWinScreen() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			restart();
		} else {
			Gdx.gl.glClearColor(1,0,0,1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.setProjectionMatrix(menuCam.combined);
			batch.begin();
			batch.draw(background,0,0);
			winMessage.draw(batch,1f);
			batch.end();
		}
	}

	public void renderDeathScreen() {
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			restart();
		} else {
			Gdx.gl.glClearColor(1,0,0,1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.setProjectionMatrix(menuCam.combined);
			batch.begin();
			batch.draw(background,0,0);
			deathMessage.draw(batch,1f);
			batch.end();
		}
	}

	@Override
	public void render () {
		if (playerHealth <= 0) {
			scene = 3;
		}

		if (scene == 1) {
			songs.get(0).stop();
			songs.get(2).stop();
			songs.get(1).play();
			renderMain();
		} else if (scene == 2) {
			songs.get(1).stop();
			songs.get(2).stop();
			songs.get(0).play();
			renderWinScreen();

		} else if (scene == 3) {
			songs.get(1).stop();
			songs.get(2).stop();
			songs.get(0).play();
			renderDeathScreen();
		} else {
			songs.get(0).stop();
			songs.get(1).stop();
			songs.get(2).play();
			renderTitleScreen();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
