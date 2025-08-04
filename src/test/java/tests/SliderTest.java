package tests;

import cst8218.jeffin.slider.entity.Slider;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SliderTest {

    private Slider slider;

    @Before
    public void setup() {
        slider = new Slider();
        slider.setX(100);
        slider.setY(100);
        slider.setSize(50);
        slider.setMaxTravel(20);
        slider.setCurrentTravel(0);
        slider.setMvtDirection(1);
        slider.setDirChangeCount(0);
    }

    @Test
    public void testTimeStepMovesRight() {
        slider.timeStep();  // Should move +5
        assertEquals(5, slider.getCurrentTravel().intValue());
        assertEquals(1, slider.getMvtDirection().intValue());  // Still moving right
    }

    @Test
    public void testDirectionReversal() {
        slider.setCurrentTravel(20); // Already at edge
        slider.timeStep();  // Should reverse
        assertEquals(-1, slider.getMvtDirection().intValue());
    }

    @Test
    public void testMaxTravelDecayAfterDirChanges() {
        slider.setDirChangeCount(Slider.MAX_DIR_CHANGES);  // Trigger decay
        slider.setCurrentTravel(slider.getMaxTravel());
        int oldMax = slider.getMaxTravel();

        slider.timeStep();  // Should reverse + decay
        assertEquals(oldMax - Slider.DECREASE_RATE, slider.getMaxTravel().intValue());
        assertEquals(0, slider.getDirChangeCount().intValue());
    }

    @Test
    public void testUpdateWithNonNullValues() {
        Slider newSlider = new Slider();
        newSlider.setX(200);
        newSlider.setSize(100);
        newSlider.setMvtDirection(-1);

        slider.updateWithNonNullValues(newSlider);

        assertEquals(200, slider.getX().intValue());
        assertEquals(100, slider.getSize().intValue());
        assertEquals(-1, slider.getMvtDirection().intValue());
    }

    @Test
    public void testTimeStepStopsAtZeroMaxTravel() {
        slider.setMaxTravel(0);
        slider.setCurrentTravel(10);
        slider.timeStep();
        assertEquals(10, slider.getCurrentTravel().intValue());  // Should not move
    }

    
    @Test
public void testSetSizeAffectsMaxTravel() {
    slider.setSize(100);
    slider.setMaxTravel(99); // Travel should be within size
    slider.setCurrentTravel(99);
    slider.setMvtDirection(1); // Moving right at edge
    slider.timeStep();        // Should reverse
    assertEquals(-1, slider.getMvtDirection().intValue());
}


@Test
public void testMoveLeftFromMiddle() {
    slider.setSize(50);
    slider.setMaxTravel(49);
    slider.setCurrentTravel(25);
    slider.setMvtDirection(-1); // Move left
    slider.timeStep();
    assertEquals(20, slider.getCurrentTravel().intValue()); // -5 movement
    assertEquals(-1, slider.getMvtDirection().intValue());
}



}
