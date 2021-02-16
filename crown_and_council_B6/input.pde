
void mouseReleased() {
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

Button getUpgradeButton(int actionId) {
	if (actionId == ACTION_ARMY) return upgradeArmyButton;
    if (actionId == ACTION_FORT) return upgradeFortButton;
    if (actionId == ACTION_FLEET) return upgradeFleetButton;
    if (actionId == ACTION_VILLAGE) return upgradeTradeButton;
    return null;
}

void checkUpgradeButtons(Player _p){
  for (int actionId = 0; actionId < NUM_UPGRADE_ACTIONS; ++actionId) {
      if (getUpgradeButton(actionId).mouseOver() && !_p.isActionUpgraded(actionId)) {
          _p.upgrade(actionId);
      }
  }
}


void keyPressed() {
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

void keyReleased() {
    if (key == '+') {
    myScale = int(constrain(myScale+1,1,6));
    println(myScale);
    saveSettings();
    screenSet();
  }
  if (key == '-') {
    myScale = int(constrain(myScale-1,1,6));
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
        color levelSeed = color(int(random(255)),int(random(255)),int(random(255)));
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
    myScale = int(constrain(myScale+1,1,6));
    println(myScale);
    saveSettings();
    screenSet();
  }
  if (key == '-') {
    myScale = int(constrain(myScale-1,1,6));
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
      String name = "worldmap" + int(random(1)*10000) + ".png";
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