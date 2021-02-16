
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
  
  void update( int _invItem, boolean canPressAction){
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
    scale(0.5);
   fill(20,20,10);
   //text(cost,(1+x1+pic.width*1.5)*2,(11 + y1)*2);
   //text(cost,(x1+pic.width*1.5)*2,(11 + y1)*2);
   text(cost,(x1+pic.width*1.5)*2,(10 + y1)*2+1);
   fill(230,190,100);
   if(_p.gold<cost){
      fill(150,100,90,220);
    }
   text(cost,(x1+pic.width*1.5)*2,(10 + y1)*2);
   
   scale(2);
  noTint();
  }
}