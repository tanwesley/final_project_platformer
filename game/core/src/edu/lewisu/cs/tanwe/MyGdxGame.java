package edu.lewisu.cs.tanwe;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;

import edu.lewisu.cs.cpsc41000.common.Boundary;
import edu.lewisu.cs.cpsc41000.common.EdgeHandler;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObjectDrawer;
import edu.lewisu.cs.cpsc41000.common.MobileImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.labels.ActionLabel;



public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	PlayerCharacter pc;
	ArrayList<Alien> aliens;
	MobileImageBasedScreenObject rocket;
	ImageBasedScreenObjectDrawer artist;

	int playerHealth = 3;
	int playerScore = 0;
	int keysCollected = 0;

	// hud 
	LabelStyle labelStyle;
	Label hud;

	ArrayList<MobileImageBasedScreenObject> keys;
	MobileImageBasedScreenObject key;
	ArrayList<ImageBasedScreenObject> walls;
	MobileImageBasedScreenObject goal;
	ArrayList<ImageBasedScreenObject> bouncyPlatforms;
	ArrayList<ImageBasedScreenObject> gooPlatforms; // players cannot jump on these
	ArrayList<MobileImageBasedScreenObject> rocks;

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
	ArrayList<ImageBasedScreenObject> tutorialObj;

	int scene;
	Texture background;

	int WORLDWIDTH, WORLDHEIGHT;

	ArrayList<Music> songs;
	Music ashesToAshes;
	Music lifeOnMars;

	// animation sequences
	int[] fseq = {0,1,0,2,0}; // animation for player
	int[] kseq = {0,0,0,1,0,0,0,2,0,0,0,3}; // animation for key and collectables
	int[] rseq = {0,1,0,2,0,3}; // rocket animation, alien, goal animation

	public void createTutorials() {
		tutorials = new ArrayList<ActionLabel>();
		tutorials.add(new ActionLabel("HOW TO PLAY",50,400,"fonts/arial_large_font.fnt"));
		tutorials.add(new ActionLabel("MOVEMENT: W - Jump \nA - move left \nS - accelerate downward \nD move right", 50, 300, "fonts/arial_small_font.fnt"));
		tutorials.add(new ActionLabel("PAUSE: ESC", 50, 270, "fonts/arial_small_font.fnt"));

		tutorials.add(new ActionLabel("COLLECT ALL 3 \nMISSING PARTS \nTO YOUR SHIP! ->",50,150,"fonts/arial_small_font.fnt"));
		tutorials.add(new ActionLabel("GET TO YOUR SHIP \nONCE YOU HAVE \nALL PARTS! ->",50,75,"fonts/arial_small_font.fnt"));
		tutorials.add(new ActionLabel("COLLECT SPACE\n ROCKS TO SCORE\n POINTS! ->",400,320,"fonts/arial_small_font.fnt"));

		tutorials.add(new ActionLabel("AVOID ALIENS! ->",400,250,"fonts/arial_small_font.fnt"));
		tutorials.add(new ActionLabel("DON'T TOUCH GREEN GOO ->",400,200, "fonts/arial_small_font.fnt"));
		tutorials.add(new ActionLabel("JUMP ON PINK\n PLATFORMS TO \nLAUNCH YOURSELF ->",400,75, "fonts/arial_small_font.fnt"));

		Texture tKeyTex = new Texture("staticwreckage.png");
		Texture tGoalTex = new Texture("staticgoal.png");
		Texture tAlienTex = new Texture("staticalien.png");
		Texture tRockTex = new Texture("staticrock.png");
		Texture tGooTex = new Texture("goo.png");
		Texture tBouncyTex = new Texture("bouncy.png");

		tutorialObj = new ArrayList<ImageBasedScreenObject>();
		tutorialObj.add(new ImageBasedScreenObject(tKeyTex,250,150,true));
		tutorialObj.add(new ImageBasedScreenObject(tGoalTex,180,0,true));
		tutorialObj.add(new ImageBasedScreenObject(tRockTex,500,300,true));
		tutorialObj.add(new ImageBasedScreenObject(tAlienTex,550,250,true));
		tutorialObj.add(new ImageBasedScreenObject(tGooTex,430,180,true));
		tutorialObj.add(new ImageBasedScreenObject(tBouncyTex,430,40,true));

	}

	public void setupLabelStyle() {
		labelStyle = new LabelStyle();
		labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/arial_small_font.fnt"));
	}


	public void restart() {
		pc.hasKey = false;
		keysCollected = 0;
		playerHealth = 3;
		pc.setXPos(50);
		pc.setYPos(0);

		keys.get(0).setXPos(1235);
		keys.get(1).setXPos(1038);
		keys.get(2).setXPos(2000);

		rocks.get(0).setXPos(200);
		rocks.get(1).setXPos(1200);
		rocks.get(2).setXPos(400);
		rocks.get(3).setXPos(1496);
		rocks.get(4).setXPos(1934);
		rocks.get(5).setXPos(2087);
		rocks.get(6).setXPos(1044);
		rocks.get(7).setXPos(1496);
		rocks.get(8).setXPos(2231);
		rocks.get(9).setXPos(2476);
		rocks.get(10).setXPos(1393);
		rocks.get(11).setXPos(2444);
		rocks.get(12).setXPos(2394);
		rocks.get(13).setXPos(2645);
		rocks.get(14).setXPos(4200);
		rocks.get(15).setXPos(4289);

		playerScore = 0;
		scene = 1;
	}

	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		batch = new SpriteBatch();

		createTutorials();
		setupLabelStyle();
		hud = new Label("Coordinates", labelStyle);

		Texture wallTex = new Texture("wall.png");
		Texture gooTex = new Texture("goo.png");
		Texture bouncyTex = new Texture("bouncy.png");

		Texture img = new Texture("majortom.png");
		background = new Texture("background.png");
		WORLDWIDTH = background.getWidth();
		WORLDHEIGHT = background.getHeight();

		Texture rocketTex = new Texture("rocketship.png");
		rocket = new MobileImageBasedScreenObject(rocketTex,200,200,false);
		rocket.setAnimationParameters(254,128,rseq,0.7f);
		Texture goalTex = new Texture("goal.png");
		goal = new MobileImageBasedScreenObject(goalTex,WORLDWIDTH-goalTex.getWidth(),0,true);
		goal.setAnimationParameters(128,128,rseq,0.1f);

		// player character settings
		pc = new PlayerCharacter(img,50,0,false);
		pc.setAnimationParameters(32,64,fseq,0.1f);
		pc.setMaxSpeed(400);
		pc.setAcceleration(800);
		pc.setDeceleration(1600);



		// collectables
		rocks = new ArrayList<MobileImageBasedScreenObject>();
		Texture rockTex = new Texture("spacerock.png");
		rocks.add(new MobileImageBasedScreenObject(rockTex,200,100,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,1200,650,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,400,0,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,1496,562,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,1934,1062,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,2087,1062,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,1044,0,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,1496,562,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,2231,820,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,2476,817,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,1393,0,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,2444, 569,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,2394,62,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,2645,62,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,4200,0,true));
		rocks.add(new MobileImageBasedScreenObject(rockTex,4289,0,true));

		for (MobileImageBasedScreenObject rock : rocks) {
			rock.setAnimationParameters(32,32,kseq,0.1f);
		}
		// key 
		Texture keyTex = new Texture("wreckagesprite.png");
		keys = new ArrayList<MobileImageBasedScreenObject>();
		keys.add(new MobileImageBasedScreenObject(keyTex,1235,wallTex.getHeight(),true));
		keys.add(new MobileImageBasedScreenObject(keyTex,1038,262,true));
		keys.add(new MobileImageBasedScreenObject(keyTex,2000,817,true));
		for (MobileImageBasedScreenObject key : keys) {
			key.setAnimationParameters(64,64,kseq,0.1f);
		}

		// aliens
		aliens = new ArrayList<Alien>();
		Texture alienTex = new Texture("alien.png");
		aliens.add(new Alien(alienTex,300,0,false));
		aliens.add(new Alien(alienTex,450,0,false));
		aliens.add(new Alien(alienTex,1450,500+wallTex.getHeight(),false));
		aliens.add(new Alien(alienTex,2147,1062,false));
		aliens.add(new Alien(alienTex,2010,1064,false));
		aliens.add(new Alien(alienTex,2010,824,false));
		aliens.add(new Alien(alienTex,2200,824,false));
		aliens.add(new Alien(alienTex,4000,0,false));
		aliens.add(new Alien(alienTex,4200,0,false));
		aliens.add(new Alien(alienTex,4100,0,false));
		aliens.add(new Alien(alienTex,1300,0,false));
		aliens.add(new Alien(alienTex,1450,0,false));

		for (Alien alien : aliens) {
			alien.setAnimationParameters(32,32,rseq,0.1f);
		}


		// Building a level with platforms
		walls = new ArrayList<ImageBasedScreenObject>();
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
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight()*3,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight()*5,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight()*6,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight()*7,true));

		walls.add(new ImageBasedScreenObject(wallTex,1000,500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth(),500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*2,500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*2,500+wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*3,500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*4,500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*5,500,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*5,500+wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*6,500+wallTex.getHeight()*2,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,500+wallTex.getHeight()*2,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,500+wallTex.getHeight()*3,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,500+wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,500+wallTex.getHeight()*5,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,500+wallTex.getHeight()*6,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,500+wallTex.getHeight()*7,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,500+wallTex.getHeight()*8,true));

		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*4,700,true));


		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*4,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*5,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*6,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*6,1000+wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*8,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*9,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*10,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*10,1000+wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*11,1000,true));

		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*7,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*8,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*9,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*10,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*11,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*12,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*13,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*14,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*14,1000-wallTex.getHeight()*3,true));

		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*15,1000-wallTex.getHeight()*5,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*16,1000-wallTex.getHeight()*5,true));
		

		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*15,1000-wallTex.getHeight()*8,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*14,1000-wallTex.getHeight()*9,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*13,1000-wallTex.getHeight()*10,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*12,1000-wallTex.getHeight()*11,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*11,1000-wallTex.getHeight()*12,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*10,1000-wallTex.getHeight()*13,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*9,1000-wallTex.getHeight()*14,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*8,1000-wallTex.getHeight()*15,true));

		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*15,1000-wallTex.getHeight()*8,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*16,1000-wallTex.getHeight()*8,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*17,1000-wallTex.getHeight()*8,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*18,1000-wallTex.getHeight()*8,true));

		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight()*8,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight()*7,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight()*6,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight()*5,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight()*3,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight()*2,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000-wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight()*2,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight()*3,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight()*4,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight()*5,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight()*6,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight()*7,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000+wallTex.getWidth()*19,1000+wallTex.getHeight()*8,true));

		walls.add(new ImageBasedScreenObject(wallTex,2343,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,2472+gooTex.getWidth(),0,true));
		walls.add(new ImageBasedScreenObject(wallTex,2472+gooTex.getWidth()*4,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,2824,120,true));

		walls.add(new ImageBasedScreenObject(wallTex,3530,250,true));
		walls.add(new ImageBasedScreenObject(wallTex,3530,250-wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,3530,250-wallTex.getHeight()*2,true));
		walls.add(new ImageBasedScreenObject(wallTex,3530,250-wallTex.getHeight()*3,true));
		walls.add(new ImageBasedScreenObject(wallTex,3530,250-wallTex.getHeight()*4,true));

		walls.add(new ImageBasedScreenObject(wallTex,3847,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,3847+wallTex.getWidth()*5,0,true));	
		
		walls.add(new ImageBasedScreenObject(wallTex,2590,924,true));

		//player continues to bounce on these surfaces and pressing jump on landing will launch player
		bouncyPlatforms = new ArrayList<ImageBasedScreenObject>();
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,800,100,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,600,400,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,1600,150,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,1000+wallTex.getWidth()*3,480+wallTex.getHeight()*6,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,2862+bouncyTex.getWidth()*3,573,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,2472+gooTex.getWidth()*5,0,true));
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,3734,160,true));

		//harms player
		gooPlatforms = new ArrayList<ImageBasedScreenObject>();
		gooPlatforms.add(new ImageBasedScreenObject(gooTex,2472,0,true));
		gooPlatforms.add(new ImageBasedScreenObject(gooTex,2472+gooTex.getWidth()*2,0,true));
		gooPlatforms.add(new ImageBasedScreenObject(gooTex,2472+gooTex.getWidth()*3,0,true));


		pc.setPlatforms(walls);
		for (Alien alien : aliens) {
			alien.setPlatforms(walls);
		}

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
		edgy = new EdgeHandler(pc,cam,batch,0,WORLDWIDTH,0,WORLDHEIGHT,200,
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

		hud.setText("LIVES: " + playerHealth + "  SCORE: " + playerScore + "\nPARTS COLLECTED: " + keysCollected + "/3");
		hud.setPosition(20+(cam.position.x-WIDTH/2),440+cam.position.y-HEIGHT/2);
		

		// PLAYER INPUT
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			System.out.println(pc.getXPos() + "," +pc.getYPos());
		}
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 4;
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

		// enemy movement
		for (Alien alien : aliens) {
			alien.applyPhysics(dt);
			alien.animate(dt);
			if (alien.getDir()==0) {
				alien.moveRight(1f);
			} else if (alien.getDir()==1) {
				alien.moveLeft(1f);
			}
		}


		Vector2 bounce;

		if (pc.onSolid()) {
			pc.setMaxSpeed(400); // resets player speed 
		}

		for (ImageBasedScreenObject wall : walls) {
			if (pc.overlaps(wall)) {
				bounce = pc.preventOverlap(wall);
				if (bounce != null) {
					pc.rebound(bounce.angle(),0.01f);
				}
			}
			for (Alien alien : aliens) {
				if (alien.overlaps(wall)) {
					bounce = alien.preventOverlap(wall);
					if (alien.getDir() == 0) {
						alien.setDir(1);
					} else if (alien.getDir() == 1) {
						alien.setDir(0);
					}
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
					pc.rebound(bounce.angle(),1f);
					playerHealth -= 1;
				}
			}
		}

		for (MobileImageBasedScreenObject rock : rocks) {
			if (pc.overlaps(rock)) {
				playerScore += 100;
				rock.setXPos(-1000);
			}
		}

		for (Alien alien : aliens) {
			if (pc.overlaps(alien)) {
				bounce = pc.preventOverlap(alien);
				playerHealth -= 1;
				System.out.println("OUCH");
				System.out.println("Lives left: " + playerHealth);
				
				if (bounce != null) {
					pc.rebound(bounce.angle(),0.2f);
				}
			}
		}

		for (MobileImageBasedScreenObject key : keys) {
			if (pc.overlaps(key)) {
				playerScore += 50;

				keysCollected += 1;
				key.setXPos(-1000);
				System.out.println("Got key!");
			}
		}

		if (keysCollected==3) {
			pc.gotKey();
		}

		if (pc.overlaps(goal) && pc.hasKey) {
			scene = 2; // win screen
		} else if (pc.overlaps(goal) && !pc.hasKey) {
			hud.setText("You don't have all your ship parts!");
			hud.setPosition((200+cam.position.x-WIDTH/2),200+cam.position.y-HEIGHT/2);
		}


		edgy.enforceEdges();
		batch.begin();
		batch.draw(background,0,0);
		artist.draw(pc);

		for (Alien alien : aliens) {
			artist.draw(alien);
		}

		for (ImageBasedScreenObject wall : walls) {
			artist.draw(wall);
		}

		for (ImageBasedScreenObject bouncy : bouncyPlatforms) {
			artist.draw(bouncy);
		}

		for (ImageBasedScreenObject goo : gooPlatforms) {
			artist.draw(goo);
		}

		for (MobileImageBasedScreenObject rock : rocks) {
			artist.draw(rock);
			rock.animate(0.1f);
		}

		for (MobileImageBasedScreenObject key : keys) {
			key.animate(0.1f);
			artist.draw(key);
		}

		artist.draw(goal);
		goal.animate(dt);
		hud.draw(batch,1);

		batch.end();
	}	

	public void renderTitleScreen() {
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 4;
		} else {
			batch.setProjectionMatrix(menuCam.combined);
			batch.begin();
			batch.draw(background,0,0);
			rocket.animate(0.1f);
			artist.draw(rocket);
			title.draw(batch,1f);
			startGameLabel.draw(batch,1f);
			batch.end();
		}
	}

	public void renderTutorialScreen() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 1;
		} else {
			batch.setProjectionMatrix(menuCam.combined);
			batch.begin();
			batch.draw(background,0,0);
			for (ActionLabel tutorial : tutorials) {
				tutorial.draw(batch,1f);
			}
			for (ImageBasedScreenObject obj : tutorialObj) {
				artist.draw(obj);
			}
			batch.end();
		}
	}

	public void renderWinScreen() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			restart();
		} else {
			Gdx.gl.glClearColor(1,0,0,1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			winMessage = new ActionLabel("LEVEL \nCOMPLETE \nESC to restart\nScore: " + playerScore,100,100,"fonts/arial_large_font.fnt");
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
			deathMessage = new ActionLabel("YOU DIED \nESC to restart\nScore: " + playerScore, 100,100,"fonts/arial_large_font.fnt");
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

		if (scene == 1) {	// level
			songs.get(0).stop();
			songs.get(2).stop();
			songs.get(1).play();
			renderMain();
		} else if (scene == 2) {	// win screen
			songs.get(1).stop();
			songs.get(2).stop();
			songs.get(0).play();
			renderWinScreen();

		} else if (scene == 3) {	// death screen
			songs.get(1).stop();
			songs.get(2).stop();
			songs.get(0).play();
			renderDeathScreen();
		} else if (scene == 4) { // tutorial screen
			renderTutorialScreen();
		} else {					//startup screen
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
