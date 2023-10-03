package heron.gameboardeditor;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;

class CellUITest {

	@Test
	void testGenerateColors() {
    	int maxLevel = 6;
    	double brightness = .2;
    	double brightnessIncrease = ((double) 1 - brightness) / ((double) maxLevel - (double) 1); //how much each level's color should be increased from the previous
    	for (int i = 0; i < maxLevel - 1; i++) {
        	brightness = brightness + brightnessIncrease;
        }
    	assertEquals(brightness, 1);
    	
    	maxLevel = 5;
    	brightness = .2;
    	brightnessIncrease = ((double) 1 - brightness) / ((double) maxLevel - (double) 1); //how much each level's color should be increased from the previous
    	for (int i = 0; i < maxLevel - 1; i++) {
        	brightness = brightness + brightnessIncrease;
        }
    	assertEquals(brightness, 1);
    	
    	maxLevel = 5;
    	brightness = .3;
    	brightnessIncrease = ((double) 1 - brightness) / ((double) maxLevel - (double) 1); //how much each level's color should be increased from the previous
    	for (int i = 0; i < maxLevel - 1; i++) {
        	brightness = brightness + brightnessIncrease;
        }
    	assertEquals(brightness, 1);
	}
	

}
