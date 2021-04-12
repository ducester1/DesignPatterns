
/*   ---------::::DDD>>>>   Crown & Council
 by Henrik 'carnalizer' Pettersson, (c)2016 Mojang
 */

//import com.codedisaster.steamworks.*;
import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;
import java.awt.Cursor;  

Minim minim;
AudioPlayer fail1, gold1, gold2, battle1, fort1, castle1, fleet1, trade1, plague1, rebellion1, song, nogo;



// GAME STATES *****************
final int STATE_SPLASH= 0;
final int STATE_MENU = 1;
final int STATE_GAME = 2;
final int STATE_GENERATOR = 3;
final int STATE_GAMEOVER = 4;
final int STATE_MAPSTART = 5;
final int STATE_GAMEWON = 6;
final int STATE_STARTSCREEN = 7;
final int STATE_PAUSE = 8;
final int STATE_MAPTEST = 9;

int gameState = STATE_SPLASH; 
int gameStateOld = gameState;

final int COST_ARMY=1;
final int COST_FORT=3;
final int COST_FLEET=4;
final int COST_UNIVERSITY=8;
final int COST_VILLAGE=2;

final int ACTION_ARMY = 0;
final int ACTION_FORT = 1;
final int ACTION_FLEET = 2;
final int ACTION_VILLAGE = 3;
final int ACTION_UNIVERSITY = 4;
final int NUM_UPGRADE_ACTIONS = 4;

final int RESEARCH_LIMIT=5;

Boolean cheat=false, fullScr=false, hostile=true;
String cheatCode="";

int level=0;

float selectableSine=0, pauseTimer, screenShake;
final color waterPixel = color(0);
boolean saved=true, playMusic, playSound, mouseClicked=false, noAnims=false, campaignMode=true;
int musicAt=0;
boolean setWinPos=true;
PImage splash, gui_bg, bg, worldMap, water, miniMap, glow, upgrade_pane, info_pane, player_pane, player_pane_colorplate, player_pane_current, portrait1, portrait2, portrait3, portrait4, playButtonImg, iconHuman;
PImage[] defenseImg;
float myScale=4;
float tickTimer=millis(), tickDelay=30;
String outMessage="Go!";
String currentMousePressedArea;
boolean noMouseOver = true;
int playerCount=0;
boolean runOnce=true;
boolean runGenerator=true;
boolean autoRun=false;
boolean showAreaInfo=false;
boolean doStartTurn=true;
int iterations =0;
WinStats winStats;

PImage[] animBomb;
PImage[] animCheck;
PImage[] animNope;
PImage[] animGold;
PImage[] animPlague;
PImage[] animFight;
PImage[] animSmoke;
PImage[] animResearch;

int plugCount=0;
boolean loadMap=true, gameOn=true, waitForClick=true;
int playerTurn, noPlayers, humanPlayers, gameTurn;
ArrayList anims;
ArrayList popMessages;
ArrayList textButtons;
ArrayList buttons;
int textHeight = 8;
float aiDelay, aiSpeed;
Button upgradeArmyButton;
Button upgradeFortButton;
Button upgradeTradeButton;
Button upgradeFleetButton; 

Button playButton; 
TextButton endTurnButton;  
TextButton settingsButton;



// GAME WORLD ******************
int[] genLimits = {50, 100, 180};
PFont font;
int margin=27;
int genAreas;
int gridWidth =9, gridHeight = 9;
int scrWidth = 1024, scrHeight = 768;
int scaledScrWidth = int(1024/myScale), scaledScrHeight = int(768/myScale);
ArrayList<Area> areas;
ArrayList<Player> players;
int _livingPlayers;
color waterColor= color(30, 60, 110);
char[][] readmap;
boolean RMB, debug=false;
int baseIncome = 2;
int testIterations = 0;
int maxMaps=99;
String campaignFolder = "";
int testSize=20;
PImage action_army, action_castle, action_fort, action_fleet, action_village, 
    action_army2, action_fort2, action_fleet2, action_village2, action_uni, 
    price_tag, action_selected, warning, 
    upgrade_fort, upgrade_castle, upgrade_mine, upgrade_city, upgrade_uni, 
    upgrade_fort2, upgrade_city2, 
    end_turn, dead, plague, rebellion, ruta1, ruta2, button, blackImg, whiteImg, 
    forestImg, mountainImg;
float tickerTimer=millis();
float tickerPos=scrWidth*1.35;
float chanceOfEvent, eventDistribution;
int tauntIndex;

// TotD, taunt, name strings at bottom of this file.

/*public void init()
 {
 frame.removeNotify();
 frame.setUndecorated(true);
 frame.setResizable(true);
 frame.addNotify();
 frame.setLocation(0, 0);
 }*/

void settings() {
    noSmooth();
    //fullScreen();
}

void setup() {
    loadSettings();
    //size(1024, 768, JAVA2D);
    //surface.setResizable(true);
    //screenSet();
    surface.setResizable(false);

    minim = new Minim(this);
    font = loadFont("BMminiA8-8.vlw");
    textFont(font, textHeight);
    textAlign(CENTER);

  //if (!SteamAPI.init("lib\\steamworks4jnatives.jar")) {
  //   // report error
  //   println("steamerror!");
  //   log=System.getProperty("user.dir");
  //   }

    println("user dir: " + System.getProperty("user.dir"));

    loadSound();
    loadImages();

    humanPlayers=1;


    gameSetup();
}


void gameSetup() {
    setupButtons();
    screenSet();
    screenShake=0;
    clearWaterImage();
    waitForClick=true;
    anims = new ArrayList();
    popMessages = new ArrayList();
    buttons = new ArrayList();
    textButtons = new ArrayList();
    tauntIndex=int(random(taunt.length *2));
    currentTaunt = (tauntIndex < taunt.length) ? taunt[tauntIndex] : getRandomTaunt() ;
    currentTip = tipOfTheDay[int(random(tipOfTheDay.length))];
    genAreas = 10+int(random(100));
    runOnce=true;
    runGenerator=true;
    plugCount=0;
    iterations=0;
    worldMap=createImage(scaledScrWidth, scaledScrHeight, RGB);
    currentMousePressedArea = "";
    noMouseOver = true;
    areas = new ArrayList<Area>();
    players = new ArrayList<Player>();
    //playerCount=0;
    noPlayers=4;
    selectableSine=0;
    aiSpeed=600;

    if (level>maxMaps) level=maxMaps;
    gameOn=true;
    checkMusic();
}

void clearWaterImage() {
    water=createImage(scaledScrWidth, scaledScrHeight, RGB);
    for (int i=0; i<water.pixels.length; i++) {
        water.pixels[i] = waterColor;
    }
}

void screenSet() {
    //surface.setResizable(true);
    surface.setSize(int(scaledScrWidth * myScale), int( scaledScrHeight * myScale) );
    surface.setLocation((displayWidth - width) / 2, (displayHeight - height) / 2);
    //surface.setResizable(false);
}

void setupButtons() {
    upgradeArmyButton = new Button(int(scaledScrWidth*0.55), scaledScrHeight-action_army.height, action_army, STATE_GAME);
    upgradeFortButton = new Button(int(scaledScrWidth*0.60), scaledScrHeight-action_fort.height, action_fort, STATE_GAME);
    upgradeTradeButton = new Button(int(scaledScrWidth*0.75), scaledScrHeight-action_village.height, action_village, STATE_GAME);
    upgradeFleetButton = new Button(int(scaledScrWidth*0.75), scaledScrHeight-action_fleet.height, action_fleet, STATE_GAME);
    playButton = new Button(int(scaledScrWidth*0.5)-int(playButtonImg.width*0.5), int(scaledScrHeight*0.5)-int(playButtonImg.height*0.5), playButtonImg, STATE_STARTSCREEN); 
    endTurnButton = new TextButton("DONE", int(scaledScrWidth-button.width), scaledScrHeight-button.height, button, STATE_GAME);
    settingsButton = new TextButton("SETTINGS", int(scaledScrWidth*0.5)-int(button.width*0.5), int(scaledScrHeight*0.85)-int(button.height*0.5), button, STATE_STARTSCREEN).setTextScale(0.5);
}



String tickerText = "Mojang 2016 all the rights reserved, yo!    ***    Game idea, design, art, code (even the AI!), and sound by HENRIK PETTERSSON, @carnalizer    ***    Made with Processing (processing.org) + Minim library. Music by jukedeck.com. SFX made in SFXR.   ***    Many thanks to Aron, Botteu, Dragonene, HellPie, Jon, Kappische, Kinten, Notch, Samuel, TheWreck, Tommaso, Tywnis and all of Mojang for being awesome!";
String log="";




String[] taunt = {  "YOU ARE WEAK!", 
    "SURRENDER NOW, AND WE'LL SPARE YOU FAMILY.", 
    "YOUR FACE IS A BATTLEFIELD!", 
    "WAR, WHAT IS IT GOOD FOR? GOLD! SWEET SWEET GOLD!", 
    "DIE DIE DIE DIE DIE", 
    "SHOW ME YOUR ARMY, I'LL SHOW YOU MINE.", 
    "TO SECURE PEACE IS TO PREPARE FOR WAR.", 
    "YO MOMA SO FAT YOU NEED TO EXPAND YOUR EMPIRE.", 
    "YOUR BLOODLINE LOOKS LIKE A CHILD'S DRAWING.", 
    "YOU THINK THIS IS A GAME?!", 
    "GO BACK TO THE PIGSTY AND TELL MOM I SAID HI.", 
    "IT'S NEVER TO LATE TO GIVE UP.", 
    "YOU'RE PLAYING IN THE BIG LEAGUE NOW.", 
    "LAUGHING OUT LOUD AT THE THOUGHT OF YOUR ARMY.", 
    "GIVE UP. WE ALL KNOW YOU DON'T STAND A CHANCE.", 
    "A KING CAN BE LUNY, BUT THERE ARE LIMITS.", 
    "A BALANCE OF TERROR IS THE BEDROCK OF SOCEITY", 
    "WHO DIED AND MADE YOU KING?! OH, RIGHT...", 
    "AFTER THIS WAR, WE'LL HOLD ELECTIONS. PROMISE!", 
    "RANDOM NUMBER GENERATOR HELL. DON'T EVEN.", 
};
String[] tipOfTheDay = {  "AREAS WITH MANY NEIGHBORS\n CAN BE HARDER TO DEFEND.", 
    "TRY TO ISOLATE A FEW AREAS\n AND BLOCK THEM OFF WITH FORTS.", 
    "NEVER USE MORE SHIPS THAN NECESSARY.\n ONCE YOU'RE IN, USE ARMY.", 
    "A NEWLY CONQUERED AREA\n WON'T GIVE YOU INCOME.", 
    "MAYBE YOU CAN USE SHIPS\n TO BYPASS A FORT.", 
    "THE MORE YOU ATTACK AN OPPONENT,\n THE MORE HOSTILE HE'LL BE.", 
    "OPPONENTS WILL GENERALLY ATTACK A\n NEUTRAL AREA BEFORE ONE OF YOURS.", 
    "PRESS (ESC) FOR SHORTCUT HELP.", 
    "FORTS DECREASE THE RISK OF\nREBELLION IN AN AREA.", 
    "HAVING A NUMBER OF UNIVERSITIES DECREASE\nTHE RISK OF PLAGUE IN YOUR AREAS.", 
    "UPGRADING CITIES DECREASES THE\nRISK OF REBELLION.", 
    "THERE ARE "+maxMaps+" MAPS, AND YOU CAN\nGENERATE AS MANY AS YOU LIKE!"
};
String currentTip="";

String[] tauntPart1 = { "FACE", "ARMY", "CASTLE", "MOM", "DAD", "FAMILY", "LAND", "KEEP", "BODY", "CROWN", "BRAIN", "RULE", "EMPIRE", "HOME", "BED", "QUEEN", "EFFORT", "WEALTH", "SHIRT"};
String[] tauntPart2 = { "A DWARF", "A WORM", "AN INBRED", "A LUNATIC", "A COW", "A DOG", "A DUNGHEAP", "A DONKEY", "A JESTER", "A DUNCE", "A FARMER", "AN IDIOT", "A LUNATIC", "AN INBRED", "A LIZARD", "A FROG", "A MAGPIE", "A SNAKE", "AN OUTCAST", "A FISH"};
String[] tauntPart3 = { "ASS", "BEHIND", "TURD", "ROTTEN CORPSE", "PISS POT", "BED", "MOM", "OFFSPRING", "JUNK", "OUTHOUSE", "VOMIT", "WART", "PET", "BROTHER", "FOUL ODOR", "FART", "CURSE", "FAILURE", "DISAPPOINTMENT", "ERROR", "BURP", "HORSE", "INSULT"};
String currentTaunt="";

String[] name1 = { "Mar", "Ice", "Wood", "Grey", "Red", "God", "Way", "May", "Hay", "Hy", "In", "Cor", "Slo", "Em", "Man", 
    "Aber", "Act", "Glan", "Exe", "Usk", "Lund", "Ork", "Bal", "Hol", "Rose", "Blen", "Brad", "Ayle", "Dew", 
    "Grim", "Ten", "Whit", "Cros", "Kirk", "Rug", "Kin", "Caer", "Dal", "Aire", "Croy", "Drum", "Roms", "Horn", 
    "Holm", "Fang", "High", "Low", "Mid", "Gil", "Ruth", "Guth", "Hy", "Wool", "Old", "News", "Hem", "Howe", 
    "King", "Bi", "Bul", "Dark", "Un", "Zu", "Be", "Neu", "Stone", "Bird", "Dog", "Fish", "Nook", "Cart", 
    "Lar", "Lip", "Hand", "Cat", "Hat", "Gro", "Buck", "Oak", "Carn", "Arn"};
String[] name2 = { "vik", "nom", "mire", "wain", "ick", "ly", "fly", "bir", "bri", "lay", "song", "grass", "stay", "home", 
    "road", "nock", "bro", "ie", "new", "rock", "path", "moor", "brey", "prey", "hand", "gobb", "vain", "ling", 
    "farm", "hope", "nock", "glen", "ham", "gate", "foss", "ford", "hain", "sop", "ley", "moss", "wall", "wich", 
    "ster", "kirk", "law", "shaw", "pool", "stow", "bury", "ness", "ton", "head", "toft", "va", "ar", "reath", 
    "say", "key", "ring", "elk", "mund", "vim", "clo", "gut", "har", "weed" };
