
void AI_turn(){
  
  
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

float evalAttack(int _testArea, int type){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( !_area.selectable ) return 0;
  if(type==ACTION_ARMY) {_score=0.9 + random(0.2);}
  if(type==ACTION_FLEET) {_score=0.60 + random(0.6);}
  _score *= (1 + (_area.income*0.09));
  if(_AI.dominance > float(1/playersAlive())){_score *= 1.1;}
  if(_area.fort){_score *= 0.6;}
  if(_area.city){_score *= 1.4;}
  if(_area.mine){_score *= 1.4;}
  if(_area.forest){_score *= 1.5;}
  _score *= 1 + _area.neighborValue()/2.5;
  if(_area.ownedBy==0){
    _score *= 4.8;
  }else{
    _score *= _AI.aiDiplomacy[_area.ownedBy-1];
  }
  if(_area.ownedBy>0){
    Player _owner = getController(_area);
    if(_owner.dominance > 1.0/float(playersAlive())+0.08){_score *= 1+_owner.dominance*2;}
    _score *= 1 + (1 - (_owner.controlFactor()) - _AI.controlFactor()) * 0.9;
    if(campaignMode && hostile){
        if( (gameState==STATE_MAPTEST && _owner.id==1) || _owner.isHuman) {
              _score *= ( 1+( random( min(level,60))/35 ) );
        }
    }
  } 
  _score *= (1-(_area.neighbors.size()*0.05));
  if(_AI.gold<4) _score *= 0.9;
  if(type==ACTION_ARMY) _score *= _AI.aiPreference[ACTION_ARMY];
  if(type==ACTION_FLEET) _score *= _AI.aiPreference[ACTION_FLEET];
  return _score;
}

float evalFort(int _testArea){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( _area.selectable ){_score=0.9 + random(0.18);}
  _score *= (1 + (_area.income*0.3));
  _score *= (_area.mine) ? 1.35 : 1 ;
  //_score *= ((_area.neighbors.size()*0.1));
  _score *= (_area.city) ? 0.05 : 1 ;
  _score *= (0.9 + _area.contested * 0.25);
  if(_AI.dominance > 0.6){_score *= 0.6;}
  if(_AI.dominance < 0.5/playersAlive()){_score *= 1.6;}
  if(_area.uni){_score*=0.5;}
  if(_area.forest){_score *= 1.4;}
  if(_area.mountain){_score *= 1.2;}
  if(isAreaBorder(_area)){
    _score*=4;
  }else{
    _score*=0.5;
  }
  if(_AI.areaControlledCount()==1) _score*=4;
  _score *= _AI.aiPreference[ACTION_FORT];
  return _score;
}
boolean isAreaBorder(Area a){
  for(int aNeighbors : a.neighbors){
    Area n = areas.get(aNeighbors);
    if(n.ownedBy == 0) continue;
    if(n.ownedBy != currentPlayer().id) return true;
  }
  return false;
}


float evalVillage(int _testArea){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( _area.selectable ){_score=1 + random(-0.4,0.3);}
  float _ns = 1;
  for(int i=0;i<_area.neighbors.size();i++){
    Area _a = areas.get(_area.neighbors.get(i));
    if(_a.ownedBy != playerTurn){_ns -= 0.35;}
  }
  _score *= _ns;
  if (_area.fort){_score *= 0.2;}
  _score *= (1.2-(_area.neighbors.size()*0.1));
  if(_area.mine){_score *= 0.4;}
  float _c = _area.contested * -0.2;
  
  if(isAreaBorder(_area)){
    _score*=0.1;
  }else{
    _score*=1.1;
  }
  
  if(_area.invadedBy == _AI.id){_score *= 0.1;}
  _score *= 1 + constrain(_c,-100,0.2);
  if(_AI.dominance > 0.6){_score *= 0.6;}
  if(_area.uni){_score=0;}
  if(_AI.areaControlledCount()==1) _score*=0;
  _score *= _AI.aiPreference[3];
  return _score;
}

float evalUni(int _testArea){
  Area _area = areas.get(_testArea);
  Player _AI = currentPlayer();
  float _score=0;
  if( _area.selectable && countUnis(_AI) <= _AI.maxUni){_score=1 + random(-0.4,0.3);}
  _score *= (1 - _area.contested * 0.25);
  _score*=_AI.uniFondness;
  if (_area.fort){_score *= 0.2;}
  if (_area.city){_score *= 0.2;}
  if(_area.invadedBy == _AI.id){_score *= 0.1;}
  boolean upgradePossible=false;
  if(_AI.dominance < 0.6){_score *= 1.5;}
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

void adjustDiplomacy(int _defender){
  int livingPlayers=playersAlive();
  for(int i = 0; i<players.size();i++){
    Player p = players.get(i);
    if(p.isAlive) livingPlayers++;
  }
  if(livingPlayers==0 || _defender<=0) return;
  float multiplier = 0.5;//(currentPlayer().isHuman & campaignMode) ? 0.65 : 0.5;
  //if(gameState==STATE_MAPTEST && currentPlayer().id==1) multiplier = 0.65;
  float baseVal = currentPlayer().dominance * multiplier;
  float adjustWith=constrain(baseVal,0.001,1);
  
  Player defendingPlayer = players.get(_defender-1);
  defendingPlayer.aiDiplomacy[playerTurn-1] = constrain(defendingPlayer.aiDiplomacy[playerTurn-1] + adjustWith,0.1,4);
  
  for(int i = 0; i<players.size();i++){
    Player p = players.get(i);
    if(p.isAlive){
      p.aiDiplomacy[currentPlayer().id-1] = constrain(p.aiDiplomacy[currentPlayer().id-1] - (adjustWith/livingPlayers*1.05),0.1,4);
    }else{
      p.aiDiplomacy[currentPlayer().id-1] = 0.0001;
    }
  }
}

void doAIUpgrade(Player _AI){
  int actionCount = (campaignMode) ? constrain(level-1,0,4) : 4 ;
  
  while(_AI.hasUpgrade()){
    int _try = int(random(actionCount));
    if(!_AI.actions.get(_try).upgraded){
      _AI.actions.get(_try).upgraded=true;
      _AI.research-=RESEARCH_LIMIT;
    }
  }
}

void doTactics(Player p){
  float prefSum=0;
  for(int i=0;i<p.actions.size();i++){
    prefSum+=p.aiPreference[i];
  }
  boolean noTactic = (prefSum<2.5);
  if(random(1)<0.1 || noTactic){
    p.setAIPreference();
    int tactic=int(random(0,p.actions.size()));
    p.aiPreference[tactic] = random(100000.5,200000);
  }
}

void printDiplomacyValues(Player _AI){
 for(int i=0;i<_AI.aiDiplomacy.length;i++){
   if(_AI.aiDiplomacy[i] > 1.5){
     println("PLR "+_AI.id+" DIPLOMACY VS " + (i+1)+":", _AI.aiDiplomacy[i]);
   }
 }
}

int getBestEval(float[] _evals){
  int _bestEval=0;
  for(int _i=0;_i<_evals.length;_i++){
    if( _evals[_i] > _evals[_bestEval]){
      _bestEval=_i;
    }
  }
  return _bestEval;
}

void tryAction(int _bestEval){
  int _tryAction = int(_bestEval/areas.size());
  int _tryArea = _bestEval % areas.size();
  Area _area = areas.get(_tryArea);

  unselectActions(-1);
  selectableAreas(_tryAction); 
  Action _a= currentPlayer().actions.get(_tryAction);
  _a.selected=true;

  areaClicked(_area.name);
}

boolean stupefyAI(Player _AI){
  if(_AI.id >1 && level < 8 && random(15)>level+9 && iterations > 1) {
    endTurn();
    return true;
  }
  return false;
}