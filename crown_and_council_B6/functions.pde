void startCampaignMap(){
    gameState=STATE_GENERATOR;
    gameSetup();
}

int sMouseX(){
  return int(mouseX/myScale);
}
int sMouseY(){
  return int(mouseY/myScale);
}

Player currentPlayer() {
  return players.get(playerTurn - 1);
}

boolean isEven(int n){
  return (n&1)==0;
}
boolean isOdd(int n){
  return (n&1)==1;
}

void checkIfPlayerIsDead() {
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
void killPlayer(Player p){
  p.isAlive=false;
  screenShake=3;
  p.gold=0; 
  p.income=0;
  for(int i=0;i<p.actions.size();i++){
    p.actions.get(i).upgraded=false;
  }
}
Player getController(Area a){
  return players.get(a.ownedBy-1);
}

int getIncome(Player _plr){
  int _inc=baseIncome;
  for(int _i=0;_i<areas.size();_i++){
    Area _a = areas.get(_i);
    if(_a.ownedBy==_plr.id && _a.invadedBy==0){
      _inc+=_a.income;
    }
  }
  return _inc;
}


void runPlayer() {
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

String getActionName(int actionId){
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

void startTurn(){
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

void actionSelection(Player _currP) {
  for (int i=0;i<_currP.actions.size();i++) {
    Action _action = _currP.actions.get(i);
    if (_action.selected) return;
  }
    deselectAreas();

}

void deselectAreas() {
  for (Area area : areas) {
    area.deselect();
  }
}

void selectAction(int actionId){
  for (int i =0;i<currentPlayer().actions.size();i++){
    currentPlayer().actions.get(i).selected = false;
  }
  currentPlayer().actions.get(actionId).selected = true;
  //unselectActions(actionId);
  selectableAreas(actionId);
}

void endTurn() {
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
  _p.aggression = (_p.aggression > 0) ? _p.aggression-0.2 : 0;
  doStartTurn=true;
}

void reduceContested(){
    for (int i=0;i<areas.size();i++) {
    Area a = areas.get(i);
    if(a.contested>5){a.contested=5;}
    if(a.contested<-5){a.contested=-5;}
    a.contested--;
  }
}

void winCheck() {
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

void collectGold() {
  
  doEvent();
  Player _p = currentPlayer();
  _p.gold+=_p.income;
  for (int i=0;i<areas.size();i++) {
    Area a = areas.get(i);
    if (a.ownedBy == playerTurn) {
      if(a.invadedBy != playerTurn) {
        for (int g=0;g<a.income;g++) {
          if (_p.isAlive) {
            playAreaAnim(a, animGold, 0.6,true,int(random(2, 15)));
            if(_p.isActionUpgraded(3)){
            }
            playSound("gold");
          }
        }
        if(a.mine && random(1) < 0.20 && _p.isAlive){
          for(int _i=0;_i<8;_i++){
            playAreaAnim(a, animGold, 0.6,true,int(random(2, 15)));
            playSound("gold");
          }
        }
        if(a.uni && _p.isAlive){
          playAreaAnim(a, animResearch, 0.4,true,int(random(2, 15)));
          _p.research++;
        }
      } else {
        a.invadedBy = 0;
        playAreaAnim(a, animSmoke, 0.17,true,int(random(2, 15)));
      }
    }
  }
}

void playAreaAnim(Area a, PImage[] anim, float speed, boolean variedPos, float delay) {
  playAreaAnim(a,anim,speed,variedPos,delay,0,0);
}

void playAreaAnim(Area a, PImage[] anim, float speed, boolean variedPos, float delay, int xShift, int yShift) {
  if (noAnims) {
    return;
  }
  if(variedPos){
    anims.add (new Animation(anim, int(a.x1+a.pic.width*0.5-8+random(-10, 10))+xShift, int(a.y1+a.pic.height*0.5-8+random(-10, 10))+yShift, speed).setDelay(delay));
  }else{
    anims.add (new Animation(anim, int(a.x1+a.pic.width*0.5-8)+xShift, int(a.y1+a.pic.height*0.5-8)+yShift, speed).setDelay(delay));
  }
}

void areaClicked(String _areaName) {
  
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
          playAreaAnim(_withArea, animNope, 0.3,false,1);
          RMB=false;
          return;
        }
      } 
      else {
        _player.gold -= _action.cost;
        float odds;
        switch(_type) {
        case 0 : // army
          _player.aggression += 0.1;
          adjustDiplomacy(_withArea.ownedBy);
          playAreaAnim(_withArea, animBomb, 0.3,false,0);
          odds=0.0;
          if (_withArea.ownedBy !=0 ) {
            odds=0.3;
          }
          if (_withArea.fort) {
            odds=0.8;
            if(_withArea.ownedBy>0){
              Player _defender = players.get(_withArea.ownedBy-1);
              if(_defender.isActionUpgraded(1)){
                odds=1.4;
              }
            }
            if(currentPlayer().isActionUpgraded(0)){odds*=0.5;}
          }
          odds *= (_withArea.mountain) ? 1.25 : 1;
          odds *= _withArea.defense();
 
          if (random(1)>odds || (playerTurn==1 && cheat)) {
            _withArea.contested +=2;
            _withArea.invadedBy = playerTurn;
            playSound("battle");
            _withArea.ownedBy=playerTurn;
            _withArea.fort=false;
            _withArea.uni=false;
            playAreaAnim(_withArea, animCheck, 0.3,false,10);
            
          } else {
            _withArea.reduceDefense();
            playSound("fail");
            if(RMB) areaClicked(_areaName);
          }
          break;
        case 1 : // fort
          playSound("fort");
          playAreaAnim(_withArea, animBomb, 0.3,false,0);
          if(_withArea.city){_withArea.income -=1;} 
          //_withArea.castle=false;
          _withArea.fort=true;
          _withArea.city=false;
          _withArea.uni=false;
          _withArea.contested=0;
          break;
        case 2 : // fleet
          _player.aggression += 0.1;
          adjustDiplomacy(_withArea.ownedBy);
          playAreaAnim(_withArea, animBomb, 0.3,false,0);
          odds=0.3;
          if (_withArea.ownedBy !=0 ) {
            odds=0.6;
          }
          if (_withArea.fort) {
            odds=0.9;
            if(_withArea.ownedBy>0){
              Player _defender = players.get(_withArea.ownedBy-1);
              if(_defender.isActionUpgraded(1)){
                odds=1.4;
              }
            }
            if(currentPlayer().isActionUpgraded(2)){odds*=0.5;}
          }
          odds *= (_withArea.mountain) ? 1.25 : 1;
          odds *= _withArea.defense();
          if (random(1)>odds || (playerTurn==1 && cheat)) {
            _withArea.contested+=2;
            _withArea.invadedBy = playerTurn;
            playSound("fleet");
            _withArea.ownedBy=playerTurn;
            _withArea.fort=false;
            _withArea.uni=false;
            playAreaAnim(_withArea, animCheck, 0.3,false,10);
          } else {
            _withArea.reduceDefense();
            playSound("fail");
            if(RMB) areaClicked(_areaName);
          }
          break;
        case 3 : // trade
          playSound("trade");
          playAreaAnim(_withArea, animBomb, 0.3,false,0);
          _withArea.income=_withArea.income+1;
          _withArea.fort=false;
          _withArea.city=true;
          _withArea.uni=false;
          _withArea.contested=0;
          break;
        case 4 : // uni
          playSound("trade");
          playAreaAnim(_withArea, animBomb, 0.3,false,0);
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


void selectableAreas(int _type) { 
  
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

void doEvent() {
  if(level<=4 && campaignMode) return;
  
  if (random(1)<chanceOfEvent) {
    
    Area _withArea;
    int _xx;
    int _yy;
    int _e = (random(1)<eventDistribution) ? 0:1;
    switch (_e) {

    case 0 :
    if(random(3)<countPlayerUnis()) return;
    _withArea = areas.get(int(random(areas.size())));
    _xx = int(_withArea.x1+_withArea.pic.width*0.5-8);
    _yy = int(_withArea.y1+_withArea.pic.height*0.5-8);
    	boolean doPlague = (level>=6 || !campaignMode);
      if (_withArea.income>1 && doPlague) {
        if (_withArea.city) {
          _withArea.income=1;
          _withArea.city=false;
          if(gameState!=STATE_MAPTEST) {
            popMessages.add (new PopMessage("PLAGUE!", _xx*2+16, _yy*2+35, 0.9, 25).setDelay(30));
            playAreaAnim(_withArea, animPlague, 0.25,false,0,0,-4);
            playSound("plague");
            screenShake=1.5;
          }
        }
      }
      break;
    case 1 :
    _withArea = getRebelArea(); 
    _xx = int(_withArea.x1+_withArea.pic.width*0.5-8);
    _yy = int(_withArea.y1+_withArea.pic.height*0.5-8);
      boolean doRebellion = (level>=7 || !campaignMode);
      if(currentPlayer().controlFactor()<0.05) doRebellion = false;
      if(countControlledAreas(currentPlayer()) <= 1) doRebellion = false;
      if(_withArea.ownedBy <= 0) doRebellion = false;
      if(currentPlayer().actions.get(ACTION_VILLAGE).upgraded && random(1)<0.7) doRebellion = false;
      if (_withArea.ownedBy>0 && doRebellion) {   
          _withArea.ownedBy=0;
          if(gameState!=STATE_MAPTEST) {
            playAreaAnim(_withArea, animFight, 0.3,false,0,0,-4);
             popMessages.add (new PopMessage("REBELLION!", _xx*2+16, _yy*2+35, 0.9, 25).setDelay(30));
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

void printPlayerPrefs(){
    
    for(int i=0;i<players.size();i++){
            Player p = players.get(i);
            println("PLAYER ",p.id, "     ARMY:", p.aiPreference[0] , "     FORT:", p.aiPreference[1] , "     FLEET:", p.aiPreference[2] , "     CITY:", p.aiPreference[3] , "     UNIVERSITY:", p.aiPreference[4] );
        }
}

int countPlayerUnis(){
    Player p = currentPlayer();
    int c=0;
    for(Area a : areas){
        if(a.uni && a.ownedBy==p.id) c++;
    }
    return c;
}

Area getRebelArea(){
    float rs=0;
    Area a = areas.get(0), c = areas.get(int(random(areas.size()))) ;
    for (int i = 0;i<areas.size();i++){
        a=areas.get(i);
        if( a.ownedBy == currentPlayer().id && a.rebelScore() + random(0.05) > rs ){
            rs=a.rebelScore();
            c=a;
        }
    }
    return c;
}

int countUnis(Player _pl){
  int _count=0;
  for(int _c=0;_c<areas.size()-1;_c++){
    Area _a = areas.get(_c);
    if (_a.ownedBy == _pl.id && _a.uni){
      _count++;
    }
  }
  return _count;
}

int countControlledAreas(Player _pl){
  int _count=0;
  for(int _c=0;_c<areas.size()-1;_c++){
    Area _a = areas.get(_c);
    if (_a.ownedBy == _pl.id){
      _count++;
    }
  }
  return _count;
}

void printShadedFade(String _info, int _x, int _y, int _fade) {
  //scale(0.5);
  String _mes = _info;
  int fading = _fade;
  fill(60, 50, 20, 250*(fading*0.1));
  text(_mes, 1+_x*0.5, 1+_y*0.5);
  text(_mes, _x*0.5, 1+_y*0.5);
  fill(230, 190, 100, 250*(fading*0.1));
  if (_mes.equals("PLAGUE!")) {
    fill(70, 199, 90, 250*(fading*0.1));
  }
  if (_mes.equals("REBELLION!") ){
    fill(199, 70, 70, 250*(fading*0.1));
  }
  text(_mes, _x*0.5, _y*0.5);
  //scale(2);
}
void printShadedGold(String _info, int _x, int _y) {
  scale(0.5);
  fill(30, 25, 10);
  text(_info, 1+_x, 1+_y);
  text(_info, _x, 1+_y);
  fill(170, 140, 60);
  text(_info, _x, _y);
  scale(2);
}
void printShaded(String _info, int _x, int _y) {
  scale(0.5);
  fill(30, 25, 10);
  text(_info, 1+_x, 1+_y);
  text(_info, _x, 1+_y);
  fill(170, 160, 130);
  text(_info, _x, _y);
  scale(2);
}
void printShadedDark(String _info, int _x, int _y) {
  scale(0.5);
  fill(40, 30, 20);
  text(_info, 1+_x, 1+_y);
  text(_info, _x, 1+_y);
  fill(100, 90, 60);
  text(_info, _x, _y);
  scale(2);
}

void printInfo(String _info) {
  scale(0.5);
  fill(60, 50, 20);
  text(_info, 1+scaledScrWidth, scaledScrHeight*2-12);
  text(_info, scaledScrWidth, scaledScrHeight*2-12);
  fill(230, 190, 100);
  text(_info, scaledScrWidth, scaledScrHeight*2-13);
  scale(2);
}
color tintByOwner(int _withPlayer) {
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

color tintByOwner2(int _withPlayer, color _c) {
  color _inCol=_c;
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

void unselectActions(int type) {
  for (int _i=0;_i<players.size();_i++) { 
    Player _p = players.get(_i);
    for (int _u=0;_u<_p.actions.size();_u++) {
      Action _a= _p.actions.get(_u);
      if(_a.type == type){ _a.selected=!_a.selected; }else{ _a.selected=false; }
    }
  }
}


PImage renderArea(PImage _im) {
  int noiseRange=20;
  PImage _baseImg=_im;
  for (int _y=0;_y<_im.height;_y++) {
    for (int _x=0;_x<_im.width;_x++) {
      if (_baseImg.get(_x, _y) != color(0)) {
        int borderProximity = (countSurrounding(_baseImg, _x, _y, color(0))*14);
        float _centerdist = dist(_x, _y, (_im.width*0.5), (_im.height*0.5));
        float pOfHalf = _centerdist/dist(0, 0, (_im.width*0.5), (_im.height*0.5));
        int adj = int(borderProximity+(100*pOfHalf));
        _im.set(_x, _y, color(red(_im.get(_x, _y))+int(random(-noiseRange, noiseRange))-adj, green(_im.get(_x, _y))+int(random(-noiseRange, noiseRange))-adj, blue(_im.get(_x, _y))+int(random(-noiseRange, noiseRange*0.1))-adj));
      }
    }
  }


  return _im;
}

String getRandomTaunt(){
 return "YOUR " + tauntPart1[int(random(tauntPart1.length))] + " IS " + tauntPart2[int(random(tauntPart2.length))] + "'S " + tauntPart3[int(random(tauntPart3.length)) ];
}