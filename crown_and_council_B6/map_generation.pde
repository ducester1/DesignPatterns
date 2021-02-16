void generatorState(){
  background(100);
  image(worldMap,32,0);
  if(loadMap){
    startCurrentMap();
  }else{
    createMap();
  }
}

void createMap() {
    color levelSeed;
  while (true) { 
    randomSeed(millis());
    levelSeed = color(int(random(255)),int(random(255)),int(random(255)));
    startMap();
    generateMap();
    makeRivers(int(random(200,1500)));
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
void setEventWeights(){
  chanceOfEvent=random(0.3,0.99);
  eventDistribution=random(0.1,0.9);
  //println(chanceOfEvent,eventDistribution);
}
void makeRivers(int riverCount){
  for(int t=0;t<riverCount;t++){
    int riverLength=int(random(5,35));
    boolean atCoast=false;
    IntPoint r=new IntPoint( int(random(1,scaledScrWidth-1)), int(random(1,scaledScrHeight-1)) );
    
    while(worldMap.get(r.x,r.y) != waterPixel){
      r=new IntPoint( int(random(1,scaledScrWidth-1)), int(random(1,scaledScrHeight-1)) );
    }
    
    while(!atCoast){
      int dir=int(random(8));
      r = r.move8Directions(dir);
      if(r.x < 0 || r.x > worldMap.width || r.y < 0 || r.y > worldMap.height) {
        r = r.moveDirection(dir, -1); 
      }
      if(countSurrounding(worldMap, r.x, r.y, waterPixel) > 0) atCoast=true;
    }
    for(int i=0;i<riverLength;i++){
      int dir=int(random(8));
      r = r.move8Directions(dir);
      worldMap.set(r.x,r.y,waterPixel);
    }
  }
}

boolean isWorldValid(WorldValidator ... validators){
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
  boolean validate();
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

void removeSmallAreas(){
  HashMap<Integer, ArrayList<Integer>> colorPositions = new HashMap();
  color waterColor = color(0);
    
  for(int i = 0;i<worldMap.pixels.length;i++){
    color c = worldMap.pixels[i];
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

void fixBorders(){
  fillRect(worldMap, 0, 0, 32, worldMap.height, color(0));
  fillRect(worldMap, 0, worldMap.height-16, worldMap.width, 16, color(0));
}
void makeMapBase(int mWidth, int mHeight, int gridW, int gridH){
  color[][] colArray = new color[gridW][gridH];
  colArray=fillColArray(colArray);
  worldMap=createImage(mWidth,mHeight,RGB);
  int spacerX = int(mWidth/gridW);
  int spacerY = int(mHeight/gridH);
  
  for(int x=0;x<colArray.length;x++){
    for(int y=0;y<colArray[x].length;y++){
      int atX = x*spacerX;
      int atY = y*spacerY+int(random(10));
      int boxW = max(3,int(random(spacerX*0.7)));
      int boxH = max(3,int(random(spacerY*0.7)));
      if(y<colArray[x].length-1 && x<colArray.length-1){
        if(colArray[x+1][y]==colArray[x][y]) boxW+=int(spacerX*1.5);
        if(colArray[x][y+1]==colArray[x][y]) boxH+=int(spacerY*1.5);
      }
      int shift=0;
      if(isOdd(y)) shift=int(spacerX*0.5);
      fillRect(worldMap,atX+shift,atY,boxW,boxH,colArray[x][y]);
    }
  }
}

void fillRect(PImage img, int x, int y, int w, int h, color c){
  for(int _x=x; _x<x+w; _x++){
    for(int _y=y; _y<y+h; _y++){
      img.set(_x,_y,c);
    }
  }
}

color [][] fillColArray(color[][] colArray){
  float chanceOfWater=random(0.3,0.8);
  float chanceOfMerge=random(0.1,0.8);
  for(int x=0;x<colArray.length;x++){
    for(int y=0;y<colArray[x].length;y++){
      boolean duplicate=true;
      while(duplicate){
        duplicate=false;
        color c=color(int(random(1)*50)+145,int(random(1)*55)+155,int(random(1)*40)+140);
        if(!isUniqueColor(c,colArray)) { 
          duplicate=true;
        }else{
          colArray[x][y]=c;
        }
      }
      if(random(1)<chanceOfWater) colArray[x][y]=color(0);
      if(random(1)<chanceOfMerge){
        int sx = int(random(3)) - 1; 
        int sy = (sx!=0) ? 0 : int(random(3)) - 1;
        if(x+sx>=0 && x+sx < colArray.length && y+sy >= 0 && y+sy < colArray[x].length) colArray[x+sx][y+sy]=colArray[x][y];
      }
    }
  }
  return colArray;
}
boolean isUniqueColor(color c, color[][] colArray){
  for(int x=0;x<colArray.length;x++){
    for(int y=0;y<colArray[x].length;y++){
      if(c==colArray[x][y]) return false;
    }
  }
  return true;
}

void makeStroke(PImage map, int x, int y, float rotation, int l, float growth, color c) {
  PVector pos = new PVector(x, y);
  PVector dir;
  dir = PVector.fromAngle(radians(rotation));
  int r = int(random(5));
  int turn = 15;
  for (int i = 0; i<l*0.5; i++) {
    if (random(1)<growth) r++;
    if (random(1)<0.05) r*=0.7;
    drawDot(int(pos.x), int(pos.y), map, r, c);
    //}
    pos.add(dir);
    dir.rotate(radians(random(-turn, turn)));
  }
  for (int i = 0; i<l*0.5; i++) {
    if (random(1)<growth) r--;
    if (random(1)<0.05) r*=1.1;
    if (r<1 && i < l*0.45) r++;
    drawDot(int(pos.x), int(pos.y), map, r, c);
    pos.add(dir);
    dir.rotate(radians(random(-turn, turn)));
  }
}

void drawDot(int x, int y, PImage img, float r, color c){
  for(int x1 = int(x-r)-1; x1<=x+r+1;x1++){
    for(int y1 = int(y-r)-1; y1<=y+r+1;y1++){
      if(dist(x,y,x1,y1)<r){
        img.set(x1,y1,c);
      }
    }
  }
}

void drawDotOnColor(int x, int y, PImage img, float r, color c, color onThis){
  for(int x1 = int(x-r-1); x1<=x+r+1;x1++){
    for(int y1 = int(y-r-1); y1<=y+r+1;y1++){
      if(dist(x,y,x1,y1)<r && img.get(x1,y1) == onThis){
        img.set(x1,y1,c);
      }
    }
  }
}



void generateMap(){
  int _N,_xx,_yy;
  
  color _doCol=color(0);
  color _watCol=worldMap.get(0,0);
  for(int i = 0;i<500000;i++){
    _xx=int(random(1,scaledScrWidth-1));
    _yy=int(random(1,scaledScrHeight-1));
    _doCol = worldMap.get(_xx,_yy);
    if(_doCol==_watCol){
      _N=countSurrounding(worldMap,_xx,_yy,_watCol);
      if(_N<random(1,7)){
        color nc = getNeighborColor(worldMap,_xx,_yy);
        drawDotOnColor(_xx, _yy, worldMap, random(1,3.5), nc, _watCol);
      }
    }
  }
  for(int i = 0;i<300000;i++){
    _xx=int(random(1,scaledScrWidth-1));
    _yy=int(random(1,scaledScrHeight-1));
    _doCol = worldMap.get(_xx,_yy);
    if(_doCol==_watCol){
      _N=countSurrounding(worldMap,_xx,_yy,_watCol);
      if(_N<random(1,7)){
        color nc = getNeighborColor(worldMap,_xx,_yy);
        drawDotOnColor(_xx, _yy, worldMap, 1, nc, _watCol);
      }
    }
  }
}

void drawWithMouse(){
  worldMap.set(1+int(mouseX*(1/myScale)),1+int(mouseY*(1/myScale)),color(0));
  worldMap.set(int(mouseX*(1/myScale)),int(mouseY*(1/myScale)),color(0));
  worldMap.set(-1+int(mouseX*(1/myScale)),-1+int(mouseY*(1/myScale)),color(0));
  worldMap.set(1+int(mouseX*(1/myScale)),-1+int(mouseY*(1/myScale)),color(0));
}


void startCurrentMap(){
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

void makeCoastLine(){
    water=loadImage("water.png");
  color alphaColor = color(0);
  for(int _y=1;_y<worldMap.height-2;_y++){
    for(int _x=1;_x<worldMap.width-2;_x++){
      int landProximity=9-(countSurrounding(worldMap, _x, _y, alphaColor));
      landProximity=constrain(landProximity,0,2);
      color _cTweak = color(red(water.get(_x,_y))+landProximity*14,green(water.get(_x,_y))+landProximity*16,blue(water.get(_x,_y))+landProximity*16);
      water.set(_x,_y,_cTweak);
    }
  }
}
void createWorld(){
  areas = new ArrayList<Area>();
  color alphaColor = worldMap.get(0,0);
  color currColor = color(0);
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

void makeArea(color _col,int _xx,int _yy){
  color _c = _col;
  color _moc = color(245);
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

int countSurrounding(PImage _img, int xx, int yy, color compareWith){
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
boolean inImg(PImage _img,int _x,int _y){
  boolean in=false;
  if(_x >= 0 && _y >= 0 && _x <= _img.width  && _y <= _img.height ){
    in=true;
  }
  return in;
}

color getSumOfSurrounding(PImage _baseImg,int _x,int _y){
  return (_baseImg.get(_x,_y));
}

//---------------------------------------
void initPlayers(){
  gameTurn=1;
  playerTurn=1;
  playerCount = 0;
  players.clear();
  noPlayers=(level>3 || !campaignMode) ? 4 : level+1 ;
  while(playerCount < noPlayers){
    Area _a = areas.get(int(random(areas.size())));
    if(_a.ownedBy==0){
      _a.ownedBy=playerCount+1;
      _a.fort=true;
      _a.city=false;
      _a.uni=false;
      _a.independence=0.000001;
      playerCount++;
      players.add(new Player("Player " + _a.ownedBy, int(1+playerCount*1.25), _a.id,playerCount));
    }
  }
  specialCases();
  randomSeed(millis());
}

void specialCases(){
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
            p.aiPreference[ACTION_FORT]=0.0005;
            p.aiPreference[ACTION_FLEET]=0.8;
        }
    }
    if(level==5){
        Player p = players.get(int(random(1,4)));
        for(int i=0;i<p.aiPreference.length;i++){ 
        	p.aiPreference[i]=0.1 + random(0.1);
    	}
    	p.aiPreference[4] = 0.9;
    }
}
void startMap(){
  if(!campaignTest) println("INITIALIZING NEW MAP WITH", genAreas,"AREAS");
  int gw = int(random(4,18));
  int gh = int(gw*0.8);
  makeMapBase(scaledScrWidth-38,scaledScrHeight-20,gw,gh);
  PImage m = createImage(scaledScrWidth,scaledScrHeight,RGB);
  m.copy(worldMap,0,0,worldMap.width,worldMap.height, m.width-worldMap.width,0,worldMap.width,worldMap.height);
  worldMap = m;
}

void areaBoxing(int x, int y, color c){
  int b=int(random(5));
  for(int i=0;i<b;i++){
    int _xr=int(random(-10,10));
    int _yr=int(random(-10,10));
    
    for(int _y=-5;_y<5;_y++){
      for(int _x=-4;_x<4;_x++){
        worldMap.set(x+_xr+_x,y+_yr+_y,c);
      }
    }
  } 
}


void initMap(){ 
  for(int _my=0;_my<gridHeight;_my++){
    for(int _mx=0;_mx<gridWidth;_mx++){
      miniMap.set(_mx,_my,color(int(random(1)*50)+145,int(random(1)*55)+155,int(random(1)*40)+140));
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
    int XX=int(random(gridWidth+1));
    int YY=int(random(gridHeight+1));
    if(_aCount<genAreas){
      miniMap.set(XX,YY,color(int(random(1)*150)+100,int(random(1)*150)+100,int(random(1)*150)+100));
    } else{
      miniMap.set(XX,YY,color(0)); 
    }
  }
}
color getNeighborColor(PImage pic,int x,int y){
  int dx = int(random(-2,2));
  int dy = int(random(-2,2));
  return pic.get(x+dx,y+dy);
}




void printNeighborlist(String _currentMousePressedArea){
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