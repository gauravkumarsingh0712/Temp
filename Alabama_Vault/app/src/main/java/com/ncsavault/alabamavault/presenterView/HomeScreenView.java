package com.ncsavault.alabamavault.presenterView;

/**
 * Created by gauravkumar.singh on 5/16/2017.
 */

public interface HomeScreenView {

    /**
     * this function should get called once we want to update the View
     */
    void update();

    /**
     * This function shoult get called once we want to update view then should be invisible.
     * @param visibility
     */
    void onSetProgressBarVisibility(int visibility);
}
