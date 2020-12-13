package edu.lewisu.cs.tanwe;

import com.badlogic.gdx.graphics.Texture;

import edu.lewisu.cs.cpsc41000.common.PlatformCharacter;

public class PlayerCharacter extends PlatformCharacter {
    public boolean hasKey;
    
    public PlayerCharacter(Texture tex) {
        super(tex,0,0,0,0,0,1,1,false,false,0,0,null,0,700,1000,450,null);
        hasKey = false;
    }

    public PlayerCharacter(Texture tex, int xpos, int ypos, boolean geoCenter) {
        super(tex,xpos,ypos,0,0,0,1,1,false,false,0,0,null,0,700,1000,450,null);
        if (geoCenter) {
            centerOriginGeometrically();
        }
        hasKey = false;
    }

    public boolean gotKey() {
        hasKey = true;
        return hasKey;
    }
}
