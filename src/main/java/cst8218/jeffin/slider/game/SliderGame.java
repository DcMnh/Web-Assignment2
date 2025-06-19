/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SingletonEjbClass.java to edit this template
 */
package cst8218.jeffin.slider.game;

import cst8218.jeffin.slider.ejb.SliderFacade;
import cst8218.jeffin.slider.entity.Slider;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Startup;
import java.util.List;

/**
 *
 * @author leoje
 */
@Startup
@Singleton
@LocalBean
public class SliderGame {
    @EJB
    private SliderFacade sliderFacade;  // Inject SliderFacade to access and modify Slider entities
    private List<Slider> sliders;       // List to hold all sliders

    // Define the game constants
    private static final double CHANGE_RATE = 30.0;  // How many times per second to update

    /**
     * This method starts the game and runs the simulation indefinitely.
     */
    @PostConstruct
    public void go() {
        // Start a new thread for game loop
        new Thread(new Runnable() {
            public void run() {
                // The game runs indefinitely
                while (true) {
                    // Fetch all sliders from the database
                    sliders = sliderFacade.findAll();

                    // Update each slider by calling their timeStep method and save changes
                    for (Slider slider : sliders) {
                        slider.timeStep();
                        sliderFacade.edit(slider);  // Save updated slider to the database
                    }

                    // Sleep for a short period before processing the next frame
                    try {
                        // Wake up roughly CHANGE_RATE times per second
                        Thread.sleep((long) (1.0 / CHANGE_RATE * 1000));
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
