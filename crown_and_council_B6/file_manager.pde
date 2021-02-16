String[] loadStringArray(String file){
  String f = file;
  String[] _l = loadStrings(f+".txt");
  println("loaded file:",f);
  return _l;
}
void saveStringArray(String file, String[] strings){
  String f=file+".txt";
  String[] s = strings;
  saveStrings(f, s); 
  println("saved file:",f);
}

void loadSound(){
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


void loadCurrent(){
    color levelSeed;
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

String printRGB(color col){
    return int(red(col))+", "+int(green(col))+", "+int(blue(col));
}

void saveCurrent(color levelSeed){
  if(campaignMode){
    worldMap.set(0,worldMap.height-1,levelSeed);
    worldMap.save("data/"+campaignFolder+"/worldmap"+level+".png"); 
  }else{
    worldMap.set(0,worldMap.height-1,levelSeed);
    worldMap.save("data/generated_current.png"); 
  }
}

void loadImages(){//splash;
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
  
  defenseImg = new PImage[int(defMap.width/16)];
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
void loadSettings(){
    String[] l = loadStrings("settings.txt");
	for(String line : l){
    	if(line.startsWith("//")) continue;
    	String[] setting = split(line,':');
    	if( setting[0].equals("scale") ) myScale = int(setting[1]);
        if( setting[0].equals("map") ){ level = int(setting[1]);}
        if( setting[0].equals("lastMap") ) maxMaps = int(setting[1]);
        if( setting[0].equals("campaignFolder") ) campaignFolder = setting[1];
        if( setting[0].equals("playMusic") ) playMusic = boolean(setting[1]);
        if( setting[0].equals("playSound") ) playSound = boolean(setting[1]);
        if( setting[0].equals("showAreaInfo") ) showAreaInfo = boolean(setting[1]);
	}
    println("SETTINGS LOADED");
}
void saveSettings(){
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