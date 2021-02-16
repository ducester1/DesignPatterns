
class Area { 
  String name;
  int x1,y1,x2,y2;
  PImage pic;
  PImage mouseOverPic;
  color areaColor;
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
  
  Area (int _x1,int _y1,int _x2,int _y2,PImage _im, PImage _moim, color _c, int _id) {
    id=_id; 
    x1=_x1; 
    y1=_y1;
    x2=_x2;
    y2=_y2;
    if((random(1)<0.5 && level>7)){
      int feature =int(random(8));
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
    namestring =int( random(name1.length));
    name=name1[constrain(namestring,0,name1.length-1)];
    namestring =int( random(name2.length) );
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
  float rebelScore(){
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
     if(fort) rs*=0.5;
     return rs;
  }
  void drawUpgrades() { 
    int _adj=0;
    int _yAdj = (mouseOver) ? -7:-6;
    if(forest){
      image(forestImg,x1+_adj+(pic.width*0.35)-4,y1+(pic.height*0.35+_yAdj-2));
    }
    if(mountain){
      image(mountainImg,x1+_adj+(pic.width*0.35)-4,y1+(pic.height*0.35+_yAdj-2));
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
        image(upgrade_fort2,x1-_adj-2+(pic.width*0.35),y1+(pic.height*0.35)+_yAdj);
      }else{
        image(upgrade_fort,x1-_adj-2+(pic.width*0.35),y1+(pic.height*0.35)+_yAdj);
      }
    }
    if(city){
      Boolean _upg=false;
      if(ownedBy>0){
        if(players.get(ownedBy-1).isActionUpgraded(3)){_upg=true;}
      }
      if(_upg){
        image(upgrade_city2,x1-_adj-2+(pic.width*0.35),y1+(pic.height*0.35)+_yAdj);
      }else{
        image(upgrade_city,x1-_adj-2+(pic.width*0.35),y1+(pic.height*0.35+_yAdj));
      }
    }
    if(uni){
      image(upgrade_uni,x1+_adj-2+(pic.width*0.35),y1+(pic.height*0.35+_yAdj));
    }
    if(mine){
      image(upgrade_mine,x1+_adj+(pic.width*0.35),y1+(pic.height*0.35+_yAdj+4));
    }
    
    
    if (uni && city) println("Error: Has");
    if(mouseOver) showDefense();
    if(showAreaInfo) drawAreaText();
  }
  void drawArea(boolean canShowMouseOver) {
    calcIncome();
    mouseOver=false;
    if(canShowMouseOver && mouseX*(1/myScale)>x1 && mouseX*(1/myScale)<x2 && mouseY*(1/myScale) > y1 && mouseY*(1/myScale) < y2){
      if(pic.get(int(mouseX*(1/myScale)-x1),int(mouseY*(1/myScale)-y1)) != 0){
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
        tint( tintByOwner2( ownedBy, color( int(130)*(1+(sin(selectableSine))*0.4) ) ) );
      }else{
        if (_p.isHuman){tint(int(150)*(1+(sin(selectableSine))*0.2));}
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
  
  void showDefense(){
    int yShift=-6;
    if(!selectable) return;
    if(!currentPlayer().actions.get(0).selected) return;
    if(level>2 && !currentPlayer().actions.get(2).selected) return;
    int tx = int( x1+(x2-x1)*0.5 ) ;
    int ty = int( y1+(y2-y1)*0.5 ) + yShift;
    if(ty < 16) ty+=12;
    
    image(getDefenseImg(),tx-10,ty-10+(sin(selectableSine))*0.2);
  }
  float neighborValue(){
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
  
  void deselect() {
    selectable=false;
    defense=1;
    defenseFrame = 0;
  }
  
  void reduceDefense() {
    defenseFrame++;
    defenseFrame = min(defenseFrame, defenseImg.length - 1);
    
    if (defenseFrame >= defenseImg.length - 1) {
      defense = 0;
    } else {
      defense -= 0.1;
    }
  }
  
  float defense() {
    return defense;
  }
  
  PImage getDefenseImg(){
    return defenseImg[defenseFrame];
  }
  
  void drawAreaText(){
    int _inc =income;
    scale(0.5);
    if(ownedBy>0){
      fill(20,20,10,190);
    }else{
      fill(20,20,10,170);
    }
    
    
    text(name,2+(x1+(x2-x1)*0.5)*2,5+(y1+(y2-y1)*0.5)*2);
    text(name,1+(x1+(x2-x1)*0.5)*2,5+(y1+(y2-y1)*0.5)*2);
    if(ownedBy>0){
      fill(80+red(tintByOwner(ownedBy)),80+green(tintByOwner(ownedBy)),70+blue(tintByOwner(ownedBy)),210);
    }else{
      fill(150,140,100,220);
    }
    text(name,1+(x1+(x2-x1)*0.5)*2,4+(y1+(y2-y1)*0.5)*2);
    //
    
    if(ownedBy>0){
      fill(30,20,10,220);
    }else{
      fill(10,10,10,180);
    }
    
    text( "Income:"+_inc,2+(x1+(x2-x1)*0.5)*2,5-textHeight+(y1+(y2-y1)*0.5)*2);
    text( "Income:"+_inc,1+(x1+(x2-x1)*0.5)*2,5-textHeight+(y1+(y2-y1)*0.5)*2);
    if(ownedBy>0){
      fill(120+red(tintByOwner(ownedBy)),120+green(tintByOwner(ownedBy)),110+blue(tintByOwner(ownedBy)),230);
    }else{
      fill(150,150,130,220);
    }
    
    text( "Income:"+_inc,1+(x1+(x2-x1)*0.5)*2,4-textHeight+(y1+(y2-y1)*0.5)*2);
    scale(2);
  }
  
  void findNeighbors(){ 
    for(int _ly=y1;_ly<y2+1;_ly++){
      for(int _lx=x1;_lx<x2+1;_lx++){
        color _checkCol=worldMap.get(_lx,_ly);
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
  void calcIncome(){
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
  
  void findChokePoints(){
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