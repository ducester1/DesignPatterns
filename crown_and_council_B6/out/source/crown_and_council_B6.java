import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.spi.*; 
import ddf.minim.signals.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.ugens.*; 
import ddf.minim.effects.*; 
import java.awt.Cursor; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class crown_and_council_B6 extends PApplet {


/*   ---------::::DDD>>>>   Crown & Council
 by Henrik 'carnalizer' Pettersson, (c)2016 Mojang
 */

//import com.codedisaster.steamworks.*;






  

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
final int waterPixel = color(0);
boolean saved=true, playMusic, playSound, mouseClicked=false, noAnims=false, campaignMode=true;
int musicAt=0;
boolean setWinPos=true;
PImage splash, gui_bg, bg, worldMap, water, miniMap, glow, upgrade_pane, info_pane, player_pane, player_pane_colorplate, player_pane_current, portrait1, portrait2, portrait3, portrait4, playButtonImg, iconHuman;
PImage[] defenseImg;
float myScale=4;
float tickTimer=millis(), tickDelay=300;
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
int scaledScrWidth = PApplet.parseInt(1024/myScale), scaledScrHeight = PApplet.parseInt(768/myScale);
ArrayList<Area> areas;
ArrayList<Player> players;
int _livingPlayers;
int waterColor= color(30, 60, 110);
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
float tickerPos=scrWidth*1.35f;
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

public void settings() {
    noSmooth();
    //fullScreen();
}

public void setup() {
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


public void gameSetup() {
    setupButtons();
    screenSet();
    screenShake=0;
    clearWaterImage();
    waitForClick=true;
    anims = new ArrayList();
    popMessages = new ArrayList();
    buttons = new ArrayList();
    textButtons = new ArrayList();
    tauntIndex=PApplet.parseInt(random(taunt.length *2));
    currentTaunt = (tauntIndex < taunt.length) ? taunt[tauntIndex] : getRandomTaunt() ;
    currentTip = tipOfTheDay[PApplet.parseInt(random(tipOfTheDay.length))];
    genAreas = 10+PApplet.parseInt(random(100));
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

public void clearWaterImage() {
    water=createImage(scaledScrWidth, scaledScrHeight, RGB);
    for (int i=0; i<water.pixels.length; i++) {
        water.pixels[i] = waterColor;
    }
}

public void screenSet() {
    //surface.setResizable(true);
    surface.setSize(PApplet.parseInt(scaledScrWidth * myScale), PApplet.parseInt( scaledScrHeight * myScale) );
    surface.setLocation((displayWidth - width) / 2, (displayHeight - height) / 2);
    //surface.setResizable(false);
}

public void setupButtons() {
    upgradeArmyButton = new Button(PApplet.parseInt(scaledScrWidth*0.55f), scaledScrHeight-action_army.height, action_army, STATE_GAME);
    upgradeFortButton = new Button(PApplet.parseInt(scaledScrWidth*0.60f), scaledScrHeight-action_fort.height, action_fort, STATE_GAME);
    upgradeTradeButton = new Button(PApplet.parseInt(scaledScrWidth*0.75f), scaledScrHeight-action_village.height, action_village, STATE_GAME);
    upgradeFleetButton = new Button(PApplet.parseInt(scaledScrWidth*0.75f), scaledScrHeight-action_fleet.height, action_fleet, STATE_GAME);
    playButton = new Button(PApplet.parseInt(scaledScrWidth*0.5f)-PApplet.parseInt(playButtonImg.width*0.5f), PApplet.parseInt(scaledScrHeight*0.5f)-PApplet.parseInt(playButtonImg.height*0.5f), playButtonImg, STATE_STARTSCREEN); 
    endTurnButton = new TextButton("DONE", PApplet.parseInt(scaledScrWidth-button.width), scaledScrHeight-button.height, button, STATE_GAME);
    settingsButton = new TextButton("SETTINGS", PApplet.parseInt(scaledScrWidth*0.5f)-PApplet.parseInt(button.width*0.5f), PApplet.parseInt(scaledScrHeight*0.85f)-PApplet.parseInt(button.height*0.5f), button, STATE_STARTSCREEN).setTextScale(0.5f);
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

public void AI_turn(){
  
  
  Player _AI = currentPlayer();
  doAIUpgrade(_AI);
  if (_AI.gold<1 || _AI.isAlive==false){return;}
  if(stupefyAI(_AI)) return;
  
  float[] _evals = new float[5*areas.size()];
  int actionCount = constrain(level,1,5);
  if(!campaignMode) actionCount=5;
  
  for(int _action=0;_action<actionCount;_action++){
    Action _a = _AI.actions.get(_action);
    for(int _testArea=0;_testArea<areas.size();_testArea++){
      int _index =_action*areas.size() + _testArea;
      _evals[_index]=0;
      if (_AI.gold<_a.cost){
        continue;
      }
      switch(_action){
        case ACTION_ARMY:
            selectableAreas(_action);
            _evals[_index]=evalAttack(_testArea,_action);
        break;
        case ACTION_FORT:
            selectableAreas(_action);
            _evals[_index]=evalFort(_testArea);
        break;
        case ACTION_FLEET:
            selectableAreas(_action);
            _evals[_index]=evalAttack(_testArea,_action);
        break;
        case ACTION_VILLAGE:
          selectableAreas(_action);
          _evals[_index]=evalVillage(_testArea);
        break;
        case ACTION_UNIVERSITY:
          selectableAreas(_action);
          _evals[_index]=evalUni(_testArea);
        break;
        default:
        break;
      }
    }
  }
  int _bestEval=getBestEval(_evals);
  if (_evals[_bestEval]==0){
    endTurn();
    return;
  }
  
  tryAction(_bestEval);
  
}

public float evalAttack(int _testArea, int type){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( !_area.selectable ) return 0;
  if(type==ACTION_ARMY) {_score=0.9f + random(0.2f);}
  if(type==ACTION_FLEET) {_score=0.60f + random(0.6f);}
  _score *= (1 + (_area.income*0.09f));
  if(_AI.dominance > PApplet.parseFloat(1/playersAlive())){_score *= 1.1f;}
  if(_area.fort){_score *= 0.6f;}
  if(_area.city){_score *= 1.4f;}
  if(_area.mine){_score *= 1.4f;}
  if(_area.forest){_score *= 1.5f;}
  _score *= 1 + _area.neighborValue()/2.5f;
  if(_area.ownedBy==0){
    _score *= 4.8f;
  }else{
    _score *= _AI.aiDiplomacy[_area.ownedBy-1];
  }
  if(_area.ownedBy>0){
    Player _owner = getController(_area);
    if(_owner.dominance > 1.0f/PApplet.parseFloat(playersAlive())+0.08f){_score *= 1+_owner.dominance*2;}
    _score *= 1 + (1 - (_owner.controlFactor()) - _AI.controlFactor()) * 0.9f;
    if(campaignMode && hostile){
        if( (gameState==STATE_MAPTEST && _owner.id==1) || _owner.isHuman) {
              _score *= ( 1+( random( min(level,60))/35 ) );
        }
    }
  } 
  _score *= (1-(_area.neighbors.size()*0.05f));
  if(_AI.gold<4) _score *= 0.9f;
  if(type==ACTION_ARMY) _score *= _AI.aiPreference[ACTION_ARMY];
  if(type==ACTION_FLEET) _score *= _AI.aiPreference[ACTION_FLEET];
  return _score;
}

public float evalFort(int _testArea){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( _area.selectable ){_score=0.9f + random(0.18f);}
  _score *= (1 + (_area.income*0.3f));
  _score *= (_area.mine) ? 1.35f : 1 ;
  //_score *= ((_area.neighbors.size()*0.1));
  _score *= (_area.city) ? 0.05f : 1 ;
  _score *= (0.9f + _area.contested * 0.25f);
  if(_AI.dominance > 0.6f){_score *= 0.6f;}
  if(_AI.dominance < 0.5f/playersAlive()){_score *= 1.6f;}
  if(_area.uni){_score*=0.5f;}
  if(_area.forest){_score *= 1.4f;}
  if(_area.mountain){_score *= 1.2f;}
  if(isAreaBorder(_area)){
    _score*=4;
  }else{
    _score*=0.5f;
  }
  if(_AI.areaControlledCount()==1) _score*=4;
  _score *= _AI.aiPreference[ACTION_FORT];
  return _score;
}
public boolean isAreaBorder(Area a){
  for(int aNeighbors : a.neighbors){
    Area n = areas.get(aNeighbors);
    if(n.ownedBy == 0) continue;
    if(n.ownedBy != currentPlayer().id) return true;
  }
  return false;
}


public float evalVillage(int _testArea){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( _area.selectable ){_score=1 + random(-0.4f,0.3f);}
  float _ns = 1;
  for(int i=0;i<_area.neighbors.size();i++){
    Area _a = areas.get(_area.neighbors.get(i));
    if(_a.ownedBy != playerTurn){_ns -= 0.35f;}
  }
  _score *= _ns;
  if (_area.fort){_score *= 0.2f;}
  _score *= (1.2f-(_area.neighbors.size()*0.1f));
  if(_area.mine){_score *= 0.4f;}
  float _c = _area.contested * -0.2f;
  
  if(isAreaBorder(_area)){
    _score*=0.1f;
  }else{
    _score*=1.1f;
  }
  
  if(_area.invadedBy == _AI.id){_score *= 0.1f;}
  _score *= 1 + constrain(_c,-100,0.2f);
  if(_AI.dominance > 0.6f){_score *= 0.6f;}
  if(_area.uni){_score=0;}
  if(_AI.areaControlledCount()==1) _score*=0;
  _score *= _AI.aiPreference[3];
  return _score;
}

public float evalUni(int _testArea){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( _area.selectable && countUnis(_AI) <= _AI.maxUni){_score=1 + random(-0.4f,0.3f);}
  _score *= (1 - _area.contested * 0.25f);
  _score*=_AI.uniFondness;
  if (_area.fort){_score *= 0.2f;}
  if (_area.city){_score *= 0.2f;}
  if(_area.invadedBy == _AI.id){_score *= 0.1f;}
  boolean upgradePossible=false;
  if(_AI.dominance < 0.6f){_score *= 1.5f;}
  _score *= _AI.aiPreference[4];
  for(int i=0;i<_AI.actions.size();i++){
    if( !_AI.actions.get(i).upgraded ){
      upgradePossible=true;
    }
  }
  if(_AI.areaControlledCount()==1) _score*=0;
  if (!upgradePossible){_score = 0;}
  return _score;
}

public void adjustDiplomacy(int _defender){
  int livingPlayers=playersAlive();
  for(int i = 0; i<players.size();i++){
    Player p = players.get(i);
    if(p.isAlive) livingPlayers++;
  }
  if(livingPlayers==0 || _defender<=0) return;
  float multiplier = 0.5f;//(currentPlayer().isHuman & campaignMode) ? 0.65 : 0.5;
  //if(gameState==STATE_MAPTEST && currentPlayer().id==1) multiplier = 0.65;
  float baseVal = currentPlayer().dominance * multiplier;
  float adjustWith=constrain(baseVal,0.001f,1);
  
  Player defendingPlayer = players.get(_defender-1);
  defendingPlayer.aiDiplomacy[playerTurn-1] = constrain(defendingPlayer.aiDiplomacy[playerTurn-1] + adjustWith,0.1f,4);
  
  for(int i = 0; i<players.size();i++){
    Player p = players.get(i);
    if(p.isAlive){
      p.aiDiplomacy[currentPlayer().id-1] = constrain(p.aiDiplomacy[currentPlayer().id-1] - (adjustWith/livingPlayers*1.05f),0.1f,4);
    }else{
      p.aiDiplomacy[currentPlayer().id-1] = 0.0001f;
    }
  }
}

public void doAIUpgrade(Player _AI){
  int actionCount = (campaignMode) ? constrain(level-1,0,4) : 4 ;
  
  while(_AI.hasUpgrade()){
    int _try = PApplet.parseInt(random(actionCount));
    if(!_AI.actions.get(_try).upgraded){
      _AI.actions.get(_try).upgraded=true;
      _AI.research-=RESEARCH_LIMIT;
    }
  }
}

public void doTactics(Player p){
  float prefSum=0;
  for(int i=0;i<p.actions.size();i++){
    prefSum+=p.aiPreference[i];
  }
  boolean noTactic = (prefSum<2.5f);
  if(random(1)<0.1f || noTactic){
    p.setAIPreference();
    int tactic=PApplet.parseInt(random(0,p.actions.size()));
    p.aiPreference[tactic] = random(100000.5f,200000);
  }
}

public void printDiplomacyValues(Player _AI){
 for(int i=0;i<_AI.aiDiplomacy.length;i++){
   if(_AI.aiDiplomacy[i] > 1.5f){
     println("PLR "+_AI.id+" DIPLOMACY VS " + (i+1)+":", _AI.aiDiplomacy[i]);
   }
 }
}

public int getBestEval(float[] _evals){
  int _bestEval=0;
  for(int _i=0;_i<_evals.length;_i++){
    if( _evals[_i] > _evals[_bestEval]){
      _bestEval=_i;
    }
  }
  return _bestEval;
}

public void tryAction(int _bestEval){
  int _tryAction = PApplet.parseInt(_bestEval/areas.size());
  int _tryArea = _bestEval % areas.size();
  Area _area = areas.get(_tryArea);

  unselectActions(-1);
  selectableAreas(_tryAction); 
  Action _a= currentPlayer().actions.get(_tryAction);
  _a.selected=true;

  areaClicked(_area.name);
}

public boolean stupefyAI(Player _AI){
  if(_AI.id >1 && level < 8 && random(15)>level+9 && iterations > 1) {
    endTurn();
    return true;
  }
  return false;
}

class Action {
  PImage pic,pic2;
  int type;
  int cost;
  int invItem;
  int x1,y1,x2,y2;
  boolean mouseOver,selected,upgraded;
  String info;
  

  Action(int _type){
    type=_type;
    mouseOver=false;
    upgraded=false;
    switch (type){
      
      case ACTION_ARMY : 
        pic=action_army;
        pic2=action_army2;
        cost=COST_ARMY;
        info="Army: Attack an adjacent area.";
      break;
      case ACTION_FORT : 
        pic=action_fort;
        pic2=action_fort2;
        cost=COST_FORT;
        info="Fort: Increase defense.";
      break;
      case ACTION_FLEET : 
        pic=action_fleet;
        pic2=action_fleet2;
        cost=COST_FLEET;
        info="Fleet: Attack any area.";
      break;
      case ACTION_VILLAGE : 
        pic=action_village;
        pic2=action_village2;
        cost=COST_VILLAGE;
        info="Village: Increase income.";
      break;
      case ACTION_UNIVERSITY : 
        pic=action_uni;
        
        pic2=action_uni;
        cost=COST_UNIVERSITY;
        info="University: Generates research points for upgrades.";
      break;
      default : 
      break;
    }
 
  }
  
  public void update( int _invItem, boolean canPressAction){
    invItem=_invItem;
    x1=0;
    y1=scaledScrHeight - (2 + invItem) * pic.height;
    x2=x1+pic.width;
    y2=y1+pic.height; 
    mouseOver=false;
    if(canPressAction && mouseX*(1/myScale)>x1 && mouseX*(1/myScale)<x2*2 && mouseY*(1/myScale) > y1 && mouseY*(1/myScale) < y2){
        mouseOver=true;
    }
    if(mouseOver){
      printInfo(info + "               Cost: "+cost);
      if(mouseClicked){
        mouseClicked=false;
        tint(120,110,110);
        unselectActions(type);
        selectableAreas(type);
      }else{
        tint(tintByOwner(0));
        tint(255,255,255,220);
      }
    }
    Player _p = currentPlayer();
    if(_p.gold<cost){
      tint(150,90,75,220);
    }
    if(!upgraded){
      image(pic,x1,y1);
    }else{
      image(pic2,x1,y1);
    }
    image(price_tag,x1+pic.width,y1);
    if(selected){
      if(sMouseX() < 32 && sMouseY() > 32 && sMouseY() < 94){
      }else{
      image(action_selected,x1,y1);
      }
    }
    scale(0.5f);
   fill(20,20,10);
   //text(cost,(1+x1+pic.width*1.5)*2,(11 + y1)*2);
   //text(cost,(x1+pic.width*1.5)*2,(11 + y1)*2);
   text(cost,(x1+pic.width*1.5f)*2,(10 + y1)*2+1);
   fill(230,190,100);
   if(_p.gold<cost){
      fill(150,100,90,220);
    }
   text(cost,(x1+pic.width*1.5f)*2,(10 + y1)*2);
   
   scale(2);
  noTint();
  }
}

class Area { 
  String name;
  int x1,y1,x2,y2;
  PImage pic;
  PImage mouseOverPic;
  int areaColor;
  boolean mouseOver;
  int namestring;
  int ownedBy;
  int id;
  int income;
  IntList neighbors = new IntList();
  boolean selectable,fort,mine,city,uni,forest,mountain;
  int contested,invadedBy;
  boolean chokePoint;
  float defense;
  int defenseFrame;
  float independence;
  
  Area (int _x1,int _y1,int _x2,int _y2,PImage _im, PImage _moim, int _c, int _id) {
    id=_id; 
    x1=_x1; 
    y1=_y1;
    x2=_x2;
    y2=_y2;
    if((random(1)<0.5f && level>7)){
      int feature =PApplet.parseInt(random(8));
      switch(feature){
        case 0 :
          mine=true;
        break;
        case 1 :
          fort=true;
        break;
        case 2 :
          city=true;
        break;
        case 3 :
          mountain=true;
        break;
        case 4 :
          forest=true;
        break;
        default:
        break;
      }
    }
    namestring =PApplet.parseInt( random(name1.length));
    name=name1[constrain(namestring,0,name1.length-1)];
    namestring =PApplet.parseInt( random(name2.length) );
    name=name + name2[constrain(namestring,0,name2.length-1)];
    invadedBy=0;
    defense=1;
    pic=_im;
    pic.mask(_moim);
    mouseOverPic = _moim;
    mouseOverPic.mask(_moim);
    areaColor=_c;
    mouseOver=false;
    selectable = false;  
    independence = random(1);
  } 
  public float rebelScore(){
     if(ownedBy<1) return 0;
     float d = players.get(ownedBy-1).dominance;
     float rs=independence;
     if(neighbors.size()>0){
         for(int i = 0;i<neighbors.size();i++){
             Area n = areas.get(neighbors.get(i));
             if (n.ownedBy<1) rs*=2;
         }
     }
     rs *= d;
     if(fort) rs*=0.5f;
     return rs;
  }
  public void drawUpgrades() { 
    int _adj=0;
    int _yAdj = (mouseOver) ? -7:-6;
    if(forest){
      image(forestImg,x1+_adj+(pic.width*0.35f)-4,y1+(pic.height*0.35f+_yAdj-2));
    }
    if(mountain){
      image(mountainImg,x1+_adj+(pic.width*0.35f)-4,y1+(pic.height*0.35f+_yAdj-2));
    }
    if(mine){
      _adj = (fort || city || uni) ? 0:-3;
    }
    if(uni){
      _adj = (fort || city || mine) ? 0:-3;
    }
    if(fort){
      Boolean _upg=false;
      if(ownedBy>0){
        if(players.get(ownedBy-1).isActionUpgraded(1)){_upg=true;}
      }
      if(_upg){
        image(upgrade_fort2,x1-_adj-2+(pic.width*0.35f),y1+(pic.height*0.35f)+_yAdj);
      }else{
        image(upgrade_fort,x1-_adj-2+(pic.width*0.35f),y1+(pic.height*0.35f)+_yAdj);
      }
    }
    if(city){
      Boolean _upg=false;
      if(ownedBy>0){
        if(players.get(ownedBy-1).isActionUpgraded(3)){_upg=true;}
      }
      if(_upg){
        image(upgrade_city2,x1-_adj-2+(pic.width*0.35f),y1+(pic.height*0.35f)+_yAdj);
      }else{
        image(upgrade_city,x1-_adj-2+(pic.width*0.35f),y1+(pic.height*0.35f+_yAdj));
      }
    }
    if(uni){
      image(upgrade_uni,x1+_adj-2+(pic.width*0.35f),y1+(pic.height*0.35f+_yAdj));
    }
    if(mine){
      image(upgrade_mine,x1+_adj+(pic.width*0.35f),y1+(pic.height*0.35f+_yAdj+4));
    }
    
    
    if (uni && city) println("Error: Has");
    if(mouseOver) showDefense();
    if(showAreaInfo) drawAreaText();
  }
  public void drawArea(boolean canShowMouseOver) {
    calcIncome();
    mouseOver=false;
    if(canShowMouseOver && mouseX*(1/myScale)>x1 && mouseX*(1/myScale)<x2 && mouseY*(1/myScale) > y1 && mouseY*(1/myScale) < y2){
      if(pic.get(PApplet.parseInt(mouseX*(1/myScale)-x1),PApplet.parseInt(mouseY*(1/myScale)-y1)) != 0){
        mouseOver=true;
      }
    }
    int _lift = (mouseOver) ? -1 : 0 ;
    if(mouseOver){
      tint(0,150);
      image(pic,x1,y1);
      tint(150,150,150);
      image(pic,x1,y1+_lift);
      
    }
    
    tint(tintByOwner(ownedBy));
    Player _p = currentPlayer();
    if (selectable && _p.isHuman){
      if(ownedBy>0){
        tint( tintByOwner2( ownedBy, color( PApplet.parseInt(130)*(1+(sin(selectableSine))*0.4f) ) ) );
      }else{
        if (_p.isHuman){tint(PApplet.parseInt(150)*(1+(sin(selectableSine))*0.2f));}
      }
    }
    
    //
    if(mouseOver){
      if(mousePressed){
        tint(60,120,150);
        currentMousePressedArea = name;
      }else{
        printInfo(name+ ". Income: " + income );
        tint( tintByOwner2( ownedBy, color( 190,210,170 ) ) );
        
      }
    }
    image(pic,x1,y1+_lift);
    noTint();
    
  }
  
  public void showDefense(){
    int yShift=-6;
    if(!selectable) return;
    if(!currentPlayer().actions.get(0).selected) return;
    if(level>2 && !currentPlayer().actions.get(2).selected) return;
    int tx = PApplet.parseInt( x1+(x2-x1)*0.5f ) ;
    int ty = PApplet.parseInt( y1+(y2-y1)*0.5f ) + yShift;
    if(ty < 16) ty+=12;
    
    image(getDefenseImg(),tx-10,ty-10+(sin(selectableSine))*0.2f);
  }
  public float neighborValue(){
    if(neighbors.size()<=0) return 1;
    float nVal=0;
    for(int i =0;i<neighbors.size();i++){
      int n = neighbors.get(i);
      Area a = areas.get(n);
      nVal+=a.income;
      if(a.mine) nVal++;
    }
    return nVal;
  }
  
  public void deselect() {
    selectable=false;
    defense=1;
    defenseFrame = 0;
  }
  
  public void reduceDefense() {
    defenseFrame++;
    defenseFrame = min(defenseFrame, defenseImg.length - 1);
    
    if (defenseFrame >= defenseImg.length - 1) {
      defense = 0;
    } else {
      defense -= 0.1f;
    }
  }
  
  public float defense() {
    return defense;
  }
  
  public PImage getDefenseImg(){
    return defenseImg[defenseFrame];
  }
  
  public void drawAreaText(){
    int _inc =income;
    scale(0.5f);
    if(ownedBy>0){
      fill(20,20,10,190);
    }else{
      fill(20,20,10,170);
    }
    
    
    text(name,2+(x1+(x2-x1)*0.5f)*2,5+(y1+(y2-y1)*0.5f)*2);
    text(name,1+(x1+(x2-x1)*0.5f)*2,5+(y1+(y2-y1)*0.5f)*2);
    if(ownedBy>0){
      fill(80+red(tintByOwner(ownedBy)),80+green(tintByOwner(ownedBy)),70+blue(tintByOwner(ownedBy)),210);
    }else{
      fill(150,140,100,220);
    }
    text(name,1+(x1+(x2-x1)*0.5f)*2,4+(y1+(y2-y1)*0.5f)*2);
    //
    
    if(ownedBy>0){
      fill(30,20,10,220);
    }else{
      fill(10,10,10,180);
    }
    
    text( "Income:"+_inc,2+(x1+(x2-x1)*0.5f)*2,5-textHeight+(y1+(y2-y1)*0.5f)*2);
    text( "Income:"+_inc,1+(x1+(x2-x1)*0.5f)*2,5-textHeight+(y1+(y2-y1)*0.5f)*2);
    if(ownedBy>0){
      fill(120+red(tintByOwner(ownedBy)),120+green(tintByOwner(ownedBy)),110+blue(tintByOwner(ownedBy)),230);
    }else{
      fill(150,150,130,220);
    }
    
    text( "Income:"+_inc,1+(x1+(x2-x1)*0.5f)*2,4-textHeight+(y1+(y2-y1)*0.5f)*2);
    scale(2);
  }
  
  public void findNeighbors(){ 
    for(int _ly=y1;_ly<y2+1;_ly++){
      for(int _lx=x1;_lx<x2+1;_lx++){
        int _checkCol=worldMap.get(_lx,_ly);
        if(_checkCol != areaColor && _checkCol != worldMap.get(0,0) && countSurrounding(worldMap, _lx, _ly, areaColor) >0){ 
          for(int _i=0;_i<areas.size();_i++){
            if(areas.get(_i).areaColor == _checkCol){
              boolean _newNeighbor=true;
              for(int u=neighbors.size()-1;u>=0;u--){ 
                if(neighbors.get(u) == _i){ 
                  _newNeighbor = false;
                }
              }
              if(_newNeighbor){
                neighbors.append(_i);
              }
            }
          }
        }
      }
    }
    neighbors.sort();
    for(int u=neighbors.size()-1;u>0;u--){
      if(neighbors.get(u) == id){
        neighbors.remove(u);
      }
    }
    for(int u=neighbors.size()-1;u>0;u--){
      if(u>=1){
        if(neighbors.get(u) == neighbors.get(u-1)){
          neighbors.remove(u);
        }
      }
    }
    calcIncome();
   
  }
  public void calcIncome(){
   income= 1;
   income = (income<0) ? 0 : income;
   if(forest) income++;
   if(city){
     income++;
      if(ownedBy>0 && players.get(ownedBy-1).isActionUpgraded(3)){
        income++;
      }
    }
  }
  
  public void findChokePoints(){
    chokePoint = false;
    if(neighbors.size()>1){
      chokePoint = true;
      int chokepointDisprovalPoints = 0;
      for(int _n1=0;_n1<neighbors.size();_n1++){
        Area _neighbor1 = areas.get(neighbors.get(_n1));
        for(int _n2=0;_n2<neighbors.size();_n2++){ 
          Area _neighbor2 = areas.get(neighbors.get(_n2));
          if(_neighbor1.id != _neighbor2.id ){
            for(int _n2list=0;_n2list<_neighbor2.neighbors.size();_n2list++){
              if(_neighbor2.neighbors.get(_n2list) == _neighbor1.id ) {
                chokepointDisprovalPoints++;
              }
            }
          }
        }
        if(chokepointDisprovalPoints == neighbors.size()) {
          chokePoint = false; 
        }
      }
    }
  }
} 

class Bounds {
  int x1,y1,x2,y2;
  Bounds (int _x1,int _y1,int _x2,int _y2){
    x1=_x1;
    y1=_y1;
    x2=_x2;
    y2=_y2;
    
  }
}
class Button {
  PImage pic;
  int inGameState;
  int x1,y1,x2,y2;
  boolean mouseOver,selected, enabled=true;
  String info;
  
  Button(int _x1,int _y1, PImage _pic, int _state){
    pic = _pic;
    x1=_x1;
    y1=_y1;
    x2=pic.width;
    y2=pic.height;
    inGameState=_state;
  }
  
  public void update(){
      if(!enabled) return;
    if(gameState!=inGameState) return;
    
    if(mouseOver()){
      if(mousePressed){
        tint(120,110,110);
        selected=true;
      }else{
        tint(255,255,255,220);
      }
    }
    image(pic,x1,y1);
    noTint();
  }
  public boolean mouseOver(){
    if(enabled && mouseX*(1/myScale) > x1 && mouseX*(1/myScale) < x1+x2 && mouseY*(1/myScale) > y1 && mouseY*(1/myScale) < y1+y2){
      return true;
    }
    return false;
  }
}

class TextButton {
  int x;
  int y;
  PImage btnImage;
  PImage btnImage_Down;
  int id;
  String caption;
  boolean enabled;
  int margin = 0;
  float textScale = 1;
  
  TextButton (String caption, int x, int y, PImage btnImage, int id){
    this.x=x;
    this.y=y;
    this.btnImage=btnImage;
    this.id=id;
    this.caption=caption;
    this.enabled=true;
  }
  public TextButton setTextScale(float textScale){
    this.textScale=textScale;
    return this;
  }
  public boolean contains (int xx, int yy){ 
    return (xx > x + margin && xx < x + btnImage.width -margin && yy > y + margin && yy  < y + btnImage.height - margin);
  }
  
  public void render(){
    textAlign(CENTER,CENTER); 
    if (enabled){
      if ( contains( sMouseX(),sMouseY() ) ){
        if (mousePressed == true) {
          renderPressed();
        } else {
          renderUnpressed();
        }
      } else {
        renderNormal();
      }
    } else {
      renderDisabled();
    }
    fill(255,255);
    tint(255,255);
    textAlign(CENTER); 
  }
  
  public void renderNormal(){
    image(btnImage,x,y);
    float tx = (x + (btnImage.width*0.5f))/textScale;
    float ty = (y + btnImage.height*0.5f)/textScale;
    scale(textScale);
    fill(20,10,10,200);
    text(caption, tx, ty+1);
    fill(200,180,160,255);
    text(caption, tx, ty);
    scale(1/textScale);
  }
  
  public void renderDisabled(){
    float tx = (x + (btnImage.width*0.5f))/textScale;
    float ty = (y + btnImage.height*0.5f)/textScale;
    image(btnImage,x,y);
    scale(textScale);
    fill(255,100);
    text(caption, tx, ty+1);
    tint(255, 100);
    image(blackImg,x,y,btnImage.width,btnImage.height);    
    scale(1/textScale);
  }
  
  public void renderPressed(){
    float tx = (x + (btnImage.width*0.5f))/textScale;
    float ty = (y + btnImage.height*0.5f)/textScale;
    image(btnImage,x,y);
    scale(textScale);
    text(caption, tx, ty);
    text(caption, tx, ty);
    scale(1/textScale);
    tint(155, 100);
    image(blackImg,x,y,btnImage.width,btnImage.height);
  }
  
  public void renderUnpressed(){
    float tx = (x + (btnImage.width*0.5f))/textScale;
    float ty = (y + btnImage.height*0.5f)/textScale;
    image(btnImage,x,y);
    tint(255,255,225, 20);
    image(whiteImg,x,y,btnImage.width,btnImage.height); 
    scale(textScale);
    fill(30,20,20,200);
    text(caption, tx, ty+1);
    fill(250,230,200,255);
    text(caption, tx, ty);
    scale(1/textScale);
  }
  
  public void update(){
  }
}
public String[] loadStringArray(String file){
  String f = file;
  String[] _l = loadStrings(f+".txt");
  println("loaded file:",f);
  return _l;
}
public void saveStringArray(String file, String[] strings){
  String f=file+".txt";
  String[] s = strings;
  saveStrings(f, s); 
  println("saved file:",f);
}

public void loadSound(){
  fail1 = minim.loadFile("fail1.wav");
  nogo = minim.loadFile("nogo1.wav");
  gold1 = minim.loadFile("gold1.wav");
  
  gold2 = minim.loadFile("gold2.wav");
  battle1 = minim.loadFile("battle1.wav");
  fort1 = minim.loadFile("fort1.wav");
  castle1 = minim.loadFile("castle1.wav");
  fleet1 = minim.loadFile("fleet1.wav");
  trade1 = minim.loadFile("trade1.wav");
  plague1 = minim.loadFile("plague1.wav");
  rebellion1 = minim.loadFile("rebellion1.wav");
  song = minim.loadFile("Sweltering Expansion.mp3");
}


public void loadCurrent(){
    int levelSeed;
      String f = campaignFolder+"/worldmap"+level+".png";
      //println(f);
  if(campaignMode){
    worldMap=loadImage(f);
    //worldMap=loadImage("campaign1/worldmap69.png");
    levelSeed=worldMap.get(0,worldMap.height-1);
    randomSeed(levelSeed);
  }else{
    worldMap=loadImage("generated_current.png");
    levelSeed=worldMap.get(0,worldMap.height-1);
    randomSeed(levelSeed);
  }
}

public String printRGB(int col){
    return PApplet.parseInt(red(col))+", "+PApplet.parseInt(green(col))+", "+PApplet.parseInt(blue(col));
}

public void saveCurrent(int levelSeed){
  if(campaignMode){
    worldMap.set(0,worldMap.height-1,levelSeed);
    worldMap.save("data/"+campaignFolder+"/worldmap"+level+".png"); 
  }else{
    worldMap.set(0,worldMap.height-1,levelSeed);
    worldMap.save("data/generated_current.png"); 
  }
}

public void loadImages(){//splash;
  splash=loadImage("splash.png");
  bg=loadImage("bg.png");
  gui_bg=loadImage("gui_bg.png");
  water=loadImage("water.png");
  glow=loadImage("glow.png");
  whiteImg=loadImage("white.png");
  blackImg=loadImage("black.png");
  forestImg=loadImage("forest.png");
  mountainImg=loadImage("mountain.png");
  upgrade_pane=loadImage("upgrade_pane.png");
  info_pane=loadImage("info_pane.png");
  player_pane=loadImage("player_pane1.png");
  player_pane_colorplate=loadImage("player_pane_colorplate1.png");
  player_pane_current=loadImage("player_pane_current1.png");
  action_army=loadImage("action_army.png");
  action_army2=loadImage("action_army2.png");
  action_fort=loadImage("action_fort.png");
  action_fort2=loadImage("action_fort2.png");
  action_fleet=loadImage("action_fleet.png");
  action_fleet2=loadImage("action_fleet2.png");
  action_village=loadImage("action_trade.png");
  action_village2=loadImage("action_trade2.png");
  action_uni=loadImage("action_uni.png");
  button=loadImage("bottombarbutton.png");
  price_tag=loadImage("price_tag.png");
  action_selected=loadImage("action_selected.png");
  //warning=loadImage("warning.png");
  upgrade_fort=loadImage("upgrade_fort.png");
  upgrade_mine=loadImage("upgrade_mine.png");
  upgrade_city=loadImage("upgrade_city.png");
  upgrade_fort2=loadImage("upgrade_fort2.png");
  //upgrade_mine2=loadImage("upgrade_mine2.png");
  upgrade_city2=loadImage("upgrade_city2.png");
  upgrade_uni=loadImage("upgrade_uni.png");
  end_turn=loadImage("end_turn.png");
  dead=loadImage("dead.png");
  playButtonImg=loadImage("playButtonImg.png");
  portrait1=loadImage("portrait1.png");
  portrait2=loadImage("portrait2.png");
  portrait3=loadImage("portrait3.png");
  portrait4=loadImage("portrait4.png");
  iconHuman=loadImage("human.png");
  PImage defMap=loadImage("defense.png");
  
  defenseImg = new PImage[PApplet.parseInt(defMap.width/16)];
  for(int i=0;i<defenseImg.length;i++){
    defenseImg[i] = createImage(16, 16, ARGB);
    defenseImg[i].copy(defMap,i*16,0,16,16,0,0,16,16);
  }
  plague=loadImage("plague.png");
  rebellion=loadImage("rebellion.png");
  ruta1=loadImage("ruta1.png");
  ruta2=loadImage("ruta2.png");

  PImage[][] animSprites = cutAnim(loadImage("anims.png"),16);  
  animBomb = animSprites[0];
  animCheck = animSprites[1];
  animNope = animSprites[2];
  animGold = animSprites[3];
  animPlague = animSprites[4];
  animFight = animSprites[5];
  animSmoke = animSprites[6];
  animResearch = animSprites[7];
}
public void loadSettings(){
    String[] l = loadStrings("settings.txt");
	for(String line : l){
    	if(line.startsWith("//")) continue;
    	String[] setting = split(line,':');
    	if( setting[0].equals("scale") ) myScale = PApplet.parseInt(setting[1]);
        if( setting[0].equals("map") ){ level = PApplet.parseInt(setting[1]);}
        if( setting[0].equals("lastMap") ) maxMaps = PApplet.parseInt(setting[1]);
        if( setting[0].equals("campaignFolder") ) campaignFolder = setting[1];
        if( setting[0].equals("playMusic") ) playMusic = PApplet.parseBoolean(setting[1]);
        if( setting[0].equals("playSound") ) playSound = PApplet.parseBoolean(setting[1]);
        if( setting[0].equals("showAreaInfo") ) showAreaInfo = PApplet.parseBoolean(setting[1]);
	}
    println("SETTINGS LOADED");
}
public void saveSettings(){
  String[] setting = new String[8];
  setting[0] = "// With this you could make your own campaign by editing lots of maps, and putting them in a new folder next to the others.";
  setting[1] = "map:"+str(level);
  setting[2] = "lastMap:"+str(maxMaps);
  setting[3] = "campaignFolder:"+campaignFolder;
  setting[4] = "scale:"+str(myScale);
  setting[5] = "playMusic:"+str(playMusic);
  setting[6] = "playSound:"+str(playSound);
  setting[7] = "showAreaInfo:"+str(showAreaInfo);
  saveStrings("data/settings.txt", setting); 
    println("SETTINGS SAVED");
}
public void startCampaignMap(){
    gameState=STATE_GENERATOR;
    gameSetup();
}

public int sMouseX(){
  return PApplet.parseInt(mouseX/myScale);
}
public int sMouseY(){
  return PApplet.parseInt(mouseY/myScale);
}

public Player currentPlayer() {
  return players.get(playerTurn - 1);
}

public boolean isEven(int n){
  return (n&1)==0;
}
public boolean isOdd(int n){
  return (n&1)==1;
}

public void checkIfPlayerIsDead() {
  noPlayers=players.size();
  for (int i=players.size()-1;i>=0;i--) {
    Player _p = players.get(i);
    boolean _hasArea=false;
    for (int a=0;a<areas.size();a++) {
      Area _a = areas.get(a);
      if (_p.id==_a.ownedBy) {
        _hasArea=true;
      }
    }
    if (!_hasArea && _p.isAlive) { 
      killPlayer(_p);
    }
    if (!_p.isAlive) { 
      noPlayers--;
      _p.isHuman=false;
    }
  }
}
public void killPlayer(Player p){
  p.isAlive=false;
  screenShake=3;
  p.gold=0; 
  p.income=0;
  for(int i=0;i<p.actions.size();i++){
    p.actions.get(i).upgraded=false;
  }
}
public Player getController(Area a){
  return players.get(a.ownedBy-1);
}

public int getIncome(Player _plr){
  int _inc=baseIncome;
  for(int _i=0;_i<areas.size();_i++){
    Area _a = areas.get(_i);
    if(_a.ownedBy==_plr.id && _a.invadedBy==0){
      _inc+=_a.income;
    }
  }
  return _inc;
}


public void runPlayer() {
  if(doStartTurn) startTurn();
  if(currentPlayer().isHuman){
    return;
  }
  if (aiDelay<millis()) {
    aiDelay=millis()+aiSpeed;
    if(gameState==STATE_MAPTEST) aiDelay = millis();
    iterations++;
    AI_turn();
    if (iterations>=currentPlayer().aiIterations || currentPlayer().gold<1 || !currentPlayer().isAlive) {
      iterations=0;
      endTurn();
    }
  }
  actionSelection(currentPlayer()); 
}

public String getActionName(int actionId){
    switch(actionId){
        case ACTION_ARMY:
        	return "ARMY";
        case ACTION_FORT:
            return "FORT";
        case ACTION_FLEET:
            return "FLEET";
        case ACTION_VILLAGE:
            return "VILLAGE";
        case ACTION_UNIVERSITY:
            return "UNIVERSITY";
        default:
            return "ERROR";
    }
}

public void startTurn(){
  doStartTurn=false;
  Player p = currentPlayer();
  p.setIncome();
  p.calcDominance();
  for(int i = 0;i<players.size();i++){  
  }
  for(int i = 0;i<p.actions.size();i++){
    p.actions.get(i).selected=false;
  }
  if(!currentPlayer().hasUpgrade()) p.actions.get(0).selected=true;
  selectableAreas(0);
  if(!currentPlayer().isHuman && level > 10) doTactics(currentPlayer());
  
}

public void actionSelection(Player _currP) {
  for (int i=0;i<_currP.actions.size();i++) {
    Action _action = _currP.actions.get(i);
    if (_action.selected) return;
  }
    deselectAreas();

}

public void deselectAreas() {
  for (Area area : areas) {
    area.deselect();
  }
}

public void selectAction(int actionId){
  for (int i =0;i<currentPlayer().actions.size();i++){
    currentPlayer().actions.get(i).selected = false;
  }
  currentPlayer().actions.get(actionId).selected = true;
  //unselectActions(actionId);
  selectableAreas(actionId);
}

public void endTurn() {
  iterations=0;
  playerTurn++;
  if (playerTurn>playerCount) {
    gameTurn++;
    playerTurn=1;
    reduceContested();
  }
  deselectAreas(); 
  
  Player _p = currentPlayer();
  if (gameOn && _p.isAlive) {
    collectGold();
  } 
  _p.aggression = (_p.aggression > 0) ? _p.aggression-0.2f : 0;
  doStartTurn=true;
}

public void reduceContested(){
    for (int i=0;i<areas.size();i++) {
    Area a = areas.get(i);
    if(a.contested>5){a.contested=5;}
    if(a.contested<-5){a.contested=-5;}
    a.contested--;
  }
}

public void winCheck() {
  if(noAnims) autoRun=true;
  if (gameOn) {
     checkIfPlayerIsDead();
    if (noPlayers<=1) {
      gameOn=false;
      Player _p = players.get(0);
      if(_p.isAlive && !noAnims && campaignMode){
        level++;
        saveSettings();
      }
    }
    
  } //else {
  if(!gameOn){
    Player _p = players.get(0);
    if(_p.isAlive){
      waitForClick=true;
      pauseTimer=millis()+500;
      gameState = STATE_GAMEWON;
    }else{
      loadMap=true;
      gameState = STATE_GAMEOVER;
      if(autoRun){gameSetup();}
    }
  }
}

public void collectGold() {
  
  doEvent();
  Player _p = currentPlayer();
  _p.gold+=_p.income;
  for (int i=0;i<areas.size();i++) {
    Area a = areas.get(i);
    if (a.ownedBy == playerTurn) {
      if(a.invadedBy != playerTurn) {
        for (int g=0;g<a.income;g++) {
          if (_p.isAlive) {
            playAreaAnim(a, animGold, 0.6f,true,PApplet.parseInt(random(2, 15)));
            if(_p.isActionUpgraded(3)){
            }
            playSound("gold");
          }
        }
        if(a.mine && random(1) < 0.20f && _p.isAlive){
          for(int _i=0;_i<8;_i++){
            playAreaAnim(a, animGold, 0.6f,true,PApplet.parseInt(random(2, 15)));
            playSound("gold");
          }
        }
        if(a.uni && _p.isAlive){
          playAreaAnim(a, animResearch, 0.4f,true,PApplet.parseInt(random(2, 15)));
          _p.research++;
        }
      } else {
        a.invadedBy = 0;
        playAreaAnim(a, animSmoke, 0.17f,true,PApplet.parseInt(random(2, 15)));
      }
    }
  }
}

public void playAreaAnim(Area a, PImage[] anim, float speed, boolean variedPos, float delay) {
  playAreaAnim(a,anim,speed,variedPos,delay,0,0);
}

public void playAreaAnim(Area a, PImage[] anim, float speed, boolean variedPos, float delay, int xShift, int yShift) {
  if (noAnims) {
    return;
  }
  if(variedPos){
    anims.add (new Animation(anim, PApplet.parseInt(a.x1+a.pic.width*0.5f-8+random(-10, 10))+xShift, PApplet.parseInt(a.y1+a.pic.height*0.5f-8+random(-10, 10))+yShift, speed).setDelay(delay));
  }else{
    anims.add (new Animation(anim, PApplet.parseInt(a.x1+a.pic.width*0.5f-8)+xShift, PApplet.parseInt(a.y1+a.pic.height*0.5f-8)+yShift, speed).setDelay(delay));
  }
}

public void areaClicked(String _areaName) {
  
  if( mouseButton == RIGHT ) RMB =true;
  int _type=-1;
  Area _withArea =areas.get(0);
  Player _player = currentPlayer();

  for (int i=0;i<areas.size();i++) { 
    Area _a = areas.get(i);
    if (_a.name==_areaName) {
      _withArea=_a;
    }
  }

  for (int i=0;i<_player.actions.size();i++) { 
    Action _action=_player.actions.get(i);
    if (_action.selected) {
      _type=i;
      if (!_withArea.selectable || _action.cost>_player.gold) {
        selectableAreas(_type);
        if (_player.isHuman) {
          playSound("nogo");
          playAreaAnim(_withArea, animNope, 0.3f,false,1);
          RMB=false;
          return;
        }
      } 
      else {
        _player.gold -= _action.cost;
        float odds;
        switch(_type) {
        case 0 : // army
          _player.aggression += 0.1f;
          adjustDiplomacy(_withArea.ownedBy);
          playAreaAnim(_withArea, animBomb, 0.3f,false,0);
          odds=0.0f;
          if (_withArea.ownedBy !=0 ) {
            odds=0.3f;
          }
          if (_withArea.fort) {
            odds=0.8f;
            if(_withArea.ownedBy>0){
              Player _defender = players.get(_withArea.ownedBy-1);
              if(_defender.isActionUpgraded(1)){
                odds=1.4f;
              }
            }
            if(currentPlayer().isActionUpgraded(0)){odds*=0.5f;}
          }
          odds *= (_withArea.mountain) ? 1.25f : 1;
          odds *= _withArea.defense();
 
          if (random(1)>odds || (playerTurn==1 && cheat)) {
            _withArea.contested +=2;
            _withArea.invadedBy = playerTurn;
            playSound("battle");
            _withArea.ownedBy=playerTurn;
            _withArea.fort=false;
            _withArea.uni=false;
            playAreaAnim(_withArea, animCheck, 0.3f,false,10);
            
          } else {
            _withArea.reduceDefense();
            playSound("fail");
            if(RMB) areaClicked(_areaName);
          }
          break;
        case 1 : // fort
          playSound("fort");
          playAreaAnim(_withArea, animBomb, 0.3f,false,0);
          if(_withArea.city){_withArea.income -=1;} 
          //_withArea.castle=false;
          _withArea.fort=true;
          _withArea.city=false;
          _withArea.uni=false;
          _withArea.contested=0;
          break;
        case 2 : // fleet
          _player.aggression += 0.1f;
          adjustDiplomacy(_withArea.ownedBy);
          playAreaAnim(_withArea, animBomb, 0.3f,false,0);
          odds=0.3f;
          if (_withArea.ownedBy !=0 ) {
            odds=0.6f;
          }
          if (_withArea.fort) {
            odds=0.9f;
            if(_withArea.ownedBy>0){
              Player _defender = players.get(_withArea.ownedBy-1);
              if(_defender.isActionUpgraded(1)){
                odds=1.4f;
              }
            }
            if(currentPlayer().isActionUpgraded(2)){odds*=0.5f;}
          }
          odds *= (_withArea.mountain) ? 1.25f : 1;
          odds *= _withArea.defense();
          if (random(1)>odds || (playerTurn==1 && cheat)) {
            _withArea.contested+=2;
            _withArea.invadedBy = playerTurn;
            playSound("fleet");
            _withArea.ownedBy=playerTurn;
            _withArea.fort=false;
            _withArea.uni=false;
            playAreaAnim(_withArea, animCheck, 0.3f,false,10);
          } else {
            _withArea.reduceDefense();
            playSound("fail");
            if(RMB) areaClicked(_areaName);
          }
          break;
        case 3 : // trade
          playSound("trade");
          playAreaAnim(_withArea, animBomb, 0.3f,false,0);
          _withArea.income=_withArea.income+1;
          _withArea.fort=false;
          _withArea.city=true;
          _withArea.uni=false;
          _withArea.contested=0;
          break;
        case 4 : // uni
          playSound("trade");
          playAreaAnim(_withArea, animBomb, 0.3f,false,0);
          _withArea.fort=false;
          _withArea.uni=true;
          _withArea.city=false;
          _withArea.contested=0;
          break;
        default:
          break;
        }
        
        selectableAreas(_type);
      }
    }
  }
  for(Player p : players){
    p.setIncome();
    p.calcDominance();
  }
  RMB=false;
}


public void selectableAreas(int _type) { 
  
  Player _player = currentPlayer();
  Action _action = _player.actions.get(_type);
  
  switch(_type) {
  case 0 : // army
    for (int _i=0;_i<areas.size();_i++) { 
      Area _a=areas.get(_i);
      _a.selectable=false;
      if (_a.ownedBy != playerTurn && _player.gold>=_action.cost) {  
        for (int _n=_a.neighbors.size()-1;_n>=0;_n--) { 
          Area _a2=areas.get(_a.neighbors.get(_n)); 
          if (_a2.ownedBy== playerTurn) {
            _a.selectable=true;
          }
        }
      }
    }
    break;
  case 1 : // fort
    for (int _i=0;_i<areas.size();_i++) {
      Area _a=areas.get(_i);
      _a.selectable=false;

      if (_a.ownedBy == playerTurn && _player.gold>=_action.cost && !_a.fort) {
        _a.selectable=true;
      }
    }
    break;
  case 2 : // fleet
    for (int _i=0;_i<areas.size();_i++) { 
      Area _a=areas.get(_i);
      _a.selectable=false;
      if (_a.ownedBy != playerTurn  && _player.gold>=_action.cost) {   
        _a.selectable=true;
      }
    }
    break;
  case 3 : // city
    for (int _i=0;_i<areas.size();_i++) {
      Area _a=areas.get(_i);
      _a.selectable=false;
      if (_a.ownedBy == playerTurn  && _player.gold>=_action.cost  && !_a.city) {
        _a.selectable=true;
      }
    }
    break;
  case 4 : // uni
    for (int _i=0;_i<areas.size();_i++) {
      Area _a=areas.get(_i);
      _a.selectable=false;
      if (_a.ownedBy == playerTurn  && _player.gold>=_action.cost && !_a.uni) {
        _a.selectable=true;
      }
    }
    break;
  default:
    break;
  }
}

public void doEvent() {
  if(level<=4 && campaignMode) return;
  
  if (random(1)<chanceOfEvent) {
    
    Area _withArea;
    int _xx;
    int _yy;
    int _e = (random(1)<eventDistribution) ? 0:1;
    switch (_e) {

    case 0 :
    if(random(3)<countPlayerUnis()) return;
    _withArea = areas.get(PApplet.parseInt(random(areas.size())));
    _xx = PApplet.parseInt(_withArea.x1+_withArea.pic.width*0.5f-8);
    _yy = PApplet.parseInt(_withArea.y1+_withArea.pic.height*0.5f-8);
    	boolean doPlague = (level>=6 || !campaignMode);
      if (_withArea.income>1 && doPlague) {
        if (_withArea.city) {
          _withArea.income=1;
          _withArea.city=false;
          if(gameState!=STATE_MAPTEST) {
            popMessages.add (new PopMessage("PLAGUE!", _xx*2+16, _yy*2+35, 0.9f, 25).setDelay(30));
            playAreaAnim(_withArea, animPlague, 0.25f,false,0,0,-4);
            playSound("plague");
            screenShake=1.5f;
          }
        }
      }
      break;
    case 1 :
    _withArea = getRebelArea(); 
    _xx = PApplet.parseInt(_withArea.x1+_withArea.pic.width*0.5f-8);
    _yy = PApplet.parseInt(_withArea.y1+_withArea.pic.height*0.5f-8);
      boolean doRebellion = (level>=7 || !campaignMode);
      if(currentPlayer().controlFactor()<0.05f) doRebellion = false;
      if(countControlledAreas(currentPlayer()) <= 1) doRebellion = false;
      if(_withArea.ownedBy <= 0) doRebellion = false;
      if(currentPlayer().actions.get(ACTION_VILLAGE).upgraded && random(1)<0.7f) doRebellion = false;
      if (_withArea.ownedBy>0 && doRebellion) {   
          _withArea.ownedBy=0;
          if(gameState!=STATE_MAPTEST) {
            playAreaAnim(_withArea, animFight, 0.3f,false,0,0,-4);
             popMessages.add (new PopMessage("REBELLION!", _xx*2+16, _yy*2+35, 0.9f, 25).setDelay(30));
            playSound("rebellion");
            screenShake=2;
          }
      }
      break;
    default:
      break;
    }
  }
}

public void printPlayerPrefs(){
    
    for(int i=0;i<players.size();i++){
            Player p = players.get(i);
            println("PLAYER ",p.id, "     ARMY:", p.aiPreference[0] , "     FORT:", p.aiPreference[1] , "     FLEET:", p.aiPreference[2] , "     CITY:", p.aiPreference[3] , "     UNIVERSITY:", p.aiPreference[4] );
        }
}

public int countPlayerUnis(){
    Player p = currentPlayer();
    int c=0;
    for(Area a : areas){
        if(a.uni && a.ownedBy==p.id) c++;
    }
    return c;
}

public Area getRebelArea(){
    float rs=0;
    Area a = areas.get(0), c = areas.get(PApplet.parseInt(random(areas.size()))) ;
    for (int i = 0;i<areas.size();i++){
        a=areas.get(i);
        if( a.ownedBy == currentPlayer().id && a.rebelScore() + random(0.05f) > rs ){
            rs=a.rebelScore();
            c=a;
        }
    }
    return c;
}

public int countUnis(Player _pl){
  int _count=0;
  for(int _c=0;_c<areas.size()-1;_c++){
    Area _a = areas.get(_c);
    if (_a.ownedBy == _pl.id && _a.uni){
      _count++;
    }
  }
  return _count;
}

public int countControlledAreas(Player _pl){
  int _count=0;
  for(int _c=0;_c<areas.size()-1;_c++){
    Area _a = areas.get(_c);
    if (_a.ownedBy == _pl.id){
      _count++;
    }
  }
  return _count;
}

public void printShadedFade(String _info, int _x, int _y, int _fade) {
  //scale(0.5);
  String _mes = _info;
  int fading = _fade;
  fill(60, 50, 20, 250*(fading*0.1f));
  text(_mes, 1+_x*0.5f, 1+_y*0.5f);
  text(_mes, _x*0.5f, 1+_y*0.5f);
  fill(230, 190, 100, 250*(fading*0.1f));
  if (_mes.equals("PLAGUE!")) {
    fill(70, 199, 90, 250*(fading*0.1f));
  }
  if (_mes.equals("REBELLION!") ){
    fill(199, 70, 70, 250*(fading*0.1f));
  }
  text(_mes, _x*0.5f, _y*0.5f);
  //scale(2);
}
public void printShadedGold(String _info, int _x, int _y) {
  scale(0.5f);
  fill(30, 25, 10);
  text(_info, 1+_x, 1+_y);
  text(_info, _x, 1+_y);
  fill(170, 140, 60);
  text(_info, _x, _y);
  scale(2);
}
public void printShaded(String _info, int _x, int _y) {
  scale(0.5f);
  fill(30, 25, 10);
  text(_info, 1+_x, 1+_y);
  text(_info, _x, 1+_y);
  fill(170, 160, 130);
  text(_info, _x, _y);
  scale(2);
}
public void printShadedDark(String _info, int _x, int _y) {
  scale(0.5f);
  fill(40, 30, 20);
  text(_info, 1+_x, 1+_y);
  text(_info, _x, 1+_y);
  fill(100, 90, 60);
  text(_info, _x, _y);
  scale(2);
}

public void printInfo(String _info) {
  scale(0.5f);
  fill(60, 50, 20);
  text(_info, 1+scaledScrWidth, scaledScrHeight*2-12);
  text(_info, scaledScrWidth, scaledScrHeight*2-12);
  fill(230, 190, 100);
  text(_info, scaledScrWidth, scaledScrHeight*2-13);
  scale(2);
}
public int tintByOwner(int _withPlayer) {
  switch (_withPlayer) {
  case 0 : // neutral
    return color(140, 140, 130, 190);
  case 1 : // plr1    
    return color(60, 80, 255, 220);
  case 2 : // plr2
    return color(255, 90, 80, 250);
  case 3 : // plr3
    return color(255, 255, 90, 220);
  case 4 : // plr4
    return color(200, 40, 240, 220);
  case 5 : // plr5
    return color(120, 255, 170, 230);
  default:
    return color(0);
  }
}

public int tintByOwner2(int _withPlayer, int _c) {
  int _inCol=_c;
  switch (_withPlayer) {
  case 0 : // neutral
    return color(140, 140, 130, 190);
  case 1 : // plr1    
    return color(red(_inCol) -30, green(_inCol) -20, blue(_inCol)+100, 220);
  case 2 : // plr2
    return color(red(_inCol)+100, green(_inCol) -30, blue(_inCol) -30, 250);
  case 3 : // plr3
    return color(red(_inCol)+80, green(_inCol)+120, blue(_inCol) -40, 220);
  case 4 : // plr4
    return color(red(_inCol)+90, green(_inCol) -50, blue(_inCol)+110, 220);
  case 5 : // plr5
    return color(red(_inCol) -30, green(_inCol)+150, blue(_inCol)+100, 230);
  default:
    return color(0);
  }
}

public void unselectActions(int type) {
  for (int _i=0;_i<players.size();_i++) { 
    Player _p = players.get(_i);
    for (int _u=0;_u<_p.actions.size();_u++) {
      Action _a= _p.actions.get(_u);
      if(_a.type == type){ _a.selected=!_a.selected; }else{ _a.selected=false; }
    }
  }
}


public PImage renderArea(PImage _im) {
  int noiseRange=20;
  PImage _baseImg=_im;
  for (int _y=0;_y<_im.height;_y++) {
    for (int _x=0;_x<_im.width;_x++) {
      if (_baseImg.get(_x, _y) != color(0)) {
        int borderProximity = (countSurrounding(_baseImg, _x, _y, color(0))*14);
        float _centerdist = dist(_x, _y, (_im.width*0.5f), (_im.height*0.5f));
        float pOfHalf = _centerdist/dist(0, 0, (_im.width*0.5f), (_im.height*0.5f));
        int adj = PApplet.parseInt(borderProximity+(100*pOfHalf));
        _im.set(_x, _y, color(red(_im.get(_x, _y))+PApplet.parseInt(random(-noiseRange, noiseRange))-adj, green(_im.get(_x, _y))+PApplet.parseInt(random(-noiseRange, noiseRange))-adj, blue(_im.get(_x, _y))+PApplet.parseInt(random(-noiseRange, noiseRange*0.1f))-adj));
      }
    }
  }


  return _im;
}

public String getRandomTaunt(){
 return "YOUR " + tauntPart1[PApplet.parseInt(random(tauntPart1.length))] + " IS " + tauntPart2[PApplet.parseInt(random(tauntPart2.length))] + "'S " + tauntPart3[PApplet.parseInt(random(tauntPart3.length)) ];
}
public void gameWon() {
  renderGame();
  image(ruta1, 0, -8);
  scale(2);
  image(portrait1, PApplet.parseInt(scaledScrWidth*0.19f), PApplet.parseInt(scaledScrHeight*0.06f) );
  
  scale(0.5f);
  
  scale(2);
  printShaded("CONGRATULATIONS!", PApplet.parseInt(scaledScrWidth*0.50f), PApplet.parseInt(scaledScrHeight*0.50f) );
  printShaded("YOU HAVE VANQUISHED YOUR ENEMIES.", PApplet.parseInt(scaledScrWidth*0.50f), PApplet.parseInt(scaledScrHeight*0.60f) );
  if (millis()<pauseTimer) {
    if(waitForClick){
      	pauseTimer=millis()+500;
    }else{
        loadMap=true;
      	gameState=STATE_GENERATOR;
      	waitForClick=true;
      	gameSetup();
    }
  }else{
    gameState=STATE_GAMEWON;
  }
  if(noAnims){
    gameState=STATE_GENERATOR;
    gameSetup();
  }
}

public void gameOver() {
  renderGame();
  image(ruta1, 0, -8);
  scale(2);
  int winner =0;
  for(int i=0;i<players.size();i++){
      Player p = players.get(i);
      if(p.isAlive) winner=i+1;
   }
   
  if(winner==1) image(portrait1, PApplet.parseInt(scaledScrWidth*0.19f), PApplet.parseInt(scaledScrHeight*0.06f) );
  if(winner==2) image(portrait2, PApplet.parseInt(scaledScrWidth*0.19f), PApplet.parseInt(scaledScrHeight*0.06f) );
  if(winner==3) image(portrait3, PApplet.parseInt(scaledScrWidth*0.19f), PApplet.parseInt(scaledScrHeight*0.06f) );
  if(winner==4) image(portrait4, PApplet.parseInt(scaledScrWidth*0.19f), PApplet.parseInt(scaledScrHeight*0.06f) );
  
  scale(0.5f);
  scale(2);
  printShaded("GAME OVER", PApplet.parseInt(scaledScrWidth*0.50f), PApplet.parseInt(scaledScrHeight*0.50f) );
  printShaded("PLAYER " + winner + " HAS WON.", PApplet.parseInt(scaledScrWidth*0.50f), PApplet.parseInt(scaledScrHeight*0.60f) );
  
  
  if (millis()<pauseTimer) {
    if(waitForClick){
          pauseTimer=millis()+500;
    }else{
        loadMap=true;
          gameState=STATE_GENERATOR;
          waitForClick=true;
          gameSetup();
    }
  }else{
    gameState=STATE_GAMEOVER;
  }
  if(noAnims){
    gameState=STATE_GENERATOR;
    gameSetup();
  }
}

public void mouseReleased() {
  mouseClicked=true;
  if (gameState==STATE_SPLASH || gameState==STATE_PAUSE) {
    waitForClick=false;
  }
  if (gameState==STATE_MAPSTART) {
    waitForClick=false;
  }
  if (gameState==STATE_GAMEWON) {
    waitForClick=false;
  }
  if (gameState==STATE_GAME || gameState==STATE_GAMEOVER) {
    Player _p = currentPlayer();
    if (gameState==STATE_GAMEOVER) {
      gameSetup();
      gameState=STATE_GENERATOR;
      waitForClick=true;
      noTint();
      return;
    }
    if (_p.isHuman) {
      if (noMouseOver && !currentPlayer().hasUpgrade()) {
        if (endTurnButton.contains(sMouseX(), sMouseY())  ) {
          if (gameState==STATE_GAME) {
            endTurn();
            return;
          }
        }
      } else {
        
        if (currentPlayer().hasUpgrade()) {
         checkUpgradeButtons(_p);
         return;
        }
        areaClicked(currentMousePressedArea);
      }
    }
  }
}

public Button getUpgradeButton(int actionId) {
	if (actionId == ACTION_ARMY) return upgradeArmyButton;
    if (actionId == ACTION_FORT) return upgradeFortButton;
    if (actionId == ACTION_FLEET) return upgradeFleetButton;
    if (actionId == ACTION_VILLAGE) return upgradeTradeButton;
    return null;
}

public void checkUpgradeButtons(Player _p){
  for (int actionId = 0; actionId < NUM_UPGRADE_ACTIONS; ++actionId) {
      if (getUpgradeButton(actionId).mouseOver() && !_p.isActionUpgraded(actionId)) {
          _p.upgrade(actionId);
      }
  }
}


public void keyPressed() {
  if (key == ESC) {
      key=0;
      if(gameState==STATE_SPLASH || gameState==STATE_PAUSE){
          gameState=gameStateOld;
          waitForClick=false;
          return;
      }
      	waitForClick=true;
        gameStateOld = gameState;
        gameState=STATE_PAUSE;
        println("KeyPressed", key);
  }
}

public void keyReleased() {
    if (key == '+') {
    myScale = PApplet.parseInt(constrain(myScale+1,1,6));
    println(myScale);
    saveSettings();
    screenSet();
  }
  if (key == '-') {
    myScale = PApplet.parseInt(constrain(myScale-1,1,6));
    println(myScale);
    saveSettings();
    screenSet();
  }
  if (key == 's' || key == 'S') {
   playSound = !playSound;
   saveSettings();
   }
   if (key == 'm' || key == 'M') {
    playMusic=!playMusic;
    saveSettings();
    if (playMusic) {
      song.play(musicAt);
    }
  }
  
  if (gameState==STATE_SPLASH || gameState==STATE_PAUSE) {
    return;
  }
  
  if(gameState==STATE_GENERATOR) return;
  cheatCode+=key;
  if (cheatCode.length() > 3) {
    cheatCode=cheatCode.substring(cheatCode.length()-3);
  }
  if (cheatCode.equals("wow")) {
    debug = !debug;
    println("DEBUG IS:", debug);
  }
  if (cheatCode.equals("iddqd")) {
    cheat = !cheat;
    println("CHEAT IS:", cheat);
  }
  if(debug){
      if (key == 'l' || key == 'L') {
        randomSeed(millis());
        int levelSeed = color(PApplet.parseInt(random(255)),PApplet.parseInt(random(255)),PApplet.parseInt(random(255)));
        saveCurrent(levelSeed);
        println("CURRENT LEVELSEED:",printRGB(levelSeed));
        loadMap=true;
        gameState=STATE_GENERATOR;
        gameSetup();
      }
  }
  
  if (key == 'n' && debug) {
    level++;
    if (level>75) level=76;
    saveSettings();
    startCampaignMap();
  }
  if (key == 'i' && debug){
    printPlayerPrefs();
  }
  if (key == 'u' && debug){
    currentPlayer().research+=1;
  }
  if (key == 'y' && debug){
    currentPlayer().gold+=10;
  }
  
  
  
  if (key == '+') {
    myScale = PApplet.parseInt(constrain(myScale+1,1,6));
    println(myScale);
    saveSettings();
    screenSet();
  }
  if (key == '-') {
    myScale = PApplet.parseInt(constrain(myScale-1,1,6));
    println(myScale);
    saveSettings();
    screenSet();
  }
  if (key == 'r' || key == 'R') {
    gameState=STATE_GENERATOR;
    gameSetup();
  }
  if (key == 'q' || key == 'Q' && debug) {
    screenShake=2;
  }
  if (key == 'h' || key == 'H' && debug) {
    hostile=!hostile;
    println("HOSTILE:",hostile);
  }
  if (key == ' ' ) {
    Player _pp = currentPlayer();
    if (_pp.isHuman) {
      endTurn();
    }
  }
  if (key == 't' && debug) {
      startMapTest();
  }
  if (key == 'o' && debug) {
      String path = "/data/exports/";
      String name = "worldmap" + PApplet.parseInt(random(1)*10000) + ".png";
      println("EXPORTING MAP AS", name);
    worldMap.save(path + name);
  }
  
  if (key == 'g' || key == 'G') {
    if(campaignMode) {
      campaignMode=false;
    }else{
      loadMap = false;
    }
    gameState=STATE_GENERATOR;
    gameSetup();
  }
  if (key == 'z' || key == 'Z') {
    selectAction(4);
  }
  if (key == 'x' || key == 'X') {
    selectAction(3);
  }
  if (key == 'c' || key == 'C') {
    selectAction(2);
  }
  if (key == 'v' || key == 'V') {
    selectAction(1);
  }
  if (key == 'b' || key == 'B') {
    selectAction(0);
  }
  
  if (key == 'q' && debug) {
    if (runGenerator) {
      runGenerator=false;
    } else {
      runGenerator=true;
    }
  }

  if (key == 'k' || key == 'K') {
    Player _p = currentPlayer();
    _p.isHuman=false;
    aiSpeed=200;
  }
  if (key >= '1' && key <= '4') {
    humanPlayers = (key-'0');
    gameState=STATE_GENERATOR;
    gameSetup();
  }
  if (keyCode == UP) {
  }
  if (keyCode == DOWN) {
  }
  if (keyCode == LEFT) {
  }
  if (keyCode == RIGHT) {
  }
}
public void draw(){
  try {
  tick();
  scale(myScale);
  switch (gameState){
    
    case STATE_SPLASH :
      splash();
    break;
    case STATE_MENU :
    break;
    case STATE_GAME :
      selectableSine+=0.25f;
      runPlayer();
      renderGame();
      winCheck();
    break;
    case STATE_GENERATOR :
      generatorState();
    break;
    case STATE_GAMEOVER :
      if(gameState==STATE_GAMEWON) return;
      gameOver();
      
    break;
    case STATE_MAPSTART :
      mapStart();
    break;
    case STATE_GAMEWON :
      gameWon();
    break;
    case STATE_STARTSCREEN :
      startScreen();
    break;
    case STATE_PAUSE :
      pause();
    break;
    case STATE_MAPTEST :
      updateMapTest();
    break;
    default:
    break;
  }
  } catch (Exception e) {
    e.printStackTrace();
  }
  
  mouseClicked=false;
}

class WinStats {
  int[] players = {0, 0, 0, 0, 0};
  int totalCount = 0;

  public void won(int playerIndex) {
    players[playerIndex]++;
    totalCount++;
  }
  
  public int get(int playerIndex) {
    return players[playerIndex];
  }
  
  public float winFraction(int playerIndex) {
    return players[playerIndex] / PApplet.parseFloat(totalCount);
  }
}

public void startMapTest() {
  if(!campaignTest) println("STARTING MAPTEST");
  gameState = STATE_MAPTEST;
  testIterations = 0;
  winStats = new WinStats();
  setPlayersToAI();
  noAnims=true;
}
boolean campaignTest = true;

public void updateMapTest(){
  if (getWinner() != 0) {
    startCurrentMap();
    setPlayersToAI();
  }
  
  int iter = 0;
  while(getWinner()==0 /*&& ++iter <= 1000*/){
    runPlayer();
  }
  renderGame();
  if (getWinner() != 0) {
    testIterations++;
    winStats.won(getWinner());
     
    if(testIterations>=testSize){
      printWins(); 
      if(campaignTest) {
        level++;
        if(level>=100) exit();
        startMapTest();
        return;
      }
      gameState=STATE_GENERATOR;
      noAnims=false;
      loadMap=true;
      gameSetup();
    }
  }
}


public void printWins(){
  if(!campaignTest) println("\n");
  if(!campaignTest) println("LEVEL:",level);
  for(Player player : players){
    if(!campaignTest) println("PLAYER", player.id, "WINS:", winStats.get(player.id));
  }
  println( "LEVEL:",level, "WINS: " + printBar( PApplet.parseInt( ( 100 * winStats.winFraction(1) ) ) ));
  if(!campaignTest) println();
}

public void setPlayersToAI(){
  for(Player player : players){
    player.isHuman=false;
  }
}

public String printBar(int l){
  String s="";
 for(int i =0;i<100;i++) {
   if(i<=l){
     s+="!";
   }else{
     s+=".";
   }
 }
 return s;
}
public int getWinner(){
  checkIfPlayerIsDead();
  if (noPlayers > 1) {
    return 0;
  }
  
  for (Player player : players) {
    if (player.isAlive) {
      return player.id;
    }
  }
  return 0;
}

int oldWidth = -1, oldHeight = -1;

public void setScreen(){
  background(0);

  float scaleChangeX = PApplet.parseFloat(width)/PApplet.parseFloat(scrWidth);
  float scaleChangeY = PApplet.parseFloat(height)/PApplet.parseFloat(scrHeight);
  float minScale = min(scaleChangeX,scaleChangeY);
  
  myScale = (float) scrWidth / scaledScrWidth * minScale;
  scale(myScale);

  if (oldWidth == width && oldHeight == height)
    return;

  oldWidth = width;
  oldHeight = height;

  int newWidth = PApplet.parseInt(scrWidth*minScale);
  int newHeight = PApplet.parseInt(scrHeight*minScale);
  
  surface.setSize(newWidth, newHeight);
}
public void generatorState(){
  background(100);
  image(worldMap,32,0);
  if(loadMap){
    startCurrentMap();
  }else{
    createMap();
  }
}

public void createMap() {
    int levelSeed;
  while (true) { 
    randomSeed(millis());
    levelSeed = color(PApplet.parseInt(random(255)),PApplet.parseInt(random(255)),PApplet.parseInt(random(255)));
    startMap();
    generateMap();
    makeRivers(PApplet.parseInt(random(200,1500)));
    fixBorders();
    removeSmallAreas();
    createWorld();
    if (isWorldValid()) {
      break;
    }
  }
  
  saveCurrent(levelSeed);
  startCurrentMap();
}
public void setEventWeights(){
  chanceOfEvent=random(0.3f,0.99f);
  eventDistribution=random(0.1f,0.9f);
  //println(chanceOfEvent,eventDistribution);
}
public void makeRivers(int riverCount){
  for(int t=0;t<riverCount;t++){
    int riverLength=PApplet.parseInt(random(5,35));
    boolean atCoast=false;
    IntPoint r=new IntPoint( PApplet.parseInt(random(1,scaledScrWidth-1)), PApplet.parseInt(random(1,scaledScrHeight-1)) );
    
    while(worldMap.get(r.x,r.y) != waterPixel){
      r=new IntPoint( PApplet.parseInt(random(1,scaledScrWidth-1)), PApplet.parseInt(random(1,scaledScrHeight-1)) );
    }
    
    while(!atCoast){
      int dir=PApplet.parseInt(random(8));
      r = r.move8Directions(dir);
      if(r.x < 0 || r.x > worldMap.width || r.y < 0 || r.y > worldMap.height) {
        r = r.moveDirection(dir, -1); 
      }
      if(countSurrounding(worldMap, r.x, r.y, waterPixel) > 0) atCoast=true;
    }
    for(int i=0;i<riverLength;i++){
      int dir=PApplet.parseInt(random(8));
      r = r.move8Directions(dir);
      worldMap.set(r.x,r.y,waterPixel);
    }
  }
}

public boolean isWorldValid(WorldValidator ... validators){
  if (areas.size() < 12) {
    return false;
  }
  
  for (WorldValidator validator : validators) {
    if (!validator.validate()) {
      return false;
    }
  }
  return true;
}

static class IntPoint {
  public final int x;
  public final int y;
  
  public IntPoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public IntPoint moveDirection(int direction) {
    return moveDirection(direction, 1);
  }
  public IntPoint move8Directions(int direction) {
    return move8Directions(direction, 1);
  }
  public IntPoint moveDirection(int direction, int steps) {
    switch(direction){
      case 0:
        return new IntPoint(x, y - steps);
      case 1:
        return new IntPoint(x + steps, y);
      case 2:
        return new IntPoint(x, y + steps);
      case 3:
        return new IntPoint(x - steps, y);
      default:
        throw new IllegalArgumentException("Direction must be in the range [0, 3]");
    }
  }
  public IntPoint move8Directions(int direction, int steps) {
    switch(direction){
      case 0:
        return new IntPoint(x, y - steps);
      case 1:
        return new IntPoint(x + steps, y - steps);
      case 2:
        return new IntPoint(x + steps, y);
      case 3:
        return new IntPoint(x + steps, y + steps);
      case 4:
        return new IntPoint(x, y + steps);
      case 5:
        return new IntPoint(x - steps, y + steps);
      case 6:
        return new IntPoint(x - steps, y);
      case 7:
        return new IntPoint(x - steps, y - steps);
      default:
        throw new IllegalArgumentException("Direction must in the range [0, 3]");
    }
  }
}

interface WorldValidator {
  public boolean validate();
}

class MinAreas implements WorldValidator {
  private int min;
  
  public MinAreas(int min) {
    this.min = min;
  }
  
  public boolean validate() {
     return areas.size() >= min; 
  }
}

class MaxAreas implements WorldValidator {
  private int max;
  
  public MaxAreas(int max) {
    this.max = max;
  }
  
  public boolean validate() {
     return areas.size() <= max; 
  }
}

class MaxNeighbours implements WorldValidator {
  private int max;
  
  public MaxNeighbours(int max) {
    this.max = max;
  }
  
  public boolean validate() {
    for (Area area : areas) {
      if (area.neighbors.size() > max) {
        return false;
      }
    }
    return true;
  }
}

class MinNeighbours implements WorldValidator {
  private int min;
  
  public MinNeighbours(int min) {
    this.min = min;
  }
  
  public boolean validate() {
    for (Area area : areas) {
      if (area.neighbors.size() < min) {
        return false;
      }
    }
    return true;
  }
}

class MinAverageNeighbours implements WorldValidator {
  private float min;
  
  public MinAverageNeighbours(float min) {
    this.min = min;
  }
  
  public boolean validate() {
    int totalNeighbours = 0;
    for (Area area : areas) {
      totalNeighbours += area.neighbors.size();
    }
    return ((float)totalNeighbours / areas.size()) >= min;
  }
}

class MaxAverageNeighbours implements WorldValidator {
  private float max;
  
  public MaxAverageNeighbours(float max) {
    this.max = max;
  }
  
  public boolean validate() {
    int totalNeighbours = 0;
    for (Area area : areas) {
      totalNeighbours += area.neighbors.size();
    }
    return ((float)totalNeighbours / areas.size()) <= max;
  }
}

public void removeSmallAreas(){
  HashMap<Integer, ArrayList<Integer>> colorPositions = new HashMap();
  int waterColor = color(0);
    
  for(int i = 0;i<worldMap.pixels.length;i++){
    int c = worldMap.pixels[i];
    if(c == waterColor) continue;
    
    ArrayList<Integer> positions = colorPositions.get(c);
    if (positions == null) {
      positions = new ArrayList<Integer>();
      colorPositions.put(c, positions);
    }
    positions.add(i);
  }
  
  final int MinimumLandSize = 150;
  for (ArrayList<Integer> positions : colorPositions.values()) {
    if (positions.size() < MinimumLandSize) {
      for (Integer index : positions) {
        worldMap.pixels[index] = waterColor;
      }
    }
  }
}

public void fixBorders(){
  fillRect(worldMap, 0, 0, 32, worldMap.height, color(0));
  fillRect(worldMap, 0, worldMap.height-16, worldMap.width, 16, color(0));
}
public void makeMapBase(int mWidth, int mHeight, int gridW, int gridH){
  int[][] colArray = new int[gridW][gridH];
  colArray=fillColArray(colArray);
  worldMap=createImage(mWidth,mHeight,RGB);
  int spacerX = PApplet.parseInt(mWidth/gridW);
  int spacerY = PApplet.parseInt(mHeight/gridH);
  
  for(int x=0;x<colArray.length;x++){
    for(int y=0;y<colArray[x].length;y++){
      int atX = x*spacerX;
      int atY = y*spacerY+PApplet.parseInt(random(10));
      int boxW = max(3,PApplet.parseInt(random(spacerX*0.7f)));
      int boxH = max(3,PApplet.parseInt(random(spacerY*0.7f)));
      if(y<colArray[x].length-1 && x<colArray.length-1){
        if(colArray[x+1][y]==colArray[x][y]) boxW+=PApplet.parseInt(spacerX*1.5f);
        if(colArray[x][y+1]==colArray[x][y]) boxH+=PApplet.parseInt(spacerY*1.5f);
      }
      int shift=0;
      if(isOdd(y)) shift=PApplet.parseInt(spacerX*0.5f);
      fillRect(worldMap,atX+shift,atY,boxW,boxH,colArray[x][y]);
    }
  }
}

public void fillRect(PImage img, int x, int y, int w, int h, int c){
  for(int _x=x; _x<x+w; _x++){
    for(int _y=y; _y<y+h; _y++){
      img.set(_x,_y,c);
    }
  }
}

public int [][] fillColArray(int[][] colArray){
  float chanceOfWater=random(0.3f,0.8f);
  float chanceOfMerge=random(0.1f,0.8f);
  for(int x=0;x<colArray.length;x++){
    for(int y=0;y<colArray[x].length;y++){
      boolean duplicate=true;
      while(duplicate){
        duplicate=false;
        int c=color(PApplet.parseInt(random(1)*50)+145,PApplet.parseInt(random(1)*55)+155,PApplet.parseInt(random(1)*40)+140);
        if(!isUniqueColor(c,colArray)) { 
          duplicate=true;
        }else{
          colArray[x][y]=c;
        }
      }
      if(random(1)<chanceOfWater) colArray[x][y]=color(0);
      if(random(1)<chanceOfMerge){
        int sx = PApplet.parseInt(random(3)) - 1; 
        int sy = (sx!=0) ? 0 : PApplet.parseInt(random(3)) - 1;
        if(x+sx>=0 && x+sx < colArray.length && y+sy >= 0 && y+sy < colArray[x].length) colArray[x+sx][y+sy]=colArray[x][y];
      }
    }
  }
  return colArray;
}
public boolean isUniqueColor(int c, int[][] colArray){
  for(int x=0;x<colArray.length;x++){
    for(int y=0;y<colArray[x].length;y++){
      if(c==colArray[x][y]) return false;
    }
  }
  return true;
}

public void makeStroke(PImage map, int x, int y, float rotation, int l, float growth, int c) {
  PVector pos = new PVector(x, y);
  PVector dir;
  dir = PVector.fromAngle(radians(rotation));
  int r = PApplet.parseInt(random(5));
  int turn = 15;
  for (int i = 0; i<l*0.5f; i++) {
    if (random(1)<growth) r++;
    if (random(1)<0.05f) r*=0.7f;
    drawDot(PApplet.parseInt(pos.x), PApplet.parseInt(pos.y), map, r, c);
    //}
    pos.add(dir);
    dir.rotate(radians(random(-turn, turn)));
  }
  for (int i = 0; i<l*0.5f; i++) {
    if (random(1)<growth) r--;
    if (random(1)<0.05f) r*=1.1f;
    if (r<1 && i < l*0.45f) r++;
    drawDot(PApplet.parseInt(pos.x), PApplet.parseInt(pos.y), map, r, c);
    pos.add(dir);
    dir.rotate(radians(random(-turn, turn)));
  }
}

public void drawDot(int x, int y, PImage img, float r, int c){
  for(int x1 = PApplet.parseInt(x-r)-1; x1<=x+r+1;x1++){
    for(int y1 = PApplet.parseInt(y-r)-1; y1<=y+r+1;y1++){
      if(dist(x,y,x1,y1)<r){
        img.set(x1,y1,c);
      }
    }
  }
}

public void drawDotOnColor(int x, int y, PImage img, float r, int c, int onThis){
  for(int x1 = PApplet.parseInt(x-r-1); x1<=x+r+1;x1++){
    for(int y1 = PApplet.parseInt(y-r-1); y1<=y+r+1;y1++){
      if(dist(x,y,x1,y1)<r && img.get(x1,y1) == onThis){
        img.set(x1,y1,c);
      }
    }
  }
}



public void generateMap(){
  int _N,_xx,_yy;
  
  int _doCol=color(0);
  int _watCol=worldMap.get(0,0);
  for(int i = 0;i<500000;i++){
    _xx=PApplet.parseInt(random(1,scaledScrWidth-1));
    _yy=PApplet.parseInt(random(1,scaledScrHeight-1));
    _doCol = worldMap.get(_xx,_yy);
    if(_doCol==_watCol){
      _N=countSurrounding(worldMap,_xx,_yy,_watCol);
      if(_N<random(1,7)){
        int nc = getNeighborColor(worldMap,_xx,_yy);
        drawDotOnColor(_xx, _yy, worldMap, random(1,3.5f), nc, _watCol);
      }
    }
  }
  for(int i = 0;i<300000;i++){
    _xx=PApplet.parseInt(random(1,scaledScrWidth-1));
    _yy=PApplet.parseInt(random(1,scaledScrHeight-1));
    _doCol = worldMap.get(_xx,_yy);
    if(_doCol==_watCol){
      _N=countSurrounding(worldMap,_xx,_yy,_watCol);
      if(_N<random(1,7)){
        int nc = getNeighborColor(worldMap,_xx,_yy);
        drawDotOnColor(_xx, _yy, worldMap, 1, nc, _watCol);
      }
    }
  }
}

public void drawWithMouse(){
  worldMap.set(1+PApplet.parseInt(mouseX*(1/myScale)),1+PApplet.parseInt(mouseY*(1/myScale)),color(0));
  worldMap.set(PApplet.parseInt(mouseX*(1/myScale)),PApplet.parseInt(mouseY*(1/myScale)),color(0));
  worldMap.set(-1+PApplet.parseInt(mouseX*(1/myScale)),-1+PApplet.parseInt(mouseY*(1/myScale)),color(0));
  worldMap.set(1+PApplet.parseInt(mouseX*(1/myScale)),-1+PApplet.parseInt(mouseY*(1/myScale)),color(0));
}


public void startCurrentMap(){
  anims = new ArrayList();
  popMessages = new ArrayList();

  loadCurrent();
  runGenerator=false;
  createWorld();
  
  makeCoastLine();
  initPlayers();
  setEventWeights();
  pauseTimer=millis()+3000;
  if(autoRun){pauseTimer=millis()+1000;}
  if(gameState!=STATE_MAPTEST) gameState=STATE_MAPSTART;
  
 if(autoRun){
     for(int i=0; i<players.size();i++){
        Player _p = players.get(i);
        _p.isHuman=false;
     }
    aiSpeed=150;
  }
}
int testseed=1;

public void makeCoastLine(){
    water=loadImage("water.png");
  int alphaColor = color(0);
  for(int _y=1;_y<worldMap.height-2;_y++){
    for(int _x=1;_x<worldMap.width-2;_x++){
      int landProximity=9-(countSurrounding(worldMap, _x, _y, alphaColor));
      landProximity=constrain(landProximity,0,2);
      int _cTweak = color(red(water.get(_x,_y))+landProximity*14,green(water.get(_x,_y))+landProximity*16,blue(water.get(_x,_y))+landProximity*16);
      water.set(_x,_y,_cTweak);
    }
  }
}
public void createWorld(){
  areas = new ArrayList<Area>();
  int alphaColor = worldMap.get(0,0);
  int currColor = color(0);
  for(int _y=0;_y<worldMap.height-1;_y++){
    for(int _x=2;_x<worldMap.width-1;_x++){
      currColor=worldMap.get(_x,_y);
      if (currColor!=alphaColor){
        //found land. Make area!
        boolean newArea = true;
        for(int i = areas.size()-1;i>=0;i--){
          Area _area = areas.get(i);
          if(currColor == _area.areaColor){
            newArea=false;
          }
        }
        if(newArea==true) {
          makeArea(currColor,_x,_y);
        }
      }
    }
  }
  for(int i=0;i<areas.size();i++){
    Area a = areas.get(i);
    a.findNeighbors();
  }
  for(int i=0;i<areas.size();i++){
    Area a = areas.get(i);
    a.findChokePoints();
  }
}

public void makeArea(int _col,int _xx,int _yy){
  int _c = _col;
  int _moc = color(245);
  int _x1=_xx;
  int _y1=_yy;
  int _x2=_xx+1;
  int _y2=_yy+1;
  
  for(int _y=0;_y<worldMap.height-1;_y++){
    for(int _x=0;_x<worldMap.width-1;_x++){
      if (worldMap.get(_x,_y)==_c) { //adjust bounds
        _x1 = (_x < _x1) ? _x : _x1 ; 
        _x2 = (_x > _x2) ? _x : _x2 ;
        _y1 = (_y < _y1) ? _y : _y1 ; 
        _y2 = (_y > _y2) ? _y : _y2 ;
      }
    }
  }
  PImage _im = createImage(_x2-_x1+2,_y2-_y1+2,RGB); 
  PImage _moim = createImage(_x2-_x1+2,_y2-_y1+2,RGB);
  for(int _y=0;_y<_im.height-1;_y++){
    for(int _x=0;_x<_im.width-1;_x++){
      if(worldMap.get(_x1+_x,_y1+_y)==_c) {
        _im.set(_x+1,_y+1,_c);
        _moim.set(_x+1,_y+1,_moc);
      }else{
        _im.set(_x+1,_y+1,color(0));
        _moim.set(_x+1,_y+1,color(0));
      }
    }
  }
  
  areas.add(new Area(_x1-1,_y1-1,_x2+1,_y2+1, renderArea(_im), _moim, _c, areas.size()));
}

public int countSurrounding(PImage _img, int xx, int yy, int compareWith){
  int count=0;
  for(int tx = -1;tx<2;tx++){
    for(int ty = -1;ty<2;ty++){
      if(inImg(_img,xx+tx,yy+ty)){
        if(_img.get(xx+tx,yy+ty) == compareWith){
          count++;
        }
      }
    }
  }
  return count;
}
public boolean inImg(PImage _img,int _x,int _y){
  boolean in=false;
  if(_x >= 0 && _y >= 0 && _x <= _img.width  && _y <= _img.height ){
    in=true;
  }
  return in;
}

public int getSumOfSurrounding(PImage _baseImg,int _x,int _y){
  return (_baseImg.get(_x,_y));
}

//---------------------------------------
public void initPlayers(){
  gameTurn=1;
  playerTurn=1;
  playerCount = 0;
  players.clear();
  noPlayers=(level>3 || !campaignMode) ? 4 : level+1 ;
  while(playerCount < noPlayers){
    Area _a = areas.get(PApplet.parseInt(random(areas.size())));
    if(_a.ownedBy==0){
      _a.ownedBy=playerCount+1;
      _a.fort=true;
      _a.city=false;
      _a.uni=false;
      _a.independence=0.000001f;
      playerCount++;
      players.add(new Player("Player " + _a.ownedBy, PApplet.parseInt(1+playerCount*1.25f), _a.id,playerCount));
    }
  }
  specialCases();
  randomSeed(millis());
}

public void specialCases(){
    if(!campaignMode) return;
    if(level<=2){
        for(Area area : areas){
          area.fort=false;
        }
    }
    if(level==3){
        for(int i=0;i<players.size();i++){
            Player p = players.get(i);
            
            p.aiPreference[ACTION_ARMY]=1;
            p.aiPreference[ACTION_FORT]=0.0005f;
            p.aiPreference[ACTION_FLEET]=0.8f;
        }
    }
    if(level==5){
        Player p = players.get(PApplet.parseInt(random(1,4)));
        for(int i=0;i<p.aiPreference.length;i++){ 
        	p.aiPreference[i]=0.1f + random(0.1f);
    	}
    	p.aiPreference[4] = 0.9f;
    }
}
public void startMap(){
  if(!campaignTest) println("INITIALIZING NEW MAP WITH", genAreas,"AREAS");
  int gw = PApplet.parseInt(random(4,18));
  int gh = PApplet.parseInt(gw*0.8f);
  makeMapBase(scaledScrWidth-38,scaledScrHeight-20,gw,gh);
  PImage m = createImage(scaledScrWidth,scaledScrHeight,RGB);
  m.copy(worldMap,0,0,worldMap.width,worldMap.height, m.width-worldMap.width,0,worldMap.width,worldMap.height);
  worldMap = m;
}

public void areaBoxing(int x, int y, int c){
  int b=PApplet.parseInt(random(5));
  for(int i=0;i<b;i++){
    int _xr=PApplet.parseInt(random(-10,10));
    int _yr=PApplet.parseInt(random(-10,10));
    
    for(int _y=-5;_y<5;_y++){
      for(int _x=-4;_x<4;_x++){
        worldMap.set(x+_xr+_x,y+_yr+_y,c);
      }
    }
  } 
}


public void initMap(){ 
  for(int _my=0;_my<gridHeight;_my++){
    for(int _mx=0;_mx<gridWidth;_mx++){
      miniMap.set(_mx,_my,color(PApplet.parseInt(random(1)*50)+145,PApplet.parseInt(random(1)*55)+155,PApplet.parseInt(random(1)*40)+140));
    }
  }
  int _aCount=0;
  while (_aCount!=genAreas){
    _aCount=0;
    for(int _my=0;_my<gridHeight;_my++){
      for(int _mx=0;_mx<gridWidth;_mx++){
        _aCount = (miniMap.get(_mx,_my) == color(0)) ? _aCount : _aCount+1;
      }
    }
    int XX=PApplet.parseInt(random(gridWidth+1));
    int YY=PApplet.parseInt(random(gridHeight+1));
    if(_aCount<genAreas){
      miniMap.set(XX,YY,color(PApplet.parseInt(random(1)*150)+100,PApplet.parseInt(random(1)*150)+100,PApplet.parseInt(random(1)*150)+100));
    } else{
      miniMap.set(XX,YY,color(0)); 
    }
  }
}
public int getNeighborColor(PImage pic,int x,int y){
  int dx = PApplet.parseInt(random(-2,2));
  int dy = PApplet.parseInt(random(-2,2));
  return pic.get(x+dx,y+dy);
}




public void printNeighborlist(String _currentMousePressedArea){
  for(int i=0;i<areas.size();i++){
    Area a = areas.get(i);
    if(a.name==_currentMousePressedArea){
      println(a.name+"'s list of neighbors:");
      for(int _n=0;_n<a.neighbors.size();_n++){
        for(int _b=0;_b<areas.size();_b++){
          Area b = areas.get(_b);
          if(b.id == a.neighbors.get(_n)){
            println(b.name);
          }
        }
      }
    }
  }
}
public void mapStart() {
  String _map="MAP "+level+"!";
  
  if(!campaignMode){
    genModeMapStart();
    return;
  }
  image(bg,0,0);
  image(ruta1, 0, -8);
  image(portrait1, PApplet.parseInt(scaledScrWidth*0.85f), PApplet.parseInt(scaledScrHeight*0.12f) );
  scale(2);
  float opponentHeight = 0.77f;
  if (millis()<pauseTimer) {
    switch (level){
      case 1:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(action_army, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("ARMY, COST "+COST_ARMY+" GOLD:", PApplet.parseInt(scaledScrWidth*0.45f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("LETS YOU ATTACK AN ADJACENT AREA.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded("EACH AREA GIVES YOU 1 GOLD, BUT\nNOT THE TURN AFTER BEING CONQUERED.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.48f) );
        
        printShaded("DESTROY ALL ENEMIES TO WIN.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.64f) );
        printShaded("DESPITE BEING ENEMIES, LET'S LIVE IN PEACE.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 2:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.33f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.53f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(action_fort, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("FORT, COST "+COST_FORT+" GOLD:", PApplet.parseInt(scaledScrWidth*0.45f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("INCREASES DEFENSE OF AN AREA.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded("YOU CAN'T HIDE BEHIND WALLS, BLUE!", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 3:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(action_fleet, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        
        printShaded("FLEET, COST "+COST_FLEET+" GOLD:", PApplet.parseInt(scaledScrWidth*0.45f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("LETS YOU ATTACK AN ANY AREA.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded("WAR IS THE INEVITABLE CONCLUSION OF FAILED DIPLOMACY.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 4:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(action_village, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("CITY, COST "+COST_VILLAGE+" GOLD:", PApplet.parseInt(scaledScrWidth*0.45f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("INCREASES THE INCOME FROM AN AREA BY 1.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded("KINGS DON'T HAVE FRIENDS.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 5:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(action_uni, PApplet.parseInt(scaledScrWidth*0.68f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("UNIVERSITY, COST "+COST_UNIVERSITY+" GOLD:", PApplet.parseInt(scaledScrWidth*0.44f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("BUILDS RESEARCH POINTS.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded(RESEARCH_LIMIT + " RESEARCH NEEDED TO UPGRADE ONE ACTION.\nAN UPGRADED ACTION DOES THE\nSAME THING, BUT BETTER.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.46f) );
        printShaded("WE SEEK A PEACEFUL SOLUTION TO THIS CONFLICT.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 6:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(plague, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("PLAGUE, EVENT:", PApplet.parseInt(scaledScrWidth*0.44f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("PLAGUE DESTROYS CITIES.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded(currentTaunt, PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 7:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(rebellion, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("REBELLION, EVENT:", PApplet.parseInt(scaledScrWidth*0.44f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("TURNS AN AREA NEUTRAL.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        //printShaded(taunt[tauntIndex], int(scaledScrWidth*0.5), int(scaledScrHeight*(opponentHeight+0.20)) );
        printShaded(currentTaunt, PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 8:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(upgrade_mine, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("MINE:", PApplet.parseInt(scaledScrWidth*0.44f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("SOMETIMES PAYS A LOT!", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded(currentTaunt, PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      case 76:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(rebellion, PApplet.parseInt(scaledScrWidth*0.61f), PApplet.parseInt(scaledScrHeight*0.24f) );
        scale(2);
        printShaded("LAST AND FINAL MAP!", PApplet.parseInt(scaledScrWidth*0.44f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded("GENERATE NEW ONES BY\nPRESSING THE 'G' KEY.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded(currentTaunt, PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
      default:
        scale(0.5f);
        image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*opponentHeight) );
        //image(action_castle, int(scaledScrWidth*0.61), int(scaledScrHeight*0.24) );
        scale(2);
        printShaded("HELPFUL TIP:", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.30f) );
        printShaded(currentTip, PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
        printShaded(currentTaunt, PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(opponentHeight+0.20f)) );
      break;
    }
    if(waitForClick){ pauseTimer=millis()+500; }
    fill(200, 200, 150);
    scale(2);
    printShaded( _map, PApplet.parseInt(scaledScrWidth*0.25f), PApplet.parseInt(scaledScrHeight*0.075f) );
    scale(0.5f);
  }else{
    gameState=STATE_GAME;
  }
  scale(0.5f);
  if(gameState==STATE_MAPTEST) {
    for(Player player : players){
      player.isHuman=false;
    }
  }
}

public void genModeMapStart(){
    if(!loadMap){
        gameState=STATE_GAME;
        return;
    }
  if (millis()<pauseTimer) {
    if(waitForClick){ pauseTimer=millis()+500; }
    image(bg,0,0);
    image(ruta1, 0, -8);
    image(portrait1, PApplet.parseInt(scaledScrWidth*0.85f), PApplet.parseInt(scaledScrHeight*0.12f) );
    scale(2);
    printShaded("PRESS 'G' TO GENERATE NEW MAP.", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.30f) );
    printShaded("RESTART GAME TO RETURN TO CAMPAIGN MODE", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.38f) );
    printShaded(currentTaunt, PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*(0.77f+0.20f)) );
    scale(0.5f);
    image(portrait2, PApplet.parseInt(scaledScrWidth*0.28f), PApplet.parseInt(scaledScrHeight*0.77f) );
    image(portrait3, PApplet.parseInt(scaledScrWidth*0.43f), PApplet.parseInt(scaledScrHeight*0.77f) );
    image(portrait4, PApplet.parseInt(scaledScrWidth*0.58f), PApplet.parseInt(scaledScrHeight*0.77f) );
    scale(2);
    scale(2);
    printShaded( "GENERATOR MODE", PApplet.parseInt(scaledScrWidth*0.25f), PApplet.parseInt(scaledScrHeight*0.075f) );
    scale(0.5f);
  }else{
    gameState=STATE_GAME;
  }
}
class Animation {
  PImage[] images;
  float currentFrame;
  float numFrames;
  float pace;
  int xpos;
  int ypos;
  boolean finished = false;

  Animation(PImage[] images, int mx, int my, float p) { 
    xpos = mx;
    ypos = my;
    pace = p;
    numFrames = images.length;
    this.images = images;

    currentFrame=0;
  }
  
  public Animation setDelay(float delay) {
    currentFrame = -delay;
    return this;
  }
  
	public void tick(){
    	currentFrame = (currentFrame+(pace*2));
        if (PApplet.parseInt(currentFrame) >= numFrames){
          finished=true;
        }
    }
    
  public void display(float xpos, float ypos) {
      if (finished || currentFrame < 0) {
        return;
      }
      image(images[PApplet.parseInt(currentFrame)], xpos, ypos);
    
  }
}

public PImage[] cutAnimRow(PImage spriteMap, int sprWidth, int row){
  return cutAnim(spriteMap, sprWidth)[row];
}

public PImage[][] cutAnim(PImage spriteMap, int sprWidth){
  return cutAnim(spriteMap, sprWidth, sprWidth);
}
public PImage[][] cutAnim(PImage spriteMap, int sprWidth, int sprHeight){
  int animation = spriteMap.height/sprHeight;
  int frame = spriteMap.width/sprWidth;
  PImage[][] images = new PImage[PApplet.parseInt(animation)][PApplet.parseInt(frame)];
  for (int a = 0; a < animation; a++) {
    for (int i = 0; i < frame; i++) {
      images[a][i] = createImage(sprWidth, sprHeight, ARGB);
      images[a][i].copy(spriteMap,i*sprWidth,a*sprHeight,sprWidth,sprHeight,0,0,sprWidth,sprHeight); 
    }
  }
  return images;
}


class PopMessage {
  String message;
  int time;
  float pace,currentTime;
  int xpos;
  int ypos;
  boolean finished = false;
  
  PopMessage(String _mes, int mx, int my, float _pace, int _time) { 
    message = _mes;

    xpos = mx;
    ypos = my;
    pace = _pace;
    time = _time;
    currentTime=0;
  }
  
  public PopMessage setDelay(float delay) {
    currentTime = -delay;
    return this;
  }

  public void display(float xpos, float ypos) {
    currentTime = (currentTime+pace);
    if (PApplet.parseInt(currentTime) >= time){
      finished=true;
    } else {
      if (currentTime < 0) {
        return;
      }
      printShadedFade(message,PApplet.parseInt(xpos),PApplet.parseInt(ypos-currentTime*0.5f), PApplet.parseInt(time - currentTime));
    }
  }
}
public void pause(){
  if(waitForClick){
    image(bg,0,0);
    image(ruta1, 0, 0);
    scale(2);
    printShaded("PAUSED", PApplet.parseInt(scaledScrWidth*0.5f), PApplet.parseInt(scaledScrHeight*0.189f) );
    textAlign(LEFT);
    printShaded("SHORTCUTS:", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 1) );
    printShaded("R - RESTART MAP", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 3) );
    printShaded("M - TOGGLE MUSIC", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 4) );
    printShaded("S - TOGGLE SOUND", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 5) );
    printShaded("K - AUTO FINISH MAP", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 6) );
    printShaded("G - GENERATE MAP", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 7) );
    printShaded("RMB - AUTO ATTACK", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 8) );
    printShaded("Z,X,C,V,B - ACTIONS", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 9) );
    printShaded("SPACE - END TURN", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 10) );
    printShaded("+/-  - GAME SCREEN SIZE", PApplet.parseInt(scaledScrWidth*0.2f), PApplet.parseInt(scaledScrHeight*0.22f + textHeight * 11) );
    
    textAlign(CENTER);
    scale(.5f);
  }else{
    gameState=gameStateOld;
    waitForClick=true;
  }
}
class Player {
  String name;
  int id;
  PImage portrait;
  int gold, income;
  int startArea;
  boolean isAlive,isHuman;
  ArrayList <Action> actions;
  float aiIterations;
  float[] aiPreference=new float[5];
  float[] aiDiplomacy=new float[4];
  float aggression,uniFondness;
  float dominance;
  int research,maxUni;
  int wins;
  final int upgradeCost = RESEARCH_LIMIT;
  
  Player (String _name, int _gold, int _startArea, int _id){
    name=_name;
    id=_id;
    portrait=loadImage("portrait"+id+".png");
    gold=_gold;
    isAlive=true;
    isHuman = (id<=humanPlayers);
    
    research=0;
    maxUni=PApplet.parseInt(random(3,5));
    uniFondness=random(1)+.5f;
    aiIterations=70;
    
    setAIPreference();
    startArea=_startArea;
    aggression=0;
    
    actions = new ArrayList<Action>();
    int _actionLimit = (level>5 || !campaignMode) ? 5 : level ;    
    for(int i=0;i<_actionLimit;i++){ 
      actions.add(new Action(i));
    }
    for(int i=0;i<aiDiplomacy.length;i++){ 
      aiDiplomacy[i]=1;
    }
    setIncome();
 }

 public void setAIPreference(){
    aiPreference[0] = 0.4f + random(0.1f);
    aiPreference[1] = 0.4f + random(0.1f);
    aiPreference[2] = 0.4f + random(0.1f);
    aiPreference[3] = 0.4f + random(0.1f);
    aiPreference[4] = 0.4f + random(0.1f);
 }
 
 public void calcDominance(){
   dominance = ( incomeFactor() + controlFactor() + terrainFactor() + upgradeFactor() ) / 4.0f; 
   if(!isAlive) dominance=0;
 }
 
  public float incomeFactor(){
    float totalIncome=0;
    for (int i=0;i<players.size();i++){
      Player p = players.get(i);
      totalIncome+=p.income;
    }
    return income/totalIncome;
  }
  
 public float controlFactor(){
    float controlledByPlayers = 0;
    float controlledByMe = 0;
     for (int i=0;i<areas.size();i++) {
        Area a = areas.get(i);
        if(a.ownedBy>0) controlledByPlayers++;
        if(a.ownedBy==id){
          controlledByMe++;
        }
     }
     return controlledByMe/areas.size();
  }
  public int areaControlledCount(){
     int controlled = 0;
     for (int i=0;i<areas.size();i++) {
        Area a = areas.get(i);
        if(a.ownedBy==id){
          controlled++;
        }
     }
     return controlled;
  }
 public boolean hasUpgrade() {
   Boolean _upgradeable=false;
   for(int _i=0;_i<actions.size()-1;_i++){ 
      if(!actions.get(_i).upgraded){_upgradeable=true;}
    }
   return (research >= upgradeCost && _upgradeable);
 }
 
 public float terrainFactor(){
  float forests=0, myForests=0, mines=0, myMines=0;
  for (int i=0;i<areas.size();i++) {
    Area a = areas.get(i);
    if(a.forest && a.ownedBy>0){
      forests++;
      if(a.ownedBy==id) myForests++;
    }
    if(a.mine && a.ownedBy>0){
      mines++;
      if(a.ownedBy==id) myMines++;
    }
  }
  if(forests>0 && mines >0) return (myForests/forests + myMines/mines) / 2;
  if(mines>0) return myMines/mines;
  if(forests>0) return myForests/forests;
  return 0;
 }

  public float upgradeFactor(){
    float totalUpgrades=0, myUpgrades=0;
    for (int i=0;i<players.size();i++){
      Player p = players.get(i);
      for(int u=0;u<p.actions.size();u++){
        Action a = p.actions.get(u);
        if (a.upgraded){
          totalUpgrades++;
          if(p == currentPlayer()) myUpgrades++;
        }
      }
    }
    if (totalUpgrades>2) return myUpgrades/totalUpgrades;
    return 0.25f;
  }
  
 public void upgrade(int type) {
     println("UPGRADING", getActionName(type));
   if (research < upgradeCost)
     return;
   if (actions.get(type).upgraded)
     return;
   research -= upgradeCost;
   actions.get(type).upgraded=true;
 }
 
 public Boolean isActionUpgraded(int actionId){
     if (actionId < 0 || actionId >= actions.size()) return false;
     return actions.get(actionId).upgraded;
 }
 
 public void showActions(boolean canPressAction){
   for(int i=0;i<actions.size();i++){
    Action _a = actions.get(i);
     _a.update(i,canPressAction);
   }
  if(gold*0.5f>aiIterations){aiIterations=PApplet.parseInt(gold*0.5f);}
  image(portrait,0,0);
  if(research>0){
    printShaded("RESEARCH: " + research, 32, 58);
  }
 }
 public void setIncome(){
  int _inc=baseIncome;
  for(int _i=0;_i<areas.size();_i++){
    Area _a = areas.get(_i);
    if(_a.ownedBy==id && _a.invadedBy==0){
      _inc+=_a.income;
    }
  }
  income=_inc;
 }
 
 public void showGold(){
   if(playerTurn==id){
     printShaded("GOLD: " + gold, 32, 83);
     noTint();
   }else{
     //scale(2);
     noTint();
   }
   if(!isAlive){
     tint(tintByOwner(id));
      image(dead, 0,32+16+(id-2)*(player_pane.height));
   }
 }
}

public int playersAlive(){
  int l=0;
  for(int i = 0; i<players.size();i++){
    Player p = players.get(i);
    if(p.isAlive) l++;
  }
  return l;
}


public void renderGame(){
  startRender();
  renderBG();
  renderAreas();  
  renderAnims();
  renderMessages();
  renderEffect();
  
  playerActions();
  playerPane();
  hasUpgrade();  
}

public void renderEffect(){
    tint(250,250,200,25);
  image(glow,0,0);
  noTint(); 
  image(gui_bg,0,2);
}

public void renderMessages(){
    for (int i = popMessages.size()-1; i >= 0; i--) {
    PopMessage _mes = (PopMessage) popMessages.get(i);
    _mes.display(_mes.xpos,_mes.ypos);
    if (_mes.finished) {
      popMessages.remove(i);
    }
  }
}

public void renderBG(){
    image(water,0,0);
  image(info_pane,0,scaledScrHeight-info_pane.height);
}

public void startRender(){
    if(gameState==STATE_MAPTEST) screenShake=0;
  if(screenShake>0.01f){
    translate(random(-screenShake,screenShake),random(-screenShake,screenShake));
    screenShake-=0.1f;
  }
  noTint();
  noMouseOver = true;
}

public void renderAreas(){
    for(int i=0;i<areas.size();i++){
    Area a = areas.get(i);
    a.drawArea(!currentPlayer().hasUpgrade());
    noMouseOver = (a.mouseOver) ? false:noMouseOver;
  }
  
  for(int i=0;i<areas.size();i++){
    Area a = areas.get(i);
    a.drawUpgrades();
  }
}
public void renderAnims(){
    for (int i = anims.size()-1; i >= 0; i--) { 
    Animation _anim = (Animation) anims.get(i);
    _anim.display(_anim.xpos,_anim.ypos);
  }
}
public void checkMusic(){
  if(song.isPlaying() == false && playMusic==true) {
   playSound("song");
  }
  if(playMusic==false){
    song.pause();
  }else{
    musicAt=song.position();
  }
}

public void playerActions(){
    
  for(int i=0;i<players.size();i++){
    Player _p = players.get(i);
    //_p.showGold();
    if(_p.id==playerTurn){_p.showActions(!currentPlayer().hasUpgrade());}
    
    if(_p.id==playerTurn){
      if(gameState==STATE_GAMEOVER){
        printShaded("EXIT",2*scaledScrWidth-42,2*scaledScrHeight-12);
      }else{
        if(_p.isHuman){
          endTurnButton.render();
        }else{
          printShaded("ENEMY TURN "+gameTurn,2*scaledScrWidth-42,2*scaledScrHeight-12);
        }
      }
    }
  }
}

public void playerPane(){
  int _placer=0;
  int _atY=32;
  for(int i=0;i<players.size();i++){
    _placer=i;    
    image(player_pane,0,_atY+(_placer*16));
    tint(tintByOwner2(i+1,color(10)));
    image(player_pane_colorplate,0,_atY+(_placer*16));
    if(i+1==playerTurn){
      tint(220,210,200);
      image(player_pane_current,0,_atY+(_placer*16));
    }
    Player _p = players.get(i);
    if(_p.isAlive){
      printShadedDark(getPlayerDesc(_p.id),32,_atY*2+15+(_placer*32));
      printShadedGold("GOLD: "+_p.gold+"/"+_p.income,32,_atY*2+24+(_placer*32));
    }else{
      printShadedDark(getPlayerDesc(_p.id),32,_atY*2+15+(_placer*32));
      printShadedDark(" - DEAD -",32,_atY*2+24+(_placer*32));
    }
    if(_p.isHuman) {
        noTint();
        scale(0.5f);
        image(iconHuman,3,_atY*2+19+(_placer*32));
        scale(2);
    }
    if(_p.id==1 && level == 1 && campaignMode){
      printShaded("SELECT",32,14*12);
      printShaded("ACTION",32,15*12);
      printShaded("BELOW,",32,16*12);
      printShaded("THEN",32,17*12);
      printShaded("ATTACK",32,18*12);
      printShaded("ADJACENT",32,19*12);
      printShaded("AREA",32,20*12);
      printShaded("|",32,22*12);
      printShaded("|",32,23*12);
      printShaded("\\/",32,24*12);
    }
    noTint();
  }
  if(sMouseX()<32){
    if(sMouseY()>32 && sMouseY()<48) showUpgradedActions(1);
    if(sMouseY()>48 && sMouseY()<64) showUpgradedActions(2);
    if(sMouseY()>64 && sMouseY()<80) showUpgradedActions(3);
    if(sMouseY()>80 && sMouseY()<94) showUpgradedActions(4);
  }
}

public String getPlayerDesc(int id){
  String s="";
  if(id>players.size()) return s;
  if(id==1) s="BLUE KING";
  if(id==2) s="RED QUEEN";
  if(id==3) s="GREEN QUEEN";
  if(id==4) s="PURPLE KING";
  return s;
}

public void showUpgradedActions(int playerId){
  if(playerId > players.size()) return;
  Player p = players.get(playerId-1);
  int upgradeCount=0;
  for(int i=0;i<p.actions.size();i++){
    if(p.actions.get(i).upgraded){
      image(p.actions.get(i).pic2, 0, scaledScrHeight-32-i*16);
      upgradeCount++;
    }
  }
  image(p.portrait,0,0);
  if(p.research>0) printShaded("RESEARCH: " + p.research, 32, 58);
}
public void hasUpgrade(){
  if(currentPlayer().hasUpgrade()){
    deselectAreas();
    image(upgrade_pane,0,scaledScrHeight-info_pane.height);
    printShaded("CHOOSE UPGRADE:",PApplet.parseInt(scaledScrWidth*0.18f),2*scaledScrHeight-12);
    int _btnCount=0;
    for(int _a=0;_a<currentPlayer().actions.size()-1;_a++){
      Action _action = currentPlayer().actions.get(_a);
      if(!_action.upgraded){
        _btnCount++;

        Button upgradeButton = getUpgradeButton(_action.type);
        upgradeButton.x1 = PApplet.parseInt( (scaledScrWidth*0.1f) + (_btnCount*action_army.width*1.2f) );
        upgradeButton.update();
       
      }
    }
  }
}
public void playSound(String _snd){
  if(_snd.equals("song")){
      song.rewind();
      song.play();
    }
  if(!playSound) return;
  boolean _noHuman=true;
  for(int _i=0;_i<players.size();_i++){
    Player _p = players.get(_i);
    if(_p.isHuman && _p.isAlive){_noHuman=false;}
  }
  if(!_noHuman){
    
    if(_snd.equals("battle")){
      battle1.rewind();
      battle1.play();
    }
    if(_snd.equals("fail")){
      fail1.rewind();
      fail1.play();
    }
    if(_snd.equals("nogo")){
      nogo.rewind();
      nogo.play();
    }
    if(_snd.equals("fort")){
      fort1.rewind();
      fort1.play();
    }
      if(_snd.equals("castle")){
      castle1.rewind();
      castle1.play();
    }
    if(_snd.equals("fleet")){
      fleet1.rewind();
      fleet1.play();
    }
      if(_snd.equals("trade")){
      trade1.rewind();
      trade1.play();
    }
    if(_snd.equals("gold")){
      if(random(1)<0.5f){
        gold1.rewind();
        gold1.play();
      }else{
        gold2.rewind();
        gold2.play();
      }
    }
    if(_snd.equals("plague")){
      plague1.rewind();
      plague1.play();
    }
    if(_snd.equals("rebellion")){
      rebellion1.rewind();
      rebellion1.play();
    }
  }
}
public void splash(){
  if(waitForClick){
    image(splash,0,0);
    ticker();
  }else{
    gameState=STATE_GENERATOR;
    waitForClick=true;
  }
}

public void ticker(){
  if(tickerTimer+10<millis()){
    tickerTimer=millis();
    tickerPos-=0.5f; 
  }
  if(tickerPos<-850) tickerPos=scrWidth*1.35f;;
    printShaded(tickerText, PApplet.parseInt(tickerPos), PApplet.parseInt(scrHeight*0.49f) );
}
public void startScreen(){
    image(bg,0,0);
    playButton.update();
    settingsButton.render();
    if(playButton.mouseOver && mouseClicked){
      gameState=STATE_GENERATOR;
    }
}
public void tick(){
    if(tickTimer>millis()){
        return;
    }else{
        tickTimer=millis()+tickDelay;
    }
    tickAnims();
    checkMusic();
}

public void tickAnims(){
    for (int i = anims.size()-1; i >= 0; i--) { 
    Animation _anim = (Animation) anims.get(i);
    _anim.tick();
    if (_anim.finished) {
      anims.remove(i);
    }
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "crown_and_council_B6" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
