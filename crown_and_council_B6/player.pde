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
    maxUni=int(random(3,5));
    uniFondness=random(1)+.5;
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

 void setAIPreference(){
    aiPreference[0] = 0.4 + random(0.1);
    aiPreference[1] = 0.4 + random(0.1);
    aiPreference[2] = 0.4 + random(0.1);
    aiPreference[3] = 0.4 + random(0.1);
    aiPreference[4] = 0.4 + random(0.1);
 }
 
 void calcDominance(){
   dominance = ( incomeFactor() + controlFactor() + terrainFactor() + upgradeFactor() ) / 4.0; 
   if(!isAlive) dominance=0;
 }
 
  float incomeFactor(){
    float totalIncome=0;
    for (int i=0;i<players.size();i++){
      Player p = players.get(i);
      totalIncome+=p.income;
    }
    return income/totalIncome;
  }
  
 float controlFactor(){
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
  int areaControlledCount(){
     int controlled = 0;
     for (int i=0;i<areas.size();i++) {
        Area a = areas.get(i);
        if(a.ownedBy==id){
          controlled++;
        }
     }
     return controlled;
  }
 boolean hasUpgrade() {
   Boolean _upgradeable=false;
   for(int _i=0;_i<actions.size()-1;_i++){ 
      if(!actions.get(_i).upgraded){_upgradeable=true;}
    }
   return (research >= upgradeCost && _upgradeable);
 }
 
 float terrainFactor(){
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

  float upgradeFactor(){
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
  
 void upgrade(int type) {
     println("UPGRADING", getActionName(type));
   if (research < upgradeCost)
     return;
   if (actions.get(type).upgraded)
     return;
   research -= upgradeCost;
   actions.get(type).upgraded=true;
 }
 
 Boolean isActionUpgraded(int actionId){
     if (actionId < 0 || actionId >= actions.size()) return false;
     return actions.get(actionId).upgraded;
 }
 
 void showActions(boolean canPressAction){
   for(int i=0;i<actions.size();i++){
    Action _a = actions.get(i);
     _a.update(i,canPressAction);
   }
  if(gold*0.5>aiIterations){aiIterations=int(gold*0.5);}
  image(portrait,0,0);
  if(research>0){
    printShaded("RESEARCH: " + research, 32, 58);
  }
 }
 void setIncome(){
  int _inc=baseIncome;
  for(int _i=0;_i<areas.size();_i++){
    Area _a = areas.get(_i);
    if(_a.ownedBy==id && _a.invadedBy==0){
      _inc+=_a.income;
    }
  }
  income=_inc;
 }
 
 void showGold(){
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

int playersAlive(){
  int l=0;
  for(int i = 0; i<players.size();i++){
    Player p = players.get(i);
    if(p.isAlive) l++;
  }
  return l;
}