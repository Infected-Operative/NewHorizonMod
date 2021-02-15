package newhorizon;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import newhorizon.content.*;
import newhorizon.effects.EffectTrail;
import newhorizon.func.NHSetting;
import newhorizon.func.SettingDialog;
import newhorizon.func.TableFuncs;

import java.io.IOException;

import static newhorizon.func.TableFuncs.*;


public class NewHorizon extends Mod{
	public static final String NHNAME = "new-horizon-";
	
	private static void links(){
		BaseDialog dialog = new BaseDialog("@links");
		addLink(dialog.cont, Icon.github, "Github", "https://github.com/Yuria-Shikibe/NewHorizonMod.git");
		dialog.cont.button("@back", Icon.left, Styles.cleart, dialog::hide).size(LEN * 3, LEN).padLeft(OFFSET / 2);
		dialog.show();
	}
	
	private static void addLink(Table table, TextureRegionDrawable icon, String buttonName, String link){
		table.button(buttonName, icon, Styles.cleart, () -> {
			BaseDialog dialog = new BaseDialog("@link");
			dialog.addCloseListener();
			dialog.cont.pane(t -> t.add("[gray]" + Core.bundle.get("confirm.link") + ": [accent]" + link + " [gray]?")).fillX().height(LEN / 2f).row();
			dialog.cont.image().fillX().pad(8).height(4f).color(Pal.accent).row();
			dialog.cont.pane(t -> {
				t.button("@back", Icon.left, Styles.cleart, dialog::hide).size(LEN * 3, LEN);
				t.button("@confirm", Icon.link, Styles.cleart, () -> Core.app.openURI(link)).size(LEN * 3, LEN).padLeft(OFFSET / 2);
			}).fillX();
			dialog.show();
		}).size(LEN * 3, LEN).left().row();
	}
	
	private static void logShow(){
		new BaseDialog("@log"){{
			cont.table(Tex.buttonEdge3, table -> {
				table.add("[accent]" + NHSetting.modMeta.version + " [gray]Update Log:").center().row();
				this.addCloseListener();
				table.pane(t -> {
					t.add("@fix").color(Pal.accent).left().row();
					t.image().color(Pal.accent).fillX().height(OFFSET / 4).pad(OFFSET / 3).row();
					t.add(TableFuncs.tabSpace + Core.bundle.get("update.fix")).row();
					
					t.add("@add").color(Pal.accent).padTop(OFFSET * 1.5f).left().row();
					t.image().color(Pal.accent).fillX().height(OFFSET / 4).pad(OFFSET / 3).row();
					t.add(TableFuncs.tabSpace + Core.bundle.get("update.add")).row();
					
					t.add("@remove").color(Pal.accent).padTop(OFFSET * 1.5f).left().row();
					t.image().color(Pal.accent).fillX().height(OFFSET / 4).pad(OFFSET / 3).row();
					t.add(TableFuncs.tabSpace + Core.bundle.get("update.remove")).row();
					
					t.add("@other").color(Pal.accent).padTop(OFFSET * 1.5f).left().row();
					t.image().color(Pal.accent).fillX().height(OFFSET / 4).pad(OFFSET / 3).row();
					t.add(TableFuncs.tabSpace + Core.bundle.get("update.other")).row();
				}).growX().height((Core.graphics.getHeight() - LEN * 2) / (Vars.mobile ? 1.1f : 2.2f));
			}).growX().fillY();
			cont.image().color(Pal.accent).fillX().height(OFFSET / 4).pad(OFFSET / 3).row();
			cont.button("@back", Icon.left, this::hide).fillX().height(LEN).row();
		}}.show();
	}
	
	public static void startLog(){
		BaseDialog dialog = new BaseDialog("Welcome");
		dialog.addCloseListener();
		dialog.cont.pane(table -> {
			table.image(Core.atlas.find(NHNAME + "upgrade")).row();
			table.image().width(LEN * 5).height(OFFSET / 2.5f).pad(OFFSET / 3f).color(Color.white).row();
			table.add("[white]<< Powered by NewHorizonMod >>", Styles.techLabel).row();
			table.image().width(LEN * 5).height(OFFSET / 2.5f).pad(OFFSET / 3f).color(Color.white).row();
			table.add("").row();
		}).width(Core.graphics.getWidth() - LEN).growY().center().row();
		
		dialog.cont.pane(t -> {
			t.add("[gray]You can get back to here by ");
			t.add(NHLoader.content.localizedName).color(Pal.lancerLaser);
			t.add("[gray] in [accent]<View Content>[gray] in the [accent]<ModDialog>[gray].").row();
		}).width(Core.graphics.getWidth() / 2f).fillY().bottom().row();
		
		dialog.cont.table(Tex.clear, table -> {
			table.button("@back", Icon.left, Styles.cleart, () -> {
				dialog.hide();
				NHSetting.settingApply();
			}).size(LEN * 2f, LEN);
			table.button("@links", Icon.link, Styles.cleart, NewHorizon::links).size(LEN * 2f, LEN).padLeft(OFFSET / 2);
			table.button("@settings", Icon.settings, Styles.cleart, () -> new SettingDialog().show()).size(LEN * 2f, LEN).padLeft(OFFSET / 2);
			table.button("@log", Icon.book, Styles.cleart, NewHorizon::logShow).size(LEN * 2f, LEN).padLeft(OFFSET / 2);
		}).fillX().height(LEN + OFFSET);
		dialog.show();
	}
	
    public NewHorizon(){
        Log.info("Loaded NewHorizon Mod constructor.");
        Events.on(ClientLoadEvent.class, e -> Time.runTask(10f, () -> {
        	startLog();
	        tableMain();
        }));
    }
    
    @Override
    public void loadContent(){
	    try{
		    NHSetting.settingFile();
		    NHSetting.initSetting();
		    NHSetting.initSettingList();
	    }catch(IOException e){
		    throw new IllegalArgumentException(e);
	    }
	    Log.info("Loading NewHorizon Mod Objects");
	    NHSounds.load();
		NHLoader loader = new NHLoader();
		loader.load();
	    new NHItems().load();
	    new NHLiquids().load();
	    new NHBullets().load();
		new NHUpgradeDatas().load();
		new NHUnits().load();
		new NHBlocks().load();
		//new NHPlanets().load();
	    new NHTechTree().load();
	    loader.loadLast();
    }
	
	
}
