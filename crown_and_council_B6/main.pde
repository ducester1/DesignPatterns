void draw(){
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
      selectableSine+=0.25;
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

  void won(int playerIndex) {
    players[playerIndex]++;
    totalCount++;
  }
  
  int get(int playerIndex) {
    return players[playerIndex];
  }
  
  float winFraction(int playerIndex) {
    return players[playerIndex] / float(totalCount);
  }
}

void startMapTest() {
  if(!campaignTest) println("STARTING MAPTEST");
  gameState = STATE_MAPTEST;
  testIterations = 0;
  winStats = new WinStats();
  setPlayersToAI();
  noAnims=true;
}
boolean campaignTest = true;

void updateMapTest(){
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


void printWins(){
  if(!campaignTest) println("\n");
  if(!campaignTest) println("LEVEL:",level);
  for(Player player : players){
    if(!campaignTest) println("PLAYER", player.id, "WINS:", winStats.get(player.id));
  }
  println( "LEVEL:",level, "WINS: " + printBar( int( ( 100 * winStats.winFraction(1) ) ) ));
  if(!campaignTest) println();
}

void setPlayersToAI(){
  for(Player player : players){
    player.isHuman=false;
  }
}

String printBar(int l){
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
int getWinner(){
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

void setScreen(){
  background(0);

  float scaleChangeX = float(width)/float(scrWidth);
  float scaleChangeY = float(height)/float(scrHeight);
  float minScale = min(scaleChangeX,scaleChangeY);
  
  myScale = (float) scrWidth / scaledScrWidth * minScale;
  scale(myScale);

  if (oldWidth == width && oldHeight == height)
    return;

  oldWidth = width;
  oldHeight = height;

  int newWidth = int(scrWidth*minScale);
  int newHeight = int(scrHeight*minScale);
  
  surface.setSize(newWidth, newHeight);
}
