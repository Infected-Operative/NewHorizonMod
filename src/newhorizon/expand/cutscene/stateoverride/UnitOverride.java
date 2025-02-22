package newhorizon.expand.cutscene.stateoverride;

import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import newhorizon.expand.cutscene.actions.CutsceneAI;

public class UnitOverride{
	private static final Seq<Unit> tmpSeq = new Seq<>();
	public static final ObjectMap<Long, Seq<Unit>> flaggedCache = new ObjectMap<>();
	
	//ALL RETURN ARE A TEMPORARY SEQ
	public static ObjectMap<String, Vec2> targets = new ObjectMap<>();
	
	public static Unit findCertain(Seq<Unit> units, UnitType type){
		for(Unit unit : units){
			if(unit.type == type)return unit;
		}
		return null;
	}
	
//	public static PathPoint pathpoints
	
	public static Unit findStrongest(Seq<Unit> units){
		Unit u = units.firstOpt();
		for(Unit unit : units){
			if(u.maxHealth() * u.healthMultiplier < unit.maxHealth() * unit.healthMultiplier){
				u = unit;
			}
		}
		
		return u;
	}
	
	public static Seq<Unit> matched(long flag, boolean isStatic){
		tmpSeq.clear();
		
		if(isStatic && flaggedCache.containsKey(flag)){
			return flaggedCache.get(flag);
		}
		
		for(Unit u : Groups.unit){
			if(Double.doubleToLongBits(u.flag) == flag)tmpSeq.add(u);
		}
		
		if(isStatic){
			if(!flaggedCache.containsKey(flag)){
				return flaggedCache.put(flag, tmpSeq.copy());
			}
		}
		
		return tmpSeq;
	}
	
	public static Seq<Unit> check(Seq<Unit> units){
		return units.filter(Healthc::isValid);
	}
	
	public static void moveParallel(Unit center, Seq<Unit> group, Vec2 target){
		group.remove(center);
		
		
		
		for(Unit u : group){
			Tmp.v1.set(u).sub(center).add(target);
		}
	}
	
	public static void overrideMove(Unit unit, Vec2 target){
		CutsceneAI ai = new CutsceneAI();
		unit.controller(ai);
		ai.commandPosition(target);
	}
	
	public static void endControl(Unit unit){
		unit.controller(unit.type.controller.get(unit));
	}
}
